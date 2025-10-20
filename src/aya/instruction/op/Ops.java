package aya.instruction.op;

import static aya.obj.Obj.BLOCK;
import static aya.obj.Obj.CHAR;
import static aya.obj.Obj.DICT;
import static aya.obj.Obj.LIST;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.NUMBERLIST;
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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aya.StaticData;
import aya.eval.BlockEvaluator;
import aya.eval.ExecutionContext;
import aya.exceptions.parser.NotAnOperatorError;
import aya.exceptions.parser.ParserException;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.IndexError;
import aya.exceptions.runtime.InternalAyaRuntimeException;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.UnimplementedError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.index.AnonGetIndexInstruction;
import aya.obj.Obj;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.dict.DictIndexing;
import aya.obj.list.GenericList;
import aya.obj.list.List;
import aya.obj.list.ListIterationFunctions;
import aya.obj.list.ListRangeUtils;
import aya.obj.list.Str;
import aya.obj.list.numberlist.NumberList;
import aya.obj.list.numberlist.NumberListOp;
import aya.obj.number.BigNum;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.NumberMath;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.obj.symbol.SymbolTable;
import aya.parser.Parser;
import aya.parser.SourceString;
import aya.parser.SourceStringRef;
import aya.util.Casting;
import aya.util.FileUtils;
import aya.util.Pair;
import aya.util.TypeUtils;
import aya.util.UTF16;
import aya.util.VectorizedFunctions;

public class Ops {
	
	public static final Random RAND = new Random((new Date()).getTime());
	public static final Pattern PATTERN_URL = Pattern.compile("https?://.*");

	
	////////////////////////
	// OPERATOR OVERLOADS //
	////////////////////////


	
	
	public static final char FIRST_OP = '!';
	public static final char MAX_OP = (char)126;
	protected static final Operator OP_PLUS = new OP_Plus();
	public static final Operator OP_I_INSTANCE = new OP_GetIndex();
	public static final Operator OP_T_MAKE_TYPE = new OP_T();
	public static final Operator OP_POUND = new OP_Pound();

	
	public static final Operator[] OPS = {
		/* 33 !  */ new OP_Bang(),
		/* 34 "  */ null, // String
		/* 35 #  */ OP_POUND,
		/* 36 $  */ new OP_Duplicate(),
		/* 37 %  */ new OP_Percent(),
		/* 38 &  */ new OP_And(),
		/* 39 '  */ null, // Character / Base Numbers
		/* 40 (  */ null, // Lambda
		/* 41 )  */ null, // Lambda
		/* 42 *  */ new OP_Times(),
		/* 43 +  */ OP_PLUS,
		/* 44 ,  */ null, // Delimiter
		/* 45 -  */ new OP_Minus(),
		/* 46 .  */ null, // Dot Operators
		/* 47 /  */ new OP_Divide(),
		
		//Not used
		/* 48-57 0-9  */ null,null,null,null,null,null,null,null,null,null, //Number Literals
		/* 58    */ null, //Not used - Whitespace
		
		/* 59 ;  */ new OP_SemiColon(),
		/* 60 <  */ new OP_LessThan(),
		/* 61 =  */ new OP_Equal(),
		/* 62 >  */ new OP_GreaterThan(),
		/* 63 ?  */ new OP_Conditional(),
		/* 64 @  */ new OP_At(),
		/* 65 A  */ new OP_A(),
		/* 66 B  */ new OP_B(),
		/* 67 C  */ new OP_Sort(),
		/* 68 D  */ new OP_D(),
		/* 69 E  */ new OP_E(),
		/* 70 F  */ null,
		/* 71 G  */ new OP_G(),
		/* 72 H  */ new OP_H(),
		/* 73 I  */ OP_I_INSTANCE,
		/* 74 J  */ new OP_Join(),
		/* 75 K  */ new OP_K(),
		/* 76 L  */ new OP_L(),
		/* 77 M  */ null, //Math Library
		/* 78 N  */ new OP_N(),
		/* 79 O  */ new OP_O(),
		/* 80 P  */ new OP_P(),
		/* 81 Q  */ new OP_Q(),
		/* 82 R  */ new OP_R(),
		/* 83 S  */ new OP_S(),
		/* 84 T  */ OP_T_MAKE_TYPE, 
		/* 85 U  */ new OP_U(),
		/* 86 V  */ new OP_V(),
		/* 87 W  */ new OP_W(),
		/* 88 X  */ new OP_X(),
		/* 89 Y  */ new OP_Y(),
		/* 90 Z  */ new OP_Z(),
		/* 91 [  */ null, // List Literal
		/* 92 \  */ new OP_Backslash(),
		/* 93 ]  */ null, // List Literal
		/* 94 ^  */ new OP_Caret(),
		/* 95 _  */ null, // Symbol
		/* 96 `  */ null, // Hold Operator
	};
	
	/* 124 | */ public static final Operator BAR = new OP_Bar();
	/* 126 ~ */ public static final Operator TILDE = new OP_Tilde();
	
	public static final Operator[] EXTRA_OPS = { BAR, TILDE };

	//Special Ops
	//public static final Operation APPLY_TO = new OP_ApplyTo();
	public static final Operator GETINDEX = new OP_GetIndex();
	public static final Operator SETINDEX = new OP_SetIndex();
	
	/** Returns true if char c is bound to an operator */
	public static boolean isOp(char c) {
		if(c <= 96 && c >= FIRST_OP ) {
			return OPS[c-FIRST_OP] != null;
		} else if (c == '|' || c == '~') {
			return true;
		}
		return false;
	}

	public static boolean isOpChar(int c) {
		return UTF16.is2Byte(c) && isOpChar((char) c);
	}

	public static boolean isOpChar(char c) {
		char[] op_exceptions = {
				',','(',')','[',']','`','.','"','\'', '#','_'
			};		
		
		//Check Exceptions
		for (char k : op_exceptions) {
			if (c == k)
				return false;
		}
		
		//Normal Check
		return (c >= 33 && c <= 47)
				|| (c >= 59 && c <= 96)
				|| c == '|'
				|| c == '~';
	}
	
	public static OperatorInstruction getOp(char op, SourceStringRef source) throws NotAnOperatorError {
		if ((op >= 33 && op <= 47) || (op >= 59 && op <= 96)) {
			return new OperatorInstruction(source, OPS[op-FIRST_OP]);
		} else if (op == '~') {
			return new OperatorInstruction(source, TILDE);
		} else if (op == '|') {
			return new OperatorInstruction(source, BAR);
		} else {
			throw new NotAnOperatorError(""+op, source);
		}
	}
	
}

// ! - 33
class OP_Bang extends Operator {
	
	public OP_Bang() {
		init("!");
		arg("N", "1-N (logical not, complementary probability)");
		arg("C", "swap case");
		vect();
		setOverload(-1, "new");
	}

	@Override public void execute(final BlockEvaluator blockEvaluator) {
		Obj o = blockEvaluator.pop();
		
		if (o.isa(Obj.NUMBER)) {
			blockEvaluator.push(Num.ONE.sub(asNumber(o)));
		} else if (o.isa(Obj.CHAR)) {
			blockEvaluator.push(((Char)o).swapCase());
		} else if (o.isa(STR)) {
				blockEvaluator.push(new List(asStr(o).swapCase()));
		} else if (o.isa(Obj.NUMBERLIST)) {
			blockEvaluator.push( new List(asNumberList(o).subFrom(Num.ONE)) );
		} else if (o.isa(DICT)) {
			Dict d = (Dict)o;
			if (d.containsKey(SymbolConstants.KEYVAR_NEW)) {
				blockEvaluator.push(o);
				blockEvaluator.addOrDumpVar(d.get(SymbolConstants.KEYVAR_NEW));
			} else {
				//Create a new empty dict with the input as its metatable
				//blockEvaluator.push(new Dict((Dict)o));
				throw new IndexError(d, SymbolConstants.KEYVAR_NEW);
			}
		} else {
			throw new TypeError(this,o);
		}
	}
}

//# - 35
class OP_Pound extends Operator {
	
	public OP_Pound() {
		init("#");
		arg("LA..#A", "map");
	}
	
	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		
		if(a.isa(Obj.BLOCK)) {
			//final BlockEvaluator map = ((BlockEvaluator)a).duplicate();
			StaticBlock map = Casting.asStaticBlock(a);
						
			Obj popped = blockEvaluator.pop();
			
			//Capture all non-list items from the left of the #
			while (!popped.isa(Obj.LIST)) {
				map = BlockUtils.addObjToStack(map, popped);
				
				if (blockEvaluator.stackEmpty()) {
					throw new ValueError("Could not find list to map to\n"
							+ "\t in " + blockEvaluator.toString() + "\n"
							+ "\t map using " + map);
				} else {
					popped = blockEvaluator.pop();
				}
			}
			
			blockEvaluator.push(ListIterationFunctions.map(blockEvaluator.getContext(), asList(popped), map));
			
			return;
		} 	
		throw new TypeError(this,a);
	}
}

// $ - 36
class OP_Duplicate extends Operator {
	
	public OP_Duplicate() {
		init("$");
		arg("A", "deepcopy (duplicate)");
	}

	@Override public void execute (final BlockEvaluator blockEvaluator) {
		blockEvaluator.push(blockEvaluator.peek().deepcopy());
	}
}

// % - 37
class OP_Percent extends Operator {
	
	public OP_Percent() {
		init("%");
		arg("LB", "fold");
		arg("LS", "join");
		arg("LC", "join");
		arg("BN", "repeat");
	}
	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
	
		if (a.isa(LIST) && b.isa(BLOCK)) {
			fold(asList(a), asStaticBlock(b), blockEvaluator);
		} else if (a.isa(BLOCK) && b.isa(NUMBER)) {
			int repeats = ((Number)(b)).toInt();
			StaticBlock blk = asStaticBlock(a);
			for (int i = 0; i < repeats; i ++) {
				blockEvaluator.dump(blk);
			}
			return;
		} else if (a.isa(LIST) && ((b.isa(STR) || b.isa(CHAR)))) {
			blockEvaluator.push(List.fromString(strJoin(asList(a), b.str())));
		} else {
			throw new TypeError(this, b, a); // stack order
		}
	}

	private static String strJoin(List list, String sep) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.length()-1; i++) {
			sb.append(list.getExact(i).str());
			sb.append(sep);
		}
		if (list.length() > 0) sb.append(list.tail().str());
		return sb.toString();
	}

	private static void fold(List list, StaticBlock foldBlock, BlockEvaluator resultBlock) {
		int length = list.length();
		if(length == 0) {
			resultBlock.push(Num.ZERO);
		} else {
			//Push all but the last item
			//for(int i = 0; i < list.size()-1; i++) {
			for(int i = length-1; i > 0; i--) {
				resultBlock.dump(foldBlock);
				resultBlock.add(list.getExact(i));
			}
			//Push the last element outside the loop so that there is not an extra plus (1 1+2+3+)
			//blockEvaluator.add(list.get(list.size()-1));
			resultBlock.add(list.getExact(0));
		}
	}
}



// & - 38
class OP_And extends Operator {
	
	public OP_And() {
		init("&");
		arg("NN", "bitwise and");
		arg("NB|BB", "and (short circuit)");
		arg("SS", "list all expressions matching the regex");
		setOverload(2, "and");
		vect();
	}

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		
		if (b.isa(BLOCK)) {
			// Get the boolean value of a
			boolean aVal = false;
			if (a.isa(NUMBER)) {
				aVal = a.bool();
			} else if (a.isa(BLOCK)) {
				// aVal = block.eval
				BlockEvaluator be = blockEvaluator.getContext().createEvaluator();
				be.dump(Casting.asStaticBlock(a));
				be.eval();
				aVal = be.pop().bool();
			} else {
				throw new TypeError(this, b, a); // Stack order
			}
			
			
			if (aVal) {
				// Evaluate b and return that
				BlockEvaluator be = blockEvaluator.getContext().createEvaluator();
				be.dump(Casting.asStaticBlock(b));
				be.eval();
				blockEvaluator.push(be.pop());
			} else {
				// Do not evaluate b, just return false
				blockEvaluator.push(Num.ZERO);
			}
		} else {
			blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
		}
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.band(b);}
		public NumberList nl(Number a, NumberList b) { return b.bandFrom(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.band(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};
		
	// a b & => "a & b"
	@Override
	public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(context, this, a, b, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(context, b, a)) != null) return res; // stack order

		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			return NumberMath.band((Number)a, (Number)b);
		} else if (a.isa(Obj.STR) && b.isa(Obj.STR)) {
			ArrayList<Obj> allMatches = new ArrayList<Obj>();
			Matcher m = Pattern.compile(b.str()).matcher(a.str());
			while (m.find()) {
				 allMatches.add(List.fromString(m.group()));
			}
			return new List(allMatches);
		} else {
			throw new TypeError(this, b, a); // stack order
		}

		
	}
}

// * - 42
class OP_Times extends Operator {
	
	public OP_Times() {
		init("*");
		arg("NN", "multiply");
		vect();
		setOverload(2, "mul");
	}
	
	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.mul(b);}
		public NumberList nl(Number a, NumberList b) { return b.mul(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.mul(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
	}

	// a b * => "a * b"
	@Override
	public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(context, this, a, b, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(context, b, a)) != null) return res; // stack order
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			return NumberMath.mul(asNumber(a), asNumber(b));
		} else {
			throw new TypeError(this, b, a); // stack order
		}
	}
}


// + - 43
class OP_Plus extends Operator {
	
	public OP_Plus() {
		init("+");
		arg("NN|CC", "add");
		arg("SA|AS", "append string");
		vect();
		setOverload(2, "add");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.add(b);}
		public NumberList nl(Number a, NumberList b) { return b.add(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.add(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
	}

	// a b + => "a + b"
	@Override
	public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(context, this, a, b, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(context, b, a)) != null) return res; // stack order

		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			return NumberMath.add(asNumber(a), asNumber(b));
		} else if (a.isa(STR) || b.isa(STR)) {
			return List.fromString(a.str() + b.str());
		} else if (a.isa(Obj.NUMBER) && b.isa(CHAR)) {
			return ((Char)b).add((Number)a);
		} else if (a.isa(CHAR) && b.isa(NUMBER)) {
			return ((Char)a).add((Number)b);
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			return ((Char)a).add((Char)b);
		} else {
			throw new TypeError(this, b, a); // stack order
		}
	}
}

// - - 44
class OP_Minus extends Operator {
	
	public OP_Minus() {
		init("-");
		arg("NN|CC", "subtract");
		vect();
		setOverload(2, "sub");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.sub(b);}
		public NumberList nl(Number a, NumberList b) { return b.subFrom(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.sub(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
	}

	// a b - => "a - b"
	@Override
	public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(context, this, a, b, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(context, b, a)) != null) return res; // stack order

		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			return NumberMath.sub(asNumber(a), asNumber(b));
		} else if (a.isa(NUMBER) && b.isa(CHAR)) {
			return ((Char)b).subFrom((Number)a);
		} else if (a.isa(CHAR) && b.isa(Obj.NUMBER)) {
			return ((Char)a).sub((Number)b);
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			return ((Char)a).sub((Char)b);
		} else {
			throw new TypeError(this, b, a); // stack order
		}
	}
}

// / - 47
class OP_Divide extends Operator {
	
	public OP_Divide() {
		init("/");
		arg("NN", "divide");
		vect();
		setOverload(2, "div");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.div(b);}
		public NumberList nl(Number a, NumberList b) { return b.divFrom(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.div(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
	}
		
	// a b / => "a / b"
	@Override
	public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(context, this, a, b, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(context, b, a)) != null) return res; // stack order

		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			return NumberMath.div(asNumber(a), asNumber(b));
		} else {
			throw new TypeError(this, b, a); // stack order
		}
	}
}


// ; - 59
class OP_SemiColon extends Operator {
	
	public OP_SemiColon() {
		init(";");
		arg("A", "pop and discard");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		blockEvaluator.pop();
	}
}


// < - 60
class OP_LessThan extends Operator {
	
	public OP_LessThan() {
		init("<");
		arg("NN|CC|SS", "less than");
		setOverload(2, "lt");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.lt(b);}
		public NumberList nl(Number a, NumberList b) { return b.gt(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.lt(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
	}
		
	// a b < => "a < b"
	@Override
	public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(context, this, a, b, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(context, b, a)) != null) return res; // stack order
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			return Num.fromBool(((Number)a).compareTo((Number)b) < 0);
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			return Num.fromBool(((Char)a).compareTo((Char)b) < 0);
		} else if (a.isa(STR) && b.isa(STR)) {
			return Num.fromBool(a.str().compareTo(b.str()) < 0);
		} else {
			throw new TypeError(this, a,b);
		}
	}
}


// = - 61
class OP_Equal extends Operator {
	
	public OP_Equal() {
		init("=");
		arg("AA", "equality");
		setOverload(-1, "eq");
	}

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		final Obj b = blockEvaluator.pop();
		
		if (a.isa(DICT)) {
			if (((Dict)a).hasMetaKey(SymbolConstants.KEYVAR_EQ)) {
				blockEvaluator.push(b);
				blockEvaluator.callVariable((Dict)a, SymbolConstants.KEYVAR_EQ);
			} else {
				if (b.isa(DICT)) {
					blockEvaluator.push(a.equiv(b) ? Num.ONE : Num.ZERO);
				} else {
					blockEvaluator.push(Num.ZERO);
				}
			}
		} else if (b.isa(DICT)) {
			if (((Dict)b).hasMetaKey(SymbolConstants.KEYVAR_EQ)) {
				blockEvaluator.callVariable((Dict)b, SymbolConstants.KEYVAR_EQ, a);
			} else {
				blockEvaluator.push(Num.ZERO);
			}
		} else {
			blockEvaluator.push(a.equiv(b) ? Num.ONE : Num.ZERO);
		}
	}
}

// > - 62
class OP_GreaterThan extends Operator {
	
	public OP_GreaterThan() {
		init(">");
		arg("NN|CC|SS", "greater than");
		vect();
		setOverload(2, "gt");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.gt(b);}
		public NumberList nl(Number a, NumberList b) { return b.lt(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.gt(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
	}
		
	// a b > => "a > b"
	@Override
	public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(context, this, a, b, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(context, b, a)) != null) return res; // stack order

		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			return Num.fromBool(((Number)a).compareTo((Number)b) > 0);
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			return Num.fromBool(((Char)a).compareTo((Char)b) > 0);
		} else if (a.isa(STR) && b.isa(STR)) {
			return Num.fromBool(a.str().compareTo(b.str()) > 0);
		} else {
			throw new TypeError(this, a,b);
		}
	}
}

// ? - 63
class OP_Conditional extends Operator {
	
	public OP_Conditional() {
		init("?");
		arg("AA", "if A1, then A2. If A2 is blockEvaluator, execute it");
	}

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		final Obj b = blockEvaluator.pop();

		//  b     a
		// cond {true}

		if(b.bool()) {			
			if(a.isa(BLOCK)) {
				blockEvaluator.dump(asStaticBlock(a));
			} else {
				blockEvaluator.push(a);
			}
		}
	}
}


// @ - 64
class OP_At extends Operator {
	
	public OP_At() {
		init("@");
		arg("AAA", "rotates the top three elements on the stack [abc->bca]");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		final Obj b = blockEvaluator.pop();
		final Obj c = blockEvaluator.pop();
		
		blockEvaluator.push(b);
		blockEvaluator.push(a);
		blockEvaluator.push(c);
	}
}

// A - 65
class OP_A extends Operator {
	
	public OP_A() {
		init("A");
		arg("A", "wrap in list");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final ArrayList<Obj> al = new ArrayList<Obj>();
		al.add(blockEvaluator.pop());
		blockEvaluator.push(new List(al));
	}
}

// B - 66
class OP_B extends Operator {
	
	public OP_B() {
		init("B");
		arg("N|C", "increment");
		arg("J", "increment in place");
		arg("L", "uncons from front");
		setOverload(1, "inc");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		
		final ExecutionContext context = blockEvaluator.getContext();
		
		if (overload().execute(blockEvaluator, a)) return;

		if (a.isa(Obj.NUMBER)) {
			blockEvaluator.push( ((Number)a).inc() );
		} else if (a.isa(CHAR)) {
			blockEvaluator.push( ((Char)a).inc() );
		} else if (a.isa(SYMBOL)) {
			Symbol var = asSymbol(a);
			Obj o = context.getVars().getVar(var);
			if (o.isa(NUMBER)) {
				context.getVars().setVar(var, ((Number)o).inc());
			} else if (o.isa(CHAR)) {
				context.getVars().setVar(var, ((Char)o).inc());
			}  else {
				throw new ValueError("Cannot increment " + o.repr() 
				+ " in place in call " + a.repr() + " V");
			}
		} else if (a.isa(LIST)) {
			List l = asList(a);
			if (l.length() > 0) {
				Obj popped = l.mutPopBack();
				blockEvaluator.push(l);
				blockEvaluator.push(popped);
			} else {
				throw new ValueError("B: unable to remove element from empty list");
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

// C - 67
class OP_Sort extends Operator {
	
	public OP_Sort() {
		init("C");
		arg("S|L", "sort least to greatest");
		arg("N", "bitwise not");
		setOverload(1, "sort");
	}

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		
		if (overload().execute(blockEvaluator, a)) return;
		
		if (a.isa(Obj.NUMBER)) {
			blockEvaluator.push( NumberMath.bnot((Number)a) );
		} else if(a.isa(LIST)) {
			asList(a).mutSort();
			blockEvaluator.push(a);
		} else {
			throw new TypeError(this,a);
		}		
	}
}



// D - 68
class OP_D extends Operator {
	
	public OP_D() {
		this.name = "D";
		init("D");
		arg("ALN", "set index");
		setOverload(-1, "setindex");

	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();  	//Index
		final Obj b = blockEvaluator.pop();		//List
		final Obj o = blockEvaluator.pop();		//Item

		if (b.isa(DICT)) {
			blockEvaluator.push(o);
			blockEvaluator.push(a);
			blockEvaluator.callVariable((Dict)b, SymbolConstants.KEYVAR_SETINDEX);
		}
		else if (b.isa(LIST)) {
			asList(b).mutSetIndexed(blockEvaluator.getContext(), a, o);
			blockEvaluator.push(b);
		}
		else {		
			throw new TypeError(this, a, b, o);
		}
		
	}

}

// E - 69
class OP_E extends Operator {
	
	public OP_E() {
		init("E");
		arg("N", "10^N");
		arg("L|S", "length");
		setOverload(1, "len");
	}
	
	private static Num TEN = Num.fromInt(10);

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		Obj n = blockEvaluator.pop();

		if (overload().execute(blockEvaluator, n)) return;

		if (n.isa(NUMBER)) {
			blockEvaluator.push( TEN.pow((Number)n) );
		} else if (n.isa(LIST)) {
			blockEvaluator.push( Num.fromInt(asList(n).length()) );
		} else {
			throw new TypeError(this, n);
		}
	}
}

// G - 71
class OP_G extends Operator {
	
	public OP_G() {
		init("G");
		arg("S", "read a string from a filename or URL");
		arg("N", "isprime");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		
		if(a.isa(STR)) {
			String name = a.str();

			if(Ops.PATTERN_URL.matcher(name).matches()) {
				try {
					String downloaded = StaticData.HTTP_DOWNLOADER.downloadFile(name);
					blockEvaluator.push(List.fromString(downloaded));
				}
				catch(IOException ex) {
					throw new IOError("G", name, ex);
				}
			} else {
				File readFile = FileUtils.resolveFile(name);
				try {
					blockEvaluator.push( List.fromString(FileUtils.readAllText(readFile)) );
				} catch (IOException e) {
					throw new IOError("G", readFile.getAbsolutePath(), e);
				}
			}
		} else if (a.isa(NUMBER)) {
			blockEvaluator.push( Num.fromBool(((Number)a).isPrime()) );
		} else {
			throw new TypeError(this, a);
		}
	}
}

class OP_H extends Operator {
	
	public OP_H() {
		init("H");
		arg("SA", "has; 1 if string contains substring");
		arg("LA", "has; 1 if list contains object");
		arg("DS|DC|DJ", "has; 1 if dict contains key");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		
		if (a.isa(STR)) {
			blockEvaluator.push(Num.fromBool(a.str().contains(b.str())));
		} else if (a.isa(LIST)) {
			blockEvaluator.push(Num.fromBool(asList(a).find(b) >= 0));
		} else if (a.isa(DICT)) {
			Symbol key;
			if (b.isa(SYMBOL)) {
				key = asSymbol(b);
			} else if (b.isa(STR) || b.isa(CHAR)) {
				key = SymbolTable.getSymbol(b.str());
			} else {
				throw new TypeError(this, b, a);
			}
			blockEvaluator.push(Num.fromBool(asDict(a).containsKey(key)));
		} else {
			throw new TypeError(this, b, a);
		}
	}
}

// I - 73
class OP_GetIndex extends Operator {
	
	private AnonGetIndexInstruction _instruction;
	
	public OP_GetIndex() {
		init("I");
		arg("LL|LN", "get index");
		arg("LB", "filter");
		setOverload(-1, "getindex");
		_instruction = new AnonGetIndexInstruction();
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		this._instruction.execute(blockEvaluator);
	}
}

// N/A - N/A
class OP_SetIndex extends Operator {
	
	public OP_SetIndex() {
		this.name = ".:";
	}
	@Override public void execute (final BlockEvaluator blockEvaluator) {
		Obj index = blockEvaluator.pop();
		final Obj list = blockEvaluator.pop();
		final Obj item = blockEvaluator.pop();
		
		if (index.isa(LIST) && !index.isa(STR)) {
			List l = asList(index);
			if (l.length() == 1)
				index = l.getExact(0);
		}
		
		// If it is a list, run the standard setindex method
		if(list.isa(LIST)) {		
			asList(list).mutSetIndexed(blockEvaluator.getContext(), index, item);
		}
		
		// If it is a dictionary check to see if has a metamethod first
		else if (list.isa(DICT)) {
			if ( ((Dict)list).hasMetaKey(SymbolConstants.KEYVAR_SETINDEX) ) {
				blockEvaluator.push(index);
				blockEvaluator.callVariable((Dict)list, SymbolConstants.KEYVAR_SETINDEX);
			} else {
				Dict.setIndex((Dict)list, index, item);
			}
		}
		else {
			throw new TypeError(this, index, list);
		}
		
		blockEvaluator.push(list);
	}
}


// J - 74
class OP_Join extends Operator {
	
	public OP_Join() {
		init("J");
		arg("LL", "join lists");
		arg("LA|AL", "add to list");
		arg("AA", "create list [ A A ]");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		final Obj b = blockEvaluator.pop();
		
		final boolean a_is_list = a.isa(LIST) && !a.isa(STR);
		final boolean b_is_list = b.isa(LIST) && !b.isa(STR);
		
		if (a_is_list && b_is_list) {
			blockEvaluator.push(asList(b).copyAddAll(asList(a)));
		} else if (a_is_list) {
			blockEvaluator.push(asList(a).copyAddItemExact(0, b));
		} else if (b_is_list) {
			blockEvaluator.push(asList(b).copyAddItem(a));
		} else {
			final ArrayList<Obj> list = new ArrayList<Obj>();
			list.add(b);  //Stack - Add in reverse order
			list.add(a);
			blockEvaluator.push(new List(list));
		}
	}
}
// K - 75
class OP_K extends Operator {
	
	public OP_K() {
		init("K");
		arg("N", "negate");
		vect();
		setOverload(1, "negate");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.negate(); }
	};

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec1arg(blockEvaluator.getContext(), a));
	}

	@Override
	public Obj exec1arg(ExecutionContext context, final Obj a) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize1arg(context, this, a, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(context, a)) != null) return res;
		
		if (a.isa(NUMBER)) {
			return ((Number)a).negate();
		} else {
			throw new TypeError(this, a);
		}
	}
}




// L - 76
class OP_L extends Operator {
	
	public OP_L() {
		init("L");
		arg("AN", "create list by repeating A N times");
		arg("NL|LL", "reshape");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		Obj n = blockEvaluator.pop();
		Obj item = blockEvaluator.pop();

		if(n.isa(NUMBER)) {
			int repeats = ((Number)n).toInt();

			if(repeats < 0) {
				throw new ValueError("Cannot create list with negative number of elements " + repeats);
			}

			if (item.isa(CHAR)) {
				blockEvaluator.push( new List(new Str( ((Char)item).charValue(), repeats)) );
			} else if (item.isa(NUMBER)) {
				blockEvaluator.push( new List(NumberList.repeat((Number)item, repeats)) );
			} else  {
				blockEvaluator.push( new List(new GenericList(item, repeats)) );
			}

		} 
		
		else if (n.isa(NUMBERLIST)) {
			List l;
			
			if (item.isa(LIST) && !item.isa(STR)) {
				l = asList(item);
			} else {
				l = new List();
				l.mutAdd(item);
			}
			blockEvaluator.push( l.reshape(asNumberList(n)) );
		}
		
		else {
			throw new TypeError(this, n , item);
		}
	}
}


// N - 78
class OP_N extends Operator {
	
	public OP_N() {
		init("N");
		arg("SS|LA", "return index of first occurance, -1 if not found; keep list on stack");
		arg("DJ|DS", "contains key; keep dict on stack");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop(); //Item
		final Obj b = blockEvaluator.peek(); //List
		
		if (a.isa(STR) && b.isa(STR)) {
			blockEvaluator.push(Num.fromInt(b.str().indexOf(a.str())));
		} else if(b.isa(Obj.LIST)) {			
			blockEvaluator.push(Num.fromInt(asList(b).find(a)));
		} else if (b.isa(DICT) && a.isa(STR)) {
			Symbol s = SymbolTable.getSymbol(a.str());
			blockEvaluator.push( Num.fromBool(((Dict)b).containsKey(s)) );
		} else if (b.isa(DICT) && a.isa(SYMBOL)) {
			blockEvaluator.push( Num.fromBool(((Dict)b).containsKey(asSymbol(a))) );
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// O - 79
class OP_O extends Operator {
	
	public OP_O() {
		init("O");
		arg("LB", "Map blockEvaluator to list");
		arg("DB", "Map blockEvaluator to dict");
		setOverload(-1, "each");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj blk_obj = blockEvaluator.pop();
		final Obj container = blockEvaluator.pop();

		// Repeat
		if (blk_obj.isa(NUMBER) && container.isa(BLOCK)) {
			int repeats = ((Number)(blk_obj)).toInt();
			StaticBlock blk = asStaticBlock(container);
			for (int i = 0; i < repeats; i ++) {
				blockEvaluator.dump(blk);
			}
			return;
		} else {
			StaticBlock blk = null;
			try {
				blk = asStaticBlock(blk_obj);
			} catch (ClassCastException e) {
				throw new TypeError(this, blk_obj, container);
			}
			
			if (container.isa(Obj.LIST)) {
				blockEvaluator.push(ListIterationFunctions.map(blockEvaluator.getContext(), asList(container), blk));
			} else if (container.isa(Obj.DICT)) {
				Dict d = (Dict)container;
				if (d.pushSelf() && d.containsKey(SymbolConstants.KEYVAR_EACH)) {
					blockEvaluator.push(blk);
					blockEvaluator.callVariable(d, SymbolConstants.KEYVAR_EACH);
				} else {
					blockEvaluator.push(DictIndexing.map(blockEvaluator.getContext(), (Dict)container, asStaticBlock(blk)));
				}
			} else {
				throw new TypeError(this, blk_obj, container);
			}
		}
	}
}



// P - 80
class OP_P extends Operator {
	
	public OP_P() {
		init("P");
		arg("A", "to string");
		setOverload(-1, "str");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		blockEvaluator.push(List.fromString(blockEvaluator.pop().str()));
	}
}

// Q - 81
class OP_Q extends Operator {
	
	public OP_Q() {
		init("Q");
		arg("N", "N>0: random number 0-N, N<0: random number N-0, N=0: any int");
		arg("L", "random choice");
		setOverload(1, "random");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();

		if (overload().execute(blockEvaluator, a)) return;
		
		if(a.isa(Obj.NUMBER)) {
			int i = ((Number)a).toInt();
			if (i > 0) {
				blockEvaluator.push(Num.fromInt(Ops.RAND.nextInt(i)));
			} else if (i < 0) {
				blockEvaluator.push(Num.fromInt(-1 * Ops.RAND.nextInt(i*-1)));
			} else {
				blockEvaluator.push(Num.fromInt(Ops.RAND.nextInt()));
			}
		} else if (a.isa(Obj.LIST)) {
			List l = asList(a);
			int i = Ops.RAND.nextInt(l.length());
			blockEvaluator.push(l.getExact(i));
		} else {
			throw new TypeError(this, a);
		}
	}
}

// R - 82
class OP_R extends Operator {
	
	public OP_R() {
		init("R");
		arg("N|C", "range [1, 2 .. N]");
		arg("L", "len L = 2: range [N1, N1+1, ..., N2], len l = 3: range [N1, N2, ..., N3]");
		setOverload(1, "range");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();

		if (overload().execute(blockEvaluator, a)) return;

		if(a.isa(LIST)) {
			blockEvaluator.push( new List(ListRangeUtils.buildRange(asList(a))) );
		} else if (a.isa(NUMBER)) {
			blockEvaluator.push( new List(ListRangeUtils.buildRange((Number)a)) );
		} else if (a.isa(CHAR)) {
			blockEvaluator.push( new List(ListRangeUtils.buildRange(((Char)a).charValue())) );
		} else {
			throw new TypeError(this, a);
		}
	}
}

// S - 83
class OP_S extends Operator {
	
	public OP_S() {
		init("S");
		arg("SS", "split at regex");
		arg("SC", "split at char");
		arg("LN", "split list at index");
	}
	
	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		
		if (a.isa(STR) && b.isa(STR)) {
			blockEvaluator.push(asStr(a).splitRegex(b.str()));
		} else if (a.isa(STR) && b.isa(CHAR)) {
			blockEvaluator.push(asStr(a).splitAtChar(asChar(b).charValue()));
		} else if (a.isa(LIST) && b.isa(NUMBER)) {
			Pair<List, List> lists = asList(a).splitAtIndexed(asNumber(b).toInt());
			List out = new List();
			out.mutAdd(lists.first());
			out.mutAdd(lists.second());
			blockEvaluator.push(out);
		} else {
			throw new TypeError(this, b, a);
		}
	}
}

// T - 84 
class OP_T extends Operator {
	
	public OP_T() {
		init("T");
		arg("LD", "type");
	}


	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		Obj type  = blockEvaluator.pop();
		Obj inner = blockEvaluator.pop();
		
		if (inner.isa(LIST) && type.isa(DICT)) {
			blockEvaluator.push(TypeUtils.makeType(asList(inner), asDict(type)));
		} else if (inner.isa(Obj.NUM) && !inner.bool() && type.isa(Obj.NUM) && !type.bool()) {
			// 0 0 T -> returns base type
			blockEvaluator.push(TypeUtils.TYPE);
		} else {
			throw new TypeError(this, type, inner);
		}	
	}
}


//U - 85
class OP_U extends Operator {

	public OP_U() {
		init("U");
		arg("L", "reverse");
		setOverload(1, "reverse");
	}

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		Obj o = blockEvaluator.pop();

		if (overload().execute(blockEvaluator, o)) return;
		
		if (o.isa(LIST)) {
			asList(o).mutReverse();
			blockEvaluator.push(o);
		} else {
			throw new TypeError(this,o);
		}
	}
}



// V - 86
class OP_V extends Operator {
	
	public OP_V () {
		init("V");
		arg("N|C", "decrement");
		arg("J", "decrement in place");
		arg("L", "uncons from back");
		setOverload(1, "dec");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		
		final ExecutionContext context = blockEvaluator.getContext();
		
		if (overload().execute(blockEvaluator, a)) return;
		
		if (a.isa(NUMBER)) {
			blockEvaluator.push( ((Number)a).dec() );
		} else if (a.isa(CHAR)) {
			blockEvaluator.push( ((Char)a).dec() );
		} else if (a.isa(SYMBOL)) {
			Symbol var = asSymbol(a);
			Obj o = context.getVars().getVar(var);
			if (o.isa(NUMBER)) {
				context.getVars().setVar(var, ((Number)o).dec());
			} else if (o.isa(CHAR)) {
				context.getVars().setVar(var, ((Char)o).dec());
			} else {
				throw new ValueError("Cannot decrement " + o.repr() 
						+ " in place in call " + a.repr() + " V");
			}
		} else if (a.isa(LIST)) {
			List l = asList(a);
			if (l.length() > 0) {
				Obj popped = l.mutPop();
				blockEvaluator.push(l);
				blockEvaluator.push(popped);
			} else {
				throw new ValueError("V: unable to remove element from empty list");
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

// W - 87
class OP_W extends Operator {
	
	public OP_W() {
		init("W");
		arg("B", "while loop (repeat as long as blockEvaluator returns true)");
		arg("D", "export all variables");
		arg("L", "sum (fold using +)");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		
		if (a.isa(LIST)) {
			blockEvaluator.push(sum(blockEvaluator.getContext(), asList(a)));
		} else if (a.isa(Obj.BLOCK)) {
			StaticBlock blk = asStaticBlock(a);
			BlockEvaluator state = blockEvaluator.getContext().createEvaluator();
			state.setStack((Stack<Obj>)blockEvaluator.getStack().clone());
			
			boolean condition = false;
			
			do {
				state.dump(blk);
				state.eval();
				
				final Obj cond = state.pop();
				
				if (cond.isa(NUMBER)) {
					condition = cond.bool();
				} else {
					throw new TypeError(this, "While condition must be a boolean or a number", cond);
				}
			
			} while (condition);
			
			//Merge the stack
			blockEvaluator.setStack(state.getStack());
		}
		else if(a.isa(DICT)) {
			Dict d = (Dict)a;
			for (Entry<Symbol, Obj> e : d.getMap().entrySet()) {
				blockEvaluator.getContext().getVars().setVar(e.getKey(), e.getValue());
			}
		} else {
			throw new TypeError(this, a);
		}
	}


	/** Generic list summation */
	public Obj sum(ExecutionContext context, List a) {
		//Using the new Promoted list, use Str, NumberList, or ObjList
		if (a.isa(STR)) {
			char total = 0;
			char[] chars = a.str().toCharArray();
			for (char c : chars) {
				total += c;
			}
			return Char.valueOf(total);
		} else if(a.isa(NUMBERLIST)) {
			return asNumberList(a).sum();
		} else {
			// If a normal list, fold using '+'
			List list = asList(a);
			if(list.length() == 0) {
				return Num.ZERO;
			}
			//Push all but the last item
			BlockEvaluator exec_block = context.createEvaluator();
			for(int i = list.length()-1; i > 0; i--) {
				exec_block.add(new OperatorInstruction(null, Ops.OP_PLUS));
				exec_block.add(list.getExact(i));
			}
			//Push the last element outside the loop so that there is not an extra plus (1 1+2+3+)
			exec_block.add(list.getExact(0)); 
			exec_block.eval();
			return exec_block.pop();
		}
	}



}


// X - 88
class OP_X extends Operator {
	
	public OP_X() {
		init("X");
		arg("A", "assign to variable x and pop from stack");
	}
	
	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		blockEvaluator.getContext().getVars().setVar(SymbolConstants.X, blockEvaluator.pop());
	}
}

// Y - 89
class OP_Y extends Operator {
	
	public OP_Y() {
		init("Y");
		arg("A", "assign to variable y and leave on stack");
	}
	
	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		blockEvaluator.getContext().getVars().setGlobalVar(SymbolConstants.Y, blockEvaluator.peek());
	}
}

// Z - 90
class OP_Z extends Operator {
	
	public OP_Z() {
		init("Z");
		arg("N", "cast to bignum");
		arg("S", "parse to bignum");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		
		if (a.isa(Obj.NUMBER)) {
			blockEvaluator.push(new BigNum(((Number)a).toBigDecimal()));
		} else if (a.isa(Obj.STR)) {
			try	{
				blockEvaluator.push(new BigNum(new BigDecimal(a.str())));
			} catch (NumberFormatException e) {
				throw new ValueError("Cannot cast " + a.str() + " to bignum");
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

// \ - 92
class OP_Backslash extends Operator {
	
	public OP_Backslash() {
		init("\\");
		arg("AA", "swap top two elements on the stack");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		final Obj b = blockEvaluator.pop();
		blockEvaluator.push(a);
		blockEvaluator.push(b);
	}
}

// ^ - 94
class OP_Caret extends Operator {
	
	public OP_Caret() {
		init("^");
		arg("NN", "power");
		arg("SS", "levenshtein distance");
		setOverload(2, "pow");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.pow(b);}
		public NumberList nl(Number a, NumberList b) { return b.powFrom(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.pow(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
	}
		
	// a b ^ => "a ^ b"
	@Override
	public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(context, this, a, b, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(context, b, a)) != null) return res; // stack order
		
		if(a.isa(NUMBER) && b.isa(NUMBER)){
			return NumberMath.pow(asNumber(a), asNumber(b));
		} else if (a.isa(STR) && b.isa(STR)) {
			return Num.fromInt( (asStr(b)).levDist(asStr(a)));
		} else {
			throw new TypeError(this, b, a); // stack order
		}
	}
}

// | - 124
class OP_Bar extends Operator {
	
	public OP_Bar() {
		init("|");
		arg("NN", "logical or");
		arg("NB|BB", "or (short circuit)");
		setOverload(2,  "or");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.bor(b); }
		public NumberList nl(Number a, NumberList b) { return b.borFrom(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.bor(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		
		if (b.isa(BLOCK)) {
			// Get the boolean value of a
			boolean aVal = false;
			if (a.isa(NUMBER)) {
				aVal = a.bool();
			} else if (a.isa(BLOCK)) {
				// aVal = block.eval
				BlockEvaluator be = blockEvaluator.getContext().createEvaluator();
				be.dump(Casting.asStaticBlock(a));
				be.eval();
				aVal = be.pop().bool();
			} else {
				throw new TypeError(this, b, a); // Stack order
			}
			
			
			if (aVal) {
				// Do not evaluate b, just return true
				blockEvaluator.push(Num.ONE);
			} else {
				// Evaluate b and return that
				BlockEvaluator be = blockEvaluator.getContext().createEvaluator();
				be.dump(Casting.asStaticBlock(b));
				be.eval();
				blockEvaluator.push(be.pop());
			}
		} else {
			blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
		}
	}

	// a b | => "a | b"
	@Override
	public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(context, this, a, b, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(context, b, a)) != null) return res; // stack order
		
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			return NumberMath.bor((Number)a, (Number)b);
		} else {
			throw new TypeError(this, b, a); // stack order
		}
	}
	
}


// ~ - 126
class OP_Tilde extends Operator {
	
	public OP_Tilde() {
		init("~");
		arg("B|S|C", "evaluate");
		arg("L", "dump to stack");
		arg("D", "set variables if they exist in the local scope");
	}

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		
		final ExecutionContext context = blockEvaluator.getContext();
		
		if(a.isa(BLOCK)) {
			blockEvaluator.dump(asStaticBlock(a));
		} else if (a.isa(STR) || a.isa(CHAR)) {
			try {
				StaticBlock compiled_str = Parser.compile(new SourceString(a.str(), "~"));
				blockEvaluator.dump(compiled_str);
			} catch (ParserException e) {
				throw new InternalAyaRuntimeException(e.typeSymbol(), e);
			}
		} else if (a.isa(LIST)) {
			List list = asList(a);
			//Collections.reverse(list);
			for (int i = list.length()-1; i >= 0; i--) {
				blockEvaluator.add(list.getExact(i));
			}
		} else if (a.isa(SYMBOL)) {
			blockEvaluator.push(context.getVars().getVar(asSymbol(a)));
		} else if (a.isa(DICT)) {
			// Dump all vars if they exist in the most local scope
			context.getVars().peek().mergeDefined(asDict(a));
		} else {
			throw new TypeError(this, a);
		}
	}
}


