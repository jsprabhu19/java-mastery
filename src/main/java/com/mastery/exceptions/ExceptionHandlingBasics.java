package com.mastery.exceptions;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.io.IOException;

@JavaConcept(
    name = "Exception Handling Basics (Checked vs Unchecked)",
    difficulty = Difficulty.BEGINNER,
    what = "Java handles runtime errors using Exceptions. Checked exceptions must be declared in method signatures or caught at compile-time; Unchecked (Runtime) exceptions represent programming or logic errors that can happen dynamically.",
    whyItMatters = "Incorrect exception flow leads to application crashes or swallowed errors. Knowing how to leverage unchecked exceptions for business failures keeps signatures clean and codebase maintainable.",
    keyPoints = {
        "Checked exceptions inherit from Throwable/Exception (excluding RuntimeException subclasses) and force compile-time handling.",
        "Unchecked exceptions inherit from RuntimeException and do not require declaration or immediate catching.",
        "Multi-catch blocks (catch (A | B e)) allow handling multiple unrelated exceptions without repeating catch bodies.",
        "When catching exceptions, always position subclass catches before superclass catches (specific to generic)."
    },
    interviewQuestions = {
        @Question(
            question = "What is the difference between Checked and Unchecked exceptions?",
            answer = "Checked exceptions are checked at compile-time (e.g., IOException). Unchecked exceptions occur at runtime (e.g., NullPointerException). Checked exceptions represent recoverable conditions, while Unchecked exceptions indicate programming bugs."
        ),
        @Question(
            question = "Can a method override declare a broader Checked exception than the superclass method?",
            answer = "No. An overriding method in a subclass cannot throw broader or new checked exceptions. It can only throw the same exceptions, narrower subclasses, or declare no exceptions at all."
        )
    },
    pitfalls = {
        "Swallowing exceptions (empty catch block), which leaves systems in undefined states with no debugging trails.",
        "Catching generic Exception or Throwable everywhere, hiding critical system errors like OutOfMemoryError."
    }
)
@SuppressWarnings("all")
public class ExceptionHandlingBasics {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Exception Handling Basics");

        // 1. Checked Exception compilation rules
        ConsoleFormatter.printStep("Checked Exception Handling", "Executing method that forces compile-time checked handling");
        try {
            triggerCheckedException();
        } catch (IOException e) {
            ConsoleFormatter.printError("Checked Exception caught successfully!", e);
        }

        // 2. Unchecked Exceptions call-stack unwinding
        ConsoleFormatter.printStep("Unchecked Exception Unwinding", "Allowing RuntimeException to unwind stack frame");
        try {
            methodA();
        } catch (NullPointerException npe) {
            ConsoleFormatter.printError("Runtime NPE caught from deep inside the stack trace:", npe);
        }

        // 3. Custom Business Exception & Multi-catch
        ConsoleFormatter.printStep("Custom Exceptions & Multi-Catch", "Validating dynamic inputs and executing multi-catch blocks");
        try {
            validateInputs("", -5);
        } catch (ValidationException | IllegalArgumentException e) {
            // Multi-catch in action
            ConsoleFormatter.printWarning("Multi-catch caught exception type: " + e.getClass().getSimpleName() + " - Message: " + e.getMessage());
            if (e instanceof ValidationException ve) {
                System.out.println("  Validation Error Code: " + ve.getErrorCode());
                System.out.println("  Rejected Value: " + ve.getRejectedValue());
            }
        }
        ConsoleFormatter.printSuccess("Exception handling flow demo finished.");
    }

    private static void triggerCheckedException() throws IOException {
        // Must declare 'throws IOException' or compile fails
        throw new IOException("Simulated disk read failure.");
    }

    private static void methodA() {
        methodB();
    }

    private static void methodB() {
        String data = null;
        data.length(); // Triggers NullPointerException
    }

    private static void validateInputs(String name, int age) throws ValidationException {
        if (name == null || name.isEmpty()) {
            throw new ValidationException("ERR-01", "Name cannot be empty", name);
        }
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative: " + age);
        }
    }

    // Custom Business Exception
    public static class ValidationException extends Exception {
        private final String errorCode;
        private final Object rejectedValue;

        public ValidationException(String errorCode, String message, Object rejectedValue) {
            super(message);
            this.errorCode = errorCode;
            this.rejectedValue = rejectedValue;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }
    }
}
