package com.vmanolache.httpserver.processor;

import com.vmanolache.httpserver.api.Request;
import com.vmanolache.httpserver.api.RequestProcessor;
import com.vmanolache.httpserver.api.Response;
import com.vmanolache.httpserver.api.constants.HttpMethod;
import com.vmanolache.httpserver.api.constants.StatusCode;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
public class DeleteStaticFile extends RequestProcessor {

    public DeleteStaticFile() {
        super(null, HttpMethod.DELETE);
    }

    @Override
    public boolean process(Request request, Response response) {
        final Path path = request.getRequestLine().getPath();
        if (!Files.isRegularFile(path)) {
            response.getHeaders().setStatusCode(StatusCode.NOT_FOUND);
            return false;
        }

        log.debug("Will DELETE file at path " + request.getRequestLine().getPath());

        try {
            File file = new File(request.getRequestLine().getPath().toString());
            boolean success = file.delete();
            if (success) {
                response.getHeaders().setStatusCode(StatusCode.OK);
            }
            else {
                response.getHeaders().setStatusCode(StatusCode.INTENRAL_SERVER_ERROR);
            }
            response.send();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

}
