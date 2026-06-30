package com.mastery.jvm_internals;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.ArrayList;
import java.util.List;

@JavaConcept(
    name = "JVM Memory Areas (StackOverflow vs OutOfMemory)",
    difficulty = Difficulty.INTERMEDIATE,
    what = "The JVM partitions memory into distinct areas. The Stack holds local variables and method execution frames (each thread has its own stack). The Heap stores all object allocations and is shared across threads.",
    whyItMatters = "Understanding the boundary between stack and heap prevents memory leak bugs. Exhausting stack frames (deep recursion) triggers StackOverflowError; exhausting heap space (unbounded object growth) triggers OutOfMemoryError.",
    keyPoints = {
        "Stack holds local primitives and references; object instances reside on the Heap.",
        "Deep method recursion consumes stack frames, throwing java.lang.StackOverflowError.",
        "Accumulating active object references in lists without cleanup fills the heap, throwing java.lang.OutOfMemoryError."
    },
    interviewQuestions = {
        @Question(
            question = "Where are local primitive variables vs object instances stored in JVM memory?",
            answer = "Local primitive variables (and object references themselves) are stored in the stack frame of the thread executing the method. Actual object instances always reside on the shared heap."
        ),
        @Question(
            question = "What JVM parameters adjust the stack and heap sizes?",
            answer = "-Xss defines the stack size per thread. -Xms sets the initial heap size, and -Xmx sets the maximum heap size limit."
        )
    },
    pitfalls = {
        "Writing infinite recursion loops (StackOverflowError).",
        "Retaining references to unused objects inside static collections, causing JVM heap memory leaks (OutOfMemoryError)."
    }
)
public class MemoryBehavior {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("JVM Memory Areas");

        // 1. StackOverflowError simulation
        ConsoleFormatter.printStep("Stack Frame Exhaustion", "Simulating StackOverflowError via infinite recursion");
        try {
            recursiveMethod(1);
        } catch (StackOverflowError soe) {
            ConsoleFormatter.printError("StackOverflowError caught successfully at deep stack recursion frame!", null);
        }

        // 2. OutOfMemoryError simulation
        ConsoleFormatter.printStep("Heap Space Exhaustion", "Simulating Heap OutOfMemoryError by filling memory with objects");
        try {
            List<byte[]> memoryHog = new ArrayList<>();
            // Allocate large byte arrays in loop to fill heap quickly
            while (true) {
                memoryHog.add(new byte[1024 * 1024 * 10]); // 10MB chunks
                if (memoryHog.size() > 500) { // Limit to prevent crashing test run if max heap is massive
                    System.out.println("      Reached threshold allocation limit without crashing. Aborting OOM simulation.");
                    break;
                }
            }
        } catch (OutOfMemoryError oome) {
            ConsoleFormatter.printError("OutOfMemoryError caught successfully! Heap memory exhausted.", null);
        }

        ConsoleFormatter.printSuccess("JVM memory boundaries demonstrated cleanly.");
    }

    private static void recursiveMethod(int depth) {
        // No exit condition - forces stack frame exhaustion
        if (depth % 500 == 0) {
            System.out.println("      Stack depth reached: " + depth + " frames.");
        }
        recursiveMethod(depth + 1);
    }
}
