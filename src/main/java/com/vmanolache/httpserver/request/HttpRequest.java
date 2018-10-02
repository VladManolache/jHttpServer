package com.vmanolache.httpserver.request;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.vmanolache.httpserver.api.*;
import com.vmanolache.httpserver.api.constants.HttpConstants;
import com.vmanolache.httpserver.api.constants.HttpHeader;
import com.vmanolache.httpserver.api.constants.HttpMethod;
import com.vmanolache.httpserver.api.constants.HttpVersion;
import com.vmanolache.httpserver.api.PathResolver;
import com.vmanolache.httpserver.utils.StringUtils;

import lombok.Getter;

@Getter
public final class HttpRequest implements Request {

	private RequestLine requestLine;

	private Map<String, String> headers;

	private Map<String, String> params;

	private byte[] body;

	public HttpRequest(InputStream is, PathResolver pathResolver) throws IOException {
		headers = new HashMap<>();
		params = new HashMap<>();

		parseInput(is, pathResolver);
	}

	private void parseInput(InputStream is, PathResolver pathResolver) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		requestLine = parseRequestLine(reader, pathResolver);

		params = parseRequestParams();

		headers = parseHeaders(reader);

		body = parseBody(reader);
	}

	private RequestLine parseRequestLine(BufferedReader reader, PathResolver pathResolver) throws IOException {
		String line = reader.readLine();
		String[] input = line.split(" ");
		if (input.length < 3) {
			throw new IOException("Invalid http request");
		}
		else {
			Optional<HttpMethod> method = HttpMethod.safeValueOf(input[0]);
			if (!method.isPresent()) {
				throw new IOException("Invalid http request");
			}
			Optional<HttpVersion> version = HttpVersion.safeValueOf(input[2]);
			if (!version.isPresent()) {
				throw new IOException("Invalid http request");
			}
			Path path = pathResolver.resolve(input[1]);
			return new RequestLine(method.get(), path, version.get());
		}
	}

	private Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
		Map<String, String> headers = new HashMap<>();
		String line = reader.readLine();
		while (line != null && !line.isEmpty()) {
			String[] input = line.split(": ", 2);
			if (input.length == 2) {
				headers.put(StringUtils.capitalizeFirstLetter(input[0]), input[1]);
			}
			line = reader.readLine();
		}
		return headers;
	}

	private Map<String, String> parseRequestParams() {
		Map<String, String> params = new HashMap<>();
		String[] uriParts = requestLine.getPath().toString().split("\\?", 2);
		if (uriParts.length == 2) {
			String query = uriParts[1];

			String[] keyValuePairs = query.split("&");
			for (String keyValuePair : keyValuePairs) {
				String[] keyValue = keyValuePair.split("=", 2);
				if (keyValue.length == 2) {
                    String key;
                    try {
                        key = URLDecoder.decode(keyValue[0].trim(), StandardCharsets.UTF_8.toString());
                        String value = URLDecoder.decode(keyValue[1].trim(), StandardCharsets.UTF_8.toString());
                        params.put(key, value);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
				}
			}

			String pathWithoutParams = requestLine.getPath().toString().substring(0, uriParts[0].length());
			requestLine.setPath(Paths.get(pathWithoutParams));
		}
		return params;
	}

	private byte[] parseBody(BufferedReader reader) throws IOException {
		String header = headers.get(HttpHeader.TRANSFER_ENCODING.toString());
		if (header == null || header.toLowerCase(Locale.getDefault()).equals(HttpConstants.IDENTITY)) {
			header = headers.get(HttpHeader.CONTENT_LENGTH.toString());
			int contentLength = header == null ? 0 : Integer.parseInt(header);
			LimitedBodyReader limitedBodyReader = new LimitedBodyReader();
			body = limitedBodyReader.read(reader, contentLength);
		}
		else {
			if (header.contains(HttpConstants.CHUNKED)) {
				ChunkedBodyReader chunkedBodyReader = new ChunkedBodyReader();
				body = chunkedBodyReader.read(reader);
			}
			else {
				throw new IOException("Unsupported Transfer-encoding");
			}
		}

		return body;
	}

}
