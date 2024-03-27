package aya;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DebugUtils {
	
	public static String exToString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(AyaPrefs.BUG_MESSAGE);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
}
