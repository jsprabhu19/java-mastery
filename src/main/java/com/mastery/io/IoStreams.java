package com.mastery.io;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.io.*;

@JavaConcept(
    name = "Java I/O Streams and Serialization",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Java I/O processes data streams. Byte streams (InputStream/OutputStream) process raw binary data; Character streams (Reader/Writer) handle character translation using charsets. Serialization saves object graphs into bytes.",
    whyItMatters = "Using unbuffered streams executes disk writes byte-by-byte, exhausting OS operations. Buffering aggregates data, reducing I/O system calls. Serialization transient modifiers prevent sensitive fields (e.g. passwords) from writing to disk.",
    keyPoints = {
        "Byte streams process raw bytes (8-bit); Character streams handle text (16-bit) and translation encodings.",
        "Buffered wrapper streams (BufferedReader) hold data chunks in memory arrays, reducing hardware access calls.",
        "Object Serialization requires class implements java.io.Serializable. Fields marked 'transient' are ignored."
    },
    interviewQuestions = {
        @Question(
            question = "What is the purpose of the transient keyword in Java?",
            answer = "The transient keyword prevents a field from being serialized. When an object is serialized, transient fields are ignored, and when deserialized, they return to their default values (e.g., null for objects, 0 for primitives)."
        ),
        @Question(
            question = "What is serialVersionUID and why is it important?",
            answer = "serialVersionUID is a unique identifier hash matching compiled class definitions. If not explicitly declared, the compiler generates one at compile-time. If the class definition changes slightly (like adding a field), serialization ids mismatch, causing InvalidClassException during deserialization."
        )
    },
    pitfalls = {
        "Forgetting to declare serialVersionUID explicitly, leading to deserialization failures when classes undergo code updates.",
        "Swallowing close() exceptions in finally blocks or omitting try-with-resources, leaking stream OS descriptors."
    }
)
@SuppressWarnings("all")
public class IoStreams {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Java I/O Streams & Serialization");

        // 1. Buffered Reader/Writer
        ConsoleFormatter.printStep("Character Buffering Writer", "Writing message using BufferedWriter");
        File tempFile = new File("temp_io_demo.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("Antigravity Core Java Mastery Reference File");
            writer.newLine();
            writer.write("Buffered systems reduce direct disk writes.");
        } catch (IOException e) {
            ConsoleFormatter.printError("Failed writing file", e);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            System.out.println("  [READER] Reading content line by line:");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("    --> " + line);
            }
        } catch (IOException e) {
            ConsoleFormatter.printError("Failed reading file", e);
        } finally {
            // Clean up temp file
            if (tempFile.delete()) {
                System.out.println("  [CLEANUP] Deleted temporary file.");
            }
        }

        // 2. Object Serialization & Deserialization
        ConsoleFormatter.printStep("Object Serialization", "Serializing User Session with transient fields");
        UserSession session = new UserSession("admin_user", "encryptedPassword123");
        byte[] serializedData = null;

        // Serialize to memory stream
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            
            System.out.println("  [SERIALIZER] Serializing object: " + session);
            oos.writeObject(session);
            serializedData = baos.toByteArray();
            
        } catch (IOException e) {
            ConsoleFormatter.printError("Serialization failed", e);
        }

        // Deserialization
        if (serializedData != null) {
            ConsoleFormatter.printStep("Object Deserialization", "Reconstructing User Session from bytes");
            try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                
                UserSession deserialized = (UserSession) ois.readObject();
                System.out.println("  [DESERIALIZER] Restored object: " + deserialized);
                
                if (deserialized.getPassword() == null) {
                    ConsoleFormatter.printSuccess("Transient password field was successfully ignored during serialization!");
                }
            } catch (IOException | ClassNotFoundException e) {
                ConsoleFormatter.printError("Deserialization failed", e);
            }
        }
    }

    static class UserSession implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L; // Explicit serialVersionUID

        private final String username;
        private final transient String password; // Transient (Excluded from serialization)

        public UserSession(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public String getPassword() { return password; }

        @Override
        public String toString() {
            return "UserSession{username='" + username + "', password='" + password + "'}";
        }
    }
}
