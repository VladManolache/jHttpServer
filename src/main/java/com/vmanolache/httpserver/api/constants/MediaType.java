package com.vmanolache.httpserver.api.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.Getter;

/**
 * List of HTTP media types.
 *
 * Created by Vlad Manolache on 2018-09-23.
 */
public enum MediaType {
	TEXT_PLAIN("text/plain"),
	APPLICATION_JSON("application/json"),
	HTML_TEXT("text/html"),
	MESSAGE_HTML("message/html"),
	APPLICATION_OCTET_STREAM("application/octet_stream");

	@Getter
	private String type;

	MediaType(String type) {
		this.type = type;
	}

	public static Optional<MediaType> safeValueOf(String input) {
		List<MediaType> types = new ArrayList<>(Arrays.asList(TEXT_PLAIN, HTML_TEXT, MESSAGE_HTML));
		return types.stream()
				.filter(type -> type.getType().equals(input))
				.findFirst();
	}

	@Override
	public String toString() {
		return this.type;
	}

}
