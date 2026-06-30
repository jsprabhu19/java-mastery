package com.mastery.basics;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.Arrays;

@JavaConcept(
    name = "Array Management and Cloning Semantics",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Arrays are fixed-size sequential blocks of memory containing primitives or objects. Cloning an array creates a shallow copy, meaning the array structure is duplicated, but references inside it point to the same memory addresses.",
    whyItMatters = "Confusing shallow cloning with deep copying leads to unintended data alterations in multi-layered structures, where altering elements in a copied array corrupts data in the original array.",
    keyPoints = {
        "Array clone() or Arrays.copyOf() yields a shallow copy (references are shared).",
        "System.arraycopy is a highly optimized native method for partial array copying.",
        "Deep copying requires instantiating new objects for each element, often implemented via a Copy Constructor."
    },
    interviewQuestions = {
        @Question(
            question = "Does calling clone() on an Object array double-copy the objects?",
            answer = "No, array.clone() performs a shallow copy. It allocates a new array object and copies references. Both original and cloned array elements point to the same objects in memory."
        ),
        @Question(
            question = "What is the fastest way to copy elements from one array to another in Java?",
            answer = "System.arraycopy() is the fastest because it is a low-level native JVM implementation that copies memory chunks directly."
        )
    },
    pitfalls = {
        "Modifying elements inside a cloned array and expecting the original array to remain unchanged.",
        "Forgetting to resize arrays manually or not utilizing ArrayList when dynamic resizing is necessary."
    }
)
@SuppressWarnings("all")
public class ArrayBasicsAndCloning {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Array Management & Cloning Semantics");

        // 1. Array Copies via System.arraycopy
        ConsoleFormatter.printStep("Native Array Copies", "Copying segment using System.arraycopy");
        int[] source = {10, 20, 30, 40, 50};
        int[] target = new int[5];
        System.arraycopy(source, 1, target, 2, 3); // Copy [20, 30, 40] into target starting index 2
        System.out.println("Source: " + Arrays.toString(source));
        System.out.println("Target: " + Arrays.toString(target));
        ConsoleFormatter.printSuccess("System.arraycopy completed.");

        // 2. Shallow Copy Demonstration
        ConsoleFormatter.printStep("Shallow Copy Validation", "Cloning an array of mutable objects");
        User[] originalUsers = { new User("Alice"), new User("Bob") };
        User[] clonedUsers = originalUsers.clone(); // Shallow clone

        System.out.println("originalUsers == clonedUsers: " + (originalUsers == clonedUsers)); // False (different arrays)
        System.out.println("originalUsers[0] == clonedUsers[0]: " + (originalUsers[0] == clonedUsers[0])); // True (shared reference!)

        // Modify object in clone
        clonedUsers[0].name = "Charlie";
        System.out.println("Modified clone index 0. Original index 0 name: " + originalUsers[0].name);
        
        if ("Charlie".equals(originalUsers[0].name)) {
            ConsoleFormatter.printWarning("ALERT: Original object altered because cloning was shallow! Reference was shared.");
        }

        // 3. Deep Copy with Copy Constructor
        ConsoleFormatter.printStep("Deep Copy Implementation", "Copying elements using a copy constructor");
        User[] deepCopiedUsers = new User[originalUsers.length];
        for (int i = 0; i < originalUsers.length; i++) {
            deepCopiedUsers[i] = new User(originalUsers[i]); // Invokes copy constructor
        }

        System.out.println("originalUsers[0] == deepCopiedUsers[0]: " + (originalUsers[0] == deepCopiedUsers[0])); // False
        
        // Modify deep copy
        deepCopiedUsers[0].name = "David";
        System.out.println("Modified deep copy index 0 to 'David'. Original name: " + originalUsers[0].name);
        if (!"David".equals(originalUsers[0].name)) {
            ConsoleFormatter.printSuccess("Success: Original remains '" + originalUsers[0].name + "'. Deep copy isolated elements successfully.");
        }
    }

    static class User {
        String name;

        User(String name) {
            this.name = name;
        }

        // Copy constructor
        User(User other) {
            this.name = other.name;
        }

        @Override
        public String toString() {
            return "User{name='" + name + "'}";
        }
    }
}
