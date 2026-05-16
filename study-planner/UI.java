import java.time.LocalDate;
import java.util.List;

public class UI {
    private static final String RESET   = "[0m";
    private static final String DIM     = "[2m";
    private static final String RED     = "[31m";
    private static final String YELLOW  = "[33m";
    private static final String GREEN   = "[32m";
    private static final String CYAN    = "[36m";
    private static final String MAGENTA = "[35m";

    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void title(String t) {
        System.out.println(CYAN + "==== " + t + " ====" + RESET + "\n");
    }

    public static String colorPriority(Priority p) {
        return switch (p) {
            case HIGH -> RED + "[HIGH]" + RESET;
            case LOW  -> GREEN + "[LOW]" + RESET;
            default   -> YELLOW + "[MED]" + RESET;
        };
    }

    public static String colorStatus(Status s) {
        return switch (s) {
            case DONE  -> GREEN + "(DONE)" + RESET;
            case DOING -> MAGENTA + "(DOING)" + RESET;
            default    -> DIM + "(TODO)" + RESET;
        };
    }

    public static void printTasks(List<Task> tasks) {
        if (tasks.isEmpty()) { System.out.println("(no tasks yet)"); return; }
        System.out.printf("%-4s %-7s %-8s %-12s %-10s %s%n", "ID", "PRIOR", "STATUS", "DUE", "TAGS", "TITLE");
        System.out.println(DIM + "---------------------------------------------------------------------" + RESET);
        LocalDate now = LocalDate.now();
        for (Task t : tasks) {
            String rawDue = t.getDueDate() == null ? "-" : t.getDueDate().toString();
            boolean overdue = t.getDueDate() != null && t.getStatus() != Status.DONE && t.getDueDate().isBefore(now);
            // Pre-pad to fixed width BEFORE applying color so ANSI codes don't break column alignment
            String duePadded = String.format("%-12s", rawDue);
            String dueDisp = overdue ? RED + duePadded + RESET : duePadded;

            String tags = t.getTags().isEmpty() ? "-" : String.join("|", t.getTags());
            System.out.printf("%-4d %s %s %s %-10s %s%n",
                    t.getId(),
                    colorPad(colorPriority(t.getPriority()), 7),
                    colorPad(colorStatus(t.getStatus()), 8),
                    dueDisp,
                    tags,
                    t.getTitle());
        }
    }

    // Pad a colored string to a minimum visible width (ANSI escape codes are invisible).
    private static String colorPad(String colored, int width) {
        int visible = strip(colored).length();
        return colored + " ".repeat(Math.max(0, width - visible));
    }

    private static String strip(String s) {
        return s.replaceAll("\\[[;\\d]*m", "");
    }

    public static void printStats(Planner planner) {
        System.out.printf("Total: %d  | TODO: %d  DOING: %d  DONE: %d  | Overdue: %d%n",
                planner.allSorted().size(),
                planner.count(Status.TODO),
                planner.count(Status.DOING),
                planner.count(Status.DONE),
                planner.overdue());
        System.out.printf("Priority counts  LOW:%d  MED:%d  HIGH:%d%n",
                planner.count(Priority.LOW),
                planner.count(Priority.MEDIUM),
                planner.count(Priority.HIGH));
    }

    public static void help() {
        System.out.println("""
            Tips:
            • Add tag lists like: algorithms,math
            • Filters accept blank to skip
            • Sort options: due | priority | status | id
            • Edit: blank title keeps existing; '-' clears due date
            • Import expects tasks_import.csv (same columns as tasks.csv)
            """);
    }

    public static void pause(java.util.Scanner sc) {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }
}
