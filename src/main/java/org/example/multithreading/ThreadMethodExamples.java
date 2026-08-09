
package org.example.multithreading;

/**
 * Interview-oriented demo of Thread methods in a realistic order-processing workflow.
 *
 * This class is written like a Java tech lead would explain it:
 * - start() / run(): delegate work to background threads
 * - join(): wait for dependent tasks before making a business decision
 * - sleep(): add retry/backoff behavior for flaky integrations
 * - interrupt(): cancel long-running work gracefully
 * - isAlive(): monitor progress of asynchronous operations
 * - setName(): make logs and monitoring meaningful
 * - setDaemon(): run background housekeeping without blocking shutdown
 * - yield(): give other threads a chance in a cooperative loop
 */
public class ThreadMethodExamples {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Real-world scenario: checkout workflow ===");

        System.out.println("1) start() vs run() - launching validation work asynchronously");
        Thread validationThread = new Thread(new OrderValidationTask(), "order-validation");
        validationThread.start();
        validationThread.join();

        System.out.println("\n2) join() - wait for inventory and payment tasks before confirming the order");
        Thread inventoryThread = new Thread(new InventoryCheckTask(), "inventory-check");
        Thread paymentThread = new Thread(new PaymentCheckTask(), "payment-check");
        inventoryThread.start();
        paymentThread.start();
        inventoryThread.join();
        paymentThread.join();
        System.out.println("Order can now be confirmed because inventory and payment checks are complete.");

        System.out.println("\n3) sleep() - retry payment gateway calls with backoff");
        Thread retryThread = new Thread(new PaymentRetryTask(), "payment-retry");
        retryThread.start();
        retryThread.join();

        System.out.println("\n4) interrupt() - cancel a long-running report export if the user abandons it");
        Thread exportThread = new Thread(new ReportExportTask(), "report-export");
        exportThread.start();
        Thread.sleep(400);
        exportThread.interrupt();
        exportThread.join();

        System.out.println("\n5) isAlive() and setName() - monitor an async batch sync job");
        Thread batchSyncThread = new Thread(new BatchSyncTask(), "batch-sync");
        batchSyncThread.setName("batch-sync-worker");
        batchSyncThread.start();
        while (batchSyncThread.isAlive()) {
            System.out.println("Batch sync still running...");
            Thread.sleep(200);
        }

        System.out.println("\n6) setDaemon() - background metrics flush should not keep the JVM alive");
        Thread metricsThread = new Thread(new MetricsFlushTask(), "metrics-flush");
        metricsThread.setDaemon(true);
        metricsThread.start();
        Thread.sleep(300);
        System.out.println("Main application thread can now finish; daemon thread will stop with the JVM.");

        System.out.println("\n7) yield() - cooperative scheduling in a low-priority heartbeat loop");
        Thread heartbeatThread = new Thread(new HeartbeatTask(), "heartbeat");
        heartbeatThread.start();
        heartbeatThread.join();

        System.out.println("\nAll business workflow examples completed.");
    }

    private static class OrderValidationTask implements Runnable {
        @Override
        public void run() {
            System.out.println("Validating order details in " + Thread.currentThread().getName());
        }
    }

    private static class InventoryCheckTask implements Runnable {
        @Override
        public void run() {
            System.out.println("Checking stock availability in " + Thread.currentThread().getName());
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static class PaymentCheckTask implements Runnable {
        @Override
        public void run() {
            System.out.println("Authorizing payment in " + Thread.currentThread().getName());
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static class PaymentRetryTask implements Runnable {
        @Override
        public void run() {
            for (int attempt = 1; attempt <= 3; attempt++) {
                System.out.println("Payment attempt " + attempt + " for " + Thread.currentThread().getName());
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println("Payment retry workflow finished.");
        }
    }

    private static class ReportExportTask implements Runnable {
        @Override
        public void run() {
            int step = 0;
            while (!Thread.currentThread().isInterrupted() && step < 10) {
                System.out.println("Exporting report chunk " + step);
                step++;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Report export cancelled by the user.");
            } else {
                System.out.println("Report export completed.");
            }
        }
    }

    private static class BatchSyncTask implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 3; i++) {
                System.out.println("Syncing batch " + i + "...");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println("Batch sync finished.");
        }
    }

    private static class MetricsFlushTask implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Flushing metrics in the background");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private static class HeartbeatTask implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 4; i++) {
                System.out.println("Heartbeat tick " + i + " for a low-priority monitor");
                Thread.yield();
            }
            System.out.println("Heartbeat loop completed.");
        }
    }
}
