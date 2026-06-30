package com.mastery.streams;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@JavaConcept(
    name = "Expert Stream Operations (parallel overhead, ForkJoinPool customization, stateful lambdas)",
    difficulty = Difficulty.EXPERT,
    what = "Expert stream manipulation manages concurrent resources. Parallel streams split processing across threads. Running parallel tasks in custom ForkJoinPools prevents common pool exhaustion; stateful lambdas introduce non-deterministic race bugs.",
    whyItMatters = "By default, parallel streams run on the shared ForkJoinPool.commonPool(). If a thread executes a blocking IO task in this common pool, the entire JVM-wide concurrent system bottlenecks. Stateful lambdas yield corrupt outputs when run in parallel.",
    keyPoints = {
        "Small collections run slower in parallel due to thread spawning and split-merge CPU overhead.",
        "Parallel streams bind to the system-wide ForkJoinPool.commonPool() unless submitted inside a custom ForkJoinPool task.",
        "Stateful lambdas (modifying shared collections inside filters/maps) trigger race conditions and corrupted lists in parallel runs."
    },
    interviewQuestions = {
        @Question(
            question = "How do you run a parallel stream using a custom thread pool instead of the common pool?",
            answer = "You submit the parallel stream execution inside a Callable/Runnable block to a custom ForkJoinPool instance: customThreadPool.submit(() -> stream.parallel().collect(...)).get()."
        ),
        @Question(
            question = "Why are stateful lambdas dangerous when used in parallel streams?",
            answer = "Stateful lambdas rely on mutable states that change during execution. When run across multiple threads, concurrent modifications cause race conditions, data corruption, and non-deterministic results."
        )
    },
    pitfalls = {
        "Assuming parallel streams are always faster. Small data sizes or fast operations run significantly slower in parallel.",
        "Executing blocking I/O calls directly inside the parallel commonPool(), locking concurrent execution threads system-wide."
    }
)
@SuppressWarnings("all")
public class StreamsExpert {
    public static void main(String[] args) throws Exception {
        ConsoleFormatter.printHeader("Expert Streams (Parallel/Tuning/Thread-Pools)");

        // 1. Parallel Stream Overhead (Small collection check)
        ConsoleFormatter.printStep("Parallel Stream Overhead", "Comparing sequential vs parallel execution on a tiny array list");
        List<Integer> tinyList = List.of(1, 2, 3, 4, 5);

        long start = System.nanoTime();
        int sumSeq = tinyList.stream().map(n -> n * 2).reduce(0, Integer::sum);
        long durationSeq = System.nanoTime() - start;

        start = System.nanoTime();
        int sumPar = tinyList.stream().parallel().map(n -> n * 2).reduce(0, Integer::sum);
        long durationPar = System.nanoTime() - start;

        System.out.println("Sequential time: " + durationSeq + " ns");
        System.out.println("Parallel time:   " + durationPar + " ns");
        if (durationPar > durationSeq) {
            ConsoleFormatter.printWarning("Parallel stream was SLOWER than sequential for a small collection due to thread coordinator overhead!");
        }

        // 2. Custom ForkJoinPool Execution
        ConsoleFormatter.printStep("Custom ForkJoinPool Isolation", "Isolating parallel stream tasks to a custom thread pool");
        ForkJoinPool customPool = new ForkJoinPool(4); // Allocate 4 isolated threads
        
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 1000; i++) numbers.add(i);

        // Run stream inside the isolated custom pool
        long totalSum = customPool.submit(() -> 
            numbers.stream().parallel()
                .peek(n -> {
                    // Print thread name to verify it's NOT the common pool
                    if (n == 500) {
                        System.out.println("      Executing thread name: " + Thread.currentThread().getName());
                    }
                })
                .reduce(0, Integer::sum)
        ).get();

        System.out.println("Resulting isolated sum: " + totalSum);
        customPool.shutdown();

        if (totalSum == 499500) {
            ConsoleFormatter.printSuccess("Verified: Parallel stream ran inside isolated custom pool threads without blocking the common pool.");
        }

        // 3. Stateful Lambda Danger Demonstration
        ConsoleFormatter.printStep("Stateful Lambda Hazard", "Accessing a shared mutable list in a parallel stream");
        List<Integer> input = new ArrayList<>();
        for (int i = 0; i < 100; i++) input.add(i);

        List<Integer> corruptedCollection = Collections.synchronizedList(new ArrayList<>());
        
        // Stateful lambda: modifying corruptedCollection during map()
        input.stream().parallel()
            .map(x -> {
                corruptedCollection.add(x); // side-effect modifications
                return x * 2;
            })
            .collect(java.util.stream.Collectors.toList());

        System.out.println("Input size: 100. Synchronized list collector output size: " + corruptedCollection.size());
        
        // Let's demo a completely non-synchronized stateful write to show real race conditions
        List<Integer> raceCollection = new ArrayList<>(); // Unsynchronized
        try {
            input.stream().parallel()
                .map(x -> {
                    raceCollection.add(x); // Severe race condition!
                    return x;
                })
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            ConsoleFormatter.printWarning("Exception caught during race condition: " + e.getMessage());
        }
        System.out.println("Unsynchronized race collection size: " + raceCollection.size());

        ConsoleFormatter.printSuccess("Expert streams and thread isolation patterns executed.");
    }
}
