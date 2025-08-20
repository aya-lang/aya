package aya.instruction.op;

import static aya.obj.Obj.BLOCK;
import static aya.obj.Obj.CHAR;
import static aya.obj.Obj.DICT;
import static aya.obj.Obj.NUM;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.STR;
import static aya.util.Casting.asNumber;

import aya.StaticData;
import aya.eval.BlockEvaluator;
import aya.eval.ExecutionContext;
import aya.exceptions.parser.NotAnOperatorError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.UnimplementedError;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.block.BlockUtils;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.numberlist.DoubleList;
import aya.obj.list.numberlist.NumberList;
import aya.obj.list.numberlist.NumberListOp;
import aya.obj.number.ComplexNum;
import aya.obj.number.FractionNum;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.NumberMath;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;
import aya.util.Casting;
import aya.util.NamedCharacters;
import aya.util.StringUtils;
import aya.util.VectorizedFunctions;

public class MiscOps {	

	
	public static char FIRST_OP = '!';
	
	/** A list of all valid single character operations. 
	 *  Stored in final array for fast lookup.
	 *  Array indexes are always [(operator character) - FIRST_OP]
	 */
	public static Operator[] MATH_OPS = {
		/* 33 !  */ new OP_Fact(),
		/* 34 "  */ null,
		/* 35 #  */ new OP_HashCode(),
		/* 36 $  */ new OP_SysTime(),
		/* 37 %  */ null,
		/* 38 &  */ null,
		/* 39 '  */ null,
		/* 40 (  */ null,
		/* 41 )  */ null,
		/* 42 *  */ null,
		/* 43 +  */ null,
		/* 44 ,  */ null,
		/* 45 -  */ null,
		/* 46 .  */ null,
		/* 47 /  */ null,
		/* 48 0  */ null,
		/* 49 1  */ null,
		/* 50 2  */ null,
		/* 51 3  */ null,
		/* 52 4  */ null,
		/* 53 5  */ null,
		/* 54 6  */ null,
		/* 55 7  */ null,
		/* 56 8  */ null,
		/* 57 9  */ null,
		/* 58 :  */ null,
		/* 59 ;  */ null,
		/* 60 <  */ null,
		/* 61 =  */ null,
		/* 62 >  */ null,
		/* 63 ?  */ new OP_Help(),
		/* 64 @  */ null,
		/* 65 A  */ null,
		/* 66 B  */ null,
		/* 67 C  */ new OP_Acosine(),
		/* 68 D  */ null,
		/* 69 E  */ null,
		/* 70 F  */ null,
		/* 71 G  */ null,
		/* 72 H  */ null,
		/* 73 I  */ new OP_CreateComplex(),
		/* 74 J  */ null,
		/* 75 K  */ null,
		/* 76 L  */ new OP_Log(),
		/* 77 M  */ null,
		/* 78 N  */ null,
		/* 79 O  */ null,
		/* 80 P  */ null,
		/* 81 Q  */ null,
		/* 82 R  */ null,
		/* 83 S  */ new OP_Asine(),
		/* 84 T  */ new OP_Atangent(),
		/* 85 U  */ null,
		/* 86 V  */ null,
		/* 87 W  */ null,
		/* 88 X  */ null,
		/* 89 Y  */ null,
		/* 90 Z  */ null,
		/* 91 [  */ null,
		/* 92 \  */ null,
		/* 93 ]  */ null,
		/* 94 ^  */ null,
		/* 95 _  */ null,
		/* 96 `  */ null,
		/* 97 a  */ new OP_Ma(),
		/* 98 b  */ new OP_Mb(),
		/* 99 c  */ new OP_Cosine(),
		/* 100 d */ new OP_CastDouble(),
		/* 101 e */ new OP_Me(),
		/* 102 f */ null,
		/* 103 g */ null,
		/* 104 h */ null,
		/* 105 i */ new OP_Mi(),
		/* 106 j */ null,
		/* 107 k */ new OP_AddParserChar(),
		/* 108 l */ new OP_Ln(),
		/* 109 m */ new OP_HasMeta(),
		/* 110 n */ null,
		/* 111 o */ null,
		/* 112 p */ new OP_Primes(),
		/* 113 q */ null,
		/* 114 r */ new OP_To_Rat(),
		/* 115 s */ new OP_Sine(),
		/* 116 t */ new OP_Tangent(),
		/* 117 u */ new OP_Atan2(),
		/* 118 v */ null,
		/* 119 w */ null,
		/* 120 x */ null,
		/* 121 y */ null,
		/* 122 z */ null,
		/* 123 { */ null,
		/* 124 | */ null,
		/* 125 } */ null,
		/* 126 ~ */ null,
	};
	
	/** Returns the operation bound to the character */
	public static OperatorInstruction getOp(char c, SourceStringRef source) throws NotAnOperatorError {
		OperatorInstruction op = getOpOrNull(c, source);
		if (op == null) {
			throw new NotAnOperatorError("M" + c, source);
		} else {
			return op;
		}
	}
	
	public static OperatorInstruction getOpOrNull(char op, SourceStringRef source) {
		if(op >= 33 && op <= 126) {
			Operator operator = MATH_OPS[op-FIRST_OP];
			if (operator == null) {
				return null;
			} else {
				return new OperatorInstruction(source, operator);
			}
		} else {
			return null;
		}
	}
	
}

// ! - 33
class OP_Fact extends Operator {
	
	public OP_Fact() {
		init("M!");
		arg("N", "factorial");
		setOverload(1, "fact");
		vect();
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.factorial(); }
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
		
		if(a.isa(NUMBER)){
			return asNumber(a).factorial();
		} else {
			throw new TypeError(this, a);
		}
	}
}

// # - 35
class OP_HashCode extends Operator {
	
	public OP_HashCode() {
		init("M#");
		arg("A", "hash code of the object");
	}
	
	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		blockEvaluator.push(Num.fromInt(blockEvaluator.pop().hashCode()));
	}
}

// $ - 36
class OP_SysTime extends Operator {
	
	public OP_SysTime() {
		init("M$");
		arg("-", "system time in milliseconds");
	}
	
	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		blockEvaluator.push(new Num(System.currentTimeMillis()));
	}
}



// ? - 63
class OP_Help extends Operator {
	
	public OP_Help() {
		init("M?");
		arg("N", "list op descriptions where N=[0:std, 1:dot, 2:colon, 3:misc]");
		arg("S", "search all help data");
		arg("B", "get help data for operator");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj s = blockEvaluator.pop();
		
		if(s.isa(STR)) {
			String str = s.str();
			List items = new List();
			
			if (str.length() == 0) {
				String[] ss = StaticData.getInstance().getHelpData().getAllItems();
				for (String a : ss) {
					items.mutAdd(List.fromString(a));
				}
			} else {
				java.util.List<String> ss = StaticData.getInstance().getHelpData().staticSearch(s.str());
				for (String a : ss) {
					items.mutAdd(List.fromString(a));
				}
			}
			
			blockEvaluator.push(items);
		} else if (s.isa(NUMBER)) {
			blockEvaluator.push(new List(OpDocReader.getAllOpDicts()));
		} else if (s.isa(BLOCK)) {
			blockEvaluator.push(BlockUtils.getHelpDataForOperator(Casting.asStaticBlock(s)));
		}
		else {
			throw new TypeError(this, s);
		}
	}
}

// C - 67
class OP_Acosine extends Operator {
	
	public OP_Acosine() {
		init("MC");
		arg("N", "inverse cosine");
		setOverload(1, "acos");
		vect();
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.acos(); }
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
		
		if(a.isa(NUMBER)) {
			return asNumber(a).acos();
		} else {
			throw new TypeError(this, a);
		}
	}
}

// I - 73
class OP_CreateComplex extends Operator {
	
	public OP_CreateComplex() {
		init("MI");
		arg("NN", "create complex number");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj im = blockEvaluator.pop();
		Obj r = blockEvaluator.pop();
		
		if(r.isa(NUMBER) && im.isa(NUMBER)) {
			blockEvaluator.push(new ComplexNum(asNumber(r).toDouble(), asNumber(im).toDouble()));
		} else {
			throw new TypeError(this, im, r);
		}
	}
}

// L - 76
class OP_Log extends Operator {
	
	public OP_Log() {
		init("ML");
		arg("N", "base-10 logarithm");
		setOverload(1, "log");
		vect();
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.log(); }
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
		
		if(a.isa(NUMBER)) {
			return asNumber(a).log();
		} else {
			throw new TypeError(this, a);
		}
	}
}


// S - 83
class OP_Asine extends Operator {
	
	public OP_Asine() {
		init("MS");
		arg("N", "inverse sine");
		vect();
		setOverload(1, "asin");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.asin(); }
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
		
		if(a.isa(NUMBER)) {
			return asNumber(a).asin();
		} else {
			throw new TypeError(this, a);
		}
	}
}

// T - 84
class OP_Atangent extends Operator {
	
	public OP_Atangent() {
		init("MT");
		arg("N", "inverse tangent");
		vect();
		setOverload(1, "atan");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.atan(); }
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
		
		if(a.isa(NUMBER)) {
			return asNumber(a).atan();
		} else {
			throw new TypeError(this, a);
		}
	}
}

// a - 97
class OP_Ma extends Operator {
	
	public OP_Ma() {
		init("Ma");
		arg("J", "Aya meta information");
	}

	@Override
	public void execute (BlockEvaluator blockEvaluator) {		
		Obj a = blockEvaluator.pop();
		
		if (a.isa(Obj.SYMBOL)) {
			Symbol sym = (Symbol)a;
			if (sym.name().equals("ops")) {
				blockEvaluator.push(OpInfo.getDict());
			} else {
				throw new ValueError("'Ma': Unknown symbol " + sym.name());
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}



// b - 98
class OP_Mb extends Operator {
	
	public OP_Mb() {
		init("Mb");
		arg("B", "duplicate blockEvaluator, add locals if they do not exist");
		arg("J", "is defined");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		
		if (a.isa(Obj.SYMBOL)) {
			blockEvaluator.push(Num.fromBool(blockEvaluator.getContext().getVars().isDefined(Casting.asSymbol(a))));
		} else if (a.isa(BLOCK)) {
			blockEvaluator.push(BlockUtils.addLocals(Casting.asStaticBlock(a)));
		} else {
			throw new TypeError(this, a);
		}
	}
}


// c - 99
class OP_Cosine extends Operator {
	
	public OP_Cosine() {
		init("Mc");
		arg("N", "cosine");
		vect();
		setOverload(1, "cos");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.cos(); }
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
	
		if(a.isa(NUMBER)) {
			return asNumber(a).cos();
		} else {
			throw new TypeError(this, a);
		}
	}
}

//d - 100
class OP_CastDouble extends Operator {
	
	public OP_CastDouble() {
		init("Md");
		arg("N", "cast to double");
		arg("S", "parse double, if invalid, return 0.0");
		setOverload(1, "float");
		vect();
	}

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec1arg(blockEvaluator.getContext(), a));
	}

	@Override
	public Obj exec1arg(ExecutionContext context, final Obj a) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize1arg(context, this, a)) != null) return res;
		if ((res = overload().executeAndReturn(context, a)) != null) return res;
		
		if(a.isa(STR)) {
			try {
				return new Num(Double.parseDouble(a.str()));
			} catch (NumberFormatException e) {
				throw new ValueError("Cannot cast string \""+ a.repr() + "\" to a double.");
			}
		} else if (a.isa(NUM)) {
			return a; // already a double
		} else if (a.isa(CHAR)) {
			return Num.fromInt(Casting.asChar(a).charValue() - '0');
		} else if (a.isa(NUMBER)){
			return new Num(((Number)a).toDouble());
		} else {
			throw new TypeError(this, a);
		}
	}
}

// e - 100
class OP_Me extends Operator {
	
	public OP_Me() {
		init("Me");
		arg("N", "exponential function");
		vect();
		setOverload(1, "exp");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.exp(); }
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
		
		if(a.isa(NUMBER)) {
			return asNumber(a).exp();
		} else {
			throw new TypeError(this, a);
		}
	}
}

// i - 105
class OP_Mi extends Operator {
	
	public OP_Mi() {
		init("Mi");
		arg("N", "imag part of complex number");
		vect();
		setOverload(1, "imag");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.imag(); }
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
		
		if(a.isa(NUMBER)) {
			return asNumber(a).imag();
		} else {
			throw new TypeError(this, a);
		}
	}
}



// k - 107
class OP_AddParserChar extends Operator {
	
	public OP_AddParserChar() {
		init("Mk");
		arg("CS", "add special character");
		arg("NN", "unsigned right bitshift");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		
		if (b.isa(STR) && a.isa(CHAR)) {
			String str = b.str();
			if (str.length() > 0 && StringUtils.lalpha(str)) {
				NamedCharacters.addChar(str, ((Char)a).charValue());
			} else {
				throw new ValueError("Cannot create special character using " + str);
			}
		} else if (a.isa(NUMBER) && b.isa(NUMBER)) {
			blockEvaluator.push( NumberMath.unsignedRightShift((Number)a, (Number)b) );
		} else {
			throw new TypeError(this, b, a);
		}
	}
}


// l - 108
class OP_Ln extends Operator {
	
	public OP_Ln() {
		init("Ml");
		arg("N", "natural logarithm");
		vect();
		setOverload(1, "ln");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.ln(); }
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

		if(a.isa(NUMBER)) {
			return asNumber(a).ln();
		} else {
			throw new TypeError(this, a);
		}
	}
}

// m - 109
class OP_HasMeta extends Operator {
	
	public OP_HasMeta() {
		init("Mm");
		arg("D", "true if the dict has a metatable, leave D on stack");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj d = blockEvaluator.pop();

		if (d.isa(DICT)) {
			blockEvaluator.push(((Dict)d).hasMetaTable() ? Num.ONE : Num.ZERO);
		} else {
			throw new TypeError(this, d);
		}
	}
}

// p - 112
class OP_Primes extends Operator {
	
	public OP_Primes() {
		init("Mp");
		arg("N", "list primes up to N");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();

		if (a.isa(NUMBER)) {
			int i = ((Number)a).toInt();
			if (i < 0) {
				throw new ValueError("Mp: Input must be positive");
			}
			blockEvaluator.push(new List(DoubleList.primes(i)));
		} else {
			throw new TypeError(this, a);
		}
	}

}

// r - 114
class OP_To_Rat extends Operator {
	
	public OP_To_Rat() {
		init("Mr");
		arg("N", "convert to fractional number");
		vect();
	}

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec1arg(blockEvaluator.getContext(), a));
	}

	@Override
	public Obj exec1arg(ExecutionContext context, final Obj a) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize1arg(context, this, a)) != null) return res;
		
		if(a.isa(NUMBER)) {
			if (a.isa(Obj.RATIONAL_NUMBER)) {
				return a;
			} else {
				return new FractionNum(asNumber(a).toDouble());
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}


// s - 115
class OP_Sine extends Operator {
	
	public OP_Sine() {
		init("Ms");
		arg("N", "sine");
		vect();
		setOverload(1, "sin");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.sin(); }
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
		
		if(a.isa(NUMBER)) {
			return asNumber(a).sin();
		} else {
			throw new TypeError(this, a);
		}
	}
}




// t - 116
class OP_Tangent extends Operator {
	
	public OP_Tangent() {
		init("Mt");
		arg("N", "tangent");
		vect();
		setOverload(1, "tan");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { throw new UnimplementedError(); }
		public NumberList nl(Number a, NumberList b) { throw new UnimplementedError(); }
		public NumberList ll(NumberList a, NumberList b) { throw new UnimplementedError(); }
		public NumberList l(NumberList a) { return a.tan(); }
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
			return asNumber(a).tan();
		} else {
			throw new TypeError(this, a);
		}
	}
}


// u - 117
class OP_Atan2 extends Operator {
	
	public OP_Atan2() {
		init("Mu");
		arg("NN", "y x Mu => atan2(y,x)");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj x = blockEvaluator.pop();
		Obj y = blockEvaluator.pop();

		if (x.isa(NUMBER) && y.isa(NUMBER)) {
			double ny = ((Num)y).toDouble();
			double nx = ((Num)x).toDouble();
			blockEvaluator.push(new Num(Math.atan2(ny, nx)));
		} else {
			throw new TypeError(this, x, y);
		}

	}
}











