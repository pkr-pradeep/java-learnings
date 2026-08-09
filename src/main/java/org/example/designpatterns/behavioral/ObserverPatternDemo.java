package org.example.designpatterns.behavioral;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>OBSERVER DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Behavioral Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Financial Stock Ticker & Crypto Price Alert Dispatcher / E-Commerce Order Status Event Bus.
 * When a stock price changes (e.g. `AAPL` spikes above $250), multiple decoupled subscribers need to react immediately:
 * 1. Mobile Push Notification Service
 * 2. Automated High-Frequency Trading Bot
 * 3. Audit Analytics Logger
 *
 * <h2>Problem Solved:</h2>
 * Defines a 1-to-many dependency between objects so that when one object changes state, all its dependents are notified automatically.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Hardcodes direct method calls to every single listener inside the `StockMarket` update method.</li>
 *   <li><b>Pitfall:</b> Adding a new listener requires modifying core market ticker logic. Observers cannot be subscribed/unsubscribed dynamically.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Implements push/pull event publishing with loose coupling.</li>
 *   <li><b>Memory Leak Warning ("Lapsed Listener Problem"):</b> Failing to unregister listeners when they are no longer needed prevents garbage collection!</li>
 *   <li>Real-world Framework Examples: Spring Event Bus ({@code ApplicationEventPublisher}, {@code @EventListener}), Reactive Extensions (RxJava / Project Reactor Flux/Mono), JMS Messaging.</li>
 * </ul>
 */
public class ObserverPatternDemo {

    public static void main(String[] args) {
        System.out.println("=== BEHAVIORAL PATTERN: OBSERVER DEMO ===");

        StockTicker appleStockTicker = new StockTicker("AAPL", 220.00);

        // 1. Create Observers
        StockObserver mobileAlert = new MobileAppPushObserver("User_9921");
        StockObserver tradingBot = new AutomatedTradingBotObserver("ALGO_ALPHA");
        StockObserver auditLogger = new AuditAnalyticsObserver();

        // 2. Register Observers with Subject
        appleStockTicker.subscribe(mobileAlert);
        appleStockTicker.subscribe(tradingBot);
        appleStockTicker.subscribe(auditLogger);

        // 3. Trigger Price Changes
        System.out.println("--- 1. Market Update: Stock Price Increases ---");
        appleStockTicker.updatePrice(245.50);

        System.out.println("\n--- 2. Market Update: Stock Price Spikes High ---");
        appleStockTicker.updatePrice(260.00);

        System.out.println("\n--- 3. Unsubscribing Trading Bot & Triggering Update ---");
        // Senior best practice: Unsubscribe to prevent memory leaks / unnecessary execution
        appleStockTicker.unsubscribe(tradingBot);
        appleStockTicker.updatePrice(210.00);
    }

    // -------------------------------------------------------------
    // Event Payload Data Object
    // -------------------------------------------------------------

    public static class StockPriceChangeEvent {
        private final String tickerSymbol;
        private final double oldPrice;
        private final double newPrice;
        private final long timestamp;

        public StockPriceChangeEvent(String tickerSymbol, double oldPrice, double newPrice) {
            this.tickerSymbol = tickerSymbol;
            this.oldPrice = oldPrice;
            this.newPrice = newPrice;
            this.timestamp = System.currentTimeMillis();
        }

        public String getTickerSymbol() { return tickerSymbol; }
        public double getOldPrice() { return oldPrice; }
        public double getNewPrice() { return newPrice; }
        public long getTimestamp() { return timestamp; }
    }

    // -------------------------------------------------------------
    // Observer Interface (Subscriber)
    // -------------------------------------------------------------

    public interface StockObserver {
        void onPriceChange(StockPriceChangeEvent event);
    }

    // -------------------------------------------------------------
    // Subject (Publisher)
    // -------------------------------------------------------------

    public static class StockTicker {
        private final String symbol;
        private double currentPrice;
        private final List<StockObserver> observers = new ArrayList<>();

        public StockTicker(String symbol, double initialPrice) {
            this.symbol = symbol;
            this.currentPrice = initialPrice;
        }

        public void subscribe(StockObserver observer) {
            if (!observers.contains(observer)) {
                observers.add(observer);
                System.out.printf("[Publisher] Subscribed new listener: %s%n", observer.getClass().getSimpleName());
            }
        }

        public void unsubscribe(StockObserver observer) {
            observers.remove(observer);
            System.out.printf("[Publisher] Unsubscribed listener: %s%n", observer.getClass().getSimpleName());
        }

        public void updatePrice(double newPrice) {
            if (Double.compare(currentPrice, newPrice) != 0) {
                double oldPrice = currentPrice;
                this.currentPrice = newPrice;
                System.out.printf("%n[StockTicker] %s price moved: $%.2f -> $%.2f%n", symbol, oldPrice, newPrice);
                
                // Notify all subscribed observers automatically
                notifyObservers(new StockPriceChangeEvent(symbol, oldPrice, newPrice));
            }
        }

        private void notifyObservers(StockPriceChangeEvent event) {
            for (StockObserver observer : observers) {
                try {
                    observer.onPriceChange(event);
                } catch (Exception e) {
                    System.err.println("Error notifying observer: " + e.getMessage());
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Concrete Observer 1: Mobile App Push Notification
    // -------------------------------------------------------------

    public static class MobileAppPushObserver implements StockObserver {
        private final String userId;

        public MobileAppPushObserver(String userId) {
            this.userId = userId;
        }

        @Override
        public void onPriceChange(StockPriceChangeEvent event) {
            System.out.printf("  -> [Mobile Push Alert for %s] %s is now $%.2f (Change: $%.2f)%n",
                    userId, event.getTickerSymbol(), event.getNewPrice(), (event.getNewPrice() - event.getOldPrice()));
        }
    }

    // -------------------------------------------------------------
    // Concrete Observer 2: Automated Trading Bot
    // -------------------------------------------------------------

    public static class AutomatedTradingBotObserver implements StockObserver {
        private final String botId;

        public AutomatedTradingBotObserver(String botId) {
            this.botId = botId;
        }

        @Override
        public void onPriceChange(StockPriceChangeEvent event) {
            if (event.getNewPrice() > 250.00) {
                System.out.printf("  -> [Trading Bot %s] TRIGGER SELL ORDER! Price $%.2f exceeded target threshold $250.00!%n",
                        botId, event.getNewPrice());
            } else {
                System.out.printf("  -> [Trading Bot %s] Holding position for %s.%n", botId, event.getTickerSymbol());
            }
        }
    }

    // -------------------------------------------------------------
    // Concrete Observer 3: Audit Analytics Logger
    // -------------------------------------------------------------

    public static class AuditAnalyticsObserver implements StockObserver {
        @Override
        public void onPriceChange(StockPriceChangeEvent event) {
            System.out.printf("  -> [Audit Log] Recorded price change event for %s at timestamp %d.%n",
                    event.getTickerSymbol(), event.getTimestamp());
        }
    }
}
