package aya.util;

import static aya.obj.Obj.LIST;
import static aya.obj.Obj.STR;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.NUMBERLIST;
import static aya.util.Casting.asNumber;
import static aya.util.Casting.asList;
import static aya.util.Casting.asNumberList;

import aya.ReprStream;
import aya.eval.AyaThread;
import aya.exceptions.runtime.ValueError;
import aya.instruction.op.Operator;
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

	private static List vectorizeListObj(AyaThread context, Operator op, List a, Obj b, NumberListOp nlop) {
		if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			return new List(nlop.ln(asNumberList(a), asNumber(b)));
		} else {
			return vectorizeListObj(context, op, a, b);
		}
	}

	private static List vectorizeListObj(AyaThread context, Operator op, List a, Obj b) {
		// Generic fallback
		List out = new List();
		for (int i = 0; i < a.length(); i++) {
			out.mutAdd(op.exec2arg(context, a.getExact(i), b));
		}
		return out;	
	}
	

	//
	// Obj, List
	//

	private static List vectorizeObjList(AyaThread context, Operator op, Obj a, List b, NumberListOp nlop) {
		if (b.isa(NUMBERLIST) && a.isa(NUMBER)) {
			return new List(nlop.nl(asNumber(a), asNumberList(b)));
		} else {
			return vectorizeObjList(context, op, a, b);
		}
	}

	private static List vectorizeObjList(AyaThread context, Operator op, Obj a, List b) {
		// Generic fallback
		List out = new List();
		for (int i = 0; i < b.length(); i++) {
			out.mutAdd(op.exec2arg(context, a, b.getExact(i)));
		}
		return out;	
	}
	

	//
	// List, List
	//

	private static List vectorizeListList(AyaThread context, Operator op, List a, List b, NumberListOp nlop) {
		if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			return new List(nlop.ll(asNumberList(a), asNumberList(b)));
		} else {
			return vectorizeListList(context, op, a, b);
		}
	}

	private static List vectorizeListList(AyaThread context, Operator op, List a, List b) {
		List out = new List();
		if (a.length() == b.length()) {
			for (int i = 0; i < a.length(); i++) {
				out.mutAdd(op.exec2arg(context, a.getExact(i), b.getExact(i)));
			}
			return out;	
		} else {
			ReprStream msg = new ReprStream();
			msg.println("Dimension mismatch. Lengths must be equal but are: " + a.length() + ", " + b.length());
			a.repr(msg);
			msg.println();
			b.repr(msg);
			throw new ValueError(msg.toString());
		}
	}

	//
	// 2 Arg Driver Functions
	//
	
	public static Obj vectorize2arg(AyaThread context, Operator op, Obj a, Obj b) {
		final boolean a_is_list = isList(a);
		final boolean b_is_list = isList(b);
		if (a_is_list && !b_is_list) {
			return vectorizeListObj(context, op, (List)a, b);
		} else if (!a_is_list && b_is_list) {
			return vectorizeObjList(context, op, a, (List)b);
		} else if (a_is_list && b_is_list) {
			return vectorizeListList(context, op, (List)a, (List)b);
		} else {
			return null;
		}
	}


	public static Obj vectorize2arg(AyaThread context, Operator op, Obj a, Obj b, NumberListOp nlop) {
		final boolean a_is_list = isList(a);
		final boolean b_is_list = isList(b);
		if (a_is_list && !b_is_list) {
			return vectorizeListObj(context, op, (List)a, b, nlop);
		} else if (!a_is_list && b_is_list) {
			return vectorizeObjList(context, op, a, (List)b, nlop);
		} else if (a_is_list && b_is_list) {
			return vectorizeListList(context, op, (List)a, (List)b, nlop);
		} else {
			return null;
		}
	}
	

	//
	// 1 Arg Functions
	//

	
	private static List vectorizeList(AyaThread context, Operator op, List a, NumberListOp nlop) {
		if (a.isa(NUMBERLIST)) {
			return new List(nlop.l(asNumberList(a)));
		} else {
			return vectorizeList(context, op, a);
		}
	}


	private static List vectorizeList(AyaThread context, Operator op, List a) {
		// Generic fallback
		List out = new List();
		for (int i = 0; i < a.length(); i++) {
			out.mutAdd(op.exec1arg(context, a.getExact(i)));
		}
		return out;	
	}



	public static Obj vectorize1arg(AyaThread context, Operator op, Obj a) {
		if (isList(a)) {
			return vectorizeList(context, op, asList(a));
		} else {
			return null;
		}
	}


	public static Obj vectorize1arg(AyaThread context, Operator op, Obj a, NumberListOp nlop) {
		if (isList(a)) {
			if (a.isa(NUMBERLIST)) {
				return vectorizeList(context, op, asList(a), nlop);
			} else {
				return vectorizeList(context, op, asList(a));
			}
		} else {
			return null;
		}
	}
}
