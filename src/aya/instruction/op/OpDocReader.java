package aya.instruction.op;

import java.util.ArrayList;

import aya.obj.Obj;

public class OpDocReader {
	
	public static ArrayList<Obj> toDicts(OpInstruction[] ops) {
		ArrayList<Obj> dicts  = new ArrayList<Obj>();
		for (OpInstruction op : ops) {
			if (op != null) {
				if (op.hasDocs()) {
					dicts.add(op.getDoc().toDict());
				}
			}
		}
		return dicts;
	}
	
	public static ArrayList<String> toStrings(OpInstruction[] ops) {
		ArrayList<String> out = new ArrayList<String>();
		for (OpInstruction op : ops) {
			if (op != null) {
				if (op.hasDocs()) {
					out.add(op.getDoc().toString());
				} else {
					//System.out.println("Warning: Operator " + op.getName() + " has no docs");
				}
			}
		}
		return out;
	}

	public static ArrayList<String> getAllOpDescriptions() {
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(toStrings(Ops.OPS));
		out.addAll(toStrings(DotOps.DOT_OPS));
		out.addAll(toStrings(ColonOps.COLON_OPS));
		out.addAll(toStrings(MiscOps.MATH_OPS));
		return out;
	}
	
	public static ArrayList<Obj> getAllOpDicts() {
		ArrayList<Obj> out = new ArrayList<Obj>();
		out.addAll(toDicts(Ops.OPS));
		out.addAll(toDicts(DotOps.DOT_OPS));
		out.addAll(toDicts(ColonOps.COLON_OPS));
		out.addAll(toDicts(MiscOps.MATH_OPS));
		return out;
	}

}
