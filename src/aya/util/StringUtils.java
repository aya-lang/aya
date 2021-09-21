package aya.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class StringUtils {

	private static final DecimalFormat _df = new DecimalFormat("#");
    static {_df.setMaximumFractionDigits(8);}

	public static String doubleToString(double d) {
		if (d % 1 == 0 && d < (double)Long.MAX_VALUE) {
			return String.format("%d",(long)d);
		} else {
			return _df.format(d);
		}
	}
	
	public static String bigDecimalToString(BigDecimal val) {
		return trimZeros(val.toString());
	}

	private static String trimZeros(String s) {
		if(!s.contains("."))
			return s;
		
		int dsi = s.length()-1;
		while(s.charAt(dsi) == '0') {
			dsi--;
		}
		if(s.charAt(dsi) == '.') {
			dsi++;
		}
		return s.substring(0, dsi+1);
	}

	// Add quotes to a string
	public static String quote(String s) {
		s = s.replaceAll("\\\"", "\\\\\"");
		return '"' + s + '"';
	}

	public static String singleQuote(String s) {
		s = s.replaceAll("\\\'", "\\\\\'");
		return "'" + s + "'";
	}

	/** Test if a string contains all lowercase alphabetical letters */
	public static boolean lalpha(String str) {
		for (char c : str.toCharArray()) {
			if (!('a' <= c && c <= 'z')) {
				return false;
			}
		}
		return true;
	}


	/** Returns true if all characters in a string are digits */
	public static boolean allDigits(String s) {
		for(char c : s.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}	
	

	/** Returns true if the string is less than 5
	 * chararacters and are all valid hex characters */
	public static boolean isHex(String s) {
		for(char c : s.toCharArray()) {
			if(Character.isUpperCase(c)) {
				c = Character.toLowerCase(c);
			}
			if ( !((c >= '0' && c <= '9') || (c >= 'A' || c <= 'F')) ) {
				return false;
			}
		}
		return true;
	}

}
