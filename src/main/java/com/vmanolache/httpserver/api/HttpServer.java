package com.vmanolache.httpserver.api;

import com.vmanolache.httpserver.DispatcherServlet;

import java.io.IOException;

/**
 * Http Server that listens for incoming requests.
 *
 * @see DispatcherServlet
 */
public interface HttpServer {

    /**
     * Starts the server. Does nothing if the server is already running.
     */
    void start() throws IOException;

    /**
     * Stops the server. Does nothing if the server is already stopped.
     */
    void stop() throws IOException;

}
