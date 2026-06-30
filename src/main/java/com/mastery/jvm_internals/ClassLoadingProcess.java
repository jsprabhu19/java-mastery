package com.mastery.jvm_internals;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "JVM Class Loading Subsystem",
    difficulty = Difficulty.INTERMEDIATE,
    what = "The ClassLoader Subsystem loads compiled class bytes (.class files) into JVM memory during execution. It utilizes a Delegation Model, delegating requests upwards to parent classloaders before attempting local loads.",
    whyItMatters = "Understanding classloaders avoids ClassNotFoundException or NoClassDefFoundError issues. Printing parent chains helps identify classpath loading priority issues.",
    keyPoints = {
        "Bootstrap ClassLoader is written in C/C++ and loads core java platform classes (java.lang.String). Returns 'null' in Java queries.",
        "Platform ClassLoader (formerly Extension) loads platform extension libraries.",
        "Application (System) ClassLoader loads classes declared on the standard classpath or module-path."
    },
    interviewQuestions = {
        @Question(
            question = "Explain the three main phases of Java class loading.",
            answer = "1. Loading: reads bytecode from files and creates class metadata in Metaspace. 2. Linking: performs verification (bytecode safety), preparation (allocating static field defaults), and resolution (translating symbolic references to direct links). 3. Initialization: runs class static initializers and assigns static variables."
        ),
        @Question(
            question = "What is the class loader Delegation Model?",
            answer = "When a ClassLoader is asked to load a class, it delegates the request to its parent ClassLoader first. This delegation bubble walks up to the Bootstrap ClassLoader. Only if parents fail to locate the class does the child attempt to load it locally."
        )
    },
    pitfalls = {
        "Confusion between ClassNotFoundException (explicit Class.forName lookup failure) vs NoClassDefFoundError (class compiled fine but missing on classpath during runtime execution)."
    }
)
public class ClassLoadingProcess {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("JVM Class Loader Delegation");

        // 1. Inspecting ClassLoader of standard Java classes
        ConsoleFormatter.printStep("Bootstrap ClassLoader", "Printing classloader of java.lang.String");
        ClassLoader bootstrapLoader = String.class.getClassLoader();
        System.out.println("String ClassLoader: " + bootstrapLoader); // Prints 'null' (represents Bootstrap loader)
        if (bootstrapLoader == null) {
            ConsoleFormatter.printSuccess("Core class String is loaded by Bootstrap Classloader (native/null).");
        }

        // 2. Inspecting Platform ClassLoader
        ConsoleFormatter.printStep("Platform ClassLoader", "Printing classloader of java.net.http.HttpClient");
        try {
            Class<?> platformClass = Class.forName("java.net.http.HttpClient");
            ClassLoader platformLoader = platformClass.getClassLoader();
            System.out.println("HttpClient ClassLoader: " + platformLoader);
            System.out.println("HttpClient ClassLoader Parent: " + (platformLoader != null ? platformLoader.getParent() : "null"));
        } catch (ClassNotFoundException e) {
            ConsoleFormatter.printWarning("HttpClient class not found: " + e.getMessage());
        }

        // 3. Inspecting Application ClassLoader
        ConsoleFormatter.printStep("Application ClassLoader", "Printing classloader of our custom ClassLoadingProcess class");
        ClassLoader appLoader = ClassLoadingProcess.class.getClassLoader();
        System.out.println("ClassLoadingProcess ClassLoader: " + appLoader); // AppClassLoader instance
        System.out.println("ClassLoadingProcess Parent: " + (appLoader != null ? appLoader.getParent() : "null")); // Platform ClassLoader

        if (appLoader != null && appLoader.getParent() != null) {
            ConsoleFormatter.printSuccess("Delegation hierarchy successfully verified (AppClassLoader -> PlatformClassLoader -> Bootstrap).");
        }
    }
}
