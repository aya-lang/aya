package aya.exceptions.runtime;

import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

@SuppressWarnings("serial")
public class IndexError extends InternalAyaRuntimeException {

	public IndexError(List l, Obj index) {
		super(SymbolConstants.INDEX_ERR, "Invalid index " + index.repr() + " for list " + l.repr());
	}
	
	public IndexError(Dict dict, Obj index, String message) {
		super(SymbolConstants.INDEX_ERR, "Invalid index " + index.repr() + " for dict " + dict.repr() + ": " + message);
	}

	public IndexError(Dict dict, Symbol key) {
		super(SymbolConstants.INDEX_ERR, "Unable to access dict at key '" + key.name() + "':\n" + dict.repr());
	}

	public IndexError(Dict dict, String key) {
		super(SymbolConstants.INDEX_ERR, "Unable to access dict at key \"" + key + "\":\n" + dict.repr());
	}
	
	public IndexError(Dict dict, Obj key, boolean invalid_type) {
		super(SymbolConstants.INDEX_ERR, "Unable to access dict using key [" + key.repr() + "] (incorrect type):\n" + dict.repr());
	}

	public IndexError(Dict dict, Obj key) {
		super(SymbolConstants.INDEX_ERR, "Unable to access dict at key [" + key.repr() + "]:\n" + dict.repr());
	}

	public IndexError(String string) {
		super(SymbolConstants.INDEX_ERR, string);
	}

}
