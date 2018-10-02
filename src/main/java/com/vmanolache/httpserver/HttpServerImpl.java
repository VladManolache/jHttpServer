package com.vmanolache.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import com.vmanolache.httpserver.api.HttpServer;
import com.vmanolache.httpserver.api.RequestProcessor;
import com.vmanolache.httpserver.api.ServerConfig;
import com.vmanolache.httpserver.api.PathResolver;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SuppressWarnings("unused")
public final class HttpServerImpl implements HttpServer {

	private final String documentRoot;

	private final int port;

	private final ExecutorService executorService;

	private final List<RequestProcessor> processorList;

	private volatile ServerSocket serverSocket;

	public HttpServerImpl(ServerConfig serverConfig) {
		this.port = serverConfig.getPort();
		this.documentRoot = serverConfig.getDocumentRoot();
		this.processorList = serverConfig.getProcessorList();

		this.executorService = Executors.newCachedThreadPool();
	}

	@Override
	public synchronized void start() throws IOException {
		if (serverSocket != null) {
			return;
		}

		serverSocket = createServerSocket();

		log.debug("HTTP Server is listening on port " + port);

		do {
			final PathResolver pathResolver = new PathResolver(documentRoot);
			final Socket clientSocket = serverSocket.accept();
			final DispatcherServlet dispatcherServlet = new DispatcherServlet(clientSocket, processorList, pathResolver);
			executorService.submit(dispatcherServlet);

		} while (!serverSocket.isClosed());
	}

	/**
	 * Creates the server socket used to accept connections.
	 * The connection will be HTTPS if a keystore was provided, otherwise HTTP.
	 *
	 * @throws IOException if the socket cannot be created
	 *
	 * @return the created server socket
	 */
	private ServerSocket createServerSocket() throws IOException {
		ServerSocketFactory serverSocketFactory;
		if (System.getProperty("javax.net.ssl.keyStore") != null) {
			serverSocketFactory = SSLServerSocketFactory.getDefault();
		}
		else {
			serverSocketFactory = ServerSocketFactory.getDefault();
		}

		ServerSocket server = serverSocketFactory.createServerSocket();
		server.setReuseAddress(true);
		server.bind(new InetSocketAddress(port));
		return server;
	}

	@Override
	public synchronized void stop() throws IOException {
		if (serverSocket != null) {
			serverSocket.close();
		}
		serverSocket = null;
	}

}
