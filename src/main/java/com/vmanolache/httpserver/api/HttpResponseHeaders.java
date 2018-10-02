package com.vmanolache.httpserver.api;

import com.vmanolache.httpserver.api.constants.HttpHeader;
import com.vmanolache.httpserver.api.constants.HttpVersion;
import com.vmanolache.httpserver.api.constants.MediaType;
import com.vmanolache.httpserver.api.constants.StatusCode;
import lombok.Setter;

/**
 * Created by Vlad Manolache on 2018-09-21.
 */
public final class HttpResponseHeaders {

	private final HttpVersion httpVersion;

	@Setter
	private StatusCode statusCode;

	@Setter
	private MediaType contentType;

	@Setter
	private String allowContent;

	@Setter
	private String contentLength;

	@Setter
	private String connection;

	public HttpResponseHeaders(Request request) {
		this.httpVersion = request.getRequestLine().getHttpVersion();
		this.statusCode = StatusCode.OK;
		this.contentType = MediaType.TEXT_PLAIN;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String statusMessage = "OK";

		sb.append(String.format("%s %d %s\n", httpVersion, statusCode.getCode(), statusMessage));
		sb.append(HttpHeader.CONTENT_TYPE).append(": ").append(contentType.getType()).append("\n");
		if (allowContent != null) {
			sb.append(HttpHeader.ALLOW).append(": ").append(allowContent).append("\n");
		}
		if (contentLength != null) {
			sb.append(HttpHeader.CONTENT_LENGTH).append(": ").append(contentLength).append("\n");
		}
		if (connection != null) {
			sb.append(HttpHeader.CONNECTION).append(": ").append(connection).append("\n");
		}

		return sb.toString();
	}

}

