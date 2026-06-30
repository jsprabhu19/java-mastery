package com.mastery.basics;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@JavaConcept(
    name = "Precision Arithmetic with BigDecimal",
    difficulty = Difficulty.INTERMEDIATE,
    what = "IEEE 754 floating-point standards (float/double) represent fractional values using binary approximations, causing rounding errors. BigDecimal offers arbitrary-precision signed decimal numbers, critical for monetary calculations.",
    whyItMatters = "Accumulating floating-point rounding errors in financials results in financial discrepancies and balance mismatches. BigDecimal solves this by enabling exact decimal representations.",
    keyPoints = {
        "Never use float or double for currency or exact financial math.",
        "Always instantiate BigDecimal using the String constructor or BigDecimal.valueOf(), never new BigDecimal(double).",
        "BigDecimal objects are immutable; operational methods return a new instance.",
        "Must specify a RoundingMode when performing division to avoid ArithmeticException from non-terminating decimals."
    },
    interviewQuestions = {
        @Question(
            question = "Why does new BigDecimal(0.1) cause problems whereas new BigDecimal(\"0.1\") does not?",
            answer = "The double literal 0.1 does not have an exact binary floating-point representation. new BigDecimal(0.1) captures the exact binary value (which is 0.1000000000000000055511151231257827021181583404541015625). The String constructor translates the literal characters exactly to 0.1."
        ),
        @Question(
            question = "What exception is thrown if you divide 1 by 3 using BigDecimal without specifying a scale or rounding mode?",
            answer = "It throws an ArithmeticException: 'Non-terminating decimal expansion; no exact representable decimal result.' because 1/3 has an infinite repeating fraction (0.333...)."
        )
    },
    pitfalls = {
        "Instantiating with double constructors: new BigDecimal(0.1). Use BigDecimal.valueOf(0.1) or new BigDecimal(\"0.1\").",
        "Forgetting that BigDecimal is immutable: bigDecimal.add(value) does not alter the original object. Must assign it back: x = x.add(y)."
    }
)
public class PrecisionBigDecimal {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Precision Arithmetic with BigDecimal");

        // 1. Floating point precision error demo
        ConsoleFormatter.printStep("Double Arithmetic Loss", "Adding 0.1 + 0.2 using primitive double");
        double d1 = 0.1;
        double d2 = 0.2;
        double sumDouble = d1 + d2;
        System.out.println("0.1 + 0.2 in double: " + sumDouble);
        if (sumDouble != 0.3) {
            ConsoleFormatter.printWarning("Double calculation results in precision loss! It equals " + sumDouble + " instead of 0.3");
        }

        // 2. Solving with BigDecimal
        ConsoleFormatter.printStep("Solving with BigDecimal", "Adding 0.1 + 0.2 using BigDecimal String constructor");
        BigDecimal b1 = new BigDecimal("0.1");
        BigDecimal b2 = new BigDecimal("0.2");
        BigDecimal sumBigDecimal = b1.add(b2);
        System.out.println("0.1 + 0.2 in BigDecimal: " + sumBigDecimal);
        if (sumBigDecimal.equals(new BigDecimal("0.3"))) {
            ConsoleFormatter.printSuccess("Exact 0.3 calculation achieved using BigDecimal!");
        }

        // 3. Constructor traps
        ConsoleFormatter.printStep("Double vs String Constructor Trap", "Comparing constructor instantiation techniques");
        BigDecimal doubleConstructor = new BigDecimal(0.1);
        BigDecimal stringConstructor = new BigDecimal("0.1");
        BigDecimal valueOfStaticMethod = BigDecimal.valueOf(0.1);

        System.out.println("new BigDecimal(0.1)      -> " + doubleConstructor);
        System.out.println("new BigDecimal(\"0.1\")    -> " + stringConstructor);
        System.out.println("BigDecimal.valueOf(0.1) -> " + valueOfStaticMethod);

        // 4. Immutability Pitfall
        ConsoleFormatter.printStep("Immutability Trap", "Checking if original object changes on addition");
        BigDecimal num = new BigDecimal("10.00");
        num.add(new BigDecimal("5.00")); // result ignored!
        System.out.println("Value after add (ignored result): " + num);
        
        num = num.add(new BigDecimal("5.00")); // correct way
        System.out.println("Value after correct re-assignment: " + num);

        // 5. Division Rounding Modes
        ConsoleFormatter.printStep("Division rounding requirements", "Dividing 1 by 3 to demonstrate scale rules");
        BigDecimal one = new BigDecimal("1.00");
        BigDecimal three = new BigDecimal("3.00");

        try {
            one.divide(three); // Throws ArithmeticException due to 0.3333333333333333333333...
        } catch (ArithmeticException ae) {
            ConsoleFormatter.printError("ArithmeticException caught when dividing without setting rounding rule!", ae);
        }

        BigDecimal quotient = one.divide(three, 4, RoundingMode.HALF_UP);
        ConsoleFormatter.printSuccess("Divided successfully with scale 4 & HALF_UP: " + quotient);
    }
}
