package com.mastery.interview;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "Interview: Custom LinkedList Reversal & Cycle Detection",
    difficulty = Difficulty.EXPERT,
    what = "LinkedList traversal is a core coding interview subject. Reversing a singly linked list requires shifting pointer directions iteratively. Cycle detection uses Floyd's Cycle-Finding Algorithm (Tortoise and Hare).",
    whyItMatters = "Understanding linked lists builds structural pointer manipulation competencies, forming base logic for graphs and tree implementations.",
    keyPoints = {
        "Reversing a LinkedList iteratively requires tracking three pointers: previous, current, and next.",
        "Floyd's algorithm uses two pointers moving at different speeds. If a cycle exists, they must eventually meet.",
        "Both solutions run in O(N) time and O(1) space complexity."
    },
    interviewQuestions = {
        @Question(
            question = "Explain how Floyd's cycle detection algorithm works conceptually.",
            answer = "Initialize slow and fast pointers at the head. Move slow by 1 step and fast by 2 steps. If there is no cycle, fast reaches null first. If a cycle exists, the fast pointer wraps around, closing the distance gap by 1 node per iteration until it meets the slow pointer."
        ),
        @Question(
            question = "What is the time and space complexity of iterative linked list reversal?",
            answer = "Time complexity is O(N) since we visit each of the N nodes exactly once. Space complexity is O(1) because we only use a constant number of pointer references (prev, current, next) without allocating heap structures."
        )
    },
    pitfalls = {
        "Losing reference to the rest of the list during reversal by updating current.next before saving next node.",
        "NullPointerExceptions in cycle detection due to not checking fast.next or fast.next.next before moving pointers."
    }
)
@SuppressWarnings("all")
public class CustomLinkedList {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Interview: LinkedList Reversal & Cycles");

        // 1. Reversal
        ConsoleFormatter.printStep("LinkedList Reversal", "Creating list and reversing pointers");
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);

        System.out.println("Original: " + printList(head));
        Node reversed = reverseList(head);
        System.out.println("Reversed: " + printList(reversed));

        if (reversed != null && reversed.value == 4) {
            ConsoleFormatter.printSuccess("LinkedList reversed correctly.");
        }

        // 2. Cycle Detection
        ConsoleFormatter.printStep("Floyd's Cycle Detection", "Testing cycle detection tortoise-and-hare algorithm");
        Node acyclicHead = new Node(10);
        acyclicHead.next = new Node(20);
        acyclicHead.next.next = new Node(30);
        System.out.println("Acyclic list cycle check: " + hasCycle(acyclicHead)); // expected false

        // Create cycle
        Node cyclicHead = new Node(10);
        Node node2 = new Node(20);
        Node node3 = new Node(30);
        cyclicHead.next = node2;
        node2.next = node3;
        node3.next = node2; // Points back to node 2 (Cycle!)
        System.out.println("Cyclic list cycle check: " + hasCycle(cyclicHead)); // expected true

        if (!hasCycle(acyclicHead) && hasCycle(cyclicHead)) {
            ConsoleFormatter.printSuccess("Floyd's Cycle-finding pointers checked out!");
        }
    }

    // Node definition
    static class Node {
        final int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    // Iterative reversal
    private static Node reverseList(Node head) {
        Node prev = null;
        Node current = head;
        while (current != null) {
            Node nextTemp = current.next; // Save next node reference
            current.next = prev;         // Reverse pointer direction
            prev = current;              // Move prev forward
            current = nextTemp;          // Move current forward
        }
        return prev;
    }

    // Floyd's Cycle Detection
    private static boolean hasCycle(Node head) {
        if (head == null) return false;
        Node slow = head;
        Node fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;          // Move 1 step
            fast = fast.next.next;     // Move 2 steps
            
            if (slow == fast) {
                return true; // Met! Cycle found.
            }
        }
        return false; // Reached end of list. No cycle.
    }

    private static String printList(Node head) {
        StringBuilder sb = new StringBuilder();
        Node curr = head;
        while (curr != null) {
            sb.append(curr.value);
            if (curr.next != null) sb.append(" -> ");
            curr = curr.next;
        }
        return sb.toString();
    }
}
