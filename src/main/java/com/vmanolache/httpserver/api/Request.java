package com.vmanolache.httpserver.api;

import java.util.Map;

/**
 * Encapsulates the contents of an HTTP request.
 */
public interface Request {

	RequestLine getRequestLine();

	Map<String, String> getHeaders();

	Map<String, String> getParams();

	byte[] getBody();

}
