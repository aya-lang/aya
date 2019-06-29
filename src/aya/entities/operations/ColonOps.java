package aya.entities.operations;

import static aya.obj.Obj.BLOCK;
import static aya.obj.Obj.CHAR;
import static aya.obj.Obj.DICT;
import static aya.obj.Obj.LIST;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.NUMBERLIST;
import static aya.obj.Obj.OBJLIST;
import static aya.obj.Obj.STR;
import static aya.obj.Obj.SYMBOL;

import java.util.ArrayList;

import aya.Aya;
import aya.OperationDocs;
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
import aya.obj.list.numberlist.NumberItemList;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.symbol.Symbol;
import aya.variable.Variable;


public class ColonOps {	
	
	public static final char FIRST_OP = '!';

	
	/** A list of all valid single character operations. 
	 *  Stored in final array for fast lookup.
	 *  Array indexes are always [(operator character) - FIRST_OP]
	 */
	public static Operation[] COLON_OPS = {
		/* 33 !  */ null,
		/* 34 "  */ null,
		/* 35 #  */ new OP_Colon_Pound(),
		/* 36 $  */ new OP_Colon_Duplicate(),
		/* 37 %  */ null,
		/* 38 &  */ new OP_Colon_And(),
		/* 39 '  */ new OP_Colon_Quote(),
		/* 40 (  */ null, //List item assignment
		/* 41 )  */ null,
		/* 42 *  */ new OP_Colon_Times(),
		/* 43 +  */ null,
		/* 44 ,  */ null,
		/* 45 -  */ null, //Special number literals
		/* 46 .  */ null,
		/* 47 /  */ new OP_Colon_Promote(),
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
		/* 59 ;  */ null,
		/* 60 <  */ new OP_Colon_LessThan(),
		/* 61 =  */ new OP_Colon_Equals(),
		/* 62 >  */ new OP_Colon_GreaterThan(),
		/* 63 ?  */ null,
		/* 64 @  */ null,
		/* 65 A  */ null,
		/* 66 B  */ null,
		/* 67 C  */ new OP_Colon_C(),
		/* 68 D  */ new OP_Colon_D(),
		/* 69 E  */ new OP_Colon_E(),
		/* 70 F  */ null,
		/* 71 G  */ null,
		/* 72 H  */ null,
		/* 73 I  */ new OP_Colon_I(),
		/* 74 J  */ null,
		/* 75 K  */ new OP_Colon_K(),
		/* 76 L  */ null,
		/* 77 M  */ new OP_Colon_M(),
		/* 78 N  */ null,
		/* 79 O  */ null,
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
		/* 92 \  */ new OP_Colon_Demote(),
		/* 93 ]  */ null,
		/* 94 ^  */ null,
		/* 95 _  */ null, // Assignment
		/* 96 `  */ null,
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
		/* 123 { */ null,
		/* 124 | */ new OP_SetMinus(),
		/* 125 } */ null,
		/* 126 ~ */ new OP_Colon_Tilde()
	};
	
	
//	/** Returns a list of all the op descriptions **/
//	public static ArrayList<String> getAllOpDescriptions() {
//		ArrayList<String> out = new ArrayList<String>();
//		for (char i = 0; i <= 126-Ops.FIRST_OP; i++) {
//			if(COLON_OPS[i] != null) {
//				out.add(COLON_OPS[i].getDocStr() + "\n(colon operator)");
//			}
//		}
//		return out;
//		
//	}
	
	
	/** Returns the operation bound to the character */
	public static Operation getOp(char op) {
		if(op >= 33 && op <= 126) {
			return COLON_OPS[op-FIRST_OP];
		} else {
			throw new SyntaxError("Colon operator ':" + op + "' does not exist");
		}
	}
	
	public static boolean isColonOpChar(char c) {
		//A char is a colonOp if it is not a lowercase letter or a '('
		return (c >= '!' && c <= '~') 		 //Char bounds
				&& !Variable.isValidChar(c)  //Not variable char
				&& !isDigit(c) 		         //Not digit
				&& c != '(' && c != ' ' && c != '-'; //Special cases
	}

	private static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
}

// # - 35
class OP_Colon_Pound extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":#");
		doc.desc("L:#B", "map");
		doc.desc("D:#B", "map over key value pairs");
		doc.ovrld(Ops.KEYVAR_EACH.name());
		OperationDocs.add(doc);
	}
	
	public OP_Colon_Pound() {
		this.name = ":#";
	}
	@Override
	public void execute(Block block) {
		Obj blk = block.pop();
		Obj col = block.pop();
		
		if (blk.isa(BLOCK) && col.isa(LIST)) {
			block.push( ((Block)blk).mapTo((List)col) );
		} else if (blk.isa(BLOCK) && col.isa(DICT)) {
			Dict d = (Dict)col;
			if (d.hasMetaTable()) {
				block.push(blk);
				block.callVariable(d, Ops.KEYVAR_EACH);
			} else {
				((Block)blk).mapTo((Dict)col);
			}
		} else {
			throw new TypeError(this, col, blk);
		}
	}
}

// $ - 36
class OP_Colon_Duplicate extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":$");
		doc.desc("..AN", "copies the first N items on the stack (not including N)");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_Duplicate() {
		this.name = ":_";
	}
	@Override public void execute (final Block block) {
		final Obj a = block.pop();
		
		if (a.isa(NUMBER)) {
			int size = block.getStack().size();
			int i = ((Number)a).toInt();
			
			if (i > size || i <= 0) {
				throw new AyaRuntimeException(i + " :$ stack index out of bounds");
			} else {
				
				while (i > 0) {
					final Obj cp = block.getStack().get(size - i);
					
					if(cp.isa(LIST)) {
						block.push( ((List)cp).deepcopy() );
					} else {
						block.push(cp);
					}
				i--;
				}
				
			}
			
		} else {
			
		}
	}
}

// & - 39
class OP_Colon_And extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":&");
		doc.desc("A", "duplicate reference (same as $ but does not make a copy)");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_And() {
		this.name = ":&";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.peek();
		block.push(a);
	}
}

// ' - 39
class OP_Colon_Quote extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":'");
		doc.desc("C", "ord (cast to int)");
		doc.desc("S", "convert a string to bytes using UTF-8 encoding");
		doc.desc("N", "identity; return N");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_Quote() {
		this.name = ":'";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (a.isa(NUMBER)) {
			block.push(a);
		} else if (a.isa(CHAR)) { 
			block.push( new Num((int)(((Char)a).charValue())) );
		} else if (a.isa(STR)) {
			Str s = (Str)a;
			ArrayList<Number> nums = new ArrayList<Number>(s.length());
			byte[] bytes = s.getBytes();
			for (byte b : bytes) {
				nums.add(new Num(b));
			}
			block.push(new NumberItemList(nums));
		} else {
			throw new TypeError(this, a);
		}
	}
}

//* - 42
class OP_Colon_Times extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":*");
		doc.desc("LLB", "outer product of two lists using B");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_Times() {
		this.name = ":*";
	}
	@Override
	public void execute(Block block) {
		Obj blk = block.pop();
		Obj a  = block.pop();
		Obj b  = block.pop();
		
		if (blk.isa(BLOCK)) {
			Block expr = (Block)blk;
			if (a.isa(LIST) && b.isa(LIST)) {
				List l1 = (List)a;
				List l2 = (List)b;
				ArrayList<Obj> out = new ArrayList<Obj>(l1.length());
				
				for (int i = 0; i < l2.length(); i++) {
					Block e = new Block();
					e.addAll(expr.getInstructions().getInstrucionList());
					out.add(e.mapToPushStack(l2.get(i), l1));
				}
				
				block.push(new GenericList(out));
			} else if (a.isa(LIST)) {
				List l1 = (List)a;
				Block e = new Block();
				e.addAll(expr.getInstructions().getInstrucionList());
				block.push(e.mapToPushStack(b, l1));
			} else if (b.isa(LIST)) {
				List l2 = (List)b;
				Block e = new Block();
				e.addAll(expr.getInstructions().getInstrucionList());
				e.add(a);
				block.push(e.mapTo(l2));
			} else {
				throw new TypeError(this, blk, a, b);
			}
		} else {
			throw new TypeError(this, blk, a, b);
		}
	}
}

// / - 47
class OP_Colon_Promote extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":/");
		doc.desc("L", "promote list to more specific type if possible");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_Promote() {
		this.name = ":/";
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
			throw new TypeError(this, a);
		}
	}
}

// < - 60
class OP_Colon_LessThan extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":<");
		doc.desc("NN|CC|SS", "less then or equal to");
		doc.ovrld(Ops.KEYVAR_LEQ.name());
		doc.vect();
		OperationDocs.add(doc);
	}
	
	public OP_Colon_LessThan() {
		this.name = ":<";
	}
	@Override
	public void execute(final Block block) {
		final Obj b = block.pop();			// Popped in Reverse Order
		final Obj a = block.pop();
		
		
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( new Num(((Number)a).compareTo((Number)b) <= 0) );
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			block.push( new Num(((Char)a).compareTo((Char)b) <= 0) );
		} else if (a.isa(STR) && b.isa(STR)) {
			block.push( new Num(a.str().compareTo(b.str()) <= 0) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).geq((Number)a) ); // geq is opposite of leq
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER) ) {
			block.push( ((NumberList)a).leq((Number)b) ); 
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST) ) {
			block.push( ((NumberList)a).leq((NumberList)b) ); 
		} else if (b.isa(DICT)) {
			block.push(a);
			block.callVariable((Dict)b, Ops.KEYVAR_LEQ);
		} 
		else {
			throw new TypeError(this, a,b);
		}
	}
}

// = - 61
class OP_Colon_Equals extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":=");
		doc.desc("AJ|AC|AS", "assign A to variable");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_Equals() {
		this.name = ":=";
	}
	@Override
	public void execute(final Block block) {
		final Obj sym = block.pop();
		final Obj obj = block.peek();
		
		if (sym.isa(SYMBOL)) {
			Aya.getInstance().getVars().setVar(((Symbol)sym).id(), obj);
		} else if (sym.isa(CHAR) || sym.isa(STR)) {
			String s = sym.str();
			if (Variable.isValidStr(s)) {
				Aya.getInstance().getVars().setVar(Variable.encodeString(s), obj);
			} else {
				throw new AyaRuntimeException(":= Invalid identifier: '" + s + "'");
			}
		} else {
			throw new TypeError(this, sym, obj);
		}
	}
}

// > - 62
class OP_Colon_GreaterThan extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":>");
		doc.desc("NN|CC|SS", "greater than or equal to");
		doc.vect();
		doc.ovrld(Ops.KEYVAR_GEQ.name());
		OperationDocs.add(doc);
	}
	
	public OP_Colon_GreaterThan() {
		this.name = ":>";

	}
	@Override
	public void execute(final Block block) {
		final Obj b = block.pop();			// Popped in Reverse Order
		final Obj a = block.pop();
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( new Num(((Number)a).compareTo((Number)b) >= 0) );
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			block.push( new Num(((Char)a).compareTo((Char)b) >= 0) );
		} else if (a.isa(STR) && b.isa(STR)) {
			block.push( new Num(a.str().compareTo(b.str()) >= 0) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( ((NumberList)b).leq((Number)a) ); // lt is opposite of gt
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER) ) {
			block.push( ((NumberList)a).geq((Number)b) ); 
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST) ) {
			block.push( ((NumberList)a).geq((NumberList)b) ); 
		} else if (b.isa(DICT)) {
			block.push(a);
			block.callVariable((Dict)b, Ops.KEYVAR_GEQ);
		} 
		
		
		else {
			throw new TypeError(this, a, b);
		}
	}
}

// C - 67
class OP_Colon_C extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":C");
		doc.desc("J", "convert symbol to string name");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_C() {
		this.name = ":C";
	}
	@Override public void execute (Block block) {		
		final Obj a = block.pop();
		
		if (a.isa(SYMBOL)) {
			block.push( new Str(((Symbol)a).name()) );
		}
		
		else {
			throw new TypeError(this, a);
		}
	}
}

// D - 68
class OP_Colon_D extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":D");
		doc.desc("ASD|AJD", "set dict index");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_D() {
		this.name = ":D";
	}
	@Override
	public void execute(Block block) {
		final Obj dict = block.pop();
		final Obj index = block.pop();
		final Obj item = block.pop();


		
		if (dict.isa(DICT) && index.isa(SYMBOL)) {
			((Dict)dict).set(((Symbol)index).id(), item);
			block.push(dict);
		} else if (dict.isa(DICT) && index.isa(STR)) {
			((Dict)dict).set(Symbol.convToSymbol(index.str()).id(), item);
			block.push(dict);
		} else {
			throw new TypeError(this, item, index, dict);
		}
	}
}

// E - 69
class OP_Colon_E extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":E");
		doc.desc("D", "number or items in a dict");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_E() {
		this.name = ":E";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (a.isa(DICT)) {
			block.push(new Num(((Dict)a).size()));
		} else {
			throw new TypeError(this, a);
		}
	}
}

// I - 73  
class OP_Colon_I extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":I");
		doc.desc("DS|DJ", "get dict item from key");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_I() {
		this.name = ":I";
	}
	@Override
	public void execute(Block block) {
		final Obj index = block.pop();
		final Obj list = block.pop();
		
		
		if (list.isa(DICT)) {
			
			Obj out = null;
			final Dict d = ((Dict)list);
			
			if (index.isa(STR)) {
				out = d.get(index.str());
			} else if (index.isa(SYMBOL)) {
				out = d.get( ((Symbol)index).id() );
			} else if (index.isa(LIST)) {
				List l = (List)index;
				if (l.length() != 2 || !l.get(0).isa(SYMBOL)) {
					throw new TypeError(this, index, list);
				}
				
				Symbol key = (Symbol)(l.get(0));
				if (d.containsKey(key.id())) {
					out = d.get(key.id());
				} else {
					out = l.get(1);
				}
				
			} else {
				throw new TypeError(this, index, list);
			}
			
			block.push(list);
		
			if (out.isa(BLOCK)) {
				block.addAll( ((Block)out).getInstructions().getInstrucionList() );
			} else {
				block.push(out);
			}
			
		} else {
			throw new TypeError(this, index, list);
		}
	}
}

// K - 75
class OP_Colon_K extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":K");
		doc.desc("D", "return a list of keys as symbols");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_K() {
		this.name = ":K";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (a.isa(DICT)) {
			ArrayList<Long> keys = ((Dict)a).keys();
			ArrayList<Obj> keyNames = new ArrayList<Obj>(keys.size());
			for (Long l : keys) {
				keyNames.add(Symbol.fromID(l));
			}
			block.push(new GenericList(keyNames));
		} else {
			throw new TypeError(this, a);
		}
	}
}

// M - 77
class OP_Colon_M extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":M");
		doc.desc("DD", "set D1's meta to D2 leave D1 on stack");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_M() {
		this.name = ":M";
	}
	@Override
	public void execute(Block block) {
		final Obj meta = block.pop();
		final Obj dict = block.pop();


		if(dict.isa(DICT) && meta.isa(DICT)) {
			((Dict)dict).setMetaTable((Dict)meta);
			block.push(dict);
		} else {
			throw new TypeError(this, meta, dict);
		}
	}
}





// P - 80
class OP_Colon_P extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":P");
		doc.desc("A", "println to stdout");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_P() {
		this.name = ":P";
	}
	@Override public void execute (Block block) {		
		Aya.getInstance().println(block.pop().str());
	}
}

//R - 82
class OP_Colon_R extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":R");
		doc.desc("-", "readline from stdin");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_R() {
		this.name = ":R";
	}
	@Override public void execute (Block block) {		
		block.push(new Str(Aya.getInstance().nextLine()));
	}
}


// S - 83
class OP_Colon_S extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":S");
		doc.desc("S|C", "convert to symbol");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_S() {
		this.name = ":S";
	}
	@Override public void execute (Block block) {		
		final Obj a = block.pop();
		
		if (a.isa(STR) || a.isa(CHAR)) {
			block.push(Symbol.convToSymbol(a.str()));
		}
		
		else {
			throw new TypeError(this, a);
		}
	}
}



//T - 84
class OP_Colon_T extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":T");
		doc.desc("A", "type of (returns a symbol)");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_T() {
		this.name = ":T";
	}
	
	private static final long TYPE_ID = Variable.encodeString("__type__");
	
	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		Obj type = null;
		
		if (a.isa(DICT)) {
			type = ((Dict)a).getFromMetaTableOrNull(TYPE_ID);
			if (type == null || !type.isa(Obj.SYMBOL)) {
				type = Obj.SYM_DICT;
			}
		} else {
			type = Obj.IDToSym(a.type());
		}
		
		block.push(type);
	}
}



//V - 86
class OP_Colon_V extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":V");
		doc.desc("D", "return a list of values");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_V() {
		this.name = ":V";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (a.isa(DICT)) {
			block.push( new GenericList(((Dict)a).values()) );
		} else {
			throw new TypeError(this, a);
		}
	}
}


//Z - 90
class OP_Colon_Zed extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":Z");
		doc.desc("N", "sleep (milliseconds)");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_Zed() {
		this.name = ":Z";
	}
	@Override public void execute (Block block) {
		Obj a = block.pop();
		
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


// \ - 92
class OP_Colon_Demote extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":\\");
		doc.desc("L", "copy list as generic list");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_Demote() {
		this.name = ":\\";
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
			throw new TypeError(this, a);
		}
	}
}

//_ - 95


// | - 124
class OP_SetMinus extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":|");
		doc.desc("LL", "remove all elements in L2 from L1");
		OperationDocs.add(doc);
	}
	
	public OP_SetMinus() {
		this.name = ":|";
	}
	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		if (a.isa(LIST) && b.isa(LIST)) {
			List.removeAllOccurances((List)b, (List)a);
			block.push(b);
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// ~ - 126
class OP_Colon_Tilde extends Operation {
	
	static {
		OpDoc doc = new OpDoc(':', ":~");
		doc.desc("L", "remove duplicates");
		OperationDocs.add(doc);
	}
	
	public OP_Colon_Tilde() {
		this.name = ":~";
	}
	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		
		if (a.isa(LIST)) {
			block.push( ((List)a).unique() );
		} else {
			throw new TypeError(this, a);
		}
	}
}
	
