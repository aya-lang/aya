package aya.parser;

import aya.exceptions.ex.EndOfInputError;

public class ParserString {
	private SourceString source;
	private char[] chars;
	private int ix;
	private int end_ix;
	private int start_ix;
	
	public ParserString(SourceString source) {
		this(source, 0, source.length());
	}

	public ParserString(SourceStringRef source, String substr) {
		this(source.getSource(), source.getIndex()-substr.length(), substr.length());
		try {
			String substr_test = source.getSource().getSource().substring(this.start_ix, this.end_ix);
			if (!substr_test.equals(substr)) {
				System.out.println("input<" + substr + ">");
				System.out.println("source<" + substr_test + ">");
				throw new AssertionError();
			}
		} catch (StringIndexOutOfBoundsException e) {
			throw e;
		}
	}
	
	private ParserString(SourceString source, int offset, int length) {
		this.source = source;
		this.chars = source.getRawString().toCharArray();
		this.ix = offset;
		this.start_ix = offset;
		this.end_ix = offset + length;
	}
	
	/** "Removes" and returns the first character in the string */
	public char next() throws EndOfInputError {
		if(ix >= this.end_ix) {
			throw new EndOfInputError("Unexpected End of Input", currentRef());
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
		return (ix + i) < this.end_ix;
	}
	
	/** Returns true if there is no more data to be parsed */
	public boolean isEmpty() {
		return ix >= this.end_ix;
	}
	
	/** Returns false if there is no more data to be parsed (opposite of isEmpty)*/
	public boolean hasNext() {
		return ix < this.end_ix;
	}

	@Override
	public String toString() {
		return new String(this.source.getRawString().substring(this.start_ix, this.end_ix));
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
