package aya.entities.operations;

import static aya.obj.Obj.BLOCK;
import static aya.obj.Obj.CHAR;
import static aya.obj.Obj.DICT;
import static aya.obj.Obj.LIST;
import static aya.obj.Obj.NUMBER;
import static aya.obj.Obj.NUMBERLIST;
import static aya.obj.Obj.STR;
import static aya.obj.Obj.SYMBOL;
import static aya.util.Casting.asDict;
import static aya.util.Casting.asList;
import static aya.util.Casting.asNumber;
import static aya.util.Casting.asNumberList;
import static aya.util.Casting.asStr;
import static aya.util.Casting.asSymbol;

import java.util.ArrayList;
import java.util.Collections;

import aya.Aya;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.AyaUserObjRuntimeException;
import aya.exceptions.SyntaxError;
import aya.exceptions.TypeError;
import aya.instruction.Instruction;
import aya.instruction.op.OpInfo;
import aya.instruction.op.OpInstruction;
import aya.instruction.variable.VariableInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.block.BlockHeader;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.Str;
import aya.obj.list.numberlist.NumberItemList;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolEncoder;
import aya.util.DictReader;
import aya.util.Triple;
import aya.variable.EncodedVars;


public class ColonOps {	
	
	public static final char FIRST_OP = '!';

	
	/** A list of all valid single character operations. 
	 *  Stored in final array for fast lookup.
	 *  Array indexes are always [(operator character) - FIRST_OP]
	 */
	public static OpInstruction[] COLON_OPS = {
		/* 33 !  */ new OP_Colon_Bang(),
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
		/* 59 ;  */ null,
		/* 60 <  */ new OP_Colon_LessThan(),
		/* 61 =  */ new OP_Colon_Equals(),
		/* 62 >  */ new OP_Colon_GreaterThan(),
		/* 63 ?  */ new OP_Colon_Bool(),
		/* 64 @  */ null,
		/* 65 A  */ new OP_Colon_A(),
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
		/* 123 { */ null, // Named instructions
		/* 124 | */ new OP_SetMinus(),
		/* 125 } */ null,
		/* 126 ~ */ new OP_Colon_Tilde()
	};
	
	public static boolean isColonOpChar(char c) {
		//A char is a colonOp if it is not a lowercase letter or a '('
		return (c >= '!' && c <= '~') 		 //Char bounds
				&& !SymbolEncoder.isValidChar(c)  //Not variable char
				&& !isDigit(c) 		         //Not digit
				&& c != '(' && c != ' ' && c != '-'; //Special cases
	}

	private static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	/** Returns the operation bound to the character */
	public static OpInstruction getOp(char c) {
		OpInstruction op = getOpOrNull(c);
		if (op == null) {
			throw new SyntaxError("Colon operator ':" + c + "' does not exist");
		} else {
			return op;
		}
	}
	
	public static OpInstruction getOpOrNull(char op) {
		if(op >= 33 && op <= 126) {
			return COLON_OPS[op-FIRST_OP];
		} else {
			return null;
		}
	}

}

// ! - 33
class OP_Colon_Bang extends OpInstruction {
	
	public OP_Colon_Bang() {
		init(":!");
		arg("AA", "assert equal");
	}

	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		final Obj b = block.pop();
		if (!a.equiv(b)) {
			Dict error = new Dict();
			error.set("message", List.fromString("AssertionError"));
			error.set("expected", a);
			error.set("received", b);
			throw new AyaUserObjRuntimeException(error);
		}
	}
}

// # - 35
class OP_Colon_Pound extends OpInstruction {
	
	private OP_O op = new OP_O();
	
	public OP_Colon_Pound() {
		init(":#");
		arg("L:#B", "map");
		arg("D:#B", "map over key value pairs");
		setOverload(-1, "each");
	}

	@Override
	public void execute(Block block) {
		this.op.execute(block);
	}
}

// $ - 36
class OP_Colon_Duplicate extends OpInstruction {
	
	public OP_Colon_Duplicate() {
		init(":$");
		arg("..AN", "copies the first N items on the stack (not including N)");
	}

	@Override
	public void execute (final Block block) {
		final Obj a = block.pop();
		
		if (a.isa(NUMBER)) {
			int size = block.getStack().size();
			int i = ((Number)a).toInt();
			
			if (i > size || i <= 0) {
				throw new AyaRuntimeException(i + " :$ stack index out of bounds");
			} else {
				while (i > 0) {
					final Obj cp = block.getStack().get(size - i);
					block.push(cp.deepcopy());
					i--;
				}
			}
			
		} else {
			
		}
	}
}

// & - 39
class OP_Colon_And extends OpInstruction {
	
	public OP_Colon_And() {
		init(":&");
		arg("A", "duplicate reference (same as $ but does not make a copy)");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.peek();
		block.push(a);
	}
}

// ' - 39
class OP_Colon_Quote extends OpInstruction {

	public OP_Colon_Quote() {
		init(":'");
		arg("C", "ord (cast to int)");
		arg("S", "convert a string to bytes using UTF-8 encoding");
		arg("N", "identity; return N");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (a.isa(NUMBER)) {
			block.push(a);
		} else if (a.isa(CHAR)) { 
			int c = ((Char)a).charValue();
			block.push(Num.fromInt(c & 0xff));
		} else if (a.isa(STR)) {
			Str s = asStr(a);
			ArrayList<Number> nums = new ArrayList<Number>(s.length());
			byte[] bytes = s.getBytes();
			for (byte b : bytes) {
				nums.add(Num.fromByte(b));
			}
			block.push(new List(new NumberItemList(nums)));
		} else {
			throw new TypeError(this, a);
		}
	}
}

//* - 42
class OP_Colon_Times extends OpInstruction {
	
	public OP_Colon_Times() {
		init(":*");
		arg("LLB", "outer product of two lists using B");
	}

	@Override
	public void execute(Block block) {
		Obj blk = block.pop();
		Obj a  = block.pop();
		Obj b  = block.pop();
		
		if (blk.isa(BLOCK)) {
			Block expr = (Block)blk;
			if (a.isa(LIST) && b.isa(LIST)) {
				List l1 = asList(a);
				List l2 = asList(b);

				ArrayList<Obj> out = new ArrayList<Obj>(l2.length());
				for (int i = 0; i < l2.length(); i++) {
					out.add(l1.map1arg(expr, l2.getExact(i)));
				}
				
				block.push(new List(out));
			} else if (a.isa(LIST)) {
				block.push(asList(a).map1arg(expr, b));
			} else if (b.isa(LIST)) {
				Block e = new Block();
				e.addAll(expr.getInstructions().getInstrucionList());
				e.add(a);
				block.push(asList(b).map(e));
			} else {
				throw new TypeError(this, blk, a, b);
			}
		} else {
			throw new TypeError(this, blk, a, b);
		}
	}
}

// / - 47


// < - 60
class OP_Colon_LessThan extends OpInstruction {
	
	public OP_Colon_LessThan() {
		init(":<");
		arg("NN|CC|SS", "less then or equal to");
		vect();
		setOverload(2, "leq");
	}

	@Override
	public void execute(final Block block) {
		final Obj b = block.pop();			// Popped in Reverse Order
		final Obj a = block.pop();

		if (overload().execute(block, b, a)) return;
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( Num.fromBool(((Number)a).compareTo((Number)b) <= 0) );
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			block.push( Num.fromBool(((Char)a).compareTo((Char)b) <= 0) );
		} else if (a.isa(STR) && b.isa(STR)) {
			block.push( Num.fromBool(a.str().compareTo(b.str()) <= 0) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push( new List(asNumberList(b).geq(asNumber(a))) ); // geq is opposite of leq
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER) ) {
			block.push( new List(asNumberList(a).leq(asNumber(b))) );
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST) ) {
			block.push( new List(asNumberList(a).leq(asNumberList(b))) );
		} else {
			throw new TypeError(this, a,b);
		}
	}
}

// = - 61
class OP_Colon_Equals extends OpInstruction {
	
	public OP_Colon_Equals() {
		init(":=");
		arg("AJ|AC|AS", "assign A to variable");
	}

	@Override
	public void execute(final Block block) {
		final Obj sym = block.pop();
		final Obj obj = block.peek();
		
		if (sym.isa(SYMBOL)) {
			Aya.getInstance().getVars().setVar(((Symbol)sym).id(), obj);
		} else if (sym.isa(CHAR) || sym.isa(STR)) {
			String s = sym.str();
			if (SymbolEncoder.isValidStr(s)) {
				Aya.getInstance().getVars().setVar(SymbolEncoder.encodeString(s), obj);
			} else {
				throw new AyaRuntimeException(":= Invalid identifier: '" + s + "'");
			}
		} else {
			throw new TypeError(this, sym, obj);
		}
	}
}

// > - 62
class OP_Colon_GreaterThan extends OpInstruction {
	
	public OP_Colon_GreaterThan() {
		init(":>");
		arg("NN|CC|SS", "greater than or equal to");
		setOverload(2, "geq");
		vect();
	}

	@Override
	public void execute(final Block block) {
		final Obj b = block.pop();			// Popped in Reverse Order
		final Obj a = block.pop();

		if (overload().execute(block, b, a)) return;
		
		if(a.isa(NUMBER) && b.isa(NUMBER)) {
			block.push( Num.fromBool(((Number)a).compareTo((Number)b) >= 0) );
		} else if (a.isa(CHAR) && b.isa(CHAR)) {
			block.push( Num.fromBool(((Char)a).compareTo((Char)b) >= 0) );
		} else if (a.isa(STR) && b.isa(STR)) {
			block.push( Num.fromBool(a.str().compareTo(b.str()) >= 0) );
		} else if (a.isa(NUMBER) && b.isa(NUMBERLIST)) {
			block.push(new List(asNumberList(b).leq(asNumber(a)))); // lt is opposite of gt
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBER) ) {
			block.push(new List(asNumberList(a).geq(asNumber(b))));
		} else if (a.isa(NUMBERLIST) && b.isa(NUMBERLIST) ) {
			block.push(new List(asNumberList(a).geq(asNumberList(b))));
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// ? - 63
class OP_Colon_Bool extends OpInstruction {
	
	public OP_Colon_Bool() {
		init(":?");
		arg("A", "convert to boolean");
	}

	@Override
	public void execute (Block block) {		
		block.push(block.pop().bool() ? Num.ONE : Num.ZERO);
	}
}



// A - 65
class OP_Colon_A extends OpInstruction {
	
	public OP_Colon_A() {
		init(":A");
		arg("..AN", "collect N items from stack into list");
	}

	@Override
	public void execute (Block block) {		
		final Obj n = block.pop();
		
		if (n.isa(NUMBER)) {
			int N = ((Number)n).toInt();
			ArrayList<Obj> arr = new ArrayList<>();
			for (int i = 0; i < N; i++) {
				arr.add(block.pop());
			}
			Collections.reverse(arr);
			block.push(new List(arr));
		} else {
			throw new TypeError(this, n);
		}
	}
}

// C - 67
class OP_Colon_C extends OpInstruction {
	
	public OP_Colon_C() {
		init(":C");
		arg("J", "convert symbol to string name");
		arg("S", "return S");
	}

	@Override
	public void execute (Block block) {		
		final Obj a = block.pop();
		
		if (a.isa(SYMBOL)) {
			block.push(List.fromString(asSymbol(a).name()));
		} else if (a.isa(STR)) {
			block.push(a);
		} else {
			throw new TypeError(this, a);
		}
	}
}

// D - 68
class OP_Colon_D extends OpInstruction {
	
	public OP_Colon_D() {
		init(":D");
		arg("ASD|AJD", "set dict index");
	}
	
	private void set(Dict d, Obj key, Obj value) {
		if (key.isa(SYMBOL)) {
			d.set(((Symbol)key).id(), value);
		} else if (key.isa(STR)) {
			d.set(key.str(), value);
		} else {
			throw new TypeError(this, value, key, d);
		}
	}

	@Override
	public void execute(Block block) {
		final Obj dict = block.pop();
		final Obj index = block.pop();
		final Obj item = block.pop();

		if (dict.isa(DICT)) {
			Dict d = (Dict)dict;
			if (index.isa(LIST)) {
				List list = asList(index);
				for (int i = 0; i < list.length(); i++) {
					set(d, list.getExact(i), item);
				}
			} else {
				set(d, index, item);
			}
			block.push(d);
		} else {
			throw new TypeError(this, item, index, dict);
		}
	}
}

// E - 69
class OP_Colon_E extends OpInstruction {
	
	public OP_Colon_E() {
		init(":E");
		arg("D", "number or items in a dict");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (a.isa(DICT)) {
			block.push(Num.fromInt( ((Dict)a).size()) );
		} else {
			throw new TypeError(this, a);
		}
	}
}

// I - 73  
class OP_Colon_I extends OpInstruction {
	
	public OP_Colon_I() {
		init(":I");
		arg("DS|DJ", "get dict item from key");
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
				List l = asList(index);
				if (l.length() != 2 || !l.getExact(0).isa(SYMBOL)) {
					throw new TypeError(this, index, list);
				}
				
				Symbol key = asSymbol(l.getExact(0));
				if (d.containsKey(key.id())) {
					out = d.get(key.id());
				} else {
					out = l.getExact(1);
				}
				
			} else {
				throw new TypeError(this, index, list);
			}
			
			block.push(list);
			block.push(out);
		} else {
			throw new TypeError(this, index, list);
		}
	}
}

// K - 75
class OP_Colon_K extends OpInstruction {
	
	public OP_Colon_K() {
		init(":K");
		arg("D", "return a list of keys as symbols");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (a.isa(DICT)) {
			block.push(new List(asDict(a).keys()));
		} else {
			throw new TypeError(this, a);
		}
	}
}

// M - 77
class OP_Colon_M extends OpInstruction {
	
	public OP_Colon_M() {
		init(":M");
		arg("DD", "set D1's meta to D2 leave D1 on stack");
		arg("BD", "duplicate block with the given metadata");
	}

	@Override
	public void execute(Block block) {
		final Obj meta = block.pop();
		final Obj obj = block.pop();

		if(obj.isa(DICT) && meta.isa(DICT)) {
			((Dict)obj).setMetaTable((Dict)meta);
			block.push(obj);
		} else if (obj.isa(BLOCK) && meta.isa(DICT)) {
			block.push(makeBlockWithMeta((Block)obj, (Dict)meta));
		} else {
			throw new TypeError(this, meta, obj);
		}
	}

	private Block makeBlockWithMeta(Block b, Dict meta) {
		BlockHeader header = headerFromDict(meta);
		return b.duplicateNewHeader(header);
	}
	
	private BlockHeader headerFromDict(Dict meta) {
		BlockHeader bh;

		if (meta.containsKey(EncodedVars.LOCALS)) {
			Obj o = meta.get(EncodedVars.LOCALS);
			if (o.isa(DICT)) {
				Dict locals = (Dict)o;
				bh = new BlockHeader(locals.getVarSet());
			} else {
				throw new AyaRuntimeException("::dict ::block .M:, key 'locals' must be a dict in " + meta.repr());
			}
		} else {
			bh = new BlockHeader();
		}
		
		// Args
		if (meta.containsKey(EncodedVars.ARGS)) {
			Obj args = meta.get(EncodedVars.ARGS);
			if (args.isa(LIST)) {
				List args_list = asList(args);
				for (int i = 0; i < args_list.length(); i++) {
					Triple<Symbol, Symbol, Boolean> info = argInfo(args_list.getExact(i));
					bh.addArg(new BlockHeader.Arg(info.first().id(), info.second().id(), info.third()));
				}
			} else {
				throw new AyaRuntimeException("::dict ::block .M:, key 'args' must be a list in " + meta.repr());
			}
		}
		
		return bh;
	}
		
	private Triple<Symbol, Symbol, Boolean> argInfo(Obj obj) {
		if (obj.isa(DICT)) {
			Dict d = (Dict)obj;
			DictReader dr = new DictReader(d);
			dr.setErrorName("::dict ::block .M");
			return new Triple<Symbol, Symbol, Boolean>(
					dr.getSymbolEx(EncodedVars.NAME),
					dr.getSymbol(EncodedVars.TYPE, Symbol.fromID(EncodedVars.ANY)),
					dr.getBool(EncodedVars.COPY, false));
			
		} else if (obj.isa(SYMBOL)) {
			return new Triple<Symbol, Symbol, Boolean>((Symbol)obj, Symbol.fromID(EncodedVars.ANY), false);
		} else {
			throw new AyaRuntimeException("::dict ::block .M: key 'args' must be a list of dicts or symbols");
		}
	}
}


// O - 80
class OP_Colon_O extends OpInstruction {
	
	public OP_Colon_O() {
		init(":O");
		arg("J", "Aya meta information");
	}

	@Override
	public void execute (Block block) {		
		Obj a = block.pop();
		
		if (a.isa(SYMBOL)) {
			Symbol sym = (Symbol)a;
			if (sym.name().equals("ops")) {
				block.push(OpInfo.getDict());
			} else {
				throw new AyaRuntimeException("':O': Unknown symbol " + sym.name());
			}
		} else {
			throw new TypeError(this, a);
		}
	}
}




// P - 80
class OP_Colon_P extends OpInstruction {
	
	public OP_Colon_P() {
		init(":P");
		arg("A", "println to stdout");
	}

	@Override
	public void execute (Block block) {		
		Aya.getInstance().println(block.pop().str());
	}
}

//R - 82
class OP_Colon_R extends OpInstruction {
	
	public OP_Colon_R() {
		init(":R");
		arg("-", "readline from stdin");
	}

	@Override
	public void execute (Block block) {		
		block.push(List.fromString(Aya.getInstance().nextLine()));
	}
}


// S - 83
class OP_Colon_S extends OpInstruction {
	
	public OP_Colon_S() {
		init(":S");
		arg("S|C", "convert to symbol");
		arg("B", "if block has single var or op convert to symbol list, else return empty list");
	}

	@Override
	public void execute (Block block) {		
		final Obj a = block.pop();
		
		if (a.isa(STR) || a.isa(CHAR)) {
			block.push(Symbol.convToSymbol(a.str()));
		} else if (a.isa(BLOCK)) {
			block.push(singleToSymbolList((Block)a));
		} else {
			throw new TypeError(this, a);
		}
	}

	private List singleToSymbolList(Block b) {
		ArrayList<Obj> out = new ArrayList<Obj>();
		ArrayList<Instruction> instructions = b.getInstructions().getInstrucionList();
		if (instructions.size() == 1) {
			Instruction i = instructions.get(0);
			if (i instanceof VariableInstruction) {
				out.add(Symbol.fromID(((VariableInstruction)i).id()));
			} else if (i instanceof OpInstruction) {
				OpInstruction op = (OpInstruction)i;
				if (op.overload() != null) {
					out.addAll(op.overload().getSymbols());
				}
			}
		}
		return new List(out);
	}
}



//T - 84
class OP_Colon_T extends OpInstruction {
	
	public OP_Colon_T() {
		init(":T");
		arg("A", "type of (returns a symbol)");
	}
	
	private static final long TYPE_ID = SymbolEncoder.encodeString("__type__");
	
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
class OP_Colon_V extends OpInstruction {
	
	public OP_Colon_V() {
		init(":V");
		arg("D", "return a list of values");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		
		if (a.isa(DICT)) {
			block.push( new List(asDict(a).values()) );
		} else {
			throw new TypeError(this, a);
		}
	}
}


//Z - 90
class OP_Colon_Zed extends OpInstruction {
	
	public OP_Colon_Zed() {
		init(":Z");
		arg("N", "sleep (milliseconds)");
	}

	@Override
	public void execute (Block block) {
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


//_ - 95


// | - 124
class OP_SetMinus extends OpInstruction {
	
	public OP_SetMinus() {
		init(":|");
		arg("LL", "remove all elements in L2 from L1");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();
		Obj b = block.pop();
		
		if (a.isa(LIST) && b.isa(LIST)) {
			block.push(asList(b).removeAllOccurances(asList(a)));
		} else {
			throw new TypeError(this, a, b);
		}
	}
}

// ~ - 126
class OP_Colon_Tilde extends OpInstruction {
	
	public OP_Colon_Tilde() {
		init(":~");
		arg("L", "remove duplicates");
	}

	@Override
	public void execute(final Block block) {
		final Obj a = block.pop();
		
		if (a.isa(LIST)) {
			block.push( asList(a).unique() );
		} else {
			throw new TypeError(this, a);
		}
	}
}
	
