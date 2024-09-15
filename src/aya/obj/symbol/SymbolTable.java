package aya.obj.symbol;

import java.util.HashMap;

public class SymbolTable {
	
	private volatile static HashMap<String, Symbol> _symbols = new HashMap<String, Symbol>();
	private volatile static HashMap<Integer, String> _symbols_rev = new HashMap<Integer, String>();
	private volatile static int _counter = 1; // 0: null
	
	public static Symbol getSymbol(String str) {
		Symbol sym = _symbols.get(str);
		if (sym != null) {
			return sym;
		} else {
			return newSymbol(str);
		}
	}
	
	public static String getName(Symbol s) {
		String name = _symbols_rev.get(s._id);
		if (name != null) {
			return name;
		} else {
			throw new RuntimeException("Unknown symbol!");
		}
	}
	
	private static Symbol newSymbol(String name) {
		Symbol sym = new Symbol(_counter);
		_symbols.put(name, sym);
		_symbols_rev.put(_counter,  name);
		_counter++;
		return sym;
	}

	/** Returns true if the string contains only lowercase alpha and underscores */
	public static boolean isBasicSymbolString(String name) {
		return isValidStr(name);
	}

	/** Returns true if the string contains only lowercase alpha and underscores */
	public static boolean isBasicSymbolChar(char c) {
		return isValidChar(c);
	}
	
	/** Return true if the string is a valid variable name */
	private static boolean isValidStr(String varname) {
		if (varname.length() == 0) return  false;
		char[] chars = varname.toCharArray();
		for (char c : chars) {
			if (!isValidChar(c)) return false;
		}
		
		return true;
	}

	private static boolean isValidChar(char c) {
		return (c >= 'a' && c <= 'z') || c == '_';
	}
	
}
