package com.vmanolache.httpserver.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import com.vmanolache.httpserver.api.*;
import com.vmanolache.httpserver.api.HttpResponseHeaders;

import com.vmanolache.httpserver.api.constants.HttpMethod;
import com.vmanolache.httpserver.api.constants.MediaType;
import lombok.extern.log4j.Log4j2;

/**
 * Created by Vlad Manolache on 2018-09-22.
 */
@Log4j2
public class DirectoryListing extends RequestProcessor {

	public DirectoryListing() {
		super(null, HttpMethod.GET);
	}

	@Override
	public synchronized boolean process(Request request, Response response) {
		final Path directory = request.getRequestLine().getPath();
		if (!Files.isDirectory(directory)) {
			return false;
		}
		log.debug("Will serve directory at path " + request.getRequestLine().getPath());

		final HttpResponseHeaders responseHeaders = new HttpResponseHeaders(request);
		responseHeaders.setContentType(MediaType.HTML_TEXT);
		response.setHeaders(responseHeaders);

		try {
			String body = String.format("<!DOCTYPE html>%n"
					+ "<html><head><title>Index of %s</title></head>%n"
					+ "<body><h1>Index of %s</h1>%n", directory, directory);

			if (!directory.toString().isEmpty()) {
				body += buildHTMLEntry("..", directory);
			}

			body += Files.list(directory)
					.map(Path::getFileName)
					.map(Path::toString)
					.map(s -> buildHTMLEntry(s, directory))
					.collect(Collectors.joining());

			body += "</pre></body></html>";

			response.setBody(body.getBytes());
			response.send();

		} catch (IOException e) {
			return false;
		}

		return true;
	}

	private String buildHTMLEntry(String fileName, Path uri) {
		Path absoluteUri = uri.toAbsolutePath().normalize();
		Path result = absoluteUri.relativize(Paths.get(".").toAbsolutePath()).resolve(uri).normalize();
		return String.format("<li>\n<a href=\"%s/%s\">%s</a>\n</li>\n", result, fileName, fileName);
	}

}
