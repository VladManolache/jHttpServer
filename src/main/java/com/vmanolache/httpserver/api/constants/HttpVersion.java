package com.vmanolache.httpserver.api.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.Getter;

/**
 * List of HTTP version.
 *
 * Created by Vlad Manolache on 2018-09-24.
 */
public enum HttpVersion {
	HTTP11("HTTP/1.1"),
	HTTP10("HTTP/1.0"),
	HTTP09("HTTP/0.9");

	@Getter
	private String name;

	HttpVersion(String name) {
		this.name = name;
	}

	public static Optional<HttpVersion> safeValueOf(String input) {
		List<HttpVersion> types = new ArrayList<>(Arrays.asList(HTTP11, HTTP10, HTTP09));
		return types.stream()
				.filter(type -> type.getName().equals(input))
				.findFirst();
	}

	@Override
	public String toString() {
		return this.name;
	}

}
