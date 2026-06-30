package com.mastery.interview;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@JavaConcept(
    name = "Interview: Least Recently Used (LRU) Cache",
    difficulty = Difficulty.EXPERT,
    what = "An LRU Cache discards the least recently accessed items when its capacity is reached. Implementation is achieved by wrapping LinkedHashMap or by building a custom Doubly-Linked List linked to a HashMap.",
    whyItMatters = "LRU Cache design is a premier system design and algorithm interview problem. It requires achieving O(1) average time complexity for both read (get) and write (put) operations.",
    keyPoints = {
        "LinkedHashMap maintains iteration ordering (insertion or access order), easily adapted into an LRU Cache.",
        "A custom LRU Cache combines a HashMap (for O(1) lookups) with a Doubly-Linked List (for O(1) updates to the access order).",
        "Writing node manipulation operations (addHead, removeNode) correctly avoids pointer corruption bugs."
    },
    interviewQuestions = {
        @Question(
            question = "How does combining a HashMap and a Doubly-Linked List yield O(1) LRU Cache operations?",
            answer = "The HashMap maps keys to Node objects directly, enabling O(1) lookups. The Doubly-Linked List maintains access ordering. When a node is accessed or added, it is unlinked and moved to the head in O(1) pointer updates. If capacity overflows, the node at the tail is unlinked and removed from the HashMap in O(1) time."
        ),
        @Question(
            question = "How do you build a quick LRU Cache using JDK's LinkedHashMap?",
            answer = "By initializing LinkedHashMap with accessOrder=true and overriding removeEldestEntry: 'protected boolean removeEldestEntry(Map.Entry eldest) { return size() > capacity; }'."
        )
    },
    pitfalls = {
        "Forgetting to update both the HashMap and the Doubly-Linked List when inserting or deleting elements, leaving the cache in an inconsistent state.",
        "Missing null-checks at head or tail boundaries in custom linked list operations."
    }
)
@SuppressWarnings("all")
public class LruCache {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Interview: LRU Cache Design");

        // 1. LinkedHashMap based LRU Cache
        ConsoleFormatter.printStep("LinkedHashMap LRU Cache", "Inserting elements and validating access-based pruning");
        LinkedHashMapLru<String, Integer> mapCache = new LinkedHashMapLru<>(2);
        mapCache.put("A", 1);
        mapCache.put("B", 2);
        mapCache.get("A"); // Access "A" (makes B least recently used)
        mapCache.put("C", 3); // Overflows capacity, evicts "B"

        System.out.println("Cache contents: " + mapCache);
        if (mapCache.get("B") == null && mapCache.containsKey("A") && mapCache.containsKey("C")) {
            ConsoleFormatter.printSuccess("LinkedHashMap LRU Cache evicted B correctly.");
        }

        // 2. Custom Doubly-Linked List + HashMap Cache
        ConsoleFormatter.printStep("Custom LRU Cache", "Testing O(1) manual Node pointer cache updates");
        CustomLruCache cache = new CustomLruCache(2);
        cache.put(1, 10);
        cache.put(2, 20);
        System.out.println("Get key 1: " + cache.get(1)); // returns 10, marks 1 as recently used
        cache.put(3, 30); // Evicts key 2

        System.out.println("Get key 2 (evicted): " + cache.get(2)); // returns -1
        System.out.println("Get key 3: " + cache.get(3)); // returns 30

        if (cache.get(2) == -1 && cache.get(1) == 10 && cache.get(3) == 30) {
            ConsoleFormatter.printSuccess("Custom Node LRU Cache logic validated!");
        }
    }

    // --- Approach 1: JDK LinkedHashMap wrapper ---
    static class LinkedHashMapLru<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        public LinkedHashMapLru(int capacity) {
            // true flag enables access-order iteration instead of insertion-order
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity; // Evict eldest if size exceeds capacity limit
        }
    }

    // --- Approach 2: Custom Doubly-Linked List + HashMap ---
    static class CustomLruCache {
        static class Node {
            int key;
            int value;
            Node prev;
            Node next;
            Node(int key, int value) {
                this.key = key;
                this.value = value;
            }
        }

        private final int capacity;
        private final Map<Integer, Node> map;
        private final Node head;
        private final Node tail;

        public CustomLruCache(int capacity) {
            this.capacity = capacity;
            this.map = new HashMap<>();
            
            // Dummy boundary sentinel nodes to avoid boundary check branches
            this.head = new Node(0, 0);
            this.tail = new Node(0, 0);
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            Node node = map.get(key);
            if (node == null) return -1;
            
            // Access updates node priority -> move to head
            remove(node);
            insertHead(node);
            return node.value;
        }

        public void put(int key, int value) {
            Node node = map.get(key);
            if (node != null) {
                // Update existing
                node.value = value;
                remove(node);
                insertHead(node);
            } else {
                // Create new
                if (map.size() >= capacity) {
                    // Evict tail (least recently used)
                    Node lru = tail.prev;
                    remove(lru);
                    map.remove(lru.key);
                    System.out.println("    --> Evicting key " + lru.key + " from Custom Cache.");
                }
                Node newNode = new Node(key, value);
                insertHead(newNode);
                map.put(key, newNode);
            }
        }

        private void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        private void insertHead(Node node) {
            node.next = head.next;
            node.next.prev = node;
            head.next = node;
            node.prev = head;
        }
    }
}
