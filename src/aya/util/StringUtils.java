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

}
