package com.vmanolache.httpserver.api;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Vlad Manolache on 2018-09-22.
 */
public class PathResolver {

	private final Path documentRoot;

	public PathResolver(String documentRoot) {
		this.documentRoot = Paths.get(documentRoot);
	}

	public Path resolve(String uri) {
		return documentRoot.resolve(uri.substring(1));
	}

}
