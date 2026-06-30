package com.mastery.exceptions;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.sql.SQLException;

@JavaConcept(
    name = "Exception Chaining",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Exception Chaining allows associating a cause (another exception) with a newly thrown exception. This enables wrapping low-level technical failures inside high-level business exceptions without losing root cause context.",
    whyItMatters = "Exposing SQL or file system exceptions to UI clients leaks system details and couples architecture. Chaining lets you throw clean domain exceptions, while logging the full exception stack trace for debugging.",
    keyPoints = {
        "Exceptions support constructor wrapping (new Exception(message, cause)).",
        "Use throwable.getCause() to navigate back down the chain of exceptions to find the original cause.",
        "Preserving stack traces is mandatory when catching and rethrowing exceptions."
    },
    interviewQuestions = {
        @Question(
            question = "Why is it important to chain exceptions rather than just rethrowing a new exception?",
            answer = "If you instantiate and throw a new exception without chaining, the stack trace of the original failure is lost. Debuggers will not know what triggered the original failure (e.g., a database connection dropout or a duplicate key conflict)."
        ),
        @Question(
            question = "How do you extract the root cause from a chain of nested exceptions?",
            answer = "You can write a loop checking ex.getCause(). While getCause() is not null, update the exception reference. The final non-null exception reference is the root cause."
        )
    },
    pitfalls = {
        "Losing exception context: catch (SQLException e) { throw new ServiceException(\"Failed\"); } (Missing cause parameter)."
    }
)
public class ExceptionChaining {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Exception Chaining");

        // 1. Simulating low-level wrapper
        ConsoleFormatter.printStep("Wrapping Technical Failure", "Simulating SQL error wrapping inside a Business exception");
        try {
            executeDatabaseQuery();
        } catch (DataAccessException dae) {
            ConsoleFormatter.printError("Business Layer caught DataAccessException: " + dae.getMessage(), null);
            System.out.println("  Printed Trace: " + dae);
            
            // 2. Traversing root cause
            ConsoleFormatter.printStep("Traversing Cause Chain", "Extracting root cause using getCause()");
            Throwable cause = dae.getCause();
            System.out.println("  Immediate Cause: " + cause);
            
            Throwable rootCause = findRootCause(dae);
            ConsoleFormatter.printSuccess("Root Cause found: " + rootCause.getClass().getSimpleName() + " - Message: " + rootCause.getMessage());
        }
    }

    private static void executeDatabaseQuery() throws DataAccessException {
        try {
            // Simulate deep driver SQLException
            throw new SQLException("Connection timed out to pool 'H2-DB'", "08001", 101);
        } catch (SQLException sqle) {
            // Throw high-level business exception WITH the cause chained!
            throw new DataAccessException("Failed to fetch client profiles", sqle);
        }
    }

    private static Throwable findRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause(); // Walk up the chain
        }
        return cause;
    }

    // High level domain exception
    public static class DataAccessException extends Exception {
        public DataAccessException(String message, Throwable cause) {
            super(message, cause); // Chains the exception!
        }
    }
}
