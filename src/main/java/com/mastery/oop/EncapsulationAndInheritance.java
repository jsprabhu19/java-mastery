package com.mastery.oop;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

@JavaConcept(
    name = "Encapsulation and Inheritance (Banking System)",
    difficulty = Difficulty.BEGINNER,
    what = "Encapsulation hides an object's internal state, exposing operations through methods. Inheritance enables a class (subclass) to acquire the properties and behaviors of another class (superclass).",
    whyItMatters = "Encapsulation safeguards internal variables from corrupt inputs (maintaining state consistency). Inheritance avoids code duplication but must be managed carefully to avoid coupling issues.",
    keyPoints = {
        "Private fields with public getter/setter methods are the standard way to enforce encapsulation.",
        "Constructor chaining via this() and super() ensures clean, ordered object initialization.",
        "Overridden methods can invoke superclass behavior using the 'super' keyword.",
        "Subclasses cannot override static methods; defining a static method with the same signature in a subclass hides it (method hiding)."
    },
    interviewQuestions = {
        @Question(
            question = "What is the difference between method overriding and method hiding?",
            answer = "Method overriding applies to instance methods where the JVM dynamically dispatches the call based on runtime object type. Method hiding applies to static methods where the compiler binds the call based on the reference type at compile-time."
        ),
        @Question(
            question = "Can a constructor be overridden?",
            answer = "No, constructors cannot be inherited, hence they cannot be overridden. They are unique to the class declaration."
        )
    },
    pitfalls = {
        "Violating encapsulation by exposing internal mutable objects (like returning a reference to a raw Date object).",
        "Deep inheritance hierarchies that create high coupling (tight binding between super and sub classes)."
    }
)
public class EncapsulationAndInheritance {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Encapsulation & Inheritance (Banking)");

        // 1. Encapsulation and Access controls
        ConsoleFormatter.printStep("Encapsulation Safeguards", "Creating a new Bank Account and testing balance modification controls");
        BankAccount account = new BankAccount("ACC1001", "Alice Smith", 500.00);
        
        System.out.println("Initial Balance: $" + account.getBalance());
        account.deposit(200.00);
        account.withdraw(100.00);
        System.out.println("Ending Balance: $" + account.getBalance());

        // Validate negative deposit protection
        account.deposit(-50.00); 
        System.out.println("Balance after invalid deposit: $" + account.getBalance());
        
        if (account.getBalance() == 600.00) {
            ConsoleFormatter.printSuccess("Encapsulation successfully blocked negative transactions.");
        }

        // 2. Inheritance & Constructor Chaining
        ConsoleFormatter.printStep("Inheritance & Constructor Chaining", "Creating a Savings Account which inherits Bank Account features");
        SavingsAccount savings = new SavingsAccount("ACC2002", "Bob Johnson", 1000.00, 0.05);
        
        System.out.println("Savings Account ID: " + savings.accountNumber); // protected field
        System.out.println("Account Holder: " + savings.accountHolder);     // package-private field
        
        savings.applyInterest(); // Custom subclass behavior
        System.out.println("Savings Balance after Interest: $" + savings.getBalance());

        if (savings.getBalance() > 1000.00) {
            ConsoleFormatter.printSuccess("Constructor chaining and inheritance verified.");
        }
    }

    // Base Class demonstrating encapsulation
    public static class BankAccount {
        protected String accountNumber; // Accessible in subclass and package
        String accountHolder;          // Package-private
        private double balance;        // Encapsulated (Strictly hidden)

        public BankAccount(String accountNumber, String accountHolder, double initialBalance) {
            this.accountNumber = accountNumber;
            this.accountHolder = accountHolder;
            if (initialBalance >= 0) {
                this.balance = initialBalance;
            }
        }

        public double getBalance() {
            return this.balance;
        }

        public void deposit(double amount) {
            if (amount > 0) {
                this.balance += amount;
                System.out.println("Deposited: $" + amount);
            } else {
                ConsoleFormatter.printWarning("Deposit amount must be positive!");
            }
        }

        public void withdraw(double amount) {
            if (amount > 0 && amount <= this.balance) {
                this.balance -= amount;
                System.out.println("Withdrew: $" + amount);
            } else {
                ConsoleFormatter.printWarning("Insufficient funds or invalid withdrawal amount!");
            }
        }
    }

    // Subclass inheriting base class
    public static class SavingsAccount extends BankAccount {
        private final double interestRate;

        public SavingsAccount(String accountNumber, String accountHolder, double initialBalance, double interestRate) {
            super(accountNumber, accountHolder, initialBalance); // Constructor chaining!
            this.interestRate = interestRate;
        }

        public void applyInterest() {
            double interest = getBalance() * interestRate;
            deposit(interest); // Inherited method call
            System.out.println("Applied Interest: $" + interest);
        }
    }
}
