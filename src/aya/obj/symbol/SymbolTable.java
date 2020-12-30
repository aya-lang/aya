package aya.obj.symbol;

import java.util.HashMap;

import aya.exceptions.AyaRuntimeException;

public class SymbolTable {
	
	HashMap<String, Symbol> _symbols;
	HashMap<Integer, String> _symbols_rev;
	int _counter;
	
	public SymbolTable() {
		_symbols = new HashMap<String, Symbol>();
		_symbols_rev = new HashMap<Integer, String>();
		_counter = 1; // 0: null
	}
	
	public Symbol getSymbol(String str) {


		Symbol sym = _symbols.get(str);
		if (sym != null) {
			return sym;
		} else {
			return newSymbol(str);
		}
	}
	
	public String getName(Symbol s) {
		String name = _symbols_rev.get(s._id);
		if (name != null) {
			return name;
		} else {
			throw new RuntimeException("Unknown symbol!");
		}
	}
	
	private Symbol newSymbol(String name) {
		if (!isValidStr(name)) {
			throw new RuntimeException("Invalid symbol name: '" + name + "'");
		}
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
		char[] chars = varname.toCharArray();
		for (char c : chars) {
			if (!isValidChar(c)) return false;
		}
		
		return true;
	}

	private static boolean isValidChar(char c) {
		return (c >= 'a' && c <= 'z') || c == '_';
	}
	
	/** Converts any string to a symbol string by ignoring non alpha chars */
	public static String toBasicSymbolName(String str) {
		String sym = "";
		
		for (char c : str.toCharArray()) {
			if (isValidChar(c)) {
				sym += c;
			} else if (c >= 'A' && c <= 'Z') {
				sym += Character.toLowerCase(c); // Make lowercase
			} else if (c == ' ') {
				sym += '_';
			}
		}
		
		if (sym.equals("")) {
			throw new AyaRuntimeException("Can't create symbol from string \"" + str + "\"");
		}
		
		return sym;
	}
	

}
