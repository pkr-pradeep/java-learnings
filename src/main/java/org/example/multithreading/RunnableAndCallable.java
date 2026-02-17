package org.example.multithreading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class RunnableAndCallable {

    private static final Logger logger = LoggerFactory.getLogger(RunnableAndCallable.class);

    public static void main(String[] args) {
        try {
            String getValueFromThread = getValueFromFuture();
            logger.info(getValueFromThread);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public static String getValueFromFuture() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {

            Runnable runnableTask = () -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            };

            Callable<String> callableTask = () -> {
                TimeUnit.MILLISECONDS.sleep(300);
                return "Task's execution";
            };

            List<Callable<String>> callableTasks = new ArrayList<>();
            callableTasks.add(callableTask);
            callableTasks.add(callableTask);
            callableTasks.add(callableTask);

            //This is for Runnable Object
            executorService.execute(runnableTask);

            StringBuilder stringBuilder = new StringBuilder();
            for (Callable<String> callable : callableTasks) {
                //This is for callable object
                Future<String> future =
                        executorService.submit(callable);
                stringBuilder.append(future.get());
            }
            return stringBuilder.toString();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        } finally {
            executorService.shutdown();
        }

        return null;
    }

}
