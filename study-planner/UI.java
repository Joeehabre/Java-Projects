import java.time.LocalDate;
import java.util.List;

public class UI {
    private static final String RESET = "\u001B[0m";
    private static final String DIM = "\u001B[2m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String MAGENTA = "\u001B[35m";

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
            case LOW -> GREEN + "[LOW]" + RESET;
            default -> YELLOW + "[MED]" + RESET;
        };
    }

    public static String colorStatus(Status s) {
        return switch (s) {
            case DONE -> GREEN + "(DONE)" + RESET;
            case DOING -> MAGENTA + "(DOING)" + RESET;
            default -> DIM + "(TODO)" + RESET;
        };
    }

    public static void printTasks(List<Task> tasks) {
        if (tasks.isEmpty()) { System.out.println("(no tasks yet)"); return; }
        System.out.printf("%-4s %-7s %-8s %-12s %-10s %s%n", "ID", "PRIOR", "STATUS", "DUE", "TAGS", "TITLE");
        System.out.println(DIM + "---------------------------------------------------------------------" + RESET);
        LocalDate now = LocalDate.now();
        for (Task t : tasks) {
            String due = t.getDueDate()==null ? "-" : t.getDueDate().toString();
            String dueDisp = due;
            if (t.getDueDate()!=null && t.getStatus()!=Status.DONE && t.getDueDate().isBefore(now)) {
                dueDisp = RED + due + RESET; // overdue highlight
            }
            String tags = t.getTags().isEmpty()? "-" : String.join("|", t.getTags());
            System.out.printf("%-4d %-7s %-8s %-12s %-10s %s%n",
                    t.getId(),
                    strip(colorPriority(t.getPriority())),
                    strip(colorStatus(t.getStatus())),
                    dueDisp,
                    tags,
                    t.getTitle());
        }
    }

    private static String strip(String s) { return s.replaceAll("\u001B\\[[;\\d]*m", ""); }

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
            • Import expects tasks_import.csv (same columns as tasks.csv)
            """);
    }

    public static void pause(java.util.Scanner sc) {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }
}
