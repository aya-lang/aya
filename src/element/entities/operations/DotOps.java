package element.entities.operations;

import static element.ElemTypes.IDToAbbrv;
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
import static element.ElemTypes.isNumeric;
import static element.ElemTypes.isString;
import static element.ElemTypes.length;
import static element.ElemTypes.printBare;
import static element.ElemTypes.toBlock;
import static element.ElemTypes.toBool;
import static element.ElemTypes.toNumeric;
import static element.ElemTypes.toChar;
import static element.ElemTypes.toList;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Stack;

import element.ElemPrefs;
import element.Element;
import element.entities.Block;
import element.entities.ListBuilder;
import element.entities.Operation;
import element.entities.number.Num;
import element.entities.number.NumMath;
import element.exceptions.ElementRuntimeException;
import element.exceptions.ElementUserRuntimeException;
import element.exceptions.SyntaxError;
import element.exceptions.TypeError;
import element.parser.CharacterParser;
import element.parser.Parser;
import element.util.QuickDialog;
import element.util.SimplePlot;
import element.variable.Variable;

public class DotOps {	
	
	public static final char FIRST_OP = '!';

	
	/** A list of all valid single character operations. 
	 *  Stored in final array for fast lookup.
	 *  Array indexes are always [(operator character) - FIRST_OP]
	 */
	public static Operation[] DOT_OPS = {
		/* 33 !  */ new OP_Dot_Bang(),
		/* 34 "  */ null,
		/* 35 #  */ null, //Comment
		/* 36 $  */ new OP_Dot_SortUsing(),
		/* 37 %  */ null,
		/* 38 &  */ null,
		/* 39 '  */ null,
		/* 40 (  */ null,
		/* 41 )  */ null,
		/* 42 *  */ null,
		/* 43 +  */ new OP_Dot_Plus(),
		/* 44 ,  */ null,
		/* 45 -  */ new OP_Dot_Minus(),
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
		/* 58    */ null,
		/* 59 ;  */ new OP_Dot_ClearAll(),
		/* 60 <  */ new OP_Dot_LessThan(),
		/* 61 =  */ null,
		/* 62 >  */ new OP_Dot_GreaterThan(),
		/* 63 ?  */ new OP_Dot_Conditional(),
		/* 64 @  */ null,
		/* 65 A  */ new OP_Dot_ArrayAll(),
		/* 66 B  */ new OP_Dot_Primes(),
		/* 67 C  */ null,
		/* 68 D  */ new OP_Dot_Error(),
		/* 69 E  */ new OP_Dot_Len(),
		/* 70 F  */ null,
		/* 71 G  */ new OP_Dot_Write(),
		/* 72 H  */ null,
		/* 73 I  */ new OP_Dot_I(),
		/* 74 J  */ null,
		/* 75 K  */ new OP_Dot_TryCatch(),
		/* 76 L  */ null,
		/* 77 M  */ null,
		/* 78 N  */ null,
		/* 79 O  */ null,
		/* 80 P  */ new OP_Dot_Print(),
		/* 81 Q  */ new OP_Dot_Rand(),
		/* 82 R  */ null,
		/* 83 S  */ new OP_Dot_Case(),
		/* 84 T  */ new OP_TypeOf(),
		/* 85 U  */ new OP_RequestString(),
		/* 86 V  */ null,
		/* 87 W  */ null,
		/* 88 X  */ new OP_SimplePlot(),
		/* 89 Y  */ null,
		/* 90 Z  */ new OP_Dot_Zed(),
		/* 91 [  */ null,
		/* 92 \  */ null,
		/* 93 ]  */ null,
		/* 94 ^  */ null,
		/* 95 _  */ null,
		/* 96 `  */ null,
		/* 97 a  */ null,
		/* 98 b  */ null,
		/* 99 c  */ null, //new OP_Dot_Case(), (now .S)
		/* 100 d */ null,
		/* 101 e */ null, //new OP_Dot_Error(), (Now .E)
		/* 102 f */ null,
		/* 103 g */ null,
		/* 104 h */ null,
		/* 105 i */ null,
		/* 106 j */ null,
		/* 107 k */ null,
		/* 108 l */ null,
		/* 109 m */ null,
		/* 110 n */ null,
		/* 111 o */ null,
		/* 112 p */ null, //new OP_SimplePlot(), (now .X)
		/* 113 q */ null, //new OP_RequestString(), (now .U)
		/* 114 r */ null,
		/* 115 s */ null,
		/* 116 t */ null, //new OP_TypeOf(),  (now .T)
		/* 117 u */ null,
		/* 118 v */ null,
		/* 119 w */ null,
		/* 120 x */ null,
		/* 121 y */ null,
		/* 122 z */ null,
		/* 123 { */ null,
		/* 124 | */ null,
		/* 125 } */ null,
		/* 126 ~ */ new OP_Dot_Tilde(),
	};
	
	
	/** Returns a list of all the op descriptions **/
	public static ArrayList<String> getAllOpDescriptions() {
		ArrayList<String> out = new ArrayList<String>();
		for (char i = 0; i <= 126-Ops.FIRST_OP; i++) {
			if(DOT_OPS[i] != null) {
				out.add(DOT_OPS[i].name + " (" + DOT_OPS[i].argTypes + ")\n" + DOT_OPS[i].info + "\n(extended operator)");
			}
		}
		return out;
	}
	
	
	/** Returns the operation bound to the character */
	public static Operation getOp(char op) {
		if(op >= 33 && op <= 126) {
			return DOT_OPS[op-FIRST_OP];
		} else {
			throw new SyntaxError("Dot operator '." + op + "' does not exist");
		}
	}
	
}

//! - 33
class OP_Dot_Bang extends Operation {
	public OP_Dot_Bang() {
		this.name = ".!";
		this.info = "N signnum\nS parse if number";
		this.argTypes = "NS";
	}
	@Override public void execute(final Block block) {
		Object o = block.pop();
		
		if (isNumeric(o)) {
			block.push(toNumeric(o).signnum());
		} else if (isString(o)) {
			String numStr = getString(o).trim();
			try {
				block.push(Integer.parseInt(numStr));
			} catch (NumberFormatException e) {
				try {
					block.push(new BigDecimal(numStr));
				} catch (NumberFormatException e2) {
					block.push(o);
				}
			}
		} else {
			throw new TypeError(this,o);
		}
	}
}

//$ - 36
class OP_Dot_SortUsing extends Operation {
	public OP_Dot_SortUsing() {
		this.name = ".$";
		this.info = "LE sort using the expresion E";
		this.argTypes = "LE";
	}
	
	@Override public void execute(Block block) {
		Object a = block.pop();
		Object b = block.pop();
		
		if (isBlock(a) && isList(b)) {
			final Block blk = toBlock(a).duplicate();
			ArrayList<Object> objs = toList(b);
			ArrayList<Object> key_obj = blk.mapTo(objs);
			
			//Convert keys to int array
			ArrayList<SUItem> items = new ArrayList<SUItem>(key_obj.size());
			for (int i = 0; i < objs.size(); i++) {
				items.add(new SUItem(objs.get(i), toNumeric(key_obj.get(i)).toDouble()));
			}
			
			Collections.sort(items);
						
			ArrayList<Object> out = new ArrayList<Object>(items.size());
			for (SUItem i : items) {
				out.add(i.o);
			}
			
			block.push(out);
			
		} else {
			throw new TypeError(this, a);
		}
	}
	
	class SUItem implements Comparable<SUItem>{
		public Object o;
		public double d;
		public SUItem(Object o, double d) {
			this.o = o;
			this.d = d;
		}
		public int compareTo(SUItem i) {
			//Ascending
			return (int)((this.d - i.d)*10000); 
		}
	}
	
	
}


//// ( - 40
//class OP_Dot_OpenParen extends Operation {
//	public OP_Dot_OpenParen() {
//		this.name = ".(";
//		this.info = "<N|C> decrement\n<L> uncons from back";
//		this.argTypes = "N|C|L";
//	}
//	@Override
//	public void execute(Block block) {
//		Object a = block.pop();
//		
//		if(isNum(a) || isChar(a)) {
//			block.add(Ops.OPS['-'-'!']);
//			block.add(1);
//			block.add(a);
//			return;
//		} else if (isList(a)) {
//			a = toList(a);
//			toList(a).remove(0);
//			block.push(a);
//			return;
//		}
//		throw new TypeError(this, a);
//	}
//}

////) - 41
//class OP_Dot_CloseParen extends Operation {
//	public OP_Dot_CloseParen() {
//		this.name = ".)";
//		this.info = "<N|C> increment\n<L> uncons from front";
//		this.argTypes = "N|C|L";
//	}
//	@Override
//	public void execute(Block block) {
//		Object a = block.pop();
//		
//		if(isNum(a) || isChar(a)) {
//			block.add(Ops.OPS['+'-'!']);
//			block.add(1);
//			block.add(a);
//			return;
//		} else if (isList(a)) {
//			a = toList(a);
//			toList(a).remove(length(a)-1);
//			block.push(a);
//			return;
//		}
//		throw new TypeError(this, a);
//	}
//}

//+ - 43
class OP_Dot_Plus extends Operation {
	public OP_Dot_Plus() {
		this.name = ".+";
		this.info = "NN gcd";
		this.argTypes = "NN";
	}
	@Override
	public void execute(Block block) {
		Object a = block.pop();
		Object b = block.pop();
		
		if (bothNumeric(a, b)) {
			NumMath.gcd(toNumeric(a), toNumeric(b));	
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

//- - 45
class OP_Dot_Minus extends Operation {
	public OP_Dot_Minus() {
		this.name = ".-";
		this.info = "NN lcm";
		this.argTypes = "NN";
	}
	@Override
	public void execute(Block block) {
		Object a = block.pop();
		Object b = block.pop();
		
		if (bothNumeric(a, b)) {
			NumMath.lcm(toNumeric(a), toNumeric(b));	
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

//; - 59
class OP_Dot_ClearAll extends Operation {
	public OP_Dot_ClearAll() {
		this.name = ".;";
		this.info = "clears the entire stack";
		this.argTypes = "A";
	}
	@Override
	public void execute(Block block) {
		block.clearStack();
	}
}

// < - 60
class OP_Dot_LessThan extends Operation {
	public OP_Dot_LessThan() {
		this.name = ".<";
		this.info = "less than or equal to comparison operator";
		this.argTypes = "NN|CC|SS";
	}
	@Override
	public void execute(Block block) {
		Object b = block.pop();			// Popped in Reverse Order
		Object a = block.pop();
		
		if (bothNumeric(a, b)) {
			block.push(NumMath.compare(toNumeric(a),toNumeric(b)) <= 0); 
		} else if (bothChar(a,b)) {
			block.push(toChar(a) <= toChar(b));
		} else if (bothString(a,b)) {
			block.push(getString(a).compareTo(getString(b)) <= 0);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}


// < - 62
class OP_Dot_GreaterThan extends Operation {
	public OP_Dot_GreaterThan() {
		this.name = ".>";
		this.info = "greater than or equal to comparison operator";
		this.argTypes = "NN|CC|SS";
	}
	@Override
	public void execute(Block block) {
		Object b = block.pop();			// Popped in Reverse Order
		Object a = block.pop();
		
		if(bothNumeric(a,b)) {
			block.push(NumMath.compare(toNumeric(a),toNumeric(b)) >= 0);
		} else if (bothChar(a,b)) {
			block.push(toChar(a) >= toChar(b));
		} else if (bothString(a,b)) {
			block.push(getString(a).compareTo(getString(b)) >= 0);
		} else {
			throw new TypeError(this, a, b);
		}
		
	}
}

// ? - 63
class OP_Dot_Conditional extends Operation {
	public OP_Dot_Conditional() {
		this.name = ".?";
		this.info = "<AB> conditional operator. if the first argument if true, do nothing. if false, pop the second";
		this.argTypes = "AB";
	}
	
	@Override
	public void execute(final Block block) {
		final Object a = block.pop();
		if(isBool(a)) {
			if (!toBool(a)) {
				block.pop();
			}
			return;
		} 
		throw new TypeError(this, a);
	}
}


//A - 64
class OP_Dot_ArrayAll extends Operation {
	public OP_Dot_ArrayAll() {
		this.name = ".A";
		this.info = "wrap the entire stack in a list";
		this.argTypes = "A";
	}
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Block block) {
		ArrayList<Object> list = new ArrayList<Object>();
		list.addAll((Stack<Object>)block.getStack().clone());
		block.clearStack();	
		block.push(list);
	}
}

//B - 65
class OP_Dot_Primes extends Operation {
	public OP_Dot_Primes() {
		this.name = ".B";
		this.info = "N list all primes up to N";
		this.argTypes = "N";
	}

	@Override
	public void execute(Block block) {
		Object a = block.pop();
		if (isNumeric(a)) {
			int i = toNumeric(a).toInt();
			if (i < 0) {
				throw new ElementRuntimeException(".B: Input must be positive");
			}
			int[] primes = ElementMath.primes(i);
			block.push(ListBuilder.arrToAL(primes));
		} else {
			throw new TypeError(this, a);
		}
	}
}

//D - 67
class OP_Dot_Error extends Operation {
	public OP_Dot_Error() {
		this.name = ".D";
		this.info = "interrupts the program and throws an error message";
		this.argTypes = "S";
	}
	@Override public void execute (Block block) {
		Object a = block.pop();
		
		if(isString(a)) {
			throw new ElementUserRuntimeException(getString(a));
		}
		
		throw new TypeError(this, a);
	}
}

//E - 68
class OP_Dot_Len extends Operation {
	public OP_Dot_Len() {
		this.name = ".E";
		this.info = "return length and keep item on the stack";
		this.argTypes = "A";
	}
	@Override
	public void execute(Block block) {
		final Object n = block.pop();
		
		if (isList(n)) {
			block.push(n);
			block.push(new Num(length(n)));
			return;
		}
		throw new TypeError(this, n);
	}
}



//




// G - 71
class OP_Dot_Write extends Operation {
	public OP_Dot_Write() {
		this.name = ".G";
		this.info = "write A as a string to file located at S. N = 0, overwrite. N = 1, append";
		this.argTypes = "ASN";
	}
	@Override
	public void execute(Block block) {
		final Object n = block.pop();
		final Object s = block.pop();
		final Object a = block.pop();
		
		if (isString(s) && isNumeric(n)) {
			final int option = toNumeric(n).toInt();
			final String filename = castString(s);
			final String write = castString(a);
			final String fstr = ElemPrefs.getWorkingDir()+filename;

			
			if(option == 0) {
				try {
				    Files.write(Paths.get(fstr), write.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				}catch (IOException e) {
				    throw new ElementRuntimeException("Cannot open file '" + fstr + "'");
				} catch (InvalidPathException ipe) {
					throw new ElementRuntimeException("Cannot open file '" + fstr + "'");
				}
			} 
			
			else if (option == 1) {
				try {
				    Files.write(Paths.get(fstr), write.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				}catch (IOException e) {
				    throw new ElementRuntimeException("Cannot open file '" + fstr + "'");
				}  catch (InvalidPathException ipe) {
					throw new ElementRuntimeException("Cannot open file '" + fstr + "'");
				}
			} 
			
			else {
			    throw new ElementRuntimeException(".U: Option " + option + "is not valid. Please use 0 for overwrite and 1 for append");
			}
			return;
		}
		throw new TypeError(this, n);
	}
}

//I - 73
//NOTE: If updating this operator, also update I
class OP_Dot_I extends Operation {
	public OP_Dot_I() {
		this.name = "I";
		this.info = "index, filter";
		this.argTypes = "LL|LI|LE";
	}
	@Override public void execute (final Block block) {
		Object index = block.pop();
		final Object list = block.pop();
		block.push(list); //.I keeps the list on the stack
		
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
			return;
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
			return;
		} else if (isBlock(index)) {
			block.push(toBlock(index).filter(toList(list)));
			return;
		}
		
		
		throw new TypeError(this, index, list);
	}
}

//K - 75
class OP_Dot_TryCatch extends Operation {
	public OP_Dot_TryCatch() {
		this.name = ".K";
		this.info = "try B1, if error, execute B2. Neither block has access to the global stack";
		this.argTypes = "EE";
	}
	@Override public void execute (Block block) {
		Object catchBlock = block.pop();
		Object tryBlock = block.pop();
		
		if(isBlock(tryBlock) && isBlock(catchBlock)) {
			try {
				Block b = toBlock(tryBlock).duplicate();
				b.eval();
				block.appendToStack(b.getStack());
			} catch (Exception e) {
				Block b = toBlock(catchBlock).duplicate();
				b.eval();
				block.appendToStack(b.getStack());
			}
		}
	}
}

//P - 80
class OP_Dot_Print extends Operation {
	public OP_Dot_Print() {
		this.name = ".P";
		this.info = "prints the value on the top of the stack to stdout";
		this.argTypes = "A";
	}
	@Override public void execute (Block block) {
		Element.getInstance().getOut().printAsPrint(printBare(block.pop()));
	}
}

//Q - 81
class OP_Dot_Rand extends Operation {
	public OP_Dot_Rand() {
		this.name = ".Q";
		this.info = "returns a random decimal from 0 to 1";
		this.argTypes = "";
	}
	@Override public void execute (Block block) {
		block.push(new Num(Ops.RAND.nextDouble()));
	}
}

//S - 83
class OP_Dot_Case extends Operation {
	public OP_Dot_Case() {
		this.name = ".S";
		this.info = "returns the first element of a list. if the first element is a block, push its contents to the instruction stack";
		this.argTypes = "L";
	}
	@Override public void execute (Block block) {
		Object a = block.pop();
		
		//Return the first element of a list
		//If block, dump it
		if(isList(a)) {
			if(length(a) == 0)
				throw new ElementRuntimeException(this.name + ": list contains no elements");
			
			Object item = toList(a).get(0);
			if(isBlock(item)) {
				block.addAll(toBlock(item).getInstructions().getInstrucionList());
			} else {
				block.push(item);
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

//T - 84
class OP_TypeOf extends Operation {
	public OP_TypeOf() {
		this.name = ".T";
		this.info = "pushes a character ID of the argument to the stack";
		this.argTypes = "A";
	}
	@Override
	public void execute(Block block) {
		block.push(IDToAbbrv(getTypeID(block.pop())));
	}
}

//U - 85
class OP_RequestString extends Operation {
	public OP_RequestString() {
		this.name = ".U";
		this.info = "requests a string using a ui dialog. S is the dialiog header";
		this.argTypes = "S";
	}
	@Override
	public void execute(Block block) {
		
		block.push(QuickDialog.requestString(castString(block.pop())));

	}
}



//X - 88
class OP_SimplePlot extends Operation {
	public OP_SimplePlot() {
		this.name = ".X";
		this.info = "plot operator. plots a list of numbers to a basic graph and saves the image in the plots folder";
		this.argTypes = "L";
	}
	@Override
	public void execute(Block block) {
		Object a = block.pop();
		
		
		if(isList(a)) {
			int len = length(a);
			double[] data = new double[len];
			ArrayList<Object> list = toList(a);
			
			for(int i = 0; i < len; i++) {
				data[i] = toNumeric(list.get(i)).toDouble();
			}
			
			SimplePlot sp = new SimplePlot(data);
			
			//Create folder if needed
			File file = new File("plots\\");
			if(!file.exists()) {
				file.mkdir();
			}
			
			//Save the file
			try {
				sp.save(new File("plots\\" + getCurrentTimeStamp()+".png"));
			} catch (IOException e) {
				//Do Nothing
			}
			sp.show();
			return;
		}
		
		throw new TypeError(this, a);
	}
	
	public static String getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HHmmss");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
}



//Z - 90
class OP_Dot_Zed extends Operation {
	public OP_Dot_Zed() {
		this.name = ".Z";
		this.info = "dereferences a string as variable";
		this.argTypes = "S";
	}
	@Override public void execute (Block block) {
		Object s = block.pop();
		
		if(isString(s)) {
			String str = castString(s);
			if(str.contains(".")) {
				throw new ElementRuntimeException(".Z: Cannot look up module variables");
			}
			Variable v = new Variable(str);
			block.push(Element.getInstance().getVars().getVar(v));
			return;
		}
		throw new TypeError(this, s);
	}
}



//~ - 126
class OP_Dot_Tilde extends Operation {
	public OP_Dot_Tilde() {
		this.name = ".~";
		this.info = "parse a string or character to a block";
		this.argTypes = "S|C";
	}
	@Override
	public void execute(final Block block) {
		final Object a = block.pop();
		
		if (isString(a)) {
			block.add(Parser.compile(getString(a), Element.getInstance()));
			return;
		} else if (isChar(a)) {
			final char c = toChar(a);
			final String varname = CharacterParser.getName(c);
			if(varname == null) {
				throw new ElementRuntimeException("Character '" + c + " is not a valid variable");
			}
			Block b = new Block();
			b.add(new Variable(varname));
			block.add(b);
			return;
		}
		
		throw new TypeError(this, a);
	}
}


