package com.mastery.designpatterns;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "Creational and Structural Patterns (Builder, Factory, Adapter, Decorator)",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Creational patterns abstract object instantiation. Builder constructs complex objects; Factory encapsulates creation choices. Structural patterns assemble objects. Adapter bridges incompatible interfaces; Decorator adds behaviors dynamically.",
    whyItMatters = "Using these patterns replaces rigid class couplings. Builder makes objects immutable and readable. Decorator stacks enhancements (like logging and encryption) dynamically without subclass bloat.",
    keyPoints = {
        "Builder isolates construction parameters, ensuring final target objects are immutable.",
        "Factory isolates instantiation, enabling adding new classes without changing client invocation code.",
        "Adapter wraps legacy interfaces to fit modern requirements.",
        "Decorator wraps target classes, matching their interface signatures to stack behaviors dynamically."
    },
    interviewQuestions = {
        @Question(
            question = "What is the primary difference between the Adapter and Decorator pattern?",
            answer = "Adapter bridges different interfaces together to achieve compatibility without altering logic. Decorator wraps the same interface, extending behavior (adding features) without altering the target's underlying interface type."
        ),
        @Question(
            question = "Why is Builder pattern preferred over telescoping constructors?",
            answer = "Telescoping constructors (having constructors with 2, 3, 4 params) make code hard to read and write. Builder makes the initialization readable (fluent API), prevents parameter swap bugs, and guarantees object immutability once build() is invoked."
        )
    },
    pitfalls = {
        "Using Decorators excessively, resulting in a large number of wrapping decorator instances that are difficult to debug.",
        "Adding Factory patterns for simple objects that rarely change, introducing unnecessary complexity."
    }
)
@SuppressWarnings("all")
public class CreationalAndStructuralPatterns {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Creational & Structural Patterns");

        // 1. Builder Pattern: Immutable HTTP Request
        ConsoleFormatter.printStep("Builder Pattern", "Constructing an immutable HTTP Request object");
        HttpRequest request = new HttpRequest.Builder()
                .setUrl("https://api.mastery.com/v1/users")
                .setMethod("POST")
                .setHeader("Content-Type", "application/json")
                .setBody("{ \"username\": \"alice\" }")
                .build();
        System.out.println("HTTP Request created: " + request);

        // 2. Factory Pattern: Database Connection selection
        ConsoleFormatter.printStep("Factory Pattern", "Fetching Database Driver via connection factory");
        ConnectionDriver mysql = ConnectionFactory.getDriver("mysql");
        ConnectionDriver h2 = ConnectionFactory.getDriver("h2");
        mysql.connect();
        h2.connect();

        // 3. Adapter Pattern: XML to JSON translation
        ConsoleFormatter.printStep("Adapter Pattern", "Translating legacy XML reporting output to JSON using Adapter");
        LegacyXmlReport xmlReport = new LegacyXmlReport("<report><user>Alice</user><status>Active</status></report>");
        ModernJsonAnalytics jsonAnalytics = new XmlToJsonAdapter(xmlReport);
        System.out.println("JSON Analytics Client output: " + jsonAnalytics.getJsonData());

        // 4. Decorator Pattern: Dynamic Writers
        ConsoleFormatter.printStep("Decorator Pattern", "Stacking encryption and logging decorators onto a basic Writer");
        MessageWriter baseWriter = new ConsoleMessageWriter();
        MessageWriter encryptedWriter = new EncryptedWriter(baseWriter);
        MessageWriter loggedEncryptedWriter = new LoggerWriter(encryptedWriter);

        System.out.println("Executing raw ConsoleMessageWriter:");
        baseWriter.write("HelloWorld");
        
        System.out.println("\nExecuting Logged + Encrypted wrapper stack:");
        loggedEncryptedWriter.write("HelloWorld");

        ConsoleFormatter.printSuccess("Creational and Structural pattern simulations completed successfully.");
    }

    // ==========================================
    // 1. Builder Pattern Implementation
    // ==========================================
    public static class HttpRequest {
        private final String url;
        private final String method;
        private final String header;
        private final String body;

        private HttpRequest(Builder builder) {
            this.url = builder.url;
            this.method = builder.method;
            this.header = builder.header;
            this.body = builder.body;
        }

        @Override
        public String toString() {
            return "HttpRequest[" + method + " " + url + ", Header=" + header + ", Body=" + body + "]";
        }

        public static class Builder {
            private String url;
            private String method = "GET"; // Default value
            private String header = "Accept: */*";
            private String body = "";

            public Builder setUrl(String url) {
                this.url = url;
                return this;
            }

            public Builder setMethod(String method) {
                this.method = method;
                return this;
            }

            public Builder setHeader(String name, String value) {
                this.header = name + ": " + value;
                return this;
            }

            public Builder setBody(String body) {
                this.body = body;
                return this;
            }

            public HttpRequest build() {
                if (url == null) throw new IllegalStateException("URL cannot be null");
                return new HttpRequest(this);
            }
        }
    }

    // ==========================================
    // 2. Factory Pattern Implementation
    // ==========================================
    public interface ConnectionDriver {
        void connect();
    }

    public static class MySqlDriver implements ConnectionDriver {
        @Override
        public void connect() { System.out.println("  [MySQL] Driver connected to MySQL DB."); }
    }

    public static class H2Driver implements ConnectionDriver {
        @Override
        public void connect() { System.out.println("  [H2] Driver connected to In-Memory H2 DB."); }
    }

    public static class ConnectionFactory {
        public static ConnectionDriver getDriver(String type) {
            return switch (type.toLowerCase()) {
                case "mysql" -> new MySqlDriver();
                case "h2" -> new H2Driver();
                default -> throw new IllegalArgumentException("Unknown driver: " + type);
            };
        }
    }

    // ==========================================
    // 3. Adapter Pattern Implementation
    // ==========================================
    public static class LegacyXmlReport {
        private final String xml;
        public LegacyXmlReport(String xml) { this.xml = xml; }
        public String getXmlData() { return xml; }
    }

    public interface ModernJsonAnalytics {
        String getJsonData();
    }

    public static class XmlToJsonAdapter implements ModernJsonAnalytics {
        private final LegacyXmlReport xmlReport; // Composition

        public XmlToJsonAdapter(LegacyXmlReport xmlReport) {
            this.xmlReport = xmlReport;
        }

        @Override
        public String getJsonData() {
            String xml = xmlReport.getXmlData();
            // Simple mock conversion parsing XML text tags to JSON values
            String user = xml.substring(xml.indexOf("<user>") + 6, xml.indexOf("</user>"));
            String status = xml.substring(xml.indexOf("<status>") + 8, xml.indexOf("</status>"));
            return "{ \"user\": \"" + user + "\", \"status\": \""+ status +"\" }";
        }
    }

    // ==========================================
    // 4. Decorator Pattern Implementation
    // ==========================================
    public interface MessageWriter {
        void write(String message);
    }

    public static class ConsoleMessageWriter implements MessageWriter {
        @Override
        public void write(String message) {
            System.out.println("Writing raw: " + message);
        }
    }

    // Base Decorator class
    public static abstract class WriterDecorator implements MessageWriter {
        protected final MessageWriter decoratedWriter;

        public WriterDecorator(MessageWriter decoratedWriter) {
            this.decoratedWriter = decoratedWriter;
        }

        @Override
        public void write(String message) {
            decoratedWriter.write(message);
        }
    }

    // Concrete Decorator 1: Encrypting
    public static class EncryptedWriter extends WriterDecorator {
        public EncryptedWriter(MessageWriter decoratedWriter) { super(decoratedWriter); }

        @Override
        public void write(String message) {
            String encrypted = new StringBuilder(message).reverse().toString(); // simple mock encryption (reverse)
            System.out.println("  [DECORATOR] [ENCRYPTING] Reversing text...");
            super.write(encrypted);
        }
    }

    // Concrete Decorator 2: Logging
    public static class LoggerWriter extends WriterDecorator {
        public LoggerWriter(MessageWriter decoratedWriter) { super(decoratedWriter); }

        @Override
        public void write(String message) {
            System.out.println("  [DECORATOR] [LOGGING] Writing operation initiated.");
            super.write(message);
            System.out.println("  [DECORATOR] [LOGGING] Writing operation completed.");
        }
    }
}
