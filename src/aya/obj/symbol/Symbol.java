package aya.obj.symbol;

import java.util.WeakHashMap;

import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.variable.Variable;

public class Symbol extends Obj {
	
	static WeakHashMap<Long, Symbol> cache = new WeakHashMap<Long, Symbol>();
	
	long _id;

	
	private Symbol(String name) {
		_id = Variable.encodeString(name);
	}
	
	private Symbol(long id) {
		_id = id;
	}
	
	/** Converts any string to a symbol string by ignoring non alpha chars */
	public static Symbol convToSymbol(String str) {
		String sym = "";
		
		for (char c : str.toCharArray()) {
			if (c >= 'a' && c <= 'z') {
				sym += c;
			} else if (c >= 'A' && c <= 'Z') {
				sym += c + 32; // Make lowercase
			}
			
			// Symbols can only be 12 chars
			if (sym.length() >= 12) {
				break;
			}
		}
		
		if (sym.equals("")) {
			throw new AyaRuntimeException("Can't create symbol from string \"" + sym + "\"");
		}
		
		return Symbol.fromStr(str);
	}
	
	/** Convert a symbol string to a symbol */
	public static Symbol fromStr(String data) {
		long id = Variable.encodeString(data);
		
		// Check if it is in the cache
		Symbol s = cache.get(id);
		if (s == null) {
			// Not in cache, add it
			s = new Symbol(id);
			cache.put(id, s);
		}
		return s;
	}	
	
	@Override
	public Obj deepcopy() {
		return this;
	}

	@Override
	public boolean bool() {
		return true;
	}

	@Override
	public String repr() {
		return "::" + Variable.decodeLong(_id);
	}

	@Override
	public String str() {
		return "::" + Variable.decodeLong(_id);
	}

	@Override
	public boolean equiv(Obj o) {
		return o instanceof Symbol && ((Symbol)o)._id == _id;
	}

	@Override
	public boolean isa(byte type) {
		return type == Obj.SYMBOL;
	}

	@Override
	public byte type() {
		return Obj.SYMBOL;
	}



}
