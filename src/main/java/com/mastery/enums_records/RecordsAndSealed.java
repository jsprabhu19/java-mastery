package com.mastery.enums_records;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "Records and Sealed Classes",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Records are immutable data carrier classes that automatically generate fields, getters, toString, equals, and hashCode. Sealed classes/interfaces restrict which subclasses or interfaces are allowed to extend or implement them.",
    whyItMatters = "Records reduce boilerplate data carrier code. Sealed classes allow compiling domain models securely, letting compilers guarantee switch statement exhaustiveness without needing fallback 'default' clauses.",
    keyPoints = {
        "Records are final, hold immutable fields, and use compact constructors for validation.",
        "Sealed classes declare permitted subclasses via the 'permits' keyword.",
        "Permitted subclasses must be declared 'final', 'non-sealed', or 'sealed'."
    },
    interviewQuestions = {
        @Question(
            question = "What is a compact constructor in a Java Record?",
            answer = "A compact constructor has no parameter list declared (e.g., public User { ... }). It allows validating or normalizing inputs before fields are implicitly assigned at the end of the constructor, reducing validation duplication."
        ),
        @Question(
            question = "What are the rules for subclasses extending a Sealed class?",
            answer = "Every permitted subclass of a sealed class must explicitly declare one of three modifiers: 'final' (cannot be extended further), 'non-sealed' (re-opens the class hierarchy for anyone to extend), or 'sealed' (continues restricted inheritance boundaries)."
        )
    },
    pitfalls = {
        "Attempting to declare instance variables inside records. Records can only contain fields defined in their header declaration (static variables are allowed).",
        "Declaring a sealed class with permitted subclasses residing in different packages (permitted classes must reside in the same package or module)."
    }
)
@SuppressWarnings("all")
public class RecordsAndSealed {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Records & Sealed Classes");

        // 1. Records and Compact Constructor Validation
        ConsoleFormatter.printStep("Record validation", "Creating a Record and verifying compact constructor limits");
        try {
            new UserProfile("John Doe", -5); // should throw exception
        } catch (IllegalArgumentException e) {
            ConsoleFormatter.printWarning("Validation failed as expected: " + e.getMessage());
        }

        UserProfile user = new UserProfile("Alice Smith", 28);
        System.out.println("User record: " + user);
        System.out.println("Username getter: " + user.name()); // Notice getter names have no 'get' prefix!
        System.out.println("User age: " + user.age());

        // 2. Sealed Classes and Pattern Matching switch (exhaustiveness check)
        ConsoleFormatter.printStep("Sealed Class Hierarchy", "Processing polymorphic shapes using pattern matching switch expressions");
        Shape circle = new Circle(5.0);
        Shape rectangle = new Rectangle(4.0, 6.0);

        printShapeArea(circle);
        printShapeArea(rectangle);

        ConsoleFormatter.printSuccess("Records and Sealed class pattern matching verified.");
    }

    // Record with compact constructor
    public record UserProfile(String name, int age) {
        public UserProfile {
            // Compact constructor - parameters name and age are implicitly available
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            if (age < 0) {
                throw new IllegalArgumentException("Age cannot be negative: " + age);
            }
            // Implicit assignment: this.name = name; this.age = age; occurs here automatically!
        }
    }

    // Sealed interface defining Shape boundaries
    public sealed interface Shape permits Circle, Rectangle {
        double area();
    }

    // Permitted subclass 1: Circle (marked final)
    public static final class Circle implements Shape {
        private final double radius;

        public Circle(double radius) {
            this.radius = radius;
        }

        @Override
        public double area() {
            return Math.PI * radius * radius;
        }
    }

    // Permitted subclass 2: Rectangle (marked final)
    public static final class Rectangle implements Shape {
        private final double width;
        private final double height;

        public Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public double area() {
            return width * height;
        }
    }

    // Compiler knows Shape only has Circle and Rectangle.
    // Therefore, this switch expression does NOT need a 'default' branch!
    private static void printShapeArea(Shape shape) {
        double area = switch (shape) {
            case Circle c -> c.area();
            case Rectangle r -> r.area();
        };
        System.out.printf("  [SHAPE] Area of %s: %.2f%n", shape.getClass().getSimpleName(), area);
    }
}
