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
import aya.entities.ListBuilder;
import aya.entities.Operation;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.TypeError;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.dict.KeyVariable;
import aya.obj.list.GenericList;
import aya.obj.list.List;
import aya.obj.list.Str;
import aya.obj.list.StrList;
import aya.obj.list.numberlist.NumberItemList;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.BigNum;
import aya.obj.number.Num;
import aya.obj.number.Number;
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
	public static final KeyVariable KEYVAR_MOD	= new KeyVariable("mod");
	public static final KeyVariable KEYVAR_RMOD	= new KeyVariable("rmod");
	// mul
	public static final KeyVariable KEYVAR_MUL	= new KeyVariable("mul");
	public static final KeyVariable KEYVAR_RMUL	= new KeyVariable("rmul");
	// add
	public static final KeyVariable KEYVAR_ADD	= new KeyVariable("add");
	public static final KeyVariable KEYVAR_RADD	= new KeyVariable("radd");
	// sub
	public static final KeyVariable KEYVAR_SUB	= new KeyVariable("sub");
	public static final KeyVariable KEYVAR_RSUB	= new KeyVariable("rsub");
	// div 
	public static final KeyVariable KEYVAR_DIV	= new KeyVariable("div");
	public static final KeyVariable KEYVAR_RDIV	= new KeyVariable("rdiv");
	// idiv
	public static final KeyVariable KEYVAR_IDIV	= new KeyVariable("idiv");
	public static final KeyVariable KEYVAR_RIDIV= new KeyVariable("ridiv");
	// pow
	public static final KeyVariable KEYVAR_POW	= new KeyVariable("pow");
	public static final KeyVariable KEYVAR_RPOW	= new KeyVariable("rpow");
	
	// NUMERIC - Comparison
	public static final KeyVariable KEYVAR_LT = new KeyVariable("lt");
	public static final KeyVariable KEYVAR_GT = new KeyVariable("gt");
	public static final KeyVariable KEYVAR_LEQ = new KeyVariable("leq");
	public static final KeyVariable KEYVAR_GEQ = new KeyVariable("geq");
	public static final KeyVariable KEYVAR_EQ	= new KeyVariable("eq");
	
	// Numeric - Monads
	public static final KeyVariable KEYVAR_NEGATE = new KeyVariable("negate");
	public static final KeyVariable KEYVAR_CEIL = new KeyVariable("ceil");
	public static final KeyVariable KEYVAR_FLOOR = new KeyVariable("floor");
	public static final KeyVariable KEYVAR_ABS = new KeyVariable("abs");
	public static final KeyVariable KEYVAR_SIGNUM = new KeyVariable("signum");
	public static final KeyVariable KEYVAR_INC = new KeyVariable("inc");
	public static final KeyVariable KEYVAR_DEC = new KeyVariable("dec");
	
	// Numeric - Math
	public static final KeyVariable KEYVAR_SIN = new KeyVariable("sin");
	public static final KeyVariable KEYVAR_ASIN = new KeyVariable("asin");
	public static final KeyVariable KEYVAR_COS = new KeyVariable("cos");
	public static final KeyVariable KEYVAR_ACOS = new KeyVariable("acos");
	public static final KeyVariable KEYVAR_TAN = new KeyVariable("tan");
	public static final KeyVariable KEYVAR_ATAN = new KeyVariable("atan");
	public static final KeyVariable KEYVAR_LN = new KeyVariable("ln");
	public static final KeyVariable KEYVAR_LOG = new KeyVariable("log");
	public static final KeyVariable KEYVAR_EXP = new KeyVariable("exp");
	public static final KeyVariable KEYVAR_FACT = new KeyVariable("fact");
	public static final KeyVariable KEYVAR_SQRT = new KeyVariable("sqrt");
	
	// List
	public static final KeyVariable KEYVAR_GETINDEX = new KeyVariable("getindex");
	public static final KeyVariable KEYVAR_SETINDEX = new KeyVariable("setindex");
	public static final KeyVariable KEYVAR_HEAD = new KeyVariable("head");
	public static final KeyVariable KEYVAR_TAIL = new KeyVariable("tail");
	public static final KeyVariable KEYVAR_MAP = new KeyVariable("map");
	public static final KeyVariable KEYVAR_LEN = new KeyVariable("len");
	public static final KeyVariable KEYVAR_REVERSE = new KeyVariable("reverse");
	public static final KeyVariable KEYVAR_SORT = new KeyVariable("sort");

	
	// Misc.
	public static final KeyVariable KEYVAR_AND		= new KeyVariable("and");
	public static final KeyVariable KEYVAR_RAND		= new KeyVariable("rand");
	public static final KeyVariable KEYVAR_OR		= new KeyVariable("or");
	public static final KeyVariable KEYVAR_ROR		= new KeyVariable("ror");
	public static final KeyVariable KEYVAR_RANDOM 	= new KeyVariable("random");
	public static final KeyVariable KEYVAR_RANGE 	= new KeyVariable("range");
	public static final KeyVariable KEYVAR_NEW 		= new KeyVariable("new");
	public static final KeyVariable KEYVAR_FLOAT 	= new KeyVariable("float");

	



	
	
	public static final char FIRST_OP = '!';
	public static final Operation[] OPS = {
		/* 33 !  */ new OP_Bang(),
		/* 34 "  */ null, // String
		/* 35 #  */ new OP_Pound(),
		/* 36 $  */ new OP_Dollar(),
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
		/* 67 C  */ null,
		/* 68 D  */ new OP_D(),
		/* 69 E  */ new OP_E(),
		/* 70 F  */ new OP_F(),
		/* 71 G  */ new OP_G(),
		/* 72 H  */ new OP_H(),
		/* 73 I  */ new OP_I(),
		/* 74 J  */ null,
		/* 75 K  */ new OP_K(),
		/* 76 L  */ new OP_L(),
		/* 77 M  */ null, //Math Library
		/* 78 N  */ new OP_N(),
		/* 79 O  */ null,
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
		/* 95 _  */ new OP_Underscore(),
		/* 96 `  */ null, // Hold Operator
	};
	
	/* 124 | */ public static final Operation BAR = new OP_Bar();
	/* 126 ~ */ public static final Operation TILDE = new OP_Tilde();
	
	//Special Ops
	public static final Operation APPLY_TO = new OP_ApplyTo();
	
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
				',','(',')','[',']','`','.','"','\'', '#',
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

	
	public static Operation getOp(char op) {
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
class OP_Bang extends Operation {
	
	static {
		OpDoc doc = new OpDoc(' ', "!");
		doc.desc("N", "1-N (logical not, complementary probability)");
		doc.desc("C", "swap case");
		doc.ovrld("new");
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
				block.addOrDumpVar(d.get(Ops.KEYVAR_NEW));
			} else {
				//Create a new empty dict with the input as its metatable
				block.push(new Dict((Dict)o));
			}

		}
		else {
			throw new TypeError(this,o);
		}
		return;
	}
}

//# - 35
class OP_Pound extends Operation {
	
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
class OP_Dollar extends Operation {
	
	
	static {
		OpDoc doc = new OpDoc(' ', "$");
		doc.desc("S|L", "sort least to greatest");
		doc.desc("N", "bitwise not");
		doc.ovrld(Ops.KEYVAR_SORT.name());
		OperationDocs.add(doc);
	}
	
	public OP_Dollar() {
		this.name = "$";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		
		if (a.isa(Obj.NUMBER)) {
			block.push(((Number)(a)).bnot());
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

// % - 37
class OP_Percent extends Operation {
	
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
class OP_And extends Operation {
	
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
			block.push( ((Number)a).band((Number)b) );
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
class OP_Times extends Operation {
	
	static {
		OpDoc doc = new OpDoc(' ', "*");
		doc.desc("NN", "multiply");
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
class OP_Plus extends Operation {
	
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
class OP_Minus extends Operation {
	
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
class OP_Divide extends Operation {
	
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
class OP_SemiColon extends Operation {
	
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
class OP_LessThan extends Operation {
	
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
class OP_Equal extends Operation {
	
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
			block.push(b);
			block.callVariable((Dict)a, Ops.KEYVAR_EQ);
		} else if (b.isa(DICT)) {
			block.callVariable((Dict)b, Ops.KEYVAR_EQ, a);
		} else {
			block.push(new Num(a.equiv(b)));
		}
	}
}

// > - 62
class OP_GreaterThan extends Operation {
	
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
class OP_Conditional extends Operation {
	
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
class OP_At extends Operation {
	
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
class OP_A extends Operation {
	
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

//B - 66
class OP_B extends Operation {
	
	static {
		OpDoc doc = new OpDoc(' ', "B");
		doc.desc("N|C", "increment");
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

// D - 68
class OP_D extends Operation {
	
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
		
		if(a.isa(NUMBER) && b.isa(LIST)) {
			((List)b).set( ((Number)a).toInt(), o);
			block.push(b);
		} 
		else if (b.isa(DICT)) {
			block.push(o);
			block.callVariable((Dict)b, Ops.KEYVAR_SETINDEX, a);
		}
		else {		
			throw new TypeError(this, a, b, o);
		}
		
	}

}

// E - 69
class OP_E extends Operation {
	
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
class OP_F extends Operation {
	
	static {
		OpDoc doc = new OpDoc(' ', "F");
		doc.desc("LB", "fold from right to left");
		OperationDocs.add(doc);
	}
	
	public OP_F() {
		this.name = "F";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		
		if(a.isa(BLOCK) && b.isa(LIST)) {
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
class OP_G extends Operation {
	
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
				if (name.charAt(0) == '/' || name.contains(":\\")) {
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

class OP_H extends Operation {
	
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
				BigInteger out_bi;
				
				//Check Radix Ranges
				if (Character.MIN_RADIX > from_base 
						|| Character.MIN_RADIX > to_base
						|| Character.MAX_RADIX < from_base
						|| Character.MAX_RADIX < to_base) {
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
class OP_I extends Operation {
	
	static {
		OpDoc doc = new OpDoc(' ', "I");
		doc.desc("LL|LN", "get index");
		doc.desc("LB", "filter");
		doc.ovrld(Ops.KEYVAR_GETINDEX.name());
		OperationDocs.add(doc);
	}
	
	public OP_I() {
		this.name = "I";
	}
	@Override public void execute (final Block block) {
		Obj index = block.pop();
		final Obj list = block.pop();
				
		if(list.isa(LIST)) {		
			if(index.isa(NUMBER)) {
				block.push( ((List)list).get(((Number)index).toInt()) );
			} else if (index.isa(NUMBERLIST)) {
				int[] is = ((NumberList)index).toIntArray();
				block.push(((List)list).get(is));
			} else if (index.isa(LIST)) {
				List ix = (List)index;
				if (ix.length() == 0) {
					block.push(list.deepcopy());
				} else {
					throw new AyaRuntimeException("Operator .I expected a numeric list, recieved:\n\t"
							+ list.repr());
				}
			}
			else if (index.isa(BLOCK)) {
				block.push( ((Block)index).filter((List)list) );
			}
		}else if (list.isa(DICT)) {
			block.callVariable((Dict)list, Ops.KEYVAR_GETINDEX, index);
		}
		else {
			throw new TypeError(this, index, list);
		}
	}
}





// L - 76
class OP_L extends Operation {
	
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
class OP_K extends Operation {
	
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
class OP_N extends Operation {
	
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

// P - 80
class OP_P extends Operation {
	
	static {
		OpDoc doc = new OpDoc(' ', "P");
		doc.desc("A", "to string");
		doc.ovrld("str");
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
class OP_Q extends Operation {
	
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
class OP_R extends Operation {
	
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
			block.push( ListBuilder.buildRange((List)a) );
		} else if (a.isa(NUMBER)) {
			block.push(ListBuilder.buildRange((Number)a));
		} else if (a.isa(CHAR)) {
			block.push(ListBuilder.buildRange(((Char)a).charValue()));
		} else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_RANGE);
		} else {
			throw new TypeError(this, a);
		}
	}
}

// S - 83
class OP_S extends Operation {
	
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
class OP_T extends Operation {
	
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
class OP_U extends Operation {
	
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



//V - 86
class OP_V extends Operation {
	
	static {
		OpDoc doc = new OpDoc(' ', "V");
		doc.desc("N|C", "increment");
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
class OP_W extends Operation {
	
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
class OP_X extends Operation {
	
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
class OP_Y extends Operation {
	
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
class OP_Z extends Operation {
	
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
class OP_Backslash extends Operation {
	
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
class OP_Caret extends Operation {
	
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

// _ - 95
class OP_Underscore extends Operation {
	
	static {
		OpDoc doc = new OpDoc(' ', "_");
		doc.desc("A", "deepcopy (duplicate)");
		OperationDocs.add(doc);
	}
	
	public OP_Underscore() {
		this.name = "_";
	}
	@Override public void execute (final Block block) {
		block.push(block.peek().deepcopy());
	}
}


// | - 124
class OP_Bar extends Operation {
	
	static {
		OpDoc doc = new OpDoc(' ', "|");
		doc.desc("NN", "logical or");
		doc.desc("SS", "split S1 using regex S2");
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
			block.push( ((Number)a).bor((Number)b) );
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
			if(index >= l.length() || index*-1 >= l.length()){
				block.push(l);
			} else if (index > 0) {
				block.push(l.slice(0, index));
				block.push(l.slice(index, l.length()));
			} else if (index < 0) {
				block.push(l.slice(0, l.length()+index));
				block.push(l.slice(l.length()+index, l.length()));
			} else if (index == 0) {
				for (int i = 0; i < l.length(); i++) {
					block.push(l.slice(i, i+1));
				}
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
class OP_Tilde extends Operation {
	
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
			final String varname = CharacterParser.getName(c);
			if(varname == null) {
				throw new AyaRuntimeException("Character '" + c + " is not a valid variable");
			}
			block.add(new Variable(varname));
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



// : - special
class OP_ApplyTo extends Operation {
	
	static {
		OpDoc doc = new OpDoc(' ', ":");
		doc.desc("L:B", "map");
		doc.desc("LN:E", "apply block to item at index N");
		doc.desc("LN:A", "set index");
		OperationDocs.add(doc);
	}
	
	public OP_ApplyTo() {
		this.name = ":";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		//Apply block to an index in the list
		if(a.isa(Obj.BLOCK) && b.isa(Obj.NUMBER)) {
			Obj c = block.pop();
			if(!c.isa(LIST)) {
				throw new TypeError(this, a,b,c);
			}
			List list = (List)c;
			Block blk = new Block();
			blk.addAll(((Block)(a)).getInstructions().getInstrucionList());
			int index = ((Number)(b)).toInt();

			blk.push(list.get(index));
			blk.eval();
			list.set(index, blk.pop());
			
			block.push(c);
		} else if (a.isa(Obj.BLOCK) && b.isa(Obj.LIST)) {
			block.push(((Block)(a)).mapTo((List)b));
		} else {
			throw new TypeError(this, a,b);
		}
	}
}


