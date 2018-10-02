package com.vmanolache.httpserver.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Used to read the body of a request when we do not know the length in advance.
 */
class ChunkedBodyReader {

	byte[] read(BufferedReader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		Scanner scanner = new Scanner(reader);
		scanner.useDelimiter("\r\n");

		try {
			String content;
			String length = scanner.nextLine();
			while (length != null) {
				content = scanner.nextLine();
				if (content.length() == 0) {
					break;
				}
				sb.append(content);
				length = scanner.nextLine();
			}
		} catch (NumberFormatException e) {
			throw new IOException("Failed to read body");
		}

		return sb.toString().getBytes();
	}

}
