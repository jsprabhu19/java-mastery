package com.mastery.generics;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.ArrayList;
import java.util.List;

@JavaConcept(name = "Generics Wildcards (PECS - Producer Extends Consumer Super)", difficulty = Difficulty.ADVANCED, what = "Java Generics are invariant, meaning List<Integer> is not a subclass of List<Number>. Wildcards (?) resolve this constraint. PECS dictates: use '? extends T' when reading elements from a structure (Producer), and '? super T' when writing elements to a structure (Consumer).", whyItMatters = "Understanding PECS is essential for designing flexible API methods. Failing to apply wildcards prevents clients from passing collections of subclass types to read-only parameters, making APIs rigid.", keyPoints = {
        "Generics are invariant; arrays are covariant (Integer[] is a subclass of Number[]).",
        "Producer Extends: List<? extends Number> allows reading Numbers, but writes are forbidden.",
        "Consumer Super: List<? super Integer> allows writing Integers, but reads yield only Object references."
}, interviewQuestions = {
        @Question(question = "What is the PECS rule in Java Generics?", answer = "PECS stands for Producer Extends, Consumer Super. Use '? extends T' if your collection produces items (you get items out of it). Use '? super T' if your collection consumes items (you put items into it)."),
        @Question(question = "Why can't you add elements to a List<? extends Number>?", answer = "Because the compiler doesn't know what specific concrete type the list holds. It could be List<Integer>, List<Double>, or List<Float>. To prevent adding a Double into a list of Integers, the compiler blocks all additions (except null).")
}, pitfalls = {
        "Attempting to read from a '? super' collection expecting a specific subclass type instead of Object.",
        "Declaring wildcards in method return types, forcing clients to handle wildcard signatures."
})
@SuppressWarnings("all")
public class GenericsWildcards {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Generics Wildcards (PECS)");

        // 1. Invariance Demo
        ConsoleFormatter.printStep("Invariance Contradiction",
                "Validating that List<Integer> does not assign to List<Number>");
        List<Integer> intList = List.of(1, 2, 3);
        // List<Number> numList = intList; // DOES NOT COMPILE!
        System.out.println(
                "  Verified: List<Integer> cannot be assigned directly to List<Number> reference (Invariance).");

        // 2. Producer Extends (? extends T)
        ConsoleFormatter.printStep("Producer Extends (Read-Only)", "Summing values from List<? extends Number>");
        double sum = sumOfList(intList);
        System.out.println("Sum of list elements: " + sum);

        List<? extends Number> extendsNumber = new ArrayList<>(intList);
        // extendsNumber.add(10); // DOES NOT COMPILE!
        System.out.println("  Verified: Cannot add items to List<? extends Number>.");

        // 3. Consumer Super (? super T)
        ConsoleFormatter.printStep("Consumer Super (Write-Only)", "Adding Integers to List<? super Integer>");
        List<Number> numList = new ArrayList<>();
        addNumbers(numList); // Accept a list of Numbers and add Integers to it
        System.out.println("List elements after addition: " + numList);

        if (numList.size() == 5) {
            ConsoleFormatter.printSuccess("PECS read/write patterns executed successfully.");
        }
    }

    // Producer Extends: Reads items from src. src 'produces' data.
    private static double sumOfList(List<? extends Number> src) {
        double sum = 0.0;
        for (Number num : src) {
            sum += num.doubleValue(); // Reading as Number is allowed
        }
        return sum;
    }

    // Consumer Super: Writes items to dest. dest 'consumes' data.
    private static void addNumbers(List<? super Integer> dest) {
        for (int i = 1; i <= 5; i++) {
            dest.add(i); // Writing Integer is allowed
        }
        // Object item = dest.get(0); // Reading only yields Object references
    }
}
