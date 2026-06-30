package com.mastery.io;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@JavaConcept(
    name = "Java NIO.2 File Systems",
    difficulty = Difficulty.INTERMEDIATE,
    what = "NIO.2 introduces Path and Files interfaces for modern file system management. It provides memory-efficient, streaming file traversals and supports non-blocking stream APIs.",
    whyItMatters = "Classic java.io.File has limited error-handling and blocks during operation. NIO.2 Files uses standard Java Exceptions, supports symbol links, and allows lazily walking folder trees without loading the entire structure in memory.",
    keyPoints = {
        "Path represents a hierarchical path reference; Files provides static utility operations.",
        "Files.lines() returns a Stream<String> that reads file lines lazily, safe for gigabyte-scale logs.",
        "Files.walk() recursively traverses directory trees, producing a Stream<Path> of file descriptors."
    },
    interviewQuestions = {
        @Question(
            question = "Explain the difference between BIO (java.io) and NIO (java.nio).",
            answer = "BIO (Blocking I/O) is stream-oriented, reading/writing one or more bytes at a time in blocking calls. NIO (Non-blocking / New I/O) is buffer-and-channel oriented, allowing data to be loaded into memory blocks and processed asynchronously without blocking threads."
        ),
        @Question(
            question = "Why is Files.lines() preferred over Files.readAllLines() for large log files?",
            answer = "Files.readAllLines() reads the entire file into a List<String> in Heap memory, causing OutOfMemoryErrors for large files. Files.lines() returns a lazy Stream<String>, reading lines sequentially from disk as the stream is traversed."
        )
    },
    pitfalls = {
        "Forgetting to close resources returned by Files.lines() or Files.walk(). These classes hold active file descriptors and must be closed using try-with-resources."
    }
)
@SuppressWarnings("all")
public class NioFiles {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Java NIO.2 File Systems");

        // 1. Path operations
        ConsoleFormatter.printStep("Path Operations", "Configuring target path references");
        Path targetPath = Paths.get("temp_nio_file.txt");
        System.out.println("Absolute Path: " + targetPath.toAbsolutePath());
        System.out.println("File Name: " + targetPath.getFileName());

        // 2. Writing and Lazy reading
        ConsoleFormatter.printStep("Lazy File Stream", "Writing content and reading lazily using Files.lines()");
        try {
            Files.writeString(targetPath, "Line 1: Java NIO.2\nLine 2: Streams Integration\nLine 3: End of file");

            // Lazy reading in try-with-resources to close file handle!
            try (Stream<String> lines = Files.lines(targetPath)) {
                System.out.println("  [NIO-STREAM] Filtering and printing lines:");
                lines.filter(line -> line.contains("Line"))
                     .forEach(line -> System.out.println("    --> " + line));
            }

            ConsoleFormatter.printSuccess("Lazy file streaming completed.");

        } catch (IOException e) {
            ConsoleFormatter.printError("NIO write/read operation failed", e);
        }

        // 3. Folder walking
        ConsoleFormatter.printStep("Folder Tree Walking", "Walking directory files recursively using Files.walk()");
        Path currentDir = Paths.get(".");
        try (Stream<Path> walk = Files.walk(currentDir, 2)) { // max depth 2
            System.out.println("  [NIO-WALKER] Scanning workspace files:");
            walk.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".xml") || p.toString().endsWith(".md"))
                .limit(5)
                .forEach(p -> System.out.println("    --> " + p.getFileName()));
        } catch (IOException e) {
            ConsoleFormatter.printError("NIO directory walk failed", e);
        } finally {
            // Cleanup
            try {
                Files.deleteIfExists(targetPath);
                System.out.println("  [CLEANUP] Deleted temporary nio file.");
            } catch (IOException ignored) {}
        }
    }
}
