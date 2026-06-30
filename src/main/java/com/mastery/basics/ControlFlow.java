package com.mastery.basics;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "Modern Control Flow and Switch Expressions",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Control flow manages the execution path of a program. Modern Java enhances switch statements into switch expressions that can yield values, prevent fall-through bugs, and support pattern matching.",
    whyItMatters = "Switch expressions reduce boilerplate code, make code safer by requiring exhaustive checks, and support pattern matching, replacing complex if-else blocks.",
    keyPoints = {
        "Labeled breaks and continues allow flow manipulation of outer/nested loops.",
        "Switch expressions use the arrow (->) syntax to execute a single branch without requiring 'break' statements (no fall-through).",
        "Switch expressions can yield values back directly using the 'yield' keyword inside code blocks.",
        "Modern switch supports pattern matching for checking runtime object types exhaustively."
    },
    interviewQuestions = {
        @Question(
            question = "What is the difference between a switch statement and a switch expression?",
            answer = "A switch statement executes code branches but does not return a value, requiring 'break' to prevent fall-through. A switch expression returns a value, uses arrows (->) to prevent fall-through, and must be exhaustive (every possible case handled)."
        ),
        @Question(
            question = "When do you use the yield keyword in a switch expression?",
            answer = "Use 'yield' when a branch in a switch expression requires multiple lines of logic enclosed within a curly-braced block to return its final value."
        )
    },
    pitfalls = {
        "Accidental fall-through in legacy switch statements due to missing break statements.",
        "Non-exhaustive switch expressions. If all enum cases or types are not covered, a compile-time error occurs unless a 'default' case is supplied."
    }
)
public class ControlFlow {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Modern Control Flow & Switch Expressions");

        // 1. Labeled Break and Continue Demo
        ConsoleFormatter.printStep("Labeled Outer Loop Control", "Searching for a number inside a 2D array");
        int[][] grid = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        int target = 5;
        boolean found = false;

        outerLoop: 
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] == target) {
                    found = true;
                    System.out.println("Found " + target + " at position (" + r + ", " + c + ")");
                    break outerLoop; // Breaks outer nested loop directly!
                }
            }
        }
        if (found) {
            ConsoleFormatter.printSuccess("Outer loop broken successfully when target was located.");
        }

        // 2. Modern Switch Expressions (Arrow Syntax)
        ConsoleFormatter.printStep("Modern Switch Expression", "Assigning a value based on a weekday enum");
        Day day = Day.WEDNESDAY;
        
        String activity = switch (day) {
            case MONDAY, TUESDAY -> "Work on project backlog";
            case WEDNESDAY -> "Team synchronization call";
            case THURSDAY, FRIDAY -> "Code review and deployment preparation";
            case SATURDAY, SUNDAY -> "Rest and personal growth";
        };
        ConsoleFormatter.printSuccess("Activity for " + day + " is: '" + activity + "'");

        // 3. Switch Expressions with Yield
        ConsoleFormatter.printStep("Switch with Yield blocks", "Computing discount rate using complex branches");
        CustomerTier tier = CustomerTier.GOLD;
        
        double discount = switch (tier) {
            case REGULAR -> 0.05;
            case SILVER -> 0.10;
            case GOLD -> {
                double base = 0.15;
                double loyaltyBonus = 0.02;
                yield base + loyaltyBonus; // Yields value from block
            }
        };
        ConsoleFormatter.printSuccess("Calculated discount for tier " + tier + " is: " + (discount * 100) + "%");

        // 4. Pattern Matching in Switch (Java 21/25 feature showcase!)
        ConsoleFormatter.printStep("Pattern Matching switch", "Determining runtime types and extracting characteristics");
        Object payload = "Antigravity coding assistant";
        
        String description = switch (payload) {
            case Integer i -> "An integer value: " + i;
            case String s -> "A string of length " + s.length() + ": '" + s + "'";
            case Double d -> "A double floating point: " + d;
            default -> "Unknown payload structure: " + payload.getClass().getSimpleName();
        };
        ConsoleFormatter.printSuccess("Pattern match output: " + description);
    }

    enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }
    enum CustomerTier { REGULAR, SILVER, GOLD }
}
