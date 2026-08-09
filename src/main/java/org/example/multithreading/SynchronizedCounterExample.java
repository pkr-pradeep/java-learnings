package org.example.multithreading;

/**
 * Shows a practical synchronization example: multiple threads update the same counter.
 *
 * Use synchronized when multiple threads access shared mutable state and you need to
 * preserve invariants such as correct increment/decrement behavior.
 */
public class SynchronizedCounterExample {

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        };

        Thread t1 = new Thread(task, "Counter-1");
        Thread t2 = new Thread(task, "Counter-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final counter value: " + counter.getValue());
        System.out.println("Expected value: 2000 when increments are synchronized.");
    }

    private static class Counter {
        private int value;

        /**
         * synchronized ensures that only one thread can update the counter at a time.
         * Without it, the final value may be lower than expected because of lost updates.
         */
        public synchronized void increment() {
            value++;
        }

        public synchronized int getValue() {
            return value;
        }
    }
}
