package element.parser.tokens;

import element.ElemTypes;

public abstract class Token {
	int type;
	String data;
	
	//IDs
	public static final int STRING 		= ElemTypes.STRING;
	public static final int BLOCK 		= ElemTypes.BLOCK;
	public static final int VAR 		= ElemTypes.VAR;
	public static final int EXTENDED 	= ElemTypes.T_EXTENDED;
	public static final int OP 			= ElemTypes.OP;
	public static final int INT 		= ElemTypes.INT;
	public static final int DOT 		= ElemTypes.T_DOT;
	public static final int CHAR 		= ElemTypes.CHARACTER;
	public static final int FLOAT 		= ElemTypes.T_FLOAT;
	public static final int LAMBDA 		= ElemTypes.LAMBDA;
	public static final int COMMA		= ElemTypes.T_COMMA;
	public static final int COLON		= ElemTypes.T_COLON;
	public static final int LIST 		= ElemTypes.T_LIST;
	public static final int TICK		= ElemTypes.T_TICK;
	public static final int NUM			= ElemTypes.NUM;
	public static final int BOOL		= ElemTypes.BOOL;
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
	public static final int BOOL_FALSE      = ElemTypes.T_BOOL_FALSE;
	public static final int BOOL_TRUE       = ElemTypes.T_BOOL_TRUE;
	
	
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
