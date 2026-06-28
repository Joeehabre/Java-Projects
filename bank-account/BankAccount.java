import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Account {
    private static int counter = 1000;
    private final int id;
    private final String name;
    private double balance;
    private final List<String> transactions = new ArrayList<>();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Account(String name, double initialDeposit) {
        this.id = ++counter;
        this.name = name;
        this.balance = initialDeposit;
        log("Account opened with initial deposit of $" + f(initialDeposit));
    }

    Account(int id, String name, double balance, List<String> txHistory) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        if (txHistory != null) transactions.addAll(txHistory);
        if (counter < id) counter = id;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
    public List<String> getTransactions() { return Collections.unmodifiableList(transactions); }

    public boolean deposit(double amount) {
        if (amount <= 0) { System.out.println("  Amount must be positive."); return false; }
        balance += amount;
        log("Deposit    +" + f(amount) + "   balance: " + f(balance));
        System.out.printf("  Deposited $%.2f. New balance: $%.2f%n", amount, balance);
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) { System.out.println("  Amount must be positive."); return false; }
        if (amount > balance) { System.out.printf("  Insufficient balance (available: $%.2f).%n", balance); return false; }
        balance -= amount;
        log("Withdrawal -" + f(amount) + "   balance: " + f(balance));
        System.out.printf("  Withdrew $%.2f. New balance: $%.2f%n", amount, balance);
        return true;
    }

    public void receiveTransfer(double amount, String fromName) {
        balance += amount;
        log("Transfer from " + fromName + " +" + f(amount) + "   balance: " + f(balance));
    }

    public void sendTransfer(double amount, String toName) {
        balance -= amount;
        log("Transfer to " + toName + "   -" + f(amount) + "   balance: " + f(balance));
    }

    public void showBalance() {
        System.out.printf("  Balance: $%.2f%n", balance);
    }

    public void showHistory() {
        if (transactions.isEmpty()) { System.out.println("  (no transactions)"); return; }
        System.out.println("  --- Transaction History ---");
        for (String tx : transactions) System.out.println("  " + tx);
    }

    private void log(String msg) {
        transactions.add(LocalDateTime.now().format(FMT) + "  " + msg);
    }

    private static String f(double n) { return String.format("%.2f", n); }
}

public class BankAccount {
    private static final String FILE = "accounts.dat";
    private static final Map<Integer, Account> accounts = new LinkedHashMap<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        load();
        while (true) {
            System.out.println("\nSimpleBank");
            System.out.println("1. Create Account");
            System.out.println("2. Access Account");
            System.out.println("3. List Accounts");
            System.out.println("4. Exit");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> createAccount();
                case "2" -> accessAccount();
                case "3" -> listAccounts();
                case "4" -> { save(); System.out.println("Goodbye!"); return; }
                default -> System.out.println("  Invalid choice.");
            }
        }
    }

    private static void createAccount() {
        System.out.print("Your name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) { System.out.println("  Name cannot be empty."); return; }

        double deposit = promptAmount("Initial deposit: $");
        if (deposit < 0) return;

        Account account = new Account(name, deposit);
        accounts.put(account.getId(), account);
        save();
        System.out.println("  Account created. Your ID: " + account.getId());
    }

    private static void accessAccount() {
        System.out.print("Account ID: ");
        int id;
        try { id = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("  Invalid ID."); return; }

        Account account = accounts.get(id);
        if (account == null) { System.out.println("  Account not found."); return; }

        while (true) {
            System.out.printf("%n  [%s | ID: %d]%n", account.getName(), account.getId());
            account.showBalance();
            System.out.println("  1. Deposit");
            System.out.println("  2. Withdraw");
            System.out.println("  3. Transfer to Another Account");
            System.out.println("  4. Transaction History");
            System.out.println("  5. Back");
            System.out.print("  Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> { double a = promptAmount("  Amount: $"); if (a >= 0) { account.deposit(a); save(); } }
                case "2" -> { double a = promptAmount("  Amount: $"); if (a >= 0) { account.withdraw(a); save(); } }
                case "3" -> transfer(account);
                case "4" -> account.showHistory();
                case "5" -> { return; }
                default -> System.out.println("  Invalid choice.");
            }
        }
    }

    private static void transfer(Account from) {
        System.out.print("  Destination account ID: ");
        int toId;
        try { toId = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("  Invalid ID."); return; }

        Account to = accounts.get(toId);
        if (to == null) { System.out.println("  Destination account not found."); return; }
        if (to.getId() == from.getId()) { System.out.println("  Cannot transfer to the same account."); return; }

        double amount = promptAmount("  Amount: $");
        if (amount < 0) return;
        if (amount > from.getBalance()) { System.out.printf("  Insufficient balance (available: $%.2f).%n", from.getBalance()); return; }

        from.sendTransfer(amount, to.getName());
        to.receiveTransfer(amount, from.getName());
        save();
        System.out.printf("  Transferred $%.2f to %s (ID: %d).%n", amount, to.getName(), to.getId());
    }

    private static void listAccounts() {
        if (accounts.isEmpty()) { System.out.println("  No accounts yet."); return; }
        System.out.printf("%n  %-6s %-20s %s%n", "ID", "Name", "Balance");
        System.out.println("  " + "-".repeat(36));
        for (Account a : accounts.values())
            System.out.printf("  %-6d %-20s $%.2f%n", a.getId(), a.getName(), a.getBalance());
    }

    // ---- Persistence ----

    private static void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Account a : accounts.values()) {
                pw.println("ACCOUNT," + a.getId() + "," + a.getName() + "," + a.getBalance());
                for (String tx : a.getTransactions()) pw.println("TX," + tx);
                pw.println("END");
            }
        } catch (IOException e) {
            System.out.println("  Warning: could not save accounts.");
        }
    }

    private static void load() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            Account current = null;
            List<String> txBuf = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (line.startsWith("ACCOUNT,")) {
                    String[] p = line.split(",", 4);
                    current = null;
                    txBuf = new ArrayList<>();
                    try {
                        int id = Integer.parseInt(p[1]);
                        String name = p[2];
                        double balance = Double.parseDouble(p[3]);
                        current = new Account(id, name, balance, null);
                        txBuf = new ArrayList<>();
                    } catch (Exception ignored) {}
                } else if (line.startsWith("TX,") && current != null) {
                    txBuf.add(line.substring(3));
                } else if (line.equals("END") && current != null) {
                    Account restored = new Account(current.getId(), current.getName(), current.getBalance(), txBuf);
                    accounts.put(restored.getId(), restored);
                    current = null;
                }
            }
        } catch (IOException e) {
            System.out.println("  Warning: could not load accounts.");
        }
    }

    private static double promptAmount(String prompt) {
        System.out.print(prompt);
        try {
            double v = Double.parseDouble(scanner.nextLine().trim());
            if (v <= 0) { System.out.println("  Amount must be positive."); return -1; }
            return v;
        } catch (NumberFormatException e) {
            System.out.println("  Invalid amount.");
            return -1;
        }
    }
}
