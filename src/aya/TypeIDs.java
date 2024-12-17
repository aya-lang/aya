package aya;

public class TypeIDs {
	
	// Type IDs
	public static final byte FLAG              = 101;
	public static final byte OP                = 102;
	public static final byte VAR_SET           = 103;
	public static final byte VAR               = 104;
	public static final byte LAMBDA            = 105;
	public static final byte TUPLE             = 106;
	public static final byte LIST_BUILDER      = 107;
	public static final byte KEY_VAR           = 108;

	//Token-Only Types
	public static final byte T_EXTENDED        = 109; // Math Operators
	public static final byte T_DOT             = 110; // (.) special operator
	public static final byte T_FLOAT           = 111; // Numbers with decimals
	public static final byte T_COMMA           = 112; // (,) special operator
	public static final byte T_COLON           = 113; // (:) special operator
	public static final byte T_LIST            = 114; // List literals
	public static final byte T_TICK            = 115; // (`) special operator
	public static final byte T_OPEN_PAREN      = 116; // ( token
	public static final byte T_CLOSE_PAREN     = 117; // ) token
	public static final byte T_OPEN_SQBRACKET  = 118; // [ token
	public static final byte T_CLOSE_SQBRACKET = 119; // ] token
	public static final byte T_OPEN_CURLY      = 120; // { token
	public static final byte T_CLOSE_CURLY     = 121; // } token
	public static final byte T_OP_DOT          = 122; // .<op> token
	public static final byte T_OP_MATH         = 123; // M<op> token
	public static final byte T_POUND           = 124; // (#) special operator
	public static final byte T_DOT_COLON       = 125; // (.:) dot colon operator
	public static final byte T_FN_QUOTE 	   = 126; // (.`) function quote
	public static final byte T_COLON_POUND 	   = 127; // (:#) special operator
	public static final byte T_NAMED_OP        = 90;  // :(...)
}
