package element;

import java.awt.Color;

public class OutputString {
	public static final int NORMAL = 0;
	public static final int WARN = 1;
	public static final int ERROR = 2;
	public static final int PRINT = 3;
	public static final int QUIET = 4;
	public static final int COLOR = 5;
	
	private String str;
	private int type;
	private Color colr;
	
	public OutputString(String str, Color colr) {
		this.str = str;
		this.type = COLOR;
		this.colr = colr;
	}
	
	public OutputString(String str, int type) {
		this.str = str;
		this.type = type;
	}
	
	public Color getColor() {
		return this.colr;
	}
	
	public OutputString(String str) {
		this.str = str;
		this.type = NORMAL;
	}
	
	public int getType() {
		return type;
	}
	
	public String toString() {
		return str;
	}
}
