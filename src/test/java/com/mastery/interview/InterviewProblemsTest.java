package com.mastery.interview;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InterviewProblemsTest {

    @Test
    public void testLruCachePruning() {
        LruCache.CustomLruCache cache = new LruCache.CustomLruCache(2);
        cache.put(1, 10);
        cache.put(2, 20);
        
        assertEquals(10, cache.get(1)); // key 1 is now most recently used
        cache.put(3, 30); // evicts key 2 (least recently used)

        assertEquals(-1, cache.get(2)); // Evicted
        assertEquals(10, cache.get(1));
        assertEquals(30, cache.get(3));
    }

    @Test
    public void testLinkedListReversalAndCycle() {
        CustomLinkedList.Node head = new CustomLinkedList.Node(1);
        head.next = new CustomLinkedList.Node(2);
        head.next.next = new CustomLinkedList.Node(3);

        // Test reversal
        CustomLinkedList.Node reversed = reverseList(head);
        assertNotNull(reversed);
        assertEquals(3, reversed.value);
        assertEquals(2, reversed.next.value);
        assertEquals(1, reversed.next.next.value);
        assertNull(reversed.next.next.next);

        // Test cycle detection
        CustomLinkedList.Node cycleHead = new CustomLinkedList.Node(10);
        CustomLinkedList.Node node2 = new CustomLinkedList.Node(20);
        cycleHead.next = node2;
        node2.next = cycleHead; // Cycle!
        assertTrue(hasCycle(cycleHead));
        assertFalse(hasCycle(reversed));
    }

    private static CustomLinkedList.Node reverseList(CustomLinkedList.Node head) {
        CustomLinkedList.Node prev = null;
        CustomLinkedList.Node current = head;
        while (current != null) {
            CustomLinkedList.Node nextTemp = current.next;
            current.next = prev;
            prev = current;
            current = nextTemp;
        }
        return prev;
    }

    private static boolean hasCycle(CustomLinkedList.Node head) {
        if (head == null) return false;
        CustomLinkedList.Node slow = head;
        CustomLinkedList.Node fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }
}
