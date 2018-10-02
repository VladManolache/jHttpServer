package com.vmanolache.httpserver.api;

import java.io.IOException;

/**
 * Encapsulates an HTTP response. Also responsible for sending the data to the OutputStream.
 */
public interface Response {

    boolean isDiscardBody();

    HttpResponseHeaders getHeaders();

	void setHeaders(HttpResponseHeaders httpResponseHeaders);

	void setBody(byte[] body);

	void send() throws IOException;

}
