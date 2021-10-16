package aya.obj;

import aya.ReprStream;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

/**
 * Superclass for all runtime objects existing on the stack
 * @author Nick
 *
 */
public abstract class Obj {	
	
	//////////////
	// TYPE IDS //
	//////////////
	
	public static final byte UNKNOWN = -1;
	
	public static final byte ANY = 0;

	public static final byte NUMBER = 1;
	public static final byte NUM = 11;
	public static final byte BIGNUM = 12;
	public static final byte RATIONAL_NUMBER = 13;
	public static final byte COMPLEXNUM = 14;
	
	public static final byte LIST = 2;
	public static final byte STR = 21;
	public static final byte NUMBERLIST = 22;
	public static final byte NUMBERITEMLIST = 23;
	public static final byte OBJLIST = 24;
	public static final byte STRLIST = 25;

	public static final byte CHAR = 3;
	
	public static final byte DICT = 4;
	
	public static final byte BLOCK = 5;

	public static final byte USEROBJ = 6;
	
	public static final byte SYMBOL = 7;
	
	
	
	
	////////////////////
	// TYPE UTILITIES //
	////////////////////
	
	/** Given the typeId, return the full type name */
	public static String typeName(byte type) {
		switch (type) {
		case NUM:
			return "NUM";
		case BIGNUM:
			return "BIGNUM";
		case RATIONAL_NUMBER:
			return "RATIONAL_NUMBER";
		case COMPLEXNUM:
			return "COMPLEXNUM";
		case STR:
			return "STR";
		case NUMBERITEMLIST:
			return "NUMBERITEMLIST";
		case LIST:
			return "LIST";
		case BLOCK: 
			return "BLOCK";
		case OBJLIST:
			return "OBJLIST";
		case STRLIST:
			return "STRLIST";
		case CHAR:
			return "CHAR";
		case DICT:
			return "DICT";
		case ANY:
			return "ANY";
		default:
			return "Obj.typeName(" + type + ") not set up.";				
		}
	}
	
	/** Converts an ID to a symbol */
	public static Symbol IDToSym(byte b) {
		switch(b) {
		//case BOOL : return 'B';
		case CHAR : return SymbolConstants.CHAR;
		case NUM : return SymbolConstants.NUM;
		case BLOCK : return SymbolConstants.BLOCK;
		case BIGNUM : return SymbolConstants.NUM;
		
		case LIST : return SymbolConstants.LIST;
		case NUMBERLIST : return SymbolConstants.LIST;
		case NUMBERITEMLIST : return SymbolConstants.LIST;
		case OBJLIST : return SymbolConstants.LIST;
		case STRLIST : return SymbolConstants.LIST;
		
		case SYMBOL : return SymbolConstants.SYM;
		case STR : return SymbolConstants.STR;
		case NUMBER : return SymbolConstants.NUM;
		case RATIONAL_NUMBER : return SymbolConstants.NUM;
		case COMPLEXNUM : return SymbolConstants.NUM;
		case DICT : return SymbolConstants.DICT;
		case ANY : return SymbolConstants.ANY;

		default: return SymbolConstants.UNKNOWN;
		}
	}
	
	
	/** Converts a character abbreviation to its ID */
	public static byte symToID(Symbol sym) {
		long s = sym.id();
		if (s == SymbolConstants.NUM.id()) {
			return NUMBER;
		} else if (s == SymbolConstants.BLOCK.id()) {
			return BLOCK;
		} else if (s == SymbolConstants.LIST.id()) {
			return LIST;
		} else if (s == SymbolConstants.STR.id()) {
			return STR;
		} else if (s == SymbolConstants.CHAR.id()) {
			return CHAR;
		} else if (s == SymbolConstants.ANY.id()) {
			return ANY;
		} else if (s == SymbolConstants.DICT.id()) {
			return DICT;
		} else if (s == SymbolConstants.SYM.id()) {
			return SYMBOL;
		} else {
			return UNKNOWN;
		}
	}
	
	/** Create a deep copy of the obj */
	public abstract Obj deepcopy();
	
	/** Return the bool value of the object */
	public abstract boolean bool();
	
	/** Return a string representation of the object
	 * used for printing to the console during interactive
	 * sessions
	 */
	public abstract ReprStream repr(ReprStream stream);
	
	public String repr() {
		return repr(new ReprStream()).toStringOneline();
	}
	
	/** Cast the obj to a string */
	public abstract String str();
	
	/** Equivalence test */
	public abstract boolean equiv(Obj o);
	
	/** Returns true if the item is a type or sub-type of the given id */
	public abstract boolean isa(byte type);
	
	/** Returns the object's most specific type id */
	public abstract byte type();
	
	@Override
	public String toString() {
		return this.repr(new ReprStream()).toString();
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Obj && this.equiv((Obj)o);
	}
}
