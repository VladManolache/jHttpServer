package com.vmanolache.httpserver.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.vmanolache.httpserver.api.Handler;
import com.vmanolache.httpserver.api.Request;
import com.vmanolache.httpserver.api.RequestProcessor;
import com.vmanolache.httpserver.api.Response;

import com.vmanolache.httpserver.api.PathResolver;
import lombok.Getter;

/**
 * Created by Vlad Manolache on 2018-09-28.
 */
public class HttpHandler implements Handler {

	@Getter
	private final PathResolver pathResolver;

	private final List<RequestProcessor> processors;

	public HttpHandler(PathResolver pathResolver, List<RequestProcessor> processorList) {
		this.pathResolver = pathResolver;

		processors = new LinkedList<>();
		if (processorList != null) {
			processors.addAll(processorList);
		}
	}

	@Override
	public synchronized boolean dispatch(Request request, Response response) {
		Optional<RequestProcessor> processor = processors
				.stream()
				.filter(p -> p.canProcessRequest(request))
				.findFirst();

		return processor.isPresent() && processor.get().process(request, response);
	}
}
