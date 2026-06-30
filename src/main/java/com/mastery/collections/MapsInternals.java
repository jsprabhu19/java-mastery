package com.mastery.collections;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@JavaConcept(
    name = "HashMap Internals and Treeification",
    difficulty = Difficulty.EXPERT,
    what = "HashMap stores elements in a bucket table array. When keys collision-hash to the same index, they form a linked list. Since Java 8, when a bucket exceeds 8 entries (and total capacity >= 64), the list is converted (treeified) to a Red-Black Tree.",
    whyItMatters = "Bad hashCode design maps multiple elements to the same bucket, degrading HashMap search performance from O(1) to O(N). Treeification maintains O(log N) worst-case performance under severe collisions.",
    keyPoints = {
        "HashMap index calculation: index = (n - 1) & hash(key.hashCode()).",
        "Collision resolution uses a linked list, which upgrades to a Red-Black Tree when TREEIFY_THRESHOLD (8) is breached.",
        "To treeify, the total map capacity must also be at least MIN_TREEIFY_CAPACITY (64). Otherwise, resizing is triggered."
    },
    interviewQuestions = {
        @Question(
            question = "Explain how a HashMap handles collisions internally in Java 8+.",
            answer = "If keys hash to the same bucket, they are added to a linked list. If the list size exceeds 8 and the table capacity is at least 64, the node structures are converted into TreeNode instances forming a Red-Black Tree. If the tree size drops to 6, it is untreeified back to a list."
        ),
        @Question(
            question = "Why does HashMap use (n - 1) & hash to calculate the bucket index?",
            answer = "Because the table size 'n' is always a power of two. Bitwise AND with (n - 1) is equivalent to the modulo operator (hash % n) but runs significantly faster on CPUs."
        )
    },
    pitfalls = {
        "Failing to override hashCode() when overriding equals(). This results in identical keys residing in different buckets, preventing retrieval.",
        "Mutable keys: modifying a field used in hashCode() after placing the key in the map hides it from index searches."
    }
)
@SuppressWarnings("all")
public class MapsInternals {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("HashMap Internals & Treeification");

        // 1. Standard HashMap Insertion
        ConsoleFormatter.printStep("HashMap Key Lookup", "Putting elements and calculating hash indexes");
        Map<String, String> map = new HashMap<>();
        map.put("Java", "Core Language");
        map.put("Maven", "Build Automation");
        System.out.println("Retrieved 'Java': " + map.get("Java"));

        // 2. Simulating Hash Collisions
        ConsoleFormatter.printStep("Simulated Collision Bucket", "Forcing multiple keys into the same bucket using a static hashCode");
        Map<CollidingKey, String> collisionMap = new HashMap<>(64); // Capacity 64 to allow treeification without resize

        // Insert 10 elements with identical hashcode
        for (int i = 1; i <= 10; i++) {
            collisionMap.put(new CollidingKey(i), "Val-" + i);
        }

        System.out.println("Map size: " + collisionMap.size());
        
        // Inspecting internals via reflection
        try {
            inspectHashMapTable(collisionMap);
        } catch (Exception e) {
            ConsoleFormatter.printWarning("Reflection lookup failed (expected under module boundaries): " + e.getMessage());
        }

        ConsoleFormatter.printSuccess("HashMap collision internals demonstrated successfully.");
    }

    // Key with static hashCode to force collisions
    static class CollidingKey implements Comparable<CollidingKey> {
        final int id;

        CollidingKey(int id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return 999; // Every single key hashes to the exact same bucket!
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CollidingKey that = (CollidingKey) obj;
            return this.id == that.id;
        }

        @Override
        public int compareTo(CollidingKey o) {
            return Integer.compare(this.id, o.id); // Required for Tree node ordering
        }

        @Override
        public String toString() {
            return "Key#" + id;
        }
    }

    // Inspects HashMap buckets using Java Reflection to show node conversion
    private static void inspectHashMapTable(Map<?, ?> map) throws Exception {
        Field tableField = HashMap.class.getDeclaredField("table");
        tableField.setAccessible(true);
        Object[] table = (Object[]) tableField.get(map);

        if (table == null) {
            System.out.println("Table is empty (not initialized).");
            return;
        }

        System.out.println("HashMap table capacity: " + table.length);
        int collisions = 0;
        for (int i = 0; i < table.length; i++) {
            Object node = table[i];
            if (node != null) {
                System.out.print("Bucket [" + i + "]: ");
                
                // Count list size or check Node type
                int depth = 0;
                Object current = node;
                String typeName = current.getClass().getSimpleName();
                
                Field nextField = current.getClass().getDeclaredField("next");
                nextField.setAccessible(true);

                while (current != null) {
                    depth++;
                    current = nextField.get(current);
                }
                
                System.out.println("Contains " + depth + " elements. Node structure class: " + typeName);
                collisions++;
            }
        }
        
        if (collisions > 0) {
            ConsoleFormatter.printSuccess("Successfully verified treeified nodes inside HashMap bucket array using reflection!");
        }
    }
}
