package aya.exceptions.runtime;

import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.util.Casting;

@SuppressWarnings("serial")
public class IndexError extends InternalAyaRuntimeException {

	// List.index converts a negative index to a backward index by simply adding the length.
	// If the magnitude of the negative index is larger than the list we will get an
	// out of bounds error but the index in the error will be off by the length of the list
	// This function allows us to undo it before sending the error
	// For example (without this function):
	//  aya> [1 2 3] :20 I
	//  Error: index out of bounds: -17
	// Since List.index([1 2 3], :20) returns -20 + 3 => -17
	public static Obj resolveNegativeListIndex(List l, Obj index) {
		if (index.isa(Obj.NUMBER)) {
			int idx = Casting.asNumber(index).toInt();
			if (idx < 0) {
				idx -= l.length();
			}
			return Num.fromInt(idx);
		} else {
			return index;
		}
	}
	
	public IndexError(List l, Obj index) {
		super(SymbolConstants.INDEX_ERR, "Invalid index " + resolveNegativeListIndex(l, index).repr() + " for list " + l.repr());
	}
	
	public IndexError(Dict dict, Obj index, String message) {
		super(SymbolConstants.INDEX_ERR, "Invalid index " + index.repr() + " for dict " + dict.repr() + ": " + message);
	}

	public IndexError(Dict dict, Symbol key) {
		super(SymbolConstants.INDEX_ERR, "Dict does not contain key '" + key.name() + "':\n" + dict.repr());
	}

	public IndexError(Dict dict, String key) {
		super(SymbolConstants.INDEX_ERR, "Dict does not contain key \"" + key + "\":\n" + dict.repr());
	}
	
	public IndexError(Dict dict, Obj key, boolean invalid_type) {
		super(SymbolConstants.INDEX_ERR, "Dict does not contain key [" + key.repr() + "] (incorrect type):\n" + dict.repr());
	}

	public IndexError(Dict dict, Obj key) {
		super(SymbolConstants.INDEX_ERR, "Dict does not contain key [" + key.repr() + "]:\n" + dict.repr());
	}

	public IndexError(String string) {
		super(SymbolConstants.INDEX_ERR, string);
	}

}
