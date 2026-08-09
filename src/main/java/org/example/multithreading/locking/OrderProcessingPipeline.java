package org.example.multithreading.locking;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Scenario 1: Interactive Order & Inventory Processing System.
 * 
 * Demonstrates:
 * 1. Blocking Queue (ArrayBlockingQueue) for thread-safe producer-consumer communication.
 * 2. ReentrantLock with tryLock(timeout) for deadlock-free financial transfers.
 * 3. ConcurrentHashMap with atomic compute/merge methods powered by Functional Interfaces.
 * 4. Standard Functional Interfaces (Predicate, Function, Consumer, Supplier) for pipeline rules.
 */
public class OrderProcessingPipeline {

    public static class Order {
        private final String orderId;
        private final String customerId;
        private final String itemId;
        private final int quantity;
        private final double price;

        public Order(String customerId, String itemId, int quantity, double price) {
            this.orderId = UUID.randomUUID().toString().substring(0, 8);
            this.customerId = customerId;
            this.itemId = itemId;
            this.quantity = quantity;
            this.price = price;
        }

        public String getOrderId() { return orderId; }
        public String getCustomerId() { return customerId; }
        public String getItemId() { return itemId; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public double getTotalPrice() { return quantity * price; }

        @Override
        public String toString() {
            return String.format("Order[%s | Cust: %s | Item: %s x%d | $%.2f]",
                    orderId, customerId, itemId, quantity, getTotalPrice());
        }
    }

    public static class Receipt {
        private final String orderId;
        private final boolean successful;
        private final String message;
        private final double amountCharged;

        public Receipt(String orderId, boolean successful, String message, double amountCharged) {
            this.orderId = orderId;
            this.successful = successful;
            this.message = message;
            this.amountCharged = amountCharged;
        }

        public String getOrderId() { return orderId; }
        public boolean isSuccessful() { return successful; }
        public String getMessage() { return message; }
        public double getAmountCharged() { return amountCharged; }

        @Override
        public String toString() {
            return String.format("Receipt[Order: %s | Success: %b | Amount: $%.2f | Info: %s]",
                    orderId, successful, amountCharged, message);
        }
    }

    public static class CustomerAccount {
        private final String customerId;
        private double balance;
        private final ReentrantLock accountLock = new ReentrantLock();

        public CustomerAccount(String customerId, double initialBalance) {
            this.customerId = customerId;
            this.balance = initialBalance;
        }

        public String getCustomerId() { return customerId; }
        public double getBalance() { return balance; }

        /**
         * Safely deduct funds using explicit ReentrantLock with tryLock timeout.
         */
        public boolean deductBalance(double amount, long timeoutMs) throws InterruptedException {
            if (accountLock.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
                try {
                    if (balance >= amount) {
                        balance -= amount;
                        return true;
                    }
                    return false;
                } finally {
                    accountLock.unlock();
                }
            }
            return false; // Lock acquisition timed out (prevents deadlock)
        }

        public void creditBalance(double amount) {
            accountLock.lock();
            try {
                balance += amount;
            } finally {
                accountLock.unlock();
            }
        }
    }

    // State maintained across concurrent threads
    private final BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(10);
    private final ConcurrentMap<String, Integer> inventoryStore = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CustomerAccount> customerAccounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Receipt> processedReceipts = new ConcurrentHashMap<>();

    // Functional rules driving business logic
    private final Predicate<Order> orderValidator;
    private final Function<Order, Receipt> orderProcessor;
    private final Consumer<Receipt> auditLogger;

    public OrderProcessingPipeline() {
        // Rule 1: Predicate - validate order requirements (quantity > 0 and total price > 0)
        this.orderValidator = order -> order != null && order.getQuantity() > 0 && order.getTotalPrice() > 0;

        // Rule 2: Function - transform Order into Receipt using thread-safe inventory & account manipulation
        this.orderProcessor = order -> {
            if (!orderValidator.test(order)) {
                return new Receipt(order.getOrderId(), false, "Validation Failed: Invalid order specs", 0.0);
            }

            // Atomic inventory check & deduction using ConcurrentHashMap computeIfPresent (Functional BiFunction)
            boolean inventoryDeducted = inventoryStore.computeIfPresent(order.getItemId(), (itemId, currentStock) -> {
                if (currentStock >= order.getQuantity()) {
                    return currentStock - order.getQuantity();
                }
                return currentStock; // Not enough stock, retain current stock
            }) != null;

            // Re-verify if stock was actually enough
            Integer remainingStock = inventoryStore.get(order.getItemId());
            // Check if stock condition succeeded
            if (remainingStock == null) {
                return new Receipt(order.getOrderId(), false, "Item out of stock / unknown", 0.0);
            }

            CustomerAccount account = customerAccounts.get(order.getCustomerId());
            if (account == null) {
                // Rollback inventory using ConcurrentHashMap.merge with Integer::sum (BinaryOperator)
                inventoryStore.merge(order.getItemId(), order.getQuantity(), Integer::sum);
                return new Receipt(order.getOrderId(), false, "Customer account not found", 0.0);
            }

            try {
                boolean paymentSuccess = account.deductBalance(order.getTotalPrice(), 200);
                if (paymentSuccess) {
                    return new Receipt(order.getOrderId(), true, "Order completed successfully", order.getTotalPrice());
                } else {
                    // Rollback inventory on payment failure
                    inventoryStore.merge(order.getItemId(), order.getQuantity(), Integer::sum);
                    return new Receipt(order.getOrderId(), false, "Payment failed: Insufficient balance or timeout", 0.0);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Rollback inventory
                inventoryStore.merge(order.getItemId(), order.getQuantity(), Integer::sum);
                return new Receipt(order.getOrderId(), false, "Processing interrupted", 0.0);
            }
        };

        // Rule 3: Consumer - audit/log receipt
        this.auditLogger = receipt -> {
            System.out.printf("[%s] AUDIT LOG -> %s%n", Thread.currentThread().getName(), receipt);
        };
    }

    public ConcurrentMap<String, Integer> getInventoryStore() { return inventoryStore; }
    public ConcurrentMap<String, CustomerAccount> getCustomerAccounts() { return customerAccounts; }
    public ConcurrentMap<String, Receipt> getProcessedReceipts() { return processedReceipts; }
    public BlockingQueue<Order> getOrderQueue() { return orderQueue; }

    public void addInventory(String itemId, int count) {
        inventoryStore.merge(itemId, count, Integer::sum);
    }

    public void addCustomer(String customerId, double balance) {
        customerAccounts.put(customerId, new CustomerAccount(customerId, balance));
    }

    public void submitOrder(Order order) throws InterruptedException {
        // Blocking Queue put: blocks if queue is full
        orderQueue.put(order);
    }

    public void processNextOrder() throws InterruptedException {
        // Blocking Queue take: blocks if queue is empty
        Order order = orderQueue.poll(500, TimeUnit.MILLISECONDS);
        if (order != null) {
            Receipt receipt = orderProcessor.apply(order);
            processedReceipts.put(receipt.getOrderId(), receipt);
            auditLogger.accept(receipt);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Starting Order & Inventory Processing Scenario ===");
        OrderProcessingPipeline pipeline = new OrderProcessingPipeline();

        // Setup master inventory and accounts
        pipeline.addInventory("LAPTOP-01", 5);
        pipeline.addInventory("PHONE-01", 10);
        pipeline.addCustomer("CUST-ALICE", 2500.0);
        pipeline.addCustomer("CUST-BOB", 800.0);

        // Supplier functional interface to generate orders dynamically
        Supplier<Order> aliceOrderSupplier = () -> new Order("CUST-ALICE", "LAPTOP-01", 2, 1000.0);
        Supplier<Order> bobOrderSupplier = () -> new Order("CUST-BOB", "PHONE-01", 2, 500.0); // Bob has $800, order is $1000 -> Should fail

        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Producer tasks
        Runnable producerTask = () -> {
            try {
                for (int i = 0; i < 3; i++) {
                    Order order = aliceOrderSupplier.get();
                    System.out.printf("[%s] PRODUCER Submitting: %s%n", Thread.currentThread().getName(), order);
                    pipeline.submitOrder(order);
                    Thread.sleep(50);
                }
                pipeline.submitOrder(bobOrderSupplier.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Consumer tasks
        Runnable consumerTask = () -> {
            try {
                for (int i = 0; i < 3; i++) {
                    pipeline.processNextOrder();
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        executor.execute(producerTask);
        executor.execute(producerTask);
        executor.execute(consumerTask);
        executor.execute(consumerTask);

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\n=== FINAL PIPELINE STATE ===");
        System.out.println("Remaining Inventory: " + pipeline.getInventoryStore());
        System.out.println("Alice Balance: $" + pipeline.getCustomerAccounts().get("CUST-ALICE").getBalance());
        System.out.println("Bob Balance: $" + pipeline.getCustomerAccounts().get("CUST-BOB").getBalance());
        System.out.println("Total Processed Receipts: " + pipeline.getProcessedReceipts().size());
        pipeline.getProcessedReceipts().values().forEach(r -> System.out.println("  " + r));
    }
}
