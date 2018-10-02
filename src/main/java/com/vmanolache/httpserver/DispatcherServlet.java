package com.vmanolache.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import com.vmanolache.httpserver.api.Request;
import com.vmanolache.httpserver.api.Handler;
import com.vmanolache.httpserver.api.Response;
import com.vmanolache.httpserver.api.constants.HttpMethod;
import com.vmanolache.httpserver.handler.HttpHandler;
import com.vmanolache.httpserver.handler.WebHandler;
import com.vmanolache.httpserver.request.HttpRequest;
import com.vmanolache.httpserver.response.HttpResponse;
import com.vmanolache.httpserver.api.PathResolver;

import lombok.extern.log4j.Log4j2;

/**
 * Handles incoming socket connections, processes the request and dispatches the request to a handler.
 * Alternatively, if the request is not valid, it handles the error case.
 *
 * @see Socket
 * @see com.vmanolache.httpserver.api.RequestProcessor
 * @see Handler
 */
@Log4j2
public final class DispatcherServlet implements Runnable {

	private final Socket socket;

	private final List<Handler> handlerList;

	private final PathResolver pathResolver;

	private final HttpProcessor httpProcessor;

	DispatcherServlet(Socket socket, List<com.vmanolache.httpserver.api.RequestProcessor> processorList, PathResolver pathResolver) {
		this.socket = socket;
		this.pathResolver = pathResolver;
		this.httpProcessor = new HttpProcessor();

		this.handlerList = new LinkedList<>();
		this.handlerList.add(new HttpHandler(pathResolver, processorList));
		this.handlerList.add(new WebHandler());
	}

	@Override
	public void run() {
		try {
			handleConnection(socket.getInputStream(), socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	/**
	 * Handles incoming connection, processes and dispatches the request.
	 */
	private void handleConnection(InputStream is, OutputStream os) throws IOException {
		final HttpRequest request = new HttpRequest(is, pathResolver);
		final HttpResponse response = new HttpResponse(request, os);

		boolean shouldContinue = httpProcessor.processRequest(request, response);
		if (!shouldContinue) {
			sendError(response);
		}
		else if (request.getRequestLine().getMethod().equals(HttpMethod.TRACE)) {
		    response.send();
        }
        else if (request.getRequestLine().getMethod().equals(HttpMethod.OPTIONS)) {
            response.send();
        }
		else {
			dispatchRequest(request, response);
		}
	}

	/**
	 * Dispatches the request using the available handlers.
	 */
	private void dispatchRequest(Request request, Response response) {
		log.debug("Will dispatch incoming request at path " + request.getRequestLine().getPath());

		handlerList.stream()
				.filter(p -> p.dispatch(request, response))
				.findFirst();
	}

	private void sendError(HttpResponse response) {
		try {
			response.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
