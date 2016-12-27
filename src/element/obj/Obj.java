package element.obj;

public abstract class Obj {	
	
	public static final byte UNKNOWN = -1;
	
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
		case STR : return 'S';
		case NUMBER : return 'N';
		//case ANY : return 'A';
		//case MODULE : return 'M';
		//case USER_OBJ : return 'U';
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
			//case 'A': return ANY;
			//case 'M': return MODULE;
			//case 'U': return USER_OBJ;
			default: return UNKNOWN;
		}
	}
	
	
	
	public abstract Obj deepcopy();
	public abstract boolean bool();
	public abstract String repr();
	public abstract String str();
	public abstract boolean equiv(Obj o);
	public abstract boolean isa(byte type);
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
