package com.mastery.oop;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "Abstraction (Smart Home device system)",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Abstraction separates operational interfaces from internal implementation details. Abstract classes support shared states and partial structures; interfaces provide pure behavioral contracts containing default, static, and private methods.",
    whyItMatters = "Decoupling systems through interfaces prevents cascading changes. Implementing default and static methods allows adding functions to interfaces without breaking existing implementations.",
    keyPoints = {
        "Abstract classes can define instance state, fields, and constructors; Interfaces cannot hold instance fields.",
        "Interfaces can implement default methods to provide standard fallback logic.",
        "Static interface methods provide utility behaviors bound to the interface context.",
        "Private interface methods allow sharing helper logic between default methods without exposing them to implementing classes."
    },
    interviewQuestions = {
        @Question(
            question = "When should you choose an abstract class over an interface?",
            answer = "Choose an abstract class when subclasses need to share common mutable state (instance fields) or constructors. Choose interfaces to define polymorphic behavior contracts across unrelated class trees, or when multiple inheritance of behavior is needed."
        ),
        @Question(
            question = "Can interfaces define concrete helper methods since Java 9?",
            answer = "Yes. Java 8 introduced default and static methods, and Java 9 added private methods, permitting interfaces to share private utility logic internally."
        )
    },
    pitfalls = {
        "Bloating interfaces with default methods, converting them into pseudo-abstract classes and causing inheritance conflicts (diamond problem).",
        "Defining state inside abstract classes that should belong to subclass implementations."
    }
)
@SuppressWarnings("all")
public class AbstractionSmartHome {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Abstraction (Smart Home Devices)");

        // 1. Interfaces vs Abstract Class instance behavior
        ConsoleFormatter.printStep("Abstract Class Implementation", "Instantiating subclass and checking common state variables");
        SmartDevice light = new SmartLight("Living Room Light");
        System.out.println("Device Name: " + light.getDeviceName());
        System.out.println("Device Active State: " + light.isActive());
        
        light.turnOn();
        System.out.println("Device State after turnOn: " + light.isActive());

        // 2. Default and Private Interface methods in action
        ConsoleFormatter.printStep("Default & Private Methods", "Accessing default capabilities on interface devices");
        Diagnostics diagnosticsDevice = (Diagnostics) light;
        diagnosticsDevice.runSystemDiagnostics(); // Invokes default method

        // 3. Static interface methods
        ConsoleFormatter.printStep("Static Utility Methods", "Invoking static helpers bound to interfaces");
        String version = Diagnostics.getSpecVersion();
        System.out.println("Diagnostics Spec Version: " + version);

        if (light.isActive()) {
            ConsoleFormatter.printSuccess("Abstraction design patterns successfully demonstrated.");
        }
    }

    // Abstract Class - Holds state (deviceName, active) and structural constructor
    public static abstract class SmartDevice {
        private final String deviceName;
        private boolean active;

        protected SmartDevice(String deviceName) {
            this.deviceName = deviceName;
            this.active = false;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public boolean isActive() {
            return active;
        }

        protected void setActive(boolean active) {
            this.active = active;
        }

        // Abstract method declarations
        public abstract void turnOn();
        public abstract void turnOff();
    }

    // Interface defining dynamic capabilities
    public interface Diagnostics {
        // Static interface method
        static String getSpecVersion() {
            return "v4.2.1-LTS";
        }

        // Abstract method contract
        boolean runPingTest();

        // Default interface method
        default void runSystemDiagnostics() {
            logDiagnostic("Initiating diagnostics suite...");
            boolean pingOk = runPingTest();
            if (pingOk) {
                System.out.println("Status: ALL SYSTEMS ONLINE.");
            } else {
                System.out.println("Status: CONNECTION FAIL DETECTED.");
            }
        }

        // Private interface method (shares logic between default methods, introduced in Java 9)
        private void logDiagnostic(String message) {
            System.out.println("[SYSTEM-LOG] " + message);
        }
    }

    // Concrete Subclass inheriting base state and implementing secondary capabilities
    public static class SmartLight extends SmartDevice implements Diagnostics {
        public SmartLight(String name) {
            super(name);
        }

        @Override
        public void turnOn() {
            setActive(true);
            System.out.println(getDeviceName() + " illuminated to 100%.");
        }

        @Override
        public void turnOff() {
            setActive(false);
            System.out.println(getDeviceName() + " light switched off.");
        }

        @Override
        public boolean runPingTest() {
            System.out.println("Pinging local gateway for " + getDeviceName() + "...");
            return true; // Simulate successful test
        }
    }
}
