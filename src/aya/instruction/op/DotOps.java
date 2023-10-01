package aya.instruction.op;


import static aya.obj.Obj.BLOCK;
import static aya.obj.Obj.CHAR;
import static aya.obj.Obj.DICT;
import static aya.obj.Obj.LIST;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.STR;
import static aya.obj.Obj.SYMBOL;
import static aya.util.Casting.asChar;
import static aya.util.Casting.asDict;
import static aya.util.Casting.asList;
import static aya.util.Casting.asNumber;
import static aya.util.Casting.asNumberList;
import static aya.util.Casting.asStaticBlock;
import static aya.util.Casting.asStr;
import static aya.util.Casting.asSymbol;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.regex.Pattern;

import aya.Aya;
import aya.AyaPrefs;
import aya.exceptions.parser.NotAnOperatorError;
import aya.exceptions.parser.ParserException;
import aya.exceptions.runtime.AyaRuntimeException;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.InternalAyaRuntimeException;
import aya.exceptions.runtime.MathError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.UnimplementedError;
import aya.exceptions.runtime.UserObjRuntimeException;
import aya.exceptions.runtime.ValueError;
import aya.ext.dialog.QuickDialog;
import aya.instruction.DataInstruction;
import aya.instruction.ListBuilderInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.dict.DictIndexing;
import aya.obj.list.List;
import aya.obj.list.ListRangeUtils;
import aya.obj.list.Str;
import aya.obj.list.numberlist.NumberList;
import aya.obj.list.numberlist.NumberListOp;
import aya.obj.number.BaseConversion;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.NumberMath;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.Parser;
import aya.parser.ParserString;
import aya.parser.SourceString;
import aya.parser.SourceStringRef;
import aya.util.Casting;
import aya.util.VectorizedFunctions;

public class DotOps {

	public static final char FIRST_OP = '!';


	/** A list of all valid single character operations.
	 *  Stored in final array for fast lookup.
	 *  Array indexes are always [(operator character) - FIRST_OP]
	 */
	public static Operator[] DOT_OPS = {
		/* 33 !  */ new OP_Dot_Bang(),
		/* 34 "  */ null, // String symbol literal
		/* 35 #  */ null, //Comment
		/* 36 $  */ new OP_Dot_Duplicate(),
		/* 37 %  */ new OP_Dot_Percent(),
		/* 38 &  */ new OP_Dot_And(),
		/* 39 '  */ new OP_Dot_CastChar(),
		/* 40 (  */ new OP_Dot_OParen(),
		/* 41 )  */ new OP_Dot_CParen(),
		/* 42 *  */ new OP_Dot_Star(),
		/* 43 +  */ new OP_Dot_Plus(),
		/* 44 ,  */ null,
		/* 45 -  */ new OP_Dot_Minus(),
		/* 46 .  */ null, // Reserved
		/* 47 /  */ new OP_Dot_FwdSlash(),
		/* 48 0  */ null, //Number Literal
		/* 49 1  */ null, //Number Literal
		/* 50 2  */ null, //Number Literal
		/* 51 3  */ null, //Number Literal
		/* 52 4  */ null, //Number Literal
		/* 53 5  */ null, //Number Literal
		/* 54 6  */ null, //Number Literal
		/* 55 7  */ null, //Number Literal
		/* 56 8  */ null, //Number Literal
		/* 57 9  */ null, //Number Literal
		/* 58    */ null,
		/* 59 ;  */ new OP_Dot_ClearAll(),
		/* 60 <  */ new OP_Dot_LessThan(),
		/* 61 =  */ new OP_Dot_Equals(),
		/* 62 >  */ new OP_Dot_GreaterThan(),
		/* 63 ?  */ new OP_Dot_Conditional(),
		/* 64 @  */ new OP_Dot_At(),
		/* 65 A  */ new OP_Dot_ArrayAll(),
		/* 66 B  */ new OP_Dot_Append(),
		/* 67 C  */ new OP_Dot_SortUsing(),
		/* 68 D  */ new OP_Dot_Error(),
		/* 69 E  */ new OP_Dot_Len(),
		/* 70 F  */ new OP_Dot_Flatten(),
		/* 71 G  */ new OP_Dot_Write(),
		/* 72 H  */ null,
		/* 73 I  */ new OP_Dot_I(),
		/* 74 J  */ null,
		/* 75 K  */ new OP_Dot_TryCatch(),
		/* 76 L  */ null,
		/* 77 M  */ new OP_Dot_M(),
		/* 78 N  */ new OP_Dot_N(),
		/* 79 O  */ new OP_Dot_O(),
		/* 80 P  */ new OP_Dot_Print(),
		/* 81 Q  */ new OP_Dot_Rand(),
		/* 82 R  */ new OP_Dot_R(),
		/* 83 S  */ new OP_Dot_S(),
		/* 84 T  */ new OP_Dot_T(),
		/* 85 U  */ new OP_RequestString(),
		/* 86 V  */ new OP_Dot_AppendBack(),
		/* 87 W  */ null,
		/* 88 X  */ null,
		/* 89 Y  */ null,
		/* 90 Z  */ null,
		/* 91 [  */ null,
		/* 92 \  */ new OP_Dot_BackSlash(),
		/* 93 ]  */ null,
		/* 94 ^  */ new OP_Dot_Pow(),
		/* 95 _  */ null, // Member Variable
		/* 96 `  */ null, // Block variable escape
		/* 97 a  */ null, // Member Variable
		/* 98 b  */ null, // Member Variable
		/* 99 c  */ null, // Member Variable
		/* 100 d */ null, // Member Variable
		/* 101 e */ null, // Member Variable
		/* 102 f */ null, // Member Variable
		/* 103 g */ null, // Member Variable
		/* 104 h */ null, // Member Variable
		/* 105 i */ null, // Member Variable
		/* 106 j */ null, // Member Variable
		/* 107 k */ null, // Member Variable
		/* 108 l */ null, // Member Variable
		/* 109 m */ null, // Member Variable
		/* 110 n */ null, // Member Variable
		/* 111 o */ null, // Member Variable
		/* 112 p */ null, // Member Variable
		/* 113 q */ null, // Member Variable
		/* 114 r */ null, // Member Variable
		/* 115 s */ null, // Member Variable
		/* 116 t */ null, // Member Variable
		/* 117 u */ null, // Member Variable
		/* 118 v */ null, // Member Variable
		/* 119 w */ null, // Member Variable
		/* 120 x */ null, // Member Variable
		/* 121 y */ null, // Member Variable
		/* 122 z */ null, // Member Variable
		/* 123 { */ null, // block comments
		/* 124 | */ new OP_Dot_Bar(),
		/* 125 } */ null, // block comments
		/* 126 ~ */ new OP_Dot_Tilde(),
	};

	/** Returns the operation bound to the character */
	public static OperatorInstruction getOp(char c, SourceStringRef source) throws NotAnOperatorError {
		OperatorInstruction op = getOpOrNull(c, source);
		if (op == null) {
			throw new NotAnOperatorError("." + c, source);
		} else {
			return op;
		}
	}
	
	public static OperatorInstruction getOpOrNull(char op, SourceStringRef source) {
		if(op >= 33 && op <= 126) {
			Operator operator = DOT_OPS[op-FIRST_OP];
			if (operator == null) {
				return null;
			} else  {
				return new OperatorInstruction(source, operator);
			}
		} else {
			return null;
		}
	}

}

// ! - 33
class OP_Dot_Bang extends Operator {

	public OP_Dot_Bang() {
		this.name = ".!";
		init(".!");
		arg("N", "signum");
		arg("S", "parse if number");
		arg("B", "copy block without header");
		setOverload(1, "signum");
		vect();
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.signnum(); }
	};

	@Override
	public void execute(final Block block) {
		Obj a = block.pop();
		block.push(exec1arg(a));
	}

	@Override
	public Obj exec1arg(final Obj a) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize1arg(this, a, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(a)) != null) return res; // stack order

		if (a.isa(NUMBER)) {
			return ((Number)a).signnum();
		} else if (a.isa(STR)) {
			String numStr = a.str().trim();
			ParserString ps = new ParserString(new SourceString(numStr, ".!"));
			Number n;
			try {
				n = Parser.parseNumber(ps).numValue();
			} catch (ParserException e) {
				// Error converting to number
				return a;
			}
			if (ps.hasNext()) {
				// The full string wasn't used, it is not completely a number
				return a;
			} else {
				return n;
			}
		} else if (a.isa(BLOCK)) {
			return BlockUtils.stripHeader(Casting.asStaticBlock(a));
		} else {
			throw new TypeError(this, a);
		}
	}
}

// " - 34
class OP_Dot_CastChar extends Operator {

	public OP_Dot_CastChar() {
		init(".'");
		arg("N|S", "cast to char");
		arg("L", "convert number list to string using UTF-8 encoding");
	}

	@Override
	public void execute(final Block block) {
		Obj o = block.pop();

		if (o.isa(NUMBER)) {
			block.push( Char.valueOf(((Number)o).toInt()) );
		} else if (o.isa(STR)) {
			block.push( Char.valueOf(o.str().charAt(0)) );
		} else if (o.isa(LIST)) {
			block.push( new List(Str.fromBytes(asNumberList(o).toByteArray())) );
		} else if (o.isa(CHAR)) {
			block.push(o);
		} else {
			throw new TypeError(this,o);
		}
	}
	
}


// $ - 36
class OP_Dot_Duplicate extends Operator {

	public OP_Dot_Duplicate() {
		init(".$");
		arg("..AN", "copies the Nth item on the stack to the top (not including N)");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();

		if (a.isa(NUMBER)) {
			int size = block.getStack().size();
			int i = ((Number)a).toInt();

			if (i > size || i <= 0) {
				throw new ValueError(i + " .$ stack index out of bounds");
			} else {
				final Obj cp = block.getStack().get(size - i);
				block.push(cp.deepcopy());
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}


// % - 37
class OP_Dot_Percent extends Operator {

	public OP_Dot_Percent() {
		init(".%");
		arg("NN", "integer division");
		setOverload(2, "idiv");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.idiv(b);}
		public NumberList nl(Number a, NumberList b) { return b.idivFrom(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.idiv(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};

	@Override
	public void execute(final Block block) {
		final Obj b = block.pop();
		final Obj a = block.pop();
		block.push(exec2arg(a, b));
	}

	// a b .% => "a .% b"
	@Override
	public Obj exec2arg(final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(this, a, b, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(b, a)) != null) return res; // stack order

		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			try {
				return NumberMath.idiv(asNumber(a), asNumber(b));
			} catch (ArithmeticException e) {
				throw new MathError("Divide by 0 in expression " + b.str() + " " + a.str() + " .%"); // stack order
			}
		} else {
			throw new TypeError(this, b, a); // stack order
		}
	}
}



// & - 38
class OP_Dot_And extends Operator {

	public OP_Dot_And() {
		init(".&");
		arg("SSS", "replace all occurances of the regex S1 with S2 in S3");
		arg("LLB", "zip with");
		arg("SNN|LNN|NNN", "convert base of N|S|L from N1 to N2");
	}

	@Override
	public void execute(final Block block) {
		Obj a = block.pop();  // replace
		Obj b = block.pop();  // find
		Obj c = block.pop();  // str

		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			// stack order: num from to op
			block.push(convertBase(a, b, c));
		} else if ( c.isa(STR) && (a.isa(STR) || a.isa(CHAR)) && (b.isa(STR) || b.isa(CHAR))) {
			block.push(List.fromString( c.str().replaceAll(b.str(), a.str()) ));
		} else if (a.isa(BLOCK) && b.isa(LIST) && c.isa(LIST)) {
			StaticBlock initial = StaticBlock.EMPTY;
			BlockUtils.addObjToStack(initial, c);
			BlockUtils.addObjToStack(initial, b);
			ListBuilderInstruction lb = new ListBuilderInstruction(null, initial, asStaticBlock(a), null, 0);
			block.add(lb);
		} else {
			throw new TypeError(this,a,b,c);
		}
	}

	private Obj convertBase(Obj to_b, Obj from_b, Obj num) {
		try {
			return BaseConversion.convertBase(asNumber(from_b).toInt(), asNumber(to_b).toInt(), num);
		} catch (NumberFormatException nfe) {
			throw new ValueError("base conversion: invalid number format (" 
					+ num.repr() + ", " + from_b.repr() + ", " + to_b.repr() + ")");
		} catch (TypeError te) {
			throw new TypeError(this, num, from_b, to_b);
		}
	}
}

// ( - 40
class OP_Dot_OParen extends Operator {

	public OP_Dot_OParen() {
		init(".(");
		arg("NN", "left bitwise shift");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();

		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			// Reverse ops
			block.push( NumberMath.leftShift((Number)b, (Number)a) );
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// ) - 41
class OP_Dot_CParen extends Operator {


	public OP_Dot_CParen() {
		init(".)");
		arg("NN", "signed right bitwise shift");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();

		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			// Reverse ops
			block.push( NumberMath.signedRightShift((Number)b, (Number)a) );
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// * - 42 
class OP_Dot_Star extends Operator {

	public OP_Dot_Star() {
		init(".*");
		arg("L", "compile");
		arg("B", "decompile");
	}
	
	@Override
	public void execute(Block block) {
		final Obj a = block.pop();

		if (a.isa(LIST)) {
			block.push(BlockUtils.fromList(asList(a)));
		} else if (a.isa(BLOCK)) {
			block.push(BlockUtils.split(asStaticBlock(a)));
		} else {
			throw new TypeError(this, a);
		}
	}
}


// + - 43
class OP_Dot_Plus extends Operator {

	public OP_Dot_Plus() {
		init(".+");
		arg("NN", "gdc");
		arg("BD", "swap vars in a copy of B for values defined in D");
		arg("BJ", "constant capture variable from outer scope");
		arg("BL<J>", "constant capture variables from outer scope");
		arg("DD", "update D1 with the values from D2 (modify D1)");
	}



	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();

		// GCD
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push(NumberMath.gcd((Number)a, (Number)b));
		} else if (b.isa(BLOCK)) {
			StaticBlock blk = asStaticBlock(b);
			// Constant capture from dict
			if (a.isa(DICT)) {
				blk = BlockUtils.assignVarValues(Casting.asDict(a), blk);
			}
			// Constant capture from scope
			else if (a.isa(SYMBOL)) {
				blk = BlockUtils.capture(blk, (Symbol)a);
			}
			// Constant capture from scope (list)
			else if (a.isa(LIST)) {
				List l = asList(a);
				for (int i = 0; i < l.length(); i++) {
					final Obj s = l.getExact(i);
					if (s.isa(SYMBOL)) {
						blk = BlockUtils.capture(blk, (Symbol)s);
					} else {
						throw new ValueError(".+ Expected list of symbols. Got:\n" + a.repr());
					}
				}
			}
			
			block.push(blk);
		} else if (a.isa(DICT) && b.isa(DICT)) {
			((Dict)b).update((Dict)a);
			block.push(b);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// - - 45
class OP_Dot_Minus extends Operator {

	public OP_Dot_Minus() {
		init(".-");
		arg("NN", "lcm");
		arg("LN", "remove item at index N from L");
		arg("LL", "remove items at indices L1 from L2");
		arg("DJ", "remove key from dict");
		arg("DS", "remove key from dict");
	}

	public boolean rmFromDict(Dict d, Obj idx) {
		if (idx.isa(SYMBOL)) {
			d.remove((Symbol)idx);
		} else if (idx.isa(STR)) {
			Symbol s = Aya.getInstance().getSymbols().getSymbol(idx.str());
			d.remove(s);
		} else if (idx.isa(LIST)) {
			List l = asList(idx);
			for (int i = 0; i < l.length(); i++) {
				if (!rmFromDict(d, l.getExact(i))) return false;
			}
		} else {
			return false;
		}
		
		return true;
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();

		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push(NumberMath.lcm((Number)a, (Number)b));
		} else if (b.isa(LIST) && a.isa(NUMBER)) {
			asList(b).mutRemoveIndexed(asNumber(a).toInt());
			block.push(b);
		} else if (a.isa(LIST) && b.isa(LIST)) {
			asList(b).mutRemoveAllIndexed(asNumberList(a).toIntArray());
			block.push(b);
		} else if (b.isa(DICT)) {
			Dict d = (Dict)b;
			if (!rmFromDict(d, a)) {
				throw new TypeError(this, a, b);
			}
			block.push(d);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// / - 47
class OP_Dot_FwdSlash extends Operator {

	public OP_Dot_FwdSlash() {
		init("./");
		arg("N", "ceiling");
		setOverload(1, "ceil");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.ceil(); }
	};

	@Override
	public void execute(final Block block) {
		Obj a = block.pop();
		block.push(exec1arg(a));
	}

	@Override
	public Obj exec1arg(final Obj a) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize1arg(this, a, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(a)) != null) return res; // stack order
		
		if (a.isa(NUMBER)) {
			return ((Number)a).ceil();
		} else {
			throw new TypeError(this, a);
		}
	}
}

// ; - 59
class OP_Dot_ClearAll extends Operator {

	public OP_Dot_ClearAll() {
		init(".;");
		arg("..A", "clear the entire stack");
	}

	@Override
	public void execute(Block block) {
		block.clearStack();
	}
}

// < - 60
class OP_Dot_LessThan extends Operator {

	public OP_Dot_LessThan() {
		init(".<");
		arg("LN", "head / pad 0");
		arg("SN", "head / pad ' '");
		arg("NN|SS|CC", "greater of");
		setOverload(-1, "head");
	}

	@Override
	public void execute(Block block) {
		Obj b = block.pop();			// Popped in Reverse Order
		Obj a = block.pop();

		if (b.isa(NUMBER) && a.isa(LIST)) {
			block.push(asList(a).headIndexed(asNumber(b).toInt()));
		} else if (a.isa(NUMBER) && b.isa(NUMBER)) {
			if ( ((Number)a).compareTo((Number)b) > 0) {
				block.push(a);
			} else {
				block.push(b);
			}
		} else if (a.isa(STR) && b.isa(STR)) {
			if ( asStr(a).compareTo(asStr(b)) > 0) {
				block.push(a);
			} else {
				block.push(b);
			}
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			if (asChar(a).compareTo(asChar(b)) > 0) {
				block.push(a);
			} else {
				block.push(b);
			}
		} else if (a.isa(DICT)) {
			block.callVariable((Dict)a, SymbolConstants.KEYVAR_HEAD, b);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}


// > - 62
class OP_Dot_GreaterThan extends Operator {

	public OP_Dot_GreaterThan() {
		init(".>");
		arg("LN", "tail / pad 0");
		arg("SN", "tail / pad ' '");
		arg("NN|CC|SS", "lesser of");
		setOverload(-1, "tail");

	}

	@Override
	public void execute(Block block) {
		Obj b = block.pop();			// Popped in Reverse Order
		Obj a = block.pop();


		if (b.isa(NUMBER) && a.isa(LIST)) {
			block.push(asList(a).tailIndexed(asNumber(b).toInt()));
		} else if (a.isa(NUMBER) && b.isa(NUMBER)) {
			if ( ((Number)a).compareTo((Number)b) < 0) {
				block.push(a);
			} else {
				block.push(b);
			}
		} else if (a.isa(STR) && b.isa(STR)) {
			if (asStr(a).compareTo(asStr(b)) < 0) {
				block.push(a);
			} else {
				block.push(b);
			}
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			if ( ((Char)a).compareTo((Char)b) < 0) {
				block.push(a);
			} else {
				block.push(b);
			}
		} else if (a.isa(DICT)) {
			block.callVariable((Dict)a, SymbolConstants.KEYVAR_TAIL, b);
		} else {
			throw new TypeError(this, a, b);
		}

	}
}

// = 61 new OP_Dot_Equals(),
class OP_Dot_Equals extends Operator {

	public OP_Dot_Equals() {
		init(".=");
		arg("LL|AL|LA", "element-wise equivalence");
	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();

		if (a.isa(DICT) && b.isa(DICT)) {
			block.push(a.equiv(b) ? Num.ONE : Num.ZERO);
		} else if (a.isa(LIST) && b.isa(LIST)) {
			block.push(asList(a).equalsElementwise(asList(b)));
		} else if ( a.isa(LIST) ) {
			block.push(asList(a).equalsElementwise(b));
		} else if ( b.isa(LIST) ) {
			block.push(asList(b).equalsElementwise(a));
		} else {
			throw new TypeError(this, a, b);
		}
	}

}


// ? - 63
class OP_Dot_Conditional extends Operator {

	public OP_Dot_Conditional() {
		init(".?");
		arg("AAA", "if A1 then A2, else A3. If A2/A3 are blocks, execute");
	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		final Obj c = block.pop();
		//  c     b      a
		// cond {then} {else}

		if(c.bool()) {
			if(b.isa(BLOCK)) {
				block.dump(asStaticBlock(b));
			} else {
				block.push(b);
			}
		} else {
			if(a.isa(BLOCK)) {
				block.dump(asStaticBlock(a));
			} else {
				block.push(a);
			}
		}
	}

}

// @ - 64
class OP_Dot_At extends Operator {

	public OP_Dot_At() {
		init(".@");
		arg("..AN", "moves the Nth item on the stack (not including N) to the top");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();

		if (a.isa(NUMBER)) {
			int size = block.getStack().size();
			int i = ((Number)a).toInt();

			if (i > size || i <= 0) {
				throw new ValueError(i + " .@ stack index out of bounds");
			} else {
				final Obj cp = block.getStack().get(size - i);
				block.getStack().remove(size - i);
				block.push(cp);
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}


// A - 65
class OP_Dot_ArrayAll extends Operator {

	public OP_Dot_ArrayAll() {
		init(".A");
		arg("..A", "wrap entire stack in a list");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Block block) {
		ArrayList<Obj> list = new ArrayList<Obj>();
		list.addAll((Stack<Obj>)block.getStack().clone());
		block.clearStack();
		block.push(new List(list));
	}
}

// B - 66
class OP_Dot_Append extends Operator {

	public OP_Dot_Append() {
		init(".B");
		arg("AL", "append item to the back of a list");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();

		if (a.isa(LIST)) {
			asList(a).mutAdd(b);
			block.push(a);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// C - 67
class OP_Dot_SortUsing extends Operator {

	public OP_Dot_SortUsing() {
		init(".C");
		arg("LB", "sort least to greatest by applying B to L");
		arg("NN", "xor");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();

		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push(NumberMath.bxor((Number)a, (Number)b));
		}
		else if (a.isa(BLOCK) && b.isa(LIST)) {
			final StaticBlock blk = asStaticBlock(a);
			List objs = asList(b);
			List key_obj = objs.map(blk);

			//Convert keys to int array
			ArrayList<SUItem> items = new ArrayList<SUItem>(key_obj.length());
			try {

				for (int i = 0; i < objs.length(); i++) {
					items.add(new SUItem(objs.getExact(i), (Comparable) key_obj.getExact(i)));
				}
				Collections.sort(items);

			} catch (ClassCastException e) {
				throw new ValueError(".C: all objects must be comparable to each other");
			}



			ArrayList<Obj> out = new ArrayList<Obj>(items.size());
			for (SUItem i : items) {
				out.add(i.o);
			}

			block.push(new List(out));

		}
		else {
			throw new TypeError(this, a);
		}
	}

	@SuppressWarnings("rawtypes")
	class SUItem<T extends Comparable> implements Comparable<SUItem<T>>{
		public Obj o;
		public T d;
		public SUItem(Obj o, T d) {
			this.o = o;
			this.d = d;
		}
		@SuppressWarnings("unchecked")
		public int compareTo(SUItem<T> i) {
			return d.compareTo(i.d);
		}
	}



}

// D - 68
class OP_Dot_Error extends Operator {

	public OP_Dot_Error() {
		init(".D");
		arg("A", "throw an exception containing A");
	}

	@Override
	public void execute (Block block) {
		throw new UserObjRuntimeException(block.pop());
	}
}

//E - 69
class OP_Dot_Len extends Operator {

	public OP_Dot_Len() {
		init(".E");
		arg("L", "length, keep list on stack");
		setOverload(-1, "len");
	}

	@Override
	public void execute(Block block) {
		final Obj n = block.peek();

		if (n.isa(LIST)) {
			block.push(Num.fromInt(asList(n).length()));
		} else if (n.isa(DICT)) {
			block.callVariable((Dict)n, SymbolConstants.KEYVAR_LEN);
		} else {
			throw new TypeError(this, n);
		}
	}
}



//F - 70
class OP_Dot_Flatten extends Operator {

	public OP_Dot_Flatten() {
		init(".F");
		arg("L", "flatten nested list");
	}

	@Override
	public void execute(Block block) {
		final Obj n = block.pop();

		if (n.isa(LIST)) {
			block.push(asList(n).flatten());
		} else {
			throw new TypeError(this, n);
		}
	}
}




// G - 71
class OP_Dot_Write extends Operator {

	public OP_Dot_Write() {
		init(".G");
		arg("ASN", "write A as a string to file located at S. N = 0, overwrite. N = 1, append");
	}

	@Override
	public void execute(Block block) {
		final Obj n = block.pop();
		final Obj s = block.pop();
		final Obj a = block.pop();

		if (s.isa(STR) && n.isa(NUMBER)) {
			final int option = ((Number)n).toInt();
			final String filename = s.str();
			final String write = a.str();
			final String fstr = AyaPrefs.getWorkingDir()+filename;


			if(option == 0) {
				try {
				    Files.write(Paths.get(fstr), write.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				}catch (IOException e) {
				    throw new IOError(".G", fstr, e);
				} catch (InvalidPathException ipe) {
				    throw new IOError(".G", fstr, "Invalid path");
				}
			}

			else if (option == 1) {
				try {
				    Files.write(Paths.get(fstr), write.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				}catch (IOException e) {
				    throw new IOError(".G", fstr, e);
				} catch (InvalidPathException ipe) {
				    throw new IOError(".G", fstr, "Invalid path");
				}
			}

			else {
			    throw new ValueError(".G: Option " + option + "is not valid. Please use 0 for overwrite and 1 for append");
			}
			return;
		}
		throw new TypeError(this, n);
	}
}

// I - 73
//NOTE: If updating this operator, also update I
class OP_Dot_I extends Operator {

	public OP_Dot_I() {
		init(".I");
		arg("LNA|DSA|DJA", "getindex with default value");
		setOverload(-1, "getindex");
	}

	@Override
	public void execute (final Block block) {
		Obj dflt_val = block.pop();
		Obj index = block.pop();
		final Obj list = block.pop();

		if(list.isa(LIST)) {		
			block.push(asList(list).getIndexed(index, dflt_val));
		} else if (list.isa(DICT)) {
			block.push(DictIndexing.getIndex(asDict(list), index, dflt_val));
		} else {
			throw new TypeError(this, index, list);
		}
	}
}

//K - 75
class OP_Dot_TryCatch extends Operator {

	public OP_Dot_TryCatch() {
		init(".K");
		arg("BB", "try B1, if error, execute B2. Neither block has access to the global stack");
	}

	@Override
	public void execute (Block block) {
		Obj catchBlock = block.pop();
		Obj tryBlock = block.pop();

		if(tryBlock.isa(BLOCK) && catchBlock.isa(BLOCK)) {
			try {
				Block evaluator = new Block(asStaticBlock(tryBlock));
				Aya.getInstance().getCallStack().setCheckpoint();
				Aya.getInstance().getVars().setCheckpoint();
				evaluator.eval();
				Aya.getInstance().getCallStack().popCheckpoint();
				Aya.getInstance().getVars().popCheckpoint();
				block.appendToStack(evaluator.getStack());
			} catch (AyaRuntimeException e) {
				Aya.getInstance().getCallStack().rollbackCheckpoint();
				Aya.getInstance().getVars().rollbackCheckpoint();
				Block evaluator = new Block(asStaticBlock(catchBlock));
				evaluator.push(e.getDict());
				evaluator.eval();
				block.appendToStack(evaluator.getStack());
			} catch (Exception e2) {
				// An actual bug
				throw e2;
			}
		}
	}
}


//M- 77
class OP_Dot_M extends Operator {

	public OP_Dot_M() {
		init(".M");
		arg("A", "get metatable");
	}
	
	@Override
	public void execute (final Block block) {
		Obj a = block.pop();
		 if (a.isa(DICT)) {
			block.push(((Dict)a).getMetaDict());
		} else {
			block.push(Aya.getInstance().getVars().getBuiltinMeta(a));
		}
	}

	
	
}

// N - 78
class OP_Dot_N extends Operator {

	public OP_Dot_N() {
		init(".N");
		arg("LB", "return the index of the first element of L that satifies E; keep list on stack");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop(); //Block
		final Obj b = block.pop(); //List

		int index = 0;
		if(b.isa(LIST) && a.isa(BLOCK)) {

			block.push(b); //Push the list

			final StaticBlock blk = asStaticBlock(a);
			List l = asList(b);
			for (int i = 0; i < l.length(); i++) {
				Block cond = new Block(blk);
				cond.push(l.getExact(i));
				cond.eval();
				Obj result = cond.pop();
				if (result.bool()) {
					block.push(Num.fromInt(index)); // ..and the index
					return;
				}
				index++;
			}
			block.push(Num.NEG_ONE);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// O - 79
class OP_Dot_O extends Operator {

	public OP_Dot_O() {
		init(".O");
		arg("AB", "apply");
		vect();
	}

	@Override
	public void execute (Block block) {
		final Obj b = block.pop();
		final Obj a = block.pop();
		block.push(exec2arg(a, b));
	}
	
	public Obj exec2arg(final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(this, a, b)) != null) return res;
		
		if (b.isa(BLOCK)) {
			Block blk = new Block(asStaticBlock(b));
			blk.push(a);
			blk.eval();
			return blk.pop();
		} else {
			throw new TypeError(this, b, a); // stack order
		}
	}
}


// P - 80
class OP_Dot_Print extends Operator {

	public OP_Dot_Print() {
		init(".P");
		arg("A", "print to stdout");
	}

	@Override
	public void execute (Block block) {
		Aya.getInstance().print(block.pop().str());
	}
}

// Q - 81
class OP_Dot_Rand extends Operator {

	public OP_Dot_Rand() {
		init(".Q");
		arg("-", "return a random decimal from 0 to 1");
	}

	@Override
	public void execute (Block block) {
		block.push(new Num(Ops.RAND.nextDouble()));
	}
}

// R - 82
class OP_Dot_R extends Operator {

	public OP_Dot_R() {
		init(".R");
		arg("N", "range [0, 1, .., N-1]");
		arg("L", "linspace [from to count], if count not provided, use 100");
	}

	@Override
	public void execute (Block block) {
		final Obj a = block.pop();

		if (a.isa(NUMBER)) {
			final Number n = (Number)a;
			if (n.compareTo(Num.ZERO) == 0) {
				// 0 .R => [ ]
				block.push(new List());
			} else if (n.compareTo(Num.ZERO) > 0) {
				// +N .R => [0 1 2 ... N-1]
				block.push( new List(ListRangeUtils.buildRange(Num.ZERO, n.dec())) );
			} else {
				// -N .R => [N+1 ... -1 0]
				block.push( new List(ListRangeUtils.buildRange(n.inc(), Num.ZERO)) );
			}
		} else if (a.isa(Obj.NUMBERLIST)) {
			NumberList list = asNumberList(a);
			if (list.length() == 2) {
				block.push(new List(linspace(list.get(0), list.get(1), Num.fromInt(100))));
			} else if (list.length() == 3) {
				block.push(new List(linspace(list.get(0), list.get(1), list.get(2))));
			} else {
				throw new ValueError("Invalid linspace input. Length must be be exactly 2 or 3. Got: " + a.repr());
			}
		} else {
			throw new TypeError(this, a);
		}
	}

	
	private NumberList linspace(Number from, Number to, Number steps) {
		if (from.equiv(to)) {
			ArrayList<Number> nums = new ArrayList<Number>();
			int count = steps.toInt();
			for (int i = 0; i < count; i++) {
				nums.add(from);
			}
			return NumberList.fromNumberAL(nums);
		} else {
			Number a = NumberMath.sub(to, from);
			Number b = NumberMath.sub(steps, Num.ONE);
			Number inc = NumberMath.div(a, b);
			NumberList out = NumberList.range(from, to, inc);

			// Rounding error may cause off-by-one
			int expected_len = steps.toInt();
			if (out.length() == expected_len - 1) {
				out.addItem(to);
			} else if (out.length() == expected_len + 1) {
				out.popBack();
			}
			
			// Verify the list length is correct
			if (out.length() != expected_len) {
				// If this is ever thrown, there is a bug in the code above
				throw new ValueError("Error creating linspace, length is incorrect");
			} else {
				return out;
			}
		}
	}
}

// S - 83
class OP_Dot_S extends Operator {

	public OP_Dot_S() {
		init(".S");
		arg("LL", "rotate [rows cols]");
		arg("LN", "rotate]");
	}

	@Override
	public void execute (Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();

		if (a.isa(Obj.NUM) && b.isa(Obj.LIST)) {
			List out = asList(b).rotate(asNumber(a).toInt());
			block.push(out);
		} else if (a.isa(Obj.LIST) && b.isa(LIST)) {
			final NumberList amount = asList(a).toNumberList();
			List list = asList(b);
			if (amount.length() == 1) {
				List out = list.rotate(amount.get(0).toInt());
				block.push(out);
			} else if (amount.length() == 2) {
				List out = rotate(list, amount.get(0).toInt(), amount.get(1).toInt());
				block.push(out);
			} else {
				throw new ValueError(".S rotation amount must be length 1 or 2");
			}
		} else {
			throw new TypeError(this, a, b);
		}
	}

	private List rotate(List l, int rows, int cols) {
		l = l.rotate(rows);
		for (int i = 0; i < l.length(); i++) {
			Obj x = l.getExact(i);
			if (x.isa(Obj.LIST)) {
				l.mutSetExact(i, asList(x).rotate(cols));
			}
		}
		return l;
	}
}



//T - 84
class OP_Dot_T extends Operator {

	public OP_Dot_T() {
		init(".T");
		arg("L", "transpose a 2d list");
	}

	@Override
	public void execute (Block block) {
		final Obj a = block.pop();

		if (a.isa(LIST)) {
			block.push(asList(a).transpose());
		} else {
			throw new TypeError(this, a);
		}
	}
}

// U - 85
class OP_RequestString extends Operator {

	public OP_RequestString() {
		init(".U");
		arg("S", "requests a string using a ui dialog, S is the prompt text");
	}

	@Override
	public void execute(Block block) {
		block.push(List.fromString(QuickDialog.requestString(block.pop().str())));
	}
}

// V - 86
class OP_Dot_AppendBack extends Operator {

	public OP_Dot_AppendBack() {
		init(".V");
		arg("AL", "append item to back of list");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();

		if (a.isa(LIST)) {
			asList(a).mutAddExact(0, b);
			block.push(a);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}


// \ - 92
class OP_Dot_BackSlash extends Operator {

	public OP_Dot_BackSlash() {
		init(".\\");
		arg("N", "floor");
		setOverload(1, "floor");
		vect();
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.floor(); }
	};

	@Override
	public void execute(final Block block) {
		Obj a = block.pop();
		block.push(exec1arg(a));
	}

	@Override
	public Obj exec1arg(final Obj a) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize1arg(this, a, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(a)) != null) return res; // stack order

		if (a.isa(NUMBER)) {
			return ((Number)a).floor();
		} else {
			throw new TypeError(this, a);
		}
	}
}

// ^ - 94

class OP_Dot_Pow extends Operator {

	public OP_Dot_Pow() {
		init(".^");
		arg("N", "square root");
		arg("S", "quote regex");
		setOverload(1, "sqrt");
		vect();
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.sqrt(); }
	};

	@Override
	public void execute(final Block block) {
		Obj a = block.pop();
		block.push(exec1arg(a));
	}

	@Override
	public Obj exec1arg(final Obj a) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize1arg(this, a, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(a)) != null) return res; // stack order
	
		if(a.isa(NUMBER)) {
			return ((Number)a).sqrt();
		} else if (a.isa(STR)) {
			return List.fromString(Pattern.quote(a.str()));
		} else {
			throw new TypeError(this, a);
		}
	}
}




// _ - 95


// | - 124
class OP_Dot_Bar extends Operator {

	

	public OP_Dot_Bar() {
		init(".|");
		arg("N", "absolute value");
		arg("B", "get meta information for a block");
		setOverload(1, "abs");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.abs(); }
	};

	@Override
	public void execute(final Block block) {
		Obj a = block.pop();
		block.push(exec1arg(a));
	}

	@Override
	public Obj exec1arg(final Obj a) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize1arg(this, a, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(a)) != null) return res; // stack order

		if (a.isa(NUMBER)) {
			return ((Number)a).abs();
		} else if (a.isa(BLOCK)) {
			return BlockUtils.getBlockMeta(asStaticBlock(a));
		} else {
			throw new TypeError(this, a);
		}
	}

}



// ~ - 126
class OP_Dot_Tilde extends Operator {

	public OP_Dot_Tilde() {
		init(".~");
		arg("S|C", "parse contents to a block");
		arg("J", "deref variable; if not a block, put contents in block");
		arg("D", "set all variables");
		arg("B", "get contents of block");
	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();

		if (a.isa(STR) || a.isa(CHAR)) {
			try {
				StaticBlock sb = BlockUtils.fromIS( Parser.compileIS(new SourceString(a.str(), "~"), Aya.getInstance()) );
				block.push(sb);
			} catch (ParserException e) {
				throw new InternalAyaRuntimeException(e.typeSymbol(), e);
			}
			return;

		} else if (a.isa(SYMBOL)) {
			Obj e = Aya.getInstance().getVars().getVar(asSymbol(a));
			if (!e.isa(BLOCK)) {
				StaticBlock b = BlockUtils.makeBlockWithSingleInstruction(new DataInstruction(e));
				block.push(b);
			} else {
				block.push(e);
			}

		} else if (a.isa(DICT)) {
			// Set all vars in the dict
			Aya.getInstance().getVars().setVars(asDict(a));
		} else if (a.isa(BLOCK)) {
			block.push(BlockUtils.convertSingleVariableToSymbol(asStaticBlock(a)));
		} else {
			throw new TypeError(this, a);
		}
	}
}
