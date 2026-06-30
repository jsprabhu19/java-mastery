package com.mastery.generics;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "Generics Basics and Bounded Types",
    difficulty = Difficulty.BEGINNER,
    what = "Generics enable classes, interfaces, and methods to act on specified data types as parameters, providing compile-time type safety and removing the need for manual type casting.",
    whyItMatters = "Without Generics, collections store raw Objects, meaning runtime ClassCastException errors can occur. Bounded types (e.g., <T extends Number>) restrict the permissible types, enabling invocation of shared superclass methods directly.",
    keyPoints = {
        "Generics enforce strict compile-time type checking, eliminating ClassCastExceptions.",
        "Bounded Type Parameters (<T extends Class & Interface>) restrict compile-time types and allow calling methods defined on the bounds.",
        "Multiple bounds are allowed using the '&' operator (e.g., T extends Number & Runnable), where classes must be declared first."
    },
    interviewQuestions = {
        @Question(
            question = "What are the benefits of using Generics in Java?",
            answer = "Generics offer compile-time type safety (catching errors during compile instead of runtime), eliminate explicit casting boilerplate, and enable writing reusable algorithms that adapt to different type parameters."
        ),
        @Question(
            question = "How do you declare a generic method that accepts only numeric values?",
            answer = "By using a bounded type parameter: public <T extends Number> void process(T number). This constrains the method parameter type T to Number or its subclasses (Integer, Double, etc.)."
        )
    },
    pitfalls = {
        "Using Raw Types (e.g. Box box = new Box()) instead of parameterized types, which completely bypasses compile-time safety protections."
    }
)
@SuppressWarnings("all")
public class GenericBasics {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Generics Basics & Bounded Types");

        // 1. Generic Class
        ConsoleFormatter.printStep("Generic Class Instantiation", "Creating a type-safe generic Box containing String");
        Box<String> stringBox = new Box<>("Java 21");
        String content = stringBox.getContent();
        System.out.println("Box contents: " + content);
        ConsoleFormatter.printSuccess("Verified type-safe retrieval without explicit casting.");

        // 2. Generic Method
        ConsoleFormatter.printStep("Generic Method Execution", "Comparing key-value pairs using generic method parameters");
        Pair<String, Integer> p1 = new Pair<>("Age", 30);
        Pair<String, Integer> p2 = new Pair<>("Age", 30);
        boolean equal = Pair.compare(p1, p2);
        System.out.println("Pair 1 equals Pair 2: " + equal);
        if (equal) {
            ConsoleFormatter.printSuccess("Generic method executed successfully.");
        }

        // 3. Bounded type parameter
        ConsoleFormatter.printStep("Bounded Types", "Executing arithmetic operations inside a numeric-restricted Box");
        NumericBox<Integer> intBox = new NumericBox<>(100);
        NumericBox<Double> doubleBox = new NumericBox<>(55.5);

        System.out.println("Integer Box value: " + intBox.getValue());
        System.out.println("Double Box value: " + doubleBox.getValue());

        double sum = intBox.doubleValue() + doubleBox.doubleValue();
        System.out.println("Sum of bounded boxes: " + sum);

        if (sum == 155.5) {
            ConsoleFormatter.printSuccess("Successfully invoked Number methods (doubleValue()) due to type bounds (<T extends Number>).");
        }
    }

    // Generic Box
    static class Box<T> {
        private T content;

        public Box(T content) {
            this.content = content;
        }

        public T getContent() {
            return content;
        }
    }

    // Generic Pair and static generic comparison method
    static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }

        public static <K, V> boolean compare(Pair<K, V> p1, Pair<K, V> p2) {
            return p1.getKey().equals(p2.getKey()) && p1.getValue().equals(p2.getValue());
        }
    }

    // Bounded type parameter Box
    static class NumericBox<T extends Number> {
        private final T value;

        public NumericBox(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public double doubleValue() {
            return value.doubleValue(); // Allowed because T extends Number
        }
    }
}
