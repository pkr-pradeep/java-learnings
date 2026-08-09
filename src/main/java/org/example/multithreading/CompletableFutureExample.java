package org.example.multithreading;

import java.util.concurrent.CompletableFuture;

/**
 * Shows a real use case for CompletableFuture: running independent operations in parallel
 * and combining their results once both are available.
 *
 * Use CompletableFuture when you want asynchronous, non-blocking composition of results,
 * especially for independent service calls or IO-bound operations.
 */
public class CompletableFutureExample {

    public static void main(String[] args) {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
            sleep(200);
            return "User details loaded";
        });

        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "Account balance loaded";
        });

        CompletableFuture<String> combined = futureA.thenCombine(futureB,
                (userDetails, accountBalance) -> "Combined payload: [" + userDetails + "] & [" + accountBalance + "]");

        // Use join() when the main thread needs the final result after async composition.
        System.out.println("Waiting for combined result...");
        System.out.println(combined.join());
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
