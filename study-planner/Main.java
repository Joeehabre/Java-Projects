import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    private static final String DATA_FILE = "tasks.csv";

    public static void main(String[] args) {
        Planner planner = new Planner();
        Storage.load(DATA_FILE, planner);

        Scanner sc = new Scanner(System.in);
        while (true) {
            UI.clear();
            UI.title("Study Planner");
            System.out.println("""
                1) Add task
                2) List tasks
                3) Edit task
                4) Mark DOING / DONE
                5) Delete task
                6) Stats
                7) Import / Export
                8) Help
                9) Save & Exit
                """);
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> addTask(sc, planner);
                case "2" -> listMenu(sc, planner);
                case "3" -> editMenu(sc, planner);
                case "4" -> statusMenu(sc, planner);
                case "5" -> deleteMenu(sc, planner);
                case "6" -> { UI.clear(); UI.title("Stats"); UI.printStats(planner); UI.pause(sc); }
                case "7" -> importExportMenu(sc, planner);
                case "8" -> { UI.clear(); UI.help(); UI.pause(sc); }
                case "9" -> {
                    Storage.save(DATA_FILE, planner);
                    System.out.println("\nSaved to " + DATA_FILE + ". Bye!");
                    return;
                }
                default -> { System.out.println("Invalid choice."); UI.pause(sc); }
            }
        }
    }

    // ---------- Menus ----------
    private static void addTask(Scanner sc, Planner planner) {
        UI.clear(); UI.title("Add Task");
        System.out.print("Title: ");
        String title = sc.nextLine().trim();
        LocalDate due = askDate(sc, "Due date (YYYY-MM-DD or blank): ");
        Priority pr = askPriority(sc, "Priority [LOW/MEDIUM/HIGH] (default MEDIUM): ");
        Set<String> tags = askTags(sc);

        Task t = planner.add(title, due, pr, tags);
        Storage.save("tasks.csv", planner); // autosave
        System.out.println("\nAdded: " + t.pretty());
        UI.pause(sc);
    }

    private static void listMenu(Scanner sc, Planner planner) {
        UI.clear(); UI.title("List Tasks (filters optional)");
        Status st = askStatus(sc, "Filter by Status [TODO/DOING/DONE or blank]: ", true);
        Priority pr = askPriority(sc, "Filter by Priority [LOW/MEDIUM/HIGH or blank]: ", true);
        System.out.print("Filter by Tag (single tag or blank): ");
        String tag = sc.nextLine().trim();
        LocalDate dueBefore = askDate(sc, "Due-before (YYYY-MM-DD or blank): ");

        System.out.print("Sort by [due|priority|status|id] (default: due): ");
        String sort = sc.nextLine().trim().toLowerCase();
        if (!List.of("due","priority","status","id").contains(sort)) sort = "due";

        List<Task> filtered = planner.filtered(st, pr, tag.isEmpty()?null:tag, dueBefore, sort);
        UI.clear(); UI.title("Tasks");
        UI.printTasks(filtered);
        UI.pause(sc);
    }

    private static void editMenu(Scanner sc, Planner planner) {
        UI.clear(); UI.title("Edit Task");
        int id = askInt(sc, "Task ID: ");
        Task t = planner.find(id);
        if (t == null) { System.out.println("Not found."); UI.pause(sc); return; }

        System.out.println("Editing: " + t.pretty());
        System.out.print("New title (blank to keep): ");
        String title = sc.nextLine().trim();
        LocalDate due = askDate(sc, "New due date (YYYY-MM-DD or blank to clear): ", true);
        Priority pr = askPriority(sc, "New priority [LOW/MEDIUM/HIGH or blank]: ", true);
        System.out.print("Tags (comma-separated; blank to keep; '-' to clear): ");
        String tagsLine = sc.nextLine().trim();

        if (!title.isEmpty()) t.setTitle(title);
        if (due != null || " ".equals(" ")) {
            t.setDueDate(due);
        }
        if (pr != null) t.setPriority(pr);
        if (!tagsLine.isEmpty()) {
            if (tagsLine.equals("-")) t.setTags(new LinkedHashSet<>());
            else t.setTags(TagUtil.parseTags(tagsLine));
        }

        Storage.save(DATA_FILE, planner);
        System.out.println("\nUpdated: " + t.pretty());
        UI.pause(sc);
    }

    private static void statusMenu(Scanner sc, Planner planner) {
        UI.clear(); UI.title("Change Status");
        int id = askInt(sc, "Task ID: ");
        Task t = planner.find(id);
        if (t == null) { System.out.println("Not found."); UI.pause(sc); return; }

        System.out.print("Set status [TODO/DOING/DONE]: ");
        String s = sc.nextLine().trim().toUpperCase();
        Status st = switch (s) {
            case "DOING" -> Status.DOING;
            case "DONE" -> Status.DONE;
            default -> Status.TODO;
        };
        t.setStatus(st);
        Storage.save(DATA_FILE, planner);
        System.out.println("Updated: " + t.pretty());
        UI.pause(sc);
    }

    private static void deleteMenu(Scanner sc, Planner planner) {
        UI.clear(); UI.title("Delete Task");
        int id = askInt(sc, "Task ID: ");
        Task t = planner.find(id);
        if (t == null) { System.out.println("Not found."); UI.pause(sc); return; }
        System.out.println("About to delete: " + t.pretty());
        System.out.print("Confirm (y/N): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            planner.delete(id);
            Storage.save(DATA_FILE, planner);
            System.out.println("Deleted.");
        } else System.out.println("Canceled.");
        UI.pause(sc);
    }

    private static void importExportMenu(Scanner sc, Planner planner) {
        UI.clear(); UI.title("Import / Export");
        System.out.println("""
            1) Export to CSV (tasks_export.csv)
            2) Import from CSV (tasks_import.csv)
            3) Back
            """);
        System.out.print("Choose: ");
        String c = sc.nextLine().trim();
        switch (c) {
            case "1" -> { Storage.exportCsv("tasks_export.csv", planner); System.out.println("Exported to tasks_export.csv"); }
            case "2" -> { Storage.importCsv("tasks_import.csv", planner); Storage.save(DATA_FILE, planner); System.out.println("Imported from tasks_import.csv"); }
            default -> { /* back */ }
        }
        UI.pause(sc);
    }

    // ---------- Helpers ----------
    private static int askInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (Exception e) { System.out.println("Please enter a number."); }
        }
    }

    private static LocalDate askDate(Scanner sc, String prompt) {
        return askDate(sc, prompt, false);
    }

    private static LocalDate askDate(Scanner sc, String prompt, boolean allowBlank) {
        System.out.print(prompt);
        String s = sc.nextLine().trim();
        if (s.isEmpty() && allowBlank) return null; // clear
        if (s.isEmpty()) return null;
        try { return LocalDate.parse(s); }
        catch (DateTimeParseException e) { System.out.println("Invalid date. Skipping."); return null; }
    }

    private static Priority askPriority(Scanner sc, String prompt) {
        return askPriority(sc, prompt, false);
    }

    private static Priority askPriority(Scanner sc, String prompt, boolean allowBlank) {
        System.out.print(prompt);
        String p = sc.nextLine().trim().toUpperCase();
        if (p.isEmpty() && allowBlank) return null;
        return switch (p) {
            case "LOW" -> Priority.LOW;
            case "HIGH" -> Priority.HIGH;
            default -> Priority.MEDIUM;
        };
    }

    private static Status askStatus(Scanner sc, String prompt, boolean allowBlank) {
        System.out.print(prompt);
        String s = sc.nextLine().trim().toUpperCase();
        if (s.isEmpty() && allowBlank) return null;
        return switch (s) {
            case "DOING" -> Status.DOING;
            case "DONE" -> Status.DONE;
            default -> Status.TODO;
        };
    }

    private static Set<String> askTags(Scanner sc) {
        System.out.print("Tags (comma-separated, optional): ");
        String line = sc.nextLine().trim();
        return TagUtil.parseTags(line);
    }
}
