package com.mastery.basics;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "Wrapper Classes and Autoboxing Cache",
    difficulty = Difficulty.BEGINNER,
    what = "Java automatically converts primitive types to their corresponding object wrapper classes (Autoboxing) and vice versa (Unboxing). Standard wrappers cache frequently used values to save memory.",
    whyItMatters = "Using reference equality (==) on wrapper classes can yield highly confusing results because of this hidden caching mechanism, leading to hard-to-find bugs in production.",
    keyPoints = {
        "Autoboxing occurs when a primitive is assigned to a wrapper class type; Unboxing is the reverse.",
        "Integer, Byte, Short, Character, and Long cache specific ranges. For Integer, it is -128 to 127 by default.",
        "Always use .equals() to compare wrapper objects for value, never use == which compares reference identity."
    },
    interviewQuestions = {
        @Question(
            question = "Why does Integer a = 100; Integer b = 100; System.out.println(a == b); print true, but if the values are 200 it prints false?",
            answer = "Java caches Integer objects for values between -128 and 127. Values of 100 point to the same cached instance in memory. 200 exceeds this cache limit, so JVM instantiates two separate object instances, making the == reference comparison false."
        ),
        @Question(
            question = "How can you modify the upper limit of the Integer cache?",
            answer = "You can customize the JVM parameter -XX:AutoBoxCacheMax=<size> at startup."
        )
    },
    pitfalls = {
        "Comparing wrapper references with ==. Always check values with .equals().",
        "NullPointerException during unboxing when assigning a null wrapper variable to a primitive variable."
    }
)
@SuppressWarnings("all")
public class WrapperCacheAndAutoboxing {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Wrapper Classes & Autoboxing Cache");

        // 1. Autoboxing & Unboxing Demo
        ConsoleFormatter.printStep("Autoboxing", "Converting primitive 'int' 42 to 'Integer'");
        Integer boxed = 42; // Autoboxing
        int unboxed = boxed; // Unboxing
        ConsoleFormatter.printSuccess("Primitives auto-converted cleanly. Primitive = 42, Object = " + boxed);

        // 2. Cache Demonstration
        ConsoleFormatter.printStep("Inside Cache Limit (-128 to 127)", "Comparing Integer references containing 100");
        Integer num1 = 100;
        Integer num2 = 100;
        
        System.out.println("num1 == num2: " + (num1 == num2)); // True (cached)
        System.out.println("num1.equals(num2): " + num1.equals(num2)); // True

        if (num1 == num2) {
            ConsoleFormatter.printSuccess("num1 == num2 matches because 100 is cached.");
        }

        ConsoleFormatter.printStep("Outside Cache Limit", "Comparing Integer references containing 200");
        Integer num3 = 200;
        Integer num4 = 200;

        System.out.println("num3 == num4: " + (num3 == num4)); // False (not cached)
        System.out.println("num3.equals(num4): " + num3.equals(num4)); // True

        if (num3 != num4) {
            ConsoleFormatter.printWarning("num3 == num4 is FALSE even though both contain 200! This is because they are separate objects.");
        }

        // 3. NullPointerException Trap
        ConsoleFormatter.printStep("NPE Hazard during Unboxing", "Assigning null Integer wrapper to primitive double");
        Integer nullInteger = null;
        try {
            int val = nullInteger; // Implicitly invokes nullInteger.intValue()
            System.out.println("Value is: " + val); // Will not reach here
        } catch (NullPointerException npe) {
            ConsoleFormatter.printError("Caught NullPointerException as expected during unboxing of null reference!", npe);
        }
    }
}
