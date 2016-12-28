package element.entities.operations;

import static element.obj.Obj.BIGNUM;
import static element.obj.Obj.BLOCK;
import static element.obj.Obj.CHAR;
import static element.obj.Obj.LIST;
import static element.obj.Obj.NUM;
import static element.obj.Obj.NUMBER;
import static element.obj.Obj.NUMBERLIST;
import static element.obj.Obj.STR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apfloat.Apfloat;

import element.ElemPrefs;
import element.Element;
import element.entities.Block;
import element.entities.ListBuilder;
import element.entities.Operation;
import element.exceptions.ElementRuntimeException;
import element.exceptions.TypeError;
import element.obj.Obj;
import element.obj.character.Char;
import element.obj.list.List;
import element.obj.list.ObjList;
import element.obj.list.Str;
import element.obj.list.numberlist.NumberItemList;
import element.obj.list.numberlist.NumberList;
import element.obj.number.BigNum;
import element.obj.number.Num;
import element.obj.number.Number;
import element.parser.CharacterParser;
import element.parser.Parser;
import element.variable.MemberVariable;
import element.variable.Variable;

public class Ops {
	
	public static final Random RAND = new Random((new Date()).getTime());
	public static final Pattern PATTERN_URL = Pattern.compile("http:\\/\\/.*|https:\\/\\/.*");
	
	public static final MemberVariable MEMVAR_NEW = new MemberVariable("new");

	public static final MemberVariable MV_DOLLAR = new MemberVariable("dollar");
	public static final MemberVariable MV_LEN = new MemberVariable("len");
	public static final MemberVariable MV_STR = new MemberVariable("show");
	public static final MemberVariable MV_PERCENT = new MemberVariable("percent");
	public static final MemberVariable MV_AMP = new MemberVariable("amp");
	public static final MemberVariable MV_STAR = new MemberVariable("star");
	public static final MemberVariable MV_PLUS = new MemberVariable("plus");
	public static final MemberVariable MV_MINUS = new MemberVariable("minus");
	public static final MemberVariable MV_FSLASH = new MemberVariable("fslash");
	public static final MemberVariable MV_BAR = new MemberVariable("bar");
	public static final MemberVariable MV_POW = new MemberVariable("pow");
	public static final MemberVariable MV_INDEX = new MemberVariable("index");
	public static final MemberVariable MV_RANDCHOICE = new MemberVariable("randchoice");
	public static final MemberVariable MV_SETINDEX = new MemberVariable("setindex");
	public static final MemberVariable MV_EQ = new MemberVariable("eq");


	
	
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
		/* 70 F  */ null, //False Literal
		/* 71 G  */ new OP_G(),
		/* 72 H  */ new OP_H(),
		/* 73 I  */ new OP_I(),
		/* 74 J  */ null, //Repeat
		/* 75 K  */ new OP_K(),
		/* 76 L  */ new OP_L(),
		/* 77 M  */ null, //Math Library
		/* 78 N  */ new OP_N(),
		/* 79 O  */ null,
		/* 80 P  */ new OP_P(),
		/* 81 Q  */ new OP_Q(),
		/* 82 R  */ new OP_R(),
		/* 83 S  */ new OP_S(),
		/* 84 T  */ null, //True Literal
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
	
	/** Returns a list of all the op descriptions **/
	public static ArrayList<String> getAllOpDescriptions() {
		ArrayList<String> out = new ArrayList<String>();
		for (char i = 0; i <= 96-FIRST_OP; i++) {
			if(OPS[i] != null) {
				out.add(OPS[i].name + " (" + OPS[i].argTypes + ")\n" + OPS[i].info + "\n(operator)");
			}
		}
		out.add(TILDE.name + " (" + TILDE.argTypes + ")\n" + TILDE.info + "\n(operator)" );
		out.add(BAR.name + " (" + BAR.argTypes + ")\n" + BAR.info + "\n(operator)" );
		out.add(APPLY_TO.name + " (" + APPLY_TO.argTypes + ")\n" + APPLY_TO.info + "\n(operator)" );

		return out;
	}

	
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
	public OP_Bang() {
		this.name = "!";
		this.info = "<N> negate\n<S|L> reverse\n<B> logical not\n<C> swap case";
		this.argTypes = "S|N|L|B|C";
	}
	@Override public void execute(final Block block) {
		Obj o = block.pop();
		
		if (o.isa(Obj.NUMBER)) {
			block.push(((Number)o).negate());
		} else if (o.isa(Obj.STR)) {
			((Str)o).reverse();
			block.push(o);
		} else if(o.isa(Obj.LIST)) {
			((List)o).reverse();
			block.push(o);
		}
		// else if (isBool(o)) {
		//	 block.push(!toBool(o));
		//	 return;
		// }
		else if (o.isa(Obj.CHAR)) {
			block.push(((Char)o).swapCase());
		}
//		else if (isModule(o)) {
//			Module m = toModule(o);
//			if(m.hasVar(Ops.MEMVAR_NEW)) {
//				Object new_var = m.get(Ops.MEMVAR_NEW);
//				if (isBlock(new_var)) {
//					block.addAll(toBlock(new_var).getInstructions().getInstrucionList());
//				} else {
//					block.add(new_var);
//				}
//			} else {
//				throw new ElementRuntimeException("(!) the variable 'new' is nod defined in module " + m.toString());
//			}
//		} 
		else {
			throw new TypeError(this,o);
		}
		return;
	}
}

//# - 35
class OP_Pound extends Operation {
	public OP_Pound() {
		this.name = "#";
		this.info = "<LA#A> map";
		this.argTypes = "LE|LN";
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
					throw new ElementRuntimeException("Could not find list to map to\n"
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
	public OP_Dollar() {
		this.name = "$";
		this.info = "sort least to greatest\nbitwise not\n(overloadable: dollar)";
		this.argTypes = "L<N>|L<S>|S|I";
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
		} else {
			throw new TypeError(this,a);
		}
			
//			//Type validation
//			boolean string = false;	//Will be true if there exists a string in the list
//			boolean num = false;	//Will be true if there exists a number in the list
//			
//			//Numerical ranks
//			//final byte i = 0; 		// Int
//			final byte basic = 1; 		// Num
//			final byte big = 2; 		// BigNum
//			
//			byte highestRank = 1;			//The highest ranking number type
//			
//			//The list to be sorted
//			ArrayList<Object> list = toList(a);
//			
//			//Collect type data
//			for (Object item : list) {
//				if (isNumeric(item)) {
//					num = true;
//					//Are there any bigdecimals?
//					if(highestRank < big && isBigNum(item)) {
//						highestRank = big;
//					}
//				}
//				else if (isString(item)) {
//					string = true;
//				}
//				else {
//					//Only strings and numbers can be sorted
//					throw new TypeError(this,a);
//				}
//				if(string && num) {
//					//Strings and numbers cannot be sorted together
//					throw new TypeError(this,a);
//				}
//			} 
//			
//			
//			//Based on the type, sort the list
//			if(num) {
//				switch(highestRank) {
//				case basic:
//					ArrayList<Num> nums = new ArrayList<Num>();
//					for (Object item : list) {
//						nums.add(Num.fromObj(item));
//					}
//					list.clear();
//					Collections.sort(nums);
//					for(Num item : nums) {
//						list.add(item);
//					}
//					block.push(list);
//					return;
//				case big:
//					ArrayList<BigNum> bigs = new ArrayList<BigNum>();
//					for (Object item : list) {
//						bigs.add(BigNum.fromObj(item));
//					}
//					list.clear();
//					Collections.sort(bigs);
//					for(BigNum item : bigs) {
//						list.add(item);
//					}
//					block.push(list);
//					return;
//				}
//			} else if(string) {
//				ArrayList<String> strings = new ArrayList<String>();
//				for (Object item : list) {
//					strings.add(getString(item));
//				}
//				list.clear();
//				Collections.sort(strings);
//				for(String item : strings) {
//					list.add(item);
//				}
//				block.push(list);
//				return;
//			}
//		} 
//		else if (isUserObject(a)) {
//			toUserObject(a).callVariable(block, Ops.MV_DOLLAR);
//			return;
//		}
		
		
	}
}

// % - 37
class OP_Percent extends Operation {
	public OP_Percent() {
		this.name = "%";
		this.info = "<NN>mod\n<EN>repeat the block N times\n(overloadable: percent)";
		this.argTypes = "NN|EN";
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
				throw new ElementRuntimeException("%: Divide by 0");
			}
		}
		else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( ((NumberList)a).modFrom((Number)b) );
		}
		
		else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).mod((Number)a) );
		}
		else if (a.isa(NUMBER) && b.isa(BLOCK)) {
			int repeats = ((Number)(a)).toInt();
			Block blk = ((Block)b);
			for (int i = 0; i < repeats; i ++) {
				block.addAll(blk.getInstructions().getInstrucionList());
			}
			return;
		} 
//		else if (isUserObject(a)) {
//			block.push(b);
//			toUserObject(a).callVariable(block, Ops.MV_PERCENT);
//			return;
//		} else if (isUserObject(b)) {
//			toUserObject(b).callVariable(block, Ops.MV_PERCENT, a);
//			return;
//		}
		else {
			throw new TypeError(this, a,b);
		}
	}
}



// & - 38
class OP_And extends Operation {
	public OP_And() {
		this.name = "&";
		this.info = "<BB> logical and\n  <SS>match all expressions matching the regex\n<II> bitwise or\n(overloadable: amp)";
		this.argTypes = "BB|SS|II";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
//		if(bothBool(a,b)) {
//			block.push(toBool(a) && toBool(b));
//		} else 
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( ((Number)a).band((Number)b) );
		} 
		
		else if (a.isa(Obj.STR) && b.isa(Obj.STR)) {
			ArrayList<Obj> allMatches = new ArrayList<Obj>();
			Matcher m = Pattern.compile(a.str()).matcher(b.str());
			while (m.find()) {
				 allMatches.add(new Str(m.group()));
			}
			block.push(new ObjList(allMatches));
		}
//		else if (isUserObject(a)) {
//			block.push(b);
//			toUserObject(a).callVariable(block, Ops.MV_AMP);
//		} else if (isUserObject(b)) {
//			toUserObject(b).callVariable(block, Ops.MV_AMP, a);
//		} 
		else {
			throw new TypeError(this, a);
		}

	}
}

// * - 42
class OP_Times extends Operation {
	public OP_Times() {
		this.name = "*";
		this.info = "multiply\n(overloadable: star)";
		this.argTypes = "NN";
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
//		else if (isUserObject(a)) {
//			block.push(b);
//			toUserObject(a).callVariable(block, Ops.MV_STAR);
//		} else if (isUserObject(b)) {
//			toUserObject(b).callVariable(block, Ops.MV_STAR, a);
//		} 
		else {	
			throw new TypeError(this, a,b);
		}
	}
}


// + - 43
class OP_Plus extends Operation {
	public OP_Plus() {
		this.name = "+";
		this.info = "<NN|CC|NC|CN> add\n<SA|AS> concat\n(overloadable: plus)";
		this.argTypes = "NN|CC|NC|CN|SA|AS";
	}
	@Override public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( ((Number)a).add((Number)b) );
		}
//		else if (isUserObject(a)) { //Must happen before anyString()
//			block.push(b);
//			toUserObject(a).callVariable(block, Ops.MV_PLUS);
//		} else if (isUserObject(b)) { //Must happen before anyString()
//			toUserObject(b).callVariable(block, Ops.MV_PLUS, a);
//		} 
		
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
	public OP_Minus() {
		this.name = "-";
		this.info = "<NN|CC|NC|CN> subtract\n(overloadable: minus)";
		this.argTypes = "NN|CC|NC|CN";
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
		
//		else if (isUserObject(b)) {
//			block.push(a);
//			toUserObject(b).callVariable(block, Ops.MV_MINUS);
//		} else if (isUserObject(a)) {
//			toUserObject(a).callVariable(block, Ops.MV_MINUS, b);
//		} 
		else {
			throw new TypeError(this, a,b);
		}
	}
}

// / - 47
class OP_Divide extends Operation {
	public OP_Divide() {
		this.name = "/";
		this.info = "divide\n(overloadable: fslash)";
		this.argTypes = "NN";
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
//		else if (isUserObject(a)) {
//			block.push(b);
//			toUserObject(a).callVariable(block, Ops.MV_FSLASH);
//		} else if (isUserObject(b)) {
//			toUserObject(b).callVariable(block, Ops.MV_FSLASH, a);
//		}
	
		else {
			throw new TypeError(this, a,b);
		}
	}
}


// ; - 59
class OP_SemiColon extends Operation {
	public OP_SemiColon() {
		this.name = ";";
		this.info = "pop and discard the top of the stack";
		this.argTypes = "A";
	}
	@Override public void execute (final Block block) {
		block.pop();
	}
}


// < - 60
class OP_LessThan extends Operation {
	public OP_LessThan() {
		this.name = "<";
		this.info = "less than comparison operator\nhead";
		this.argTypes = "NN|CC|SS|LN";
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
		}
//		else if (b.isa(NUMBER) && a.isa(STR)) {
//			String str = getString(a);
//			int n = ((Number)(b)).toIndex(str.length());
//
//			
//			if (n <= str.length()) {
//				block.push(str.substring(0, n));
//			} else {
//				int extra = n - str.length();
//				char[] pad = new char[extra];
//				Arrays.fill(pad, ' ');
//				block.push(str + new String(pad));
//			}
//			
//		} 
		else if (b.isa(NUMBER) && a.isa(LIST)) {
//			ArrayList<Object> list = toList(a);
//			int n = ((Number)(b)).toIndex(list.size());
//			ArrayList<Object> out = new ArrayList<Object>(n);
//			
//			if (n <= list.size()) {
//				for (int i = 0; i < n; i++) {
//					out.add(list.get(i));
//				}
//			} else {
//				out.addAll(list);
//				for (int i = list.size(); i < n; i++) {
//					out.add(0); //Pad with 0s
//				}
//			}
			
			
			block.push(((List)a).head(((Number)a).toInt()));

		} else {
			throw new TypeError(this, a,b);
		}
	}
}


// = - 61
class OP_Equal extends Operation {
	public OP_Equal() {
		this.name = "=";
		this.info = "equality comparison operator\n(overloadable: eq )";
		this.argTypes = "AA";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
//		if (isUserObject(a)) {
//			block.push(b);
//			toUserObject(a).callVariable(block, Ops.MV_EQ);
//		} 
//		else if (isUserObject(b)) {
//			toUserObject(b).callVariable(block, Ops.MV_EQ, a);
//		} 
//		else {
			block.push(new Num(a.equiv(b)));
//		}
	}
}

// > - 62
class OP_GreaterThan extends Operation {
	public OP_GreaterThan() {
		this.name = ">";
		this.info = "greater than comparison operator";
		this.argTypes = "NN|CC|SS";
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
		} 
//		else if (b.isa(Obj.NUMBER) && a.isa(Obj.STR)) {
//			String str = getString(a);
//			int n = ((Number)(b)).toIndex(str.length());
//
//			
//			if (n <= str.length()) {
//				block.push(str.substring(str.length()-n, str.length()));
//			} else {
//				int extra = n - str.length();
//				char[] pad = new char[extra];
//				Arrays.fill(pad, ' ');
//				block.push(new String(pad) + str);
//			}
//		}
		else if (b.isa(NUMBER) && a.isa(LIST)) {
//			ArrayList<Object> list = toList(a);
//			int n = ((Number)(b)).toIndex(list.size());
//			ArrayList<Object> out = new ArrayList<Object>(n);
//			
//			if (n <= list.size()) {
//				for (int i = list.size()-n; i < list.size(); i++) {
//					out.add(list.get(i));
//				}
//			} else {
//				for (int i = 0; i < n-list.size(); i++) {
//					out.add(0); //Pad with 0s
//				}
//				out.addAll(list);
//			}
			
			block.push( ((List)a).tail(((Number)b).toInt()) );
		}  else {
			throw new TypeError(this, a,b);
		}
	}
}

// ? - 63
class OP_Conditional extends Operation {
	public OP_Conditional() {
		this.name = "?";
		this.info = "<AAB> conditional operator. if the first argument if true pop the second, if false, pop the third";
		this.argTypes = "AAB";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		final Obj c = block.pop();
		//   c      b     a
		//{false} {true} cond
		
		
		if(a.bool()) {			
			if(b.isa(BLOCK)) {
				block.addAll(((Block)b).duplicate().getInstructions().getInstrucionList());
			} else {
				block.push(b);
			}
		} else {
			if(c.isa(BLOCK)) {
				block.addAll(((Block)c).duplicate().getInstructions().getInstrucionList());
			} else {
				block.push(c);
			}
		}
		
		//throw new TypeError(this, a,b,c);
	}
}


// @ - 64
class OP_At extends Operation {
	public OP_At() {
		this.name = "@";
		this.info = "rotates the top three elements on the stack [abc->bca]";
		this.argTypes = "AAA";
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

//A - 65
class OP_A extends Operation {
	public OP_A() {
		this.name = "A";
		this.info = "wrap item in stack in an array";
		this.argTypes = "A";
	}
	@Override public void execute (final Block block) {
		final ArrayList<Obj> al = new ArrayList<Obj>();
		al.add(block.pop());
		block.push(new ObjList(al));
	}
}

//B - 66
class OP_B extends Operation {
	public OP_B() {
		this.name = "B";
		this.info = "<N|C> increment\n<L> uncons from front";
		this.argTypes = "N|C|L";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (a.isa(Obj.NUMBER)) {
			block.push( ((Number)a).inc() );
		} else if (a.isa(CHAR)) {
			block.push( ((Char)a).inc() );
		} else if (a.isa(LIST)) {
			//ArrayList<Object> l = toList(a);
			//Object popped = l.remove(l.size()-1);
			//block.push(l.clone()); //Keep list on stack
			//block.push(popped);
			List l = (List)a;
			Obj popped = l.pop();
			block.push(l);
			block.push(popped);
			
		} else {
			throw new TypeError(this, a);
		}
	}
}

//D - 68
class OP_D extends Operation {
	public OP_D() {
		this.name = "D";
		this.info = "ALI set index\n(overloadable: setindex)";
		this.argTypes = "ALI|AUI";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();   //Index
		Obj b = block.pop();			//List
		final Obj o = block.pop();	//Item
		
		if(a.isa(NUMBER) && b.isa(LIST)) {
			((List)b).set( ((Number)a).toInt(), o);
		}
//		else if (isUserObject(b)) {
//			toUserObject(b).callVariable(block, Ops.MV_SETINDEX, a);
//		} 
		else {		
			throw new TypeError(this, a, b, o);
		}
		
	}

}

//E - 69
class OP_E extends Operation {
	public OP_E() {
		this.name = "E";
		this.info = "<N> scientific notation operator. return 10^N\n<L|S> length\n(overloadable: len)";
		this.argTypes = "I|L|S";
	}
	@Override public void execute (final Block block) {
		Obj n = block.pop();
		if (n.isa(NUMBER)) {
			block.push( new Num(10).pow((Number)n) );
		} else if (n.isa(LIST)) {
			block.push( new Num(((List)n).length()) );
		} 
//		else if (isUserObject(n)) {
//			toUserObject(n).callVariable(block, Ops.MV_LEN);
//		}
		else {
			throw new TypeError(this, n);
		}
	}
}

// F - 70

//G - 71
class OP_G extends Operation {
	public OP_G() {
		this.name = "G";
		this.info = "S reads a string from a filename string\nS downloads text file located at a URL as a string"
				+ "\nN isprime";
		this.argTypes = "SI";
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
					throw new ElementRuntimeException("Cannot read URL: " + name);
				} finally {
					if(scnr != null)
						scnr.close();
				}
			} else {
				String path = "";
				if (name.charAt(0) == '/' || name.contains(":\\")) {
					path = name;
				} else {
					path = ElemPrefs.getWorkingDir() + name;
				}
				File file = new File(path);
				BufferedReader br = null;
				StringBuilder sb = new StringBuilder();
				try {
					String line;
					br = new BufferedReader(new FileReader(file));
					while((line = br.readLine()) != null) {
						sb.append(line+'\n');
					}
					block.push( new Str(sb.toString()) );
				} catch (IOException e) {
					throw new ElementRuntimeException("Cannot open file: " + file.getAbsolutePath());
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (IOException e) {
						throw new ElementRuntimeException("Cannot close file: " + file.getAbsolutePath());
					}
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
	public OP_H() {
		this.name = "H";
		this.info = "convert base of S|N from I1 to I2";
		this.argTypes = "SII|NII";
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
					throw new ElementRuntimeException("H: base out of range (" + from_base + ", " + to_base + ")");
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
								throw new ElementRuntimeException("H: List must be base 2");
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
			throw new ElementRuntimeException("H: invalid number format (" 
					+ num.repr() + ", " + from_b.repr() + ", " + to_b.repr() + ")");
		}
		
		
		throw new TypeError(this, num, from_b, to_b);
	}
}

// I - 73
//NOTE: If updating this operator, also update .I
class OP_I extends Operation {
	public OP_I() {
		this.name = "I";
		this.info = "<LL>|<LI> index\n<LE>filter\n(overloadable: index, must be <UA>)";
		this.argTypes = "LL|LI|LE";
	}
	@Override public void execute (final Block block) {
		Obj index = block.pop();
		final Obj list = block.pop();
		
//		if (isUserObject(list)) {
//			toUserObject(list).callVariable(block, Ops.MV_INDEX, index);
//			return;
//		}
		
		//Normal list execution
		
		if(!list.isa(LIST)) {
			throw new TypeError(this, index, list);
		}
		
		if(index.isa(NUMBER)) {
			block.push( ((List)list).get(((Number)index).toInt()) );
		} else if (index.isa(NUMBERLIST)) {
			NumberList indexList = ((List)index).toNumberList();
			List refList = (List)list;
			for(int i = 0; i < indexList.length(); i++) {
				indexList.set( i, refList.get(indexList.get(i).toInt()) );
			}
			block.push(indexList);
		} else if (index.isa(BLOCK)) {
			block.push( ((Block)index).filter((List)list) );
		} else {
			throw new TypeError(this, index, list);
		}
	}
}

// L - 76
class OP_L extends Operation {
	public OP_L() {
		this.name = "L";
		this.info = "create a list by repeating A N times";
		this.argTypes = "AN";
	}
	@Override public void execute (final Block block) {
		Obj n = block.pop();
		Obj item = block.pop();
		if(n.isa(NUMBER)) {
			int repeats = ((Number)n).toInt();
			if(repeats < 0) {
				throw new ElementRuntimeException("Cannot create list with negative number of elements");
			}
//			ArrayList<Object> list = new ArrayList<Object>(repeats);
//			if(isList(item)) {
//				for(int i = 0; i < repeats; i++) {
//					list.add(toList(item).clone());
//				}				
//			} else {
//				for(int i = 0; i < repeats; i++) {
//					list.add(item);
//				}
//			}
			
			if (item.isa(CHAR)) {
				block.push( new Str( ((Char)item).charValue(), repeats) );
			} else if (item.isa(NUMBER)) {
				block.push( new NumberItemList((Number)item, repeats) );
			} else {
				block.push( new ObjList(item, repeats) );
			}

		} else {
			throw new TypeError(this, n , item);
		}
	}
}

// K - 76
class OP_K extends Operation {
	public OP_K() {
		this.name = "K";
		this.info = "<LL> concatenate lists\n<LA|AL> add a to front/back of list L\n<AA> create a list containing both args";
		this.argTypes = "LL|LA|AL|AA";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		//int a_type = getTypeID(a);
		//int b_type = getTypeID(b);
		
		// Zero length list is a string, we want it to behave as a list
		//if(a.isa(STR) && ((Str)a).length() == 0 ) {a_type = LIST;}
		//if(b.isa(STR) && ((Str)b).length() == 0 ) {b_type = LIST;}
		
		if (a.isa(LIST) && b.isa(LIST)) {
			//final ArrayList<Object> bl = toList(b);
			//bl.addAll(toList(a));
			
			((List)b).addAll((List)a);
			block.push(b);
			
		} else if (a.isa(LIST)){//&& !a.isa(Obj.STR)) {
			//final ArrayList<Object> al = toList(a);
			//al.add(0,b);
			//block.push(al);
			
			((List)a).addItem(0, b);
			block.push(a);
			
		} else if (b.isa(LIST)){//&& !b.isa(Obj.STR)) {
//			final ArrayList<Object> bl = toList(b);
//			bl.add(a);
//			block.push(bl);
			
			((List)b).addItem(a);
			block.push(b);
			
		} else {
			
//			final ArrayList<Object> list = new ArrayList<Object>(2);
//			list.add(b);	//Stack - Add in reverse order
//			list.add(a);
//			block.push(list);
//			return;
			
			final ArrayList<Obj> list = new ArrayList<Obj>();
			list.add(b);  //Stack - Add in reverse order
			list.add(a);
			block.push(new ObjList(list).promote());
		}
	}
}

// N - 78
class OP_N extends Operation {
	public OP_N() {
		this.name = "N";
		this.info = "L<A> returns the index of the first occurance of A in L. returns -1 if not found";
		this.argTypes = "LA";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop(); //Item
		final Obj b = block.pop(); //List
		
		if(b.isa(Obj.LIST)) {
			
			block.push(b);
			
			List l = (List)b;
			block.push(new Num(l.find(a)));

//			for (Object o : toList(b)) {
//				if (areEqual(o, a)) {
//					block.push(new Num(index));
//					return;
//				}
//				index++;
//			}
//			block.push(new Num(-1));
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// P - 80
class OP_P extends Operation {
	public OP_P() {
		this.name = "P";
		this.info = "returns the value as a string\n(overloadable: str)";
		this.argTypes = "A";
	}
	@Override public void execute (final Block block) {
		block.push(new Str(block.pop().str()));
	}
}

//Q - 81
class OP_Q extends Operation {
	public OP_Q() {
		this.name = "Q";
		this.info = "positive N: random number 0-N\nnegative N: random number N-0\nN=0: any int\n(overloadable: randchoice)";
		this.argTypes = "NU";
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
//		else if (isUserObject(a)) {
//			toUserObject(a).callVariable(block, Ops.MV_RANDCHOICE);
//		} else if (a.isa(LIST)) {
//			ArrayList<Object> list = toList(a);
//			int ix = Ops.RAND.nextInt(list.size());
//			block.push(toList(a).get(ix));
//		}
		else {
			throw new TypeError(this, a);
		}
	}
}

//R - 82
class OP_R extends Operation {
	public OP_R() {
		this.name = "R";
		this.info = "creates a range from 0 to N using the format from list comprehension";
		this.argTypes = "N|C|L";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		if(a.isa(LIST)) {
			block.push( ListBuilder.buildRange((List)a) );
		} else if (a.isa(NUM)) {
			block.push(ListBuilder.buildRange((Num)a));
		} else if (a.isa(BIGNUM)) {
			block.push(ListBuilder.buildRange((BigNum)a));
		} else if (a.isa(CHAR)) {
			block.push(ListBuilder.buildRange(((Char)a).charValue()));
		} else {
			throw new TypeError(this, a);
		}
	}
}

// S - 83
class OP_S extends Operation {
	public OP_S() {
		this.name = "S";
		this.info = "sum (fold using +)";
		this.argTypes = "L";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		
		if (a.isa(LIST)) {
			
			// Attempt to promote the list
			List l = (List)a;
			if (l.isa(Obj.OBJLIST)) {
				l = ((ObjList)l).promote();
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

//T - 84 (True Literal)

//U - 85
class OP_U extends Operation {
	public OP_U() {
		this.name = "U";
		this.info = "fold from left to right";
		this.argTypes = "LB";
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
		}
			
		throw new TypeError(this, a, b);
	}
}

//V - 86
class OP_V extends Operation {
	public OP_V () {
		this.name = "V";
		this.info = "<N|C> decrement\n<L> uncons from back";
		this.argTypes = "N|C|L";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (a.isa(NUMBER)) {
			block.push( ((Number)a).dec() );
		} else if (a.isa(CHAR)) {
			block.push( ((Char)a).dec() );
		} else if (a.isa(LIST)) {
//			ArrayList<Object> l = toList(a);
//			Object popped = l.remove(0);
//			block.push(l.clone()); //Keep list on stack
//			block.push(popped);
			
			List l = (List)a;
			Obj popped = l.popBack();
			block.push(l);
			block.push(popped);
		} else {
			throw new TypeError(this, a);
		}
	}
}

//W - 87
class OP_W extends Operation {
	public OP_W() {
		this.name = "W";
		this.info = "E while loop\nM import all vars to global";
		this.argTypes = "E|M";
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
					throw new TypeError("While","condition must be a boolean or a number",cond);
				}
			
			} while (condition);
			
			//Merge the stack
			block.setStack(state.getStack());
			
			return;
		}
//		else if(isModule(a)) {
//			Module m = toModule(a);
//			Element.getInstance().getVars().getGlobals().merge(m.getVarSet());
//			return;
//		}
		throw new TypeError(this, a);
	}
}


// X - 88
class OP_X extends Operation {
	public OP_X() {
		this.name = "X";
		this.info = "assigns the value to the global variable x and removes it from the stack";
		this.argTypes = "A";
	}
	@Override public void execute (final Block block) {
		Element.getInstance().getVars().setGlobalVar(new Variable("x"), block.pop());
	}
}

//Y - 89
class OP_Y extends Operation {
	public OP_Y() {
		this.name = "Y";
		this.info = "assigns the value to the global variable x and leaves it on the stack";
		this.argTypes = "A";
	}
	@Override public void execute (final Block block) {
		Element.getInstance().getVars().setGlobalVar(new Variable("y"), block.peek());
	}
}

//Y - 89
class OP_Z extends Operation {
	public OP_Z() {
		this.name = "Z";
		this.info = "SN cast to bignum";
		this.argTypes = "SN";
	}
	@Override public void execute (final Block block) {
		Obj a = block.pop();
		
		if (a.isa(Obj.NUMBER)) {
			block.push(new BigNum(((Number)a).toApfloat()));
		} else if (a.isa(Obj.STR)) {
			try	{
				block.push(new BigNum(new Apfloat(a.str())));
			} catch (NumberFormatException e) {
				throw new ElementRuntimeException("Cannot cast " + a.str() + " to bignum");
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

// \ - 92
class OP_Backslash extends Operation {
	public OP_Backslash() {
		this.name = "\\";
		this.info = "swaps the top two elements on the stack";
		this.argTypes = "AA";
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
	public OP_Caret() {
		this.name = "^";
		this.info = "NN Raises n1 to the n2th power\nSS the levenshtein distance of two strings";
		this.argTypes = "NN";
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
		}
//		else if (isUserObject(a)) {
//			block.push(b);
//			toUserObject(a).callVariable(block, Ops.MV_POW);
//		} else if (isUserObject(b)) {
//			toUserObject(b).callVariable(block, Ops.MV_POW, a);
//		} 
		else {
			throw new TypeError(this, a, b);
		}

	}
}

// _ - 95
class OP_Underscore extends Operation {
	public OP_Underscore() {
		this.name = "_";
		this.info = "duplicates the top item on the stack using deepcopy";
		this.argTypes = "A";
	}
	@Override public void execute (final Block block) {
//		final Object a = block.peek();
//		if(a instanceof ArrayList) {
//			block.push(toList(a).clone());
//		} else {
//			block.push(a);
//		}
		block.push(block.pop().deepcopy());
	}
}


// | - 124
class OP_Bar extends Operation {
	public OP_Bar() {
		this.name = "|";
		this.info = "<BB|SS> logical or\n<SS> split S1 using regex S2\n(overloadable: bar)";
		this.argTypes = "BB|SS";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		//Bitwise or
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( ((Number)a).bor((Number)b) );
		}
		//Logical or
//		else if(bothBool(a,b)) {
//			block.push(toBool(a) || toBool(b));
//		}
		// find regex matches
		else if (a.isa(Obj.STR) && b.isa(Obj.STR)) {
			String[] list = b.str().split(a.str());
			ArrayList<Obj> res = new ArrayList<Obj>(list.length);
			for(String s : list) {
				res.add(new Str(s));
			}
			block.push(new ObjList(res));
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
//		//User object
//		else if (isUserObject(a)) {
//			block.push(b);
//			toUserObject(a).callVariable(block, Ops.MV_BAR);
//		} else if (isUserObject(b)) {
//			toUserObject(b).callVariable(block, Ops.MV_BAR, a);
//		} 
		else {
			throw new TypeError(this, a, b);
		}

	}
}


// ~ - 126
class OP_Tilde extends Operation {
	public OP_Tilde() {
		this.name = "~";
		this.info = "evaluate a block, string, or char. dump a list to the stack";
		this.argTypes = "E|S|L|C";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		
		if(a.isa(BLOCK)) {
			block.addAll(((Block)(a)).getInstructions().getInstrucionList());
			return;
		} else if (a.isa(STR)) {
			block.addAll(Parser.compile(a.str(), Element.getInstance()).getInstructions().getInstrucionList());
			return;
		} else if (a.isa(CHAR)) {
			final char c = ((Char)a).charValue();
			final String varname = CharacterParser.getName(c);
			if(varname == null) {
				throw new ElementRuntimeException("Character '" + c + " is not a valid variable");
			}
			block.add(new Variable(varname));
			return;
		} else if (a.isa(LIST)) {
			List list = (List)a;
			//Collections.reverse(list);
			for (int i = list.length()-1; i >= 0; i--) {
				block.add(list.get(i));
			}
			return;
		}
		
		throw new TypeError(this, a);
	}
}



// : - special
class OP_ApplyTo extends Operation {
	public OP_ApplyTo() {
		this.name = ":";
		this.info = "<LN:E> (infix) apply block to the item at index N in the list\n<L:E> (infix) map E to L";
		this.argTypes = "LN:E|L:E";
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


