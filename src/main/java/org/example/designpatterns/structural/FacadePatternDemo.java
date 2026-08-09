package org.example.designpatterns.structural;

/**
 * <h1>FACADE DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Structural Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * E-Commerce One-Click Checkout Subsystem Orchestrator.
 * Placed behind a simple API endpoint (`orderFacade.placeOrder(...)`), completing a purchase requires coordinating 5 distinct complex subsystems:
 * 1. Inventory Management System (reserve stock)
 * 2. Payment Gateway Service (charge credit card)
 * 3. Logistics & Shipping Provider (generate tracking label)
 * 4. Loyalty Rewards Engine (calculate & award cashback points)
 * 5. Customer Notification Service (send confirmation email)
 *
 * <h2>Problem Solved:</h2>
 * Hides complex internal subsystem interactions behind a single, user-friendly high-level interface.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Exposes all subsystem classes directly to controllers or UI components.</li>
 *   <li><b>Pitfall:</b> Front-end or API layer is forced to orchestrate 10+ method calls in precise order, causing high coupling and duplication.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Creates a Facade object that encapsulates workflow logic and subsystem lifecycle management.</li>
 *   <li>Real-world Framework Examples: Spring {@code RestTemplate} / {@code WebClient} (Facade over HTTP connection managers, serializers, request body writers), JDBC {@code JdbcTemplate}.</li>
 * </ul>
 */
public class FacadePatternDemo {

    public static void main(String[] args) {
        System.out.println("=== STRUCTURAL PATTERN: FACADE DEMO ===");

        // Facade simplifies complex subsystem orchestration into 1 clean API call
        OrderProcessingFacade orderFacade = new OrderProcessingFacade();

        System.out.println("--- Executing One-Click Order Checkout ---");
        boolean orderStatus = orderFacade.placeOrder(
                "CUST-8831",
                "PROD-LAPTOP-X1",
                1,
                1299.99,
                "4111-2222-3333-4444",
                "123 Tech Park Way, San Jose CA"
        );

        System.out.println("\nFinal Checkout Status: " + (orderStatus ? "SUCCESS" : "FAILED"));
    }

    // -------------------------------------------------------------
    // Complex Subsystems
    // -------------------------------------------------------------

    public static class InventoryService {
        public boolean checkStock(String productId, int qty) {
            System.out.printf("[Subsystem: Inventory] Checking stock for '%s' (Qty: %d)... AVAILABLE%n", productId, qty);
            return true;
        }

        public void reserveStock(String productId, int qty) {
            System.out.printf("[Subsystem: Inventory] Reserved %d unit(s) of '%s'.%n", qty, productId);
        }
    }

    public static class PaymentProcessingService {
        public boolean processPayment(String customerId, String cardNumber, double amount) {
            System.out.printf("[Subsystem: Payment] Charging card ending in %s for $%.2f... SUCCESS%n",
                    cardNumber.substring(cardNumber.length() - 4), amount);
            return true;
        }
    }

    public static class ShippingLogisticsService {
        public String createShippingLabel(String productId, String destinationAddress) {
            String trackingNumber = "TRK-" + System.currentTimeMillis();
            System.out.printf("[Subsystem: Shipping] Created dispatch label for '%s' -> Destination: '%s'. Tracking: %s%n",
                    productId, destinationAddress, trackingNumber);
            return trackingNumber;
        }
    }

    public static class LoyaltyRewardsService {
        public void addPoints(String customerId, double orderAmount) {
            int points = (int) (orderAmount * 0.10); // 10% cash equivalent in points
            System.out.printf("[Subsystem: Loyalty] Awarded %d reward points to customer '%s'.%n", points, customerId);
        }
    }

    public static class EmailNotificationService {
        public void sendConfirmation(String customerId, String trackingNumber) {
            System.out.printf("[Subsystem: Notification] Sent order confirmation email to '%s'. Tracking: %s%n", customerId, trackingNumber);
        }
    }

    // -------------------------------------------------------------
    // FACADE CLASS
    // -------------------------------------------------------------

    public static class OrderProcessingFacade {
        private final InventoryService inventoryService;
        private final PaymentProcessingService paymentService;
        private final ShippingLogisticsService shippingService;
        private final LoyaltyRewardsService loyaltyService;
        private final EmailNotificationService notificationService;

        public OrderProcessingFacade() {
            this.inventoryService = new InventoryService();
            this.paymentService = new PaymentProcessingService();
            this.shippingService = new ShippingLogisticsService();
            this.loyaltyService = new LoyaltyRewardsService();
            this.notificationService = new EmailNotificationService();
        }

        /**
         * One-stop simplified method orchestrating complex multi-step workflow.
         */
        public boolean placeOrder(String customerId, String productId, int qty, double totalAmount, String cardNumber, String shippingAddress) {
            System.out.println("[Facade] Initiating simplified order placement workflow...");

            // Step 1: Verify inventory
            if (!inventoryService.checkStock(productId, qty)) {
                System.err.println("[Facade] Order Failed: Item out of stock.");
                return false;
            }

            // Step 2: Reserve inventory
            inventoryService.reserveStock(productId, qty);

            // Step 3: Process payment
            boolean paymentSuccess = paymentService.processPayment(customerId, cardNumber, totalAmount);
            if (!paymentSuccess) {
                System.err.println("[Facade] Order Failed: Payment declined.");
                return false;
            }

            // Step 4: Shipping dispatch
            String trackingNumber = shippingService.createShippingLabel(productId, shippingAddress);

            // Step 5: Loyalty rewards calculation
            loyaltyService.addPoints(customerId, totalAmount);

            // Step 6: Customer Notification
            notificationService.sendConfirmation(customerId, trackingNumber);

            System.out.println("[Facade] Workflow complete! Order successfully placed.");
            return true;
        }
    }
}
