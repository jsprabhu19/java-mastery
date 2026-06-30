package com.mastery.concurrency;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.concurrent.CompletableFuture;

@JavaConcept(
    name = "Asynchronous Concurrency with CompletableFuture",
    difficulty = Difficulty.ADVANCED,
    what = "CompletableFuture implements Future and CompletionStage, supporting non-blocking asynchronous task pipelines. It allows chaining dependent tasks, merging independent actions, and handling failures functionally.",
    whyItMatters = "Legacy Futures block threads via future.get() to retrieve results. CompletableFuture registers event-driven callbacks (like thenApply or thenAccept) that execute automatically when tasks finish, maximizing CPU efficiency.",
    keyPoints = {
        "supplyAsync() triggers execution on the default ForkJoinPool.commonPool() unless an explicit executor is provided.",
        "thenApply() transforms results (like map); thenCompose() chains dependent futures (like flatMap); thenCombine() merges two independent futures.",
        "exceptionally() provides default fallback values if exceptions occur inside the pipeline."
    },
    interviewQuestions = {
        @Question(
            question = "What is the difference between thenApply() and thenCompose() in CompletableFuture?",
            answer = "thenApply() is used for basic transformations, taking a Function<T, U> and returning CompletableFuture<U>. thenCompose() is used to chain dependent asynchronous tasks where the mapper returns another CompletableFuture, returning a flattened CompletableFuture<U>."
        ),
        @Question(
            question = "How does exceptionally() handle errors in a CompletableFuture pipeline?",
            answer = "exceptionally() acts like a catch block in a functional pipeline. If any previous step throws an exception, execution skips remaining success steps and triggers exceptionally(), allowing you to supply a default fallback value and keep the pipeline alive."
        )
    },
    pitfalls = {
        "Blocking threads prematurely by calling join() or get() in the middle of a pipeline, losing the advantages of async callbacks.",
        "Forgetting to supply custom thread pools for blocking I/O operations inside supplyAsync(), which blocks the common pool threads."
    }
)
public class CompletableFutureBasics {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("CompletableFuture Async Pipelines");

        // 1. Chaining & Transformations
        ConsoleFormatter.printStep("Async Chain", "Running supplyAsync -> thenApply -> thenAccept");
        CompletableFuture<Void> pipeline = CompletableFuture.supplyAsync(() -> {
            System.out.println("  [TASK-1] Fetching client data on thread: " + Thread.currentThread().getName());
            return "Client Profile: John Doe";
        }).thenApply(profile -> {
            System.out.println("  [TASK-2] Formatting profile text...");
            return profile.toUpperCase();
        }).thenAccept(formatted -> {
            System.out.println("  [TASK-3] Result consumed: " + formatted);
        });

        // Wait for pipeline completion
        pipeline.join();

        // 2. Combining Independent Futures
        ConsoleFormatter.printStep("Combining Independent Futures", "Running two async tasks in parallel and merging results");
        CompletableFuture<Double> priceFuture = CompletableFuture.supplyAsync(() -> 100.00);
        CompletableFuture<Double> exchangeRateFuture = CompletableFuture.supplyAsync(() -> 0.85);

        CompletableFuture<Double> convertedPriceFuture = priceFuture.thenCombine(exchangeRateFuture, (price, rate) -> {
            System.out.println("  [COMBINER] Merging price $" + price + " with exchange rate " + rate);
            return price * rate;
        });

        double converted = convertedPriceFuture.join();
        System.out.println("Converted final price: " + converted);

        // 3. Exception Handling functional fallback
        ConsoleFormatter.printStep("Functional Exception Fallback", "Running pipeline that throws an exception and catching it functionally");
        CompletableFuture<String> errorPipeline = CompletableFuture.<String>supplyAsync(() -> {
            throw new RuntimeException("Service Unavailable!");
        }).exceptionally(ex -> {
            System.out.println("  [FALLBACK] Caught exception: " + ex.getMessage());
            return "Fallback User Profile Data";
        });

        String finalData = errorPipeline.join();
        System.out.println("Pipeline ending data: " + finalData);

        if ("Fallback User Profile Data".equals(finalData) && converted == 85.00) {
            ConsoleFormatter.printSuccess("CompletableFuture operations and exception handling validated.");
        }
    }
}
