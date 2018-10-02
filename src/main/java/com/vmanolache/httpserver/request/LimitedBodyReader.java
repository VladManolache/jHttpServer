package com.vmanolache.httpserver.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Used to read the body of a request when we know the body length in advance.
 */
class LimitedBodyReader {

	byte[] read(BufferedReader reader, int length) throws IOException {
		char[] buffer = null;
		try {
			buffer = new char[length];
			reader.read(buffer, 0, length);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return toBytes(buffer);
	}

	private byte[] toBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
		Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		return bytes;
	}

}
