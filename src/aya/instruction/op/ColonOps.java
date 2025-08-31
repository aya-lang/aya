package aya.instruction.op;

import static aya.obj.Obj.BLOCK;
import static aya.obj.Obj.CHAR;
import static aya.obj.Obj.DICT;
import static aya.obj.Obj.LIST;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.STR;
import static aya.obj.Obj.SYMBOL;
import static aya.util.Casting.asDict;
import static aya.util.Casting.asList;
import static aya.util.Casting.asNumber;
import static aya.util.Casting.asStr;
import static aya.util.Casting.asSymbol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import aya.StaticData;
import aya.eval.BlockEvaluator;
import aya.eval.ExecutionContext;
import aya.exceptions.parser.NotAnOperatorError;
import aya.exceptions.parser.ParserException;
import aya.exceptions.runtime.AssertError;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.InternalAyaRuntimeException;
import aya.exceptions.runtime.MathError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.UnimplementedError;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.block.BlockUtils;
import aya.obj.block.ConditionalUtils;
import aya.obj.block.StaticBlock;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.ListIterationFunctions;
import aya.obj.list.Str;
import aya.obj.list.numberlist.DoubleList;
import aya.obj.list.numberlist.NumberList;
import aya.obj.list.numberlist.NumberListOp;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.NumberMath;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.parser.Parser;
import aya.parser.SourceString;
import aya.parser.SourceStringRef;
import aya.parser.tokens.StringToken;
import aya.util.Casting;
import aya.util.FileUtils;
import aya.util.TypeUtils;
import aya.util.VectorizedFunctions;


public class ColonOps {	
	
	private static final char FIRST_OP = '!';
	
	public static final OP_Colon_Pound OP_COLON_POUND = new OP_Colon_Pound();

	
	/** A list of all valid single character operations. 
	 *  Stored in final array for fast lookup.
	 *  Array indexes are always [(operator character) - FIRST_OP]
	 */
	public static Operator[] COLON_OPS = {
		/* 33 !  */ new OP_Colon_Bang(),
		/* 34 "  */ null, // Quoted Symbol
		/* 35 #  */ OP_COLON_POUND,
		/* 36 $  */ new OP_Colon_Duplicate(),
		/* 37 %  */ new OP_Colon_Percent(),
		/* 38 &  */ new OP_Colon_And(),
		/* 39 '  */ new OP_Colon_Quote(),
		/* 40 (  */ null, //List item assignment
		/* 41 )  */ null,
		/* 42 *  */ new OP_Colon_Times(),
		/* 43 +  */ null,
		/* 44 ,  */ null,
		/* 45 -  */ null, //Special number literals
		/* 46 .  */ null,
		/* 47 /  */ null,
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
		/* 59 ;  */ new OP_Colon_Semicolon(),
		/* 60 <  */ new OP_Colon_LessThan(),
		/* 61 =  */ new OP_Colon_Equals(),
		/* 62 >  */ new OP_Colon_GreaterThan(),
		/* 63 ?  */ new OP_Colon_Bool(),
		/* 64 @  */ new OP_IsInstance(),
		/* 65 A  */ new OP_Colon_A(),
		/* 66 B  */ new OP_Colon_B(),
		/* 67 C  */ new OP_Colon_C(),
		/* 68 D  */ new OP_Colon_D(),
		/* 69 E  */ new OP_Colon_E(),
		/* 70 F  */ new OP_Colon_F(),
		/* 71 G  */ new OP_Colon_G(),
		/* 72 H  */ null,
		/* 73 I  */ new OP_Colon_I(),
		/* 74 J  */ new OP_Colon_J(),
		/* 75 K  */ new OP_Colon_K(),
		/* 76 L  */ null,
		/* 77 M  */ new OP_Colon_M(),
		/* 78 N  */ new OP_Colon_N(),
		/* 79 O  */ new OP_Colon_O(),
		/* 80 P  */ new OP_Colon_P(),
		/* 81 Q  */ null,
		/* 82 R  */ new OP_Colon_R(),
		/* 83 S  */ new OP_Colon_S(),
		/* 84 T  */ new OP_Colon_T(),
		/* 85 U  */ null,
		/* 86 V  */ new OP_Colon_V(),
		/* 87 W  */ null,
		/* 88 X  */ null,
		/* 89 Y  */ null,
		/* 90 Z  */ new OP_Colon_Zed(),
		/* 91 [  */ null,
		/* 92 \  */ null,
		/* 93 ]  */ null,
		/* 94 ^  */ new OP_Colon_Carat(),
		/* 95 _  */ null, // Assignment
		/* 96 `  */ new OP_Colon_Tick(),
		/* 97 a  */ null, // Assignment
		/* 98 b  */ null, // Assignment
		/* 99 c  */ null, // Assignment
		/* 100 d */ null, // Assignment
		/* 101 e */ null, // Assignment
		/* 102 f */ null, // Assignment
		/* 103 g */ null, // Assignment
		/* 104 h */ null, // Assignment
		/* 105 i */ null, // Assignment
		/* 106 j */ null, // Assignment
		/* 107 k */ null, // Assignment
		/* 108 l */ null, // Assignment
		/* 109 m */ null, // Assignment
		/* 110 n */ null, // Assignment
		/* 111 o */ null, // Assignment
		/* 112 p */ null, // Assignment
		/* 113 q */ null, // Assignment
		/* 114 r */ null, // Assignment
		/* 115 s */ null, // Assignment
		/* 116 t */ null, // Assignment
		/* 117 u */ null, // Assignment
		/* 118 v */ null, // Assignment
		/* 119 w */ null, // Assignment
		/* 120 x */ null, // Assignment
		/* 121 y */ null, // Assignment
		/* 122 z */ null, // Assignment
		/* 123 { */ null, // Named instructions
		/* 124 | */ new OP_SetMinus(),
		/* 125 } */ null,
		/* 126 ~ */ new OP_Colon_Tilde()
	};
	
	public static boolean isColonOpChar(char c) {
		//A char is a colonOp if it is not a lowercase letter or a '('
		return (c >= '!' && c <= '~') 		 //Char bounds
				&& !SymbolTable.isBasicSymbolChar(c)  //Not variable char
				&& !isDigit(c) 		         //Not digit
				&& c != '(' && c != ' ' && c != '-'; //Special cases
	}

	private static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	/** Returns the operation bound to the character */
	public static OperatorInstruction getOp(char c, SourceStringRef source) throws NotAnOperatorError {
		OperatorInstruction op = getOpOrNull(c, source);
		if (op == null) {
			throw new NotAnOperatorError(":" + c, source);
		} else {
			return op;
		}
	}
	
	private static OperatorInstruction getOpOrNull(char op, SourceStringRef source) {
		if(op >= 33 && op <= 126) {
			Operator operator = COLON_OPS[op-FIRST_OP];
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
class OP_Colon_Bang extends Operator {
	
	public OP_Colon_Bang() {
		init(":!");
		arg("AA", "assert equal");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		final Obj b = blockEvaluator.pop();
		if (!a.equiv(b)) {
			throw new AssertError("AssertError", a, b);
		}
	}
}

// " - 34
class OP_Colon_Quote extends Operator {

	public OP_Colon_Quote() {
		init(":'");
		arg("C", "to int");
		arg("S", "convert a string to bytes using UTF-8 encoding");
		arg("N", "identity; return N");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		
		if (a.isa(NUMBER)) {
			blockEvaluator.push(a);
		} else if (a.isa(CHAR)) { 
			int c = ((Char)a).charValue();
			blockEvaluator.push(Num.fromInt(c & 0xff));
		} else if (a.isa(STR)) {
			Str s = asStr(a);
			byte[] bytes = s.getBytes();
			double[] nums = new double[bytes.length];
			for (int i = 0; i < bytes.length; i++) {
				nums[i] = bytes[i];
			}
			blockEvaluator.push(new List(new DoubleList(nums)));
		} else {
			throw new TypeError(this, a);
		}
	}
}


// # - 35
class OP_Colon_Pound extends Operator {
	
	private OP_O op = new OP_O();
	
	public OP_Colon_Pound() {
		init(":#");
		arg("L:#B", "map");
		arg("D:#B", "map over key value pairs");
		setOverload(-1, "each");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		this.op.execute(blockEvaluator);
	}
}

// $ - 36
class OP_Colon_Duplicate extends Operator {
	
	public OP_Colon_Duplicate() {
		init(":$");
		arg("..AN", "copies the first N items on the stack (not including N)");
	}

	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		
		if (a.isa(NUMBER)) {
			int size = blockEvaluator.getStack().size();
			int i = ((Number)a).toInt();
			
			if (i > size || i <= 0) {
				throw new ValueError(i + " :$ stack index out of bounds");
			} else {
				while (i > 0) {
					final Obj cp = blockEvaluator.getStack().get(size - i);
					blockEvaluator.push(cp.deepcopy());
					i--;
				}
			}
			
		} else {
			
		}
	}
}

// % - 38
class OP_Colon_Percent extends Operator {

	public OP_Colon_Percent() {
		init(":%");
		arg("NN", "mod");
		vect();
		setOverload(2, "mod");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.mod(b);}
		public NumberList nl(Number a, NumberList b) { return b.modFrom(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.mod(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
	}

	// a b % => "a % b"
	@Override
	public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
		Obj result;
		// Vectorize?
		result = VectorizedFunctions.vectorize2arg(context, this, a, b, NUML_OP);
		if (result != null) {
			return result;
		}
		// Overload?
		result = overload().executeAndReturn(context, b, a); // stack order
		if (result != null) {
			return result;
		}
		// Standard operation
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			try {
				return NumberMath.mod(asNumber(a), asNumber(b));
			} catch (ArithmeticException e) {
				throw new MathError("Divide by 0 in expression " + a.str() + " " + b.str() + "%" );
			}
		} else {
			throw new TypeError(this, b, a); // stack order
		}
	}
}

// & - 39
class OP_Colon_And extends Operator {
	
	public OP_Colon_And() {
		init(":&");
		arg("A", "duplicate reference (same as $ but does not make a copy)");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.peek();
		blockEvaluator.push(a);
	}
}

//* - 42
class OP_Colon_Times extends Operator {
	
	public OP_Colon_Times() {
		init(":*");
		arg("LLB", "outer product of two lists using B");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj c = blockEvaluator.pop();
		Obj b  = blockEvaluator.pop();
		Obj a  = blockEvaluator.pop();
		
		if (c.isa(BLOCK)) {
			StaticBlock expr = Casting.asStaticBlock(c);
			if (b.isa(LIST) && a.isa(LIST)) {
				List l1 = asList(b);
				List l2 = asList(a);

				ArrayList<Obj> out = new ArrayList<Obj>(l2.length());
				for (int i = 0; i < l2.length(); i++) {
					out.add(ListIterationFunctions.map1arg(blockEvaluator.getContext(), l1, expr, l2.getExact(i)));
				}
				
				blockEvaluator.push(new List(out));
			} else if (b.isa(LIST)) {
				blockEvaluator.push(ListIterationFunctions.map1arg(blockEvaluator.getContext(), asList(b), expr, a));
			} else if (a.isa(LIST)) {
				StaticBlock e = StaticBlock.EMPTY;
				e = BlockUtils.addAll(e, expr);
				e = BlockUtils.add(e, b);
				blockEvaluator.push(ListIterationFunctions.map(blockEvaluator.getContext(), asList(a), e));
			} else {
				throw new TypeError(this, c, b, a);
			}
		} else {
			throw new TypeError(this, c, b, a);
		}
	}
}

// / - 47
class OP_Colon_Semicolon extends Operator {
	
	public OP_Colon_Semicolon() {
		init(":;");
		arg("..AA", "clear all but the top of the stack");
	}

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		blockEvaluator.clearStack();
		blockEvaluator.push(a);
	}
}


// < - 60
class OP_Colon_LessThan extends Operator {
	
	public OP_Colon_LessThan() {
		init(":<");
		arg("NN|CC|SS", "less then or equal to");
		vect();
		setOverload(2, "leq");
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.leq(b);}
		public NumberList nl(Number a, NumberList b) { return b.geq(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.leq(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
	}

	// a b :< => "a :< b"
	@Override
	public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(context, this, a, b, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(context, b, a)) != null) return res; // stack order

		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			return Num.fromBool(((Number)a).compareTo((Number)b) <= 0);
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			return Num.fromBool(((Char)a).compareTo((Char)b) <= 0);
		} else if (a.isa(STR) && b.isa(STR)) {
			return Num.fromBool(a.str().compareTo(b.str()) <= 0);
		} else {
			throw new TypeError(this, b, a); // stack order
		}
	}
}

// = - 61
class OP_Colon_Equals extends Operator {
	
	public OP_Colon_Equals() {
		init(":=");
		arg("AJ|AC|AS", "assign A to variable");
	}

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj sym = blockEvaluator.pop();
		final Obj obj = blockEvaluator.peek();
	
		final ExecutionContext context = blockEvaluator.getContext();

		if (sym.isa(SYMBOL)) {
			context.getVars().setVar((Symbol)sym, obj);
		} else if (sym.isa(CHAR) || sym.isa(STR)) {
			String s = sym.str();
			context.getVars().setVar(SymbolTable.getSymbol(s), obj);
		} else {
			throw new TypeError(this, sym, obj);
		}
	}
}

// > - 62
class OP_Colon_GreaterThan extends Operator {
	
	public OP_Colon_GreaterThan() {
		init(":>");
		arg("NN|CC|SS", "greater than or equal to");
		setOverload(2, "geq");
		vect();
	}

	private static NumberListOp NUML_OP = new NumberListOp() {
		public NumberList ln(NumberList a, Number b) { return a.geq(b);}
		public NumberList nl(Number a, NumberList b) { return b.leq(a);}
		public NumberList ll(NumberList a, NumberList b) { return a.geq(b);}
		public NumberList l(NumberList a) { throw new UnimplementedError(); }
	};

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();
		blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
	}

	// a b :> => "a :> b"
	@Override
	public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
		Obj res;
		if ((res = VectorizedFunctions.vectorize2arg(context, this, a, b, NUML_OP)) != null) return res;
		if ((res = overload().executeAndReturn(context, b, a)) != null) return res; // stack order
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			return Num.fromBool(((Number)a).compareTo((Number)b) >= 0);
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			return Num.fromBool(((Char)a).compareTo((Char)b) >= 0);
		} else if (a.isa(STR) && b.isa(STR)) {
			return Num.fromBool(a.str().compareTo(b.str()) >= 0);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// ? - 63
class OP_Colon_Bool extends Operator {
	
	public OP_Colon_Bool() {
		init(":?");
		arg("A", "if/else");
	}

	@Override
	public void execute (BlockEvaluator blockEvaluator) {		
		Obj a = blockEvaluator.pop();
		if (a.isa(Obj.BLOCK)) {
			Obj result = ConditionalUtils.runConditional(blockEvaluator.getContext(), Casting.asStaticBlock(a));
			if (result != null) {
				blockEvaluator.push(result);
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}



// A - 65
class OP_Colon_A extends Operator {
	
	public OP_Colon_A() {
		init(":A");
		arg("..AN", "collect N items from stack into list");
	}

	@Override
	public void execute (BlockEvaluator blockEvaluator) {		
		final Obj n = blockEvaluator.pop();
		
		if (n.isa(NUMBER)) {
			int N = ((Number)n).toInt();
			ArrayList<Obj> arr = new ArrayList<>();
			for (int i = 0; i < N; i++) {
				arr.add(blockEvaluator.pop());
			}
			Collections.reverse(arr);
			blockEvaluator.push(new List(arr));
		} else {
			throw new TypeError(this, n);
		}
	}
}

// B - 66
class OP_Colon_B extends Operator {
	
	public OP_Colon_B() {
		init(":B");
		arg("S", "interpolate string");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		// TODO: SourceStringRef
		String s = blockEvaluator.pop().str();
		SourceString source = new SourceString(s, "<:B>");
		StringToken str_token = new StringToken(s, true, source.ref(source.length()));
		try {
			str_token.getInstruction().execute(blockEvaluator);
		} catch (ParserException e) {
			throw new ValueError("Error when parsing string at :% " + e.getMessage());
		}
	}
}



// C - 67
class OP_Colon_C extends Operator {
	
	public OP_Colon_C() {
		init(":C");
		arg("J", "convert symbol to string name");
		arg("S", "return S");
	}

	@Override
	public void execute (BlockEvaluator blockEvaluator) {		
		final Obj a = blockEvaluator.pop();
		
		if (a.isa(SYMBOL)) {
			blockEvaluator.push(List.fromString(asSymbol(a).name()));
		} else if (a.isa(STR)) {
			blockEvaluator.push(a);
		} else {
			throw new TypeError(this, a);
		}
	}
}

// D - 68
class OP_Colon_D extends Operator {
	
	public OP_Colon_D() {
		init(":D");
		arg("ASD|AJD", "set dict index");
	}
	
	private void set(Dict d, Obj key, Obj value) {
		if (key.isa(SYMBOL)) {
			d.set((Symbol)key, value);
		} else if (key.isa(STR)) {
			Symbol s = SymbolTable.getSymbol(key.str());
			d.set(s, value);
		} else {
			throw new TypeError(this, d, key, value);
		}
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj dict = blockEvaluator.pop();
		final Obj index = blockEvaluator.pop();
		final Obj item = blockEvaluator.pop();

		if (dict.isa(DICT)) {
			Dict d = (Dict)dict;
			if (index.isa(LIST) && !index.isa(STR)) {
				List list = asList(index);
				for (int i = 0; i < list.length(); i++) {
					set(d, list.getExact(i), item);
				}
			} else {
				set(d, index, item);
			}
			blockEvaluator.push(d);
		} else {
			throw new TypeError(this, dict, index, item);
		}
	}
}

// E - 69
class OP_Colon_E extends Operator {
	
	public OP_Colon_E() {
		init(":E");
		arg("D", "number or items in a dict");
		arg("L", "shape");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		
		if (a.isa(DICT)) {
			blockEvaluator.push(Num.fromInt( ((Dict)a).size()) );
		} else if (a.isa(LIST)) {
			blockEvaluator.push(asList(a).shape());
		} else {
			throw new TypeError(this, a);
		}
	}
	
}

// F - 70
class OP_Colon_F extends Operator {
	
	public OP_Colon_F() {
		init(":F");
		arg("S", "load aya source file");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		
		if (a.isa(STR)) {			
			File readFile = FileUtils.resolveFile(a.str());

			String content = null;
			try {
				content = FileUtils.readAllText(readFile);
			} catch (IOException e) {
				throw new IOError(":F", readFile.getAbsolutePath(), e);
			}
			
			// Is there a shebang? If yes, drop the first line
			if (content.length() > 0 && content.charAt(0) == '#' && content.charAt(1) == '!') {
				int line_end = content.indexOf('\n');
				content = content.substring(line_end);
			}
			
			SourceString source = new SourceString(content, readFile.getAbsolutePath());

			try {
				StaticBlock block = Parser.compile(source);
				blockEvaluator.dump(block);
			} catch (ParserException e) {
				throw new InternalAyaRuntimeException(e.typeSymbol(), e);
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}


// G - 71
class OP_Colon_G extends Operator {
	
	public OP_Colon_G() {
		init(":G");
		arg("", "Return the variable scope stack as a list of dicts");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		blockEvaluator.push(blockEvaluator.getContext().getVars().getDictList());
	}
}

// I - 73  
class OP_Colon_I extends Operator {
	
	public OP_Colon_I() {
		init(":I");
		arg("DS|DJ", "get dict item from key");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj index = blockEvaluator.pop();
		final Obj list = blockEvaluator.pop();
		
		
		if (list.isa(DICT)) {
			Obj out = null;
			final Dict d = ((Dict)list);
			
			if (index.isa(STR)) {
				Symbol s = SymbolTable.getSymbol(index.str());
				out = d.get(s);
			} else if (index.isa(SYMBOL)) {
				out = d.get( (Symbol)index );
			} else if (index.isa(LIST)) {
				List l = asList(index);
				if (l.length() != 2 || !l.getExact(0).isa(SYMBOL)) {
					throw new TypeError(this, index, list);
				}
				
				Symbol key = asSymbol(l.getExact(0));
				if (d.containsKey(key)) {
					out = d.get(key);
				} else {
					out = l.getExact(1);
				}
				
			} else {
				throw new TypeError(this, index, list);
			}
			
			blockEvaluator.push(list);
			blockEvaluator.push(out);
		} else {
			throw new TypeError(this, index, list);
		}
	}
}

// J - 74
class OP_Colon_J extends Operator {
	
	public OP_Colon_J() {
		init(":J");
		arg("LL", "concatenate lists (modify list 1)");
		arg("LA|AL", "add to list (modify list)");
		arg("AA", "create list [ A A ]");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		final Obj b = blockEvaluator.pop();
		
		if (a.isa(LIST) && b.isa(LIST)) {
			asList(b).mutAddAll(asList(a));
			blockEvaluator.push(b);
		} else if (a.isa(LIST)){//&& !a.isa(Obj.STR)) {			
			asList(a).mutAddExact(0, b);
			blockEvaluator.push(a);
		} else if (b.isa(LIST)){//&& !b.isa(Obj.STR)) {
			asList(b).mutAdd(a);
			blockEvaluator.push(b);
		} else {
			final ArrayList<Obj> list = new ArrayList<Obj>();
			list.add(b);  //Stack - Add in reverse order
			list.add(a);
			blockEvaluator.push(new List(list));
		}

	}
}

// K - 75
class OP_Colon_K extends Operator {
	
	public OP_Colon_K() {
		init(":K");
		arg("D", "return a list of keys as symbols");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		
		if (a.isa(DICT)) {
			ArrayList<Symbol> keys = asDict(a).keys();
			List out = new List();
			for (Symbol k : keys) out.mutAdd(k);
			blockEvaluator.push(out);
		} else {
			throw new TypeError(this, a);
		}
	}
}

// M - 77
class OP_Colon_M extends Operator {
	
	public OP_Colon_M() {
		init(":M");
		arg("DD", "set D1's meta to D2 leave D1 on stack");
		arg("BD", "update a copy of the blockEvaluator locals with the dict");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj meta = blockEvaluator.pop();
		final Obj obj = blockEvaluator.pop();

		if(obj.isa(DICT) && meta.isa(DICT)) {
			((Dict)obj).setMetaTable((Dict)meta);
			blockEvaluator.push(obj);
		} else if(obj.isa(BLOCK) && meta.isa(DICT)) {
			StaticBlock new_block = BlockUtils.mergeLocals(Casting.asStaticBlock(obj), asDict(meta));
			blockEvaluator.push(new_block);
		} else {
			throw new TypeError(this, meta, obj);
		}
	}
}

// N - 79
class OP_Colon_N extends Operator {
	
	public OP_Colon_N() {
		init(":N");
		arg("LA", "find all instances of A in L");
	}

	@Override
	public void execute (BlockEvaluator blockEvaluator) {		
		final Obj a = blockEvaluator.pop(); //Item
		final Obj b = blockEvaluator.peek(); //List


		if(b.isa(Obj.LIST)) {			
			blockEvaluator.push(asList(b).findAll(a));
		} else {
			throw new TypeError(this, a, b);
		}
	}
}




class OP_Colon_O extends Operator {
	
	public OP_Colon_O() {
		init(":O");
		arg("AAB", "apply (2-arg)");
	}

	private static class BlockOpInstruction extends Operator {
		private final StaticBlock _block;
		public BlockOpInstruction(StaticBlock block) {
			_block = block;
		}
		@Override
		public void execute(BlockEvaluator blockEvaluator) {
			final Obj b = blockEvaluator.pop();
			final Obj a = blockEvaluator.pop();
			blockEvaluator.push(exec2arg(blockEvaluator.getContext(), a, b));
		}
		@Override
		public Obj exec2arg(ExecutionContext context, final Obj a, final Obj b) {
			Obj res;
			if ((res = VectorizedFunctions.vectorize2arg(context, this, a, b)) != null) {
				return res;
			} else {
				BlockEvaluator blk = context.createEvaluator();
				blk.push(a);
				blk.push(b);
				blk.dump(_block);
				blk.eval();
				return blk.pop();
			}
		}
	}


	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj c = blockEvaluator.pop(); // block
		final Obj b = blockEvaluator.pop();
		final Obj a = blockEvaluator.pop();

		if (c.isa(Obj.BLOCK)) {
			final BlockOpInstruction block_op = new BlockOpInstruction(Casting.asStaticBlock(c));
			final Obj result = VectorizedFunctions.vectorize2arg(blockEvaluator.getContext(), block_op, a, b);
			if (result == null) {
				throw new ValueError("Cannot vectorize over args: " + a.repr() + ", " + b.repr());
			} else {
				blockEvaluator.push(result);
			}
		} else {
			throw new TypeError(this, c, b, a);
		}

	}

}


// P - 80
class OP_Colon_P extends Operator {
	
	public OP_Colon_P() {
		init(":P");
		arg("A", "println to stdout");
	}

	@Override
	public void execute (BlockEvaluator blockEvaluator) {		
		StaticData.IO.out().println(blockEvaluator.pop().str());
	}
}

//R - 82
class OP_Colon_R extends Operator {
	
	public OP_Colon_R() {
		init(":R");
		arg("-", "readline from stdin");
	}

	@Override
	public void execute (BlockEvaluator blockEvaluator) {		
		blockEvaluator.push(List.fromString(StaticData.IO.nextLine()));
	}
}


// S - 83
class OP_Colon_S extends Operator {
	
	public OP_Colon_S() {
		init(":S");
		arg("S|C", "convert to symbol");
		arg("B", "if blockEvaluator has single var or op convert to symbol list, else return empty list");
	}

	@Override
	public void execute (BlockEvaluator blockEvaluator) {		
		final Obj a = blockEvaluator.pop();
		
		if (a.isa(STR) || a.isa(CHAR)) {
			blockEvaluator.push(SymbolTable.getSymbol(a.str()));
		} else if (a.isa(BLOCK)) {
			blockEvaluator.push(BlockUtils.singleToSymbolList(Casting.asStaticBlock(a)));
		} else {
			throw new TypeError(this, a);
		}
	}

}



//T - 84
class OP_Colon_T extends Operator {
	
	public OP_Colon_T() {
		init(":T");
		arg("A", "type of");
	}
		
	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		blockEvaluator.push(TypeUtils.getType(blockEvaluator.pop()));
	}
}



//V - 86
class OP_Colon_V extends Operator {
	
	public OP_Colon_V() {
		init(":V");
		arg("D", "return a list of values");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		
		if (a.isa(DICT)) {
			blockEvaluator.push( new List(asDict(a).values()) );
		} else {
			throw new TypeError(this, a);
		}
	}
}


//Z - 90
class OP_Colon_Zed extends Operator {
	
	public OP_Colon_Zed() {
		init(":Z");
		arg("N", "sleep (milliseconds)");
	}

	@Override
	public void execute (BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		
		if(a.isa(NUMBER)) {
			try {
				Thread.sleep(((Number)a).toLong());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

// ^ - 94
class OP_Colon_Carat extends Operator {
	
	public OP_Colon_Carat() {
		init(":^");
		arg("LL", "set intersection");
	}

	@Override
	public void execute (BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		Obj b = blockEvaluator.pop();
		
		if(a.isa(LIST) && b.isa(LIST)) {
			blockEvaluator.push(asList(a).intersect(asList(b)));
		} else {
			throw new TypeError(this, a);
		}
	}
}





//` - 96
class OP_Colon_Tick extends Operator {
	
	public OP_Colon_Tick() {
		init(":`");
		arg("BN:`A", "wrap next N instructions in a blockEvaluator");
	}

	@Override
	public void execute (BlockEvaluator blockEvaluator) {
		Obj n_obj = blockEvaluator.pop();
		Obj blk_obj = blockEvaluator.pop();
		if (n_obj.isa(Obj.NUM) && blk_obj.isa(Obj.BLOCK)) {
			int n = Casting.asNumber(n_obj).toInt();
			boolean unwrap_list = false;
			if (n == 0) {
				n = 1;
				unwrap_list = true;
			}
			List l = new List();
			for (int i = 0; i < n; i++) {
				StaticBlock b = BlockUtils.makeBlockWithSingleInstruction(blockEvaluator.getInstructions().popNextNonFlagInstruction());
				l.mutAdd(b);
			}
			if (unwrap_list) {
				blockEvaluator.push(l.getExact(0));
			} else {
				blockEvaluator.push(l);
			}
			
			blockEvaluator.dump(Casting.asStaticBlock(blk_obj));
			
		} else {
			throw new TypeError(this, n_obj, blk_obj);
		}
	}
}




// \ - 92


//_ - 95


// | - 124
class OP_SetMinus extends Operator {
	
	public OP_SetMinus() {
		init(":|");
		arg("LL", "remove all elements in L2 from L1");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();
		Obj b = blockEvaluator.pop();
		
		if (a.isa(LIST) && b.isa(LIST)) {
			blockEvaluator.push(asList(b).removeAllOccurances(asList(a)));
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

class OP_IsInstance extends Operator {
	
	public OP_IsInstance() {
		init(":@");
		arg("AA", "isinstance");
	}

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj type = blockEvaluator.pop();
		final Obj obj = blockEvaluator.pop();
		
		if (type.isa(DICT)) {
			blockEvaluator.push(Num.fromBool(TypeUtils.isInstance(obj, Casting.asDict(type), blockEvaluator.getContext())));
		} else {
			throw new TypeError(this, obj, type);
		}
	}
}
	
// ~ - 126
class OP_Colon_Tilde extends Operator {
	
	public OP_Colon_Tilde() {
		init(":~");
		arg("L", "remove duplicates");
	}

	@Override
	public void execute(final BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		
		if (a.isa(LIST)) {
			blockEvaluator.push( asList(a).unique() );
		} else {
			throw new TypeError(this, a);
		}
	}
}
	
