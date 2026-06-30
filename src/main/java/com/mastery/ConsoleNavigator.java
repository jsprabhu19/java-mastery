package com.mastery;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.utils.ConsoleFormatter;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class ConsoleNavigator {

    private static final List<Class<?>> concepts = new ArrayList<>();

    public static void main(String[] args) {
        // Find and load all concepts
        Path srcDir = Paths.get("src/main/java/com/mastery");
        if (Files.exists(srcDir)) {
            loadConcepts(srcDir.toFile(), "com.mastery");
        } else {
            ConsoleFormatter.printWarning("Source directory not found. Trying local target compilation scanning...");
            // As a fallback, we search the class path or hardcode common packages
        }

        if (concepts.isEmpty()) {
            ConsoleFormatter.printError("No concepts loaded. Please ensure the project is compiled: run 'mvn compile'.", null);
            return;
        }

        // Sort by difficulty, then name
        concepts.sort(Comparator.<Class<?>, Difficulty>comparing(c -> c.getAnnotation(JavaConcept.class).difficulty())
                .thenComparing(c -> c.getAnnotation(JavaConcept.class).name()));

        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMainMenu();
            System.out.print("Enter choice (0-5): ");
            String input = scanner.nextLine().trim();

            if ("0".equals(input)) {
                System.out.println("Goodbye! Keep mastering Java!");
                break;
            }

            switch (input) {
                case "1":
                    listAllConcepts(scanner);
                    break;
                case "2":
                    filterByDifficulty(scanner);
                    break;
                case "3":
                    searchConcepts(scanner);
                    break;
                case "4":
                    runConceptInteractively(scanner);
                    break;
                case "5":
                    System.out.println("Triggering README documentation auto-generation...");
                    try {
                        Class<?> docGen = Class.forName("com.mastery.utils.DocGenerator");
                        Method mainMethod = docGen.getMethod("main", String[].class);
                        mainMethod.invoke(null, (Object) new String[]{});
                        ConsoleFormatter.printSuccess("README.md has been dynamically updated!");
                    } catch (Exception e) {
                        ConsoleFormatter.printError("Failed to trigger DocGenerator: " + e.getMessage(), e);
                    }
                    break;
                default:
                    ConsoleFormatter.printWarning("Invalid option, please try again.");
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void loadConcepts(File dir, String packageName) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                loadConcepts(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".java")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 5);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(JavaConcept.class)) {
                        concepts.add(clazz);
                    }
                } catch (Exception e) {
                    // Fail silently or log
                }
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n\u001B[36m\u001B[1m");
        System.out.println("=========================================================");
        System.out.println("          JAVA MASTERY INTERACTIVE TERMINAL EXPLORER      ");
        System.out.println("=========================================================\u001B[0m");
        System.out.println("  [1] List All Available Concepts (" + concepts.size() + ")");
        System.out.println("  [2] Filter Concepts by Difficulty");
        System.out.println("  [3] Search Concepts by Name/Keyword");
        System.out.println("  [4] View Details & Run a Concept");
        System.out.println("  [5] Run DocGenerator (Auto-update README)");
        System.out.println("  [0] Exit Navigator");
        System.out.println("---------------------------------------------------------");
    }

    private static void listAllConcepts(Scanner scanner) {
        System.out.println("\n--- All Concepts ---");
        for (int i = 0; i < concepts.size(); i++) {
            Class<?> clazz = concepts.get(i);
            JavaConcept concept = clazz.getAnnotation(JavaConcept.class);
            System.out.printf("[%d] %s [%s] - %s%n", i + 1, concept.name(), concept.difficulty(), concept.what());
        }
    }

    private static void filterByDifficulty(Scanner scanner) {
        System.out.println("\nSelect Difficulty Tier:");
        Difficulty[] values = Difficulty.values();
        for (int i = 0; i < values.length; i++) {
            System.out.printf("  [%d] %s%n", i + 1, values[i]);
        }
        System.out.print("Choice: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (choice >= 0 && choice < values.length) {
                Difficulty diff = values[choice];
                List<Class<?>> filtered = concepts.stream()
                        .filter(c -> c.getAnnotation(JavaConcept.class).difficulty() == diff)
                        .collect(Collectors.toList());

                System.out.println("\n--- " + diff + " Concepts ---");
                if (filtered.isEmpty()) {
                    System.out.println("No concepts implemented for this tier yet.");
                } else {
                    for (int i = 0; i < filtered.size(); i++) {
                        JavaConcept c = filtered.get(i).getAnnotation(JavaConcept.class);
                        System.out.printf("[%d] %s - %s%n", i + 1, c.name(), c.what());
                    }
                }
            } else {
                ConsoleFormatter.printWarning("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            ConsoleFormatter.printWarning("Please enter a valid number.");
        }
    }

    private static void searchConcepts(Scanner scanner) {
        System.out.print("\nEnter keyword to search: ");
        String query = scanner.nextLine().trim().toLowerCase();

        List<Class<?>> matching = concepts.stream()
                .filter(c -> {
                    JavaConcept jc = c.getAnnotation(JavaConcept.class);
                    return jc.name().toLowerCase().contains(query) ||
                            jc.what().toLowerCase().contains(query) ||
                            c.getSimpleName().toLowerCase().contains(query);
                })
                .collect(Collectors.toList());

        System.out.println("\n--- Search Results (" + matching.size() + ") ---");
        if (matching.isEmpty()) {
            System.out.println("No matching concepts found.");
        } else {
            for (int i = 0; i < matching.size(); i++) {
                JavaConcept c = matching.get(i).getAnnotation(JavaConcept.class);
                System.out.printf("[%d] %s [%s] - %s%n", i + 1, c.name(), c.difficulty(), c.what());
            }
        }
    }

    private static void runConceptInteractively(Scanner scanner) {
        listAllConcepts(scanner);
        System.out.print("\nEnter the number of the concept to view/run (1-" + concepts.size() + "): ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index >= 0 && index < concepts.size()) {
                Class<?> clazz = concepts.get(index);
                displayConceptDetails(clazz);

                System.out.print("\nWould you like to run the main method of this concept? (y/n): ");
                String answer = scanner.nextLine().trim().toLowerCase();
                if ("y".equals(answer)) {
                    runClassMain(clazz);
                }
            } else {
                ConsoleFormatter.printWarning("Invalid index.");
            }
        } catch (NumberFormatException e) {
            ConsoleFormatter.printWarning("Please enter a valid number.");
        }
    }

    private static void displayConceptDetails(Class<?> clazz) {
        JavaConcept c = clazz.getAnnotation(JavaConcept.class);
        System.out.println("\n=========================================================");
        System.out.println(" CONCEPT DETAILS: " + c.name());
        System.out.println("=========================================================");
        System.out.println("DIFFICULTY: " + c.difficulty());
        System.out.println("\nWHAT IS IT?");
        System.out.println("  " + c.what());
        System.out.println("\nWHY DOES IT MATTER?");
        System.out.println("  " + c.whyItMatters());
        System.out.println("\nKEY LEARNING POINTS:");
        for (String kp : c.keyPoints()) {
            System.out.println("  • " + kp);
        }
        if (c.pitfalls().length > 0) {
            System.out.println("\nCOMMON PITFALLS:");
            for (String pf : c.pitfalls()) {
                System.out.println("  ⚠️ " + pf);
            }
        }
        if (c.interviewQuestions().length > 0) {
            System.out.println("\nCOMMON INTERVIEW QUESTIONS:");
            for (JavaConcept.Question q : c.interviewQuestions()) {
                System.out.println("  Q: " + q.question());
                System.out.println("  A: " + q.answer());
                System.out.println();
            }
        }
        System.out.println("=========================================================");
    }

    private static void runClassMain(Class<?> clazz) {
        try {
            ConsoleFormatter.printHeader("Running: " + clazz.getSimpleName());
            Method mainMethod = clazz.getMethod("main", String[].class);
            // invoke main(new String[]{})
            mainMethod.invoke(null, (Object) new String[]{});
            ConsoleFormatter.printSuccess("Finished running " + clazz.getSimpleName());
        } catch (NoSuchMethodException e) {
            ConsoleFormatter.printWarning("No runnable main method found in " + clazz.getSimpleName());
        } catch (Exception e) {
            ConsoleFormatter.printError("Exception occurred during execution:", e.getCause() != null ? e.getCause() : e);
        }
    }
}
