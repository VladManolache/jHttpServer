package com.vmanolache.httpserver.api;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Preferences for the HTTP server.
 */
public class ServerConfig {

	/**
	 * Root path of the API.
	 */
	@Getter
	private String documentRoot;

    /**
     * HTTP port to listen on.
     */
	@Getter
	private int port;

    /**
     * List of processors.
     */
	@Getter @Setter
	private List<RequestProcessor> processorList;

	public ServerConfig(String documentRoot, int port) {
		this.documentRoot = documentRoot;
		this.port = port;
	}

}
