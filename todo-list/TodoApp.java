import java.io.*;
import java.util.*;

public class TodoApp {
    private static final String FILE_NAME = "tasks.txt";
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<String[]> tasks = new ArrayList<>(); // [0]=done flag, [1]=title

    public static void main(String[] args) {
        loadTasks();
        System.out.println("Welcome to the Java To-Do List");

        while (true) {
            showTasks();
            System.out.println("\nOptions:");
            System.out.println("1. Add Task");
            System.out.println("2. Remove Task");
            System.out.println("3. Toggle Done / Undone");
            System.out.println("4. Edit Task");
            System.out.println("5. Clear Completed");
            System.out.println("6. Save & Exit");

            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> addTask();
                case "2" -> removeTask();
                case "3" -> toggleTask();
                case "4" -> editTask();
                case "5" -> clearCompleted();
                case "6" -> {
                    saveTasks();
                    System.out.println("Saved. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("[x] ") || line.startsWith("[ ] ")) {
                    tasks.add(new String[]{line.startsWith("[x]") ? "x" : " ", line.substring(4)});
                } else {
                    tasks.add(new String[]{" ", line});
                }
            }
        } catch (IOException e) {
            // first run — no file yet
        }
    }

    private static void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String[] task : tasks) {
                writer.write("[" + task[0] + "] " + task[1]);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks.");
        }
    }

    private static void showTasks() {
        System.out.println("\nYour Tasks:");
        if (tasks.isEmpty()) {
            System.out.println("  (no tasks yet)");
        } else {
            long done = tasks.stream().filter(t -> t[0].equals("x")).count();
            for (int i = 0; i < tasks.size(); i++) {
                String[] task = tasks.get(i);
                String check = task[0].equals("x") ? "[x]" : "[ ]";
                System.out.println("  " + (i + 1) + ". " + check + " " + task[1]);
            }
            System.out.println("  --- " + done + "/" + tasks.size() + " done ---");
        }
    }

    private static void addTask() {
        System.out.print("Enter a new task: ");
        String title = scanner.nextLine().trim();
        if (!title.isEmpty()) {
            tasks.add(new String[]{" ", title});
            saveTasks();
            System.out.println("Task added.");
        } else {
            System.out.println("Task cannot be empty.");
        }
    }

    private static void removeTask() {
        if (tasks.isEmpty()) { System.out.println("No tasks to remove."); return; }
        System.out.print("Enter task number to remove: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index >= 0 && index < tasks.size()) {
                System.out.println("Removed: " + tasks.get(index)[1]);
                tasks.remove(index);
                saveTasks();
            } else {
                System.out.println("Invalid task number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void toggleTask() {
        if (tasks.isEmpty()) { System.out.println("No tasks to toggle."); return; }
        System.out.print("Enter task number to toggle: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index >= 0 && index < tasks.size()) {
                String[] task = tasks.get(index);
                task[0] = task[0].equals("x") ? " " : "x";
                String state = task[0].equals("x") ? "done" : "undone";
                System.out.println("Marked as " + state + ": " + task[1]);
                saveTasks();
            } else {
                System.out.println("Invalid task number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void editTask() {
        if (tasks.isEmpty()) { System.out.println("No tasks to edit."); return; }
        System.out.print("Enter task number to edit: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index >= 0 && index < tasks.size()) {
                System.out.println("Current: " + tasks.get(index)[1]);
                System.out.print("New title: ");
                String newTitle = scanner.nextLine().trim();
                if (!newTitle.isEmpty()) {
                    tasks.get(index)[1] = newTitle;
                    saveTasks();
                    System.out.println("Updated.");
                } else {
                    System.out.println("Title cannot be empty. No changes made.");
                }
            } else {
                System.out.println("Invalid task number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void clearCompleted() {
        long before = tasks.size();
        tasks.removeIf(t -> t[0].equals("x"));
        long removed = before - tasks.size();
        if (removed == 0) System.out.println("No completed tasks to clear.");
        else {
            saveTasks();
            System.out.println("Cleared " + removed + " completed task(s).");
        }
    }
}
