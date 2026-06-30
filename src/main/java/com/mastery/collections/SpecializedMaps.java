package com.mastery.collections;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;

@JavaConcept(
    name = "Specialized Map Collections (EnumMap, WeakHashMap, IdentityHashMap)",
    difficulty = Difficulty.ADVANCED,
    what = "Java provides specialized Map implementations optimized for specific requirements. EnumMap uses enum ordinals for fast array lookups; WeakHashMap references keys weakly to prevent memory leaks; IdentityHashMap compares keys using reference equality (==).",
    whyItMatters = "Understanding specialized maps allows building highly optimized components. For example, WeakHashMap automatically collects keys when they are dereferenced, preventing caching memory leaks. IdentityHashMap is critical for circular references or graph serialization.",
    keyPoints = {
        "EnumMap is internally backed by an array, resulting in O(1) reads/writes and zero hash collision overhead.",
        "WeakHashMap key references are wrapped in WeakReference. When a key has no other strong reference, the GC will sweep it.",
        "IdentityHashMap uses == for keys, letting keys with duplicate values exist in the same map."
    },
    interviewQuestions = {
        @Question(
            question = "Explain how WeakHashMap key garbage collection works.",
            answer = "WeakHashMap keys are held using WeakReferences. During GC cycles, if no strong references to the key exist, the key object is collected. WeakHashMap registers this with a ReferenceQueue, clearing the corresponding entry value the next time map operations are invoked."
        ),
        @Question(
            question = "Why does IdentityHashMap violate the general Map interface contract?",
            answer = "The standard Map contract specifies key matching using .equals(). IdentityHashMap uses reference equality (==), allowing two separate String objects containing 'key' to represent different map entries."
        )
    },
    pitfalls = {
        "Holding strong references to WeakHashMap keys somewhere else in the application, which blocks the GC from reclaiming them.",
        "Expecting IdentityHashMap to match key values across serializations or standard lookups."
    }
)
@SuppressWarnings("all")
public class SpecializedMaps {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Specialized Maps");

        // 1. EnumMap
        ConsoleFormatter.printStep("EnumMap Performance", "Creating EnumMap mapped to System Tiers");
        Map<SystemState, String> enumMap = new EnumMap<>(SystemState.class);
        enumMap.put(SystemState.STARTING, "System boot init");
        enumMap.put(SystemState.RUNNING, "All modules active");
        System.out.println("EnumMap contents: " + enumMap);
        ConsoleFormatter.printSuccess("EnumMap operations completed. Fast array index mapping utilized.");

        // 2. IdentityHashMap
        ConsoleFormatter.printStep("IdentityHashMap Reference Matching", "Comparing keys using reference equality (==) vs equals()");
        Map<String, String> identityMap = new IdentityHashMap<>();
        
        // Two separate String objects with identical values
        String key1 = new String("key");
        String key2 = new String("key");

        identityMap.put(key1, "Val1");
        identityMap.put(key2, "Val2");

        System.out.println("IdentityMap size (expected 2): " + identityMap.size());
        System.out.println("Key1 value: " + identityMap.get(key1));
        System.out.println("Key2 value: " + identityMap.get(key2));

        if (identityMap.size() == 2) {
            ConsoleFormatter.printSuccess("IdentityHashMap correctly isolated identical value keys based on reference memory addresses!");
        }

        // 3. WeakHashMap Memory Cache
        ConsoleFormatter.printStep("WeakHashMap Lifecycle", "Demonstrating automatic key sweep when strong reference is lost");
        Map<Object, String> weakMap = new WeakHashMap<>();
        
        Object strongRefKey = new Object();
        Object transientKey = new Object(); // We will drop this reference

        weakMap.put(strongRefKey, "Strongly Referenced Cache");
        weakMap.put(transientKey, "Weakly Referenced Cache");

        System.out.println("Initial WeakHashMap size: " + weakMap.size());

        // Dereference the transient key
        transientKey = null;

        // Force GC to run
        System.out.println("Requesting Garbage Collection...");
        System.gc();

        // Give GC a brief moment to run
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {}

        // Triggering any map method causes WeakHashMap to purge reference queue items
        System.out.println("WeakHashMap size after GC: " + weakMap.size());

        if (weakMap.size() == 1) {
            ConsoleFormatter.printSuccess("WeakHashMap successfully garbage collected and pruned the dereferenced key!");
        } else {
            ConsoleFormatter.printWarning("GC did not trigger in time. WeakHashMap size remains " + weakMap.size());
        }
    }

    enum SystemState { STARTING, RUNNING, STOPPED }
}
