package aya.parser;

import aya.exceptions.EndOfInputError;

public class ParserString {
	char[] chars;
	int ix;
	
	public ParserString(String s) {
		this.chars = s.toCharArray();
		ix = 0;
	}
	
	/** "Removes" and returns the first character in the string */
	public char next() throws EndOfInputError {
		if(ix >= chars.length) {
			throw new EndOfInputError("Unexpected End of Input");
		}
		char c = chars[ix];
		ix++;
		return c;
	}
	
	/** Returns the first character in the string */
	public char peek() {
		return chars[ix];
	}
	
	/** Returns the nth next character in the string. peek() == peek(0) */
	public char peek(int i) {
		return chars[ix + i];
	}
	
	/** Returns false if there is no more data to be parsed by looking ahead i characters. hasNext() == hasNext(0) */
	public boolean hasNext(int i) {
		return (ix + i) < chars.length;
	}
	
	/** Returns true if there is no more data to be parsed */
	public boolean isEmpty() {
		return ix >= chars.length;
	}
	
	/** Returns false if there is no more data to be parsed (opposite of isEmpty)*/
	public boolean hasNext() {
		return ix < chars.length;
	}
	
	
	
	public String toString() {
		String s = new String(chars);
		StringBuilder sb = new StringBuilder();
		int i = 0;
		while (i < ix-1) {
			sb.append(' ');
			i++;
		}
		sb.append('^');
		
		return "\n" + s + '\n' + sb.toString();
	}

	public String lookAround(int i) {
		int start = Math.max(0, ix - i);
		int end = Math.min(ix + i, chars.length);
		//char[] out = new char[(end - start) + 1];
		String out = "";
		for (int k = start; k < end - 1; k++)
			out += chars[k];
		return new String(out);
	}
}
