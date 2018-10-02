package com.vmanolache.httpserver.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {

    public static void executeTest(ExecutorService executorService, boolean expectSuccess, Callable callable) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        executorService.submit(() -> {
            try {
                boolean result = (boolean) callable.call();
                future.complete(result);
            } catch (Exception e) {
                future.complete(false);
            }
        });

        try {
            boolean result = future.get();
            assertEquals(expectSuccess, result);
        } catch (InterruptedException | ExecutionException e) {
            fail("Should not throw an exception");
        }
    }

}
