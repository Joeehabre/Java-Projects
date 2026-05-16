import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Expense {
    private final String description;
    private final String category;
    private final double amount;
    private final LocalDateTime date;

    public Expense(String description, String category, double amount, LocalDateTime date) {
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public static Expense create(String desc, String cat, double amt) {
        return new Expense(desc, cat, amt, LocalDateTime.now());
    }

    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }

    public boolean isInMonthYear(int month, int year) {
        return date.getMonthValue() == month && date.getYear() == year;
    }

    @Override
    public String toString() {
        return String.format("%-22s %-14s $%-9.2f %s",
            description, category, amount,
            date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    public String toCSV() {
        return csvEscape(description) + "," + csvEscape(category) + ","
            + amount + "," + date.toString();
    }

    public static Expense fromCSV(String line) {
        String[] parts = csvSplit(line, 4);
        return new Expense(
            csvUnescape(parts[0]),
            csvUnescape(parts[1]),
            Double.parseDouble(parts[2]),
            LocalDateTime.parse(parts[3])
        );
    }

    private static String csvEscape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n"))
            return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }

    private static String csvUnescape(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\""))
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        return s;
    }

    private static String[] csvSplit(String line, int expected) {
        String[] out = new String[expected];
        int idx = 0;
        boolean inQ = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < line.length() && idx < expected - 1; i++) {
            char c = line.charAt(i);
            if (c == '"') { inQ = !inQ; }
            else if (c == ',' && !inQ) { out[idx++] = cur.toString(); cur.setLength(0); }
            else cur.append(c);
        }
        out[idx] = cur.toString();
        return out;
    }
}

public class ExpenseTracker {
    private static final String FILE = "expenses.csv";
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Expense> expenses = new ArrayList<>();

    public static void main(String[] args) {
        loadExpenses();
        System.out.println("Java Expense Tracker");

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. Filter by Category");
            System.out.println("4. Filter by Month/Year");
            System.out.println("5. Summary by Category");
            System.out.println("6. Total Expenses");
            System.out.println("7. Delete Expense");
            System.out.println("8. Save & Exit");
            System.out.print("Choose: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> addExpense();
                case "2" -> viewExpenses(expenses);
                case "3" -> filterByCategory();
                case "4" -> filterByDate();
                case "5" -> summaryByCategory();
                case "6" -> showTotal(expenses);
                case "7" -> deleteExpense();
                case "8" -> { saveExpenses(); System.out.println("Saved. Goodbye!"); return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addExpense() {
        System.out.print("Description: ");
        String desc = scanner.nextLine().trim();
        if (desc.isEmpty()) { System.out.println("Description cannot be empty."); return; }

        System.out.print("Category: ");
        String cat = scanner.nextLine().trim();
        if (cat.isEmpty()) { System.out.println("Category cannot be empty."); return; }

        System.out.print("Amount: $");
        double amt;
        try {
            amt = Double.parseDouble(scanner.nextLine().trim());
            if (amt <= 0) { System.out.println("Amount must be positive."); return; }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }

        expenses.add(Expense.create(desc, cat, amt));
        saveExpenses();
        System.out.println("Expense recorded.");
    }

    private static void viewExpenses(List<Expense> list) {
        if (list.isEmpty()) { System.out.println("No expenses to show."); return; }
        System.out.printf("%n%-4s %-22s %-14s %-11s %s%n", "#", "Description", "Category", "Amount", "Date");
        System.out.println("-".repeat(72));
        for (int i = 0; i < list.size(); i++)
            System.out.printf("%-4d %s%n", i + 1, list.get(i));
    }

    private static void filterByCategory() {
        System.out.print("Category keyword: ");
        String keyword = scanner.nextLine().trim().toLowerCase();
        List<Expense> filtered = expenses.stream()
            .filter(e -> e.getCategory().toLowerCase().contains(keyword))
            .toList();
        viewExpenses(filtered);
        if (!filtered.isEmpty()) showTotal(filtered);
    }

    private static void filterByDate() {
        try {
            System.out.print("Month (1-12): ");
            int month = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Year (e.g. 2025): ");
            int year = Integer.parseInt(scanner.nextLine().trim());
            List<Expense> filtered = expenses.stream()
                .filter(e -> e.isInMonthYear(month, year))
                .toList();
            viewExpenses(filtered);
            if (!filtered.isEmpty()) showTotal(filtered);
        } catch (NumberFormatException e) {
            System.out.println("Invalid date input.");
        }
    }

    private static void showTotal(List<Expense> list) {
        double total = list.stream().mapToDouble(Expense::getAmount).sum();
        System.out.printf("Total: $%.2f%n", total);
    }

    private static void summaryByCategory() {
        if (expenses.isEmpty()) { System.out.println("No expenses recorded."); return; }
        Map<String, Double> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Expense e : expenses)
            map.merge(e.getCategory(), e.getAmount(), Double::sum);

        System.out.println("\nCategory Summary:");
        System.out.println("-".repeat(30));
        double grand = 0;
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            System.out.printf("  %-16s $%.2f%n", entry.getKey(), entry.getValue());
            grand += entry.getValue();
        }
        System.out.println("-".repeat(30));
        System.out.printf("  %-16s $%.2f%n", "TOTAL", grand);
    }

    private static void deleteExpense() {
        if (expenses.isEmpty()) { System.out.println("No expenses to delete."); return; }
        viewExpenses(expenses);
        System.out.print("Enter expense number to delete (0 to cancel): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx == -1) { System.out.println("Cancelled."); return; }
            if (idx >= 0 && idx < expenses.size()) {
                Expense removed = expenses.remove(idx);
                saveExpenses();
                System.out.println("Deleted: " + removed.getDescription() + " ($" + String.format("%.2f", removed.getAmount()) + ")");
            } else {
                System.out.println("Invalid number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void saveExpenses() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE))) {
            for (Expense e : expenses) out.println(e.toCSV());
        } catch (IOException e) {
            System.out.println("Could not save expenses.");
        }
    }

    private static void loadExpenses() {
        try (BufferedReader in = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try { expenses.add(Expense.fromCSV(line)); }
                    catch (Exception e) { System.out.println("Skipping malformed line: " + line); }
                }
            }
        } catch (IOException ignored) {
            // first run
        }
    }
}
