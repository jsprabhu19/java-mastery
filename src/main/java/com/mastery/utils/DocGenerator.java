package com.mastery.utils;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DocGenerator {

    private static final String START_MARKER = "<!-- TOPIC_INDEX_START -->";
    private static final String END_MARKER = "<!-- TOPIC_INDEX_END -->";

    public static void main(String[] args) {
        System.out.println("Starting documentation generation...");
        try {
            Path srcDir = Paths.get("src/main/java/com/mastery");
            if (!Files.exists(srcDir)) {
                System.err.println("Source directory not found: " + srcDir.toAbsolutePath());
                return;
            }

            List<Class<?>> conceptClasses = findConceptClasses(srcDir.toFile(), "com.mastery");
            System.out.println("Found " + conceptClasses.size() + " concept classes.");

            String markdownIndex = generateMarkdownIndex(conceptClasses);
            updateReadme(markdownIndex);

            System.out.println("Documentation generation completed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Class<?>> findConceptClasses(File dir, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null) return classes;

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findConceptClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".java")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 5);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(JavaConcept.class)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Could not load class: " + className + ". Make sure it is compiled.");
                }
            }
        }
        return classes;
    }

    private static String generateMarkdownIndex(List<Class<?>> classes) {
        // Group by Difficulty
        Map<Difficulty, List<Class<?>>> grouped = classes.stream()
                .collect(Collectors.groupingBy(c -> c.getAnnotation(JavaConcept.class).difficulty()));

        StringBuilder sb = new StringBuilder();
        sb.append("\n");

        for (Difficulty diff : Difficulty.values()) {
            List<Class<?>> list = grouped.getOrDefault(diff, Collections.emptyList());
            if (list.isEmpty()) continue;

            sb.append("### ").append(capitalize(diff.name())).append(" Tiers\n\n");
            sb.append("| Concept | Description | File Link | Key Takeaway |\n");
            sb.append("| :--- | :--- | :--- | :--- |\n");

            // Sort by class name to keep order consistent
            list.stream()
                    .sorted(Comparator.comparing(c -> c.getAnnotation(JavaConcept.class).name()))
                    .forEach(clazz -> {
                        JavaConcept concept = clazz.getAnnotation(JavaConcept.class);
                        String relativePath = clazz.getName().replace('.', '/') + ".java";
                        String fileLink = "[`" + clazz.getSimpleName() + ".java`](src/main/java/" + relativePath + ")";
                        String keyTakeaway = concept.keyPoints().length > 0 ? concept.keyPoints()[0] : "";
                        sb.append("| **").append(concept.name()).append("** | ")
                          .append(concept.what()).append(" | ")
                          .append(fileLink).append(" | ")
                          .append(keyTakeaway).append(" |\n");
                    });
            sb.append("\n");
        }

        return sb.toString();
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private static void updateReadme(String indexContent) throws IOException {
        Path readmePath = Paths.get("README.md");
        if (!Files.exists(readmePath)) {
            // Write a basic skeletal README if it doesn't exist
            String basicContent = "# Java Mastery Reference\n\n" +
                    "A complete, interactive learning guide covering the breadth of core Java.\n\n" +
                    "## Navigation & Catalog\n" +
                    START_MARKER + "\n" + END_MARKER + "\n";
            Files.writeString(readmePath, basicContent);
        }

        String readme = Files.readString(readmePath);
        int startIdx = readme.indexOf(START_MARKER);
        int endIdx = readme.indexOf(END_MARKER);

        if (startIdx == -1 || endIdx == -1) {
            System.err.println("Could not find markers in README.md. Adding them to the end.");
            readme = readme + "\n\n" + START_MARKER + indexContent + END_MARKER;
        } else {
            readme = readme.substring(0, startIdx + START_MARKER.length()) +
                    indexContent +
                    readme.substring(endIdx);
        }

        Files.writeString(readmePath, readme);
        System.out.println("README.md index section updated.");
    }
}
