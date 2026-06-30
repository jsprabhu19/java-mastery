package com.mastery.functional;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.function.*;

@JavaConcept(
    name = "Lambda Expressions and Functional Interfaces",
    difficulty = Difficulty.BEGINNER,
    what = "Lambdas are anonymous block operations that can receive arguments and return results. A Functional Interface contains exactly one abstract method, acting as the target type for lambda expressions.",
    whyItMatters = "Functional interfaces form the core of Java's stream processing and functional frameworks. They allow behavior to be passed as arguments, simplifying code structures and replacing verbose anonymous classes.",
    keyPoints = {
        "Functional interfaces can have default and static methods, but exactly one abstract method (annotated with @FunctionalInterface).",
        "Variables referenced inside lambdas must be final or effectively final (values not altered after assignment).",
        "Standard pillars: Function (map), Predicate (test), Consumer (consume), Supplier (produce), and their Bi- variants."
    },
    interviewQuestions = {
        @Question(
            question = "Why must variables captured in a lambda expression be final or effectively final?",
            answer = "Because lambdas can execute asynchronously or in different threads. Java captures a copy of the local variables rather than referencing them directly on the stack. If these variables could be modified, the stack state and the lambda state would drift, creating thread-safety issues."
        ),
        @Question(
            question = "How do you declare a functional interface that accepts three inputs and returns a result?",
            answer = "By creating a custom interface annotated with @FunctionalInterface: interface TriFunction<T, U, V, R> { R apply(T t, U u, V v); }."
        )
    },
    pitfalls = {
        "Attempting to modify external local variables inside a lambda (causes compile errors).",
        "Overusing raw anonymous classes when clean lambdas or method references are available."
    }
)
public class LambdaAndFunctionalInterfaces {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Lambda Expressions & Functional Interfaces");

        // 1. Effectively Final validation
        ConsoleFormatter.printStep("Closure Capture", "Verifying local variable closure bounds");
        String prefix = "Alert: "; // Effectively final
        Consumer<String> alertPrinter = msg -> System.out.println(prefix + msg);
        alertPrinter.accept("Authentication Failed!");
        // prefix = "Change"; // Un-commenting this makes compiling fail!

        // 2. Pillars of Functional API & Bi- Variants
        ConsoleFormatter.printStep("Standard Functional Pillars", "Executing Predicate, Function, Consumer, Supplier, and Bi- interfaces");
        
        Predicate<Integer> isEven = x -> x % 2 == 0;
        Function<Integer, String> intToString = x -> "Number: " + x;
        Consumer<String> printData = System.out::println;
        Supplier<Double> randomValue = Math::random;

        BiPredicate<String, Integer> lengthMatches = (str, len) -> str.length() == len;
        BiFunction<Integer, Integer, String> sumString = (a, b) -> "Sum is " + (a + b);
        BiConsumer<String, String> concatPrint = (s1, s2) -> System.out.println(s1 + " " + s2);

        System.out.println("isEven(4): " + isEven.test(4));
        System.out.println("intToString(10): " + intToString.apply(10));
        System.out.print("Consumer output: "); printData.accept("Active");
        System.out.println("Supplier output: " + randomValue.get());
        System.out.println("lengthMatches('Java', 4): " + lengthMatches.test("Java", 4));
        System.out.println("sumString(10, 20): " + sumString.apply(10, 20));
        System.out.print("BiConsumer output: "); concatPrint.accept("Hello", "World");

        // 3. Custom Tri-Functional Interface
        ConsoleFormatter.printStep("Custom Functional Interfaces", "Invoking custom TriFunction interface lambda");
        TriFunction<Integer, Integer, Integer, Integer> sumThree = (a, b, c) -> a + b + c;
        int result = sumThree.apply(10, 20, 30);
        System.out.println("TriFunction sum (10 + 20 + 30): " + result);

        if (result == 60) {
            ConsoleFormatter.printSuccess("Standard and custom functional interfaces executed cleanly.");
        }
    }

    // Custom Functional Interface
    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }
}
