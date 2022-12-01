package aya.util;

import static aya.obj.Obj.LIST;
import static aya.obj.Obj.STR;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.NUMBERLIST;
import static aya.util.Casting.asNumber;
import static aya.util.Casting.asList;
import static aya.util.Casting.asNumberList;

import aya.exceptions.runtime.ValueError;
import aya.instruction.op.OpInstruction;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.list.numberlist.NumberListOp;

public class VectorizedFunctions {

	// Vectorized ops treat strings as atoms
	private static boolean isList(Obj a) {
		return a.isa(LIST) && !a.isa(STR);
	}

	//
	// List, Obj
	//

	private static List vectorizeListObj(OpInstruction op, List a, Obj b, NumberListOp nlop) {
		if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			return new List(nlop.ln(asNumberList(a), asNumber(b)));
		} else {
			return vectorizeListObj(op, a, b);
		}
	}

	private static List vectorizeListObj(OpInstruction op, List a, Obj b) {
		// Generic fallback
		List out = new List();
		for (int i = 0; i < a.length(); i++) {
			out.mutAdd(op.exec2arg(a.getExact(i), b));
		}
		return out;	
	}
	

	//
	// Obj, List
	//

	private static List vectorizeObjList(OpInstruction op, Obj a, List b, NumberListOp nlop) {
		if (b.isa(NUMBERLIST) && a.isa(NUMBER)) {
			return new List(nlop.nl(asNumber(a), asNumberList(b)));
		} else {
			return vectorizeObjList(op, a, b);
		}
	}

	private static List vectorizeObjList(OpInstruction op, Obj a, List b) {
		// Generic fallback
		List out = new List();
		for (int i = 0; i < b.length(); i++) {
			out.mutAdd(op.exec2arg(a, b.getExact(i)));
		}
		return out;	
	}
	

	//
	// List, List
	//

	private static List vectorizeListList(OpInstruction op, List a, List b, NumberListOp nlop) {
		if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			return new List(nlop.ll(asNumberList(a), asNumberList(b)));
		} else {
			return vectorizeListList(op, a, b);
		}
	}

	private static List vectorizeListList(OpInstruction op, List a, List b) {
		List out = new List();
		if (a.length() == b.length()) {
			for (int i = 0; i < a.length(); i++) {
				out.mutAdd(op.exec2arg(a.getExact(i), b.getExact(i)));
			}
			return out;	
		} else {
			throw new ValueError("dim mismatch");
		}
	}

	//
	// 2 Arg Driver Functions
	//
	
	public static Obj vectorize2arg(OpInstruction op, Obj a, Obj b) {
		if (isList(a) && !isList(b)) {
			return vectorizeListObj(op, asList(a), b);
		} else if (!isList(a) && isList(b)) {
			return vectorizeObjList(op, a, asList(b));
		} else if (isList(a) && isList(b)) {
			return vectorizeListList(op, asList(a), Casting.asList(b));
		} else {
			return null;
		}
	}


	public static Obj vectorize2arg(OpInstruction op, Obj a, Obj b, NumberListOp nlop) {
		if (isList(a) && !isList(b)) {
			return vectorizeListObj(op, asList(a), b, nlop);
		} else if (!isList(a) && isList(b)) {
			return vectorizeObjList(op, a, asList(b), nlop);
		} else if (isList(a) && isList(b)) {
			return vectorizeListList(op, asList(a), Casting.asList(b), nlop);
		} else {
			return null;
		}
	}
	

	//
	// 1 Arg Functions
	//

	
	private static List vectorizeList(OpInstruction op, List a, NumberListOp nlop) {
		if (a.isa(NUMBERLIST)) {
			return new List(nlop.l(asNumberList(a)));
		} else {
			return vectorizeList(op, a);
		}
	}


	private static List vectorizeList(OpInstruction op, List a) {
		// Generic fallback
		List out = new List();
		for (int i = 0; i < a.length(); i++) {
			out.mutAdd(op.exec1arg(a.getExact(i)));
		}
		return out;	
	}



	public static Obj vectorize1arg(OpInstruction op, Obj a) {
		if (isList(a)) {
			return vectorizeList(op, asList(a));
		} else {
			return null;
		}
	}


	public static Obj vectorize1arg(OpInstruction op, Obj a, NumberListOp nlop) {
		if (isList(a)) {
			if (a.isa(NUMBERLIST)) {
				return vectorizeList(op, asList(a), nlop);
			} else {
				return vectorizeList(op, asList(a));
			}
		} else {
			return null;
		}
	}
}
