package com.mastery.collections;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.*;

@JavaConcept(
    name = "Collections Utility Algorithms and Immutability",
    difficulty = Difficulty.INTERMEDIATE,
    what = "The Collections utility class provides algorithms for sorting, searching, shuffling, and wrapping collections. Modern Java supports truly immutable collections via List.of() vs unmodifiable wrappers.",
    whyItMatters = "Confusing unmodifiable views with immutable collections allows security bypasses. An unmodifiable view updates if the underlying list changes, whereas List.of() creates a permanent, thread-safe, read-only copy.",
    keyPoints = {
        "Binary search (Collections.binarySearch) requires the list to be pre-sorted, or output indexes are unpredictable.",
        "Collections.unmodifiableList() returns a wrapper. If the original list is mutated, the wrapper view reflects those changes.",
        "List.of(), Set.of(), and Map.of() return truly immutable collections that cannot be altered and reject null elements."
    },
    interviewQuestions = {
        @Question(
            question = "What is the difference between Collections.unmodifiableList(list) and List.copyOf(list)?",
            answer = "Collections.unmodifiableList(list) returns a read-only view wrapper pointing to the original list. If the original list changes, the view changes. List.copyOf(list) creates a completely separate, independent immutable copy. Changes to the original list have no effect on the copy."
        ),
        @Question(
            question = "How does List.of() handle null elements?",
            answer = "List.of() explicitly forbids null values, throwing a NullPointerException if a null element is supplied. Standard ArrayList permits null values."
        )
    },
    pitfalls = {
        "Running binarySearch() on an unsorted collection and expecting accurate element indexes.",
        "Passing a backing list to a class constructor, wrapping it in Collections.unmodifiableList(), and mutating the backing list later, exposing variables to unexpected changes."
    }
)
@SuppressWarnings("all")
public class CollectionsAlgorithms {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Collections Algorithms & Immutability");

        // 1. Binary Search sorting requirement
        ConsoleFormatter.printStep("Binary Search Contract", "Demonstrating requirement for sorted lists");
        List<String> list = new ArrayList<>(List.of("Zebra", "Apple", "Mango", "Banana"));
        
        // Unsorted search
        int unsortedIndex = Collections.binarySearch(list, "Banana");
        System.out.println("Search 'Banana' (unsorted list): " + unsortedIndex); // May be negative/wrong

        // Sorted search
        Collections.sort(list);
        System.out.println("Sorted list: " + list);
        int sortedIndex = Collections.binarySearch(list, "Banana");
        System.out.println("Search 'Banana' (sorted list): " + sortedIndex); // Correct index

        if (sortedIndex >= 0) {
            ConsoleFormatter.printSuccess("Binary search succeeded after sorting!");
        }

        // 2. Unmodifiable wrappers vs Immutable copies
        ConsoleFormatter.printStep("Unmodifiable Views vs Immutable Copies", "Comparing modification trends of views vs copies");
        List<String> original = new ArrayList<>();
        original.add("Item 1");

        // Unmodifiable view
        List<String> unmodifiableView = Collections.unmodifiableList(original);
        // Immutable copy (Java 10+)
        List<String> immutableCopy = List.copyOf(original);

        // Mutate original list
        original.add("Item 2");

        System.out.println("Original list: " + original);
        System.out.println("Unmodifiable view (updated!): " + unmodifiableView);
        System.out.println("Immutable copy (isolated!): " + immutableCopy);

        try {
            unmodifiableView.add("Item 3"); // Throws UnsupportedOperationException
        } catch (UnsupportedOperationException uoe) {
            ConsoleFormatter.printWarning("Caught UnsupportedOperationException trying to modify the unmodifiable view!");
        }

        try {
            List<String> nullSet = List.of("Item", null); // Throws NPE
        } catch (NullPointerException npe) {
            ConsoleFormatter.printWarning("Caught NullPointerException trying to add null to List.of()!");
        }

        if (unmodifiableView.size() == 2 && immutableCopy.size() == 1) {
            ConsoleFormatter.printSuccess("Immutability definitions verified successfully.");
        }
    }
}
