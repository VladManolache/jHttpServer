package com.vmanolache.httpserver.api;

import java.nio.file.Path;

import com.vmanolache.httpserver.api.constants.HttpMethod;
import com.vmanolache.httpserver.api.constants.HttpVersion;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * First line of a HTTP request.
 */
@Data
@AllArgsConstructor
public class RequestLine {

	private HttpMethod method;

	private Path path;

	private HttpVersion httpVersion;

}
