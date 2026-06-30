package com.mastery.interview;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.LinkedList;
import java.util.Queue;

@JavaConcept(
    name = "Interview: Producer-Consumer with wait and notify",
    difficulty = Difficulty.EXPERT,
    what = "The Producer-Consumer pattern coordinates concurrent threads exchanging data. This custom queue uses synchronized monitor blocks with wait() and notifyAll() to suspend and resume threads safely.",
    whyItMatters = "Re-implementing this pattern without concurrent utilities demonstrates mastery of low-level Java synchronization. Specifically, it tests knowing how to prevent thread locking issues and spurious wakeups.",
    keyPoints = {
        "Threads must own the object's monitor (inside synchronized block) before invoking wait() or notifyAll().",
        "wait() releases the monitor lock and suspends the thread, allowing other threads to acquire the lock.",
        "To prevent spurious wakeups, wait() must always be called inside a while loop checking the condition."
    },
    interviewQuestions = {
        @Question(
            question = "Why must wait() be invoked inside a while loop rather than an if statement?",
            answer = "To protect against spurious wakeups (where a thread wakes up without being explicitly notified) and race conditions (where Thread A wakes up but Thread B steals the resource before Thread A re-acquires the lock). Checking the condition inside a while loop guarantees the thread re-evaluates the condition before continuing."
        ),
        @Question(
            question = "What is the difference between notify() and notifyAll()?",
            answer = "notify() wakes up a single random thread waiting on the object's monitor. notifyAll() wakes up all waiting threads. notifyAll() is safer because notify() can wake up a thread that cannot make progress (e.g., another producer when the queue is full), causing the application to hang (deadlock)."
        )
    },
    pitfalls = {
        "Invoking wait() outside a synchronized block, throwing an IllegalMonitorStateException at runtime.",
        "Using notify() instead of notifyAll() in multi-thread systems, resulting in lost signals and thread hangs."
    }
)
public class ProducerConsumer {
    public static void main(String[] args) throws InterruptedException {
        ConsoleFormatter.printHeader("Interview: Producer-Consumer wait/notify");

        // Shared queue buffer with capacity 2
        CustomQueue<Integer> buffer = new CustomQueue<>(2);

        // Producer Thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.put(i);
                    Thread.sleep(100); // Simulate production time
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Consumer Thread
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.take();
                    Thread.sleep(200); // Simulate slower consumption time
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        ConsoleFormatter.printSuccess("Producer-Consumer wait/notify pipeline finished execution.");
    }

    // Custom Queue using monitor locks
    static class CustomQueue<E> {
        private final Queue<E> queue = new LinkedList<>();
        private final int limit;

        public CustomQueue(int limit) {
            this.limit = limit;
        }

        // Add item - blocks if queue is full
        public synchronized void put(E item) throws InterruptedException {
            // Must use WHILE loop, not IF!
            while (queue.size() == limit) {
                System.out.println("  [QUEUE FULL] Producer is waiting...");
                wait(); // Releases lock, suspends thread
            }
            
            queue.add(item);
            System.out.println("  [PRODUCER] Produced item: " + item + " | Queue size: " + queue.size());
            
            notifyAll(); // Wake up consumers waiting on take()
        }

        // Retrieve item - blocks if queue is empty
        public synchronized E take() throws InterruptedException {
            // Must use WHILE loop, not IF!
            while (queue.isEmpty()) {
                System.out.println("  [QUEUE EMPTY] Consumer is waiting...");
                wait(); // Releases lock, suspends thread
            }
            
            E item = queue.poll();
            System.out.println("  [CONSUMER] Consumed item: " + item + " | Queue size: " + queue.size());
            
            notifyAll(); // Wake up producers waiting on put()
            return item;
        }
    }
}
