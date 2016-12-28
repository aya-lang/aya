package element.obj.list;

import java.util.ArrayList;
import java.util.Arrays;

import element.exceptions.ElementRuntimeException;
import element.obj.Obj;
import element.obj.character.Char;
import element.obj.list.numberlist.NumberItemList;
import element.obj.number.Num;
import element.obj.number.Number;

public class Str extends List implements Comparable<Str> {
	
	private String _str;
	
	public String getStr() {
		return _str;
	}
		
	public Str(String s) {
		_str = s;
	}
	
	/** Create a new string by repeating c n times */
	public Str(char c, int repeats) {
		_str = repeat(c, repeats);
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
	public Str head(int n) {
		n = List.index(n, _str.length());
		if (n <= _str.length()) {
			return new Str(_str.substring(0, n));
		} else {
			return new Str(_str + repeat(' ', n-_str.length()));
		}
	}

	@Override
	public Str tail(int n) {
		n = List.index(n, _str.length());
		if (n <= _str.length()) {
			return new Str(_str.substring(_str.length() - n, _str.length()));
		} else {
			return new Str(repeat(' ', n-_str.length()) + _str);
		}
	}

	@Override
	public Obj head() {
		return Char.valueOf(_str.charAt(0));
	}

	@Override
	public Char tail() {
		return Char.valueOf(_str.charAt(_str.length()-1));
	}

	@Override
	public Obj pop() {
		Char out = Char.valueOf(_str.charAt(_str.length()-1));
		_str = _str.substring(0, _str.length()-1);
		return out;
	}

	@Override
	public Obj popBack() {
		Char out = Char.valueOf(_str.charAt(0));
		_str = _str.substring(1, _str.length());
		return out;
	}

	@Override
	public void reverse() {
		_str = new StringBuilder(_str).reverse().toString();
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
	
	@Override
	public void sort() {
	     char[] chars = _str.toCharArray();
	     Arrays.sort(chars);
	     _str = new String(chars);
	}
	
	@Override
	public void set(int i, Obj o) {
		if (o.isa(Obj.CHAR)) {
			char[] chars = _str.toCharArray();
			chars[List.index(i, chars.length)] = ((Char)o).charValue();
			_str = new String(chars);
		} else {
			throw new ElementRuntimeException("Cannot set item " + o.repr() + " in string " + this.repr() + ". Item must be a char.");
		}
	}
	
	@Override
	public ArrayList<Obj> getObjAL() {
		ArrayList<Obj> l = new ArrayList<Obj>(_str.length());
		char[] chars = _str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			l.add(Char.valueOf(chars[i]));
		}
		return l;
	}

	@Override
	public NumberItemList toNumberList() {
		char[] chars = _str.toCharArray();
		ArrayList<Number> nums = new ArrayList<Number>(chars.length);
		for (char c : chars) {
			nums.add(new Num(c));
		}
		return new NumberItemList(nums);
	}
	
	

	@Override
	public void add(Obj o) {
		if (o.isa(Obj.CHAR)) {
			_str += ((Char)o).charValue();
		} else {
			throw new ElementRuntimeException("Cannot append " + o.repr() + " to string " + repr()
					+ ". Use + to convert to string and concat or convert string to a generic list");
		}
	}
	
	@Override
	public void add(int i, Obj o) {
		if (o.isa(Obj.CHAR)) {
			i = List.index(i, _str.length());
			// If 0, just append to front
			if (i == 0) {
				_str = ((Char)o) + _str;
			} else {
				_str = new StringBuilder(_str).insert(i, ((Char)o).charValue()).toString();
			}
		} else {
			throw new ElementRuntimeException("Cannot append " + o.repr() + " to string " + repr()
					+ ". Use + to convert to string and concat or convert string to a generic list");
		}
	}

	@Override
	public void addAll(List l) {
		for (int i = 0; i < l.length(); i++) {
			add(l.get(i));
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

	////////////////////
	// HELPER METHODS //
	////////////////////
	
	/** Generate a string with n copies of c */
	private String repeat(char c, int n) {
		char[] cs = new char[n];
		for (int i = 0; i < n; i++)
			cs[i] = c;
		return new String(cs);
	}

	


}
