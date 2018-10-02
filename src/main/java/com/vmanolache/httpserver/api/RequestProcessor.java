package com.vmanolache.httpserver.api;

import com.vmanolache.httpserver.api.constants.HttpMethod;

import java.nio.file.Path;

/**
 * Responsible for processing a request given that the path and method match.
 *
 * @see Request
 * @see Response
 */
public abstract class RequestProcessor {

	private final Path path;
	private final HttpMethod httpMethod;

	public RequestProcessor(Path path) {
		this(path, null);
	}

	public RequestProcessor(Path path, HttpMethod httpMethod) {
		this.path = path;
		this.httpMethod = httpMethod;
	}

    /**
     * Process the current request and update the response.
     *
     * @apiNote Method should be synchronized to avoid a race condition.
     */
	public abstract boolean process(Request request, Response response);

	/**
	 * Decide if the current request can be processed.
	 */
	public synchronized final boolean canProcessRequest(Request request) {
		boolean pathMatches =
				path == null || path.equals(request.getRequestLine().getPath());
		boolean httpMethodMatches =
				httpMethod == null || httpMethod.equals(request.getRequestLine().getMethod());
		return pathMatches && httpMethodMatches;
	}

}
