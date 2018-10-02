package com.vmanolache.httpserver.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.vmanolache.httpserver.api.Request;
import com.vmanolache.httpserver.api.RequestProcessor;
import com.vmanolache.httpserver.api.Response;
import com.vmanolache.httpserver.api.constants.StatusCode;
import com.vmanolache.httpserver.api.HttpResponseHeaders;

import lombok.extern.log4j.Log4j2;

/**
 * Created by Vlad Manolache on 2018-09-22.
 */
@Log4j2
public class ResourceNotFound extends RequestProcessor {

	public ResourceNotFound() {
		super(null);
	}

	@Override
	public synchronized boolean process(Request request, Response response) {
		final Path path = request.getRequestLine().getPath();
		if (Files.exists(path)) {
			return false;
		}

		log.debug("Resource not found at path " + request.getRequestLine().getPath());

		HttpResponseHeaders responseHeaders = new HttpResponseHeaders(request);
		responseHeaders.setStatusCode(StatusCode.NOT_FOUND);
		response.setHeaders(responseHeaders);

		String body = String.format("Resource %s not found at path ", request.getRequestLine().getPath());
		response.setBody(body.getBytes());
		try {
			response.send();
		} catch (IOException e) {
			return false;
		}

		return true;
	}
}
