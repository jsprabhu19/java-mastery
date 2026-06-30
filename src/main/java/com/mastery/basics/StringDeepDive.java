package com.mastery.basics;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.StringJoiner;

@JavaConcept(
    name = "String Immutability and Performance",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Strings in Java are immutable sequence wrappers of char arrays. The JVM maintains a special memory area (String Pool) to store unique string values to optimize memory usage.",
    whyItMatters = "Understanding String Pool mechanics avoids redundant memory creation. Using the wrong concatenation technique in loops can cause garbage collector pressure and slow down operations by orders of magnitude.",
    keyPoints = {
        "String literals are stored in the String Pool in the PermGen/Metaspace Heap.",
        "Calling intern() manually attempts to place the string inside the pool, returning the pooled reference.",
        "String is immutable; any modification generates a new String instance.",
        "StringBuilder is non-synchronized and fast; StringBuffer is thread-safe and slower."
    },
    interviewQuestions = {
        @Question(
            question = "How many String objects are created by: String s = new String(\"hello\"); ?",
            answer = "Two objects. One is the literal 'hello' loaded into the String Pool (if not already present), and the second is the explicit String object created in the heap via the 'new' keyword."
        ),
        @Question(
            question = "Why is String immutable in Java?",
            answer = "Immutability allows sharing strings in the String Pool safely without locking, ensures Security (parameters like DB URLs, file paths cannot be altered), and enables cache hashing (improving Map performance)."
        )
    },
    pitfalls = {
        "Concatenating strings inside a loop using the '+' operator. This compiles to repeated StringBuilder creations, yielding O(N^2) complexity.",
        "Comparing string values using '=='. Always use '.equals()'."
    }
)
@SuppressWarnings("all")
public class StringDeepDive {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("String Immutability & Performance");

        // 1. String Pool and Reference Identity
        ConsoleFormatter.printStep("String Pool Verification", "Comparing literal definitions and explicit heap instantiations");
        String s1 = "Java";
        String s2 = "Java";
        String s3 = new String("Java");
        String s4 = s3.intern(); // Fetch reference from String Pool

        System.out.println("s1 == s2 (Literals check): " + (s1 == s2)); // True
        System.out.println("s1 == s3 (Literal vs New Object): " + (s1 == s3)); // False
        System.out.println("s1 == s4 (Literal vs Interned Object): " + (s1 == s4)); // True

        if (s1 == s2 && s1 != s3 && s1 == s4) {
            ConsoleFormatter.printSuccess("String Pool reference sharing validated successfully.");
        }

        // 2. String Concatenation Performance Benchmark
        ConsoleFormatter.printStep("Performance Benchmarking", "Comparing Loop Concatenation: '+' vs StringBuilder");
        int iterations = 10000;

        // Loop Concatenation (+)
        long startTime = System.nanoTime();
        String resultStr = "";
        for (int i = 0; i < iterations; i++) {
            resultStr += "a";
        }
        long endTime = System.nanoTime();
        long durationPlus = (endTime - startTime) / 1000000; // milliseconds
        System.out.println("Loop with '+' completed in: " + durationPlus + " ms");

        // StringBuilder
        startTime = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("a");
        }
        String resultSb = sb.toString();
        endTime = System.nanoTime();
        long durationSb = (endTime - startTime) / 1000000; // milliseconds
        System.out.println("Loop with StringBuilder completed in: " + durationSb + " ms");

        if (durationSb < durationPlus) {
            ConsoleFormatter.printSuccess("StringBuilder is significantly faster (" + durationPlus + "ms vs " + durationSb + "ms)!");
        }

        // 3. StringJoiner Demo
        ConsoleFormatter.printStep("StringJoiner Utility", "Combining items with prefix/suffix separators");
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        joiner.add("Java").add("Python").add("C++");
        System.out.println("Joined String: " + joiner.toString());
        ConsoleFormatter.printSuccess("StringJoiner correctly formatted output.");
    }
}
