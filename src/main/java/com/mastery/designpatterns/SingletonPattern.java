package com.mastery.designpatterns;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.io.*;
import java.lang.reflect.Constructor;

@JavaConcept(
    name = "Singleton Pattern (Breaking and Defending)",
    difficulty = Difficulty.EXPERT,
    what = "Singleton restricts class instantiation to a single object. Advanced implementations include Double-Checked Locking (DCL) and the Bill Pugh Holder idiom. Singletons can be broken using Reflection, Serialization, and Cloning.",
    whyItMatters = "Understanding Singleton weaknesses is a hallmark of expert Java design. Knowing how to write double-checked locks with volatile prevents thread-safety bugs, and implementing readResolve() prevents serialization duplication.",
    keyPoints = {
        "DCL requires the instance field to be volatile to prevent CPU instruction reordering during instantiation.",
        "Bill Pugh Holder pattern utilizes lazy class loading for thread-safe instantiation without sync overhead.",
        "Standard singletons are broken via Reflection, Serialization, and Cloning. Enum Singleton is naturally immune."
    },
    interviewQuestions = {
        @Question(
            question = "Why is the volatile keyword mandatory in Double-Checked Locking?",
            answer = "Without volatile, JIT/CPU can reorder the instance initialization. The instruction steps are: 1. Allocate memory, 2. Construct object, 3. Point reference to memory. If reordered to 1 -> 3 -> 2, Thread B checks instance == null (which is false because reference points to memory) and accesses a half-initialized object before constructor finishes."
        ),
        @Question(
            question = "How do you defend a Singleton class against Serialization duplicate instances?",
            answer = "By implementing the private/protected method 'Object readResolve() { return getInstance(); }'. During deserialization, JVM invokes readResolve() if present, replacing the reconstructed object with the singleton instance."
        )
    },
    pitfalls = {
        "Omitting volatile in Double-Checked Locking, leading to rare, hard-to-reproduce thread corruption.",
        "Implementing Cloneable on Singleton without overriding clone() to throw CloneNotSupportedException."
    }
)
public class SingletonPattern {
    public static void main(String[] args) throws Exception {
        ConsoleFormatter.printHeader("Singleton: Breaking & Defending");

        // 1. Get standard instance
        ConsoleFormatter.printStep("Standard DCL Access", "Fetching standard Double-Checked Lock instance");
        DclSingleton instance1 = DclSingleton.getInstance();
        DclSingleton instance2 = DclSingleton.getInstance();
        System.out.println("instance1 == instance2: " + (instance1 == instance2)); // True

        // 2. Breaking via Reflection
        ConsoleFormatter.printStep("Breaking via Reflection", "Invoking private constructor using reflection");
        Constructor<DclSingleton> constructor = DclSingleton.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            DclSingleton brokenReflectInstance = constructor.newInstance();
            System.out.println("instance1 == brokenReflectInstance: " + (instance1 == brokenReflectInstance));
            if (instance1 != brokenReflectInstance) {
                ConsoleFormatter.printWarning("BUG: Singleton broken by Reflection! We now have two distinct DCL instances.");
            }
        } catch (Exception e) {
            ConsoleFormatter.printSuccess("Reflection construction failed: " + e.getCause().getMessage());
        }

        // 3. Breaking via Serialization
        ConsoleFormatter.printStep("Breaking via Serialization", "Serializing and Deserializing DclSingleton");
        byte[] serializedData;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(instance1);
            serializedData = baos.toByteArray();
        }

        DclSingleton deserializedInstance;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            deserializedInstance = (DclSingleton) ois.readObject();
        }

        System.out.println("instance1 == deserializedInstance: " + (instance1 == deserializedInstance));
        if (instance1 != deserializedInstance) {
            ConsoleFormatter.printWarning("BUG: Singleton broken by Deserialization! New instance allocated.");
        }

        // 4. Defended Singleton check
        ConsoleFormatter.printStep("Defended Singleton Testing", "Testing Reflection & Serialization on DefendedSingleton");
        DefendedSingleton defended1 = DefendedSingleton.getInstance();

        // Try Reflection on Defended Singleton
        Constructor<DefendedSingleton> defConstructor = DefendedSingleton.class.getDeclaredConstructor();
        defConstructor.setAccessible(true);
        try {
            defConstructor.newInstance();
        } catch (Exception e) {
            ConsoleFormatter.printSuccess("DefendedSingleton blocked reflection instantiation! Error: " + e.getCause().getMessage());
        }

        // Try Deserialization on Defended Singleton
        byte[] defSerialized;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(defended1);
            defSerialized = baos.toByteArray();
        }

        DefendedSingleton defDeserialized;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(defSerialized);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            defDeserialized = (DefendedSingleton) ois.readObject();
        }

        System.out.println("defended1 == defDeserialized: " + (defended1 == defDeserialized));
        if (defended1 == defDeserialized) {
            ConsoleFormatter.printSuccess("DefendedSingleton preserved uniqueness during Deserialization (readResolve worked)!");
        }

        // 5. Enum Singleton (Safest representation)
        ConsoleFormatter.printStep("Enum Singleton", "Accessing thread-safe Enum Singleton");
        EnumSingleton enumInstance1 = EnumSingleton.INSTANCE;
        EnumSingleton enumInstance2 = EnumSingleton.INSTANCE;
        System.out.println("enumInstance1 == enumInstance2: " + (enumInstance1 == enumInstance2));
        
        // JVM naturally forbids creating Enum instances via reflection!
        try {
            Constructor<EnumSingleton> enumConstructor = EnumSingleton.class.getDeclaredConstructor(String.class, int.class);
            enumConstructor.setAccessible(true);
            enumConstructor.newInstance("TEST", 0);
        } catch (Exception e) {
            ConsoleFormatter.printSuccess("JVM naturally blocked Enum reflection constructor instantiation: " + e.getMessage());
        }
    }

    // --- 1. Vulnerable Double-Checked Locking Singleton ---
    public static class DclSingleton implements Serializable {
        private static final long serialVersionUID = 1L;
        private static volatile DclSingleton instance;

        private DclSingleton() {
            // No defenses
        }

        public static DclSingleton getInstance() {
            if (instance == null) { // Check 1
                synchronized (DclSingleton.class) {
                    if (instance == null) { // Check 2
                        instance = new DclSingleton();
                    }
                }
            }
            return instance;
        }
    }

    // --- 2. Defended Singleton against Reflection & Serialization & Cloning ---
    public static class DefendedSingleton implements Serializable, Cloneable {
        private static final long serialVersionUID = 1L;

        // Bill Pugh Singleton Holder Idiom
        private static class Holder {
            private static final DefendedSingleton INSTANCE = new DefendedSingleton();
        }

        private DefendedSingleton() {
            // Defense against Reflection
            if (Holder.INSTANCE != null) {
                throw new IllegalStateException("Instance already exists. Reflection construction blocked!");
            }
        }

        public static DefendedSingleton getInstance() {
            return Holder.INSTANCE;
        }

        // Defense against Deserialization
        @Serial
        protected Object readResolve() {
            return getInstance(); // Returns the existing instance!
        }

        // Defense against Cloning
        @Override
        protected Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException("Cloning of this singleton is blocked.");
        }
    }

    // --- 3. Enum Singleton (Immutable / Thread-safe) ---
    public enum EnumSingleton {
        INSTANCE;

        public void performAction() {
            System.out.println("Enum action performed.");
        }
    }
}
