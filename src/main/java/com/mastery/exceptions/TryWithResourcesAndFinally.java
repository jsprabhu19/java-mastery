package com.mastery.exceptions;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "Try-With-Resources and Finally Semantics",
    difficulty = Difficulty.ADVANCED,
    what = "Try-with-resources handles closing of AutoCloseable resources automatically, preventing memory/socket leaks. The finally block guarantees execution block cleanups unless the JVM exits. If close() and the try body throw exceptions simultaneously, the close() exceptions are suppressed.",
    whyItMatters = "Swallowing exceptions during resource closure hides bugs. Try-with-resources automatically suppresses subordinate close failures and appends them to the primary exception, allowing developers to inspect the entire failure chain.",
    keyPoints = {
        "Resources must implement java.lang.AutoCloseable to be used in try-with-resources.",
        "Suppressed Exceptions: Close-failures are attached to the primary exception block via Throwable.getSuppressed().",
        "A return statement inside a finally block silently discards thrown exceptions and overrides try block returns."
    },
    interviewQuestions = {
        @Question(
            question = "What are suppressed exceptions in Java try-with-resources?",
            answer = "If an exception is thrown inside the try-block, and another is thrown during the automatic close() of the resource, the close() exception is suppressed so it doesn't mask the primary failure. It can be retrieved using primaryException.getSuppressed()."
        ),
        @Question(
            question = "Under what circumstances will a finally block NOT execute?",
            answer = "The finally block will not execute if System.exit() is called, if the JVM crashes, if there is an infinite loop in the try block, or if the power supply to the host machine is disconnected."
        )
    },
    pitfalls = {
        "Returning values from within a finally block. This is a severe antipattern because it swallows unhandled exceptions.",
        "Declaring non-AutoCloseable objects in try-with-resources headers."
    }
)
@SuppressWarnings("all")
public class TryWithResourcesAndFinally {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Try-With-Resources & Finally");

        // 1. Try-with-resources and Suppressed exceptions
        ConsoleFormatter.printStep("Suppressed Exceptions", "Triggering failures in both try body and close() method");
        try (FailingResource resource = new FailingResource()) {
            resource.use();
        } catch (Exception e) {
            ConsoleFormatter.printError("Primary Exception Caught: " + e.getMessage(), null);
            
            // Retrieve suppressed exceptions
            Throwable[] suppressed = e.getSuppressed();
            for (Throwable s : suppressed) {
                System.out.println("  ↳ Suppressed exception attached: " + s.getMessage());
            }
            if (suppressed.length > 0) {
                ConsoleFormatter.printSuccess("Verified: Close-failure was successfully suppressed and attached to primary exception.");
            }
        }

        // 2. Finally block return pitfall
        ConsoleFormatter.printStep("Finally Return Pitfall", "Demonstrating how returning in finally hides exceptions");
        int result = returnPitfallDemo();
        System.out.println("Returned value: " + result);
        ConsoleFormatter.printWarning("Notice that the ArithmeticException thrown inside the try block was completely swallowed by the finally block return!");

        // 3. System.exit exception
        ConsoleFormatter.printStep("System.exit Exception", "Explaining that System.exit terminates JVM instantly, skipping finally");
        System.out.println("  (Skipping actual System.exit(0) execution to allow the interactive run to continue!)");
    }

    private static int returnPitfallDemo() {
        try {
            int divideByZero = 10 / 0;
            return divideByZero;
        } finally {
            return 999; // Swallows the ArithmeticException!
        }
    }

    // Custom Resource implementing AutoCloseable
    static class FailingResource implements AutoCloseable {
        public void use() {
            System.out.println("  [RESOURCE] Using resource...");
            throw new RuntimeException("Body error: Data write failed.");
        }

        @Override
        public void close() throws Exception {
            System.out.println("  [RESOURCE] Auto-closing resource...");
            throw new Exception("Close error: Failed to release resource socket.");
        }
    }
}
