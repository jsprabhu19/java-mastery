package com.mastery.functional;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.function.Function;

@JavaConcept(
    name = "Function Currying",
    difficulty = Difficulty.EXPERT,
    what = "Currying is a functional technique of decomposing a function that accepts multiple arguments into a chain of nested single-argument functions.",
    whyItMatters = "Currying enables Partial Application, allowing developers to pre-configure a function with specific parameters (like a default tax rate or environment endpoint) and reuse it across multiple evaluations.",
    keyPoints = {
        "Currying uses nested Function definitions: Function<A, Function<B, C>>.",
        "It supports partial execution: calling apply() on the outer function returns a configured inner function.",
        "It improves code composition and reusability in stream pipelines."
    },
    interviewQuestions = {
        @Question(
            question = "What is Currying in functional programming?",
            answer = "Currying is the process of transforming a function that takes multiple arguments into a sequence of functions, each taking a single argument. E.g. f(x, y, z) becomes f(x)(y)(z)."
        ),
        @Question(
            question = "How is currying useful in real-world application configurations?",
            answer = "It allows partial application. You can pass configuration variables (like server host or database credentials) to the outer functions, producing a specialized single-argument function that can be executed later."
        )
    },
    pitfalls = {
        "Highly complex nested generic types that decrease code readability if nested too deep."
    }
)
public class CurryingFunctions {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Function Currying");

        // 1. Basic Curried Add Function
        ConsoleFormatter.printStep("Basic Curried Sum", "Currying a two-argument addition (a, b) -> a + b");
        Function<Integer, Function<Integer, Integer>> curriedAdd = a -> b -> a + b;

        Function<Integer, Integer> addFive = curriedAdd.apply(5); // Partial Application: 'a' bound to 5
        int sum1 = addFive.apply(10); // 'b' bound to 10 -> 15
        int sum2 = addFive.apply(20); // 'b' bound to 20 -> 25

        System.out.println("5 + 10 = " + sum1);
        System.out.println("5 + 20 = " + sum2);

        // 2. Real-world configuration currying: Price Calculator
        ConsoleFormatter.printStep("Price Calculation Currying", "Creating a curried function with tax and discount tiers");
        
        // Structure: price -> taxRate -> discount -> finalPrice
        Function<Double, Function<Double, Function<Double, Double>>> priceCalculator = 
                price -> taxRate -> discount -> price * (1 + taxRate) * (1 - discount);

        // Configure calculations for a specific base product price ($100.00)
        Function<Double, Function<Double, Double>> productCalculation = priceCalculator.apply(100.00);

        // Configure calculations for a standard tax state (10% state tax) on that product
        Function<Double, Double> productWithStateTax = productCalculation.apply(0.10);

        // Now calculate prices applying different client loyalty discounts
        double regularPrice = productWithStateTax.apply(0.00); // 0% discount
        double goldPrice = productWithStateTax.apply(0.15);    // 15% discount

        System.out.println("Final product price (Regular, 10% tax, 0% discount): $" + regularPrice);
        System.out.println("Final product price (Gold, 10% tax, 15% discount): $" + goldPrice);

        if (regularPrice == 110.00 && goldPrice == 93.50) {
            ConsoleFormatter.printSuccess("Curried calculator computed exact partial-applied prices!");
        }
    }
}
