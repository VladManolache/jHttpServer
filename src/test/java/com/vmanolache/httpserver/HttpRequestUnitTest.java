package com.vmanolache.httpserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.vmanolache.httpserver.api.constants.HttpMethod;
import com.vmanolache.httpserver.api.constants.HttpVersion;
import com.vmanolache.httpserver.request.HttpRequest;
import com.vmanolache.httpserver.api.RequestLine;
import com.vmanolache.httpserver.api.PathResolver;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Vlad Manolache on 2018-09-21.
 */
class HttpRequestUnitTest {

	@Test
    @DisplayName("Parse GET request")
    void test_parse_get_request() {
		HttpMethod method = HttpMethod.GET;
		String path = "/aaa/bbb?firstName=John&secondName=Doe";
		String expectedPath = "/aaa/bbb";
		HttpVersion httpVersion = HttpVersion.HTTP11;
		String requestLine = method + " " + path + " " + httpVersion;

		Map<String, String> expectedParams = new HashMap<>();
		expectedParams.put("firstName", "John");
		expectedParams.put("secondName", "Doe");

		RequestLine expectedRequestLine = new RequestLine(method, Paths.get(expectedPath), httpVersion);
		InputStream is = new ByteArrayInputStream(requestLine.getBytes(StandardCharsets.UTF_8));
		try {
			PathResolver pathResolver = new PathResolver("/");
			HttpRequest request = new HttpRequest(is, pathResolver);
			assertEquals(request.getRequestLine(), expectedRequestLine);
			assertEquals(request.getParams(), expectedParams);
			assertEquals(request.getHeaders(), new HashMap<>());
		} catch (IOException e) {
			fail("Should not throw an exception");
		}
	}

	@Test
    @DisplayName("Parse POST request")
    void test_parse_post_request() {
		HttpMethod method = HttpMethod.POST;
		String path = "/";
		HttpVersion httpVersion = HttpVersion.HTTP11;
		String requestLine = method + " " + path + " " + httpVersion;
		String requestBody = "say=hello&to=world";

		String requestString = requestLine + "\n"
				+ "Host: foo.com\n"
				+ "Content-Type: application/x-www-form-urlencoded\n"
				+ "Content-Length: " + requestBody.length() + "\n"
				+ "\n"
				+ requestBody;

		RequestLine expectedRequestLine = new RequestLine(method, Paths.get(path), httpVersion);
		Map<String, String> expectedHeaders = new HashMap<>();
		expectedHeaders.put("Host", "foo.com");
		expectedHeaders.put("Content-Length", requestBody.length() + "");
		expectedHeaders.put("Content-Type", "application/x-www-form-urlencoded");

		InputStream is = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		try {
			PathResolver pathResolver = new PathResolver("/");
			HttpRequest request = new HttpRequest(is, pathResolver);
			assertEquals(request.getRequestLine(), expectedRequestLine);
			assertEquals(request.getParams(), new HashMap<>());
			assertEquals(request.getHeaders(), expectedHeaders);
			assertTrue(Arrays.equals(request.getBody(), requestBody.getBytes()));
		} catch (IOException e) {
			fail("Should not throw an exception");
		}
	}

}
