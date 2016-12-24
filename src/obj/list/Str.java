package obj.list;

import obj.Obj;

public class Str extends List implements Comparable<Str> {
	
	private String _str;
	
	public String getStr() {
		return _str;
	}
		
	public Str(String s) {
		_str = s;
	}
	
	
	///////////////////////
	// STRING OPERATIONS //
	///////////////////////
	
	/** Trim whitespace from a string */
	public Str trim() {
		return new Str(_str.trim());
	}
	
	/** replace all occurrences of 'find' with 'replace' */
	public Str replaceAll(String regex, String replacement) {
		return new Str(_str.replaceAll(regex, replacement));
	}
	
	/** Test if Str matches the regex */
	public boolean matches(String regex) {
		return _str.matches(regex);
	}
	
	/** Apply format rules to the string */
	public Str format(Object... args) {
		return new Str(String.format(_str, args));
	}
	
	
	
	
	////////////////////
	// LIST OVERRIDES //
	////////////////////

	@Override
	public int length() {
		return _str.length();
	}

	@Override
	public Str head(int i) {
		// TODO Pad with space if out of range
		return null;
	}

	@Override
	public Str tail(int i) {
		// TODO Pad with space if out of range
		return null;
	}

	@Override
	public Obj head() {
		return _str.charAt(_str.length()-1);
	}

	@Override
	public Obj tail() {
		return _str.charAt(0);
	}

	@Override
	public Obj pop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Obj popBack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reverse() {
		// TODO Auto-generated method stub

	}

	@Override
	public void slice(int i, int j) {
		// TODO Auto-generated method stub

	}

	@Override
	public Obj get(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int find(Obj o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int findBack(Obj o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int count(Obj o) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public Obj deepcopy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean bool() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String repr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String str() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equiv(Obj o) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isa(byte type) {
		return type == Obj.LIST || type == Obj.STR;
	}

	@Override
	public byte type() {
		return Obj.STR;
	}
	
	
	
	////////////////
	// COMPARABLE //
	////////////////
	
	@Override
	public int compareTo(Str o) {
		// TODO Auto-generated method stub
		return 0;
	}



}
