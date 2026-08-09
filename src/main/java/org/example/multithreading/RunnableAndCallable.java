package org.example.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates the difference between Runnable and Callable when using an ExecutorService.
 *
 * Use Runnable for fire-and-forget tasks that do not return a result. Use Callable when the
 * task needs to return a value or throw a checked exception.
 */
public class RunnableAndCallable {

    private static final Logger logger = LoggerFactory.getLogger(RunnableAndCallable.class);

    public static void main(String[] args) {
        try {
            String result = getValueFromFuture();
            logger.info(result);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public static String getValueFromFuture() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        try {
            Runnable runnableTask = () -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                    logger.info("Runnable task completed without a result.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Runnable was interrupted", e);
                }
            };

            Callable<String> callableTask = () -> {
                TimeUnit.MILLISECONDS.sleep(300);
                return "Callable result returned";
            };

            List<Callable<String>> callableTasks = new ArrayList<>();
            callableTasks.add(callableTask);
            callableTasks.add(callableTask);
            callableTasks.add(callableTask);

            // Runnable is used when we only need to execute work and do not need a response.
            executorService.execute(runnableTask);

            // Callable is used when tasks must return values that the main thread depends on.
            StringBuilder stringBuilder = new StringBuilder();
            for (Callable<String> callable : callableTasks) {
                Future<String> future = executorService.submit(callable);
                stringBuilder.append(future.get());
                stringBuilder.append(" | ");
            }
            return stringBuilder.toString();
        } catch (Exception ex) {
            logger.error("Error executing tasks", ex);
            throw new RuntimeException(ex);
        } finally {
            executorService.shutdown();
        }
    }

}
