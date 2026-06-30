package com.mastery.functional;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.function.Function;
import java.util.function.Predicate;

@JavaConcept(
    name = "Functional Composition and Chaining",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Functional Composition is the process of combining multiple simple functions into a single complex operation. The Function interface provides andThen() and compose() to chain calculations. The Predicate interface supports logical chaining.",
    whyItMatters = "Composing small, reusable functional blocks makes code cleaner and easier to unit test. It eliminates intermediate local variables, expressing logic as a declarative pipelines.",
    keyPoints = {
        "andThen() executes the current function first, then passes its output to the next function: g(f(x)).",
        "compose() executes the parameter function first, then passes its output to the current function: f(g(x)).",
        "Predicates support logical combining operators: and(), or(), and negate()."
    },
    interviewQuestions = {
        @Question(
            question = "Explain the difference between f.andThen(g) and f.compose(g).",
            answer = "For f.andThen(g), f is evaluated first and its output is passed as input to g. For f.compose(g), g is evaluated first and its output is passed as input to f."
        ),
        @Question(
            question = "How do you combine two Predicates to form an AND logical filter?",
            answer = "By using the .and() method: Predicate<T> combined = p1.and(p2). This returns true only if both p1 and p2 evaluate to true."
        )
    },
    pitfalls = {
        "Mixing up evaluation order in complex compose() chains, causing Type mismatch compile errors or runtime math bugs."
    }
)
@SuppressWarnings("all")
public class FunctionalComposition {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Functional Composition");

        // 1. Function Chaining: andThen vs compose
        ConsoleFormatter.printStep("Function Chaining", "Comparing andThen() vs compose() processing execution order");
        
        Function<Integer, Integer> multiplyByTwo = x -> x * 2;
        Function<Integer, Integer> addThree = x -> x + 3;

        // multiplyByTwo, then addThree: (x * 2) + 3
        Function<Integer, Integer> multiplyThenAdd = multiplyByTwo.andThen(addThree);
        // addThree, then multiplyByTwo: (x + 3) * 2
        Function<Integer, Integer> addThenMultiply = multiplyByTwo.compose(addThree);

        System.out.println("Input: 5");
        System.out.println("andThen result ((5 * 2) + 3): " + multiplyThenAdd.apply(5)); // Expected 13
        System.out.println("compose result ((5 + 3) * 2): " + addThenMultiply.apply(5)); // Expected 16

        if (multiplyThenAdd.apply(5) == 13 && addThenMultiply.apply(5) == 16) {
            ConsoleFormatter.printSuccess("Function ordering (andThen/compose) works as mathematically defined.");
        }

        // 2. Predicate Chaining
        ConsoleFormatter.printStep("Predicate Chaining", "Combining checks using and(), or(), and negate()");
        Predicate<String> startWithA = s -> s.startsWith("A");
        Predicate<String> endWithZ = s -> s.endsWith("Z");

        // A and Z: starts with A AND ends with Z
        Predicate<String> startAndEnd = startWithA.and(endWithZ);
        // A or not Z: starts with A OR does NOT end with Z
        Predicate<String> complexCheck = startWithA.or(endWithZ.negate());

        System.out.println("startAndEnd('ALCATRAZ'): " + startAndEnd.test("ALCATRAZ")); // True
        System.out.println("startAndEnd('APPLE'): " + startAndEnd.test("APPLE"));       // False
        System.out.println("complexCheck('APPLE'): " + complexCheck.test("APPLE"));     // True (starts with A)
        System.out.println("complexCheck('BANANA'): " + complexCheck.test("BANANA"));   // True (does not end with Z)
        System.out.println("complexCheck('BUZZ'): " + complexCheck.test("BUZZ"));       // False (starts with B, ends with Z)

        if (startAndEnd.test("ALCATRAZ") && !startAndEnd.test("APPLE")) {
            ConsoleFormatter.printSuccess("Predicate logical compositions verified successfully.");
        }
    }
}
