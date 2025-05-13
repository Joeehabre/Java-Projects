import java.util.*;

class BankAccount {
    private static int counter = 1000;
    private final int id;
    private final String name;
    private double balance;

    public BankAccount(String name, double initialDeposit) {
        this.id = ++counter;
        this.name = name;
        this.balance = initialDeposit;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("❌ Amount must be positive.");
            return;
        }
        balance += amount;
        System.out.println("✅ Deposited $" + amount);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("❌ Amount must be positive.");
        } else if (amount > balance) {
            System.out.println("❌ Insufficient balance.");
        } else {
            balance -= amount;
            System.out.println("✅ Withdrew $" + amount);
        }
    }

    public void showBalance() {
        System.out.printf("💰 %s's balance: $%.2f%n", name, balance);
    }
}

public class BankApp {
    private static final Map<Integer, BankAccount> accounts = new HashMap<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n🏦 Welcome to SimpleBank");
            System.out.println("1. Create Account");
            System.out.println("2. Access Account");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> createAccount();
                case "2" -> accessAccount();
                case "3" -> {
                    System.out.println("👋 Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    private static void createAccount() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Initial deposit: ");
        double deposit = Double.parseDouble(scanner.nextLine());

        BankAccount account = new BankAccount(name, deposit);
        accounts.put(account.getId(), account);
        System.out.println("✅ Account created! Your ID: " + account.getId());
    }

    private static void accessAccount() {
        System.out.print("Enter your account ID: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID.");
            return;
        }

        BankAccount account = accounts.get(id);
        if (account == null) {
            System.out.println("❌ Account not found.");
            return;
        }

        while (true) {
            System.out.printf("\n👤 Welcome, %s (ID: %d)%n", account.getName(), account.getId());
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check Balance");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Amount to deposit: ");
                    double amount = Double.parseDouble(scanner.nextLine());
                    account.deposit(amount);
                }
                case "2" -> {
                    System.out.print("Amount to withdraw: ");
                    double amount = Double.parseDouble(scanner.nextLine());
                    account.withdraw(amount);
                }
                case "3" -> account.showBalance();
                case "4" -> { return; }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }
}
