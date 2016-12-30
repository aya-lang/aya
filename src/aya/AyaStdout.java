package aya;

import java.awt.Color;
import java.util.ArrayList;

/** Handles output of the program */
public class AyaStdout {	
	private ArrayList<OutputString> output = new ArrayList<OutputString>();
	
	/** Prints a string to stdout */
	public void print(String str) {
		output.add(new OutputString(str, OutputString.NORMAL));
	}
	
	public void printEx(String str) {
		output.add(new OutputString(str, OutputString.ERROR));
	}
	
	public void printWarn(String str) {
		output.add(new OutputString(str, OutputString.WARN));
	}
	
	public void printQuiet(String str) {
		output.add(new OutputString(str, OutputString.QUIET));
	}
	
	public void printAsPrint(String str) {
		output.add(new OutputString(str, OutputString.PRINT));
	}
	
	public void printColor(String str, Color color) {
		output.add(new OutputString(str, color));
	}
	
	/** Prints a string and a newline character to stdout */
	public void println(String str) {
		output.add(new OutputString(str +'\n'));
	}
	
	
	
	/** Returns stdout as a list of outputs */
	public ArrayList<OutputString> dump() {
		@SuppressWarnings("unchecked")
		ArrayList<OutputString> out = (ArrayList<OutputString>) output.clone();
		clear();
		return out;
	}
	
	
	/** Returns stdout as a string */
	public String dumpAsString() {
		StringBuilder sb = new StringBuilder();
		
		for (OutputString os : this.output) {
			sb.append(os.toString());
			if (os.getType() != OutputString.PRINT) {
				sb.append(" ");
			}
		}
		
		clear();
		String outStr = sb.toString();
		return outStr.trim();
	}

	
	/** Clears the stdout string */
	public void clear() {
		output.clear();
	}
	
	/** Returns true if STDOUT currently contains an error message */
	public boolean hasError() {
		for (OutputString os : output) {
			if (os.getType() == OutputString.ERROR) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isEmpty() {
		return output.size() == 0;
	}
}
