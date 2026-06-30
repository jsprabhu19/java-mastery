package com.mastery.functional;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.function.Supplier;

@JavaConcept(
    name = "Supplier and Lazy Evaluation",
    difficulty = Difficulty.INTERMEDIATE,
    what = "A Supplier represents an operation that takes no arguments and returns a result. It enables Lazy Evaluation, where computation of values is deferred until they are explicitly requested via supplier.get().",
    whyItMatters = "Eager evaluation executes expensive operations immediately, wasting memory and CPU cycles if the result is ultimately ignored (e.g., generating diagnostic log strings when debug logging is disabled). Suppliers defer execution until strictly needed.",
    keyPoints = {
        "Eager evaluation resolves arguments before method invocation.",
        "Lazy evaluation passes a Supplier lambda, resolving it only inside the method under specific conditions.",
        "Java's modern Logger framework uses Suppliers to prevent unnecessary string formatting overhead."
    },
    interviewQuestions = {
        @Question(
            question = "How does a Supplier enable lazy evaluation in Java?",
            answer = "Instead of evaluating an expression immediately and passing the computed value to a method, you wrap the expression in a Supplier lambda: () -> expression. The method receives the lambda reference and only invokes .get() when the output is actually required."
        ),
        @Question(
            question = "Why is logger.log(Level.DEBUG, \"User info: \" + getUserDetails()) considered a performance antipattern?",
            answer = "Even if DEBUG logging is disabled, Java evaluates the string concatenation and calls getUserDetails() eagerly before passing it to log(). Wrapping it in a Supplier () -> \"User info: \" + getUserDetails() ensures getUserDetails() is never called if DEBUG is disabled."
        )
    },
    pitfalls = {
        "Calling supplier.get() multiple times inside a method, triggering the expensive computation repeatedly instead of caching the initial result."
    }
)
@SuppressWarnings("all")
public class SupplierLazyEvaluation {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Supplier & Lazy Evaluation");

        // 1. Eager Evaluation Demo
        ConsoleFormatter.printStep("Eager Evaluation", "Calling logMethodEager() where argument is pre-computed");
        long start = System.nanoTime();
        logMethodEager(false, computeExpensiveData()); // computeExpensiveData runs even though log is false!
        long durationEager = (System.nanoTime() - start) / 1000000;
        System.out.println("Eager logging execution: " + durationEager + " ms");

        // 2. Lazy Evaluation Demo
        ConsoleFormatter.printStep("Lazy Evaluation", "Calling logMethodLazy() passing a Supplier lambda");
        start = System.nanoTime();
        logMethodLazy(false, () -> computeExpensiveData()); // computeExpensiveData never runs because log is false!
        long durationLazy = (System.nanoTime() - start) / 1000000;
        System.out.println("Lazy logging execution: " + durationLazy + " ms");

        if (durationLazy < durationEager) {
            ConsoleFormatter.printSuccess("Lazy evaluation saved substantial CPU execution time!");
        }
    }

    private static String computeExpensiveData() {
        System.out.println("    --> [COMPUTATION] Running expensive calculations...");
        try {
            Thread.sleep(500); // Simulate high database/networking load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Calculated Core Insights: 0x4B3A";
    }

    // Eager logger
    private static void logMethodEager(boolean enabled, String message) {
        if (enabled) {
            System.out.println("LOG: " + message);
        } else {
            System.out.println("LOG: Logging is disabled. (Eager argument discarded)");
        }
    }

    // Lazy logger using Supplier
    private static void logMethodLazy(boolean enabled, Supplier<String> messageSupplier) {
        if (enabled) {
            System.out.println("LOG: " + messageSupplier.get()); // Evaluated only here!
        } else {
            System.out.println("LOG: Logging is disabled. (Lazy Supplier skipped)");
        }
    }
}
