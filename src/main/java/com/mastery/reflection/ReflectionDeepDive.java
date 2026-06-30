package com.mastery.reflection;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@JavaConcept(
    name = "Reflection API Deep Dive",
    difficulty = Difficulty.ADVANCED,
    what = "Reflection enables program inspection and dynamic invocation of classes, methods, and fields at runtime, bypassing compile-time access controls (like private).",
    whyItMatters = "Reflection forms the backbone of dependency injection (Spring) and ORM (Hibernate) frameworks. It allows framework engines to instantiate classes and inject private dependencies dynamically.",
    keyPoints = {
        "Methods like getDeclaredFields() access all variables regardless of visibility (private/public).",
        "Calling field.setAccessible(true) bypasses Java language access control checks.",
        "Reflection has a runtime performance penalty because JVM optimization is disabled during metadata lookups."
    },
    interviewQuestions = {
        @Question(
            question = "How does Spring Framework use reflection internally to inject dependencies?",
            answer = "Spring reads annotations (e.g., @Autowired) on class fields. It searches the application context for matching bean instances, fetches the target field via reflection, sets field.setAccessible(true), and injects the dependency instance directly into the private field."
        ),
        @Question(
            question = "Can you modify a private final field using Reflection?",
            answer = "Yes. You can fetch the field, call setAccessible(true), and use field.set(object, newValue). However, if the field is static final or initialized in the line declaration, JVM might inline the value at compile-time, meaning subsequent reads still see the old value."
        )
    },
    pitfalls = {
        "SecurityManager exceptions under modern modular Java versions, which restrict setAccessible checks.",
        "Performance issues due to constant dynamic field/method lookups inside high-frequency loops."
    }
)
@SuppressWarnings("all")
public class ReflectionDeepDive {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Reflection API Deep Dive");

        // 1. Dynamic Instantiation
        ConsoleFormatter.printStep("Dynamic Instantiation", "Instantiating an object using private Constructor reflection");
        try {
            Class<?> clazz = SecretUser.class;
            Constructor<?> constructor = clazz.getDeclaredConstructor(String.class);
            constructor.setAccessible(true); // Bypass private constructor!
            
            SecretUser userInstance = (SecretUser) constructor.newInstance("Agent-007");
            System.out.println("Instantiated instance successfully. Class: " + userInstance.getClass().getSimpleName());

            // 2. Private Field Modification
            ConsoleFormatter.printStep("Mutating Private State", "Inspecting and altering private String variable");
            Field secretKeyField = clazz.getDeclaredField("secretKey");
            secretKeyField.setAccessible(true); // Bypass private variable!
            
            String originalVal = (String) secretKeyField.get(userInstance);
            System.out.println("  Original Key: " + originalVal);
            
            // Set new value
            secretKeyField.set(userInstance, "SHIELD-CONFIDENTIAL");
            System.out.println("  Modified Key via Reflection: " + secretKeyField.get(userInstance));

            // 3. Invoking Private Methods
            ConsoleFormatter.printStep("Dynamic Method Invocation", "Invoking private void executeMission() method");
            Method missionMethod = clazz.getDeclaredMethod("executeMission", String.class);
            missionMethod.setAccessible(true); // Bypass private method!
            missionMethod.invoke(userInstance, "Infiltrate target base");

            if ("SHIELD-CONFIDENTIAL".equals(secretKeyField.get(userInstance))) {
                ConsoleFormatter.printSuccess("Private fields and methods manipulated successfully!");
            }

        } catch (Exception e) {
            ConsoleFormatter.printError("Reflection operations failed", e);
        }
    }

    // Class with strictly private constructors, fields, and methods
    static class SecretUser {
        private final String username;
        private String secretKey = "HYDRA-DEFAULT";

        private SecretUser(String username) {
            this.username = username;
        }

        private void executeMission(String target) {
            System.out.println("  [MISSION] Execution starting by " + username + "! Target: " + target);
        }

        public String getUsername() { return username; }
    }
}
