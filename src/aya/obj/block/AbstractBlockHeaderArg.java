package aya.obj.block;

import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

public abstract class AbstractBlockHeaderArg {
	public abstract void assign(Dict vars, Obj o);

	protected abstract void toDict(Dict d);
	protected abstract Symbol blockHeaderArgType();

	public Dict toDict() {
		Dict d = new Dict();
		d.set(SymbolConstants.ARGTYPE, blockHeaderArgType());
		toDict(d);
		return d;
	}
}
