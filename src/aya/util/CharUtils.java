package aya.util;

public class CharUtils {

	/** Returns a character given its unicode value as a hex string */
	public static char getCharUni(String unicode) {
		return (char)Integer.parseInt(unicode, 16);
	}

	/** Returns true if the character is lowercase a-z */
	public static boolean isLowerAlpha(char c) {
		return (c >= 'a' && c <= 'z');
	}


	/** Returns true if the character is 0-9 */
	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
}
