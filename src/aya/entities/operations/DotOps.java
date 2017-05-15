package aya.entities.operations;

import static aya.obj.Obj.BLOCK;
import static aya.obj.Obj.BIGNUM;
import static aya.obj.Obj.CHAR;
import static aya.obj.Obj.DICT;
import static aya.obj.Obj.LIST;
import static aya.obj.Obj.NUM;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.NUMBERLIST;
import static aya.obj.Obj.OBJLIST;
import static aya.obj.Obj.STR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Stack;

import org.apfloat.Apfloat;

import aya.Aya;
import aya.AyaPrefs;
import aya.entities.ListBuilder;
import aya.entities.Operation;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.AyaUserRuntimeException;
import aya.exceptions.SyntaxError;
import aya.exceptions.TypeError;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.list.GenericList;
import aya.obj.list.List;
import aya.obj.list.Str;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.BigNum;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.NumberMath;
import aya.parser.CharacterParser;
import aya.parser.Parser;
import aya.util.QuickDialog;
import aya.util.SimplePlot;
import aya.variable.Variable;

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
		/* 37 %  */ new OP_Dot_Percent(),
		/* 38 &  */ new OP_Dot_And(),
		/* 39 '  */ new OP_Dot_CastChar(),
		/* 40 (  */ null,
		/* 41 )  */ null,
		/* 42 *  */ null,
		/* 43 +  */ new OP_Dot_Plus(),
		/* 44 ,  */ null,
		/* 45 -  */ new OP_Dot_Minus(),
		/* 46 .  */ null,
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
		/* 67 C  */ null,
		/* 68 D  */ new OP_Dot_Error(),
		/* 69 E  */ new OP_Dot_Len(),
		/* 70 F  */ new OP_Dot_Flatten(),
		/* 71 G  */ new OP_Dot_Write(),
		/* 72 H  */ null,
		/* 73 I  */ new OP_Dot_I(),
		/* 74 J  */ null,
		/* 75 K  */ new OP_Dot_TryCatch(),
		/* 76 L  */ null,
		/* 77 M  */ null,
		/* 78 N  */ new OP_Dot_N(),
		/* 79 O  */ null,
		/* 80 P  */ new OP_Dot_Print(),
		/* 81 Q  */ new OP_Dot_Rand(),
		/* 82 R  */ new OP_Dot_R(),
		/* 83 S  */ new OP_Dot_Case(),
		/* 84 T  */ new OP_Dot_T(),
		/* 85 U  */ new OP_RequestString(),
		/* 86 V  */ new OP_Dot_AppendBack(),
		/* 87 W  */ new OP_Dot_W(),
		/* 88 X  */ new OP_SimplePlot(),
		/* 89 Y  */ null,
		/* 90 Z  */ new OP_Dot_Zed(),
		/* 91 [  */ new OP_Dot_Ceiling(),
		/* 92 \  */ new OP_Dot_BackSlash(),
		/* 93 ]  */ new OP_Dot_Floor(),
		/* 94 ^  */ null,
		/* 95 _  */ new OP_Dot_Underscore(),
		/* 96 `  */ null,
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
	
	
	/** Returns a list of all the op descriptions **/
	public static ArrayList<String> getAllOpDescriptions() {
		ArrayList<String> out = new ArrayList<String>();
		for (char i = 0; i <= 126-Ops.FIRST_OP; i++) {
			if(DOT_OPS[i] != null) {
				out.add(DOT_OPS[i].getDocStr() + "\n(extended operator)");
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

// ! - 33
class OP_Dot_Bang extends Operation {
	public OP_Dot_Bang() {
		this.name = ".!";
		this.info = "N signnum\nS parse if number";
		this.argTypes = "NS";
		this.overload = Ops.KEYVAR_SIGNUM.name();
	}
	@Override public void execute(final Block block) {
		Obj o = block.pop();
		
		if (o.isa(NUMBER)) {
			block.push(((Number)o).signnum());
		} else if (o.isa(STR)) {
			String numStr = o.str().trim();
			try {
				block.push(new Num(Integer.parseInt(numStr)));
			} catch (NumberFormatException e) {
				try {
					block.push(new BigNum(new Apfloat(numStr)));
				} catch (NumberFormatException e2) {
					block.push(o);
				}
			}
		} 
		else if (o.isa(DICT)) {
			block.callVariable((Dict)o, Ops.KEYVAR_SIGNUM);
		}
		else {
			throw new TypeError(this,o);
		}
	}
}

// $ - 36
class OP_Dot_SortUsing extends Operation {
	public OP_Dot_SortUsing() {
		this.name = ".$";
		this.info = "LE sort using the expresion E";
		this.argTypes = "LE";
	}
	
	@Override public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		if (a.isa(BLOCK) && b.isa(LIST)) {
			final Block blk = ((Block)a).duplicate();
			List objs = ((List)b);
			List key_obj = blk.mapTo(objs);
			
			//Convert keys to int array
			ArrayList<SUItem> items = new ArrayList<SUItem>(key_obj.length());
			try {
				
				for (int i = 0; i < objs.length(); i++) {
					items.add(new SUItem(objs.get(i), (Comparable) key_obj.get(i)));
				}
				Collections.sort(items);
				
			} catch (ClassCastException e) {
				throw new AyaRuntimeException(".$: all objects must be comparable to each other");
			}
			
			
						
			ArrayList<Obj> out = new ArrayList<Obj>(items.size());
			for (SUItem i : items) {
				out.add(i.o);
			}
			
			block.push(new GenericList(out).promote());
			
		} 
		else {
			throw new TypeError(this, a);
		}
	}
	
	class SUItem<T extends Comparable> implements Comparable<SUItem<T>>{
		public Obj o;
		public T d;
		public SUItem(Obj o, T d) {
			this.o = o;
			this.d = d;
		}
		public int compareTo(SUItem<T> i) {
			//Ascending
			//return (int)((this.d - i.d)*10000); 
			return d.compareTo(i.d);
		}
	}

	
	
}

// % - 37
class OP_Dot_Percent extends Operation {
	public OP_Dot_Percent() {
		this.name = ".%";
		this.info = "NN integer division";
		this.argTypes = "NN";
		this.overload = Ops.KEYVAR_IDIV.name() + "/" + Ops.KEYVAR_RIDIV.name();
	}
	@Override public void execute(final Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			try {
				//b idiv a
				block.push(((Number)(b)).idiv((Number)(a)));
			} catch (ArithmeticException e) {
				throw new AyaRuntimeException("%: Divide by 0");
			}
		}
		else if (a.isa(NUMBERLIST) && b.isa(NUMBER)) {
			block.push( ((NumberList)a).idivFrom((Number)b) );
		}
		
		else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).idiv((Number)a) );
		}
		else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).idiv((NumberList)a) );
		}

		else if (a.isa(DICT)) {
			block.push(b);
			block.callVariable((Dict)a, Ops.KEYVAR_IDIV);
		} else if (b.isa(DICT)) {
			block.callVariable((Dict)b, Ops.KEYVAR_RIDIV, a);
		}
		else {
			throw new TypeError(this, a,b);
		}
	}
}



// & - 38
class OP_Dot_And extends Operation {
	public OP_Dot_And() {
		this.name = ".&";
		this.info = "SSS replace all occurances of the regex S1 with S2 in S3";
		this.argTypes = "SSS";
	}
	@Override public void execute(final Block block) {
		Obj a = block.pop();  // str
		Obj b = block.pop();  // replace
		Obj c = block.pop();  // find
		
		if ( a.isa(STR) && (b.isa(STR) || b.isa(CHAR)) && (c.isa(STR) || c.isa(CHAR))) {
			block.push(new Str( a.str().replaceAll(c.str(), b.str()) ));
		} else {
			throw new TypeError(this,a,b,c);
		}
	}
}

// ' - 39
class OP_Dot_CastChar extends Operation {
	public OP_Dot_CastChar() {
		this.name = ".'";
		this.info = "NSC cast to char";
		this.argTypes = "NS";
	}
	@Override public void execute(final Block block) {
		Obj o = block.pop();
		
		if (o.isa(NUMBER)) {
			block.push( Char.valueOf(((Number)o).toInt()) );
		} else if (o.isa(STR)) {
			block.push( Char.valueOf(o.str().charAt(0)) );
		} else if (o.isa(CHAR)) {
			block.push(o);
		} else {
			throw new TypeError(this,o);
		}
	}
}

// + - 43
class OP_Dot_Plus extends Operation {
	public OP_Dot_Plus() {
		this.name = ".+";
		this.info = "NN gcd";
		this.argTypes = "NN";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push(NumberMath.gcd((Number)a, (Number)b));	
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// - - 45
class OP_Dot_Minus extends Operation {
	public OP_Dot_Minus() {
		this.name = ".-";
		this.info = "NN lcm\nLN remove item N from list L";
		this.argTypes = "NN";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		if (a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push(NumberMath.lcm((Number)a, (Number)b));	
		} else if (b.isa(LIST) && a.isa(NUMBER)) {
			((List)b).remove( ((Number)a).toInt() );
			block.push(b);
		} else if (a.isa(LIST) && b.isa(LIST)) {
			List l = (List)a;
			NumberList ns = l.toNumberList();
			((List)b).removeAll(ns.toIntegerArray());
			block.push(b);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// / - 47
class OP_Dot_FwdSlash extends Operation {
	public OP_Dot_FwdSlash() {
		this.name = "./";
		this.info = "ceiling";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_CEIL.name();
	}
	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		
		if (a.isa(NUMBER)) {
			block.push(((Number)a).ceil());
		} 
		
		else if (a.isa(NUMBERLIST)) {
			block.push( ((NumberList)a).ceil() );
		}
		
		else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_CEIL);
		}
		
		else {
			throw new TypeError(this, a);
		}
	}
}

// ; - 59
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
		this.info = "LN head, NN lesser of";
		this.argTypes = "LN|NN";
		this.overload = Ops.KEYVAR_HEAD.name();
	}
	@Override
	public void execute(Block block) {
		Obj b = block.pop();			// Popped in Reverse Order
		Obj a = block.pop();
		

		if (b.isa(NUMBER) && a.isa(LIST)) {	
			block.push(((List)a).head(((Number)b).toInt()));
		}
		
		else if (a.isa(NUMBER) && b.isa(NUMBER)) {
			if ( ((Number)a).compareTo((Number)b) > 0) {
				block.push(a);
			} else {
				block.push(b);
			}
		}
		
		else if (a.isa(STR) && b.isa(STR)) {
			if ( ((Str)a).compareTo((Str)b) > 0) {
				block.push(a);
			} else {
				block.push(b);
			}
		}
		
		else if (a.isa(CHAR) && b.isa(CHAR)) {
			if ( ((Char)a).compareTo((Char)b) > 0) {
				block.push(a);
			} else {
				block.push(b);
			}
		}
		
		else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_HEAD, b);
		}
		
		else {
			throw new TypeError(this, a, b);
		}
	}
}


// > - 62
class OP_Dot_GreaterThan extends Operation {
	public OP_Dot_GreaterThan() {
		this.name = ".>";
		this.info = "LN tail, NN greater of";
		this.argTypes = "LN|NN";
		this.overload = Ops.KEYVAR_TAIL.name();
	}
	@Override
	public void execute(Block block) {
		Obj b = block.pop();			// Popped in Reverse Order
		Obj a = block.pop();
		
		if (b.isa(NUMBER) && a.isa(LIST)) {
		block.push( ((List)a).tail(((Number)b).toInt()) );
		}
		
		else if (a.isa(NUMBER) && b.isa(NUMBER)) {
			if ( ((Number)a).compareTo((Number)b) < 0) {
				block.push(a);
			} else {
				block.push(b);
			}
		}
		
		else if (a.isa(STR) && b.isa(STR)) {
			if ( ((Str)a).compareTo((Str)b) < 0) {
				block.push(a);
			} else {
				block.push(b);
			}
		}
		
		else if (a.isa(CHAR) && b.isa(CHAR)) {
			if ( ((Char)a).compareTo((Char)b) < 0) {
				block.push(a);
			} else {
				block.push(b);
			}
		}
		
		else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_TAIL, b);
		}
		
		else {
			throw new TypeError(this, a, b);
		}
		
	}
}

// = 61 new OP_Dot_Equals(),
class OP_Dot_Equals extends Operation {
	public OP_Dot_Equals() {
		this.name = ".=";
		this.info = "LL element-wise equals";
		this.argTypes = "LL|LA|AL";
	}
	
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		
		if (a.isa(DICT) && b.isa(DICT)) {
			block.push(a.equiv(b) ? Num.ONE : Num.ZERO);
		}
		
		else if (a.isa(LIST) && b.isa(LIST)) {
			block.push( List.equalsElementwise((List)a, (List)b) );
		}
		
		else if ( a.isa(LIST) ) {
			block.push( List.equalsElementwise((List)a, b) );
		}
		
		else if ( b.isa(LIST) ) {
			block.push( List.equalsElementwise((List)b, a) );
		}
		
		else {
			throw new TypeError(this, a, b);
		}
	}

}


// ? - 63
class OP_Dot_Conditional extends Operation {
	public OP_Dot_Conditional() {
		this.name = ".?";
		this.info = "BAA conditional operator. if B, then A1 else A2. If A is a block, execute it.";
		this.argTypes = "BAA";
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
				block.addAll(((Block)b).duplicate().getInstructions().getInstrucionList());
			} else {
				block.push(b);
			}
		} else {
			if(a.isa(BLOCK)) {
				block.addAll(((Block)a).duplicate().getInstructions().getInstrucionList());
			} else {
				block.push(a);
			}
		}
	}

}

// @ - 64
class OP_Dot_At extends Operation {
	public OP_Dot_At() {
		this.name = ".@";
		this.info = "moves the Nth item on the stack (not including N) to the top";
		this.argTypes = "N";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		
		if (a.isa(NUMBER)) {
			int size = block.getStack().size();
			int i = ((Number)a).toInt();
			
			if (i > size || i <= 0) {
				throw new AyaRuntimeException(i + " .@ stack index out of bounds");
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
class OP_Dot_ArrayAll extends Operation {
	public OP_Dot_ArrayAll() {
		this.name = ".A";
		this.info = "wrap the entire stack in a generic list";
		this.argTypes = "A";
	}
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Block block) {
		ArrayList<Obj> list = new ArrayList<Obj>();
		list.addAll((Stack<Obj>)block.getStack().clone());
		block.clearStack();	
		block.push(new GenericList(list));
	}
}

// B - 66
class OP_Dot_Append extends Operation {
	public OP_Dot_Append() {
		this.name = ".B";
		this.info = "append item to the back of a list";
		this.argTypes = "AL";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		if (a.isa(LIST)) {
			((List)a).addItem(b);
			block.push(a);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}




// D - 68
class OP_Dot_Error extends Operation {
	public OP_Dot_Error() {
		this.name = ".D";
		this.info = "interrupts the program and throws an error message";
		this.argTypes = "S";
	}
	@Override public void execute (Block block) {
		Obj a = block.pop();
		
		if(a.isa(STR)) {
			throw new AyaUserRuntimeException(a.str());
		} else {
			throw new TypeError(this, a);
		}
	}
}

//E - 69
class OP_Dot_Len extends Operation {
	public OP_Dot_Len() {
		this.name = ".E";
		this.info = "return length and keep item on the stack";
		this.argTypes = "A";
		this.overload = Ops.KEYVAR_LEN.name();
	}
	@Override
	public void execute(Block block) {
		final Obj n = block.pop();
		
		if (n.isa(LIST)) {
			block.push(n);
			block.push( new Num(((List)n).length()) );
		}
		else if (n.isa(DICT)) {
			block.push(n);
			block.callVariable((Dict)n, Ops.KEYVAR_LEN);
		}
		else {
			throw new TypeError(this, n);
		}
	}
}



//F - 70
class OP_Dot_Flatten extends Operation {
	public OP_Dot_Flatten() {
		this.name = ".F";
		this.info = "L flatten nested list";
		this.argTypes = "L";
	}
	@Override
	public void execute(Block block) {
		final Obj n = block.pop();
		
		if (n.isa(LIST)) {
			if (n.isa(STR) || n.isa(NUMBERLIST)) {
				block.push(n.deepcopy());
			} else {
				block.push(List.flatten((GenericList)n));
			}
		} else {
			throw new TypeError(this, n);
		}
	}
}




// G - 71
class OP_Dot_Write extends Operation {
	public OP_Dot_Write() {
		this.name = ".G";
		this.info = "write A as a string to file located at S. N = 0, overwrite. N = 1, append";
		this.argTypes = "ASN";
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
				    throw new AyaRuntimeException("Cannot open file '" + fstr + "'");
				} catch (InvalidPathException ipe) {
					throw new AyaRuntimeException("Cannot open file '" + fstr + "'");
				}
			} 
			
			else if (option == 1) {
				try {
				    Files.write(Paths.get(fstr), write.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				}catch (IOException e) {
				    throw new AyaRuntimeException("Cannot open file '" + fstr + "'");
				}  catch (InvalidPathException ipe) {
					throw new AyaRuntimeException("Cannot open file '" + fstr + "'");
				}
			} 
			
			else {
			    throw new AyaRuntimeException(".U: Option " + option + "is not valid. Please use 0 for overwrite and 1 for append");
			}
			return;
		}
		throw new TypeError(this, n);
	}
}

// I - 73
//NOTE: If updating this operator, also update I
class OP_Dot_I extends Operation {
	public OP_Dot_I() {
		this.name = "I";
		this.info = "index, filter";
		this.argTypes = "LL|LI|LE";
	}
	@Override public void execute (final Block block) {
		Obj index = block.pop();
		final Obj list = block.pop();
		block.push(list); //.I keeps the list on the stack
		
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

//K - 75
class OP_Dot_TryCatch extends Operation {
	public OP_Dot_TryCatch() {
		this.name = ".K";
		this.info = "try B1, if error, execute B2. Neither block has access to the global stack";
		this.argTypes = "EE";
	}
	@Override public void execute (Block block) {
		Obj catchBlock = block.pop();
		Obj tryBlock = block.pop();
		
		if(tryBlock.isa(BLOCK) && catchBlock.isa(BLOCK)) {
			try {
				Block b = ((Block)tryBlock).duplicate();
				b.eval();
				block.appendToStack(b.getStack());
			} catch (Exception e) {
				Block b = ((Block)catchBlock).duplicate();
				b.push(new Str(Aya.exToString(e)));
				b.eval();				
				block.appendToStack(b.getStack());
			}
		}
	}
}

// N - 78
class OP_Dot_N extends Operation {
	public OP_Dot_N() {
		this.name = ".N";
		this.info = "return the index of the first element of L that satifies E";
		this.argTypes = "LA";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop(); //Block
		final Obj b = block.pop(); //List
		
		int index = 0;
		if(b.isa(LIST) && a.isa(BLOCK)) {
			
			block.push(b); //Push the list

			final Block blk = (Block)a;
			List l = (List)b;
			for (int i = 0; i < l.length(); i++) {
				Block cond = blk.duplicate();
				cond.push(l.get(i));
				cond.eval();
				Obj result = cond.pop();
				if (result.bool()) {
					block.push(new Num(index)); // ..and the index
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
class OP_Dot_Print extends Operation {
	public OP_Dot_Print() {
		this.name = ".P";
		this.info = "prints the value on the top of the stack to stdout";
		this.argTypes = "A";
	}
	@Override public void execute (Block block) {
		//Aya.getInstance().getOut().printAsPrint(block.pop().str());
		Aya.getInstance().print(block.pop().str());
	}
}

// Q - 81
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

// R - 82
class OP_Dot_R extends Operation {
	public OP_Dot_R() {
		this.name = ".R";
		this.info = "Returns a numeric list from 0 to N-1";
		this.argTypes = "N";
	}
	@Override public void execute (Block block) {
		final Obj a = block.pop();
		
		if (a.isa(NUM)) {
			final Num n = (Num)a;
			block.push(ListBuilder.buildRange(Num.ZERO, (Num)(n.add(n.signnum().negate())) ));
		} else if (a.isa(BIGNUM)) {
			final BigNum n = (BigNum)a;
			block.push(ListBuilder.buildRange(BigNum.ZERO, (BigNum)(n.add(n.signnum().negate())) ));
		} else {
			throw new TypeError(this, a);
		}
	}
}


// S - 83
class OP_Dot_Case extends Operation {
	public OP_Dot_Case() {
		this.name = ".S";
		this.info = "returns the first element of a list. if the first element is a block, push its contents to the instruction stack";
		this.argTypes = "L";
	}
	@Override public void execute (Block block) {
		Obj a = block.pop();
		
		//Return the first element of a list
		//If block, dump it
		if(a.isa(LIST)) {
			List l = (List)a;
			if(l.length() == 0)
				throw new AyaRuntimeException(this.name + ": list contains no elements");
			
			Obj item = l.get(0);
			if(item.isa(BLOCK)) {
				block.addAll(((Block)item).getInstructions().getInstrucionList());
			} else {
				block.push(item);
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

//T - 84
class OP_Dot_T extends Operation {
	public OP_Dot_T() {
		this.name = ".T";
		this.info = "transpose a 2d list";
		this.argTypes = "L";
	}
	@Override public void execute (Block block) {		
		final Obj a = block.pop();
		
		if (a.isa(LIST)) {
			block.push( List.transpose((List)a) );
		}
		
		else {
			throw new TypeError(this, a);
		}
	}
}

// U - 85
class OP_RequestString extends Operation {
	public OP_RequestString() {
		this.name = ".U";
		this.info = "requests a string using a ui dialog. S is the dialiog header";
		this.argTypes = "S";
	}
	@Override
	public void execute(Block block) {
		
		block.push(new Str(QuickDialog.requestString(block.pop().str())));

	}
}

// V - 86
class OP_Dot_AppendBack extends Operation {
	public OP_Dot_AppendBack() {
		this.name = ".V";
		this.info = "append item to the back of a list";
		this.argTypes = "AL";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		if (a.isa(LIST)) {
			((List)a).addItem(0, b);
			block.push(a);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// W - 87
class OP_Dot_W extends Operation {
	public OP_Dot_W() {
		this.name = ".W";
		this.info = "R export variables only if they exist in the local scope";
		this.argTypes = "AL";
	}
	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		
		if (a.isa(DICT)) {
			final Dict d = (Dict)a;
			Aya.getInstance().getVars().peek().mergeDefined(d.getVarSet());
		} else {
			throw new TypeError(this, a);
		}
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
		Obj a = block.pop();
		
		
		if(a.isa(LIST)) {
			List l = (List)a;
			
			double[] data = l.toNumberList().todoubleArray();
			
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



// Z - 90
class OP_Dot_Zed extends Operation {
	public OP_Dot_Zed() {
		this.name = ".Z";
		this.info = "dereferences a string as variable";
		this.argTypes = "S";
	}
	@Override public void execute (Block block) {
		Obj s = block.pop();
		
		if(s.isa(STR)) {
			String str = s.str();
			if(str.contains(".")) {
				throw new AyaRuntimeException(".Z: Cannot look up dictionary variables");
			}
			Variable v = new Variable(str);
			block.push(Aya.getInstance().getVars().getVar(v));
			return;
		}
		throw new TypeError(this, s);
	}
}

// [ - 91
class OP_Dot_Ceiling extends Operation {
	public OP_Dot_Ceiling() {
		this.name = ".[";
		this.info = "promote a list to a more specific type if possible";
		this.argTypes = "L";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (a.isa(OBJLIST)) {
			block.push(((GenericList)a).promote());
		} else if (a.isa(LIST)) {
			block.push(a);
		}
		else {
			throw new TypeError(this.name, this.argTypes, a);
		}
	}
}

// \ - 92
class OP_Dot_BackSlash extends Operation {
	public OP_Dot_BackSlash() {
		this.name = ".\\";
		this.info = "floor";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_FLOOR.name();
	}
	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		
		if (a.isa(NUMBER)) {
			block.push(((Number)a).floor());
		}
		
		else if (a.isa(NUMBERLIST)) {
			block.push( ((NumberList)a).floor() );
		}
		
		else {
			throw new TypeError(this, a);
		}
	}
}


// ] - 93
class OP_Dot_Floor extends Operation {
	public OP_Dot_Floor() {
		this.name = ".]";
		this.info = "copy a list as a generic list";
		this.argTypes = "L";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if(a.isa(LIST)) {
			if (a.isa(OBJLIST)) {
				block.push(a.deepcopy());
			} else {
				block.push( new GenericList(((List)a).getObjAL()) );
			}
		} else {
			throw new TypeError(this.name, this.argTypes, a);
		}
	}
}

// _ - 95
class OP_Dot_Underscore extends Operation {
	public OP_Dot_Underscore() {
		this.name = "._";
		this.info = "copies the Nth item on the stack (not including N)";
		this.argTypes = "N";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		
		if (a.isa(NUMBER)) {
			int size = block.getStack().size();
			int i = ((Number)a).toInt();
			
			if (i > size || i <= 0) {
				throw new AyaRuntimeException(i + " ._ stack index out of bounds");
			} else {
				final Obj cp = block.getStack().get(size - i);
				
				if(cp.isa(LIST)) {
					block.push(((List)cp).deepcopy());
				} else {
					block.push(cp);
				}
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}

// | - 124
class OP_Dot_Bar extends Operation {
	public OP_Dot_Bar() {
		this.name = ".|";
		this.info = "absolute value";
		this.argTypes = "N";
		this.overload = Ops.KEYVAR_ABS.name();
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();	
		
		if (a.isa(NUMBER)) {
			block.push( ((Number)a).abs() );
		} else if (a.isa(NUMBERLIST)) {
			block.push( ((NumberList)a).abs() );
		} else if (a.isa(DICT)) {
			block.callVariable((Dict)a, Ops.KEYVAR_ABS);
		} else {
			throw new TypeError(this, a);
		}
	}
}




// ~ - 126
class OP_Dot_Tilde extends Operation {
	public OP_Dot_Tilde() {
		this.name = ".~";
		this.info = "parse a string or character to a block";
		this.argTypes = "S|C";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();	
		
		if (a.isa(STR)) {
			block.push( Parser.compile(a.str(), Aya.getInstance()) );
			return;
		} else if (a.isa(CHAR)) {
			final char c = ((Char)a).charValue();
			final String varname = CharacterParser.getName(c);
			if(varname == null) {
				throw new AyaRuntimeException("Character '" + c + " is not a valid variable");
			}
			Block b = new Block();
			b.add(new Variable(varname));
			block.add(b);
			return;
		}
		
		throw new TypeError(this, a);
	}
}


