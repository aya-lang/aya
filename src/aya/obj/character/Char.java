package aya.obj.character;

import aya.obj.Obj;
import aya.obj.number.Number;

/**
 * Wrapper for characters
 * @author Nick
 *
 */
public class Char extends Obj implements Comparable<Char> {
	
	/** Char(Character.MAX_VALUE) */
	public static final Char MAX_VALUE = new Char(Character.MAX_VALUE);
	
	/** Cache for the most commonly used characters */
	private static final Char[] CACHE = new Char[128];
	
	// Fill the cache with the first 128 characters
	static {
		for (char c = 0; c < 128; c++) {
			CACHE[(int)c] = new Char(c);
		}
	}
	
	/** The inner char */
	char _c;
	
	/** Create a new Char object. {@code Char.valueOf(_c) may
	 * have increased performance because it caches commonly used
	 * character values.
	 * @param c
	 */
	public Char(char c) {
		_c = c;
	}
	
	/** Create a new Char object. May
	 * have increased performance because it caches commonly used
	 * character values.
	 * @param c
	 */
	public static Char valueOf(int i) {
		if (i < 128 && i >= 0) {
			return CACHE[i];
		} else {
			return new Char((char)i);
		}
	}
	
	/** Create a new Char object. May
	 * have increased performance because it caches commonly used
	 * character values.
	 * @param c
	 */
	public static Char valueOf(char c) {
		if (c < 128) {
			return CACHE[c];
		} else {
			return new Char(c);
		}
	}
	
	/** Return the inner character */
	public char charValue() {
		return _c;
	}
	
	////////////////
	// CHAR TESTS //
	////////////////
	
	/** Returns true if this character is in the range ['A'..'Z'] */
	public static boolean isUpper(char c) {
		return c >= 'A' && c <= 'Z';
	}
	
	/** Returns true if this character is in the range ['A'..'Z'] */
	public boolean isUpper() {
		return isUpper(_c);
	}
	
	/** Returns true if this character is in the range ['a'..'z'] */
	public static boolean isLower(char c) {
		return c >= 'a' && c <= 'z';
	}
	
	/** Returns true if this character is in the range ['A'..'Z'] */
	public boolean isLower() {
		return isLower(_c);
	}
	
	/** Returns true if this character is in the range ['0'..'9'] */
	public boolean isDigit() {
		return _c >= '0' && _c <= '9';
	}
	
	/** Returns true if this character is in the range ['A'..'Z'] or ['a'..'z'] */
	public boolean isAlpha() {
		return isUpper() || isLower();
	}
	
	/** Returns true if this character is a ' ', '\t', '\n', or '\r' */
	public boolean isWhitespace() {
		return _c == ' ' || _c == '\t' || _c == '\n' || _c == '\r';
	}
	
	
	
	
	/////////////////////
	// CHAR OPERATIONS //
	/////////////////////
	
	/** If the char is in the range ['a'..'z'], convert it to uppercase */
	public static char toUpper(char c) {
		if (isLower(c)) 
			return (char)(c - 32);
		else
			return c;
	}
	
	/** If the char is in the range ['a'..'z'], convert it to uppercase */
	public Char toUpper() {
		return Char.valueOf(toUpper(_c));
	}
	
	/** If the char is in the range ['A'..'Z'], convert it to lowercase */
	public static char toLower(char c) {
		if (isUpper(c)) 
			return (char)(c + 32);
		else
			return c;
	}
	
	/** If the char is in the range ['A'..'Z'], convert it to lowercase */
	public Char toLower() {
		return Char.valueOf(toLower(_c));
	}
	
	
	/** If uppercase, make lowercase, if lowercase make uppercase, otherwise do nothing */
	public static char swapCase(char c) {
		if (isUpper(c)) {
			return toLower(c);
		} else {
			return toUpper(c);
		}
	}
	
	/** If uppercase, make lowercase, if lowercase make uppercase, otherwise do nothing */
	public Char swapCase() {
		return Char.valueOf(swapCase(_c));
	}
	
	
	
	
	
	
	////////////////
	// BASIC MATH //
	////////////////
	
	/** Increment */
	public Char inc() {
		return Char.valueOf(_c + 1);
	}
	
	/** Decrement */
	public Char dec() {
		return Char.valueOf(_c - 1);
	}
	
	/** Add a number to the char */
	public Char add(Number n) {
		return Char.valueOf(_c + n.toInt());
	}
	
	/** Add a char to the char */
	public Char add(Char n) {
		return Char.valueOf(_c + n._c);
	}
	
	/** Subtract a number to from char */
	public Char sub(Number n) {
		return Char.valueOf(_c - n.toInt());
	}
	
	/** Subtract a char from the char */
	public Char sub(Char n) {
		return Char.valueOf(_c - n._c);
	}
	
	/** Subtract this char value from the input number */
	public Char subFrom(Number n) {
		return Char.valueOf(n.toInt() - _c);
	}
	
	/** Subtract this char value from the input char */
	public Char subFrom(Char n) {
		return Char.valueOf(n._c - _c);
	}
	
	/** Multiply a number to the char */
	public Char mul(Number n) {
		return Char.valueOf(_c * n.toInt());
	}
	
	/** Multiply a number to the char */
	public Char mul(Char n) {
		return Char.valueOf(_c * n._c);
	}
	
	/** Divide this char by the input number */
	public Char idiv(Number n) {
		return Char.valueOf(_c / n.toInt());
	}
	
	/** Divide this char by the input char */
	public Char idiv(Char n) {
		return Char.valueOf(_c / n._c);
	}
	
	/** Divide the input number by this char */
	public Char idivFrom(Number n) {
		return Char.valueOf(n.toInt() / _c);
	}
	
	/** Divide the input char by this char */
	public Char idivFrom(Char n) {
		return Char.valueOf(n._c / _c);
	}
	
	/** This char value `mod` input number */
	public Char mod(Number n) {
		return Char.valueOf(_c % n.toInt());
	}
	
	/** This char value `mod` input char */
	public Char mod(Char n) {
		return Char.valueOf(_c % n._c);
	}
	
	/** Input number `mod` this char value */
	public Char modFrom(Number n) {
		return Char.valueOf(n.toInt() % _c);
	}
	
	/** Input char `mod` this char value */
	public Char modFrom(Char n) {
		return Char.valueOf(n._c & _c);
	}
	
	
	
	
	///////////////////
	// OBJ OVERRIDES //
	//////////////////
	
	@Override
	public Char deepcopy() {
		return new Char(_c);
	}

	@Override
	public boolean bool() {
		return _c != 0;
	}

	@Override
	public String repr() {
		return "'" + _c;
	}

	@Override
	public String str() {
		return ""+_c;
	}

	@Override
	public boolean equiv(Obj o) {
		return o instanceof Char && ((Char)o)._c == _c;
	}

	@Override
	public boolean isa(byte type) {
		return type == Obj.CHAR;
	}

	@Override
	public byte type() {
		return Obj.CHAR;
	}
	
	
	

	////////////////
	// COMPARABLE //
	////////////////
	
	@Override
	public int compareTo(Char o) {
		//Ascending
		return (int)((_c - o._c)); 
	}

}
