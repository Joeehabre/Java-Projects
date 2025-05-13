import java.io.*;
import java.util.*;

public class TodoApp {
    private static final String FILE_NAME = "tasks.txt";
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<String> tasks = new ArrayList<>();

    public static void main(String[] args) {
        loadTasks();
        System.out.println("✅ Welcome to the Java To-Do List");

        while (true) {
            showTasks();
            System.out.println("\nOptions:");
            System.out.println("1. Add Task");
            System.out.println("2. Remove Task");
            System.out.println("3. Save & Exit");

            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> addTask();
                case "2" -> removeTask();
                case "3" -> {
                    saveTasks();
                    System.out.println("👋 Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private static void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("File not found.");
        }
    }

    private static void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String task : tasks) {
                writer.write(task);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Error saving tasks.");
        }
    }

    private static void showTasks() {
        System.out.println("\n📝 Your Tasks:");
        if (tasks.isEmpty()) {
            System.out.println(" (No tasks yet)");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + tasks.get(i));
            }
        }
    }

    private static void addTask() {
        System.out.print("Enter a new task: ");
        String task = scanner.nextLine().trim();
        if (!task.isEmpty()) {
            tasks.add(task);
            System.out.println("✅ Task added.");
        } else {
            System.out.println("❌ Task cannot be empty.");
        }
    }

    private static void removeTask() {
        System.out.print("Enter task number to remove: ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index >= 0 && index < tasks.size()) {
                tasks.remove(index);
                System.out.println("✅ Task removed.");
            } else {
                System.out.println("❌ Invalid task number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
    }
}
