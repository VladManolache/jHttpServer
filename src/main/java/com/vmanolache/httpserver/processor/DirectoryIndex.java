package com.vmanolache.httpserver.processor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import com.vmanolache.httpserver.api.Request;
import com.vmanolache.httpserver.api.RequestProcessor;
import com.vmanolache.httpserver.api.constants.HttpMethod;
import com.vmanolache.httpserver.api.Response;

import lombok.extern.log4j.Log4j2;

/**
 * Created by Vlad Manolache on 2018-09-22.
 */
@Log4j2
public class DirectoryIndex extends RequestProcessor {

	private static final String[] INDEX_FILE_NAMES = new String[] { "index.html", "index.htm" };

	public DirectoryIndex() {
		super(null, HttpMethod.GET);
	}

	@Override
	public synchronized boolean process(Request request, Response response) {
		final Path directory = request.getRequestLine().getPath();
		if (!Files.isDirectory(directory)) {
			return false;
		}

		log.debug("Will serve index file at path " + request.getRequestLine().getPath());

		Optional<Path> path = Arrays.stream(INDEX_FILE_NAMES)
				.map(directory::resolve)
				.filter(Files::exists)
				.findFirst();

		if (path.isPresent()) {
			GetStaticFile getStaticFile = new GetStaticFile();
			Path originalPath = request.getRequestLine().getPath();
			request.getRequestLine().setPath(Paths.get(originalPath.toString() + path.get()));
			return getStaticFile.process(request, response);
		}

		return false;
	}

}
