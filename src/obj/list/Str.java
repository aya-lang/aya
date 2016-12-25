package obj.list;

import obj.Obj;
import obj.character.Char;

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
		return Char.valueOf(_str.charAt(_str.length()-1));
	}

	@Override
	public Char tail() {
		return Char.valueOf(_str.charAt(0));
	}

	@Override
	public Obj pop() {
		// TODO 0 length
		Char out = Char.valueOf(_str.charAt(_str.length()-1));
		_str = _str.substring(0, _str.length()-1);
		return out;
	}

	@Override
	public Obj popBack() {
		// TODO 0 length
		Char out = Char.valueOf(_str.charAt(0));
		_str = _str.substring(1, _str.length());
		return out;
	}

	@Override
	public void reverse() {
		// TODO Auto-generated method stub

	}

	@Override
	public List slice(int i_in, int j_in) {
		int i = List.index(i_in, _str.length());
		int j = List.index(j_in, _str.length());
		
		//Swap the order if i > j
		if (i > j) {
			int t = i;
			i = j;
			j = t;
		}
		
		return new Str(_str.substring(i, j));
	}

	@Override
	public Char get(int i) {
		return Char.valueOf(_str.charAt(List.index(i, _str.length())));
	}

	@Override
	public int find(Obj o) {
		if (o instanceof Char) {
			char c = ((Char)o).charValue();
			return _str.indexOf(c);
		} else {
			return -1;
		}
	}

	@Override
	public int findBack(Obj o) {
		if (o instanceof Char) {
			char c = ((Char)o).charValue();
			return _str.lastIndexOf(c);
		} else {
			return -1;
		}
	}

	@Override
	public int count(Obj o) {
		if (o instanceof Char) {
			char c = ((Char)o).charValue();
			int count = 0;
			for (int i = 0; i < _str.length(); i++) {
				if (c == _str.charAt(i)) {
					count++;
				}
			}
			return count;
		} else {
			return 0;
		}
	}

	
	
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public Obj deepcopy() {
		return new Str(_str);
	}

	@Override
	public boolean bool() {
		return _str.length() != 0;
	}

	@Override
	public String repr() {
		return "\"" + _str + "\"";
	}

	@Override
	public String str() {
		return _str;
	}

	@Override
	public boolean equiv(Obj o) {
		return o instanceof Str && ((Str)o)._str.equals(_str);
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
		return _str.compareTo(o._str);
	}



}
