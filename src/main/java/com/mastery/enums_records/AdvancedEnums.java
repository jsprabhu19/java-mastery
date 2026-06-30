package com.mastery.enums_records;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "Advanced Enums with Custom Behaviors",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Enums in Java are specialized class types that represent fixed sets of constants. Enums can contain fields, constructors, instance methods, implement interfaces, and declare abstract methods overridden by specific constants.",
    whyItMatters = "Basic enums are simple constants. Advanced enums with instance behaviors replace complex switch-case or if-else trees. Each constant holds its own execution logic, satisfying polymorphism principles.",
    keyPoints = {
        "Enums are implicitly subclasses of java.lang.Enum and cannot extend other classes.",
        "Enum constructors are always private; calling new on enums causes compile errors.",
        "Constants can declare class bodies to override abstract methods defined in the parent Enum structure."
    },
    interviewQuestions = {
        @Question(
            question = "Can an Enum implement interfaces and extend other classes?",
            answer = "An Enum can implement multiple interfaces (e.g. Runnable, Comparable). However, it cannot extend any class because it already implicitly extends java.lang.Enum, and Java does not support multiple class inheritance."
        ),
        @Question(
            question = "How do you declare a unique execution behavior for each Enum constant without switch cases?",
            answer = "By declaring an abstract method inside the Enum body. Each constant must then provide a implementation block overriding that abstract method (constant-specific class bodies)."
        )
    },
    pitfalls = {
        "Declaring mutable instance variables inside Enums. Enums are shared singletons globally; changing state in one thread alters it for all threads, causing race conditions."
    }
)
public class AdvancedEnums {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Advanced Enums");

        // 1. Basic properties
        ConsoleFormatter.printStep("Enum State Properties", "Reading fields and executing standard methods");
        PaymentStatus status = PaymentStatus.COMPLETED;
        System.out.println("Status constant: " + status.name());
        System.out.println("Status code: " + status.getCode());
        System.out.println("Status description: " + status.getDescription());

        // 2. Overriding Abstract Methods per constant
        ConsoleFormatter.printStep("Constant-Specific Behaviors", "Executing abstract operations defined on enums");
        double x = 12.0;
        double y = 4.0;

        for (CalculatorOperation op : CalculatorOperation.values()) {
            double result = op.apply(x, y);
            System.out.printf("  [CALC] %s: %.1f %s %.1f = %.1f%n",
                    op.name(), x, op.getSymbol(), y, result);
        }

        double multiplyResult = CalculatorOperation.TIMES.apply(x, y);
        if (multiplyResult == 48.0) {
            ConsoleFormatter.printSuccess("Polymorphic Enum operations executed successfully.");
        }
    }

    // Enum with fields and constructor
    public enum PaymentStatus {
        PENDING(100, "Transaction awaiting bank approval"),
        COMPLETED(200, "Transaction settled successfully"),
        FAILED(500, "Transaction rejected by payment gateway");

        private final int code;
        private final String description;

        // Constructor must be package-private or private
        PaymentStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }
        public String getDescription() { return description; }
    }

    // Enum implementing interface and declaring abstract method
    public interface Arithmetic {
        double apply(double a, double b);
    }

    public enum CalculatorOperation implements Arithmetic {
        PLUS("+") {
            @Override
            public double apply(double a, double b) { return a + b; }
        },
        MINUS("-") {
            @Override
            public double apply(double a, double b) { return a - b; }
        },
        TIMES("*") {
            @Override
            public double apply(double a, double b) { return a * b; }
        },
        DIVIDE("/") {
            @Override
            public double apply(double a, double b) {
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            }
        };

        private final String symbol;

        CalculatorOperation(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() { return symbol; }
    }
}
