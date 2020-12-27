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
import static aya.util.Casting.asStr;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aya.Aya;
import aya.AyaPrefs;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.TypeError;
import aya.instruction.index.AnonGetIndexInstruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.dict.DictIndexing;
import aya.obj.list.GenericList;
import aya.obj.list.List;
import aya.obj.list.ListRangeUtils;
import aya.obj.list.Str;
import aya.obj.list.numberlist.NumberItemList;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.BigNum;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.NumberMath;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolEncoder;
import aya.parser.CharacterParser;
import aya.parser.Parser;
import aya.util.FileUtils;
import aya.util.Pair;

public class Ops {
	
	public static final Random RAND = new Random((new Date()).getTime());
	public static final Pattern PATTERN_URL = Pattern.compile("http:\\/\\/.*|https:\\/\\/.*");

	
	////////////////////////
	// OPERATOR OVERLOADS //
	////////////////////////

	public static final Symbol KEYVAR_EQ	= Symbol.fromStr("__eq__");
	
	// List
	public static final Symbol KEYVAR_GETINDEX = Symbol.fromStr("__getindex__");
	public static final Symbol KEYVAR_SETINDEX = Symbol.fromStr("__setindex__");
	public static final Symbol KEYVAR_HEAD = Symbol.fromStr("__head__");
	public static final Symbol KEYVAR_TAIL = Symbol.fromStr("__tail__");
	public static final Symbol KEYVAR_MAP = Symbol.fromStr("__map__");
	public static final Symbol KEYVAR_LEN = Symbol.fromStr("__len__");
	
	// Misc.
	public static final Symbol KEYVAR_OR		= Symbol.fromStr("__or__");
	public static final Symbol KEYVAR_ROR		= Symbol.fromStr("__ror__");
	public static final Symbol KEYVAR_NEW 	= Symbol.fromStr("__new__");
	public static final Symbol KEYVAR_FLOAT 	= Symbol.fromStr("__float__");
	public static final Symbol KEYVAR_EACH 	= Symbol.fromStr("__each__");
	public static final Symbol KEYVAR_STR 	= Symbol.fromStr("__str__");
	public static final Symbol KEYVAR_REPR 	= Symbol.fromStr("__repr__");
	public static final Symbol KEYVAR_BOOL    = Symbol.fromStr("__bool__");
	
	
	public static final char FIRST_OP = '!';
	public static final OpInstruction[] OPS = {
		/* 33 !  */ new OP_Bang(),
		/* 34 "  */ null, // String
		/* 35 #  */ new OP_Pound(),
		/* 36 $  */ new OP_Duplicate(),
		/* 37 %  */ new OP_Percent(),
		/* 38 &  */ new OP_And(),
		/* 39 '  */ null, // Character / Base Numbers
		/* 40 (  */ null, // Lambda
		/* 41 )  */ null, // Lambda
		/* 42 *  */ new OP_Times(),
		/* 43 +  */ new OP_Plus(),
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
		/* 70 F  */ new OP_F(),
		/* 71 G  */ new OP_G(),
		/* 72 H  */ new OP_H(),
		/* 73 I  */ new OP_GetIndex(),
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
		/* 84 T  */ new OP_T(), 
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
	
	/* 124 | */ public static final OpInstruction BAR = new OP_Bar();
	/* 126 ~ */ public static final OpInstruction TILDE = new OP_Tilde();
	
	//Special Ops
	//public static final Operation APPLY_TO = new OP_ApplyTo();
	public static final OpInstruction GETINDEX = new OP_GetIndex();
	public static final OpInstruction SETINDEX = new OP_SetIndex();
	
	/** Returns true if char c is bound to an operator */
	public static boolean isOp(char c) {
		if(c <= 96 && c >= FIRST_OP ) {
			return OPS[c-FIRST_OP] != null;
		} else if (c == '|' || c == '~') {
			return true;
		}
		return false;
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
	
	public static OpInstruction getOp(char op) {
		if ((op >= 33 && op <= 47) || (op >= 59 && op <= 96)) {
			return OPS[op-FIRST_OP];
		} else if (op == '~') {
			return TILDE;
		} else if (op == '|') {
			return BAR;
		} else {
			throw new RuntimeException("Operator " + op + " does not exist");
		}
	}
	
}

// ! - 33
class OP_Bang extends OpInstruction {
	
	public OP_Bang() {
		init("!");
		arg("N", "1-N (logical not, complementary probability)");
		arg("C", "swap case");
		vect();
		setOverload(-1, "new");
	}

	@Override public void execute(final Block block) {
		Obj o = block.pop();
		
		if (o.isa(Obj.NUMBER)) {
			block.push(Num.ONE.sub(asNumber(o)));
		} else if (o.isa(Obj.CHAR)) {
			block.push(((Char)o).swapCase());
		} else if (o.isa(STR)) {
				block.push(new List(asStr(o).swapCase()));
		} else if (o.isa(Obj.NUMBERLIST)) {
			block.push( new List(asNumberList(o).subFrom(Num.ONE)) );
		} else if (o.isa(DICT)) {
			Dict d = (Dict)o;
			if (d.containsKey(Ops.KEYVAR_NEW)) {
				block.push(o);
				block.addOrDumpVar(d.get(Ops.KEYVAR_NEW));
			} else {
				//Create a new empty dict with the input as its metatable
				//block.push(new Dict((Dict)o));
				throw new AyaRuntimeException("! : keyword __new__ not found");
			}
		} else {
			throw new TypeError(this,o);
		}
	}
}

//# - 35
class OP_Pound extends OpInstruction {
	
	public OP_Pound() {
		init("#");
		arg("LA..#A", "map");
	}
	
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		
		if(a.isa(Obj.BLOCK)) {
			final Block map = ((Block)a).duplicate();
						
			Obj popped = block.pop();
			
			//Capture all non-list items from the left of the #
			while (!popped.isa(Obj.LIST)) {
				map.add(popped);
				
				if (block.stackEmpty()) {
					throw new AyaRuntimeException("Could not find list to map to\n"
							+ "\t in " + block.toString() + "\n"
							+ "\t map using " + map);
				} else {
					popped = block.pop();
				}
			}
			
			block.push(asList(popped).map(map));
			
			return;
		} 	
		throw new TypeError(this,a);
	}
}

// $ - 36
class OP_Duplicate extends OpInstruction {
	
	public OP_Duplicate() {
		init("$");
		arg("A", "deepcopy (duplicate)");
	}

	@Override public void execute (final Block block) {
		block.push(block.peek().deepcopy());
	}
}

// % - 37
class OP_Percent extends OpInstruction {
	
	public OP_Percent() {
		init("%");
		arg("NN", "mod");
		arg("BN", "repeat B N times");
		setOverload(2, "mod");
	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if (overload().execute(block, a, b)) return;
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			try {
				//b mod a
				block.push(NumberMath.mod(asNumber(b), asNumber(a)));
			} catch (ArithmeticException e) {
				throw new AyaRuntimeException("%: Divide by 0");
			}
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( new List(asNumberList(a).modFrom((Number)b)) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(b).mod((Number)a)) );
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(b).mod(asNumberList(a))) );
		} else if (a.isa(NUMBER) && b.isa(BLOCK)) {
			int repeats = ((Number)(a)).toInt();
			Block blk = ((Block)b);
			for (int i = 0; i < repeats; i ++) {
				block.addAll(blk.getInstructions().getInstrucionList());
			}
			return;
		} else {
			throw new TypeError(this, a,b);
		}
	}
}



// & - 38
class OP_And extends OpInstruction {
	
	public OP_And() {
		init("&");
		arg("NN", "bitwise and");
		arg("SS", "list all expressions matching the regex");
		setOverload(2, "and");

	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if (overload().execute(block, a, b)) return;
		
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( NumberMath.band((Number)a, (Number)b) );
		} else if (a.isa(SYMBOL)) {
			long varid = ((Symbol)a).id();
			Aya.getInstance().getVars().setVar(varid, b);
			block.push(b);
		} else if (a.isa(Obj.STR) && b.isa(Obj.STR)) {
			ArrayList<Obj> allMatches = new ArrayList<Obj>();
			Matcher m = Pattern.compile(a.str()).matcher(b.str());
			while (m.find()) {
				 allMatches.add(List.fromString(m.group()));
			}
			block.push(new List(allMatches));
		} else {
			throw new TypeError(this, a);
		}

	}
}

// * - 42
class OP_Times extends OpInstruction {
	
	public OP_Times() {
		init("*");
		arg("NN", "multiply");
		arg("LS|LC", "join");
		vect();
		setOverload(2, "mul");
	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();

		if (overload().execute(block, a, b)) return;
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push(NumberMath.mul(asNumber(a), asNumber(b)));
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( new List(asNumberList(a).mul((Number)b)) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(b).mul(asNumber(a))) );
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(a).mul(asNumberList(b))) );
		} else if ((a.isa(STR) || a.isa(CHAR)) && b.isa(LIST)) {
			StringBuilder sb = new StringBuilder();
			List lb = asList(b);
			String sep = a.str();
			for (int i = 0; i < lb.length()-1; i++) {
				sb.append(lb.getExact(i).str());
				sb.append(sep);
			}
			if (lb.length() > 0) sb.append(lb.tail().str());
			block.push(List.fromString(sb.toString()));
		} else {	
			throw new TypeError(this, a,b);
		}
	}
}


// + - 43
class OP_Plus extends OpInstruction {
	
	public OP_Plus() {
		init("+");
		arg("NN|CC", "add");
		arg("SA|AS", "append string");
		vect();
		setOverload(2, "add");
	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();

		if (overload().execute(block, a, b)) return;
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push(NumberMath.add(asNumber(a), asNumber(b)));
		} else if (a.isa(STR) || b.isa(STR)) {
			//Must reverse order
			block.push(List.fromString(b.str() + a.str()));
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( new List(asNumberList(a).add(asNumber(b))) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(b).add(asNumber(a))) );
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(a).add(asNumberList(b))) );
		} else if (a.isa(Obj.NUMBER) && b.isa(CHAR)) {
			block.push( ((Char)b).add((Number)a) );
		} else if (a.isa(CHAR) && b.isa(NUMBER)) {
			block.push( ((Char)a).add((Number)b) );
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			block.push( ((Char)a).add((Char)b) );
		} else {
			throw new TypeError(this, a,b);
		}
	}
}

// - - 44
class OP_Minus extends OpInstruction {
	
	public OP_Minus() {
		init("-");
		arg("NN|CC", "subtract");
		vect();
		setOverload(2, "sub");
	}

	@Override
	public void execute(final Block block) {
		final Obj b = block.pop();	//Pop in reverse order
		final Obj a = block.pop();

		if (overload().execute(block, b, a)) return;
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push(NumberMath.sub(asNumber(a), asNumber(b)));
		} else if (a.isa(NUMBER) && b.isa(CHAR)) {
			block.push(((Char)b).subFrom((Number)a));
		} else if (a.isa(CHAR) && b.isa(Obj.NUMBER)) {
			block.push( ((Char)a).sub((Number)b) );
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			block.push( ((Char)a).sub((Char)b) );
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( new List(asNumberList(a).sub(asNumber(b))) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(b).subFrom(asNumber(a))) );
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(a).sub(asNumberList(b))) );
		} else {
			throw new TypeError(this, a,b);
		}
	}
}

// / - 47
class OP_Divide extends OpInstruction {
	
	public OP_Divide() {
		init("/");
		arg("NN", "divide");
		vect();
		setOverload(2, "div");

	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if (overload().execute(block, a, b)) return;
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push(NumberMath.sub(asNumber(b), asNumber(a)));
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( new List(asNumberList(a).divFrom(asNumber(b))) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(b).div(asNumber(a))) );
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(b).div(asNumberList(a))) );
		} else {
			throw new TypeError(this, a,b);
		}
	}
}


// ; - 59
class OP_SemiColon extends OpInstruction {
	
	public OP_SemiColon() {
		init(";");
		arg("A", "pop and discard");
	}

	@Override
	public void execute (final Block block) {
		block.pop();
	}
}


// < - 60
class OP_LessThan extends OpInstruction {
	
	public OP_LessThan() {
		init("<");
		arg("NN|CC|SS", "less than");
		setOverload(2, "lt");
	}

	@Override
	public void execute(final Block block) {
		final Obj b = block.pop();			// Popped in Reverse Order
		final Obj a = block.pop();
		
		if (overload().execute(block, b, a)) return;
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( Num.fromBool(((Number)a).compareTo((Number)b) < 0) );
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			block.push( Num.fromBool(((Char)a).compareTo((Char)b) < 0) );
		} else if (a.isa(STR) && b.isa(STR)) {
			block.push( Num.fromBool(a.str().compareTo(b.str()) < 0) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(b).gt(asNumber(a))) ); // gt is opposite of lt
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER) ) {
			block.push( new List(asNumberList(a).lt(asNumber(b))) ); 
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST) ) {
			block.push( new List(asNumberList(a).lt(asNumberList(b))) ); 
		} else {
			throw new TypeError(this, a,b);
		}
	}
}


// = - 61
class OP_Equal extends OpInstruction {
	
	public OP_Equal() {
		init("=");
		arg("AA", "equality");
		setOverload(-1, "eq");
	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if (a.isa(DICT)) {
			if (((Dict)a).hasMetaKey(Ops.KEYVAR_EQ)) {
				block.push(b);
				block.callVariable((Dict)a, Ops.KEYVAR_EQ);
			} else {
				if (b.isa(DICT)) {
					block.push(a.equiv(b) ? Num.ONE : Num.ZERO);
				} else {
					block.push(Num.ZERO);
				}
			}
		} else if (b.isa(DICT)) {
			if (((Dict)b).hasMetaKey(Ops.KEYVAR_EQ)) {
				block.callVariable((Dict)b, Ops.KEYVAR_EQ, a);
			} else {
				block.push(Num.ZERO);
			}
		} else {
			block.push(a.equiv(b) ? Num.ONE : Num.ZERO);
		}
	}
}

// > - 62
class OP_GreaterThan extends OpInstruction {
	
	public OP_GreaterThan() {
		init(">");
		arg("NN|CC|SS", "greater than");
		vect();
		setOverload(2, "gt");
	}

	@Override
	public void execute(final Block block) {
		final Obj b = block.pop();			// Popped in Reverse Order
		final Obj a = block.pop();

		if (overload().execute(block, b, a)) return;
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( Num.fromBool(((Number)a).compareTo((Number)b) > 0) );
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			block.push( Num.fromBool(((Char)a).compareTo((Char)b) > 0) );
		} else if (a.isa(STR) && b.isa(STR)) {
			block.push( Num.fromBool(a.str().compareTo(b.str()) > 0) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(b).lt((Number)a)) ); // lt is opposite of gt
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER) ) {
			block.push( new List(asNumberList(a).gt((Number)b)) ); 
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST) ) {
			block.push( new List(asNumberList(a).gt(asNumberList(b))) ); 
		} else {
			throw new TypeError(this, a,b);
		}
	}
}

// ? - 63
class OP_Conditional extends OpInstruction {
	
	public OP_Conditional() {
		init("?");
		arg("AA", "if A1, then A2. If A2 is block, execute it");
	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();

		//  b     a
		// cond {true}

		if(b.bool()) {			
			if(a.isa(BLOCK)) {
				block.addAll(((Block)a).duplicate().getInstructions().getInstrucionList());
			} else {
				block.push(a);
			}
		}
	}
}


// @ - 64
class OP_At extends OpInstruction {
	
	public OP_At() {
		init("@");
		arg("AAA", "rotates the top three elements on the stack [abc->bca]");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		final Obj c = block.pop();
		
		block.push(b);
		block.push(a);
		block.push(c);
	}
}

// A - 65
class OP_A extends OpInstruction {
	
	public OP_A() {
		init("A");
		arg("A", "wrap in list");
	}

	@Override
	public void execute (final Block block) {
		final ArrayList<Obj> al = new ArrayList<Obj>();
		al.add(block.pop());
		block.push(new List(al));
	}
}

// B - 66
class OP_B extends OpInstruction {
	
	public OP_B() {
		init("B");
		arg("N|C", "increment");
		arg("J", "increment in place");
		arg("L", "uncons from front");
		setOverload(1, "inc");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (overload().execute(block, a)) return;

		if (a.isa(Obj.NUMBER)) {
			block.push( ((Number)a).inc() );
		} else if (a.isa(CHAR)) {
			block.push( ((Char)a).inc() );
		} else if (a.isa(SYMBOL)) {
			long varid = ((Symbol)a).id();
			Obj o = Aya.getInstance().getVars().getVar(varid);
			if (o.isa(NUMBER)) {
				Aya.getInstance().getVars().setVar(varid, ((Number)o).inc());
			} else if (o.isa(CHAR)) {
				Aya.getInstance().getVars().setVar(varid, ((Char)o).inc());
			}  else {
				throw new AyaRuntimeException("Cannot increment " + o.repr() 
				+ " in place in call " + a.repr() + " V");
			}
		} else if (a.isa(LIST)) {
			List l = asList(a);
			if (l.length() > 0) {
				Obj popped = l.mutPopBack();
				block.push(l);
				block.push(popped);
			} else {
				throw new AyaRuntimeException("B: unable to remove element from empty list");
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

// C - 67
class OP_Sort extends OpInstruction {
	
	public OP_Sort() {
		init("C");
		arg("S|L", "sort least to greatest");
		arg("N", "bitwise not");
		setOverload(1, "sort");
	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		
		if (overload().execute(block, a)) return;
		
		if (a.isa(Obj.NUMBER)) {
			block.push( NumberMath.bnot((Number)a) );
		} else if(a.isa(LIST)) {
			asList(a).mutSort();
			block.push(a);
		} else {
			throw new TypeError(this,a);
		}		
	}
}



// D - 68
class OP_D extends OpInstruction {
	
	public OP_D() {
		this.name = "D";
		init("D");
		arg("ALN", "set index");
		setOverload(-1, "setindex");

	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();  	//Index
		final Obj b = block.pop();		//List
		final Obj o = block.pop();		//Item

		if (b.isa(DICT)) {
			block.push(o);
			block.callVariable((Dict)b, Ops.KEYVAR_SETINDEX, a);
		}
		else if (b.isa(LIST)) {
			asList(b).mutSetIndexed(a, o);
			block.push(b);
		}
		else {		
			throw new TypeError(this, a, b, o);
		}
		
	}

}

// E - 69
class OP_E extends OpInstruction {
	
	public OP_E() {
		init("E");
		arg("N", "10^N");
		arg("L|S", "length");
		setOverload(1, "len");
	}
	
	private static Num TEN = Num.fromInt(10);

	@Override
	public void execute (final Block block) {
		Obj n = block.pop();

		if (overload().execute(block, n)) return;

		if (n.isa(NUMBER)) {
			block.push( TEN.pow((Number)n) );
		} else if (n.isa(LIST)) {
			block.push( Num.fromInt(asList(n).length()) );
		} else {
			throw new TypeError(this, n);
		}
	}
}

// F - 70
class OP_F extends OpInstruction {
	
	public OP_F() {
		this.name = "F";
		init("F");
		arg("LB", "fold from right to left");
		arg("NN", "unsigned right bitshift");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( NumberMath.unsignedRightShift((Number)b, (Number)a) );
		} else if(a.isa(BLOCK) && b.isa(LIST)) {
			List blist = asList(b);
			
			int length = blist.length();
			if(length == 0) {
				block.push(Num.ZERO);
				return;
			}
			
			Block foldBlock = ((Block)(a));
			
			//Push all but the last item
			//for(int i = 0; i < list.size()-1; i++) {
			for(int i = length-1; i > 0; i--) {
				block.addAll(foldBlock.getInstructions().getInstrucionList());
				block.add(blist.getExact(i));
			}
			//Push the last element outside the loop so that there is not an extra plus (1 1+2+3+)
			//block.add(list.get(list.size()-1));
			block.add(blist.getExact(0));
			return;
		} else {
			throw new TypeError(this, a, b);
		}
	}
}


// G - 71
class OP_G extends OpInstruction {
	
	public OP_G() {
		init("G");
		arg("S", "read a string from a filename or URL");
		arg("N", "isprime");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();
		
		
		if(a.isa(STR)) {
			String name = a.str();
			
			if(Ops.PATTERN_URL.matcher(name).matches()) {
				Scanner scnr = null;
				try {
					URL url = new URL(name);
					scnr = new Scanner(url.openStream());
					StringBuilder sb = new StringBuilder();
					
					while(scnr.hasNext()) {
						sb.append(scnr.nextLine()).append('\n');
					}
					block.push(List.fromString(sb.toString()));
				}
				catch(IOException ex) {
					throw new AyaRuntimeException("Cannot read URL: " + name);
				} finally {
					if(scnr != null)
						scnr.close();
				}
			} else {
				String path = "";
				if (name.charAt(0) == '/' || name.contains(":\\") || name.contains(":/")) {
					path = name;
				} else {
					path = AyaPrefs.getWorkingDir() + name;
				}
				try {
					block.push( List.fromString(FileUtils.readAllText(path)) );
				} catch (IOException e) {
					throw new AyaRuntimeException("Cannot open file: " + new File(path).getAbsolutePath());
				} 
			}
			return;
		} else if (a.isa(NUMBER)) {
			block.push( Num.fromBool(((Number)a).isPrime()) );
		} else {
			throw new TypeError(this, a);
		}
	}
}

class OP_H extends OpInstruction {
	
	public OP_H() {
		init("H");
		arg("SNN|LNN|NNN", "convert base of N|S|L from N1 to N2");
	}

	@Override
	public void execute (final Block block) {
		final Obj to_b = block.pop();
		final Obj from_b = block.pop();
		final Obj num = block.pop();
		
		try {
			if (from_b.isa(NUMBER) && to_b.isa(NUMBER)) {
				int from_base = ((Number)from_b).toInt();
				int to_base = ((Number)to_b).toInt();
				BigInteger out_bi = BigInteger.ZERO;
				
				//Check Radix Ranges
				if ((Character.MIN_RADIX > from_base 
						|| Character.MIN_RADIX > to_base
						|| Character.MAX_RADIX < from_base
						|| Character.MAX_RADIX < to_base) && (
								from_base != 0
								&& to_base != 0
								)){
					throw new AyaRuntimeException("H: base out of range (" + from_base + ", " + to_base + ")");
				}
				
				//String
				if(num.isa(STR)) {
					out_bi  = new BigInteger(num.str(), from_base);
					
				}
				
				//Always base ten
				else if(num.isa(NUMBER)) {
					out_bi = ((Number)num).toBigDecimal().setScale(0, RoundingMode.FLOOR).toBigInteger();
				} 
				
				//Assume base 2
				else if (num.isa(NUMBERLIST)) {
					if (from_base == 2) {
						NumberList bin_list = asNumberList(num);
						StringBuilder sb = new StringBuilder(bin_list.length());
		
							for (int i = 0; i < bin_list.length(); i++) {
								int c = bin_list.get(i).toInt();
								//Check for binary only
								if (c == 1 || c == 0) {
									sb.append(c);
								} else {
									throw new AyaRuntimeException("H: List must be base 2");
								}
								
							}
						out_bi = new BigInteger(sb.toString(), 2);
					} else if (from_base == 0) {
						NumberList nums = asNumberList(num);
						byte[] in_bytes = new byte[nums.length()];
						for (int i = 0; i < nums.length(); i++) {
							int c = nums.get(i).toInt();
							in_bytes[i] = (byte)c;
						}
						out_bi = new BigInteger(in_bytes);
					} else {
						throw new AyaRuntimeException("H: List must be base 2 or bytes (base 0)");
					}
				}
				
				else {
					throw new TypeError(this, num, from_b, to_b);
				}
				
				//OUTPUT
				
				//Convert to best use
				if (to_base == 10) {
					//Larger than an int, return BigDeciaml
					if (out_bi.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) >= 0) {
						block.push(new BigNum(new BigDecimal(out_bi)));
					}
					//Smaller than int 
					else {
						block.push(new Num(out_bi.doubleValue()));
					}
					return;
				} else if (to_base == 2) {
					String bin_str = out_bi.toString(2);
					ArrayList<Number> out_list = new ArrayList<Number>(bin_str.length());
					
					for (char c : bin_str.toCharArray()) {
						out_list.add(new Num(c-'0'));
					}
					block.push(new List(new NumberItemList(out_list)));
					return;
				} else if (to_base == 0) {
					// Special case: byte list
					byte[] bytes = out_bi.toByteArray();
					ArrayList<Number> nums = new ArrayList<>(bytes.length);
					for (byte b : bytes) {
						nums.add(Num.fromByte(b));
					}
					block.push(new List(new NumberItemList(nums)));
					return;
				} else {
					block.push(List.fromString(out_bi.toString(to_base)));
					return;
				}
			}
		}
		catch (NumberFormatException nfe) {
			throw new AyaRuntimeException("H: invalid number format (" 
					+ num.repr() + ", " + from_b.repr() + ", " + to_b.repr() + ")");
		}
		
		
		throw new TypeError(this, num, from_b, to_b);
	}
}

// I - 73
class OP_GetIndex extends OpInstruction {
	
	private AnonGetIndexInstruction _instruction;
	
	public OP_GetIndex() {
		init("I");
		arg("LL|LN", "get index");
		arg("LB", "filter");
		setOverload(-1, "getindex");
		_instruction = new AnonGetIndexInstruction();
	}

	@Override
	public void execute (final Block block) {
		this._instruction.execute(block);
	}
}

// N/A - N/A
class OP_SetIndex extends OpInstruction {
	
	public OP_SetIndex() {
		this.name = ".:";
	}
	@Override public void execute (final Block block) {
		Obj index = block.pop();
		final Obj list = block.pop();
		final Obj item = block.pop();
		
		if (index.isa(LIST) && !index.isa(STR)) {
			List l = asList(index);
			if (l.length() == 1)
				index = l.getExact(0);
		}
		
		// If it is a list, run the standard setindex method
		if(list.isa(LIST)) {		
			asList(list).mutSetIndexed(index, item);
		}
		
		// If it is a dictionary check to see if has a metamethod first
		else if (list.isa(DICT)) {
			if ( ((Dict)list).hasMetaKey(Ops.KEYVAR_SETINDEX) ) {
				block.push(index);
				block.callVariable((Dict)list, Ops.KEYVAR_SETINDEX);
			} else {
				Dict.setIndex((Dict)list, index, item);
			}
		}
		else {
			throw new TypeError(this, index, list);
		}
		
		block.push(list);
	}
}


// J - 74
class OP_Join extends OpInstruction {
	
	public OP_Join() {
		init("J");
		arg("LL", "join lists");
		arg("LA|AL", "add to list");
		arg("AA", "create list [ A A ]");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		final boolean a_is_list = a.isa(LIST) && !a.isa(STR);
		final boolean b_is_list = b.isa(LIST) && !b.isa(STR);
		
		if (a_is_list && b_is_list) {
			block.push(asList(b).copyAddAll(asList(a)));
		} else if (a_is_list) {
			block.push(asList(a).copyAddItemExact(0, b));
		} else if (b_is_list) {
			block.push(asList(b).copyAddItem(a));
		} else {
			final ArrayList<Obj> list = new ArrayList<Obj>();
			list.add(b);  //Stack - Add in reverse order
			list.add(a);
			block.push(new List(list));
		}
	}
}


// L - 76
class OP_L extends OpInstruction {
	
	public OP_L() {
		init("L");
		arg("AN", "create list by repeating A N times");
		arg("NL|LL", "reshape");
	}

	@Override
	public void execute (final Block block) {
		Obj n = block.pop();
		Obj item = block.pop();

		if(n.isa(NUMBER)) {
			int repeats = ((Number)n).toInt();

			if(repeats < 0) {
				throw new AyaRuntimeException("Cannot create list with negative number of elements");
			}

			if (item.isa(CHAR)) {
				block.push( new List(new Str( ((Char)item).charValue(), repeats)) );
			} else if (item.isa(NUMBER)) {
				block.push( new List(new NumberItemList((Number)item, repeats)) );
			} else  {
				block.push( new List(new GenericList(item, repeats)) );
			}

		} 
		
		else if (n.isa(NUMBERLIST) && item.isa(LIST)) {
			block.push( asList(item).reshape(asNumberList(n)) );
		}
		
		else {
			throw new TypeError(this, n , item);
		}
	}
}

// K - 76
class OP_K extends OpInstruction {
	
	public OP_K() {
		init("K");
		arg("LL", "concatenate lists (modify list 1)");
		arg("LA|AL", "add to list (modify list)");
		arg("AA", "create list [ A A ]");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();

		
		if (a.isa(LIST) && b.isa(LIST)) {
			asList(b).mutAddAll(asList(a));
			block.push(b);
		} else if (a.isa(LIST)){//&& !a.isa(Obj.STR)) {			
			asList(a).mutAddExact(0, b);
			block.push(a);
		} else if (b.isa(LIST)){//&& !b.isa(Obj.STR)) {
			asList(b).mutAdd(a);
			block.push(b);
		} else {
			final ArrayList<Obj> list = new ArrayList<Obj>();
			list.add(b);  //Stack - Add in reverse order
			list.add(a);
			block.push(new List(list));
		}
	}
}

// N - 78
class OP_N extends OpInstruction {
	
	public OP_N() {
		init("N");
		arg("SS|LA", "return index of first occurance, -1 if not found");
		arg("DJ|DS", "contains key");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop(); //Item
		final Obj b = block.peek(); //List
		
		if (a.isa(STR) && b.isa(STR)) {
			block.push(Num.fromInt(b.str().indexOf(a.str())));
		} else if(b.isa(Obj.LIST)) {			
			block.push(Num.fromInt(asList(b).find(a)));
		} else if (b.isa(DICT) && a.isa(STR)) {
			block.push( Num.fromBool(((Dict)b).containsKey(a.str())) );
		} else if (b.isa(DICT) && a.isa(SYMBOL)) {
			block.push( Num.fromBool(((Dict)b).containsKey(((Symbol)a).id())) );
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// O - 79
class OP_O extends OpInstruction {
	
	public OP_O() {
		init("O");
		arg("LB", "Map block to list");
		arg("DB", "Map block to dict");
		setOverload(-1, "each");
	}

	@Override
	public void execute(Block block) {
		final Obj blk_obj = block.pop();
		final Obj container = block.pop();

		Block blk = null;
		try {
			blk = (Block)blk_obj;
		} catch (ClassCastException e) {
			throw new TypeError(this, blk_obj, container);
		}
		
		if (container.isa(Obj.LIST)) {
			block.push(asList(container).map(blk));
		} else if (container.isa(Obj.DICT)) {
			Dict d = (Dict)container;
			if (d.pushSelf()) {
				block.push(blk);
				block.callVariable(d, Ops.KEYVAR_EACH);
			} else {
				block.push(DictIndexing.map((Dict)container, (Block)blk));
			}
		} else {
			throw new TypeError(this, blk_obj, container);
		}
	}
}



// P - 80
class OP_P extends OpInstruction {
	
	public OP_P() {
		init("P");
		arg("A", "to string");
		setOverload(-1, "str");
	}

	@Override
	public void execute (final Block block) {
		block.push(List.fromString(block.pop().str()));
	}
}

// Q - 81
class OP_Q extends OpInstruction {
	
	public OP_Q() {
		init("Q");
		arg("N", "N>0: random number 0-N, N<0: random number N-0, N=0: any int");
		arg("L", "random choice");
		setOverload(1, "random");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();

		if (overload().execute(block, a)) return;
		
		if(a.isa(Obj.NUMBER)) {
			int i = ((Number)a).toInt();
			if (i > 0) {
				block.push(Num.fromInt(Ops.RAND.nextInt(i)));
			} else if (i < 0) {
				block.push(Num.fromInt(-1 * Ops.RAND.nextInt(i*-1)));
			} else {
				block.push(Num.fromInt(Ops.RAND.nextInt()));
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

// R - 82
class OP_R extends OpInstruction {
	
	public OP_R() {
		init("R");
		arg("N|C", "range [1, 2 .. N]");
		arg("L", "len L = 2: range [N1, N1+1, ..., N2], len l = 3: range [N1, N2, ..., N3]");
		setOverload(1, "range");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();

		if (overload().execute(block, a)) return;

		if(a.isa(LIST)) {
			block.push( new List(ListRangeUtils.buildRange(asList(a))) );
		} else if (a.isa(NUMBER)) {
			block.push( new List(ListRangeUtils.buildRange((Number)a)) );
		} else if (a.isa(CHAR)) {
			block.push( new List(ListRangeUtils.buildRange(((Char)a).charValue())) );
		} else {
			throw new TypeError(this, a);
		}
	}
}

// S - 83
class OP_S extends OpInstruction {
	
	public OP_S() {
		init("S");
		arg("L", "sum (fold using +)");
		arg("B", "duplicate block, add locals if they do not exist");
		arg("J", "is defined");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();
		
		if (a.isa(LIST)) {
			
			//Using the new Promoted list, use Str, NumberList, or ObjList
			if (a.isa(STR)) {
				char total = 0;
				char[] chars = a.str().toCharArray();
				for (char c : chars) {
					total += c;
				}
				block.push(Char.valueOf(total));
			} else if(a.isa(NUMBERLIST)) {
				block.push( asNumberList(a).sum() );
			} else {
				// If a normal list, fold using '+'
				List list = asList(a);
				if(list.length() == 0) {
					block.push(Num.ZERO);
					return;
				}
				//Push all but the last item
				for(int i = list.length()-1; i > 0; i--) {
					block.add(Ops.getOp('+'));
					block.add(list.getExact(i));
				}
				//Push the last element outside the loop so that there is not an extra plus (1 1+2+3+)
				block.add(list.getExact(0)); 
			}
		} else if (a.isa(SYMBOL)) {
			block.push(Num.fromBool(Aya.getInstance().getVars().isDefined(((Symbol)a).id())));
			
		} else if (a.isa(BLOCK)) {
			Block b = (Block)a;
			block.push(b.duplicateAddLocals());
		} else {
			throw new TypeError(this, a);
		}
	}
}

// T - 84 
class OP_T extends OpInstruction {
	
	public OP_T() {
		init("T");
		arg("N", "negate");
		vect();
		setOverload(1, "negate");
	}

	@Override
	public void execute (final Block block) {
		Obj a = block.pop();
	
		if (overload().execute(block, a)) return;
		
		if (a.isa(NUMBER)) {
			block.push(((Number)a).negate());
		} else if (a.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(a).negate()) );
		} else {
			throw new TypeError(this, a);
		}
	}
}


//U - 85
class OP_U extends OpInstruction {

	public OP_U() {
		init("U");
		arg("L", "reverse");
		setOverload(1, "reverse");
	}

	@Override
	public void execute(final Block block) {
		Obj o = block.pop();

		if (overload().execute(block, o)) return;
		
		if (o.isa(LIST)) {
			asList(o).mutReverse();
			block.push(o);
		} else {
			throw new TypeError(this,o);
		}
	}
}



// V - 86
class OP_V extends OpInstruction {
	
	public OP_V () {
		init("V");
		arg("N|C", "decrement");
		arg("J", "decrement in place");
		arg("L", "uncons from back");
		setOverload(1, "dec");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (overload().execute(block, a)) return;
		
		if (a.isa(NUMBER)) {
			block.push( ((Number)a).dec() );
		} else if (a.isa(CHAR)) {
			block.push( ((Char)a).dec() );
		} else if (a.isa(SYMBOL)) {
			long varid = ((Symbol)a).id();
			Obj o = Aya.getInstance().getVars().getVar(varid);
			if (o.isa(NUMBER)) {
				Aya.getInstance().getVars().setVar(varid, ((Number)o).dec());
			} else if (o.isa(CHAR)) {
				Aya.getInstance().getVars().setVar(varid, ((Char)o).dec());
			} else {
				throw new AyaRuntimeException("Cannot decrement " + o.repr() 
						+ " in place in call " + a.repr() + " V");
			}
		} else if (a.isa(LIST)) {
			List l = asList(a);
			if (l.length() > 0) {
				Obj popped = l.mutPop();
				block.push(l);
				block.push(popped);
			} else {
				throw new AyaRuntimeException("V: unable to remove element from empty list");
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

// W - 87
class OP_W extends OpInstruction {
	
	public OP_W() {
		init("W");
		arg("B", "while loop (repeat as long as block returns true)");
		arg("D", "export all variables");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		
		if(a.isa(Obj.BLOCK)) {
			Block blk = ((Block)(a)).duplicate();
			Block state = new Block();
			state.setStack((Stack<Obj>)block.getStack().clone());
			
			boolean condition = false;
			
			do {
				state.addAll(blk.getInstructions().getInstrucionList());
				state.eval();
				
				final Obj cond = state.pop();
				
				if (cond.isa(NUMBER)) {
					condition = cond.bool();
				} else {
					throw new TypeError(this, "While condition must be a boolean or a number", cond);
				}
			
			} while (condition);
			
			//Merge the stack
			block.setStack(state.getStack());
			
			return;
		}
		else if(a.isa(DICT)) {
			Dict d = (Dict)a;
			//Aya.getInstance().getVars().peek().merge(d.getVarSet());
			for (Entry<Long, Obj> e : d.getVarSet().getMap().entrySet()) {
				Aya.getInstance().getVars().setVar(e.getKey(), e.getValue());
			}
			return;
		}
		throw new TypeError(this, a);
	}
}


// X - 88
class OP_X extends OpInstruction {
	
	public OP_X() {
		init("X");
		arg("A", "assign to variable x and pop from stack");
	}
	
	private static final long X = SymbolEncoder.encodeString("x");

	@Override
	public void execute (final Block block) {
		Aya.getInstance().getVars().setVar(X, block.pop());
	}
}

// Y - 89
class OP_Y extends OpInstruction {
	
	public OP_Y() {
		init("Y");
		arg("A", "assign to variable y and leave on stack");
	}
	
	private static final long Y = SymbolEncoder.encodeString("y");
	
	@Override
	public void execute (final Block block) {
		Aya.getInstance().getVars().setGlobalVar(Y, block.peek());
	}
}

// Z - 90
class OP_Z extends OpInstruction {
	
	public OP_Z() {
		init("Z");
		arg("N", "cast to bignum");
		arg("S", "parse to bignum");
	}

	@Override
	public void execute (final Block block) {
		Obj a = block.pop();
		
		if (a.isa(Obj.NUMBER)) {
			block.push(new BigNum(((Number)a).toBigDecimal()));
		} else if (a.isa(Obj.STR)) {
			try	{
				block.push(new BigNum(new BigDecimal(a.str())));
			} catch (NumberFormatException e) {
				throw new AyaRuntimeException("Cannot cast " + a.str() + " to bignum");
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

// \ - 92
class OP_Backslash extends OpInstruction {
	
	public OP_Backslash() {
		init("\\");
		arg("AA", "swap top two elements on the stack");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		block.push(a);
		block.push(b);
	}
}

// ^ - 94
class OP_Caret extends OpInstruction {
	
	public OP_Caret() {
		init("^");
		arg("NN", "power");
		arg("SS", "levenshtein distance");
		setOverload(2, "pow");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if (overload().execute(block, a, b)) return;
		
		if(a.isa(NUMBER) && b.isa(NUMBER)){
			//Raise b to the ath power
			block.push(NumberMath.pow(asNumber(b), asNumber(a)));
		} else if (a.isa(STR) && b.isa(STR)) {
			block.push(Num.fromInt( (asStr(a)).levDist(asStr(b)) ));
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( new List(asNumberList(a).powFrom((Number)b)) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(b).pow((Number)a)) );
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(b).pow(asNumberList(a))) );
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// | - 124
class OP_Bar extends OpInstruction {
	
	public OP_Bar() {
		init("|");
		arg("NN", "logical or");
		arg("SS", "split S1 using regex S2");
		arg("SC", "split str using char");
		arg("LN", "cut L at index N");
		setOverload(2,  "or");
	}
	
	private static List strsToList(String[] strs) {
		ArrayList<Obj> out = new ArrayList<Obj>(strs.length);
		for (String s : strs) out.add(List.fromString(s));
		return new List(out);
	}
	private static List strsToList(ArrayList<Str> strs) {
		ArrayList<Obj> out = new ArrayList<Obj>(strs.size());
		for (Str s : strs) out.add(new List(s));
		return new List(out);
	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();

		if (overload().execute(block, a, b)) return;
		
		//Bitwise or
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( NumberMath.bor((Number)a, (Number)b) );
		} else if (a.isa(STR) && b.isa(STR)) {
			block.push(strsToList(b.str().split(a.str())));
		} else if (a.isa(CHAR) && b.isa(STR)) {
			block.push(strsToList(asStr(b).splitAtChar(asChar(a).charValue())));
		} else if (a.isa(Obj.NUMBER) && b.isa(Obj.LIST)) {
			Pair<List, List> lists = asList(b).splitAtIndexed(asNumber(a).toInt());
			block.push(lists.first());
			block.push(lists.second());
		} else {
			throw new TypeError(this, a, b);
		}
	}
	
}


// ~ - 126
class OP_Tilde extends OpInstruction {
	
	public OP_Tilde() {
		init("~");
		arg("B|S|C", "evaluate");
		arg("L", "dump to stack");
		arg("D", "set variables if they exist in the local scope");
	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		
		if(a.isa(BLOCK)) {
			block.addAll(((Block)(a)).getInstructions().getInstrucionList());
		} else if (a.isa(STR)) {
			block.addAll(Parser.compile(a.str(), Aya.getInstance()).getInstructions().getInstrucionList());
		} else if (a.isa(CHAR)) {
			final char c = ((Char)a).charValue();
			if (c >= '0' && c <= '9') {
				block.add(Num.BYTES[c-'0']);
			} else {
				final String varname = CharacterParser.getName(c);
				if(varname == null) {
					throw new AyaRuntimeException("Character '" + c + " is not a valid variable");
				}
				block.add(new GetVariableInstruction(SymbolEncoder.encodeString(varname)));
			}
		} else if (a.isa(LIST)) {
			List list = asList(a);
			//Collections.reverse(list);
			for (int i = list.length()-1; i >= 0; i--) {
				block.add(list.getExact(i));
			}
		} else if (a.isa(SYMBOL)) {
			block.push(Aya.getInstance().getVars().getVar( ((Symbol)a).id() ));
		} else if (a.isa(DICT)) {
			// Dump all vars if they exist in the most local scope
			Aya.getInstance().getVars().peek().mergeDefined(asDict(a).getVarSet());
		} else {
			throw new TypeError(this, a);
		}
	}
}


