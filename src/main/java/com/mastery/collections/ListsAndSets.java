package com.mastery.collections;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.*;

@JavaConcept(
    name = "Lists and Sets",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Lists represent ordered collections (permitting duplicates). Sets store unique elements. ArrayList is array-backed and fast for index reads; LinkedList is node-backed. HashSet uses hash keys; TreeSet is red-black tree-backed and sorts elements.",
    whyItMatters = "Choosing the wrong collection class causes performance bottlenecks. For example, search in List takes O(N), whereas search in HashSet is O(1). TreeSet requires elements to be Comparable, determining duplicates using compareTo() rather than equals().",
    keyPoints = {
        "ArrayList index lookup takes O(1) time; LinkedList index lookup takes O(N) time.",
        "HashSet relies on hashCode() and equals() to determine uniqueness.",
        "TreeSet relies entirely on compareTo() or a Comparator. If compareTo() returns 0, the item is considered a duplicate and is rejected, regardless of equals()."
    },
    interviewQuestions = {
        @Question(
            question = "What is the difference between ArrayList and LinkedList?",
            answer = "ArrayList is backed by a dynamically resizing array, offering O(1) random access reads but O(N) for insertions/deletions in the middle. LinkedList consists of double-linked node elements, offering O(1) insertion/deletion at endpoints but O(N) random access search."
        ),
        @Question(
            question = "How does TreeSet determine duplicate elements differently than HashSet?",
            answer = "HashSet uses hash codes and equals() comparisons. TreeSet ignores equals() and hashCode(), comparing elements using compareTo() (or a Comparator). If compareTo() yields 0, the element is rejected as a duplicate."
        )
    },
    pitfalls = {
        "Adding objects that do not override equals() and hashCode() to a HashSet, resulting in duplicate entries.",
        "Adding objects that do not implement Comparable to a TreeSet, throwing a ClassCastException at runtime."
    }
)
@SuppressWarnings("all")
public class ListsAndSets {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Lists & Sets Performance");

        // 1. ArrayList vs LinkedList performance
        ConsoleFormatter.printStep("Lists Lookup Speed", "Comparing random access read time (O(1) vs O(N))");
        int size = 50000;
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }

        // ArrayList Random Read
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            arrayList.get(size / 2);
        }
        long durationArray = System.nanoTime() - start;

        // LinkedList Random Read
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            linkedList.get(size / 2);
        }
        long durationLinked = System.nanoTime() - start;

        System.out.println("ArrayList 1000 reads: " + (durationArray / 1000) + " microseconds");
        System.out.println("LinkedList 1000 reads: " + (durationLinked / 1000) + " microseconds");
        
        if (durationArray < durationLinked) {
            ConsoleFormatter.printSuccess("ArrayList random access is significantly faster due to direct memory indexing!");
        }

        // 2. HashSet vs TreeSet: The compareTo/equals gotcha
        ConsoleFormatter.printStep("Set Duplication Rules", "Comparing how HashSet and TreeSet treat custom items with mismatched equals/compareTo");
        
        // Item where equals says yes but compareTo says no (or vice versa)
        InconsistentItem item1 = new InconsistentItem(1, "Apple");
        InconsistentItem item2 = new InconsistentItem(2, "Apple"); // equals returns true based on name, but compareTo returns non-zero based on id

        // HashSet (uses equals/hashCode)
        Set<InconsistentItem> hashSet = new HashSet<>();
        hashSet.add(item1);
        hashSet.add(item2);
        System.out.println("HashSet size (checks equals based on name -> expected 1): " + hashSet.size());

        // TreeSet (uses compareTo)
        Set<InconsistentItem> treeSet = new TreeSet<>();
        treeSet.add(item1);
        treeSet.add(item2);
        System.out.println("TreeSet size (checks compareTo based on id -> expected 2): " + treeSet.size());

        if (hashSet.size() == 1 && treeSet.size() == 2) {
            ConsoleFormatter.printWarning("TreeSet and HashSet sizes differ! TreeSet treats elements as unique because compareTo() returned non-zero, violating equals() consistency.");
        }
        ConsoleFormatter.printSuccess("Lists and Sets checks completed.");
    }

    // Class with equals inconsistent with compareTo
    static class InconsistentItem implements Comparable<InconsistentItem> {
        final int id;
        final String name;

        InconsistentItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InconsistentItem that = (InconsistentItem) o;
            return Objects.equals(name, that.name); // Equals checks NAME
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public int compareTo(InconsistentItem o) {
            return Integer.compare(this.id, o.id); // CompareTo checks ID
        }

        @Override
        public String toString() {
            return "Item{" + id + ", '" + name + "'}";
        }
    }
}
