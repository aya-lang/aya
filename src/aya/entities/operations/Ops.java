package aya.entities.operations;

import static aya.obj.Obj.BLOCK;
import static aya.obj.Obj.CHAR;
import static aya.obj.Obj.DICT;
import static aya.obj.Obj.LIST;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.NUMBERLIST;
import static aya.obj.Obj.STR;
import static aya.obj.Obj.STRLIST;
import static aya.obj.Obj.SYMBOL;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apfloat.Apfloat;

import aya.Aya;
import aya.AyaPrefs;
import aya.OperationDocs;
import aya.StreamMgr;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.TypeError;
import aya.instruction.op.OpInstruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.list.GenericList;
import aya.obj.list.List;
import aya.obj.list.ListRangeUtils;
import aya.obj.list.Str;
import aya.obj.list.StrList;
import aya.obj.list.numberlist.NumberItemList;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.BigNum;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.NumberMath;
import aya.obj.symbol.Symbol;
import aya.parser.CharacterParser;
import aya.parser.Parser;
import aya.util.FileUtils;
import aya.variable.Variable;

public class Ops {
	
	public static final Random RAND = new Random((new Date()).getTime());
	public static final Pattern PATTERN_URL = Pattern.compile("http:\\/\\/.*|https:\\/\\/.*");

	
	////////////////////////
	// OPERATOR OVERLOADS //
	////////////////////////
	
	// NUMERIC - Binary
	// mod
	public static final Variable KEYVAR_MOD	= new Variable("__mod__");
	public static final Variable KEYVAR_RMOD	= new Variable("__rmod__");
	// mul
	public static final Variable KEYVAR_MUL	= new Variable("__mul__");
	public static final Variable KEYVAR_RMUL	= new Variable("__rmul__");
	// add
	public static final Variable KEYVAR_ADD	= new Variable("__add__");
	public static final Variable KEYVAR_RADD	= new Variable("__radd__");
	// sub
	public static final Variable KEYVAR_SUB	= new Variable("__sub__");
	public static final Variable KEYVAR_RSUB	= new Variable("__rsub__");
	// div 
	public static final Variable KEYVAR_DIV	= new Variable("__div__");
	public static final Variable KEYVAR_RDIV	= new Variable("__rdiv__");
	// idiv
	public static final Variable KEYVAR_IDIV	= new Variable("__idiv__");
	public static final Variable KEYVAR_RIDIV= new Variable("__ridiv__");
	// pow
	public static final Variable KEYVAR_POW	= new Variable("__pow__");
	public static final Variable KEYVAR_RPOW	= new Variable("__rpow__");
	
	// NUMERIC - Comparison
	public static final Variable KEYVAR_LT = new Variable("__lt__");
	public static final Variable KEYVAR_GT = new Variable("__gt__");
	public static final Variable KEYVAR_LEQ = new Variable("__leq__");
	public static final Variable KEYVAR_GEQ = new Variable("__geq__");
	public static final Variable KEYVAR_EQ	= new Variable("__eq__");
	
	// Numeric - Monads
	public static final Variable KEYVAR_NEGATE = new Variable("__negate__");
	public static final Variable KEYVAR_CEIL = new Variable("__ceil__");
	public static final Variable KEYVAR_FLOOR = new Variable("__floor__");
	public static final Variable KEYVAR_ABS = new Variable("__abs__");
	public static final Variable KEYVAR_SIGNUM = new Variable("__signum__");
	public static final Variable KEYVAR_INC = new Variable("__inc__");
	public static final Variable KEYVAR_DEC = new Variable("__dec__");
	
	// Numeric - Math
	public static final Variable KEYVAR_SIN = new Variable("__sin__");
	public static final Variable KEYVAR_ASIN = new Variable("__asin__");
	public static final Variable KEYVAR_COS = new Variable("__cos__");
	public static final Variable KEYVAR_ACOS = new Variable("__acos__");
	public static final Variable KEYVAR_TAN = new Variable("__tan__");
	public static final Variable KEYVAR_ATAN = new Variable("__atan__");
	public static final Variable KEYVAR_LN = new Variable("__ln__");
	public static final Variable KEYVAR_LOG = new Variable("__log__");
	public static final Variable KEYVAR_EXP = new Variable("__exp__");
	public static final Variable KEYVAR_FACT = new Variable("__fact__");
	public static final Variable KEYVAR_SQRT = new Variable("__sqrt__");
	
	// List
	public static final Variable KEYVAR_GETINDEX = new Variable("__getindex__");
	public static final Variable KEYVAR_SETINDEX = new Variable("__setindex__");
	public static final Variable KEYVAR_HEAD = new Variable("__head__");
	public static final Variable KEYVAR_TAIL = new Variable("__tail__");
	public static final Variable KEYVAR_MAP = new Variable("__map__");
	public static final Variable KEYVAR_LEN = new Variable("__len__");
	public static final Variable KEYVAR_REVERSE = new Variable("__reverse__");
	public static final Variable KEYVAR_SORT = new Variable("__sort__");

	
	// Misc.
	public static final Variable KEYVAR_AND		= new Variable("__and__");
	public static final Variable KEYVAR_RAND		= new Variable("__rand__");
	public static final Variable KEYVAR_OR		= new Variable("__or__");
	public static final Variable KEYVAR_ROR		= new Variable("__ror__");
	public static final Variable KEYVAR_RANDOM 	= new Variable("__random__");
	public static final Variable KEYVAR_RANGE 	= new Variable("__range__");
	public static final Variable KEYVAR_NEW 		= new Variable("__new__");
	public static final Variable KEYVAR_FLOAT 	= new Variable("__float__");
	public static final Variable KEYVAR_EACH 	= new Variable("__each__");
	public static final Variable KEYVAR_STR 		= new Variable("__str__");
	public static final Variable KEYVAR_REPR 	= new Variable("__repr__");
	
	
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
	
//	/** Returns a list of all the op descriptions **/
//	public static ArrayList<String> getAllOpDescriptions() {
//		ArrayList<String> out = new ArrayList<String>();
//		for (char i = 0; i <= 96-FIRST_OP; i++) {
//			if(OPS[i] != null) {
//				out.add(OPS[i].getDocStr() + "\n(operator)");
//			}
//		}
//		out.add(TILDE.getDocStr() + "\n(operator)" );
//		out.add(BAR.getDocStr() + "\n(operator)" );
//		out.add(APPLY_TO.getDocStr() + "\n(operator)" );
//
//		return out;
//	}

	
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
	
	static {
		OpDoc doc = new OpDoc(' ', "!");
		doc.desc("N", "1-N (logical not, complementary probability)");
		doc.desc("C", "swap case");
		doc.ovrld(Ops.KEYVAR_NEW.name());
		doc.vect();
		OperationDocs.add(doc);
	}
	
	public OP_Bang() {
		this.name = "!";
	}
	@Override public void execute(final Block block) {
		Obj o = block.pop();
		
		if (o.isa(Obj.NUMBER)) {
			block.push(Num.ONE.sub((Number)o));
		} else if (o.isa(Obj.STR)) {
			//((Str)o).reverse();
			//block.push(o);
			block.push( ((Str)o).swapCase() );
		} else if(o.isa(Obj.NUMBERLIST)) {
			//((List)o).reverse();
			//block.push(o);
			block.push( ((NumberList)o).subFrom(Num.ONE) );
		}
		else if (o.isa(Obj.CHAR)) {
			block.push(((Char)o).swapCase());
		}
		
		else if (o.isa(DICT)) {
			Dict d = (Dict)o;
			if (d.containsKey(Ops.KEYVAR_NEW)) {
				block.push(o);
				block.addOrDumpVar(d.get(Ops.KEYVAR_NEW));
			} else {
				//Create a new empty dict with the input as its metatable
				//block.push(new Dict((Dict)o));
				throw new AyaRuntimeException("! : keyword __new__ not found");
			}

		}
		else {
			throw new TypeError(this,o);
		}
		return;
	}
}

//# - 35
class OP_Pound extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "#");
		doc.desc("LA#A", "map");
		OperationDocs.add(doc);
	}
	
	public OP_Pound() {
		this.name = "#";
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
				
				if(block.stackEmpty()) {
					throw new AyaRuntimeException("Could not find list to map to\n"
							+ "\t in " + block.toString() + "\n"
							+ "\t map using " + map);
				} else {
					popped = block.pop();
				}
			}
			
			block.push(map.mapTo((List)popped));
			
			return;
		} 	
		throw new TypeError(this,a);
	}
}

// $ - 36
class OP_Duplicate extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "$");
		doc.desc("A", "deepcopy (duplicate)");
		OperationDocs.add(doc);
	}
	
	public OP_Duplicate() {
		this.name = "$";
	}
	@Override public void execute (final Block block) {
		block.push(block.peek().deepcopy());
	}
}

// % - 37
class OP_Percent extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "%");
		doc.desc("NN", "mod");
		doc.desc("BN", "repeat B N times");
		doc.ovrld(Ops.KEYVAR_MOD.name(), Ops.KEYVAR_RMOD.name());
		OperationDocs.add(doc);
	}
	
	public OP_Percent() {
		this.name = "%";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			try {
				//b mod a
				block.push(((Number)(b)).mod((Number)(a)));
			} catch (ArithmeticException e) {
				throw new AyaRuntimeException("%: Divide by 0");
			}
		}
		else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( ((NumberList)a).modFrom((Number)b) );
		}
		
		else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).mod((Number)a) );
		}
		else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).mod((NumberList)a) );
		}
		else if (a.isa(NUMBER) && b.isa(BLOCK)) {
			int repeats = ((Number)(a)).toInt();
			Block blk = ((Block)b);
			for (int i = 0; i < repeats; i ++) {
				block.addAll(blk.getInstructions().getInstrucionList());
			}
			return;
		} 
		else if (a.isa(DICT)) {
			block.push(b);
			block.callVariable((Dict)a, Ops.KEYVAR_MOD);
		} else if (b.isa(DICT)) {
			block.callVariable((Dict)b, Ops.KEYVAR_RMOD, a);
		}
		else {
			throw new TypeError(this, a,b);
		}
	}
}



// & - 38
class OP_And extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "&");
		doc.desc("NN", "bitwise and");
		doc.desc("SS", "list all expressions matching the regex");
		doc.ovrld(Ops.KEYVAR_AND.name(),  Ops.KEYVAR_RAND.name());
		OperationDocs.add(doc);
	}
	
	public OP_And() {
		this.name = "&";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( NumberMath.band((Number)a, (Number)b) );
		} 
		else if (a.isa(SYMBOL)) {
			long varid = ((Symbol)a).id();
			Aya.getInstance().getVars().setVar(varid, b);
			block.push(b);
		}
		else if (a.isa(Obj.STR) && b.isa(Obj.STR)) {
			ArrayList<Obj> allMatches = new ArrayList<Obj>();
			Matcher m = Pattern.compile(a.str()).matcher(b.str());
			while (m.find()) {
				 allMatches.add(new Str(m.group()));
			}
			block.push(new GenericList(allMatches));
		}
		else if (a.isa(DICT)) {
			block.push(b);
			block.callVariable((Dict)a, Ops.KEYVAR_AND);
		} else if (b.isa(DICT)) {
			block.callVariable((Dict)b, Ops.KEYVAR_AND, a);
		}
		else {
			throw new TypeError(this, a);
		}

	}
}

// * - 42
class OP_Times extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "*");
		doc.desc("NN", "multiply");
		doc.desc("LS|LC", "join");
		doc.vect();
		doc.ovrld(Ops.KEYVAR_MUL.name(), Ops.KEYVAR_RMUL.name());
		OperationDocs.add(doc);
	}
	
	public OP_Times() {
		this.name = "*";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( ((Number)a).mul((Number)b) );
		}
		else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( ((NumberList)a).mul((Number)b) );
		}
		
		else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).mul((Number)a) );
		}
		else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)a).mul((NumberList)b) );
		}
		else if ((a.isa(STR) || a.isa(CHAR)) && b.isa(LIST)) {
			StringBuilder sb = new StringBuilder();
			List la = (List)b;
			String sep = a.str();
			for (int i = 0; i < la.length()-1; i++) {
				sb.append(la.get(i).str());
				sb.append(sep);
			}
			sb.append(la.get(-1).str());
			block.push(new Str(sb.toString()));
		}
		else if (a.isa(DICT)) {
			block.push(b);
			block.callVariable((Dict)a, Ops.KEYVAR_MUL);
		} else if (b.isa(DICT)) {
			block.callVariable((Dict)b, Ops.KEYVAR_RMUL, a);
		}
		else {	
			throw new TypeError(this, a,b);
		}
	}
}


// + - 43
class OP_Plus extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "+");
		doc.desc("NN|CC", "add");
		doc.desc("SA|AS", "append string");
		doc.vect();
		doc.ovrld(Ops.KEYVAR_ADD.name(), Ops.KEYVAR_RADD.name());
		OperationDocs.add(doc);
	}
	
	public OP_Plus() {
		this.name = "+";
	}
	@Override public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( ((Number)a).add((Number)b) );
		}
		else if (a.isa(DICT)) {
			block.push(b);
			block.callVariable((Dict)a, Ops.KEYVAR_ADD);
		} else if (b.isa(DICT)) {
			block.callVariable((Dict)b, Ops.KEYVAR_RADD, a);
		}
		
		else if (a.isa(STR) || b.isa(STR)) {
			//Must reverse order
			block.push(new Str(b.str() + a.str()));
		} 
		
		else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( ((NumberList)a).add((Number)b) );
		}
		
		else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).add((Number)a) );
		}
		else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)a).add((NumberList)b) );
		}
		
		
		else if (a.isa(Obj.NUMBER) && b.isa(CHAR)) {
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
	
	static {
		OpDoc doc = new OpDoc(' ', "-");
		doc.desc("NN|CC", "subtract");
		doc.vect();
		doc.ovrld(Ops.KEYVAR_SUB.name(), Ops.KEYVAR_RSUB.name());
		OperationDocs.add(doc);
	}
	
	public OP_Minus() {
		this.name = "-";
	}
	@Override public void execute(final Block block) {
		final Obj b = block.pop();	//Pop in reverse order
		final Obj a = block.pop();

		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( ((Number)a).sub((Number)b) );
		} else if (a.isa(NUMBER) && b.isa(CHAR)) {
			block.push(((Char)b).subFrom((Number)a));
		} else if (a.isa(CHAR) && b.isa(Obj.NUMBER)) {
			block.push( ((Char)a).sub((Number)b) );
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			block.push( ((Char)a).sub((Char)b) );
		}
		
		else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( ((NumberList)a).sub((Number)b) );
		}
		
		else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).subFrom((Number)a) );
		}
		else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)a).sub((NumberList)b) );
		}
		else if (b.isa(DICT)) {
			block.push(a);
			block.callVariable((Dict)b, Ops.KEYVAR_SUB);
		}
		else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_RSUB, b);
		}
		else {
			throw new TypeError(this, a,b);
		}
	}
}

// / - 47
class OP_Divide extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "/");
		doc.desc("NN", "divide");
		doc.vect();
		doc.ovrld(Ops.KEYVAR_DIV.name(), Ops.KEYVAR_RDIV.name());
		OperationDocs.add(doc);
	}
	
	public OP_Divide() {
		this.name = "/";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			//b div a
			block.push( ((Number)b).div((Number)a) );
		}
		else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( ((NumberList)a).divFrom((Number)b) );
		}
		
		else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).div((Number)a) );
		}
		else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).div((NumberList)a) );
		}
		else if (a.isa(DICT)) {
			block.push(b);
			block.callVariable((Dict)a, Ops.KEYVAR_DIV);
		} else if (b.isa(DICT)) {
			block.callVariable((Dict)b, Ops.KEYVAR_RDIV, a);
		}
	
		else {
			throw new TypeError(this, a,b);
		}
	}
}


// ; - 59
class OP_SemiColon extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', ";");
		doc.desc("A", "pop and discard");
		OperationDocs.add(doc);
	}
	
	public OP_SemiColon() {
		this.name = ";";
	}
	@Override public void execute (final Block block) {
		block.pop();
	}
}


// < - 60
class OP_LessThan extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "<");
		doc.desc("NN|CC|SS", "less than");
		doc.ovrld(Ops.KEYVAR_LT.name());
		OperationDocs.add(doc);
	}
	
	public OP_LessThan() {
		this.name = "<";
	}
	@Override
	public void execute(final Block block) {
		final Obj b = block.pop();			// Popped in Reverse Order
		final Obj a = block.pop();
		
		
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( new Num(((Number)a).compareTo((Number)b) < 0) );
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			block.push( new Num(((Char)a).compareTo((Char)b) < 0) );
		} else if (a.isa(STR) && b.isa(STR)) {
			block.push( new Num(a.str().compareTo(b.str()) < 0) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).gt((Number)a) ); // gt is opposite of lt
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER) ) {
			block.push( ((NumberList)a).lt((Number)b) ); 
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST) ) {
			block.push( ((NumberList)a).lt((NumberList)b) ); 
		} 
		
		else if (b.isa(DICT)) {
			block.push(a);
			block.callVariable((Dict)b, Ops.KEYVAR_LT);
		} 
		
		
		else {
			throw new TypeError(this, a,b);
		}
	}
}


// = - 61
class OP_Equal extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "=");
		doc.desc("AA", "equality");
		doc.ovrld(Ops.KEYVAR_EQ.name());
		OperationDocs.add(doc);
	}
	
	public OP_Equal() {
		this.name = "=";
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
			block.push(new Num(a.equiv(b)));
		}
	}
}

// > - 62
class OP_GreaterThan extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', ">");
		doc.desc("NN|CC|SS", "greater than");
		doc.vect();
		doc.ovrld(Ops.KEYVAR_GT.name());
		OperationDocs.add(doc);
	}
	
	public OP_GreaterThan() {
		this.name = ">";
	}
	@Override
	public void execute(final Block block) {
		final Obj b = block.pop();			// Popped in Reverse Order
		final Obj a = block.pop();
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( new Num(((Number)a).compareTo((Number)b) > 0) );
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			block.push( new Num(((Char)a).compareTo((Char)b) > 0) );
		} else if (a.isa(STR) && b.isa(STR)) {
			block.push( new Num(a.str().compareTo(b.str()) > 0) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).lt((Number)a) ); // lt is opposite of gt
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER) ) {
			block.push( ((NumberList)a).gt((Number)b) ); 
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST) ) {
			block.push( ((NumberList)a).gt((NumberList)b) ); 
		} 
		
		else if (b.isa(DICT)) {
			block.push(a);
			block.callVariable((Dict)b, Ops.KEYVAR_GT);
		} 
		
		else {
			throw new TypeError(this, a,b);
		}
	}
}

// ? - 63
class OP_Conditional extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "?");
		doc.desc("AA", "if A1, then A2. If A2 is block, execute it");
		OperationDocs.add(doc);
	}
	
	public OP_Conditional() {
		this.name = "?";
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
		
		//throw new TypeError(this, a,b,c);
	}
}


// @ - 64
class OP_At extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "@");
		doc.desc("AAA", "rotates the top three elements on the stack [abc->bca]");
		OperationDocs.add(doc);
	}
	
	public OP_At() {
		this.name = "@";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		Obj c = block.pop();
		
		block.push(b);
		block.push(a);
		block.push(c);
	}
}

// A - 65
class OP_A extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "A");
		doc.desc("A", "wrap in list");
		OperationDocs.add(doc);
	}
	
	public OP_A() {
		this.name = "A";
	}
	@Override public void execute (final Block block) {
		final ArrayList<Obj> al = new ArrayList<Obj>();
		al.add(block.pop());
		block.push(new GenericList(al));
	}
}

// B - 66
class OP_B extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "B");
		doc.desc("N|C", "increment");
		doc.desc("J", "increment in place");
		doc.desc("L", "uncons from front");
		doc.ovrld(Ops.KEYVAR_INC.name());
		OperationDocs.add(doc);
	}
	
	public OP_B() {
		this.name = "B";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
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
			List l = (List)a;
			Obj popped = l.popBack();
			block.push(l);
			block.push(popped);
		} 
		
		else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_INC);
		} 
		
		else {
			throw new TypeError(this, a);
		}
	}
}

// C - 67
class OP_Sort extends OpInstruction {
	static {
		OpDoc doc = new OpDoc(' ', "C");
		doc.desc("S|L", "sort least to greatest");
		doc.desc("N", "bitwise not");
		doc.ovrld(Ops.KEYVAR_SORT.name());
		OperationDocs.add(doc);
	}
	
	public OP_Sort() {
		this.name = "C";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		
		if (a.isa(Obj.NUMBER)) {
			block.push( NumberMath.bnot((Number)a) );
		}
		else if(a.isa(LIST)) {
			((List)a).sort();
			block.push(a);
		} else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_SORT);
		} else {
			throw new TypeError(this,a);
		}		
	}
}



// D - 68
class OP_D extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "D");
		doc.desc("ALN", "set index");
		doc.ovrld(Ops.KEYVAR_SETINDEX.name());
		OperationDocs.add(doc);
	}
	
	public OP_D() {
		this.name = "D";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();  	//Index
		final Obj b = block.pop();		//List
		final Obj o = block.pop();		//Item

		if (b.isa(DICT)) {
			block.push(o);
			block.callVariable((Dict)b, Ops.KEYVAR_SETINDEX, a);
		}
		else if (b.isa(LIST)) {
			List.setIndex((List)b, a, o);
			block.push(b);
		}
		else {		
			throw new TypeError(this, a, b, o);
		}
		
	}

}

// E - 69
class OP_E extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "E");
		doc.desc("N", "10^N");
		doc.desc("L|S", "length");
		doc.ovrld(Ops.KEYVAR_LEN.name());
		OperationDocs.add(doc);
	}
	
	public OP_E() {
		this.name = "E";
	}
	@Override public void execute (final Block block) {
		Obj n = block.pop();
		if (n.isa(NUMBER)) {
			block.push( new Num(10).pow((Number)n) );
		} else if (n.isa(LIST)) {
			block.push( new Num(((List)n).length()) );
		} 
		else if (n.isa(DICT)) {
			block.callVariable((Dict)n, Ops.KEYVAR_LEN);
		} 
		else {
			throw new TypeError(this, n);
		}
	}
}

// F - 70
class OP_F extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "F");
		doc.desc("LB", "fold from right to left");
		doc.desc("NN", "unsigned right bitshift");
		OperationDocs.add(doc);
	}
	
	public OP_F() {
		this.name = "F";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( NumberMath.unsignedRightShift((Number)b, (Number)a) );
		} else if(a.isa(BLOCK) && b.isa(LIST)) {
			List blist = (List)b;
			
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
				block.add(blist.get(i));
			}
			//Push the last element outside the loop so that there is not an extra plus (1 1+2+3+)
			//block.add(list.get(list.size()-1));
			block.add(blist.get(0));
			return;
		} else {
			throw new TypeError(this, a, b);
		}
	}
}


// G - 71
class OP_G extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "G");
		doc.desc("S", "read a string from a filename or URL");
		doc.desc("N", "isprime");
		OperationDocs.add(doc);
	}
	
	public OP_G() {
		this.name = "G";
	}
	@Override public void execute (final Block block) {
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
					block.push(new Str(sb.toString()));
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
					block.push( new Str(FileUtils.readAllText(path)) );
				} catch (IOException e) {
					throw new AyaRuntimeException("Cannot open file: " + new File(path).getAbsolutePath());
				} 
			}
			return;
		} else if (a.isa(NUMBER)) {
			block.push( new Num(((Number)a).isPrime()) );
		} else {
			throw new TypeError(this, a);
		}
	}
}

class OP_H extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "H");
		doc.desc("SNN|LNN|NNN", "convert base of N|S|L from N1 to N2");
		OperationDocs.add(doc);
	}
	
	public OP_H() {
		this.name = "H";
	}
	@Override public void execute (final Block block) {
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
					out_bi = ((Number)num).toApfloat().floor().toBigInteger();
				} 
				
				//Assume base 2
				else if (num.isa(NUMBERLIST)) {
					if (from_base == 2) {
						NumberList bin_list = ((NumberList)num);
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
						NumberList nums = ((NumberList)num);
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
						block.push(new BigNum(new Apfloat(new BigDecimal(out_bi))));
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
					block.push(new NumberItemList(out_list));
					return;
				} else if (to_base == 0) {
					// Special case: byte list
					byte[] bytes = out_bi.toByteArray();
					ArrayList<Number> nums = new ArrayList<>(bytes.length);
					for (byte b : bytes) {
						if (b >= 0) {
							nums.add(Num.BYTES[b]);
						} else {
							nums.add(new Num(b));
						}
					}
					block.push(new NumberItemList(nums));
					return;
				} else {
					block.push(new Str(out_bi.toString(to_base)));
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
//NOTE: If updating this operator, also update .I
class OP_GetIndex extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "I");
		doc.desc("LL|LN", "get index");
		doc.desc("LB", "filter");
		doc.ovrld(Ops.KEYVAR_GETINDEX.name());
		OperationDocs.add(doc);
	}
	
	public OP_GetIndex() {
		this.name = "I";
	}
	@Override public void execute (final Block block) {
		Obj index = block.pop();
		final Obj list = block.pop();
				
		if(list.isa(LIST)) {		
			block.push(List.getIndex((List)list, index));
		}else if (list.isa(DICT)) {
			if (index.isa(LIST) && !index.isa(STR)) {
				List l = (List)index;
				if (l.length() == 1)
					index = l.get(0);
			}
			
			if ( ((Dict)list).hasMetaKey(Ops.KEYVAR_GETINDEX) ) {
				block.push(index);
				block.callVariable((Dict)list, Ops.KEYVAR_GETINDEX);
			} else {
				block.push(Dict.getIndex((Dict)list, index));
			}
		}
		else {
			throw new TypeError(this, index, list);
		}
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
			List l = (List)index;
			if (l.length() == 1)
				index = l.get(0);
		}
		
		// If it is a list, run the standard setindex method
		if(list.isa(LIST)) {		
			List.setIndex((List)list, index, item);
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
	}
}


// J - 74
class OP_Join extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "J");
		doc.desc("LL", "join lists");
		doc.desc("LA|AL", "add to list");
		doc.desc("AA", "create list [ A A ]");
		OperationDocs.add(doc);
	}
	
	public OP_Join() {
		this.name = "J";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		final boolean a_is_list = a.isa(LIST) && !a.isa(STR);
		final boolean b_is_list = b.isa(LIST) && !b.isa(STR);
		
		if (a_is_list && b_is_list) {
			block.push(List.joinLists((List)b, (List)a));
		} else if (a_is_list) {
			block.push(List.joinFront(b, (List)a));
		} else if (b_is_list) {
			block.push(List.joinBack((List)b, a));
		} else {
			final ArrayList<Obj> list = new ArrayList<Obj>();
			list.add(b);  //Stack - Add in reverse order
			list.add(a);
			block.push(new GenericList(list).promote());
		}
	}
}


// L - 76
class OP_L extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "L");
		doc.desc("AN", "create list by repeating A N times");
		doc.desc("NL|LL", "reshape");
		OperationDocs.add(doc);
	}
	
	public OP_L() {
		this.name = "L";
	}
	@Override public void execute (final Block block) {
		Obj n = block.pop();
		Obj item = block.pop();
		if(n.isa(NUMBER)) {
			int repeats = ((Number)n).toInt();
			if(repeats < 0) {
				throw new AyaRuntimeException("Cannot create list with negative number of elements");
			}
			if (item.isa(CHAR)) {
				block.push( new Str( ((Char)item).charValue(), repeats) );
			} else if (item.isa(NUMBER)) {
				block.push( new NumberItemList((Number)item, repeats) );
			} else if (item.isa(STR)) {
				block.push( new StrList((Str)item, repeats) );
			} else {
				block.push( new GenericList(item, repeats) );
			}

		} 
		
		else if (n.isa(NUMBERLIST)) {
			if (item.isa(LIST)) {
				block.push( List.reshape((List)item, (NumberList)n) );
			} else {
				List l = new GenericList(item, 1).promote();
				block.push( List.reshape(l, (NumberList)n) );
			}
		}
		
		else if (n.isa(NUMBERLIST) && item.isa(LIST)) {
			block.push( List.reshape((List)item, (NumberList)n) );
		}
		
		else {
			throw new TypeError(this, n , item);
		}
	}
}

// K - 76
class OP_K extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "K");
		doc.desc("LL", "concatenate lists (modify list 1)");
		doc.desc("LA|AL", "add to list (modify list)");
		doc.desc("AA", "create list [ A A ]");
		OperationDocs.add(doc);
	}
	
	public OP_K() {
		this.name = "K";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();

		
		if (a.isa(LIST) && b.isa(LIST)) {
			((List)b).addAll((List)a);
			block.push(b);
			
		} else if (a.isa(LIST)){//&& !a.isa(Obj.STR)) {			
			((List)a).addItem(0, b);
			block.push(a);
			
		} else if (b.isa(LIST)){//&& !b.isa(Obj.STR)) {
			
			((List)b).addItem(a);
			block.push(b);
			
		} else {
			
			final ArrayList<Obj> list = new ArrayList<Obj>();
			list.add(b);  //Stack - Add in reverse order
			list.add(a);
			block.push(new GenericList(list).promote());
		}
	}
}

// N - 78
class OP_N extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "N");
		doc.desc("SS|LA", "return index of first occurance, -1 if not found");
		doc.desc("DJ|DS", "contains key");
		OperationDocs.add(doc);
	}
	
	public OP_N() {
		this.name = "N";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop(); //Item
		final Obj b = block.peek(); //List
		
		if (a.isa(STR) && b.isa(STR)) {
			block.push(new Num(b.str().indexOf(a.str())));
		} else if(b.isa(Obj.LIST)) {			
			List l = (List)b;
			block.push(new Num(l.find(a)));
		}
		else if (b.isa(DICT) && a.isa(STR)) {
			block.push( new Num(((Dict)b).containsKey(a.str())) );
		}
		else if (b.isa(DICT) && a.isa(SYMBOL)) {
			block.push( new Num(((Dict)b).containsKey(((Symbol)a).id())) );
		}
		else {
			throw new TypeError(this, a, b);
		}
	}
}

// O - 79
class OP_O extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "O");
		doc.desc("NC", "stream operations: l:readline, b:readchar, a:readall, c:close, f:flush, i:info");
		doc.desc("SC", "open/close stream: w:write, r:read");
		doc.desc("SN", "write to stream");
		OperationDocs.add(doc);
	}
	
	public OP_O() {
		this.name = "O";
	}
	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		
		if (a.isa(CHAR) && b.isa(NUMBER)) {
			char c = ((Char)a).charValue();
			int i  = ((Num)b).toInt();
			
			switch (c) {
			case 'l':
				// Push 0 if invalid
				String line = StreamMgr.readline(i);
				if (line == null) {
					block.push(Num.ZERO);
				} else {
					block.push(new Str(line));
				}
				break;
			case 'b':
				// Since 0 is a valid byte, push -1 if invalid
				block.push(new Num(StreamMgr.read(i)));
				break;
			case 'a':
				// Pushes 0 if invalid
				String all = StreamMgr.readAll(i);
				if (all == null) {
					block.push(Num.ZERO);
				} else {
					block.push(new Str(all));
				}
				break;
			case 'c':
				// Close the file
				block.push(StreamMgr.close(i) ? Num.ONE : Num.ZERO);
				break;
			case 'f':
				// Flush
				block.push(StreamMgr.flush(i) ? Num.ONE : Num.ZERO);
				break;
			case 'i':
				// Info 0:does not exist, 1:input, 2:output
				block.push(new Num(StreamMgr.info(i)));
				break;
			default:
				throw new AyaRuntimeException("Invalid char for operator 'O': " + c);
			}
			
		} else if (a.isa(NUMBER)) {
			int i = ((Num)a).toInt();
			block.push(StreamMgr.print(i, b.str()) ? Num.ONE : Num.ZERO);
		} else if (a.isa(CHAR)) {
			char c = ((Char)a).charValue();
			String filename = b.str();
			block.push(new Num(StreamMgr.open(filename, c+"")));
		} else {
			throw new TypeError(this, a, b);
		}
	}
}



// P - 80
class OP_P extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "P");
		doc.desc("A", "to string");
		doc.ovrld(Ops.KEYVAR_STR.name());
		OperationDocs.add(doc);
	}
	
	public OP_P() {
		this.name = "P";
	}
	@Override public void execute (final Block block) {
		block.push(new Str(block.pop().str()));
	}
}

// Q - 81
class OP_Q extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "Q");
		doc.desc("N", "N>0: random number 0-N, N<0: random number N-0, N=0: any int");
		doc.desc("L", "random choice");
		doc.ovrld(Ops.KEYVAR_RANDOM.name());
		OperationDocs.add(doc);
	}
	
	public OP_Q() {
		this.name = "Q";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		
		if(a.isa(Obj.NUMBER)) {
			int i = ((Number)a).toInt();
			if (i > 0) {
				block.push(new Num(Ops.RAND.nextInt(i)));
			} else if (i < 0) {
				block.push(new Num(-1 * Ops.RAND.nextInt(i*-1)));
			} else {
				block.push(new Num(Ops.RAND.nextInt()));
			}
		}
		else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_RANDOM);
		} else if (a.isa(LIST)) {
			List l = (List)a;
			int ix = Ops.RAND.nextInt(l.length());
			block.push(l.get(ix));
		}
		else {
			throw new TypeError(this, a);
		}
	}
}

// R - 82
class OP_R extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "R");
		doc.desc("N|C", "range [1, 2 .. N]");
		doc.desc("L", "len L = 2: range [N1, N1+1, ..., N2], len l = 3: range [N1, N2, ..., N3]");
		doc.ovrld(Ops.KEYVAR_RANGE.name());
		OperationDocs.add(doc);
	}
	
	public OP_R() {
		this.name = "R";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		if(a.isa(LIST)) {
			block.push( ListRangeUtils.buildRange((List)a) );
		} else if (a.isa(NUMBER)) {
			block.push(ListRangeUtils.buildRange((Number)a));
		} else if (a.isa(CHAR)) {
			block.push(ListRangeUtils.buildRange(((Char)a).charValue()));
		} else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_RANGE);
		} else {
			throw new TypeError(this, a);
		}
	}
}

// S - 83
class OP_S extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "S");
		doc.desc("L", "sum (fold using +)");
		OperationDocs.add(doc);
	}
	
	public OP_S() {
		this.name = "S";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		
		if (a.isa(LIST)) {
			
			// Attempt to promote the list
			List l = (List)a;
			if (l.isa(Obj.OBJLIST)) {
				l = ((GenericList)l).promote();
			}
			
			//Using the new Promoted list, use Str, NumberList, or ObjList
			if (l.isa(STR)) {
				char total = 0;
				char[] chars = ((Str)l).str().toCharArray();
				for (char c : chars) {
					total += c;
				}
				block.push(Char.valueOf(total));
			}
			else if(l.isa(NUMBERLIST)) {
				block.push( ((NumberList)l).sum() );
			} 
			
			else if(l.isa(STRLIST)) {
				block.push( ((StrList)l).sum() );
			} 
			else {
				
				// If a normal list, fold using '+'
				Obj[] list = l.getObjAL().toArray(new Obj[l.length()]);
				if(list.length == 0) {
					block.push(Num.ZERO);
					return;
				}
				//Push all but the last item
				for(int i = list.length-1; i > 0; i--) {
					block.add(Ops.getOp('+'));
					block.add(list[i]);
				}
				//Push the last element outside the loop so that there is not an extra plus (1 1+2+3+)
				block.add(list[0]); 
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

// T - 84 
class OP_T extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "T");
		doc.desc("N", "negate");
		doc.vect();
		doc.ovrld(Ops.KEYVAR_NEGATE.name());
		OperationDocs.add(doc);
	}
	
	public OP_T() {
		this.name = "T";
	}
	@Override public void execute (final Block block) {
		Obj a = block.pop();
		if (a.isa(NUMBER)) {
			block.push(((Number)a).negate());
		} else if (a.isa(NUMBERLIST)) {
			block.push(((NumberList)a).negate());
		} else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_NEGATE);
		}
		else {
		
			throw new TypeError(this, a);
		}
	}
}


//U - 85
class OP_U extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "U");
		doc.desc("L", "reverse");
		doc.ovrld(Ops.KEYVAR_REVERSE.name());
		OperationDocs.add(doc);
	}
	
	public OP_U() {
		this.name = "U";
	}
	@Override public void execute(final Block block) {
		Obj o = block.pop();
		
		if (o.isa(LIST)) {
			((List)o).reverse();
			block.push(o);
		} else if (o.isa(DICT)) {
			block.callVariable((Dict)o, Ops.KEYVAR_REVERSE);
		}
		else {
			throw new TypeError(this,o);
		}
	}
}



// V - 86
class OP_V extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "V");
		doc.desc("N|C", "increment");
		doc.desc("J", "decrement in place");
		doc.desc("L", "uncons from back");
		doc.ovrld(Ops.KEYVAR_DEC.name());
		OperationDocs.add(doc);
	}
	
	public OP_V () {
		this.name = "V";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
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
			List l = (List)a;
			Obj popped = l.pop();
			block.push(l);
			block.push(popped);
		} else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_DEC);
		} else {
			throw new TypeError(this, a);
		}
	}
}

// W - 87
class OP_W extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "W");
		doc.desc("B", "while loop (repeat as long as block returns true)");
		doc.desc("D", "export all variables");
		OperationDocs.add(doc);
	}
	
	public OP_W() {
		this.name = "W";
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
					throw new TypeError(this, new Str("While condition must be a boolean or a number"), cond);
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
	
	static {
		OpDoc doc = new OpDoc(' ', "X");
		doc.desc("A", "assign to global variable x and pop from stack");
		OperationDocs.add(doc);
	}
	
	public OP_X() {
		this.name = "X";
	}
	@Override public void execute (final Block block) {
		Aya.getInstance().getVars().setGlobalVar(new Variable("x"), block.pop());
	}
}

// Y - 89
class OP_Y extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "Y");
		doc.desc("A", "assign to global variable y and leave on stack");
		OperationDocs.add(doc);
	}
	
	public OP_Y() {
		this.name = "Y";
	}
	@Override public void execute (final Block block) {
		Aya.getInstance().getVars().setGlobalVar(new Variable("y"), block.peek());
	}
}

// Z - 90
class OP_Z extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "Z");
		doc.desc("N", "cast to bignum");
		doc.desc("S", "parse to bignum");
		OperationDocs.add(doc);
	}
	
	public OP_Z() {
		this.name = "Z";
	}
	@Override public void execute (final Block block) {
		Obj a = block.pop();
		
		if (a.isa(Obj.NUMBER)) {
			block.push(new BigNum(((Number)a).toApfloat()));
		} else if (a.isa(Obj.STR)) {
			try	{
				block.push(new BigNum(new Apfloat(a.str())));
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
	
	static {
		OpDoc doc = new OpDoc(' ', "\\");
		doc.desc("AA", "swap top two elements on the stack");
		OperationDocs.add(doc);
	}
	
	public OP_Backslash() {
		this.name = "\\";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		block.push(a);
		block.push(b);
	}
}

// ^ - 94
class OP_Caret extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "^");
		doc.desc("NN", "power");
		doc.desc("SS", "levenshtein distance");
		doc.ovrld(Ops.KEYVAR_POW.name(), Ops.KEYVAR_RPOW.name());
		OperationDocs.add(doc);
	}
	
	public OP_Caret() {
		this.name = "^";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if(a.isa(NUMBER) && b.isa(NUMBER)){
			//Raise b to the ath power
			block.push( ((Number)b).pow((Number)a) );
		} else if (a.isa(STR) && b.isa(STR)) {
			block.push(new Num( ((Str)a).levDist((Str)b) ));
		}
		
		else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( ((NumberList)a).powFrom((Number)b) );
		}
		
		else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).pow((Number)a) );
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).pow((NumberList)a) );
		}
		else if (a.isa(DICT)) {
			block.push(b);
			block.callVariable((Dict)a, Ops.KEYVAR_POW);
		} else if (b.isa(DICT)) {
			block.callVariable((Dict)b, Ops.KEYVAR_RPOW, a);
		}
		else {
			throw new TypeError(this, a, b);
		}

	}
}

// | - 124
class OP_Bar extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "|");
		doc.desc("NN", "logical or");
		doc.desc("SS", "split S1 using regex S2");
		doc.desc("LN", "cut L at index N");
		doc.ovrld(Ops.KEYVAR_OR.name(), Ops.KEYVAR_ROR.name());
		OperationDocs.add(doc);
	}
	
	public OP_Bar() {
		this.name = "|";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		//Bitwise or
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( NumberMath.bor((Number)a, (Number)b) );
		}
		// find regex matches
		else if (a.isa(Obj.STR) && b.isa(Obj.STR)) {
			String[] list = b.str().split(a.str());
			ArrayList<Obj> res = new ArrayList<Obj>(list.length);
			for(String s : list) {
				res.add(new Str(s));
			}
			block.push(new GenericList(res));
		} else if (a.isa(Obj.NUMBER) && b.isa(Obj.LIST)) {
			int index = ((Number)a).toInt();
			List l = (List)b;
			if (index == 0) {
				block.push(l.similarEmpty());
				block.push(l);
			} else if(index >= l.length()) {
				block.push(l);
				block.push(l.similarEmpty());
			} else if  (index*-1 >= l.length()) {
				block.push(l.similarEmpty());
				block.push(l);
			} else if (index > 0) {
				block.push(l.slice(0, index));
				block.push(l.slice(index, l.length()));
			} else if (index < 0) {
				block.push(l.slice(0, l.length()+index));
				block.push(l.slice(l.length()+index, l.length()));
			} else if (index == 0) {
				block.push(new GenericList(new ArrayList<Obj>()));
				block.push(b);
			} else {
				throw new TypeError(this, a, b);
			}
		}
		else if (a.isa(DICT)) {
			block.push(b);
			block.callVariable((Dict)a, Ops.KEYVAR_OR);
		} else if (b.isa(DICT)) {
			block.callVariable((Dict)b, Ops.KEYVAR_ROR, a);
		}
		else {
			throw new TypeError(this, a, b);
		}

	}
}


// ~ - 126
class OP_Tilde extends OpInstruction {
	
	static {
		OpDoc doc = new OpDoc(' ', "~");
		doc.desc("B|S|C", "evaluate");
		doc.desc("L", "dump to stack");
		OperationDocs.add(doc);
	}
	
	public OP_Tilde() {
		this.name = "~";
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
				block.add(new GetVariableInstruction(Variable.encodeString(varname)));
			}
		} else if (a.isa(LIST)) {
			List list = (List)a;
			//Collections.reverse(list);
			for (int i = list.length()-1; i >= 0; i--) {
				block.add(list.get(i));
			}
		} else if (a.isa(SYMBOL)) {
			block.push(Aya.getInstance().getVars().getVar( ((Symbol)a).id() ));
		} else {
			throw new TypeError(this, a);
		}
	}
}


