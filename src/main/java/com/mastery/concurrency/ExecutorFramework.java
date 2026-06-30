package com.mastery.concurrency;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.concurrent.*;

@JavaConcept(
    name = "Executor Service and Thread Pools",
    difficulty = Difficulty.INTERMEDIATE,
    what = "The Executor Framework manages worker threads, decoupling thread allocation from execution logic. Thread Pools reuse pre-created threads, resolving the overhead of repeatedly spawning operating system threads.",
    whyItMatters = "Manually creating new Thread objects for high-frequency operations wastes OS resources and risks OutOfMemoryError due to stack allocations. Thread pools limit concurrent thread count and queue overflow safely.",
    keyPoints = {
        "FixedThreadPool uses a fixed thread count and an unbounded LinkedBlockingQueue.",
        "CachedThreadPool creates threads on-demand and uses a SynchronousQueue. Idle threads are destroyed after 60 seconds.",
        "Always shut down Executors. Executors do not use daemon threads by default, preventing JVM shutdown if left running."
    },
    interviewQuestions = {
        @Question(
            question = "What is the difference between submit() and execute() methods on ExecutorService?",
            answer = "execute() takes a Runnable and returns void, and any unhandled exception in the task terminates the thread (though the pool spawns a replacement). submit() accepts Callable or Runnable and returns a Future, capturing any task exceptions inside the Future wrapper to be thrown during future.get()."
        ),
        @Question(
            question = "Why does CachedThreadPool use SynchronousQueue internally?",
            answer = "SynchronousQueue has zero capacity. It serves as a direct handoff mechanism where task insertions must wait for a worker thread to take it, forcing the pool to spawn a new thread if all existing threads are busy."
        )
    },
    pitfalls = {
        "Forgetting to call shutdown() or shutdownNow(), causing threads to stay alive in background and blocking JVM termination.",
        "Using FixedThreadPool with unbounded queues for memory-intensive high-traffic applications, leading to OutOfMemoryError if workers cannot keep pace with insertions."
    }
)
public class ExecutorFramework {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Executor Service & Thread Pools");

        // 1. Thread Pool initialization and Callable execution
        ConsoleFormatter.printStep("Callable & Future", "Submitting a computation to a Fixed Thread Pool");
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<Integer> expensiveCalculation = () -> {
            System.out.println("  [WORKER] Running complex task on thread: " + Thread.currentThread().getName());
            Thread.sleep(300); // Simulate processing
            return 42;
        };

        Future<Integer> future = executor.submit(expensiveCalculation);
        System.out.println("  [MAIN] Task submitted. Checking completed state: " + future.isDone());

        try {
            // Block and wait for result
            int result = future.get(); // Blocks until worker returns 42
            System.out.println("  [MAIN] Retrieved task result: " + result);
            if (result == 42) {
                ConsoleFormatter.printSuccess("Future successfully retrieved result!");
            }
        } catch (InterruptedException | ExecutionException e) {
            ConsoleFormatter.printError("Exception occurred during task evaluation", e);
        }

        // 2. Shutting down Executor Service
        ConsoleFormatter.printStep("Executor Shutdown", "Initiating shutdown and waiting for workers to close");
        executor.shutdown(); // Refuses new tasks, completes existing ones
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow(); // Force terminates running threads
            }
            ConsoleFormatter.printSuccess("Executor Service terminated cleanly.");
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
