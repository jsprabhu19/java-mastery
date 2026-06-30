package com.mastery.jvm_internals;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.lang.ref.Cleaner;

@JavaConcept(
    name = "Garbage Collection and Resource Cleanup (Cleaner API)",
    difficulty = Difficulty.ADVANCED,
    what = "Garbage Collection (GC) automatically reclaims memory by destroying unreachable objects on the Heap. Java 9 introduced java.lang.ref.Cleaner to replace deprecated and risky finalize() methods for cleaning resources.",
    whyItMatters = "Relying on deprecated finalize() methods causes GC delays, resource leaks, and security holes. The modern Cleaner API isolates cleanup actions from object instances, executing them safely when target objects are reclaimed.",
    keyPoints = {
        "GC identifies live objects by walking root references (GC Roots) like thread stack frames, static fields, and JNI references.",
        "finalize() is deprecated since Java 9 and completely removed/de-functionalized in recent versions. Use Cleaner or AutoCloseable instead.",
        "Cleaner uses registered runnables that are invoked when the phantom reference to the object is queued by the GC."
    },
    interviewQuestions = {
        @Question(
            question = "Why is finalize() deprecated and considered dangerous in Java?",
            answer = "finalize() is called by an unpredictable JVM thread. If finalize() blocks or throws an exception, the thread hangs or leaks resources. It also allows object resurrection (saving the reference inside finalize()) and delays garbage collection by a generation cycle."
        ),
        @Question(
            question = "Explain how the young generation and old generation separate objects during GC.",
            answer = "Young generation holds short-lived allocations (Eden and Survivor spaces). Minor GC cycles clean this space frequently, migrating surviving objects to survivor blocks. If objects survive multiple threshold generations (tenuring threshold), they are promoted to the Old Generation space (Major GC)."
        )
    },
    pitfalls = {
        "Expecting System.gc() to run instantly and clean everything. System.gc() is only a recommendation to JVM, which can choose to ignore it.",
        "Creating cyclic references that cannot be collected? (No, GC uses reachability algorithms, so cyclic references with no root access are collected easily)."
    }
)
public class GarbageCollectionDemo {

    // Modern replacement for finalize()
    private static final Cleaner cleaner = Cleaner.create();

    public static void main(String[] args) throws InterruptedException {
        ConsoleFormatter.printHeader("Garbage Collection & Cleaner API");

        // 1. Cleaner Registration
        ConsoleFormatter.printStep("Resource Cleaner Registration", "Registering a resource with Cleaner API for callback upon collection");
        
        GcInfoHolder info = new GcInfoHolder();
        registerResource(info);

        System.out.println("  [MAIN] Dropping reference to target object...");
        // Dereference target object
        info = null;

        // 2. Triggering GC recommendation
        ConsoleFormatter.printStep("Garbage Collection Request", "Calling System.gc() to trigger collection sweeps");
        System.gc(); // Request GC sweep

        // Give GC thread a brief moment to run cleaner task
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            if (StateTracker.cleanupInvoked) break;
        }

        if (StateTracker.cleanupInvoked) {
            ConsoleFormatter.printSuccess("Verified: Cleaner task ran successfully after target was swept by GC!");
        } else {
            ConsoleFormatter.printWarning("Cleaner task has not run yet. (Expected behavior since System.gc() is only a hint).");
        }
    }

    private static void registerResource(GcInfoHolder targetObj) {
        // Cleaner action MUST NOT capture a reference to the target object itself,
        // or it will create a memory leak preventing garbage collection!
        // So we run a static nested runnable capturing only primitive fields.
        cleaner.register(targetObj, new CleanupAction());
    }

    // Tracker state
    static class StateTracker {
        static volatile boolean cleanupInvoked = false;
    }

    static class GcInfoHolder {
        // Stub resource
    }

    // Cleanup action runnable (doesn't hold strong references to target class)
    static class CleanupAction implements Runnable {
        @Override
        public void run() {
            System.out.println("  [CLEANER-THREAD] Callback triggered! Reclaiming native descriptors and logging allocations.");
            StateTracker.cleanupInvoked = true;
        }
    }
}
