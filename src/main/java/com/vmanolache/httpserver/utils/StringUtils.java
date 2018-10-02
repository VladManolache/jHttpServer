package com.vmanolache.httpserver.utils;

/**
 * Utility methods for working with strings.
 */
public class StringUtils {

	public static String capitalizeFirstLetter(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

}
