package com.mastery.streams;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JavaConcept(
    name = "Stream API Basics and Lazy Evaluation",
    difficulty = Difficulty.BEGINNER,
    what = "A Stream is a sequence of elements supporting sequential and parallel aggregate operations. Stream pipelines consist of a source, zero or more intermediate operations (lazy), and a terminal operation (eager).",
    whyItMatters = "Streams process large data collections declaratively, improving code clarity. Since intermediate operations are lazy, elements are only processed as needed by the terminal operation, saving resources.",
    keyPoints = {
        "Streams do not modify the original data source; they yield new collections or results.",
        "Intermediate operations (filter, map) return a new Stream and are evaluated lazily.",
        "Terminal operations (collect, count, forEach) trigger stream traversal and execute the pipeline."
    },
    interviewQuestions = {
        @Question(
            question = "What does it mean that Stream operations are lazy?",
            answer = "Intermediate operations (like filter() or map()) are not executed immediately. Instead, they build a query plan. Execution only starts when a terminal operation (like collect() or forEach()) is invoked on the stream."
        ),
        @Question(
            question = "Can a Stream be reused after a terminal operation has run?",
            answer = "No, a stream is consumed once a terminal operation executes. Attempting to reuse the stream throws an IllegalStateException: 'stream has already been operated upon or closed'."
        )
    },
    pitfalls = {
        "Forgetting to call a terminal operation, which results in the stream pipeline never executing.",
        "Attempting to reuse a stream reference after a terminal execution has finished."
    }
)
@SuppressWarnings("all")
public class StreamsBasic {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Streams Basics & Lazy Evaluation");

        // 1. Basic filter/map/collect pipeline
        ConsoleFormatter.printStep("Basic Stream Pipeline", "Filtering names by starting character and transforming to uppercase");
        List<String> names = List.of("Alice", "Bob", "Charlie", "Alex", "David");

        List<String> filteredNames = names.stream()
                .filter(name -> name.startsWith("A"))
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        System.out.println("Original list: " + names);
        System.out.println("Filtered & Mapped output: " + filteredNames);
        ConsoleFormatter.printSuccess("Basic stream pipeline completed successfully.");

        // 2. Lazy Evaluation Demo
        ConsoleFormatter.printStep("Lazy Evaluation Validation", "Demonstrating that intermediate operations do not execute without a terminal call");
        List<Integer> numbers = List.of(1, 2, 3);
        List<Integer> sideEffectTracker = new ArrayList<>();

        // Create stream with map intermediate operation
        var stream = numbers.stream()
                .map(n -> {
                    sideEffectTracker.add(n); // Side effect tracking
                    System.out.println("      Executing map on: " + n);
                    return n * 2;
                });

        System.out.println("  * Stream object created. Side-effect list size: " + sideEffectTracker.size());
        if (sideEffectTracker.isEmpty()) {
            ConsoleFormatter.printSuccess("Verified: Map operation was NOT executed eagerly when stream was defined!");
        }

        System.out.println("  * Invoking terminal collect operation...");
        List<Integer> doubled = stream.collect(Collectors.toList());
        System.out.println("  * Doubled values: " + doubled);
        System.out.println("  * Side-effect list size: " + sideEffectTracker.size());

        if (sideEffectTracker.size() == 3) {
            ConsoleFormatter.printSuccess("Verified: Map operation executed only after terminal collect() triggered!");
        }
    }
}
