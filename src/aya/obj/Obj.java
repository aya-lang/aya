package aya.obj;

import aya.ReprStream;
import aya.obj.symbol.Symbol;

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
	
//	/** Converts an ID to a char abbreviation */
//	public static char IDToAbbrv(byte b) {
//		switch(b) {
//		//case BOOL : return 'B';
//		case CHAR : return 'C' ;
//		case NUM : return 'D';
//		case BLOCK : return 'E';
//		case BIGNUM : return 'F';
//		
//		case LIST : return 'L';
//		case NUMBERLIST : return 'L';
//		case NUMBERITEMLIST : return 'L';
//		case OBJLIST : return 'L';
//		case STRLIST : return 'L';
//		
//		case STR : return 'S';
//		case NUMBER : return 'N';
//		case RATIONAL_NUMBER : return 'N';
//		case DICT : return 'R';
//		case ANY : return 'A';
//
//		default: return '?';
//		}
//	}
	
//	/** Converts a character abbreviation to its ID */
//	public static byte abbrvToID(char c) {
//		switch(c) {
//			//case 'B': return BOOL;
//			//case 'C': return CHARACTER;
//			case 'D': return NUM;
//			case 'E': return BLOCK;
//			case 'F': return BIGNUM;
//			case 'L': return LIST;
//			case 'S': return STR;
//			case 'N': return NUMBER;
//			case 'C': return CHAR;
//			case 'A': return ANY;
//			case 'R': return DICT;
//			//case 'M': return MODULE;
//			//case 'U': return USER_OBJ;
//			default: return UNKNOWN;
//		}
//	}
	
	public static final Symbol SYM_CHAR 	= Symbol.fromStr("char");
	public static final Symbol SYM_NUM 		= Symbol.fromStr("num");
	public static final Symbol SYM_BLOCK 	= Symbol.fromStr("block");
	public static final Symbol SYM_LIST 	= Symbol.fromStr("list");
	public static final Symbol SYM_STR 		= Symbol.fromStr("str");
	public static final Symbol SYM_DICT 	= Symbol.fromStr("dict");
	public static final Symbol SYM_SYM	 	= Symbol.fromStr("sym");
	public static final Symbol SYM_ANY 		= Symbol.fromStr("any");
	public static final Symbol SYM_UNKNOWN 	= Symbol.fromStr("unknown");
	public static final Symbol SYM_TYPE 	= Symbol.fromStr("__type__");

	
	/** Converts an ID to a symbol */
	public static Symbol IDToSym(byte b) {
		switch(b) {
		//case BOOL : return 'B';
		case CHAR : return SYM_CHAR;
		case NUM : return SYM_NUM;
		case BLOCK : return SYM_BLOCK;
		case BIGNUM : return SYM_NUM;
		
		case LIST : return SYM_LIST;
		case NUMBERLIST : return SYM_LIST;
		case NUMBERITEMLIST : return SYM_LIST;
		case OBJLIST : return SYM_LIST;
		case STRLIST : return SYM_LIST;
		
		case SYMBOL : return SYM_SYM;
		case STR : return SYM_STR;
		case NUMBER : return SYM_NUM;
		case RATIONAL_NUMBER : return SYM_NUM;
		case DICT : return SYM_DICT;
		case ANY : return SYM_ANY;

		default: return SYM_UNKNOWN;
		}
	}
	
	
	/** Converts a character abbreviation to its ID */
	public static byte symToID(long s) {
		if (s == SYM_NUM.id()) {
			return NUM;
		} else if (s == SYM_BLOCK.id()) {
			return BLOCK;
		} else if (s == SYM_LIST.id()) {
			return LIST;
		} else if (s == SYM_STR.id()) {
			return STR;
		} else if (s == SYM_CHAR.id()) {
			return CHAR;
		} else if (s == SYM_ANY.id()) {
			return ANY;
		} else if (s == SYM_DICT.id()) {
			return DICT;
		} else if (s == SYM_SYM.id()) {
			return SYMBOL;
		} else {
			return UNKNOWN;
		}
	}
	
	/** Converts a character abbreviation to its ID */
	public static byte symToID(Symbol s) {
		return symToID(s.id());
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
