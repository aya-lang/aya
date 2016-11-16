package element.entities.operations;

import static element.ElemTypes.LIST;
import static element.ElemTypes.STRING;
import static element.ElemTypes.abbrvToID;
import static element.ElemTypes.anyBig;
import static element.ElemTypes.anyChar;
import static element.ElemTypes.anyNum;
import static element.ElemTypes.anyString;
import static element.ElemTypes.areEqual;
import static element.ElemTypes.bothBool;
import static element.ElemTypes.bothChar;
import static element.ElemTypes.bothNumeric;
import static element.ElemTypes.bothString;
import static element.ElemTypes.castString;
import static element.ElemTypes.getString;
import static element.ElemTypes.getTypeID;
import static element.ElemTypes.isBig;
import static element.ElemTypes.isBlock;
import static element.ElemTypes.isBool;
import static element.ElemTypes.isChar;
import static element.ElemTypes.isList;
import static element.ElemTypes.isModule;
import static element.ElemTypes.isNumeric;
import static element.ElemTypes.isBigNum;
import static element.ElemTypes.isNum;
import static element.ElemTypes.isString;
import static element.ElemTypes.isUserObject;
import static element.ElemTypes.length;
import static element.ElemTypes.toBlock;
import static element.ElemTypes.toNumeric;
import static element.ElemTypes.toNum;
import static element.ElemTypes.toBigNum;
import static element.ElemTypes.toBool;
import static element.ElemTypes.toChar;
import static element.ElemTypes.toList;
import static element.ElemTypes.toModule;
import static element.ElemTypes.toUserObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import element.entities.number.Num;
import element.entities.number.BigNum;
import element.entities.number.Numeric;
import element.entities.number.NumMath;
import element.exceptions.ElementRuntimeException;
import element.exceptions.TypeError;
import element.parser.CharacterParser;
import element.parser.Parser;
import element.variable.MemberVariable;
import element.variable.Module;
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
				',','(',')','[',']','`','.','"','\'', '#','T','F'
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
		Object o = block.pop();
		
		if (isNumeric(o)) {
			block.push(toNumeric(o).negate());
		} else if (isString(o)) {
			block.push(new StringBuilder(castString(o)).reverse().toString());
		} else if(isList(o)) {
			Collections.reverse(toList(o));
			block.push(o);
		} else if (isBool(o)) {
			block.push(!toBool(o));
			return;
		} else if (isChar(o)) {
			char c = toChar(o);
			if (Character.isUpperCase(c)) {
				block.push(Character.toLowerCase(c));
			} else if (Character.isLowerCase(c)) {
				block.push(Character.toUpperCase(c));
			} else {
				block.push(c);
			}
		} else if (isModule(o)) {
			Module m = toModule(o);
			if(m.hasVar(Ops.MEMVAR_NEW)) {
				Object new_var = m.get(Ops.MEMVAR_NEW);
				if (isBlock(new_var)) {
					block.addAll(toBlock(new_var).getInstructions().getInstrucionList());
				} else {
					block.add(new_var);
				}
			} else {
				throw new ElementRuntimeException("(!) the variable 'new' is nod defined in module " + m.toString());
			}
		} else {
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
		final Object a = block.pop();
		
		if(isBlock(a)) {
			final Block map = toBlock(a).duplicate();
						
			Object popped = block.pop();
			
			//Capture all non-list items from the left of the #
			while (!isList(popped)) {
				map.add(popped);
				
				if(block.stackEmpty()) {
					throw new ElementRuntimeException("Could not find list to map to\n"
							+ "\t in " + block.toString() + "\n"
							+ "\t map using " + map);
				} else {
					popped = block.pop();
				}
			}
			
			block.push(map.mapTo(toList(popped)));
			
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
		final Object a = block.pop();
		
		if (isNumeric(a)) {
			block.push(toNumeric(a).bnot());
			return;
		}
		else if (isString(a)) {
			char[] chars = getString(a).toCharArray();
	        Arrays.sort(chars);
	        block.push(new String(chars));
			return;
		} 
		else if(isList(a)) {
			
			//Type validation
			boolean string = false;	//Will be true if there exists a string in the list
			boolean num = false;	//Will be true if there exists a number in the list
			
			//Numerical ranks
			//final byte i = 0; 		// Int
			final byte basic = 1; 		// Num
			final byte big = 2; 		// BigNum
			
			byte highestRank = 1;			//The highest ranking number type
			
			//The list to be sorted
			ArrayList<Object> list = toList(a);
			
			//Collect type data
			for (Object item : list) {
				if (isNumeric(item)) {
					num = true;
					//Are there any bigdecimals?
					if(highestRank < big && isBigNum(item)) {
						highestRank = big;
					}
				}
				else if (isString(item)) {
					string = true;
				}
				else {
					//Only strings and numbers can be sorted
					throw new TypeError(this,a);
				}
				if(string && num) {
					//Strings and numbers cannot be sorted together
					throw new TypeError(this,a);
				}
			} 
			
			
			//Based on the type, sort the list
			if(num) {
				switch(highestRank) {
				case basic:
					ArrayList<Num> nums = new ArrayList<Num>();
					for (Object item : list) {
						nums.add(Num.fromObj(item));
					}
					list.clear();
					Collections.sort(nums);
					for(Num item : nums) {
						list.add(item);
					}
					block.push(list);
					return;
				case big:
					ArrayList<BigNum> bigs = new ArrayList<BigNum>();
					for (Object item : list) {
						bigs.add(BigNum.fromObj(item));
					}
					list.clear();
					Collections.sort(bigs);
					for(BigNum item : bigs) {
						list.add(item);
					}
					block.push(list);
					return;
				}
			} else if(string) {
				ArrayList<String> strings = new ArrayList<String>();
				for (Object item : list) {
					strings.add(getString(item));
				}
				list.clear();
				Collections.sort(strings);
				for(String item : strings) {
					list.add(item);
				}
				block.push(list);
				return;
			}
		} 
		else if (isUserObject(a)) {
			toUserObject(a).callVariable(block, Ops.MV_DOLLAR);
			return;
		}
		
		throw new TypeError(this,a);
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
		final Object a = block.pop();
		final Object b = block.pop();
		
		if(bothNumeric(a,b)) {
			try {
				block.push(NumMath.mod(toNumeric(a),toNumeric(b)));
			} catch (ArithmeticException e) {
				throw new ElementRuntimeException("%: Divide by 0");
			}
		} else if (isNumeric(a) && isBlock(b)) {
			int repeats = toNumeric(a).toInt();
			Block blk = toBlock(b);
			for (int i = 0; i < repeats; i ++) {
				block.addAll(blk.getInstructions().getInstrucionList());
			}
			return;
		} else if (isUserObject(a)) {
			block.push(b);
			toUserObject(a).callVariable(block, Ops.MV_PERCENT);
			return;
		} else if (isUserObject(b)) {
			toUserObject(b).callVariable(block, Ops.MV_PERCENT, a);
			return;
		} else {
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
		final Object a = block.pop();
		final Object b = block.pop();
		
		if(bothBool(a,b)) {
			block.push(toBool(a) && toBool(b));
		} else if (bothNumeric(a,b)) {
			block.push(NumMath.band(toNumeric(a), toNumeric(b)));
		} else if (isString(a) && isString(b)) {
			ArrayList<Object> allMatches = new ArrayList<Object>();
			Matcher m = Pattern.compile(getString(a)).matcher(getString(b));
			while (m.find()) {
				 allMatches.add(m.group());
			}
			block.push(allMatches);
		} else if (isUserObject(a)) {
			block.push(b);
			toUserObject(a).callVariable(block, Ops.MV_AMP);
		} else if (isUserObject(b)) {
			toUserObject(b).callVariable(block, Ops.MV_AMP, a);
		} else {
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
		final Object a = block.pop();
		final Object b = block.pop();
		
		if(bothNumeric(a,b)) {
			block.push(NumMath.mul(toNumeric(a), toNumeric(b)));
		} else if (isUserObject(a)) {
			block.push(b);
			toUserObject(a).callVariable(block, Ops.MV_STAR);
		} else if (isUserObject(b)) {
			toUserObject(b).callVariable(block, Ops.MV_STAR, a);
		} else {	
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
		final Object a = block.pop();
		final Object b = block.pop();
		
		if(bothNumeric(a,b)) {
			block.push(NumMath.add(toNumeric(a), toNumeric(b)));
		} else if (isUserObject(a)) { //Must happen before anyString()
			block.push(b);
			toUserObject(a).callVariable(block, Ops.MV_PLUS);
		} else if (isUserObject(b)) { //Must happen before anyString()
			toUserObject(b).callVariable(block, Ops.MV_PLUS, a);
		} else if (anyString(a,b)) {
			//Must reverse order
			block.push(castString(b) + castString(a));
		} else if (isNumeric(a) && isChar(b)) {
			block.push((char) (toNumeric(a).toInt() + toChar(b)));
		} else if (isChar(a) && isNumeric(b)) {
			block.push(toChar(a) + toNumeric(b).toInt());
		} else if (bothChar(a, b)) {
			block.push((char)(toChar(a) + toChar(b)));
		} else {
			throw new TypeError(this, a,b);
		}
	}
}

//- - 44
class OP_Minus extends Operation {
	public OP_Minus() {
		this.name = "-";
		this.info = "<NN|CC|NC|CN> subtract\n(overloadable: minus)";
		this.argTypes = "NN|CC|NC|CN";
	}
	@Override public void execute(final Block block) {
		final Object b = block.pop();	//Pop in reverse order
		final Object a = block.pop();

		
		if(bothNumeric(a,b)) {
			block.push(NumMath.sub(toNumeric(a), toNumeric(b)));
		} else if (isNumeric(a) && isChar(b)) {
			block.push((char) (toNumeric(a).toInt() - toChar(b)));
		} else if (isChar(a) && isNumeric(b)) {
			block.push(toChar(a) - toNumeric(b).toInt());
		} else if (bothChar(a, b)) {
			block.push((char)(toChar(a) - toChar(b)));
		} else if (isUserObject(b)) {
			block.push(a);
			toUserObject(b).callVariable(block, Ops.MV_MINUS);
		} else if (isUserObject(a)) {
			toUserObject(a).callVariable(block, Ops.MV_MINUS, b);
		} else {
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
		final Object a = block.pop();
		final Object b = block.pop();
		
		if(bothNumeric(a,b)) {
			block.push(NumMath.div(toNumeric(a), toNumeric(b)));
		} else if (isUserObject(a)) {
			block.push(b);
			toUserObject(a).callVariable(block, Ops.MV_FSLASH);
		} else if (isUserObject(b)) {
			toUserObject(b).callVariable(block, Ops.MV_FSLASH, a);
		} else {
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
		final Object b = block.pop();			// Popped in Reverse Order
		final Object a = block.pop();
		
		
		
		if(bothNumeric(a,b)) {
			block.push(NumMath.compare(toNumeric(a), toNumeric(b)) < 0);
		} else if (bothChar(a,b)) {
			block.push(toChar(a) < toChar(b));
		} else if (bothString(a,b)) {
			block.push(getString(a).compareTo(getString(b)) < 0);
		} else if (isNumeric(b) && isString(a)) {
			int n = toNumeric(b).toInt();
			String str = getString(a);
			
			if (n <= str.length()) {
				block.push(str.substring(0, n));
			} else {
				int extra = n - str.length();
				char[] pad = new char[extra];
				Arrays.fill(pad, ' ');
				block.push(str + new String(pad));
			}
			
		} else if (isNumeric(b) && isList(a)) {
			ArrayList<Object> list = toList(a);
			int n = toNumeric(b).toInt();
			ArrayList<Object> out = new ArrayList<Object>(n);
			
			if (n <= list.size()) {
				for (int i = 0; i < n; i++) {
					out.add(list.get(i));
				}
			} else {
				out.addAll(list);
				for (int i = list.size(); i < n; i++) {
					out.add(0); //Pad with 0s
				}
			}
			
			block.push(out);

		} else {
			throw new TypeError(this, a,b);
		}
	}
}


//= - 61
class OP_Equal extends Operation {
	public OP_Equal() {
		this.name = "=";
		this.info = "equality comparison operator";
		this.argTypes = "NN|CC|SS|LL";
	}
	@Override
	public void execute(final Block block) {
		block.push(areEqual(block.pop(),block.pop()));
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
		final Object b = block.pop();			// Popped in Reverse Order
		final Object a = block.pop();
		
		if(bothNumeric(a,b)) {
			block.push(NumMath.compare(toNumeric(a), toNumeric(b)) > 0);
		} else if (bothChar(a,b)) {
			block.push(toChar(a) > toChar(b));
		} else if (bothString(a,b)) {
			block.push(getString(a).compareTo(getString(b)) > 0);
		} else if (isNumeric(b) && isString(a)) {
			int n = toNumeric(b).toInt();
			String str = getString(a);
			
			if (n <= str.length()) {
				block.push(str.substring(str.length()-n, str.length()));
			} else {
				int extra = n - str.length();
				char[] pad = new char[extra];
				Arrays.fill(pad, ' ');
				block.push(new String(pad) + str);
			}
			
		} else if (isNumeric(b) && isList(a)) {
			ArrayList<Object> list = toList(a);
			int n = toNumeric(b).toInt();
			ArrayList<Object> out = new ArrayList<Object>(n);
			
			if (n <= list.size()) {
				for (int i = list.size()-n; i < list.size(); i++) {
					out.add(list.get(i));
				}
			} else {
				for (int i = 0; i < n-list.size(); i++) {
					out.add(0); //Pad with 0s
				}
				out.addAll(list);
			}
			
			block.push(out);
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
		final Object a = block.pop();
		final Object b = block.pop();
		Object c = block.pop();
		
		if(isBool(a)) {
			//   c      b     a
			//{true} {false} cond
			
			if(toBool(a)) {
				//if the condition is false, use the second block
				c = b;
			}
			
			if(isBlock(c)) {
				block.addAll(toBlock(c).duplicate().getInstructions().getInstrucionList());
			} else {
				block.push(c);
			}
			return;
		} 
		
		throw new TypeError(this, a,b,c);
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
		final Object a = block.pop();
		final Object b = block.pop();
		Object c = block.pop();
		
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
		final ArrayList<Object> al = new ArrayList<Object>();
		al.add(block.pop());
		block.push(al);
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
		Object a = block.pop();
		
		if (isNumeric(a)) {
			block.push(NumMath.add(toNumeric(a), new Num(1)));
		} else if (isChar(a)) {
			block.push(toChar(a) + 1);
		} else if (isList(a)) {
			ArrayList<Object> l = toList(a);
			Object popped = l.remove(l.size()-1);
			block.push(l.clone()); //Keep list on stack
			block.push(popped);
		} else {
			throw new TypeError(this, a);
		}
	}
}

////C - 67
//class OP_C extends Operation {
//	public OP_C() {
//		this.name = "C";
//		this.info = "cast a to type c";
//		this.argTypes = "AC";
//	}
//	@Override public void execute (final Block block) {
//		final Object objtype = block.pop();
//		Object item = block.pop();
//		
//		if(isChar(objtype)) {
//			byte type = abbrvToID(toChar(objtype));
//			item = castGeneral(type, item);
//			block.push(item);
//			return;
//		}
//		
//		
//		throw new TypeError(this, objtype, item);
//	}
//
//}

//D - 68
class OP_D extends Operation {
	public OP_D() {
		this.name = "D";
		this.info = "ALI set index\n(overloadable: setindex)";
		this.argTypes = "ALI|AUI";
	}
	@Override public void execute (final Block block) {
		final Object a = block.pop();   //Index
		Object b = block.pop();			//List
		final Object o = block.pop();	//Item
		
		if(isNumeric(a) && isList(b)) {
			toList(b).set(toNumeric(a).toInt(), o);
		} else if (isUserObject(b)) {
			toUserObject(b).callVariable(block, Ops.MV_SETINDEX, toNumeric(a).toInt());
		} else {		
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
		Object n = block.pop();
		
		if (isNumeric(n)) {
			block.push(NumMath.pow(new Num(10), toNumeric(n)));
		} else if (isList(n)) {
			block.push(new Num(length(n)));
		} else if (isUserObject(n)) {
			toUserObject(n).callVariable(block, Ops.MV_LEN);
		} else {
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
		final Object a = block.pop();
		
		
		
		if(isString(a)) {
			String name = getString(a);
			
			if(Ops.PATTERN_URL.matcher(name).matches()) {
				Scanner scnr = null;
				try {
					URL url = new URL(name);
					scnr = new Scanner(url.openStream());
					StringBuilder sb = new StringBuilder();
					
					while(scnr.hasNext()) {
						sb.append(scnr.nextLine()).append('\n');
					}
					block.push(sb.toString());
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
					block.push(sb.toString());
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
		} else if (isNumeric(a)) {
			block.push(toNumeric(a).isPrime());
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
		final Object to_b = block.pop();
		final Object from_b = block.pop();
		final Object num = block.pop();
		
		try {
			if (bothNumeric(from_b, to_b)) {
				int from_base = toNumeric(from_b).toInt();
				int to_base = toNumeric(to_b).toInt();
				BigInteger out_bi;
				
				//Check Radix Ranges
				if (Character.MIN_RADIX > from_base 
						|| Character.MIN_RADIX > to_base
						|| Character.MAX_RADIX < from_base
						|| Character.MAX_RADIX < to_base) {
					throw new ElementRuntimeException("H: base out of range (" + from_base + ", " + to_base + ")");
				}
				
				//String
				if(isString(num)) {
					out_bi  = new BigInteger(getString(num), from_base);
					
				}
				
				//Always base ten
				else if(isNumeric(num)) {
					out_bi = toNumeric(num).toApfloat().floor().toBigInteger();
				} 
				
				//Assume base 2
				else if (isList(num)) {
					ArrayList<Object> bin_list = toList(num);
					StringBuilder sb = new StringBuilder(bin_list.size());
	
						for (Object o : bin_list) {
							int c = toNumeric(o).toInt();
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
					ArrayList<Object> out_list = new ArrayList<Object>(bin_str.length());
					
					for (char c : bin_str.toCharArray()) {
						out_list.add(new Num(c-'0'));
					}
					block.push(out_list);
					return;
				} else {
					block.push(out_bi.toString(to_base));
					return;
				}
			}
		}
		catch (NumberFormatException nfe) {
			throw new ElementRuntimeException("H: invalid number format (" 
					+ castString(num) + ", " + castString(from_b) + ", " + castString(to_b) + ")");
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
		Object index = block.pop();
		final Object list = block.pop();
		
		if (isUserObject(list)) {
			toUserObject(list).callVariable(block, Ops.MV_INDEX, index);
			return;
		}
		
		//Normal list execution
		
		if(!isList(list)) {
			throw new TypeError(this, index, list);
		}
		
		if(isNumeric(index)) {
			int i = toNumeric(index).toInt();
			//Negative numbers index from the back of the list starting at -1

			if(i < 0) {
				i = length(list)-(-1*i);
			}
			block.push(toList(list).get(i));
		} else if (isList(index)) {
			ArrayList<Object> indexList = toList(index);
			ArrayList<Object> refList = toList(list);
			for(int i = 0; i < length(index); i++) {
				if(isNumeric(indexList.get(i))) {
					int j = toNumeric(indexList.get(i)).toInt();
					//Negative numbers index from the back of the list starting at -1
					if(j < 0) {
						j = refList.size()-(-1*j);
					}
					indexList.set(i, refList.get(toNumeric(j).toInt()));
				} else {
					throw new TypeError("I", "list of ints", indexList);
				}
			}
			block.push(indexList);
		} else if (isBlock(index)) {
			block.push(toBlock(index).filter(toList(list)));
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
		Object n = block.pop();
		Object item = block.pop();
		if(isNumeric(n)) {
			int repeats = toNumeric(n).toInt();
			if(repeats < 0) {
				throw new ElementRuntimeException("Cannot create list with negative number of elements");
			}
			ArrayList<Object> list = new ArrayList<Object>(repeats);
			if(isList(item)) {
				for(int i = 0; i < repeats; i++) {
					list.add(toList(item).clone());
				}				
			} else {
				for(int i = 0; i < repeats; i++) {
					list.add(item);
				}
			}
			block.push(list);
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
		final Object a = block.pop();
		final Object b = block.pop();
		int a_type = getTypeID(a);
		int b_type = getTypeID(b);
		
		// Zero length list is a string, we want it to behave as a list
		if(a_type == STRING && length(a)==0) {a_type = LIST;}
		if(b_type == STRING && length(b)==0) {b_type = LIST;}
		
		if ((a_type == LIST && b_type == LIST) || (a_type == STRING && b_type == STRING)) {
			final ArrayList<Object> bl = toList(b);
			bl.addAll(toList(a));
			block.push(bl);
			return;
		} else if (a_type == LIST || a_type == STRING){//&& !isString(a)) {
			final ArrayList<Object> al = toList(a);
			al.add(0,b);
			block.push(al);
			return;
		} else if (b_type == LIST || b_type == STRING){//&& !isString(b)) {
			final ArrayList<Object> bl = toList(b);
			bl.add(a);
			block.push(bl);
			return;
		} else {
			final ArrayList<Object> list = new ArrayList<Object>(2);
			list.add(b);	//Stack - Add in reverse order
			list.add(a);
			block.push(list);
			return;
		}
	}
}

//N - 78
class OP_N extends Operation {
	public OP_N() {
		this.name = "N";
		this.info = "L<A> returns the index of the first occurance of A in L. returns -1 if not found";
		this.argTypes = "LA";
	}
	@Override public void execute (final Block block) {
		final Object a = block.pop(); //Item
		final Object b = block.pop(); //List
		
		int index = 0;
		if(isList(b)) {
			for (Object o : toList(b)) {
				if (areEqual(o, a)) {
					block.push(new Num(index));
					return;
				}
				index++;
			}
			block.push(new Num(-1));
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
		block.push(castString(block.pop()));
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
		final Object a = block.pop();
		
		if(isNumeric(a)) {
			int i = toNumeric(a).toInt();
			if (i > 0) {
				block.push(new Num(Ops.RAND.nextInt(i)));
			} else if (i < 0) {
				block.push(new Num(-1 * Ops.RAND.nextInt(i*-1)));
			} else {
				block.push(new Num(Ops.RAND.nextInt()));
			}
		} else if (isUserObject(a)) {
			toUserObject(a).callVariable(block, Ops.MV_RANDCHOICE);
		} else if (isList(a)) {
			ArrayList<Object> list = toList(a);
			int ix = Ops.RAND.nextInt(list.size());
			block.push(toList(a).get(ix));
		} else {
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
		final Object a = block.pop();
		if(isList(a)) {
			block.push(ListBuilder.buildRange(toList(a)));
		} else if (isNum(a)) {
			block.push(ListBuilder.buildRange(toNum(a)));
		} else if (isBigNum(a)) {
			block.push(ListBuilder.buildRange(toBigNum(a)));
		} else if (isChar(a)) {
			block.push(ListBuilder.buildRange(toChar(a)));
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
		final Object a = block.pop();
		if(isList(a)) {
			Object[] list = toList(a).toArray(new Object[length(a)]);
			if(list.length == 0) {
				block.push(0);
				return;
			}
			//Push all but the last item
			for(int i = list.length-1; i > 0; i--) {
				block.add(Ops.getOp('+'));
				block.add(list[i]);
			}
			//Push the last element outside the loop so that there is not an extra plus (1 1+2+3+)
			block.add(list[0]); 
			return;		
		}
		throw new TypeError(this, a);
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
		Object a = block.pop();
		Object b = block.pop();
		
		
		if(isBlock(a) && isList(b)) {
			int length = length(b);
			if(length == 0) {
				block.push(0);
				return;
			}
			
			Block foldBlock = toBlock(a);
			ArrayList<Object> list = toList(b);
			
			//Push all but the last item
			//for(int i = 0; i < list.size()-1; i++) {
			for(int i = length-1; i > 0; i--) {
				block.addAll(foldBlock.getInstructions().getInstrucionList());
				block.add(list.get(i));
			}
			//Push the last element outside the loop so that there is not an extra plus (1 1+2+3+)
			//block.add(list.get(list.size()-1));
			block.add(list.get(0));
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
		Object a = block.pop();
		
		if (isNumeric(a)) {
			block.push(NumMath.sub(toNumeric(a), new Num(1)));
		} else if (isChar(a)) {
			block.push(toChar(a) - 1);
		} else if (isList(a)) {
			ArrayList<Object> l = toList(a);
			Object popped = l.remove(0);
			block.push(l.clone()); //Keep list on stack
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
		final Object a = block.pop();
		
		if(isBlock(a)) {
			Block blk = toBlock(a).duplicate();
			Block state = new Block();
			state.setStack((Stack<Object>)block.getStack().clone());
			
			boolean condition = false;
			
			do {
				state.addAll(blk.getInstructions().getInstrucionList());
				state.eval();
				
				final Object cond = state.pop();
				
				if(isBool(cond)) {
					condition = toBool(cond);
				} else if (isNumeric(cond)) {
					condition = toNumeric(cond).toInt() != 0;
				} else {
					throw new TypeError("While","condition must be a boolean or a number",cond);
				}
			
			} while (condition);
			
			//Merge the stack
			block.setStack(state.getStack());
			
			return;
		} else if(isModule(a)) {
			Module m = toModule(a);
			Element.getInstance().getVars().getGlobals().merge(m.getVarSet());
			return;
		}
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
		Object a = block.pop();
		
		if (isNumeric(a)) {
			block.push(BigNum.fromObj(a));
		} else if (isString(a)) {
			try	{
				block.push(new BigNum(new Apfloat(castString(a))));
			} catch (NumberFormatException e) {
				throw new ElementRuntimeException("Cannot cast " + castString(a) + " to bignum");
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
		final Object a = block.pop();
		final Object b = block.pop();
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
		final Object a = block.pop();
		final Object b = block.pop();
		
		if(bothNumeric(a,b)){
			//Raise b to the ath power
			block.push(NumMath.pow(toNumeric(b), toNumeric(a)));
		} else if (bothString(a,b)) {
			block.push(ElementString.levenshteinDistance(getString(a), getString(b)));
		} else if (isUserObject(a)) {
			block.push(b);
			toUserObject(a).callVariable(block, Ops.MV_POW);
		} else if (isUserObject(b)) {
			toUserObject(b).callVariable(block, Ops.MV_POW, a);
		} else {
			throw new TypeError(this, a, b);
		}

	}
}

// _ - 95
class OP_Underscore extends Operation {
	public OP_Underscore() {
		this.name = "_";
		this.info = "duplicates the top item on the stack";
		this.argTypes = "A";
	}
	@Override public void execute (final Block block) {
		final Object a = block.peek();
		if(a instanceof ArrayList) {
			block.push(toList(a).clone());
		} else {
			block.push(a);
		}
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
		final Object a = block.pop();
		final Object b = block.pop();
		
		//Bitwise or
		if (bothNumeric(a,b)) {
			block.push(NumMath.bor(toNumeric(a), toNumeric(b)));
		}
		//Logical or
		else if(bothBool(a,b)) {
			block.push(toBool(a) || toBool(b));
		}
		// find regex matches
		else if (isString(a) && isString(b)) {
			String[] list = getString(b).split(getString(a));
			ArrayList<Object> res = new ArrayList<Object>(list.length);
			for(String s : list) {
				res.add(s);
			}
			block.push(res);
		} else if (isNumeric(a) && isList(b)) {
			int index = toNumeric(a).toInt();
			ArrayList<Object> l = toList(b);
			if(index >= l.size() || index*-1 >= l.size()){
				block.push(l);
			} else if (index > 0) {
				block.push(new ArrayList<Object>(l.subList(0, index)));
				block.push(new ArrayList<Object>(l.subList(index, l.size())));
			} else if (index < 0) {
				block.push(new ArrayList<Object>(l.subList(0, l.size()+index)));
				block.push(new ArrayList<Object>(l.subList(l.size()+index, l.size())));
			} else if (index == 0) {
				for (int i = 0; i < l.size(); i++) {
					block.push(new ArrayList<Object>(l.subList(i, i+1)));
				}
			} else {
				throw new TypeError(this, a, b);
			}
		}
		//User object
		else if (isUserObject(a)) {
			block.push(b);
			toUserObject(a).callVariable(block, Ops.MV_BAR);
		} else if (isUserObject(b)) {
			toUserObject(b).callVariable(block, Ops.MV_BAR, a);
		} 
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
		final Object a = block.pop();
		
		if(isBlock(a)) {
			block.addAll(toBlock(a).getInstructions().getInstrucionList());
			return;
		} else if (isString(a)) {
			block.addAll(Parser.compile(getString(a), Element.getInstance()).getInstructions().getInstrucionList());
			return;
		} else if (isChar(a)) {
			final char c = toChar(a);
			final String varname = CharacterParser.getName(c);
			if(varname == null) {
				throw new ElementRuntimeException("Character '" + c + " is not a valid variable");
			}
			block.add(new Variable(varname));
			return;
		} else if (isList(a)) {
			ArrayList<Object> list = toList(a);
			//Collections.reverse(list);
			for (int i = list.size()-1; i >= 0; i--) {
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
		final Object a = block.pop();
		final Object b = block.pop();
		
		//Apply block to an index in the list
		if(isBlock(a) && isNumeric(b)) {
			Object c = block.pop();
			if(!isList(c)) {
				throw new TypeError(this, a,b,c);
			}
			Block blk = new Block();
			blk.addAll(toBlock(a).getInstructions().getInstrucionList());
			int index = toNumeric(b).toInt();
			
			//Negative numbers index from the back of the list starting at -1
			if(index < 0) {
				index = length(c)-(-1*index);
			}
			
			//index = index==-1 ? toList(c).size()-1 : index; //Last index if index == -1
			blk.push(toList(c).get(index));
			blk.eval();
			c = toList(c); //In case it is a string
			toList(c).set(index, blk.pop());
			
			block.push(c);
		} else if (isBlock(a) && isList(b)) {
			block.push(toBlock(a).mapTo(toList(b)));
		} else {
			throw new TypeError(this, a,b);
		}
	}
}


