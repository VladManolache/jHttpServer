package com.vmanolache.httpserver;

import com.vmanolache.httpserver.api.HttpServer;
import com.vmanolache.httpserver.api.HttpServerBuilder;
import com.vmanolache.httpserver.api.ServerConfig;
import com.vmanolache.httpserver.utils.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

class HttpServerUnitTest {

    private static HttpServer httpServer;

    private static ExecutorService executorService;

    @BeforeAll
    static void setup() {
        executorService = Executors.newCachedThreadPool();

        int randomPort = ThreadLocalRandom.current().nextInt(0, 60000);
        ServerConfig serverConfig = new ServerConfig(".", randomPort);
        HttpServerBuilder httpServerBuilder = new HttpServerBuilder();
        httpServer = httpServerBuilder.build(serverConfig);
    }

    @AfterAll
    static void teardown() {
        stopServer();
    }

    @Test
    @DisplayName("Simple start stop server")
    void test_start_stop_http_server() {
        TestUtils.executeTest(executorService, true, () -> {
            startServer();
            stopServer();
            return true;
        });
    }

    @Test
    @DisplayName("Start server multiple times")
    void test_start_server_multiple_times() {
        TestUtils.executeTest(executorService, true, () -> {
            startServer();
            startServer();
            startServer();
            startServer();
            stopServer();
            return true;
        });
    }

    @Test
    @DisplayName("Start and stop the server multiple times")
    void test_start_stop_server_multiple_times() {
        TestUtils.executeTest(executorService, true, () -> {
            startServer();
            stopServer();
            startServer();
            startServer();
            stopServer();
            stopServer();
            startServer();
            stopServer();
            return true;
        });
    }

    private static void startServer() {
        new Thread(() -> {
            try {
                httpServer.start();
            } catch (IOException e) {
                throw new RuntimeException("Should not throw an exception");
            }
        }).start();
    }

    private static void stopServer() {
        new Thread(() -> {
            try {
                httpServer.stop();
            } catch (IOException e) {
                throw new RuntimeException("Should not throw an exception");
            }
        }).start();
    }

}
