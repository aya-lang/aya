package element.obj;

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

	public static final byte CHAR = 3;
	
	public static final byte DICT = 4;
	
	public static final byte BLOCK = 5;

	public static final byte USEROBJ = 6;
	
	
	
	
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
		case OBJLIST:
			return "OBJLIST";
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
	
	/** Converts an ID to a char abbreviation */
	public static char IDToAbbrv(byte b) {
		switch(b) {
		//case BOOL : return 'B';
		case CHAR : return 'C' ;
		case NUM : return 'D';
		case BLOCK : return 'E';
		case BIGNUM : return 'F';
		
		case LIST : return 'L';
		case NUMBERLIST : return 'L';
		case NUMBERITEMLIST : return 'L';
		case OBJLIST : return 'L';
		
		case STR : return 'S';
		case NUMBER : return 'N';
		case DICT : return 'R';
		case ANY : return 'A';

		default: return '?';
		}
	}
	
	/** Converts a character abbreviation to its ID */
	public static byte abbrvToID(char c) {
		switch(c) {
			//case 'B': return BOOL;
			//case 'C': return CHARACTER;
			case 'D': return NUM;
			case 'E': return BLOCK;
			case 'F': return BIGNUM;
			case 'L': return LIST;
			case 'S': return STR;
			case 'N': return NUMBER;
			case 'C': return CHAR;
			case 'A': return ANY;
			case 'R': return DICT;
			//case 'M': return MODULE;
			//case 'U': return USER_OBJ;
			default: return UNKNOWN;
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
	public abstract String repr();
	
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
		return this.repr();
	}
	
	@Override
	public boolean equals(Object o) {
		return this.equiv((Obj)o);
	}
}
