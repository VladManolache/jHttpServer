package com.vmanolache.httpserver.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.vmanolache.httpserver.api.Handler;
import com.vmanolache.httpserver.api.Request;
import com.vmanolache.httpserver.api.Response;
import com.vmanolache.httpserver.processor.*;
import com.vmanolache.httpserver.api.RequestProcessor;

/**
 * Created by Vlad Manolache on 2018-09-28.
 */
public class WebHandler implements Handler {

	private final List<RequestProcessor> processors;

	public WebHandler() {
		processors = new LinkedList<>();
		processors.add(new DirectoryIndex());
		processors.add(new DirectoryListing());
		processors.add(new GetStaticFile());
		processors.add(new PutStaticFile());
		processors.add(new DeleteStaticFile());
		processors.add(new ResourceNotFound());
	}

	@Override
	public boolean dispatch(Request request, Response response) {
        Optional<RequestProcessor> processor = processors
                .stream()
                .filter(p -> p.canProcessRequest(request))
                .filter(p -> p.process(request, response))
                .findFirst();

        return processor.isPresent();
	}
}
