package org.example.multithreading.locking;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Scenario 2: Concurrent Event Publisher & Listener Management System.
 * 
 * Demonstrates:
 * 1. CopyOnWriteArrayList for thread-safe listener iteration without ConcurrentModificationException.
 * 2. Difference and usage of synchronized blocks vs ReentrantLock for thread-safe state mutation.
 * 3. Functional Interfaces (Consumer, Predicate, UnaryOperator) for functional event filtering and state transition.
 */
public class ListenerManagerExample {

    public static class Event {
        private final String type;
        private final String payload;
        private final long timestamp;

        public Event(String type, String payload) {
            this.type = type;
            this.payload = payload;
            this.timestamp = System.currentTimeMillis();
        }

        public String getType() { return type; }
        public String getPayload() { return payload; }
        public long getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("Event[type='%s', payload='%s']", type, payload);
        }
    }

    // Thread-safe collection: Optimized for frequent reads / listener iteration and infrequent listener addition/removal.
    private final List<Consumer<Event>> listeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<Event>> synchronizedListeners = new CopyOnWriteArrayList<>();

    // State protected by synchronized block vs ReentrantLock
    private String stateSynchronized = "INITIAL";
    private final Object syncLock = new Object();

    private String stateReentrant = "INITIAL";
    private final ReentrantLock reentrantLock = new ReentrantLock();

    private final AtomicInteger eventCounter = new AtomicInteger(0);

    /**
     * Add listener safely. CopyOnWriteArrayList allows concurrent iteration without locking reads.
     */
    public void addListener(Consumer<Event> listener) {
        listeners.add(listener);
    }

    public void removeListener(Consumer<Event> listener) {
        listeners.remove(listener);
    }

    /**
     * Publish event to all listeners, applying an optional Predicate filter.
     */
    public void publishEvent(Event event, Predicate<Event> filter) {
        if (filter != null && !filter.test(event)) {
            return; // Skip event if filter fails
        }
        eventCounter.incrementAndGet();
        // Safe concurrent iteration over CopyOnWriteArrayList snapshot
        for (Consumer<Event> listener : listeners) {
            listener.accept(event);
        }
    }

    /**
     * Mutate state using intrinsic synchronized block.
     */
    public void updateStateSynchronized(UnaryOperator<String> stateTransformer) {
        synchronized (syncLock) {
            this.stateSynchronized = stateTransformer.apply(this.stateSynchronized);
        }
    }

    /**
     * Mutate state using explicit ReentrantLock.
     */
    public void updateStateReentrant(UnaryOperator<String> stateTransformer) {
        reentrantLock.lock();
        try {
            this.stateReentrant = stateTransformer.apply(this.stateReentrant);
        } finally {
            reentrantLock.unlock();
        }
    }

    public String getStateSynchronized() {
        synchronized (syncLock) {
            return stateSynchronized;
        }
    }

    public String getStateReentrant() {
        reentrantLock.lock();
        try {
            return stateReentrant;
        } finally {
            reentrantLock.unlock();
        }
    }

    public int getEventCount() {
        return eventCounter.get();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Starting Listener & Concurrent State Scenario ===");
        ListenerManagerExample manager = new ListenerManagerExample();

        // Standard Functional Consumer implementations as listeners
        Consumer<Event> loggerListener = event ->
                System.out.printf("[%s] LOG LISTENER: Received %s%n", Thread.currentThread().getName(), event);

        Consumer<Event> metricListener = event ->
                System.out.printf("[%s] METRIC LISTENER: Processed type=%s%n", Thread.currentThread().getName(), event.getType());

        manager.addListener(loggerListener);
        manager.addListener(metricListener);

        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Functional Predicate filter: process only "IMPORTANT" events
        Predicate<Event> importantFilter = event -> "IMPORTANT".equalsIgnoreCase(event.getType());

        // Functional UnaryOperator state transformers
        UnaryOperator<String> appendStateFunc = current -> current + " -> UPDATED_BY_" + Thread.currentThread().getName();

        for (int i = 0; i < 5; i++) {
            final int index = i;
            executor.execute(() -> {
                String type = (index % 2 == 0) ? "IMPORTANT" : "INFO";
                Event event = new Event(type, "Payload-" + index);
                manager.publishEvent(event, importantFilter);

                // Concurrently update state using both mechanisms
                manager.updateStateSynchronized(appendStateFunc);
                manager.updateStateReentrant(appendStateFunc);
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\n=== FINAL STATE COMPARISON ===");
        System.out.println("Total Important Events Published: " + manager.getEventCount());
        System.out.println("Synchronized State length: " + manager.getStateSynchronized().length());
        System.out.println("ReentrantLock State length: " + manager.getStateReentrant().length());
    }
}
