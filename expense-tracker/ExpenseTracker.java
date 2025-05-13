import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Expense {
    String description;
    String category;
    double amount;
    LocalDateTime date;

    public Expense(String desc, String cat, double amt) {
        description = desc;
        category = cat;
        amount = amt;
        date = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("%-20s %-10s $%-8.2f %s",
                description, category, amount, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    public String toCSV() {
        return String.join(",", description, category, String.valueOf(amount), date.toString());
    }

    public static Expense fromCSV(String line) {
        String[] parts = line.split(",");
        Expense e = new Expense(parts[0], parts[1], Double.parseDouble(parts[2]));
        e.date = LocalDateTime.parse(parts[3]);
        return e;
    }
}

public class ExpenseTracker {
    static final String FILE_NAME = "expenses.csv";
    static final Scanner scanner = new Scanner(System.in);
    static final List<Expense> expenses = new ArrayList<>();

    public static void main(String[] args) {
        loadExpenses();
        while (true) {
            System.out.println("\n💰 Expense Tracker Menu:");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. Filter by Category");
            System.out.println("4. Show Total Spent");
            System.out.println("5. Save & Exit");
            System.out.print("Choose: ");
            switch (scanner.nextLine()) {
                case "1" -> addExpense();
                case "2" -> viewExpenses();
                case "3" -> filterByCategory();
                case "4" -> showTotal();
                case "5" -> {
                    saveExpenses();
                    System.out.println("✅ Data saved. Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid option.");
            }
        }
    }

    static void addExpense() {
        System.out.print("Description: ");
        String desc = scanner.nextLine();
        System.out.print("Category (e.g. Food, Transport): ");
        String cat = scanner.nextLine();
        System.out.print("Amount: $");
        double amt;
        try {
            amt = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount.");
            return;
        }
        expenses.add(new Expense(desc, cat, amt));
        System.out.println("✅ Expense added.");
    }

    static void viewExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("📭 No expenses recorded.");
            return;
        }
        System.out.printf("\n%-20s %-10s %-8s %-20s\n", "Description", "Category", "Amount", "Date");
        System.out.println("-".repeat(65));
        for (Expense e : expenses) System.out.println(e);
    }

    static void filterByCategory() {
        System.out.print("Enter category to filter: ");
        String cat = scanner.nextLine();
        boolean found = false;
        for (Expense e : expenses) {
            if (e.category.equalsIgnoreCase(cat)) {
                if (!found) {
                    System.out.printf("\n%-20s %-10s %-8s %-20s\n", "Description", "Category", "Amount", "Date");
                    System.out.println("-".repeat(65));
                    found = true;
                }
                System.out.println(e);
            }
        }
        if (!found) System.out.println("📭 No expenses found in this category.");
    }

    static void showTotal() {
        double total = expenses.stream().mapToDouble(e -> e.amount).sum();
        System.out.printf("💵 Total Spent: $%.2f\n", total);
    }

    static void saveExpenses() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Expense e : expenses) {
                out.println(e.toCSV());
            }
        } catch (IOException e) {
            System.out.println("❌ Could not save expenses.");
        }
    }

    static void loadExpenses() {
        try (BufferedReader in = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = in.readLine()) != null) {
                expenses.add(Expense.fromCSV(line));
            }
        } catch (IOException e) {
            // File may not exist — that’s fine
        }
    }
}
