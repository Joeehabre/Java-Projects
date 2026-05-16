import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public class Calculator {
    private static final Deque<String> history = new ArrayDeque<>();
    private static double lastResult = 0;
    private static boolean hasResult = false;

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

    private static String fmt(double n) {
        if (Double.isNaN(n) || Double.isInfinite(n)) return String.valueOf(n);
        return (n == Math.floor(n) && Math.abs(n) < 1e15) ? String.valueOf((long) n) : String.valueOf(n);
    }

    private static void record(String entry, double result) {
        lastResult = result;
        hasResult = true;
        history.addFirst(entry);
        if (history.size() > 10) history.removeLast();
    }

    private static Double parseInput(String input) {
        if (input.equalsIgnoreCase("ans")) {
            if (!hasResult) { System.out.println("  No previous result yet."); return null; }
            System.out.println("  Using ans = " + fmt(lastResult));
            return lastResult;
        }
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("  Invalid number: " + input);
            return null;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Java Calculator");
        System.out.println("  Operators : + - * / ^ %");
        System.out.println("  Unary     : sqrt <number>");
        System.out.println("  Special   : ans (last result)  |  h (history)  |  q (quit)");
        System.out.println();

        while (true) {
            System.out.print(">> First number: ");
            String input = scanner.next().trim();

            if (input.equalsIgnoreCase("q")) break;

            if (input.equalsIgnoreCase("h")) {
                if (history.isEmpty()) System.out.println("  (no history yet)");
                else history.forEach(e -> System.out.println("  " + e));
                System.out.println();
                continue;
            }

            Double num1 = parseInput(input);
            if (num1 == null) { System.out.println(); continue; }

            System.out.print("   Operator : ");
            String opStr = scanner.next().trim();

            if (opStr.equalsIgnoreCase("sqrt")) {
                if (num1 < 0) { System.out.println("  Cannot take sqrt of a negative number.\n"); continue; }
                double result = Math.sqrt(num1);
                String entry = "sqrt(" + fmt(num1) + ") = " + fmt(result);
                System.out.println("  = " + fmt(result));
                record(entry, result);
                System.out.println();
                continue;
            }

            if (opStr.length() != 1 || "+-*/^%".indexOf(opStr.charAt(0)) < 0) {
                System.out.println("  Unknown operator '" + opStr + "'. Use: + - * / ^ %\n");
                continue;
            }
            char op = opStr.charAt(0);

            System.out.print("   Second   : ");
            Double num2 = parseInput(scanner.next().trim());
            if (num2 == null) { System.out.println(); continue; }

            double result = calculate(num1, num2, op);
            if (Double.isNaN(result)) {
                System.out.println("  Invalid operation (e.g. division by zero).\n");
            } else {
                String entry = fmt(num1) + " " + op + " " + fmt(num2) + " = " + fmt(result);
                System.out.println("  = " + fmt(result));
                record(entry, result);
                System.out.println();
            }
        }

        System.out.println("Bye!");
        scanner.close();
    }
}
