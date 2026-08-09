package org.example.designpatterns.behavioral;

/**
 * <h1>STRATEGY DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Behavioral Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Enterprise Dynamic Checkout Payment & Fee Calculation Engine.
 * An e-commerce system supports multiple payment strategies (Credit Card, PayPal, Crypto / Bitcoin, UPI).
 * Each strategy computes transaction fees differently and executes payment processing logic independently.
 *
 * <h2>Problem Solved:</h2>
 * Defines a family of algorithms, encapsulates each one, and makes them interchangeable at runtime without modifying the client context class.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Uses giant `if (paymentType.equalsIgnoreCase("CREDIT_CARD")) ... else if (...)` blocks scattered across controllers.</li>
 *   <li><b>Pitfall:</b> Rigid code, missing unit test isolation, violates Open-Closed Principle (OCP).</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Decouples strategy selection from execution using polymophic strategy objects.</li>
 *   <li><b>Modern Java 8 Enhancement:</b> Demonstrates how Java 8 functional interfaces and lambdas reduce boilerplate boilerplate class creation for stateless strategies!</li>
 *   <li>Real-world Framework Examples: Spring {@code Resource} loading strategies, {@code java.util.Comparator#compare()}, Java Security {@code Signature} algorithms.</li>
 * </ul>
 */
public class StrategyPatternDemo {

    public static void main(String[] args) {
        System.out.println("=== BEHAVIORAL PATTERN: STRATEGY DEMO ===");

        ShoppingCart cart = new ShoppingCart();
        cart.addItem("MacBook Pro M3", 2499.00);
        cart.addItem("Wireless Mouse", 49.99);

        // 1. Pay using Credit Card Strategy
        System.out.println("--- 1. Checkout using Credit Card Strategy ---");
        cart.setPaymentStrategy(new CreditCardPaymentStrategy("4111-2222-3333-4444", "12/28", "991"));
        cart.checkout();

        System.out.println("\n--- 2. Checkout using Crypto Payment Strategy ---");
        cart.setPaymentStrategy(new CryptoPaymentStrategy("0x71C7656EC7ab88b098defB751B7401B5f6d8976F"));
        cart.checkout();

        System.out.println("\n--- 3. Modern Java 8 Lambda Functional Strategy ---");
        // Senior Refactoring: No need to create a whole new class for simple strategies!
        PaymentStrategy fastUpiLambdaStrategy = (amount) -> {
            double fee = amount * 0.005; // 0.5% flat UPI fee
            System.out.printf("[Lambda UPI Strategy] Quick-pay executed! Total: $%.2f (Fee: $%.2f)%n", amount + fee, fee);
            return true;
        };

        cart.setPaymentStrategy(fastUpiLambdaStrategy);
        cart.checkout();
    }

    // -------------------------------------------------------------
    // Strategy Functional Interface (Java 8 Compatible)
    // -------------------------------------------------------------

    @FunctionalInterface
    public interface PaymentStrategy {
        boolean pay(double amount);
    }

    // -------------------------------------------------------------
    // Concrete Strategy 1: Credit Card
    // -------------------------------------------------------------

    public static class CreditCardPaymentStrategy implements PaymentStrategy {
        private final String cardNumber;
        private final String expiryDate;
        private final String cvv;

        public CreditCardPaymentStrategy(String cardNumber, String expiryDate, String cvv) {
            this.cardNumber = cardNumber;
            this.expiryDate = expiryDate;
            this.cvv = cvv;
        }

        @Override
        public boolean pay(double amount) {
            double processingFee = amount * 0.025; // 2.5% Credit Card Gateway Fee
            double total = amount + processingFee;
            System.out.printf("[Credit Card Strategy] Charged $%.2f ($%.2f base + $%.2f fee) to card ending in %s.%n",
                    total, amount, processingFee, cardNumber.substring(cardNumber.length() - 4));
            return true;
        }
    }

    // -------------------------------------------------------------
    // Concrete Strategy 2: Crypto Wallet
    // -------------------------------------------------------------

    public static class CryptoPaymentStrategy implements PaymentStrategy {
        private final String walletAddress;

        public CryptoPaymentStrategy(String walletAddress) {
            this.walletAddress = walletAddress;
        }

        @Override
        public boolean pay(double amount) {
            double networkGasFee = 1.50; // Fixed gas fee simulation
            double total = amount + networkGasFee;
            System.out.printf("[Crypto Strategy] Transferred $%.2f equivalent ($%.2f + $%.2f gas) to wallet %s... SUCCESS%n",
                    total, amount, networkGasFee, walletAddress.substring(0, 10) + "...");
            return true;
        }
    }

    // -------------------------------------------------------------
    // Context Class
    // -------------------------------------------------------------

    public static class ShoppingCart {
        private final java.util.List<String> items = new java.util.ArrayList<>();
        private double totalAmount = 0;
        private PaymentStrategy paymentStrategy;

        public void addItem(String itemName, double price) {
            items.add(itemName);
            totalAmount += price;
        }

        public void setPaymentStrategy(PaymentStrategy strategy) {
            this.paymentStrategy = strategy; // Interchangeable at runtime!
        }

        public void checkout() {
            if (paymentStrategy == null) {
                throw new IllegalStateException("No payment strategy configured for checkout.");
            }
            System.out.printf("[ShoppingCart] Processing payment for %d items (Total Amount: $%.2f)...%n", items.size(), totalAmount);
            boolean success = paymentStrategy.pay(totalAmount);
            if (success) {
                System.out.println("[ShoppingCart] Order paid and finalized successfully!");
            }
        }
    }
}
