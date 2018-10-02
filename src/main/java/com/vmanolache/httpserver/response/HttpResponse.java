package com.vmanolache.httpserver.response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Optional;

import com.vmanolache.httpserver.api.HttpResponseHeaders;
import com.vmanolache.httpserver.api.Response;
import com.vmanolache.httpserver.api.constants.HttpConstants;
import com.vmanolache.httpserver.api.constants.MediaType;
import com.vmanolache.httpserver.request.HttpRequest;

import lombok.Getter;
import lombok.Setter;

public class HttpResponse implements Response {

	@Getter @Setter
	private HttpResponseHeaders headers;

	@Getter
	private final OutputStream os;

	@Getter @Setter
	private boolean discardBody;

	@Setter
	private byte[] body;

	public HttpResponse(HttpRequest request, OutputStream os) {
		this.os = os;

		final Path path = request.getRequestLine().getPath();
		headers = new HttpResponseHeaders(request);
		final String contentType = URLConnection.guessContentTypeFromName(path.toString());
		if (contentType != null) {
			Optional<MediaType> type = MediaType.safeValueOf(contentType);
			type.ifPresent(headers::setContentType);
		}
	}

	public void send() throws IOException {
		sendHeaders();
		if (!discardBody) {
			sendBody();
		}
	}

	private void sendHeaders() {
		PrintWriter pw = new PrintWriter(os);
		pw.write(headers.toString());
		pw.write(HttpConstants.CLRF);
		pw.flush();
	}

	private void sendBody() throws IOException {
		os.write(body);
		os.flush();
	}

}
