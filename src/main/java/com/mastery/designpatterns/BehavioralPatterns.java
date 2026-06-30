package com.mastery.designpatterns;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.util.ArrayList;
import java.util.List;

@JavaConcept(
    name = "Behavioral Patterns (Strategy and Observer)",
    difficulty = Difficulty.INTERMEDIATE,
    what = "Behavioral patterns focus on object interaction and algorithm responsibility. Strategy isolates algorithms in interchangeable objects; Observer broadcasts state modifications to multiple dependent listeners automatically.",
    whyItMatters = "Strategy eliminates nested if-else statements for choosing algorithms (e.g. calculation policies). Observer decouples state triggers, allowing listener actions to add dynamically without modifying the state broadcaster.",
    keyPoints = {
        "Strategy defines a family of algorithms, encapsulates each one, and makes them interchangeable.",
        "Observer pattern utilizes a subject holding listener interfaces, invoking update() callbacks on change events.",
        "Both patterns enforce the Open/Closed Principle."
    },
    interviewQuestions = {
        @Question(
            question = "Explain how the Strategy pattern eliminates nested conditional branches.",
            answer = "By declaring an interface for the behavior (e.g., DiscountStrategy) and implementing concrete classes for each branch (e.g., VIPDiscount, HolidayDiscount). The client holds a strategy reference and delegates calculations to it directly, replacing nested if-else blocks."
        ),
        @Question(
            question = "What memory leak risk is associated with the Observer pattern?",
            answer = "Lapsed Listener problem: if observers register with a subject but forget to unregister, the subject maintains strong references to them. This blocks the garbage collector from reclaiming observers, creating memory leaks. Using weak references for observers solves this."
        )
    },
    pitfalls = {
        "Failing to unsubscribe observers when they are destroyed, leading to memory leaks.",
        "Overcomplicating designs with Strategy when basic conditional branches are sufficient and rarely change."
    }
)
@SuppressWarnings("all")
public class BehavioralPatterns {
    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Behavioral Patterns (Strategy & Observer)");

        // 1. Strategy Pattern: Pricing Strategies
        ConsoleFormatter.printStep("Strategy Selection", "Applying Black Friday discount vs VIP strategy on checkout cart");
        ShoppingCart cart = new ShoppingCart(100.00);

        // Apply Regular
        cart.setStrategy(new RegularDiscount());
        System.out.println("Regular Price: $" + cart.calculateTotal());

        // Apply Black Friday
        cart.setStrategy(new BlackFridayDiscount());
        System.out.println("Black Friday Price: $" + cart.calculateTotal());

        // Apply VIP
        cart.setStrategy(new VipDiscount());
        System.out.println("VIP Price: $" + cart.calculateTotal());

        if (cart.calculateTotal() == 80.00) {
            ConsoleFormatter.printSuccess("Strategies swapped and calculated correctly.");
        }

        // 2. Observer Pattern: Stock Ticker
        ConsoleFormatter.printStep("Observer Broadcast", "Publishing stock price changes to registered displays");
        StockMarket market = new StockMarket();

        DisplayScreen mobileScreen = new DisplayScreen("Mobile App Client");
        DisplayScreen webScreen = new DisplayScreen("Web Dashboard Client");

        // Subscribe observers
        market.subscribe(mobileScreen);
        market.subscribe(webScreen);

        // Modify prices (broadcast triggers)
        market.setStockPrice("GOOGL", 185.50);
        
        System.out.println("\nUnsubscribing Mobile client...");
        market.unsubscribe(mobileScreen);

        // Second price change
        market.setStockPrice("AMZN", 192.10);

        ConsoleFormatter.printSuccess("Observer notifications broad-casted.");
    }

    // ==========================================
    // 1. Strategy Pattern Implementation
    // ==========================================
    public interface DiscountStrategy {
        double applyDiscount(double basePrice);
    }

    public static class RegularDiscount implements DiscountStrategy {
        @Override
        public double applyDiscount(double basePrice) { return basePrice; } // no discount
    }

    public static class BlackFridayDiscount implements DiscountStrategy {
        @Override
        public double applyDiscount(double basePrice) { return basePrice * 0.5; } // 50% off
    }

    public static class VipDiscount implements DiscountStrategy {
        @Override
        public double applyDiscount(double basePrice) { return basePrice * 0.8; } // 20% off
    }

    public static class ShoppingCart {
        private final double basePrice;
        private DiscountStrategy strategy; // Composition

        public ShoppingCart(double basePrice) {
            this.basePrice = basePrice;
        }

        public void setStrategy(DiscountStrategy strategy) {
            this.strategy = strategy;
        }

        public double calculateTotal() {
            if (strategy == null) return basePrice;
            return strategy.applyDiscount(basePrice);
        }
    }

    // ==========================================
    // 2. Observer Pattern Implementation
    // ==========================================
    public interface Observer {
        void update(String stockSymbol, double price);
    }

    // Subject interface
    public static class StockMarket {
        private final List<Observer> observers = new ArrayList<>();

        public void subscribe(Observer observer) {
            observers.add(observer);
        }

        public void unsubscribe(Observer observer) {
            observers.remove(observer);
        }

        public void setStockPrice(String symbol, double price) {
            System.out.println("  [STOCK-MARKET] Price update: " + symbol + " is now $" + price);
            notifyObservers(symbol, price);
        }

        private void notifyObservers(String symbol, double price) {
            for (Observer observer : observers) {
                observer.update(symbol, price); // Callback!
            }
        }
    }

    // Concrete Observer
    public static class DisplayScreen implements Observer {
        private final String displayName;

        public DisplayScreen(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public void update(String stockSymbol, double price) {
            System.out.println("  [DISPLAY] '" + displayName + "' screen update -> " + stockSymbol + ": $" + price);
        }
    }
}
