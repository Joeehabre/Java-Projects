import java.util.Scanner;

public class Calculator {

    public static double calculate(double a, double b, char operator) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> b != 0 ? a / b : Double.NaN;
            case '^' -> Math.pow(a, b);
            case '%' -> a % b;
            default -> Double.NaN;
        };
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("🧮 Welcome to Java Calculator");

        while (true) {
            System.out.print("\nEnter first number (or type 'q' to quit): ");
            String input = scanner.next();
            if (input.equalsIgnoreCase("q")) break;

            double num1;
            try {
                num1 = Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number.");
                continue;
            }

            System.out.print("Enter operator (+ - * / ^ %): ");
            char op = scanner.next().charAt(0);

            System.out.print("Enter second number: ");
            double num2;
            try {
                num2 = Double.parseDouble(scanner.next());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number.");
                continue;
            }

            double result = calculate(num1, num2, op);
            if (Double.isNaN(result)) {
                System.out.println("❌ Invalid operation or division by zero.");
            } else {
                System.out.println("✅ Result: " + result);
            }
        }

        System.out.println("👋 Thanks for using the calculator!");
    }
}
