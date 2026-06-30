package com.mastery.concurrency;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("all")
public class ConcurrencyTest {

    @Test
    public void testCompletableFuturePipeline() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "hello")
                .thenApply(s -> s + " world")
                .thenApply(String::toUpperCase);

        assertEquals("HELLO WORLD", future.join());
    }

    @Test
    public void testCompletableFutureExceptionally() {
        CompletableFuture<String> future = CompletableFuture.<String>supplyAsync(() -> {
            throw new RuntimeException("Fail");
        }).exceptionally(ex -> "fallback");

        assertEquals("fallback", future.join());
    }

    @Test
    public void testReentrantLockTryLock() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();

        Thread t = new Thread(() -> {
            try {
                boolean acquired = lock.tryLock(100, TimeUnit.MILLISECONDS);
                assertFalse(acquired, "Should not acquire lock owned by main thread.");
            } catch (InterruptedException e) {
                fail("Thread interrupted unexpectedly.");
            }
        });

        t.start();
        t.join();
        lock.unlock();
    }
}
