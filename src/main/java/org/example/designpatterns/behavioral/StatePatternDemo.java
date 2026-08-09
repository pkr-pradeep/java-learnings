package org.example.designpatterns.behavioral;

/**
 * <h1>STATE DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Behavioral Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * E-Commerce Order Lifecycle State Machine.
 * An e-commerce order transitions through strict operational states:
 * <b>[Created]</b> -> <b>[Paid]</b> -> <b>[Shipped]</b> -> <b>[Delivered]</b> (or <b>[Cancelled]</b>).
 * Rules:
 * <ul>
 *   <li>Cannot ship an order if it is in `Created` state (must be `Paid` first).</li>
 *   <li>Cannot cancel an order if it has already been `Shipped` or `Delivered`.</li>
 *   <li>Refunding is only valid in `Paid` or `Cancelled` states.</li>
 * </ul>
 *
 * <h2>Problem Solved:</h2>
 * Allows an object to alter its behavior when its internal state changes.
 * Eliminates spaghetti code of huge `switch-case` or `if-else` blocks controlling state transitions.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Uses an enum variable `OrderStatus` with huge conditional blocks (`if (status == CREATED) { ... } else if (status == PAID) { ... }`).</li>
 *   <li><b>Pitfall:</b> Adding a new state (e.g., `RETURNED`) requires editing dozens of switch statements across the entire project.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Encapsulates each state in its own class implementing a common `OrderState` interface.</li>
 *   <li>State transition rules are managed cleanly inside individual state classes.</li>
 *   <li>Real-world Framework Examples: Spring StateMachine framework, TCP Connection states (`LISTEN`, `ESTABLISHED`, `CLOSED`), Thread states in Java.</li>
 * </ul>
 */
public class StatePatternDemo {

    public static void main(String[] args) {
        System.out.println("=== BEHAVIORAL PATTERN: STATE DEMO ===");

        OrderContext order = new OrderContext("ORD-7788");

        System.out.println("--- 1. Attempting invalid transition: Ship before Payment ---");
        order.shipOrder(); // Invalid! Must be paid first

        System.out.println("\n--- 2. Processing Payment ---");
        order.payOrder(499.99); // Transitions to PaidState

        System.out.println("\n--- 3. Attempting duplicate payment ---");
        order.payOrder(499.99); // Invalid! Already paid

        System.out.println("\n--- 4. Shipping Order ---");
        order.shipOrder(); // Transitions to ShippedState

        System.out.println("\n--- 5. Attempting to Cancel Shipped Order ---");
        order.cancelOrder(); // Invalid! Cannot cancel once shipped

        System.out.println("\n--- 6. Delivering Order ---");
        order.deliverOrder(); // Transitions to DeliveredState
    }

    // -------------------------------------------------------------
    // Order State Interface
    // -------------------------------------------------------------

    public interface OrderState {
        void pay(OrderContext context, double amount);
        void ship(OrderContext context);
        void deliver(OrderContext context);
        void cancel(OrderContext context);
        String getStateName();
    }

    // -------------------------------------------------------------
    // Context Class (Holds reference to current State object)
    // -------------------------------------------------------------

    public static class OrderContext {
        private final String orderId;
        private OrderState currentState;

        public OrderContext(String orderId) {
            this.orderId = orderId;
            // Initial state is CreatedState
            this.currentState = new OrderCreatedState();
            System.out.printf("[Order %s] Created. Current state: %s%n", orderId, currentState.getStateName());
        }

        public void setState(OrderState newState) {
            System.out.printf("[State Transition] Order %s: %s ===> %s%n",
                    orderId, currentState.getStateName(), newState.getStateName());
            this.currentState = newState;
        }

        public void payOrder(double amount) {
            currentState.pay(this, amount);
        }

        public void shipOrder() {
            currentState.ship(this);
        }

        public void deliverOrder() {
            currentState.deliver(this);
        }

        public void cancelOrder() {
            currentState.cancel(this);
        }

        public String getOrderId() { return orderId; }
        public OrderState getCurrentState() { return currentState; }
    }

    // -------------------------------------------------------------
    // Concrete State 1: Order Created
    // -------------------------------------------------------------

    public static class OrderCreatedState implements OrderState {
        @Override
        public void pay(OrderContext context, double amount) {
            System.out.printf("[Created State] Payment of $%.2f received successfully.%n", amount);
            context.setState(new OrderPaidState());
        }

        @Override
        public void ship(OrderContext context) {
            System.err.println("[Created State Error] Cannot ship order. Payment is pending!");
        }

        @Override
        public void deliver(OrderContext context) {
            System.err.println("[Created State Error] Cannot deliver order. Order has not been paid or shipped!");
        }

        @Override
        public void cancel(OrderContext context) {
            System.out.println("[Created State] Order cancelled prior to payment.");
            context.setState(new OrderCancelledState());
        }

        @Override
        public String getStateName() { return "CREATED"; }
    }

    // -------------------------------------------------------------
    // Concrete State 2: Order Paid
    // -------------------------------------------------------------

    public static class OrderPaidState implements OrderState {
        @Override
        public void pay(OrderContext context, double amount) {
            System.err.println("[Paid State Error] Order is already paid!");
        }

        @Override
        public void ship(OrderContext context) {
            System.out.println("[Paid State] Order dispatched to courier logistics.");
            context.setState(new OrderShippedState());
        }

        @Override
        public void deliver(OrderContext context) {
            System.err.println("[Paid State Error] Order must be shipped before delivery.");
        }

        @Override
        public void cancel(OrderContext context) {
            System.out.println("[Paid State] Order cancelled. Issuing full refund to customer card...");
            context.setState(new OrderCancelledState());
        }

        @Override
        public String getStateName() { return "PAID"; }
    }

    // -------------------------------------------------------------
    // Concrete State 3: Order Shipped
    // -------------------------------------------------------------

    public static class OrderShippedState implements OrderState {
        @Override
        public void pay(OrderContext context, double amount) {
            System.err.println("[Shipped State Error] Order is already paid and in transit!");
        }

        @Override
        public void ship(OrderContext context) {
            System.err.println("[Shipped State Error] Order is already in transit!");
        }

        @Override
        public void deliver(OrderContext context) {
            System.out.println("[Shipped State] Package signed by customer. Delivery confirmed!");
            context.setState(new OrderDeliveredState());
        }

        @Override
        public void cancel(OrderContext context) {
            System.err.println("[Shipped State Error] Cannot cancel order while in transit with courier. Customer must request return upon delivery.");
        }

        @Override
        public String getStateName() { return "SHIPPED"; }
    }

    // -------------------------------------------------------------
    // Concrete State 4: Order Delivered (Terminal State)
    // -------------------------------------------------------------

    public static class OrderDeliveredState implements OrderState {
        @Override public void pay(OrderContext context, double amount) { System.err.println("Order finalized."); }
        @Override public void ship(OrderContext context) { System.err.println("Order finalized."); }
        @Override public void deliver(OrderContext context) { System.err.println("Order already delivered."); }
        @Override public void cancel(OrderContext context) { System.err.println("Delivered orders cannot be cancelled."); }
        @Override public String getStateName() { return "DELIVERED"; }
    }

    // -------------------------------------------------------------
    // Concrete State 5: Order Cancelled (Terminal State)
    // -------------------------------------------------------------

    public static class OrderCancelledState implements OrderState {
        @Override public void pay(OrderContext context, double amount) { System.err.println("Order is cancelled."); }
        @Override public void ship(OrderContext context) { System.err.println("Order is cancelled."); }
        @Override public void deliver(OrderContext context) { System.err.println("Order is cancelled."); }
        @Override public void cancel(OrderContext context) { System.err.println("Order already cancelled."); }
        @Override public String getStateName() { return "CANCELLED"; }
    }
}
