import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Expense {
    String description;
    String category;
    double amount;
    LocalDateTime date;

    public Expense(String desc, String cat, double amt, LocalDateTime dt) {
        description = desc;
        category = cat;
        amount = amt;
        date = dt;
    }

    public static Expense create(String desc, String cat, double amt) {
        return new Expense(desc, cat, amt, LocalDateTime.now());
    }

    @Override
    public String toString() {
        return String.format("%-20s %-12s $%-8.2f %s",
            description, category, amount,
            date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    public String toCSV() {
        return String.join(",", description, category, String.valueOf(amount), date.toString());
    }

    public static Expense fromCSV(String line) {
        String[] parts = line.split(",");
        return new Expense(
            parts[0],
            parts[1],
            Double.parseDouble(parts[2]),
            LocalDateTime.parse(parts[3])
        );
    }

    public boolean isInMonthYear(int month, int year) {
        return date.getMonthValue() == month && date.getYear() == year;
    }
}

public class ExpenseTracker {
    private static final String FILE = "expenses.csv";
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Expense> expenses = new ArrayList<>();

    public static void main(String[] args) {
        loadExpenses();
        System.out.println("💸 Welcome to the Java Expense Tracker!");

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. Filter by Category");
            System.out.println("4. View Total Expenses");
            System.out.println("5. Summary by Category");
            System.out.println("6. Filter by Month/Year");
            System.out.println("7. Save & Exit");
            System.out.print("Choose: ");
            switch (scanner.nextLine()) {
                case "1" -> addExpense();
                case "2" -> viewExpenses(expenses);
                case "3" -> filterByCategory();
                case "4" -> showTotal(expenses);
                case "5" -> summaryByCategory();
                case "6" -> filterByDate();
                case "7" -> {
                    saveExpenses();
                    System.out.println("✅ Saved. Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    private static void addExpense() {
        System.out.print("Description: ");
        String desc = scanner.nextLine();
        System.out.print("Category: ");
        String cat = scanner.nextLine();
        System.out.print("Amount: $");
        double amt;
        try {
            amt = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number.");
            return;
        }

        expenses.add(Expense.create(desc, cat, amt));
        System.out.println("✅ Expense recorded.");
    }

    private static void viewExpenses(List<Expense> list) {
        if (list.isEmpty()) {
            System.out.println("📭 No expenses to show.");
            return;
        }
        System.out.printf("\n%-20s %-12s %-10s %s\n", "Description", "Category", "Amount", "Date");
        System.out.println("-".repeat(65));
        for (Expense e : list) System.out.println(e);
    }

    private static void filterByCategory() {
        System.out.print("Enter category to search: ");
        String cat = scanner.nextLine().toLowerCase();
        List<Expense> filtered = expenses.stream()
            .filter(e -> e.category.toLowerCase().equals(cat))
            .toList();

        viewExpenses(filtered);
        showTotal(filtered);
    }

    private static void filterByDate() {
        try {
            System.out.print("Enter month (1–12): ");
            int month = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter year (e.g. 2025): ");
            int year = Integer.parseInt(scanner.nextLine());

            List<Expense> filtered = expenses.stream()
                .filter(e -> e.isInMonthYear(month, year))
                .toList();

            viewExpenses(filtered);
            showTotal(filtered);
        } catch (Exception e) {
            System.out.println("❌ Invalid date input.");
        }
    }

    private static void showTotal(List<Expense> list) {
        double total = list.stream().mapToDouble(e -> e.amount).sum();
        System.out.printf("💵 Total: $%.2f\n", total);
    }

    private static void summaryByCategory() {
        Map<String, Double> map = new HashMap<>();
        for (Expense e : expenses) {
            map.put(e.category, map.getOrDefault(e.category, 0.0) + e.amount);
        }

        System.out.println("\n📊 Category Summary:");
        map.forEach((cat, amt) ->
            System.out.printf("• %-12s : $%.2f\n", cat, amt));
    }

    private static void saveExpenses() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE))) {
            for (Expense e : expenses) out.println(e.toCSV());
        } catch (IOException e) {
            System.out.println("❌ Could not save.");
        }
    }

    private static void loadExpenses() {
        try (BufferedReader in = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = in.readLine()) != null) {
                expenses.add(Expense.fromCSV(line));
            }
        } catch (IOException e) {
            System.out.println("File might not exist");
        }
    }
}
