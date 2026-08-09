package org.example.multithreading;

/**
 * Demonstrates when to use a custom Thread subclass versus a Runnable task.
 *
 * Use a Thread subclass when you want to attach thread-specific behavior or state
 * directly to the thread object. Use Runnable when you want the task logic to be
 * independent of the thread that executes it.
 */
public class ThreadExamples {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Thread subclass example ===");

        // Use a Thread subclass when the thread itself carries behavior/state.
        Thread threadA = new WorkerThread("Worker-A");
        threadA.start();

        System.out.println("=== Runnable lambda example ===");

        // Use Runnable when the work is separate from the thread that runs it.
        Thread threadB = new Thread(() -> {
            System.out.println("Runnable lambda running on " + Thread.currentThread().getName());
        }, "Runnable-Thread");
        threadB.start();

        // Wait for both threads to finish before exiting main.
        threadA.join();
        threadB.join();

        System.out.println("All threads completed.");
    }

    private static class WorkerThread extends Thread {
        WorkerThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println("Thread subclass running on " + getName());
        }
    }
}
