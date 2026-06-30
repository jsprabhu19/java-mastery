package com.mastery.oop;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "Polymorphism (Payment Gateway Integration)",
    difficulty = Difficulty.BEGINNER,
    what = "Polymorphism allows objects of different subclasses to be treated as objects of a common superclass or interface. Overriding enables runtime dynamic method dispatch. Overloading enables compile-time static binding.",
    whyItMatters = "Polymorphism allows code to remain open for extension but closed for modification. For instance, adding a new payment type (e.g., ApplePay) requires zero modifications to existing billing transaction loops.",
    keyPoints = {
        "Runtime Polymorphism relies on inheritance/interfaces and overridden methods. Binding is resolved at runtime by JVM.",
        "Static Polymorphism (Overloading) relies on different method signatures. Binding is resolved at compile time.",
        "Modern Java provides pattern-matching instanceof to combine type check and casting into a single atomic step."
    },
    interviewQuestions = {
        @Question(
            question = "How does the JVM execute dynamic method dispatch internally?",
            answer = "The JVM uses a Virtual Method Table (vtable) associated with each class type. At runtime, the JVM looks up the target method signature in the object's vtable to invoke the actual overridden implementation on the heap."
        ),
        @Question(
            question = "Can static methods perform runtime polymorphism?",
            answer = "No, static methods are resolved at compile-time based on the class reference declaration, meaning they do not utilize dynamic binding."
        )
    },
    pitfalls = {
        "Attempting to downcast an object without type validation, causing a ClassCastException at runtime.",
        "Declaring dynamic dispatch variables as concrete types rather than programming to interfaces."
    }
)
public class PolymorphismPayment {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Polymorphism (Payment Gateways)");

        // 1. Static Polymorphism (Method Overloading)
        ConsoleFormatter.printStep("Method Overloading", "Testing compilation-bound static signature choices");
        CheckoutCart cart = new CheckoutCart();
        cart.checkout(150.00);
        cart.checkout(150.00, "EUR");

        // 2. Runtime Polymorphism (Dynamic Method Dispatch)
        ConsoleFormatter.printStep("Dynamic Method Dispatch", "Processing different gateway classes via polymorphic interfaces");
        PaymentProcessor[] gateways = {
            new CreditCardProcessor("4111-2222-3333-4444"),
            new PayPalProcessor("user@domain.com"),
            new CryptoProcessor("0xABCDEF123456789")
        };

        double chargeAmount = 250.00;
        for (PaymentProcessor gateway : gateways) {
            // Polymorphic call: binds dynamically to overridden method
            gateway.processPayment(chargeAmount);
        }

        // 3. Pattern Matching Instanceof (Java 17+)
        ConsoleFormatter.printStep("Pattern Matching Casts", "Identifying specific gateway types to trigger special actions");
        for (PaymentProcessor gateway : gateways) {
            if (gateway instanceof CreditCardProcessor cc) {
                // 'cc' variable is already cast! No explicit casting required
                System.out.println("Activating credit security on card: " + maskCard(cc.getCardNumber()));
            } else if (gateway instanceof CryptoProcessor crypto) {
                System.out.println("Awaiting blockchain verification block at target: " + crypto.getWalletAddress());
            }
        }
        ConsoleFormatter.printSuccess("Polymorphic processing and pattern matching executed cleanly.");
    }

    private static String maskCard(String cardNumber) {
        return "XXXX-XXXX-XXXX-" + cardNumber.substring(cardNumber.length() - 4);
    }

    // Common Interface
    public interface PaymentProcessor {
        void processPayment(double amount);
    }

    // Subclass 1: Credit Card
    public static class CreditCardProcessor implements PaymentProcessor {
        private final String cardNumber;

        public CreditCardProcessor(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        @Override
        public void processPayment(double amount) {
            System.out.println("Charged $" + amount + " to Credit Card " + maskCard(cardNumber));
        }
    }

    // Subclass 2: PayPal
    public static class PayPalProcessor implements PaymentProcessor {
        private final String email;

        public PayPalProcessor(String email) {
            this.email = email;
        }

        @Override
        public void processPayment(double amount) {
            System.out.println("Charged $" + amount + " from PayPal account: " + email);
        }
    }

    // Subclass 3: Crypto
    public static class CryptoProcessor implements PaymentProcessor {
        private final String walletAddress;

        public CryptoProcessor(String walletAddress) {
            this.walletAddress = walletAddress;
        }

        public String getWalletAddress() {
            return walletAddress;
        }

        @Override
        public void processPayment(double amount) {
            System.out.println("Received $" + amount + " equivalent in Bitcoin from address: " + walletAddress);
        }
    }

    // Overloading class demonstrating Static Polymorphism
    public static class CheckoutCart {
        public void checkout(double amount) {
            System.out.println("Processing default checkout of $" + amount);
        }

        public void checkout(double amount, String currency) {
            System.out.println("Processing checkout of " + amount + " using currency: " + currency);
        }
    }
}
