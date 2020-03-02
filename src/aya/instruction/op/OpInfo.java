package aya.instruction.op;

import aya.entities.operations.ColonOps;
import aya.entities.operations.DotOps;
import aya.entities.operations.MiscOps;
import aya.entities.operations.Ops;
import aya.obj.dict.Dict;

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
	
	private static void _addOps(Dict dict, OpInstruction[] ops) {
		for (OpInstruction op : ops) {
			if (op != null) {
				dict.set(op.getName(), op.getInfo());
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
