package com.vmanolache.httpserver.api.constants;

import lombok.Getter;

/**
 * List of HTTP status codes.
 *
 * Created by Vlad Manolache on 2018-09-23.
 */
public enum StatusCode {
	CONTINUE(100, "Continue"),

	OK(200, "OK"),
	CREATED(201, "Created"),
	NO_CONTENT(204, "No content"),

	MOVED_PERMANENTLY(301, "Moved permanently"),
	NOT_MODIFIED(304, "No content"),

	BAD_REQUEST(400, "Bad request"),
	UNAUTHORIZED(401, "Unauthorized"),
	PAYMENT_REQUIRED(402, "Payment required"),
	FORBIDDEN(403, "Forbidden"),
	NOT_FOUND(404, "Not found"),
	CONFLICT(409, "Conflict"),
	EXPECTATION_FAILED(417, "Expectation failed"),

	INTENRAL_SERVER_ERROR(500, "Internal server error"),
	NOT_IMPLEMENTED(501, "Not implemented"),
	HTTP_VERSION_NOT_SUPPORTED(505, "Http version not implemented");

	@Getter
	private int code;

	@Getter
	private String message;

	StatusCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

}
