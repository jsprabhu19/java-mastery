package com.mastery.concurrency;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "Memory Visibility and Volatile",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Threads cache variables in CPU registers and local cores for performance, causing memory visibility drift. The volatile keyword forces threads to read and write variables directly from main memory, ensuring updates are visible instantly to all threads.",
    whyItMatters = "Without volatile, flag updates (e.g., termination signals) set by a controller thread are cached by a worker thread, resulting in infinite loops or zombie threads in production.",
    keyPoints = {
        "volatile guarantees visibility across threads, but it does NOT guarantee atomicity (compound actions like count++ are still unsafe).",
        "volatile establishes a happens-before relationship, guaranteeing instruction ordering and preventing CPU instruction reordering.",
        "For atomic compound operations on shared variables, use classes in java.util.concurrent.atomic (e.g., AtomicInteger)."
    },
    interviewQuestions = {
        @Question(
            question = "Does volatile make counter++ thread-safe?",
            answer = "No. counter++ is a compound operation consisting of three distinct steps: read current value, increment it, and write it back. volatile only guarantees that other threads see the latest write, but it does not prevent concurrent threads from interleaving during these steps, causing race conditions."
        ),
        @Question(
            question = "What is the Happens-Before relationship established by volatile?",
            answer = "A write to a volatile variable happens-before every subsequent read of that same volatile variable. This ensures that any changes made by Thread A prior to writing to a volatile field are guaranteed to be visible to Thread B when it reads that volatile field."
        )
    },
    pitfalls = {
        "Using volatile for multi-thread variables that require complex check-then-act atomic operations.",
        "Overusing volatile where basic thread confinement or locks are already protecting access."
    }
)
public class MemoryVisibilityVolatile {
    
    // Flag WITHOUT volatile - subject to caching
    private static boolean stopRequestedNonVolatile = false;
    
    // Flag WITH volatile - visible immediately
    private static volatile boolean stopRequestedVolatile = false;

    public static void main(String[] args) throws InterruptedException {
        ConsoleFormatter.printHeader("Memory Visibility & Volatile");

        // 1. Thread caching demonstration
        ConsoleFormatter.printStep("Memory Visibility Hazard", "Running loop thread check on non-volatile variable (simulated check)");
        
        Thread workerNonVolatile = new Thread(() -> {
            int count = 0;
            // The JIT compiler often optimizes this to 'while(!stopRequestedNonVolatile)'
            // caching the false value forever in CPU registers if it sees no changes locally.
            while (!stopRequestedNonVolatile) {
                count++;
                // Adding a print or sleep here forces a context switch and register reload,
                // masking the visibility issue. Thus, we keep this loop clean and tight.
                if (count == Integer.MAX_VALUE) {
                    System.out.println("      Loop reached count threshold... (Running indefinitely due to cached flag)");
                    break; 
                }
            }
            System.out.println("  [NON-VOLATILE WORKER] Thread stopped cleanly.");
        });

        workerNonVolatile.start();
        Thread.sleep(100); // Give thread time to boot
        stopRequestedNonVolatile = true; // Attempt termination
        System.out.println("  [MAIN] Set stopRequestedNonVolatile to true.");
        workerNonVolatile.join(1000); // Wait up to 1 second

        if (workerNonVolatile.isAlive()) {
            ConsoleFormatter.printWarning("Worker is STILL ALIVE! It did not see the 'stopRequested = true' due to CPU cache residency.");
            workerNonVolatile.interrupt(); // Kill thread
        }

        // 2. Solving with Volatile
        ConsoleFormatter.printStep("Solving with Volatile", "Running loop thread check on volatile flag");
        
        Thread workerVolatile = new Thread(() -> {
            long count = 0;
            while (!stopRequestedVolatile) {
                count++;
            }
            System.out.println("  [VOLATILE WORKER] Thread stopped cleanly after " + count + " loops.");
        });

        workerVolatile.start();
        Thread.sleep(100);
        stopRequestedVolatile = true;
        System.out.println("  [MAIN] Set stopRequestedVolatile to true.");
        workerVolatile.join(1000);

        if (!workerVolatile.isAlive()) {
            ConsoleFormatter.printSuccess("Worker thread stopped instantly because volatile bypassed the CPU caching registers!");
        }
    }
}
