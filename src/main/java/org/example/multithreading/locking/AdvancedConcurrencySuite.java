package org.example.multithreading.locking;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Scenario 3: Advanced Concurrency Mechanics Suite.
 * 
 * Demonstrates:
 * 1. ReentrantReadWriteLock for read/write isolation in caches.
 * 2. Condition variables (await/signal) for low-level thread synchronization & bounded buffers.
 * 3. StampedLock for lock-free optimistic reading with read/write fallback.
 * 4. Producer-Consumer with Poison Pill pattern & functional task processing.
 */
public class AdvancedConcurrencySuite {

    /**
     * 1. Read/Write Lock Cache Example
     */
    public static class ReadWriteCache<K, V> {
        private final Map<K, V> map = new HashMap<>();
        private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        private final Lock readLock = rwLock.readLock();
        private final Lock writeLock = rwLock.writeLock();

        public V get(K key) {
            readLock.lock();
            try {
                return map.get(key);
            } finally {
                readLock.unlock();
            }
        }

        public void put(K key, V value) {
            writeLock.lock();
            try {
                map.put(key, value);
            } finally {
                writeLock.unlock();
            }
        }

        public int size() {
            readLock.lock();
            try {
                return map.size();
            } finally {
                readLock.unlock();
            }
        }
    }

    /**
     * 2. Bounded Buffer implementing Blocking mechanism via Lock and Condition variables.
     */
    public static class CustomBoundedBuffer<T> {
        private final Object[] items;
        private int putIndex, takeIndex, count;
        private final Lock lock = new ReentrantLock();
        private final Condition notFull  = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        public CustomBoundedBuffer(int capacity) {
            this.items = new Object[capacity];
        }

        public void put(T item) throws InterruptedException {
            lock.lock();
            try {
                while (count == items.length) {
                    notFull.await(); // Block producer until buffer space is available
                }
                items[putIndex] = item;
                if (++putIndex == items.length) putIndex = 0;
                count++;
                notEmpty.signal(); // Signal waiting consumers
            } finally {
                lock.unlock();
            }
        }

        @SuppressWarnings("unchecked")
        public T take() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0) {
                    notEmpty.await(); // Block consumer until item is produced
                }
                T item = (T) items[takeIndex];
                items[takeIndex] = null;
                if (++takeIndex == items.length) takeIndex = 0;
                count--;
                notFull.signal(); // Signal waiting producers
                return item;
            } finally {
                lock.unlock();
            }
        }

        public int size() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 3. High-throughput Point calculation using StampedLock (Optimistic Read).
     */
    public static class HighPerformancePoint {
        private double x, y;
        private final StampedLock sl = new StampedLock();

        public HighPerformancePoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void move(double deltaX, double deltaY) {
            long stamp = sl.writeLock(); // Exclusive write lock
            try {
                x += deltaX;
                y += deltaY;
            } finally {
                sl.unlockWrite(stamp);
            }
        }

        /**
         * Optimistic read calculation: try optimistic read without acquire lock.
         * If validated, return value without any lock overhead!
         * If invalidated by a concurrent write, fall back to pessimistic read lock.
         */
        public double distanceFromOrigin() {
            long stamp = sl.tryOptimisticRead();
            double currentX = x;
            double currentY = y;
            if (!sl.validate(stamp)) { // Check if a write lock was acquired in between
                stamp = sl.readLock(); // Fallback to pessimistic read lock
                try {
                    currentX = x;
                    currentY = y;
                } finally {
                    sl.unlockRead(stamp);
                }
            }
            return Math.sqrt(currentX * currentX + currentY * currentY);
        }
    }

    /**
     * 4. Task wrapper for Poison Pill shutdown pattern with functional execution.
     */
    public static class WorkTask {
        public static final WorkTask POISON_PILL = new WorkTask("POISON_PILL", null);

        private final String taskId;
        private final Consumer<String> action;

        public WorkTask(String taskId, Consumer<String> action) {
            this.taskId = taskId;
            this.action = action;
        }

        public String getTaskId() { return taskId; }

        public void execute() {
            if (action != null) {
                action.accept(taskId);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Starting Advanced Concurrency Suite ===");

        // --- Demo 1: ReentrantReadWriteLock Cache ---
        System.out.println("\n--- 1. ReentrantReadWriteLock Cache Demo ---");
        ReadWriteCache<String, String> cache = new ReadWriteCache<>();
        cache.put("config.url", "https://api.example.com");
        cache.put("config.timeout", "5000");

        ExecutorService readerPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            final int id = i;
            readerPool.execute(() -> {
                String val = cache.get("config.url");
                System.out.printf("[%s] Reader %d fetched config.url = %s%n", Thread.currentThread().getName(), id, val);
            });
        }
        readerPool.shutdown();
        readerPool.awaitTermination(2, TimeUnit.SECONDS);

        // --- Demo 2: Custom Bounded Buffer with Condition ---
        System.out.println("\n--- 2. Custom BoundedBuffer (Condition await/signal) Demo ---");
        CustomBoundedBuffer<String> buffer = new CustomBoundedBuffer<>(2);

        Thread producer = new Thread(() -> {
            try {
                buffer.put("Message-1");
                System.out.println("[Producer] Put Message-1");
                buffer.put("Message-2");
                System.out.println("[Producer] Put Message-2");
                System.out.println("[Producer] Attempting to put Message-3 (Buffer full, will block)...");
                buffer.put("Message-3");
                System.out.println("[Producer] Put Message-3 successfully!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(300); // Give producer time to fill buffer
                System.out.println("[Consumer] Taking item: " + buffer.take());
                System.out.println("[Consumer] Taking item: " + buffer.take());
                System.out.println("[Consumer] Taking item: " + buffer.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        // --- Demo 3: StampedLock Optimistic Reading ---
        System.out.println("\n--- 3. StampedLock Optimistic Reading Demo ---");
        HighPerformancePoint point = new HighPerformancePoint(3.0, 4.0);
        System.out.println("Initial distance from origin: " + point.distanceFromOrigin()); // 5.0

        ExecutorService lockPool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 10; i++) {
            lockPool.execute(() -> {
                double dist = point.distanceFromOrigin();
                // Randomly mutate
                if (ThreadLocalRandom.current().nextBoolean()) {
                    point.move(1.0, 1.0);
                }
            });
        }
        lockPool.shutdown();
        lockPool.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("Final distance after concurrent operations: " + point.distanceFromOrigin());

        // --- Demo 4: Producer-Consumer with Poison Pill ---
        System.out.println("\n--- 4. Producer-Consumer Poison Pill & Functional Executor Demo ---");
        CustomBoundedBuffer<WorkTask> taskBuffer = new CustomBoundedBuffer<>(5);

        Consumer<String> sampleAction = taskId ->
                System.out.printf("[%s] Executed task: %s%n", Thread.currentThread().getName(), taskId);

        Thread taskProducer = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    taskBuffer.put(new WorkTask("TASK-" + i, sampleAction));
                }
                // Send Poison Pill to terminate consumer thread cleanly
                taskBuffer.put(WorkTask.POISON_PILL);
                System.out.println("[TaskProducer] Poison Pill submitted.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread taskConsumer = new Thread(() -> {
            try {
                while (true) {
                    WorkTask task = taskBuffer.take();
                    if (task == WorkTask.POISON_PILL) {
                        System.out.println("[TaskConsumer] Received Poison Pill. Shutting down gracefully!");
                        break;
                    }
                    task.execute();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        taskProducer.start();
        taskConsumer.start();
        taskProducer.join();
        taskConsumer.join();

        System.out.println("\n=== Advanced Concurrency Suite Finished Successfully ===");
    }
}
