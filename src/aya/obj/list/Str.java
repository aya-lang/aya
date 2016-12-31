package aya.obj.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.character.Char;
import aya.obj.list.numberlist.NumberItemList;
import aya.obj.number.Num;
import aya.obj.number.Number;

/** Wrapper for strings */
public class Str extends List implements Comparable<Str> {
	
	public static final Str EMPTY = new Str("");
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
	
	/** Compute the levenshtein distance of this string and another string */
	public int levDist(Str other) {
		return levenshteinDistance(_str, other._str);
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
		Char out = Char.valueOf(_str.charAt(0));
		_str = _str.substring(1, _str.length());
		return out;
	}

	@Override
	public Obj popBack() {
		Char out = Char.valueOf(_str.charAt(_str.length()-1));
		_str = _str.substring(0, _str.length()-1);
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
	public Char remove(int i) {
		int index = List.index(i, _str.length());
		Char c = Char.valueOf(_str.charAt(index));
		_str = _str.substring(0, index) + _str.substring(index+1);
		return c;
	}
	
	@Override
	public void removeAll(Integer[] ixs) {
		//Remove all duplicates from ixs
		Set<Integer> uniqKeys = new TreeSet<Integer>();
		uniqKeys.addAll(Arrays.asList(ixs));
		Integer[] uniqueIxs = uniqKeys.toArray(new Integer[uniqKeys.size()]);
		
		//Start from the highest valued index and work down
		for (int i = uniqueIxs.length-1; i >= 0; i--) {
			remove(i);
		}
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
			throw new AyaRuntimeException("Cannot set item " + o.repr() + " in string " + this.repr() + ". Item must be a char.");
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
	public Str unique() {
		StringBuilder unique = new StringBuilder();
	    for (int i = 0; i < _str.length(); i++) {
	        String si = _str.substring(i, i + 1);
	        if (unique.indexOf(si) == -1) {
	            unique.append(si);
	        }
	    }
	    return new Str(unique.toString());
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
	public void addItem(Obj o) {
		if (o.isa(Obj.CHAR)) {
			_str += ((Char)o).charValue();
		} else {
			throw new AyaRuntimeException("Cannot append " + o.repr() + " to string " + repr()
					+ ". Use + to convert to string and concat or convert string to a generic list");
		}
	}
	
	@Override
	public void addItem(int i, Obj o) {
		if (o.isa(Obj.CHAR)) {
			i = List.index(i, _str.length());
			// If 0, just append to front
			if (i == 0) {
				_str = ((Char)o).charValue() + _str;
			} else {
				_str = new StringBuilder(_str).insert(i, ((Char)o).charValue()).toString();
			}
		} else {
			throw new AyaRuntimeException("Cannot append " + o.repr() + " to string " + repr()
					+ ". Use + to convert to string and concat or convert string to a generic list");
		}
	}

	@Override
	public void addAll(List l) {
		for (int i = 0; i < l.length(); i++) {
			addItem(l.get(i));
		}
	}



	
	
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public Str deepcopy() {
		return new Str(_str);
	}

	@Override
	public boolean bool() {
		return _str.length() != 0;
	}

	@Override
	public String repr() {
		if (_str.length() > 100) {
			return "\"" + _str.substring(0, 30) + " ... " + _str.substring(_str.length()-30) + "\"";
		} else {
			return "\"" + _str + "\"";
		}
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
	
	/**
	 * Compute the levenshtein distance of two strings
	 * @author https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private static int levenshteinDistance (CharSequence lhs, CharSequence rhs) {                          
	    int len0 = lhs.length() + 1;                                                     
	    int len1 = rhs.length() + 1;                                                     
	                                                                                    
	    // the array of distances                                                       
	    int[] cost = new int[len0];                                                     
	    int[] newcost = new int[len0];                                                  
	                                                                                    
	    // initial cost of skipping prefix in String s0                                 
	    for (int i = 0; i < len0; i++) cost[i] = i;                                     
	                                                                                    
	    // dynamically computing the array of distances                                  
	                                                                                    
	    // transformation cost for each letter in s1                                    
	    for (int j = 1; j < len1; j++) {                                                
	        // initial cost of skipping prefix in String s1                             
	        newcost[0] = j;                                                             
	                                                                                    
	        // transformation cost for each letter in s0                                
	        for(int i = 1; i < len0; i++) {                                             
	            // matching current letters in both strings                             
	            int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;             
	                                                                                    
	            // computing cost for each transformation                               
	            int cost_replace = cost[i - 1] + match;                                 
	            int cost_insert  = cost[i] + 1;                                         
	            int cost_delete  = newcost[i - 1] + 1;                                  
	                                                                                    
	            // keep minimum cost                                                    
	            newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
	        }                                                                           
	                                                                                    
	        // swap cost/newcost arrays                                                 
	        int[] swap = cost; cost = newcost; newcost = swap;                          
	    }                                                                               
	                                                                                    
	    // the distance is the cost for transforming all letters in both strings        
	    return cost[len0 - 1];                                                          
	}

	


}
