package com.mastery.concurrency;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@JavaConcept(
    name = "Synchronization and Locks",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Synchronization coordinates concurrent resource access. Intrinsic locks (synchronized) are easy to use; explicit locks (ReentrantLock) provide advanced control like tryLock() and timeouts. ReadWriteLock allows multiple readers concurrently while isolating writers.",
    whyItMatters = "Using synchronized blocks blocks threads indefinitely until the lock is acquired, risking deadlocks. tryLock() allows attempting a lock acquisition with a timeout, enabling clean fallback routes.",
    keyPoints = {
        "synchronized is reentrant and automatically releases the lock when leaving the block scope.",
        "ReentrantLock allows non-blocking lock attempts (tryLock()) and timed acquisitions.",
        "ReentrantReadWriteLock increases throughput by separating read locks (shared) from write locks (exclusive)."
    },
    interviewQuestions = {
        @Question(
            question = "What is a Reentrant lock?",
            answer = "A lock is reentrant if a thread that already holds the lock can acquire it again without blocking. Intrinsic synchronized locks and ReentrantLock are both reentrant, tracking hold counts internally."
        ),
        @Question(
            question = "What advantages does ReentrantLock have over synchronized?",
            answer = "ReentrantLock supports tryLock() (non-blocking lock attempts), lockInterruptibly() (allowing lock attempts to be cancelled via interruption), fairness settings (preventing thread starvation), and timed lock acquisitions."
        )
    },
    pitfalls = {
        "Forgetting to release ReentrantLocks in a finally block: lock.lock(); try { ... } finally { lock.unlock(); } (causes thread hangs).",
        "Writing write-heavy operations under ReadWriteLock, causing reader thread starvation."
    }
)
public class SynchronizationAndLocks {
    
    private static int sharedCounter = 0;
    private static final ReentrantLock lock = new ReentrantLock();
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static int readWriteValue = 100;

    public static void main(String[] args) throws InterruptedException {
        ConsoleFormatter.printHeader("Synchronization & Explicit Locks");

        // 1. Thread Race condition and Synchronized block
        ConsoleFormatter.printStep("Thread Race Coordination", "Running parallel threads writing to shared state with synchronized lock");
        Thread t1 = new Thread(SynchronizationAndLocks::incrementCounter);
        Thread t2 = new Thread(SynchronizationAndLocks::incrementCounter);

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("Final Shared Counter (expected 2000): " + sharedCounter);
        
        if (sharedCounter == 2000) {
            ConsoleFormatter.printSuccess("Synchronized block prevented data race conditions successfully.");
        }

        // 2. ReentrantLock tryLock timeouts
        ConsoleFormatter.printStep("ReentrantLock tryLock", "Attempting lock acquisition with timeouts");
        Thread lockHolder = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("  [HOLDER] Lock acquired. Sleeping 1 second...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
                System.out.println("  [HOLDER] Lock released.");
            }
        });

        Thread lockAttempter = new Thread(() -> {
            try {
                System.out.println("  [ATTEMPTER] Attempting lock via tryLock (timeout 200ms)...");
                boolean acquired = lock.tryLock(200, TimeUnit.MILLISECONDS);
                if (acquired) {
                    try {
                        System.out.println("  [ATTEMPTER] Lock acquired!");
                    } finally {
                        lock.unlock();
                    }
                } else {
                    ConsoleFormatter.printWarning("Attempter failed to acquire lock in 200ms. Aborting gracefully.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        lockHolder.start();
        Thread.sleep(50); // let holder start
        lockAttempter.start();

        lockHolder.join();
        lockAttempter.join();

        // 3. ReadWriteLock Demo
        ConsoleFormatter.printStep("ReadWriteLock execution", "Verifying concurrent read operations");
        Thread reader1 = new Thread(SynchronizationAndLocks::readState);
        Thread reader2 = new Thread(SynchronizationAndLocks::readState);
        Thread writer = new Thread(() -> writeState(500));

        reader1.start();
        reader2.start();
        writer.start();

        reader1.join();
        reader2.join();
        writer.join();
        ConsoleFormatter.printSuccess("Read-Write lock operations completed.");
    }

    private static void incrementCounter() {
        for (int i = 0; i < 1000; i++) {
            synchronized (SynchronizationAndLocks.class) { // intrinsic monitor
                sharedCounter++;
            }
        }
    }

    private static void readState() {
        rwLock.readLock().lock(); // Shared read lock
        try {
            System.out.println("  [READER] Value read: " + readWriteValue + " by " + Thread.currentThread().getName());
            Thread.sleep(100); // Simulate reading
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    private static void writeState(int newValue) {
        rwLock.writeLock().lock(); // Exclusive write lock
        try {
            System.out.println("  [WRITER] Writing value: " + newValue);
            readWriteValue = newValue;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
