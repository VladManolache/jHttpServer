package com.vmanolache.httpserver;

import static java.lang.String.join;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vmanolache.httpserver.api.constants.HttpConstants;
import com.vmanolache.httpserver.api.constants.HttpHeader;
import com.vmanolache.httpserver.api.constants.HttpMethod;
import com.vmanolache.httpserver.api.constants.HttpVersion;
import com.vmanolache.httpserver.api.constants.MediaType;
import com.vmanolache.httpserver.api.constants.StatusCode;
import com.vmanolache.httpserver.request.HttpRequest;
import com.vmanolache.httpserver.response.HttpResponse;

/**
 * Responsible for parsing and validating HTTP requests.
 */
class HttpProcessor {

    /**
     * Process the HTTP request based on HTTP version and method.
     */
	boolean processRequest(HttpRequest request, HttpResponse response) {
		boolean shouldContinue = processByHttpVersion(request, response);
		return shouldContinue && processByMethod(request, response);
	}

	/**
	 * Processes the request by the http version and ensures protocol compliance.
	 */
	private boolean processByHttpVersion(HttpRequest request, HttpResponse response) {
		HttpVersion version = request.getRequestLine().getHttpVersion();
		Map<String, String> headers = request.getHeaders();

		switch (version) {
			case HTTP11:
				if (headers.get(HttpHeader.HOST.getName()) == null) {
					// RFC2616#14.23: missing Host header gets 400
					response.getHeaders().setStatusCode(StatusCode.BAD_REQUEST);
					return false;
				}

				String expect = request.getHeaders().get(HttpHeader.EXPECT.getName());
				if (expect != null) {
					boolean failed = handleExpectHeader(expect, headers, response);
					if (failed) {
						return false;
					}
				}
				break;

			case HTTP10:
			case HTTP09:
				removeConnectionHeader(headers);
				break;

			default:
				response.getHeaders().setStatusCode(StatusCode.BAD_REQUEST);
				return false;
		}

		return true;
	}

	private boolean handleExpectHeader(String expect, Map<String, String> headers, HttpResponse response) {
		if (expect.equalsIgnoreCase(HttpConstants.CONTINUE_100)) {
			response.getHeaders().setStatusCode(StatusCode.CONTINUE);
			try {
				// return a continue response before reading body.
				response.getOs().write(headers.toString().getBytes());
			}
			catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			// RFC2616#14.20: if unknown expect, send 417
			response.getHeaders().setStatusCode(StatusCode.EXPECTATION_FAILED);
			return false;
		}
		return true;
	}

	private void removeConnectionHeader(Map<String, String> headers) {
		// RFC2616#14.10 - remove connection headers from older versions
		if (headers.get(HttpHeader.CONNECTION.getName()) != null) {
			headers.remove(HttpHeader.CONNECTION.getName());
		}
	}

	/**
	 * Processes the request by the request method.
	 */
	private boolean processByMethod(HttpRequest request, HttpResponse response) {
		HttpMethod method = request.getRequestLine().getMethod();

		switch (method) {
			case HEAD:
				processHeadMethod(request, response);
				break;

			case TRACE:
				processTraceMethod(request, response);
				break;

			case OPTIONS:
				processOptionsMethod(response);
				break;

			case GET:
			case POST:
			case PUT:
			case DELETE:
				break;

			default:
				response.getHeaders().setStatusCode(StatusCode.HTTP_VERSION_NOT_SUPPORTED);
				return false;
		}
		return true;
	}

	private void processHeadMethod(HttpRequest request, HttpResponse response) {
		request.getRequestLine().setMethod(HttpMethod.GET); // identical to a GET
		response.setDiscardBody(true); // process normally but discard body
	}

	private void processTraceMethod(HttpRequest request, HttpResponse response) {
		response.getHeaders().setStatusCode(StatusCode.OK);
		response.getHeaders().setContentType(MediaType.MESSAGE_HTML);
		String body = String.format("%s %s %s\n%s",
				HttpMethod.TRACE,
				request.getRequestLine().getPath().normalize(),
				request.getRequestLine().getHttpVersion(),
				HttpConstants.CLRF);
		response.setBody(body.getBytes());
	}

	private void processOptionsMethod(HttpResponse response) {
		List<String> methods = HttpMethod.list().stream()
				.map(HttpMethod::getName)
				.collect(Collectors.toList());
		response.getHeaders().setAllowContent(join(", ", methods));
		response.getHeaders().setContentLength("0"); // RFC2616#9.2
		response.getHeaders().setStatusCode(StatusCode.OK);
	}

}
