package element;

import java.math.BigDecimal;
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
import element.entities.number.BasicNum;
import element.entities.number.BigNum;
import element.entities.number.Num;
import element.exceptions.ElementRuntimeException;
import element.variable.MemberVariable;
import element.variable.Module;
import element.variable.Variable;
import element.variable.VariableSet;

public class ElemTypes {
	
	// Type IDs
	public static final byte UNKNOWN           = -1;
	public static final byte BASIC_NUM         = 0;
	public static final byte BIG_NUM           = 1;
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
	public static final byte NUM               = 15;
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

	
	
	/* Big Decimal Constants */
	//public static final BigDecimal BD_ZERO = new BigDecimal("0");
	//public static final BigDecimal BD_ONE = new BigDecimal("1");
	//public static final BigDecimal BD_NEG_ONE = new BigDecimal("-1");
	
	/* BASIC TYPE CHECKING */
	
	//public static boolean isInt(Object o)			{ return o instanceof Integer; }
	//public static boolean isDouble(Object o)		{ return o instanceof Double; }
	public static boolean isNum(Object o)			{ return o instanceof Num; }
	public static boolean isBasicNum(Object o)			{ return o instanceof BasicNum; }
	public static boolean isBigNum(Object o)			{ return o instanceof BigNum; }
	public static boolean isFlag(Object o)			{ return o instanceof Flag; }
	public static boolean isBlock(Object o)			{ return o instanceof Block && !(o instanceof ListLiteral); }
	
	//public static boolean isListOrStr(Object o)		{ return o instanceof ArrayList || o instanceof String;}
	public static boolean isChar(Object o)			{ return o instanceof Character; }
	public static boolean isBig(Object o)			{ return o instanceof BigDecimal; }
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
	public static byte getTypeID(Object o) { return 	o instanceof BasicNum		? BASIC_NUM
													:	o instanceof BigNum			? BIG_NUM
													:	o instanceof Flag			? FLAG
													:	o instanceof Block			? BLOCK
													:	o instanceof Character		? CHARACTER
												//	:	o instanceof BigDecimal		? BIG_DECIMAL
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
	
	//public static boolean bothInt(Object a, Object b) 		{ return a instanceof Integer && b instanceof Integer; }
	//public static boolean bothDouble(Object a, Object b) 	{ return a instanceof Double && b instanceof Double; }
	public static boolean bothBasicNum(Object a, Object b) 	{ return a instanceof BasicNum && b instanceof BasicNum; }
	public static boolean bothBigNum(Object a, Object b) 	{ return a instanceof BigNum && b instanceof BigNum; }
	public static boolean bothNum(Object a, Object b) 		{ return isNum(a) && isNum(b); }
	public static boolean bothBig(Object a, Object b)		{ return a instanceof BigDecimal && b instanceof BigDecimal; }
	public static boolean bothChar(Object a, Object b)		{ return a instanceof Character && b instanceof Character; }
	public static boolean bothString(Object a, Object b)	{ return isString(a) && isString(b); }
	public static boolean bothBool(Object a, Object b)		{ return a instanceof Boolean && b instanceof Boolean; }
	public static boolean bothList(Object a, Object b)		{ return a instanceof ArrayList && b instanceof ArrayList; }
	
	public static boolean anyInt(Object a, Object b) 		{ return a instanceof Integer || b instanceof Integer; }
	public static boolean anyDouble(Object a, Object b) 	{ return a instanceof Double || b instanceof Double; }
	public static boolean anyNum(Object a, Object b) 		{ return isNum(a) || isNum(b); }
	public static boolean anyBig(Object a, Object b)		{ return a instanceof BigDecimal || b instanceof BigDecimal; }
	public static boolean anyChar(Object a, Object b)		{ return a instanceof Character || b instanceof Character; }
	public static boolean anyString(Object a, Object b)		{ return isString(a) ||isString(b); }
	public static boolean anyList(Object a, Object b)		{ return a instanceof ArrayList || b instanceof ArrayList; }
	public static boolean anyUserObject(Object a, Object b) { return a instanceof UserObject || b instanceof UserObject; }

	
	/* BASIC CONVERSION */
	
	//public static int toInt(Object o) 					{ return ((Number)o).intValue(); }
	//public static double toDouble(Object o)				{ return ((Number)o).doubleValue(); }
	public static Block toBlock(Object o) 				{ return (Block)o; }
	//public static BigDecimal toBig(Object o) 			{ return (BigDecimal)o; }
	
	public static Num toNum(Object o) 					{ return (Num)o; }
	public static BasicNum toBasicNum(Object o) 		{ return (BasicNum)o; }
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
//	public static int castInt(Object o)			{ return  isInt(o) 		? toInt(o)
//														: isDouble(o) 	? toInt(o)
//														: isBig(o)		? toBig(o).intValue()
//														: isChar(o) 	? toChar(o)
//														: 0;}
//	public static double castDouble(Object o)	{ return  isInt(o) 		? toDouble(o)
//														: isDouble(o) 	? toDouble(o)
//														: isBig(o)		? toBig(o).doubleValue()
//														: isChar(o)		? toDouble(castInt(o))
//														: 0.0;}
//	public static BigDecimal castBig(Object o)	{ try {
//													return  isInt(o) 	? new BigDecimal(toInt(o)) 
//														: isDouble(o) 	? new BigDecimal(toDouble(o))
//														: isBig(o)		? toBig(o)
//														: new BigDecimal(0);
//												  } catch (NumberFormatException e) {
//													  throw new ElementRuntimeException("Cannont cast " + castString(o) + " to a big decimal");
//												  }
//												}
	
	public static BigNum castBigNum(Object o) {
		if (o instanceof BigNum) {
			return (BigNum)o;
		} else if (o instanceof BasicNum) {
			return new BigNum(((BasicNum)o).value());
		} else {
			throw new ElementRuntimeException("Cannont cast " + castString(o) + " to a big decimal");
		}
	}
	
	
	/* OTHER CONVERSIONS */
	
	/** for use printing the item to the console. include type items such as " for strings and ' for chars */
	public static String show(Object o) {
//		if (possibleUserType(o)) {
//			@SuppressWarnings("unchecked")
//			Module m = ((Module)((ArrayList<Object>)o).get(0));
//			if(m.hasVar(MEMVAR_SHOW)) {
//				Object obj_show = m.get(MEMVAR_SHOW);
//				if(isBlock(obj_show)) {
//					Block blk_show = ((Block)obj_show).duplicate();
//					blk_show.push(o);
//					blk_show.eval();
//					Object obj_res = blk_show.pop();
//					return castString(obj_res);
//				}
//			}
//			return castString(o);
//		}
		
		
		int type = getTypeID(o);
		switch(type) {
		case STRING:
			return "\""+getString(o)+"\"";
		case CHARACTER:
			return "'"+o.toString()+"'";
//		case BIG_DECIMAL:
//			return trimZeros(((BigDecimal)o).toPlainString());
//		case DOUBLE:
//			String doubleString = String.format("%1.10f", toDouble(o));
//			return trimZeros(doubleString);
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
	
//	/** Casts any item to any other item */
//	public static Object castGeneral(byte outType, Object item) {
//		if(outType == STRING)
//			return castString(item);
//		
//		
//		byte inType = getTypeID(item);
//		
//		if (inType == NUM || inType == BasicNum || inType || BigNum) {
//			Num num = toNum(item);
//			switch (outType) {
//			case STRING:
//				return num.toString();
//			case INT:
//				
//			}
//		}
//		
//		try {
//			switch (outType) {
//			
//			/* to INT: Valid Conversions
//			 * Num		numeric cast
//			 * String	parseInt(str)
//			 * Char		(char)int	
//			 * Bool		true->1, false->0
//			 */
////			case NUM:
////				Num num = toNum()
////				switch(inType) {
////				
////				}
////			case INT:
////				switch(inType) {
////				case STRING:
////					return Integer.parseInt(getString(item));
////				case CHARACTER:
////					return (int)toChar(item);
////				case BOOL:
////					return toBool(item) ? 1 : 0;
////				default:
////					if(isNum(item)) return castInt(item);
////				}
////			break;
//				
//				
//			/* to DOUBLE: Valid Conversions
//			 * Num		numeric cast
//			 * String	parseInt(str)
//			 */	
////			case DOUBLE:
////				switch(inType) {
////				case STRING:
////					return Double.parseDouble(getString(item));
////				default:
////					if(isNum(item)) return castDouble(item);
////				}
////			break;
//				
//				
//			/* to BIG_DECIMAL: Valid Conversions
//			 * Num		numeric cast
//			 * String	parseInt(str)
//			 */	
//			case BIG_DECIMAL:
//				switch(inType) {
//				case STRING:
//					return new BigDecimal(getString(item));
//				default:
//					if(isNum(item)) return castBig(item);
//				}
//			break;
//			
//			/* to CHARACTER: Valid Conversions
//			 * int		(char)int
//			 * String	charAt(0)
//			 */	
//			case CHARACTER:
//				switch(inType) {
//				case STRING:
//					return getString(item).charAt(0);
//				case INT:
//					return (char)toInt(item);
//				}
//			break;
//
//				
//			}
//			
//		} catch (Exception e) {
//			throw new ElementRuntimeException("Cannot cast item " + show(item) + " to type " + IDToAbbrv((byte)outType));
//		}
//		throw new ElementRuntimeException("Cannot cast item " + show(item) + " to type " + IDToAbbrv((byte)outType));
//
//	}
	
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
		if(bothBasicNum(a, b)) {
			toBasicNum(a).eq(toBasicNum(b));
		} else if (bothNum(a, b)) {
			return toNum(a).toApfloat().compareTo(toNum(b).toApfloat()) == 0;
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
			case BASIC_NUM:
				return "BASIC_NUM";
			case BIG_NUM:
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
			case 'D': return BASIC_NUM;
			case 'E': return BLOCK;
			case 'F': return BIG_NUM;
			case 'L': return LIST;
			case 'S': return STRING;
			case 'N': return NUM;
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
		case BASIC_NUM : return 'D';
		case BLOCK : return 'E';
		case BIG_NUM : return 'F';
		case LIST : return 'L';
		case STRING : return 'S';
		case NUM : return 'N';
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
		if(id == NUM) {
			return objID == BASIC_NUM || objID == BIG_NUM || objID == NUM;
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
