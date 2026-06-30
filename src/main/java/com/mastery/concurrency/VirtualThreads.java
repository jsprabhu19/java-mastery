package com.mastery.concurrency;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@JavaConcept(
    name = "Virtual Threads (Project Loom)",
    difficulty = Difficulty.EXPERT,
    what = "Virtual Threads are lightweight, JVM-managed threads designed to support high-throughput concurrent applications. They decoupled Java threads from expensive operating system threads, permitting applications to spawn millions of threads safely.",
    whyItMatters = "Traditional platform threads map 1-to-1 with OS threads, consuming ~1MB of memory for call stacks. Virtual threads are heap-allocated objects. Spawning 100,000 platform threads causes OutOfMemoryErrors; spawning 100,000 virtual threads takes milliseconds.",
    keyPoints = {
        "Virtual Threads run on top of standard platform carrier threads using ForkJoinPool scheduling.",
        "When a virtual thread executes blocking operations (sleep, Socket read, DB call), the JVM demounts it from the carrier thread, allowing other virtual tasks to run.",
        "Created using Thread.ofVirtual().start(runnable) or Executors.newVirtualThreadPerTaskExecutor()."
    },
    interviewQuestions = {
        @Question(
            question = "Explain how Virtual Threads improve throughput under blocking I/O calls.",
            answer = "With standard platform threads, blocking calls freeze the OS thread, tying up system resources. In Virtual Threads, blocking operations trigger the JVM to unmount the virtual thread stack from the carrier OS thread, freeing the carrier thread to execute other virtual tasks. Once the I/O block finishes, JVM reschedules the virtual thread on an available carrier thread."
        ),
        @Question(
            question = "Should you pool Virtual Threads using a thread pool?",
            answer = "No. Virtual threads are cheap, short-lived heap objects. You should instantiate a new virtual thread for each tasks instead of pooling them. Use Executors.newVirtualThreadPerTaskExecutor() directly."
        )
    },
    pitfalls = {
        "Pinning Carrier Threads: executing code inside 'synchronized' blocks or invoking native code blocks prevents virtual threads from unmounting, locking the carrier thread."
    }
)
@SuppressWarnings("all")
public class VirtualThreads {
    public static void main(String[] args) throws InterruptedException {
        ConsoleFormatter.printHeader("Virtual Threads (Project Loom)");

        // 1. Spawning a single Virtual Thread
        ConsoleFormatter.printStep("Basic Virtual Thread", "Spawning a virtual thread using Thread.ofVirtual()");
        Thread vThread = Thread.ofVirtual().name("Loom-Worker").start(() -> {
            System.out.println("  [V-THREAD] Executing Loom task: " + Thread.currentThread());
            System.out.println("  [V-THREAD] Is virtual: " + Thread.currentThread().isVirtual());
        });
        vThread.join();

        // 2. Running thousands of concurrent blocking tasks
        int taskCount = 10_000;
        ConsoleFormatter.printStep("High Throughput Scaling", "Spawning " + taskCount + " concurrent blocking tasks using Virtual Thread Executor");
        
        long startTime = System.currentTimeMillis();
        
        // Creates an executor that spawns a new virtual thread for each task
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < taskCount; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    try {
                        // Simulated blocking I/O (sleep)
                        Thread.sleep(100);
                        if (taskId == 5000) {
                            System.out.println("      Middle task executed on thread: " + Thread.currentThread());
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            // AutoCloseable try-with-resources triggers executor.close(), awaiting termination automatically!
        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Finished " + taskCount + " blocking tasks in: " + duration + " ms");
        
        if (duration < 2000) {
            ConsoleFormatter.printSuccess("Completed 10k blocking operations in under 2 seconds! Spawning 10k platform threads would have crashed the JVM or run significantly slower.");
        }
    }
}
