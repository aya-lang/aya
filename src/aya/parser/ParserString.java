package aya.parser;

import aya.exceptions.ex.EndOfInputError;

public class ParserString {
	SourceString source;
	char[] chars;
	int ix;
	
	public ParserString(SourceString source) {
		this.source = source;
		this.chars = source.getRawString().toCharArray();
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

	/** Set the index back one character */
	public void backup() {
		ix--;
		if (ix < 0) ix = 0;
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

	@Override
	public String toString() {
		return new String(this.chars);
	}
	
	public SourceString getSource() {
		return this.source;
	}
	
	public int currentIndex() {
		return this.ix-1;
	}
	
	public SourceStringRef currentRef() {
		return new SourceStringRef(this.source, this.currentIndex());
	}

}
