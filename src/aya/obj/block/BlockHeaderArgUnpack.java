package aya.obj.block;

import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

public class BlockHeaderArgUnpack extends AbstractBlockHeaderArg {
	
	private UnpackAssignment _args;

	public BlockHeaderArgUnpack(UnpackAssignment ua) {
		_args = ua;
	}
	
	public void assign(Dict vars, Obj o) {
		_args.assign(vars, o);
	}

	@Override
	public String toString() {
		return _args.toString();
	}
	
	@Override
	protected void toDict(Dict d) {
		List args = new List();
		for (UnpackAssignment.Arg arg : _args.getArgs()) {
			Dict a = new Dict();
			a.set(SymbolConstants.NAME, arg.symbol);
			a.set(SymbolConstants.SLURP, Num.fromBool(arg.slurp));
			args.mutAdd(a);
		}
		d.set(SymbolConstants.ARGS, args);
	}

	@Override
	protected Symbol blockHeaderArgType() {
		return SymbolConstants.UNPACK;
	}
	
}
