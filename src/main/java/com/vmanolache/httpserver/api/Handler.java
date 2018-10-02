package com.vmanolache.httpserver.api;

/**
 * Created by Vlad Manolache on 2018-09-28.
 */
public interface Handler {

    /**
     * Dispatch to the first processor that can handle the request.
     */
	boolean dispatch(Request request, Response response);

}
