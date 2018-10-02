package com.vmanolache.httpserver;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmanolache.httpserver.api.HttpServer;
import com.vmanolache.httpserver.api.HttpServerBuilder;
import com.vmanolache.httpserver.api.constants.*;
import com.vmanolache.httpserver.utils.FileUtils;
import com.vmanolache.httpserver.utils.HttpUtils;
import com.vmanolache.httpserver.utils.TestUtils;
import org.junit.jupiter.api.*;

import com.vmanolache.httpserver.api.ServerConfig;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Vlad Manolache on 2018-09-21.
 */
class HttpServerImplIntegrationTest {

	private static int randomPort;

	private static HttpServer httpServer;

	private static ExecutorService executorService;

	private static final String localPath = "api/test/devices.json";
    private static final String relativePath = "api/test/devices.json";
    private static final String urlPath = "/api/test/devices.json";
    private static final String baseUrl = "http://localhost:";

	private List<String> deviceList;

	@BeforeAll
    static void setup() {
        executorService = Executors.newCachedThreadPool();

        randomPort = ThreadLocalRandom.current().nextInt(0, 60000);
        ServerConfig serverConfig = new ServerConfig(".", randomPort);
        HttpServerBuilder httpServerBuilder = new HttpServerBuilder();
        httpServer = httpServerBuilder.build(serverConfig);
    }

    @BeforeEach
    void populateStore() {
        deviceList = new ArrayList<>();
        deviceList.add("device 1");
        deviceList.add("device 2");
        deviceList.add("device 3");
        File file = new File(localPath);
        ObjectMapper mapper = new ObjectMapper();
        byte[] result;
        try {
            result = mapper.writeValueAsBytes(deviceList);
            boolean success = FileUtils.writeToFileWithLock(file, result);
            if (!success) {
                fail("Should not reach this point");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Should not throw an exception");
        }
    }

    @AfterAll
    static void teardown() {
        stopServer();
    }

    @Test
    @DisplayName("Execute GET and expect 200")
    void test_request_get_ok() {
        TestUtils.executeTest(executorService, true, () -> {
            startServer();

            try {
                URL url = new URL(baseUrl + randomPort + urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = executeRequest(urlConnection, HttpMethod.GET, null);

                boolean success = HttpUtils.validateGetResponse200(urlConnection, response, deviceList);

                urlConnection.disconnect();
                return success;

            } catch (IOException e) {
                return false;
            }
        });
    }

    @Test
    @DisplayName("Execute HEAD and expect 200")
    void test_request_head_ok() {
        TestUtils.executeTest(executorService, true, () -> {
            startServer();

            try {
                URL url = new URL(baseUrl + randomPort + urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = executeRequest(urlConnection, HttpMethod.HEAD, null);

                boolean success = HttpUtils.validateHeadResponse200(urlConnection, response);

                urlConnection.disconnect();
                return success;

            } catch (IOException e) {
                return false;
            }
        });
    }

    @Test
    @DisplayName("Execute PUT and expect 200")
    void test_request_put_ok() {
        TestUtils.executeTest(executorService, true, () -> {
            startServer();

            ArrayList<String> newDeviceList = new ArrayList<>();
            newDeviceList.add("device 4");
            newDeviceList.add("device 5");
            newDeviceList.add("device 6");
            try {
                ObjectMapper mapper = new ObjectMapper();
                byte[] result = mapper.writeValueAsBytes(newDeviceList);
                URL url = new URL(baseUrl + randomPort + urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = executeRequest(urlConnection, HttpMethod.PUT, result);

                boolean success = HttpUtils.validatePutResponse200(urlConnection, response);

                urlConnection.disconnect();
                return success;

            } catch (IOException e) {
                return false;
            }
        });
    }

    @Test
    @DisplayName("Execute DELETE and expect 200")
    void test_request_delete_ok() {
        TestUtils.executeTest(executorService, true, () -> {
            startServer();

            try {
                URL url = new URL(baseUrl + randomPort + urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = executeRequest(urlConnection, HttpMethod.DELETE, null);

                boolean success = HttpUtils.validateDeleteResponse200(urlConnection, response);

                urlConnection.disconnect();
                return success;

            } catch (IOException e) {
                return false;
            }
        });
    }

    @Test
    @DisplayName("Execute DELETE when the file does not exist and expect 404")
    void test_request_delete_not_found() {
        TestUtils.executeTest(executorService, false, () -> {
            startServer();

            try {
                URL url = new URL(baseUrl + randomPort + urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = executeRequest(urlConnection, HttpMethod.DELETE, null);

                File file = new File(localPath);
                if (!file.delete()) {
                    return false;
                }

                boolean success = HttpUtils.validateDeleteResponse404(urlConnection, response);

                urlConnection.disconnect();
                return success;

            } catch (IOException e) {
                return false;
            }
        });
    }

    @Test
    @DisplayName("Execute TRACE and expect 200")
    void test_request_trace_ok() {
        TestUtils.executeTest(executorService, true, () -> {
            startServer();

            try {
                URL url = new URL(baseUrl + randomPort + urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = executeRequest(urlConnection, HttpMethod.TRACE, null);

                boolean success = HttpUtils.validateTraceResponse200(urlConnection, relativePath, response);

                urlConnection.disconnect();
                return success;

            } catch (IOException e) {
                return false;
            }
        });
    }

    @Test
    @DisplayName("Execute OPTIONS and expect 200")
    void test_request_options_ok() {
        TestUtils.executeTest(executorService, true, () -> {
            startServer();

            try {
                URL url = new URL(baseUrl + randomPort + urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = executeRequest(urlConnection, HttpMethod.OPTIONS, null);

                boolean success = HttpUtils.validateOptionsResponse200(urlConnection, response);

                urlConnection.disconnect();
                return success;

            } catch (IOException e) {
                return false;
            }
        });
    }

    @Test
    @DisplayName("Stress Test - Execute 50 GET requests")
    void test_get_50_stress_test() {
        TestUtils.executeTest(executorService, true, () -> {
            startServer();

            try {
                int requestCount = 50;
                int successfullRequests = 0;
                for (int i = 0; i < requestCount; i++) {
                    URL url = new URL(baseUrl + randomPort + urlPath);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    executeRequest(urlConnection, HttpMethod.GET, null);
                    urlConnection.disconnect();
                    if (urlConnection.getResponseCode() == StatusCode.OK.getCode()) {
                        successfullRequests++;
                    }
                }

                return successfullRequests == requestCount;

            } catch (IOException e) {
                return false;
            }
        });
    }

    private String executeRequest(HttpURLConnection urlConnection, HttpMethod httpMethod, byte[] body) {
        StringBuffer response;
        try {
            if (httpMethod.equals(HttpMethod.GET)) {
                urlConnection.setDoInput(true);
            }
            urlConnection.setRequestMethod(httpMethod.getName());
            urlConnection.setRequestProperty("User-Agent", "unit test");
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            if (body != null) {
                urlConnection.setDoOutput(true);
            }

            urlConnection.connect();

            if (body != null) {
                urlConnection.getOutputStream().write(body);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            urlConnection.disconnect();

        } catch (IOException e) {
            return "";
        }
        return response.toString();
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
