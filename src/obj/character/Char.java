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
	
	public static Char valueOf(char c) {
		if (c < 128) {
			return CACHE[c];
		} else {
			return new Char(c);
		}
	}
	
	////////////////
	// CHAR TESTS //
	////////////////
	
	
	public boolean isUpper() {
		// TODO
		return false;
	}
	
	public boolean isLower() {
		// TODO
		return false;
	}
	
	public boolean isDigit() {
		// TODO
		return false;
	}
	
	public boolean isAlpha() {
		// TODO
		return false;
	}
	
	public boolean isWhitespace() {
		// TODO
		return false;
	}
	
	
	
	
	/////////////////////
	// CHAR OPERATIONS //
	/////////////////////
	
	public boolean toUpper() {
		// TODO
		return false;
	}
	
	public boolean toLower() {
		// TODO
		return false;
	}
	
	
	
	
	////////////////
	// BASIC MATH //
	////////////////
	
	public Char add(Number n) {
		// TODO
		return null;
	}
	
	public Char add(Char n) {
		// TODO
		return null;
	}
	
	public Char sub(Number n) {
		// TODO
		return null;
	}
	
	public Char sub(Char n) {
		// TODO
		return null;
	}
	
	public Char mul(Number n) {
		// TODO
		return null;
	}
	
	public Char mul(Char n) {
		// TODO
		return null;
	}
	
	public Char idiv(Number n) {
		// TODO
		return null;
	}
	
	public Char idiv(Char n) {
		// TODO
		return null;
	}
	
	public Char mod(Number n) {
		// TODO
		return null;
	}
	
	public Char mod(Char n) {
		// TODO
		return null;
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
		// TODO special chars
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
		// TODO Auto-generated method stub
		return 0;
	}

}
