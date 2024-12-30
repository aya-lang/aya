package aya.parser.tokens;

import aya.TypeIDs;
import aya.exceptions.parser.ParserException;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.parser.SourceStringRef;

public abstract class Token {
	int type;
	String data;
	SourceStringRef source;
	
	//IDs
	public static final int STRING 		= Obj.STR;
	public static final int BLOCK 		= Obj.BLOCK;
	public static final int BIGNUM  	= Obj.BIGNUM;
	public static final int NUM	        = Obj.NUM;
	public static final int CHAR 		= Obj.CHAR;
	public static final int NUMERIC		= Obj.NUMBER;
	public static final int SYMBOL 		= Obj.SYMBOL;

	
	public static final int VAR 		= TypeIDs.VAR;
	public static final int EXTENDED 	= TypeIDs.T_EXTENDED;
	public static final int OP 			= TypeIDs.OP;
	public static final int DOT 		= TypeIDs.T_DOT;
	public static final int KEY_VAR		= TypeIDs.KEY_VAR;
	public static final int LAMBDA 		= TypeIDs.LAMBDA;
	public static final int COMMA		= TypeIDs.T_COMMA;
	public static final int COLON		= TypeIDs.T_COLON;
	public static final int LIST 		= TypeIDs.T_LIST;
	public static final int TICK		= TypeIDs.T_TICK;
	
	public static final int OPEN_PAREN      = TypeIDs.T_OPEN_PAREN;
	public static final int CLOSE_PAREN     = TypeIDs.T_CLOSE_PAREN;
	public static final int OPEN_SQBRACKET  = TypeIDs.T_OPEN_SQBRACKET;
	public static final int CLOSE_SQBRACKET = TypeIDs.T_CLOSE_SQBRACKET;
	public static final int OPEN_CURLY      = TypeIDs.T_OPEN_CURLY;
	public static final int CLOSE_CURLY     = TypeIDs.T_CLOSE_CURLY;
	public static final int OP_DOT          = TypeIDs.T_OP_DOT;
	public static final int OP_MATH         = TypeIDs.T_OP_MATH;
	public static final int POUND           = TypeIDs.T_POUND;
	public static final int DOT_COLON		= TypeIDs.T_DOT_COLON;
	public static final int FN_QUOTE		= TypeIDs.T_FN_QUOTE;
	public static final int COLON_POUND 	= TypeIDs.T_COLON_POUND;
	public static final int NAMED_OP        = TypeIDs.T_NAMED_OP;
	
	public static final int DICT            = TypeIDs.T_DICT;
	public static final int NEXT_BLOCK_IS_DICT = TypeIDs.T_NEXT_BLOCK_IS_DICT;
	
	
	protected Token(int type, SourceStringRef source) {
		this.type = type;
		this.source = source;
		if (this.source == null) {
			throw new AssertionError();
		}
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
	
	abstract public Instruction getInstruction() throws ParserException;	
	abstract public String typeString();
	
	public String toString() {
		return data;
	}
	
	public SourceStringRef getSourceStringRef() {
		return this.source;
	}
	
}
