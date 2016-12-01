package element;

import java.util.AbstractList;
import java.util.ArrayList;

import element.entities.Block;
import element.entities.Flag;
import element.entities.Lambda;
import element.entities.ListBuilder;
import element.entities.ListLiteral;
import element.entities.Operation;
import element.entities.Tuple;
import element.entities.UserObject;
import element.entities.number.BigNum;
import element.entities.number.Num;
import element.entities.number.NumMath;
import element.entities.number.Numeric;
import element.variable.MemberVariable;
import element.variable.Module;
import element.variable.Variable;
import element.variable.VariableSet;

public class ElemTypes {
	
	// Type IDs
	public static final byte UNKNOWN           = -1;
	public static final byte NUM        	   = 0;
	public static final byte BIGNUM            = 1;
	public static final byte FLAG              = 2;
	public static final byte BLOCK             = 3;
	public static final byte LIST              = 4;
	public static final byte CHARACTER         = 5;
	//public static final byte BIG_DECIMAL       = 6;
	public static final byte OP                = 7;
	public static final byte VAR_SET           = 8;
	public static final byte VAR               = 9;
	public static final byte STRING            = 10;
	public static final byte LAMBDA            = 11;
	public static final byte TUPLE             = 12;
	public static final byte LIST_BUILDER      = 13;
	public static final byte BOOL              = 14;
	public static final byte NUMERIC               = 15;
	public static final byte ANY               = 16;
	
	public static final byte MODULE 		   = 35;
	public static final byte MEM_VAR           = 36;
	public static final byte USER_OBJ          = 37;

	//Token-Only Types
	public static final byte T_EXTENDED        = 17; // Math Operators
	public static final byte T_DOT             = 18; // (.) special operator
	public static final byte T_FLOAT           = 19; // Numbers with decimals
	public static final byte T_COMMA           = 20; // (,) special operator
	public static final byte T_COLON           = 21; // (:) special operator
	public static final byte T_LIST            = 22; // List literals
	public static final byte T_TICK            = 23; // (`) special operator
	public static final byte T_OPEN_PAREN      = 24; // ( token
	public static final byte T_CLOSE_PAREN     = 25; // ) token
	public static final byte T_OPEN_SQBRACKET  = 26; // [ token
	public static final byte T_CLOSE_SQBRACKET = 27; // ] token
	public static final byte T_OPEN_CURLY      = 28; // { token
	public static final byte T_CLOSE_CURLY     = 29; // } token
	public static final byte T_OP_DOT          = 30; // .<op> token
	public static final byte T_OP_MATH         = 31; // M<op> token
	public static final byte T_POUND           = 32; // (#) special operator
	public static final byte T_BOOL_FALSE      = 33; // (T) true literal
	public static final byte T_BOOL_TRUE       = 34; // (F) false literal

	
	
	/* BASIC TYPE CHECKING */
	
	public static boolean isNumeric(Object o)		{ return o instanceof Numeric; }
	public static boolean isNum(Object o)			{ return o instanceof Num; }
	public static boolean isBigNum(Object o)			{ return o instanceof BigNum; }
	public static boolean isFlag(Object o)			{ return o instanceof Flag; }
	public static boolean isBlock(Object o)			{ return o instanceof Block && !(o instanceof ListLiteral); }
	
	public static boolean isChar(Object o)			{ return o instanceof Character; }
	public static boolean isOp(Object o)			{ return o instanceof Operation; }
	public static boolean isVarSet(Object o)		{ return o instanceof VariableSet; }
	public static boolean isVar(Object o)			{ return o instanceof Variable; }
	public static boolean isModule(Object o)		{ return o instanceof Module; }
	public static boolean isMemVar(Object o)		{ return o instanceof MemberVariable; }
	public static boolean isLambda(Object o)		{ return o instanceof Lambda; }
	public static boolean isTuple(Object o)			{ return o instanceof Tuple; }
	public static boolean isListBuilder(Object o)	{ return o instanceof ListBuilder; }
	public static boolean isListLiteral(Object o)	{ return o instanceof ListLiteral; }
	public static boolean isBool(Object o)			{ return o instanceof Boolean; }
	public static boolean isUserObject(Object o)	{ return o instanceof UserObject; }
	
	@SuppressWarnings("unchecked")
	public static boolean isString(Object o) {
		if (o instanceof String) {
			return true;
		} else if (o instanceof ArrayList) {
			for(Object c : (ArrayList<Object>)o) {
				if (!(c instanceof Character)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static boolean isList(Object o)				{ return o instanceof ArrayList || o instanceof String; }
	
	@SuppressWarnings("unchecked")
	public static boolean possibleUserType(Object o) {
		if (o instanceof ArrayList) {
			ArrayList<Object> list = (ArrayList<Object>)o;
			return (list.size() == 2 
					&& list.get(0) instanceof Module 
					&& list.get(1) instanceof ArrayList);
		}
		return false;
	}
	
	/* GENERALIZED TYPE CHECKING */
	
	public static boolean isInstruction(Object o) 	{ return isVar(o) || isOp(o) || isFlag(o) || isVarSet(o) || isLambda(o) || isTuple(o); }
	public static boolean isLiteral(Object o) 		{ return !isInstruction(o); }
	
	/** Returns the type ID of the object */
	public static byte getTypeID(Object o) { return 	o instanceof Num			? NUM
													:	o instanceof BigNum			? BIGNUM
													:	o instanceof Flag			? FLAG
													:	o instanceof Block			? BLOCK
													:	o instanceof Character		? CHARACTER
													:	o instanceof Operation		? OP
													:	o instanceof VariableSet	? VAR_SET
													:	o instanceof Variable		? VAR
													:	isString(o)					? STRING	//Check String First
													:	o instanceof ArrayList		? LIST		//Then list
													:	o instanceof Lambda			? LAMBDA
													:	o instanceof Tuple			? TUPLE
													:	o instanceof ListBuilder	? LIST_BUILDER
													:	o instanceof Boolean		? BOOL
													:   o instanceof Module         ? MODULE
													:	UNKNOWN;}

	
	
	/* MULTIPLE TYPE CHECKING  */
	
	public static boolean bothNum(Object a, Object b) 		{ return a instanceof Num && b instanceof Num; }
	public static boolean bothBigNum(Object a, Object b) 	{ return a instanceof BigNum && b instanceof BigNum; }
	public static boolean bothNumeric(Object a, Object b) 	{ return isNumeric(a) && isNumeric(b); }
	public static boolean bothChar(Object a, Object b)		{ return a instanceof Character && b instanceof Character; }
	public static boolean bothString(Object a, Object b)	{ return isString(a) && isString(b); }
	public static boolean bothBool(Object a, Object b)		{ return a instanceof Boolean && b instanceof Boolean; }
	public static boolean bothList(Object a, Object b)		{ return a instanceof ArrayList && b instanceof ArrayList; }
	
	public static boolean anyInt(Object a, Object b) 		{ return a instanceof Integer || b instanceof Integer; }
	public static boolean anyDouble(Object a, Object b) 	{ return a instanceof Double || b instanceof Double; }
	public static boolean anyNum(Object a, Object b) 		{ return a instanceof Num || b instanceof Num; }
	public static boolean anyBigNum(Object a, Object b) 	{ return a instanceof BigNum || b instanceof BigNum; }
	public static boolean anyNumeric(Object a, Object b) 	{ return a instanceof Numeric || b instanceof Numeric; }
	public static boolean anyChar(Object a, Object b)		{ return a instanceof Character || b instanceof Character; }
	public static boolean anyString(Object a, Object b)		{ return isString(a) ||isString(b); }
	public static boolean anyList(Object a, Object b)		{ return a instanceof ArrayList || b instanceof ArrayList; }
	public static boolean anyUserObject(Object a, Object b) { return a instanceof UserObject || b instanceof UserObject; }

	
	/* BASIC CONVERSION */

	public static Block toBlock(Object o) 				{ return (Block)o; }	
	/** Cast <code>o</code> to the <code>Numeric</code> abstract type */
	public static Numeric toNumeric(Object o) 			{ return (Numeric)o; }
	public static Num toNum(Object o) 					{ return (Num)o; }
	public static BigNum toBigNum(Object o) 			{ return (BigNum)o; }
	public static char toChar(Object o) 				{ return (Character)o; }
	public static Operation toOp(Object o) 				{ return (Operation)o; }
	public static VariableSet toVarSet(Object o) 		{ return (VariableSet)o; }
	public static Variable toVar(Object o)				{ return (Variable)o; }
	public static MemberVariable toMemVar(Object o)		{ return (MemberVariable)o; }
	public static Module toModule(Object o)				{ return (Module)o; }
	public static boolean toBool(Object o) 				{ return (Boolean)o; }
	public static Lambda toLambda(Object o) 			{ return (Lambda)o; }
	public static Tuple toTuple(Object o) 				{ return (Tuple)o; }
	public static ListBuilder toListBuider(Object o) 	{ return (ListBuilder)o; }
	public static ListLiteral toListLiteral(Object o) 	{ return (ListLiteral)o; }
	public static UserObject toUserObject(Object o) 	{ return (UserObject)o; }
	@SuppressWarnings("rawtypes")
	public static String getString(Object o) 			{ 
		if(o instanceof ArrayList) {
			StringBuilder sb = new StringBuilder();
			for (Object c : (ArrayList)o) {
				sb.append((Character)c);
			}
			return sb.toString();
		}
		return (String)o;
	}
	/** returns the item casted as a list */
	@SuppressWarnings("unchecked")
	public static ArrayList<Object> toList(Object o) {
		if(o instanceof String) {
			ArrayList<Object> out = new ArrayList<Object>();
			for(Character c : ((String)o).toCharArray()) {
				out.add(c);
			}
			o = out;
			return out;
		}
		return (ArrayList<Object>)o;
	}


	
	
	/* NUMERIC CASTING */
	public static int castInt(Object o)			{ return  o instanceof Integer 	? (int)o
														: o instanceof Double	? (int)o
														: isNumeric(o)		? toNumeric(o).toInt()
														: isChar(o) 	? toChar(o)
														: 0;}
	
	
	/* OTHER CONVERSIONS */
	
	/** for use printing the item to the console. include type items such as " for strings and ' for chars */
	public static String show(Object o) {
		
		int type = getTypeID(o);
		switch(type) {
		case STRING:
			return "\""+getString(o)+"\"";
		case CHARACTER:
			return "'"+o.toString();
		case LIST:
			StringBuilder sb = new StringBuilder("[ ");			
			for(Object i : toList(o)) {
				sb.append(show(i) + " ");
			}
			return sb.toString() + "]";
		case FLAG:
			int flagID = toFlagID(o);
			if(flagID < 0) {
				flagID*=-1;
				String s = "";
				for (int i = 0; i < flagID; i++) {
					s+="`";
				}
				return s;
			} else {
				return "";
			}
		case VAR_SET:
			return ((VariableSet)o).show();
			
		case USER_OBJ:
			return toUserObject(o).str();
			
		default:
			return o.toString();
		}
	}
	
	
	public static String trimZeros(String s) {
		if(!s.contains("."))
			return s;
		
		int dsi = s.length()-1;
		while(s.charAt(dsi) == '0') {
			dsi--;
		}
		if(s.charAt(dsi) == '.') {
			dsi++;
		}
		return s.substring(0, dsi+1);
	}
	
	/** for use printing the BARE item to the console. include type items such as " for strings and ' for chars */
	public static String printBare(Object o) {
		int type = getTypeID(o);
		switch(type) {
		case STRING:
			return getString(o);
		case CHARACTER:
			return o.toString();
		case LIST:
			StringBuilder sb = new StringBuilder("[ ");			
			for(Object i : toList(o)) {
				sb.append(show(i) + " ");
			}
			return sb.toString() + "]";
		case FLAG:
			int flagID = toFlagID(o);
			if(flagID < 0) {
				flagID*=-1;
				String s = "";
				for (int i = 0; i < flagID; i++) {
					s+="`";
				}
				return s;
			} else {
				return "";
			}
		case VAR_SET:
			return ((VariableSet)o).show();
			
		default:
			return o.toString();
		}
	}
	
	
	/** Tests if two objects are equal */
	public static boolean areEqual(Object a, Object b) {
		//List/String
		if(anyList(a,b) && anyString(a,b)) {
			try {
				a = toList(a);
				b = toList(b);
			} catch (ClassCastException e) {
				return false;
			}
		}
		
		//Number 
		if(bothNumeric(a, b)) {
			return NumMath.compare(toNumeric(a), toNumeric(b)) == 0;
		}
		
		int type = getTypeID(a);
		
		if(type != getTypeID(b)) {
			return false;
		}
		
		
		switch(type) {
		case CHARACTER:
			return toChar(a) == toChar(b);
		case MODULE:
			return toModule(a).id == toModule(b).id;
		case STRING:
			return getString(a).equals(getString(b));
		case LIST:
			ArrayList<Object> l1 = toList(a);
			ArrayList<Object> l2 = toList(b);
			//Check size first
			if(l1.size() != l2.size()) {
				return false;
			} 
			//Check each element
			for (int i = 0; i < l1.size(); i++) {
				if(!areEqual(l1.get(i), l2.get(i))) {
					return false;
				}
			}
			return true;
			default:
				return false;
		}
	}
	

	
	/** returns arrayList.size or string.length. assumes `o` is a string or list */
	@SuppressWarnings("unchecked")
	public static int length(Object o) {
		if(o instanceof String) {
			return ((String)o).length();
		}
		return ((ArrayList<Object>)o).size();
	}
	
	/** Returns the byte ID for the flag */
	public static byte toFlagID(Object o) {
		return ((Flag)o).getID();
	}
	
	
	/** Converts a string to a ArrayList of characters */
	public static ArrayList<Character> toCharList(final String s) {
		return new ArrayList<Character>(new AbstractList<Character>() {
		       public int size() { return s.length(); }
		       public Character get(int index) { return s.charAt(index); }
		    });
	}	
	
	/**Returns the name of the type of the object*/
	public static String getTypeName(Object o) {
		if (isModule(o)) {
			return ":" + toModule(o).name();
		} else if (isUserObject(o)) {
			return "." + toUserObject(o).name();
		} else {
			return getTypeNameFromID(getTypeID(o));
		}
	}
	
	/**Returns the name of the type of the id */
	public static String getTypeNameFromID(byte id) {
		switch (id) {
			case NUM:
				return "BASIC_NUM";
			case BIGNUM:
				return "BIG_NUM";
			case FLAG:
				return "FLAG";
			case BLOCK:
				return "BLOCK";
			case LIST:
				return "LIST";
			case CHARACTER:
				return "CHARACTER";
			case OP:
				return "OP";
			case VAR_SET:
				return "VAR_SET";
			case VAR:
				return "VAR";
			case MEM_VAR:
				return "MEM VAR";
			case STRING:
				return "STRING";
			case LAMBDA:
				return "LAMBDA";
			case TUPLE:
				return "TUPLE";
			case LIST_BUILDER:
				return "LIST_BUILDER";
			case BOOL:
				return "BOOL"; 
			case ANY:
				return "ANY";
			case MODULE:
				return "MODULE";
			case USER_OBJ:
				return "USER_OBJECT";
			case UNKNOWN:
				return "UNKNOWN";
			default:
				return "SET_UP_TYPE(getTypeName())";
		}
	}
	
	/** Converts a character abbreviation to its ID */
	public static byte abbrvToID(char c) {
		switch(c) {
			case 'B': return BOOL;
			case 'C': return CHARACTER;
			case 'D': return NUM;
			case 'E': return BLOCK;
			case 'F': return BIGNUM;
			case 'L': return LIST;
			case 'S': return STRING;
			case 'N': return NUMERIC;
			case 'A': return ANY;
			case 'M': return MODULE;
			case 'U': return USER_OBJ;
			default: return UNKNOWN;
		}
	}
	
	/** Converts an ID to a char abbreviation */
	public static char IDToAbbrv(byte b) {
		switch(b) {
		case BOOL : return 'B';
		case CHARACTER : return 'C' ;
		case NUM : return 'D';
		case BLOCK : return 'E';
		case BIGNUM : return 'F';
		case LIST : return 'L';
		case STRING : return 'S';
		case NUMERIC : return 'N';
		case ANY : return 'A';
		case MODULE : return 'M';
		case USER_OBJ : return 'U';
		default: return '?';
		}
	}
	
	/** Returns a string representation of the object */
	public static String castString(Object o) {
		int typeID = getTypeID(o);
		if(typeID == FLAG) {
			return "ID=" + toFlagID(o);
		} else if (isString(o)) {
			return getString(o);
		} else if (typeID == LIST) {
			StringBuilder sb = new StringBuilder("[ ");			
			for(Object i : toList(o)) {
				sb.append(castString(i) + " ");
			}
			return sb.toString() + "]";
		} else if (o == null) {
			return "null";
		} else if (typeID == USER_OBJ) {
			return toUserObject(o).str();
		}
		
		else {
			return o.toString();
		}
	}
	
	/** Returns a string representation of the object and its type name */
	public static String debugString(Object o) {
		return "<"+getTypeName(o)+":"+castString(o)+">";
	}
	
	/**Checks whether or not an object is a member of a certain type */
	public static boolean isType(byte id, Object o) {
		if(id == ANY) {
			return true;
		}
		
		byte objID = getTypeID(o);
		if(id == NUMERIC) {
			return objID == NUM || objID == BIGNUM || objID == NUMERIC;
		}

		if(id == LIST && objID == STRING) {
			return true;
		}
		
		if(id == STRING && objID == LIST) {
			return isString(o);
		}

		
		return objID == id;

	}
	
}
