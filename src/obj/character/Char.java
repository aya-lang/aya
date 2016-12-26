package obj.character;

import obj.Obj;

public class Char extends Obj implements Comparable<Char> {
	
	public static final Char[] CACHE = new Char[128];
	
	static {
		for (char c = 0; c < 128; c++) {
			CACHE[(int)c] = new Char(c);
		}
	}
	
	char _c;
	
	public Char(char c) {
		_c = c;
	}
	
	public static Char valueOf(int i) {
		if (i < 128 && i >= 0) {
			return CACHE[i];
		} else {
			return new Char((char)i);
		}
	}
	
	public static Char valueOf(char c) {
		if (c < 128) {
			return CACHE[c];
		} else {
			return new Char(c);
		}
	}
	
	public char charValue() {
		return _c;
	}
	
	////////////////
	// CHAR TESTS //
	////////////////
	
	
	public boolean isUpper() {
		return _c >= 'A' && _c <= 'Z';
	}
	
	public boolean isLower() {
		return _c >= 'a' && _c <= 'z';
	}
	
	public boolean isDigit() {
		return _c >= '0' && _c <= '9';
	}
	
	public boolean isAlpha() {
		return isUpper() || isLower();
	}
	
	public boolean isWhitespace() {
		return _c == ' ' || _c == '\t' || _c == '\n' || _c == '\r';
	}
	
	
	
	
	/////////////////////
	// CHAR OPERATIONS //
	/////////////////////
	
	public Char toUpper() {
		if (isLower()) 
			return Char.valueOf(_c - 32);
		else
			return this;
	}
	
	public Char toLower() {
		if (isUpper()) 
			return Char.valueOf(_c + 32);
		else
			return this;
	}
	
	
	
	
	////////////////
	// BASIC MATH //
	////////////////
		
	public Char add(Number n) {
		return Char.valueOf(_c + n.intValue());
	}
	
	public Char add(Char n) {
		return Char.valueOf(_c + n._c);
	}
	
	public Char sub(Number n) {
		return Char.valueOf(_c - n.intValue());
	}
	
	public Char sub(Char n) {
		return Char.valueOf(_c - n._c);
	}
	
	public Char subFrom(Number n) {
		return Char.valueOf(n.intValue() - _c);
	}
	
	public Char subFrom(Char n) {
		return Char.valueOf(n._c - _c);
	}
	
	public Char mul(Number n) {
		return Char.valueOf(_c * n.intValue());
	}
	
	public Char mul(Char n) {
		return Char.valueOf(_c * n._c);
	}
	
	public Char idiv(Number n) {
		return Char.valueOf(_c / n.intValue());
	}
	
	public Char idiv(Char n) {
		return Char.valueOf(_c / n._c);
	}
	
	public Char idivFrom(Number n) {
		return Char.valueOf(n.intValue() / _c);
	}
	
	public Char idivFrom(Char n) {
		return Char.valueOf(n._c / _c);
	}
	
	public Char mod(Number n) {
		return Char.valueOf(_c % n.intValue());
	}
	
	public Char mod(Char n) {
		return Char.valueOf(_c % n._c);
	}
	
	public Char modFrom(Number n) {
		return Char.valueOf(n.intValue() % _c);
	}
	
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
