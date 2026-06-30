package com.mastery.functional;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.Optional;

@JavaConcept(
    name = "Optional API best practices",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Optional is a container object that may or may not contain a non-null value. It provides stream-like methods (map, flatMap, filter) to safely chain operations and eliminate NullPointerExceptions.",
    whyItMatters = "Using Optional.get() without checking isPresent() throws NoSuchElementException, defeating the purpose of using Optional. Using orElse() triggers eager evaluation of defaults, whereas orElseGet() runs lazily.",
    keyPoints = {
        "Optional.ofNullable() safely wraps values that might be null.",
        "map() wraps the transformed object in Optional. flatMap() requires the mapping function to return an Optional, preventing double wrapping.",
        "orElse() evaluates default values eagerly; orElseGet() takes a Supplier and evaluates lazily."
    },
    interviewQuestions = {
        @Question(
            question = "What is the difference between optional.orElse(computeDefault()) and optional.orElseGet(() -> computeDefault())?",
            answer = "orElse() evaluates computeDefault() eagerly before checking if the Optional contains a value, executing it even if a value is present. orElseGet() evaluates the Supplier lazily, invoking computeDefault() only if the Optional is empty."
        ),
        @Question(
            question = "When should you use flatMap() instead of map() on an Optional?",
            answer = "Use flatMap() when the mapper function returns another Optional (e.g. User::getAddress where getAddress returns Optional<Address>). Using map() in this case yields nested containers: Optional<Optional<Address>>."
        )
    },
    pitfalls = {
        "Calling optional.get() directly without verifying isPresent() or using orElse/orElseThrow.",
        "Using Optional for class fields or method parameters (it is designed solely as a method return type wrapper to indicate possible absence)."
    }
)
@SuppressWarnings("all")
public class OptionalBasics {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Optional API Best Practices");

        // 1. Map vs FlatMap
        ConsoleFormatter.printStep("Optional Transformations", "Demonstrating map() vs flatMap() returning nested Optionals");
        User user = new User("Alice", new Address("London"));
        Optional<User> userOpt = Optional.of(user);

        // Map wraps output: Optional<Optional<Address>>
        Optional<Optional<Address>> mappedAddress = userOpt.map(User::getAddressOpt);
        
        // FlatMap flattens container: Optional<Address>
        Optional<Address> flatMappedAddress = userOpt.flatMap(User::getAddressOpt);

        System.out.println("Mapped Address nested container: " + mappedAddress);
        System.out.println("FlatMapped Address flattened container: " + flatMappedAddress);

        if (flatMappedAddress.isPresent()) {
            ConsoleFormatter.printSuccess("FlatMap flattened nested container cleanly.");
        }

        // 2. OrElse vs OrElseGet Eager vs Lazy gotcha
        ConsoleFormatter.printStep("OrElse vs OrElseGet", "Comparing eager orElse() vs lazy orElseGet() execution patterns");
        Optional<String> presentOptional = Optional.of("Existing Value");

        System.out.println("--- Triggering orElse (Eager) ---");
        String val1 = presentOptional.orElse(fallbackMethod()); // runs fallbackMethod even though value is present!

        System.out.println("--- Triggering orElseGet (Lazy) ---");
        String val2 = presentOptional.orElseGet(() -> fallbackMethod()); // skips fallbackMethod because value is present!

        System.out.println("orElse result: " + val1);
        System.out.println("orElseGet result: " + val2);

        ConsoleFormatter.printSuccess("Optional evaluations verified.");
    }

    private static String fallbackMethod() {
        System.out.println("    --> [FALLBACK] Resolving fallback default value...");
        return "Default Fallback String";
    }

    static class User {
        final String name;
        final Address address;

        User(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        public Optional<Address> getAddressOpt() {
            return Optional.ofNullable(address); // Returns Optional address
        }
    }

    static class Address {
        final String city;

        Address(String city) {
            this.city = city;
        }

        @Override
        public String toString() {
            return "Address{city='" + city + "'}";
        }
    }
}
