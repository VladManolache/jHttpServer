package com.vmanolache.httpserver.processor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import com.vmanolache.httpserver.api.*;

import com.vmanolache.httpserver.api.constants.HttpMethod;
import com.vmanolache.httpserver.api.constants.StatusCode;
import lombok.extern.log4j.Log4j2;

/**
 * Created by Vlad Manolache on 2018-09-26.
 */
@Log4j2
public class PutStaticFile extends RequestProcessor {

	public PutStaticFile() {
		super(null, HttpMethod.PUT);
	}

	@Override
	public synchronized boolean process(Request request, Response response) {
		if (request.getBody() == null) {
			return false;
		}

		log.debug("Will PUT file at path " + request.getRequestLine().getPath());

		File file = new File(request.getRequestLine().getPath().toString());
		boolean success = writeToFileWithLock(file, request.getBody(), response);
		try {
			response.send();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return success;
	}

	private boolean writeToFileWithLock(File file, byte[] body, Response response) {
		ByteBuffer buffer;

		try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
				FileChannel fileChannel = randomAccessFile.getChannel();
				FileLock fileLock = fileChannel.tryLock()) {
			if (fileLock == null) {
				response.getHeaders().setStatusCode(StatusCode.CONFLICT);
			}
			else {
				randomAccessFile.setLength(0);
				buffer = ByteBuffer.wrap(body);
				fileChannel.write(buffer);
				response.getHeaders().setStatusCode(StatusCode.OK);
				response.getHeaders().setConnection("close");
			}
		} catch (OverlappingFileLockException e) {
			response.getHeaders().setStatusCode(StatusCode.CONFLICT);
			return false;
		} catch (IOException e) {
			response.getHeaders().setStatusCode(StatusCode.BAD_REQUEST);
			return false;
		}
		return true;
	}

}
