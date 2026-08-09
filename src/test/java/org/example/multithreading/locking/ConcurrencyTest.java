package org.example.multithreading.locking;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrencyTest {

    @Test
    @DisplayName("Test OrderProcessingPipeline - Concurrent Inventory & Balance Deductions")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testOrderProcessingPipeline() throws InterruptedException {
        OrderProcessingPipeline pipeline = new OrderProcessingPipeline();
        pipeline.addInventory("ITEM-100", 10);
        pipeline.addCustomer("USER-1", 1000.0);

        int numberOfOrders = 10;
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Submit 10 orders concurrently for 1 item each costing $100
        for (int i = 0; i < numberOfOrders; i++) {
            executor.execute(() -> {
                try {
                    OrderProcessingPipeline.Order order =
                            new OrderProcessingPipeline.Order("USER-1", "ITEM-100", 1, 100.0);
                    pipeline.submitOrder(order);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Process all 10 orders
        for (int i = 0; i < numberOfOrders; i++) {
            executor.execute(() -> {
                try {
                    pipeline.processNextOrder();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertEquals(0, pipeline.getInventoryStore().get("ITEM-100"), "Inventory should be depleted to 0");
        assertEquals(0.0, pipeline.getCustomerAccounts().get("USER-1").getBalance(), 0.001, "Customer balance should be $0.0");
        assertEquals(10, pipeline.getProcessedReceipts().size(), "10 receipts should be recorded");
    }

    @Test
    @DisplayName("Test ListenerManagerExample - CopyOnWriteArrayList and Thread-safe updates")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testListenerManager() throws InterruptedException {
        ListenerManagerExample manager = new ListenerManagerExample();
        AtomicInteger receivedCount = new AtomicInteger(0);

        manager.addListener(event -> receivedCount.incrementAndGet());

        ExecutorService executor = Executors.newFixedThreadPool(4);
        int threadCount = 10;

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.execute(() -> {
                manager.publishEvent(new ListenerManagerExample.Event("INFO", "Data " + index), event -> true);
                manager.updateStateSynchronized(s -> s + "+S");
                manager.updateStateReentrant(r -> r + "+R");
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(3, TimeUnit.SECONDS));

        assertEquals(threadCount, receivedCount.get(), "All events should be delivered safely without race conditions");
        assertNotNull(manager.getStateSynchronized());
        assertNotNull(manager.getStateReentrant());
    }

    @Test
    @DisplayName("Test AdvancedConcurrencySuite - Custom BoundedBuffer & StampedLock")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testCustomBoundedBufferAndStampedLock() throws InterruptedException {
        AdvancedConcurrencySuite.CustomBoundedBuffer<Integer> buffer =
                new AdvancedConcurrencySuite.CustomBoundedBuffer<>(3);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch producerDone = new CountDownLatch(1);

        executor.execute(() -> {
            try {
                buffer.put(10);
                buffer.put(20);
                buffer.put(30);
                producerDone.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        assertTrue(producerDone.await(2, TimeUnit.SECONDS));
        assertEquals(3, buffer.size());
        assertEquals(10, buffer.take());
        assertEquals(20, buffer.take());
        assertEquals(30, buffer.take());
        assertEquals(0, buffer.size());

        // Test StampedLock point calculation
        AdvancedConcurrencySuite.HighPerformancePoint point =
                new AdvancedConcurrencySuite.HighPerformancePoint(6.0, 8.0);
        assertEquals(10.0, point.distanceFromOrigin(), 0.001);

        executor.shutdown();
    }
}
