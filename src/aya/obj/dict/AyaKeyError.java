package aya.obj.dict;

import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.symbol.Symbol;

@SuppressWarnings("serial")
public class AyaKeyError extends AyaRuntimeException {

	public AyaKeyError(Dict dict, Symbol key) {
		super("Unable to access dict at key '" + key.name() + "':\n" + dict.repr());
	}

	public AyaKeyError(Dict dict, String key) {
		super("Unable to access dict at key \"" + key + "\":\n" + dict.repr());
	}
	
	public AyaKeyError(Dict dict, Obj key, boolean invalid_type) {
		super("Unable to access dict using key [" + key.repr() + "] (incorrect type):\n" + dict.repr());
	}

	public AyaKeyError(Dict dict, Obj key) {
		super("Unable to access dict at key [" + key.repr() + "]:\n" + dict.repr());
	}
}
