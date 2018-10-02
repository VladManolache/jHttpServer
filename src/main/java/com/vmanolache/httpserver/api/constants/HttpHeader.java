package com.vmanolache.httpserver.api.constants;

import lombok.Getter;

/**
 * List of HTTPS headers.
 *
 * Created by Vlad Manolache on 2018-09-26.
 */
public enum HttpHeader {
	CONNECTION("Connection"),
	CONTENT_LENGTH("Content-Length"),
	CONTENT_TYPE("Content-Type"),
	ALLOW("Allow"),
	EXPECT("Expect"),
	HOST("Host"),
	TRANSFER_ENCODING("Transfer-Encoding");

	@Getter
	private String name;

	HttpHeader(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
