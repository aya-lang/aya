package aya.util;

import aya.exceptions.runtime.AyaRuntimeException;
import aya.exceptions.runtime.IndexError;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.symbol.Symbol;

public class StaticDictReader {
	public static AyaRuntimeException notFound(String err_message, Dict d, Symbol key) throws AyaRuntimeException {
		return new IndexError(d, key, err_message);
	}
	
	public static AyaRuntimeException badType(String err_message, Symbol key, String typeExpected, Obj got) throws AyaRuntimeException {
		return new ValueError(err_message + ": Expected type ::" + typeExpected +" for key '" + key.name() + "' but got " + got.repr());
	}
	
	//
	// Symbol
	//
	
	public static Symbol getSymbolEx(Dict d, Symbol key, String err_message) throws AyaRuntimeException {
		Obj o = d.getSafe(key);
		if (o == null) {
			throw notFound(err_message, d, key);
		} else if (!o.isa(Obj.SYMBOL)) {
			throw badType(err_message, key, "sym", o);
		} else {
			return (Symbol)o;
		}
	}
	
	public static Symbol getSymbol(Dict d, Symbol key, Symbol dflt) {
		Obj o = d.getSafe(key);
		
		if (o == null || !o.isa(Obj.SYMBOL)) {
			return dflt;
		} else {
			return (Symbol)o;
		}
	}
	
	//
	// Bool
	//

	public static boolean getBool(Dict d, Symbol key, boolean dflt) {
		Obj o = d.getSafe(key);
		
		if (o == null) {
			return dflt;
		} else {
			return o.bool();
		}
	}
	
	//
	// List
	//

	public static List getListEx(Dict d, Symbol key, String err_message) {		
		Obj o = d.getSafe(key);
		if (o == null) {
			throw notFound(err_message, d, key);
		} else if (!o.isa(Obj.LIST)) {
			throw badType(err_message, key, "list", o);
		} else {
			return Casting.asList(o);
		}
	}
	
	//
	// Dict
	//
	
	public static Dict getDictEx(Dict d, Symbol key, String err_message) {
		Obj o = d.getSafe(key);
		if (o == null) {
			throw notFound(err_message, d, key);
		} else if (!o.isa(Obj.DICT)) {
			throw badType(err_message, key, "dict", o);
		} else {
			return Casting.asDict(o);
		}
	}
}
