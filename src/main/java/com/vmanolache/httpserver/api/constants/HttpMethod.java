package com.vmanolache.httpserver.api.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.Getter;

/**
 * List of HTTP methods.
 *
 * Created by Vlad Manolache on 2018-09-23.
 */
public enum HttpMethod {
	GET("GET"),
	POST("POST"),
	PUT("PUT"),
	DELETE("DELETE"),
	HEAD("HEAD"),
	TRACE("TRACE"),
	OPTIONS("OPTIONS");

	@Getter
	private String name;

	HttpMethod(String name) {
		this.name = name;
	}

	public static Optional<HttpMethod> safeValueOf(String input) {
		return list().stream().filter(type -> type.getName().equals(input)).findFirst();
	}

	public static List<HttpMethod> list() {
		return new ArrayList<>(Arrays.asList(GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS));
	}

	@Override
	public String toString() {
		return this.name;
	}

}
