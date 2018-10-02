package com.vmanolache.httpserver.api;

import com.vmanolache.httpserver.HttpServerImpl;

@SuppressWarnings("unused")
public class HttpServerBuilder {

    /**
     * Creates a new HttpServer instance using the provided ServerConfig.
     *
     * @see ServerConfig
     */
    public HttpServer build(ServerConfig serverConfig) {
        return new HttpServerImpl(serverConfig);
    }

}
