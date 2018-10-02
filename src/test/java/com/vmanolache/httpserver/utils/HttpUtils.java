package com.vmanolache.httpserver.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vmanolache.httpserver.api.constants.HttpHeader;
import com.vmanolache.httpserver.api.constants.HttpMethod;
import com.vmanolache.httpserver.api.constants.HttpVersion;
import com.vmanolache.httpserver.api.constants.StatusCode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.join;

public class HttpUtils {

    public static boolean validateGetResponse200(
            HttpURLConnection urlConnection, String response, List<String> list) throws IOException {

        if (urlConnection.getResponseCode() != StatusCode.OK.getCode()) {
            return false;
        }
        if (response.length() == 0) {
            return false;
        }
        if (!responseMatchesExpected(response.getBytes(), list)) {
            return false;
        }
        return true;
    }

    private static boolean responseMatchesExpected(byte[] response, List<String> expectedResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        List<String> devices = mapper.readValue(
                response, typeFactory.constructCollectionType(LinkedList.class, String.class));
        return devices.equals(expectedResponse);
    }

    public static boolean validateHeadResponse200(
            HttpURLConnection urlConnection, String response) throws IOException {

        if (urlConnection.getResponseCode() != StatusCode.OK.getCode()) {
            return false;
        }
        return response.length() == 0;
    }

    public static boolean validatePutResponse200(
            HttpURLConnection urlConnection, String response) throws IOException {

        if (urlConnection.getResponseCode() != StatusCode.OK.getCode()) {
            return false;
        }
        return response.length() == 0;
    }

    public static boolean validateDeleteResponse200(
            HttpURLConnection urlConnection, String response) throws IOException {

        if (urlConnection.getResponseCode() != StatusCode.OK.getCode()) {
            return false;
        }
        return response.length() == 0;
    }

    public static boolean validateDeleteResponse404(
            HttpURLConnection urlConnection, String response) throws IOException {

        if (urlConnection.getResponseCode() != StatusCode.NOT_FOUND.getCode()) {
            return false;
        }
        return response.length() == 0;
    }

    public static boolean validateTraceResponse200(
            HttpURLConnection urlConnection, String relativePath, String response) throws IOException {

        if (urlConnection.getResponseCode() != StatusCode.OK.getCode()) {
            return false;
        }
        String expectedResponse = HttpMethod.TRACE + " " + relativePath + " " + HttpVersion.HTTP11;
        return response.equals(expectedResponse);
    }

    public static boolean validateOptionsResponse200(
            HttpURLConnection urlConnection, String response) throws IOException {

        if (urlConnection.getResponseCode() != StatusCode.OK.getCode()) {
            return false;
        }
        if (response.length() != 0) {
            return false;
        }
        String allowedMethods = urlConnection.getHeaderField(HttpHeader.ALLOW.getName());
        List<String> expectedAllowedMethods = HttpMethod.list().stream()
                .map(HttpMethod::getName)
                .collect(Collectors.toList());

        return allowedMethods.equals(join(", ", expectedAllowedMethods));
    }

}
