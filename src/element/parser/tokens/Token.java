package element.parser.tokens;

import element.ElemTypes;
import element.obj.Obj;

public abstract class Token {
	int type;
	String data;
	
	//IDs
	public static final int STRING 		= Obj.STR;
	public static final int BLOCK 		= Obj.BLOCK;
	public static final int VAR 		= ElemTypes.VAR;
	public static final int EXTENDED 	= ElemTypes.T_EXTENDED;
	public static final int OP 			= ElemTypes.OP;
	public static final int BIGNUM  	= Obj.BIGNUM;
	public static final int NUM	        = Obj.NUM;
	//public static final int INT 		= ElemTypes.INT;
	public static final int DOT 		= ElemTypes.T_DOT;
	public static final int CHAR 		= Obj.CHAR;
	//public static final int FLOAT 	= ElemTypes.T_FLOAT;
	public static final int LAMBDA 		= ElemTypes.LAMBDA;
	public static final int COMMA		= ElemTypes.T_COMMA;
	public static final int COLON		= ElemTypes.T_COLON;
	public static final int LIST 		= ElemTypes.T_LIST;
	public static final int TICK		= ElemTypes.T_TICK;
	public static final int NUMERIC		= Obj.NUMBER;
//	public static final int BOOL		= ElemTypes.BOOL;
	public static final int MEM_VAR		= ElemTypes.MEM_VAR;
	

	public static final int OPEN_PAREN      = ElemTypes.T_OPEN_PAREN;
	public static final int CLOSE_PAREN     = ElemTypes.T_CLOSE_PAREN;
	public static final int OPEN_SQBRACKET  = ElemTypes.T_OPEN_SQBRACKET;
	public static final int CLOSE_SQBRACKET = ElemTypes.T_CLOSE_SQBRACKET;
	public static final int OPEN_CURLY      = ElemTypes.T_OPEN_CURLY;
	public static final int CLOSE_CURLY     = ElemTypes.T_CLOSE_CURLY;
	public static final int OP_DOT          = ElemTypes.T_OP_DOT;
	public static final int OP_MATH         = ElemTypes.T_OP_MATH;
	public static final int POUND           = ElemTypes.T_POUND;
	public static final int DOT_COLON		= ElemTypes.T_DOT_COLON;
	
	
	protected Token(int type) {
		this.type = type;
	}
	
	public boolean isa(int type) {
		return this.type == type;
	}
	
	public int getType() {
		return type;
	}
	
	public String getData() {
		return data;
	}
	
	abstract public Object getElementObj();	
	abstract public String typeString();
	
	public String toString() {
		return data;
	}
	
}
