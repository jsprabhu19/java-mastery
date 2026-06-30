package com.mastery.collections;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@JavaConcept(
    name = "Concurrent Collections Internals",
    difficulty = Difficulty.ADVANCED,
    what = "Java provides highly optimized concurrent collections for multi-threaded access. ConcurrentHashMap uses bucket-level locking and CAS; CopyOnWriteArrayList duplicates the underlying array on updates; ArrayBlockingQueue provides blocking operations for producer-consumer pipelines.",
    whyItMatters = "Synchronizing standard collections via Collections.synchronizedMap() locks the entire structure, bottlenecking threads. Concurrent collections isolate locks to specific nodes or use copy-on-write mechanisms, maximizing throughput.",
    keyPoints = {
        "ConcurrentHashMap uses synchronized on bucket head nodes and CAS for lock-free reads and first-node insertions.",
        "CopyOnWriteArrayList writes to a new copy of the array. It is highly optimized for fast, lock-free reads, but expensive for writes.",
        "BlockingQueue (e.g. ArrayBlockingQueue) blocks producers if full and consumers if empty, removing the need for manual lock coordination."
    },
    interviewQuestions = {
        @Question(
            question = "How does ConcurrentHashMap achieve high concurrency without locking the entire map?",
            answer = "Instead of locking the entire table (like Hashtable), ConcurrentHashMap uses lock-free Compare-And-Swap (CAS) instructions to create new nodes. For modifying existing buckets, it synchronizes only on the head node of the target bucket bin, allowing threads to write to separate buckets simultaneously."
        ),
        @Question(
            question = "When should you choose CopyOnWriteArrayList over synchronized List implementations?",
            answer = "Choose CopyOnWriteArrayList when reads are extremely frequent and updates (adds, updates, deletes) are rare. Readers do not block, avoiding synchronization overhead during collection iterations."
        )
    },
    pitfalls = {
        "Performing compound operations (e.g. check-then-act: if(!map.containsKey(key)) map.put(key, val)) on ConcurrentHashMap without using atomic methods like putIfAbsent().",
        "Using CopyOnWriteArrayList for high-frequency write environments, leading to excessive garbage collection and array-copy CPU overhead."
    }
)
public class ConcurrentCollectionsInternals {
    public static void main(String[] args) throws InterruptedException {
        ConsoleFormatter.printHeader("Concurrent Collections Internals");

        // 1. ConcurrentHashMap Compound Operations
        ConsoleFormatter.printStep("Atomic Compound Operations", "Demonstrating how putIfAbsent() prevents race conditions");
        Map<String, Integer> map = new ConcurrentHashMap<>();
        map.put("Counter", 1);

        // Atomic update check-then-act
        map.putIfAbsent("Counter", 999); // Will fail because "Counter" already exists
        System.out.println("Counter value: " + map.get("Counter"));
        
        if (map.get("Counter") == 1) {
            ConsoleFormatter.printSuccess("Atomic putIfAbsent successfully guarded map state.");
        }

        // 2. CopyOnWriteArrayList Copy Semantics
        ConsoleFormatter.printStep("CopyOnWriteArrayList Copy Semantics", "Demonstrating that iterators view a snapshot of the array");
        List<String> list = new CopyOnWriteArrayList<>();
        list.add("Java");
        list.add("C++");

        Iterator<String> iterator = list.iterator();

        list.add("Python"); // Modify list after iterator creation

        System.out.print("Iterator values (snapshot): ");
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " "); // Reads "Java" "C++", does not throw ConcurrentModificationException!
        }
        System.out.println();
        System.out.println("Actual List values: " + list);

        if (list.size() == 3) {
            ConsoleFormatter.printSuccess("Iterator executed successfully without throwing ConcurrentModificationException!");
        }

        // 3. ArrayBlockingQueue producer-consumer thread demo
        ConsoleFormatter.printStep("ArrayBlockingQueue Concurrency", "Running simple Producer-Consumer threads");
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(2); // Capacity 2

        Thread producer = new Thread(() -> {
            try {
                queue.put("Item 1");
                queue.put("Item 2");
                System.out.println("  [PRODUCER] Queued 2 items. Next put will block...");
                queue.put("Item 3"); // Blocks until consumer takes an item!
                System.out.println("  [PRODUCER] Queued Item 3 successfully after consumer freed slot.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(300); // Wait to trigger blocking
                System.out.println("  [CONSUMER] Taking item: " + queue.take());
                System.out.println("  [CONSUMER] Taking item: " + queue.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
        ConsoleFormatter.printSuccess("Blocking queue operations finished and synchronized automatically.");
    }
}
