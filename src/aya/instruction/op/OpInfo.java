package aya.instruction.op;

import aya.Aya;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;

public class OpInfo {
	
	static Dict _op_info = null;
	
	public static void regenerateDict() {
		Dict dict = new Dict();
		_addOps(dict, Ops.OPS);
		_addOps(dict, DotOps.DOT_OPS);
		_addOps(dict, ColonOps.COLON_OPS);
		_addOps(dict, MiscOps.MATH_OPS);
		_op_info = dict;
	}
	
	private static void _addOps(Dict dict, Operator[] ops) {
		for (Operator op : ops) {
			if (op != null) {
				Symbol s = Aya.getInstance().getSymbols().getSymbol(op.getName());
				dict.set(s, op.getInfo());
			}
		}
	}
	
	public static Dict getDict() {
		if (_op_info == null) {
			regenerateDict();
		}
		return _op_info;
	}
}
