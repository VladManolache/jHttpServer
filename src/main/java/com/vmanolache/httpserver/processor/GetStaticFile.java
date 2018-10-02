package com.vmanolache.httpserver.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.vmanolache.httpserver.api.*;

import com.vmanolache.httpserver.api.constants.HttpMethod;
import com.vmanolache.httpserver.api.constants.MediaType;
import lombok.extern.log4j.Log4j2;

/**
 * Created by Vlad Manolache on 2018-09-21.
 */
@Log4j2
public class GetStaticFile extends RequestProcessor {

	public GetStaticFile() {
		super(null, HttpMethod.GET);
	}

	@Override
	public synchronized boolean process(Request request, Response response) {
		final Path file = request.getRequestLine().getPath();
		if (!Files.isRegularFile(file)) {
			return false;
		}

		log.debug("Will GET file at path " + request.getRequestLine().getPath());

		try {
			if (!response.isDiscardBody()) {
				byte[] content = Files.readAllBytes(file);
				response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
				response.setBody(content);
			}
			response.send();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

}
