package com.mastery.oop;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@JavaConcept(
    name = "Composition vs Inheritance",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Inheritance is an 'is-a' relationship that binds a subclass to its superclass. Composition is a 'has-a' relationship where objects assemble functionality by holding references to helper instances.",
    whyItMatters = "Inheritance breaks encapsulation because subclasses depend on the internal implementation details of superclasses. Composition decouples systems, enabling dynamic runtime additions without class modification.",
    keyPoints = {
        "Inheritance exposes subclasses to superclass internal implementations (fragile base class problem).",
        "Classic HashSet double-count bug: overriding addAll() and add() results in double counting when addAll() internally redirects to add().",
        "Composition wraps functionality by holding instances (delegation) instead of extending structures."
    },
    interviewQuestions = {
        @Question(
            question = "Explain Joshua Bloch's classic HashSet inheritance pitfall.",
            answer = "If you inherit from HashSet and override add() and addAll() to count elements, calling addAll() increments the counter and then calls super.addAll(). Inside the JDK implementation, HashSet's addAll() calls add() for each element. This triggers the overridden add() method, causing the counter to double-increment."
        ),
        @Question(
            question = "How does Composition solve the fragile base class problem?",
            answer = "By wrapping the target class as a private field (Composition) and forwarding calls to it (Delegation), you avoid dependencies on the internal execution chains of the target class's methods."
        )
    },
    pitfalls = {
        "Extending classes simply to reuse utility code, violating the 'is-a' architectural rule.",
        "Experiencing tight coupling where updating a superclass method signature breaks multiple subclasses."
    }
)
@SuppressWarnings("all")
public class CompositionVsInheritance {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Composition vs Inheritance");

        // 1. Inheritance Pitfall: Double Count Bug
        ConsoleFormatter.printStep("Inheritance Pitfall", "Demonstrating double-count bug in a sub-classed HashSet");
        InstrumentedHashSet<String> badSet = new InstrumentedHashSet<>();
        badSet.addAll(List.of("One", "Two", "Three"));
        
        System.out.println("Items added (expected 3): " + badSet.getAddCount());
        if (badSet.getAddCount() != 3) {
            ConsoleFormatter.printWarning("BUG: Added 3 items but count is " + badSet.getAddCount() + "! This is because super.addAll() calls add() internally.");
        }

        // 2. Composition / Delegation Solution
        ConsoleFormatter.printStep("Composition Solution", "Validating correct count using Composition & Delegation");
        InstrumentedSetComposition<String> goodSet = new InstrumentedSetComposition<>(new HashSet<>());
        goodSet.addAll(List.of("One", "Two", "Three"));
        System.out.println("Items added (expected 3): " + goodSet.getAddCount());
        
        if (goodSet.getAddCount() == 3) {
            ConsoleFormatter.printSuccess("Composition isolated the counter logic, producing the correct count!");
        }

        // 3. Real-world Composition: Notification Service
        ConsoleFormatter.printStep("Notification Composition System", "Dispatching alert across multiple channels using Composition");
        NotificationChannel email = new EmailChannel();
        NotificationChannel sms = new SmsChannel();

        // Assemble service dynamically via composition
        NotificationService service = new NotificationService(List.of(email, sms));
        service.broadcastAlert("Security Alert: Unauthorized access attempt detected!");
        ConsoleFormatter.printSuccess("Notification broad-casted through assembled components.");
    }

    // --- 1. Broken Inheritance Class ---
    public static class InstrumentedHashSet<E> extends HashSet<E> {
        private int addCount = 0;

        @Override
        public boolean add(E e) {
            addCount++;
            return super.add(e);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            addCount += c.size(); // Increments count
            return super.addAll(c); // Internally calls add() on each element, double counting!
        }

        public int getAddCount() {
            return addCount;
        }
    }

    // --- 2. Correct Composition Class (Forwarding Set) ---
    public static class InstrumentedSetComposition<E> {
        private final HashSet<E> set; // Composition
        private int addCount = 0;

        public InstrumentedSetComposition(HashSet<E> set) {
            this.set = set;
        }

        public boolean add(E e) {
            addCount++;
            return set.add(e);
        }

        public boolean addAll(Collection<? extends E> c) {
            addCount += c.size();
            return set.addAll(c); // safe! set.addAll does not call back into this wrapper's add method.
        }

        public int getAddCount() {
            return addCount;
        }
    }

    // --- 3. Dynamic Composition: Notification Components ---
    public interface NotificationChannel {
        void send(String message);
    }

    public static class EmailChannel implements NotificationChannel {
        @Override
        public void send(String message) {
            System.out.println("[EMAIL-GATEWAY] Sending Email: " + message);
        }
    }

    public static class SmsChannel implements NotificationChannel {
        @Override
        public void send(String message) {
            System.out.println("[SMS-GATEWAY] Sending SMS text: " + message);
        }
    }

    // Notification service relies on composition to broadcast
    public static class NotificationService {
        private final List<NotificationChannel> channels; // Has-a composition

        public NotificationService(List<NotificationChannel> channels) {
            this.channels = channels;
        }

        public void broadcastAlert(String alert) {
            System.out.println("Processing notification broadcast...");
            for (NotificationChannel channel : channels) {
                channel.send(alert); // Delegation!
            }
        }
    }
}
