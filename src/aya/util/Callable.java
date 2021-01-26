package aya.util;

import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.symbol.SymbolConstants;

public class Callable {

	/** Returns the code block of the callable object
	 * 	Returns null if the object is not callable
	 * @param o
	 * @return
	 */
	public static Block getCallable(Obj o) {
		byte type = o.type();
		switch (type) {
		case Obj.BLOCK:
			return Casting.asBlock(o);
		case Obj.DICT:
			Dict d = Casting.asDict(o);
			Obj call = d.get(SymbolConstants.__CALL__, null);
			return call == null ? null : getCallable(call);
		default:
			return null;
		}
	}
}
