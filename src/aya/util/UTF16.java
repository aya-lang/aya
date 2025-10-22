package aya.util;

/**
 * Because {@code sun.text.normalizer.UTF16} is JDK dependent (=sometimes not available) this reimplements the core functionality used by Aya.
 */
public class UTF16 {
	/**
	 * The leading 6 bits identifying a "high surrogate" code-unit (the upper 10 bits of the encoded code-point)
	 */
	public static final int highSurrogateBase = 0xd800;
	/**
	 * The leading 6 bits identifying a "low surrogate" code-unit (the lower 10 bits of the encoded code-point)
	 */
	public static final int lowSurrogateBase = 0xdc00;

	public static final int surrogateMin = Math.min(highSurrogateBase, lowSurrogateBase);
	public static final int surrogateMax = Math.max(highSurrogateBase, lowSurrogateBase) + 0x3ff;

	public static boolean isHighSurrogate(int codeUnit) {
		return (codeUnit & 0b1111_1100_0000_0000) == highSurrogateBase;
	}

	public static boolean isLowSurrogate(int codeUnit) {
		return (codeUnit & 0b1111_1100_0000_0000) == lowSurrogateBase;
	}

	public static boolean isSurrogate(int codeUnit) {
		return codeUnit >= surrogateMin && codeUnit <= surrogateMax;
	}

	/**
	 * @return {@code true} if the given (pseudo-unsigned) 4 byte integer can be safely narrowed to 2 bytes.
	 */
	public static boolean is2Byte(int utf16EncodedChar) {
		return utf16EncodedChar >= 0 && utf16EncodedChar <= 0xffff;
	}

	/**
	 * Converts a utf16 encoded character (either a surrogate pair or a BMP character) to a string.
	 */
	public static String surrogateToStr(int utf16EncodedChar) {
		if (is2Byte(utf16EncodedChar)) {
			return "" + ((char) utf16EncodedChar);
		} else {
			return new String(new char[]{
					((char) (utf16EncodedChar >> 16)),
					((char) utf16EncodedChar)
			});
		}
	}

	/**
	 * Converts a Unicode code-point to a string.
	 */
	public static String codePointToStr(int codePoint) {
		if (is2Byte(codePoint)) {
			return "" + ((char) codePoint);
		} else {
			int u = codePoint - 0x1_0000;
			return new String(new char[]{
					(char) (((u >> 10) & 0b11_1111_1111) | highSurrogateBase),
					(char) ((u & 0b11_1111_1111) | lowSurrogateBase)
			});
		}
	}

	/**
	 * Converts a Unicode code-point to a string.
	 */
	public static void codePointToStr(int codePoint, StringBuilder collector) {
		if (is2Byte(codePoint)) {
			collector.append((char) codePoint);
		} else {
			int u = codePoint - 0x1_0000;
			collector.append((char) (((u >> 10) & 0b11_1111_1111) | highSurrogateBase));
			collector.append((char) ((u & 0b11_1111_1111) | lowSurrogateBase));
		}
	}
}
