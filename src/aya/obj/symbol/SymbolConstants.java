package aya.obj.symbol;

public class SymbolConstants {
	private static Symbol S(String s) {
		return SymbolTable.getSymbol(s);
	}
	
	public static final Symbol E 		= S("e");
	public static final Symbol X 		= S("x");
	public static final Symbol Y 		= S("y");
	public static final Symbol R 		= S("r");
	public static final Symbol G 		= S("g");
	public static final Symbol B 		= S("b");
	public static final Symbol A 		= S("a");
	public static final Symbol F 		= S("f");


	public static final Symbol ANY 		= S("any");
	public static final Symbol CHAR 	= S("char");
	public static final Symbol NUM 		= S("num");
	public static final Symbol BLOCK 	= S("block");
	public static final Symbol LIST 	= S("list");
	public static final Symbol STR 		= S("str");
	public static final Symbol DICT 	= S("dict");
	public static final Symbol SYM	 	= S("sym");
	public static final Symbol UNKNOWN 	= S("unknown");

	public static final Symbol HELP 	= S("help");
	public static final Symbol VERSION 	= S("version");
	public static final Symbol PI 		= S("pi");
	public static final Symbol NIL 		= S("nil");
	public static final Symbol OVERLOAD = S("overload");
	public static final Symbol NAME 	= S("name");
	public static final Symbol TYPE 	= S("type");
	public static final Symbol DOC 		= S("doc");
	public static final Symbol CALL		= S("call");
	public static final Symbol STD		= S("std");
	public static final Symbol DOT		= S("dot");
	public static final Symbol COLON	= S("colon");
	public static final Symbol MISC		= S("misc");
	public static final Symbol ARGS 	= S("args");
	public static final Symbol ARG  	= S("arg");
	public static final Symbol ARGTYPES = S("argtypes");
	public static final Symbol LOCALS 	= S("locals");
	public static final Symbol COPY 	= S("copy");
	public static final Symbol PLAIN 	= S("plain");
	public static final Symbol QUESTION = S("question");
	public static final Symbol WARN 	= S("warn");
	public static final Symbol ERROR 	= S("error");
	public static final Symbol WIDTH 	= S("width");
	public static final Symbol HEIGHT 	= S("height");
	public static final Symbol SCALE 	= S("scale");
	public static final Symbol AUTOFLUSH= S("autoflush");
	public static final Symbol SHOW 	= S("show");
	public static final Symbol MSG    	= S("msg");

	public static final Symbol DAY_OF_WEEK 	= S("day_of_week");
	public static final Symbol YEAR 		= S("year");
	public static final Symbol MONTH 		= S("month");
	public static final Symbol DAY_OF_MONTH = S("day_of_month");
	public static final Symbol HOUR			= S("hour");
	public static final Symbol MINUTE		= S("minute");
	public static final Symbol SECOND		= S("second");
	public static final Symbol MS 			= S("ms");
	public static final Symbol STAMP		= S("stamp");
	public static final Symbol MESSAGE		= S("message");
	public static final Symbol EXPECTED		= S("expected");
	public static final Symbol RECEIVED		= S("received");
	public static final Symbol SOURCE 		= S("source");
	public static final Symbol OPTYPE 		= S("optype");
	public static final Symbol VECTORIZED   = S("vectorized");
	public static final Symbol OPS          = S("ops");
	public static final Symbol FILE         = S("file");
	public static final Symbol LINE         = S("line");
	public static final Symbol COL          = S("col");
	public static final Symbol CONTEXT      = S("context");
	public static final Symbol DATA         = S("data");
	public static final Symbol SLURP        = S("slurp");
	public static final Symbol UNPACK       = S("unpack");
	public static final Symbol ARGTYPE      = S("argtype");
	public static final Symbol SIMPLE       = S("simple");
	public static final Symbol TYPED        = S("typed");
	public static final Symbol OBJECT       = S("object");
	public static final Symbol UNION        = S("union");
	public static final Symbol INNER        = S("inner");
	public static final Symbol OUTER        = S("outer");





	public static final Symbol KEYVAR_EQ		= S("__eq__");
	public static final Symbol KEYVAR_NEW 		= S("__new__");
	public static final Symbol KEYVAR_TYPE 		= S("__type__");
	public static final Symbol KEYVAR_META 		= S("__meta__");
	public static final Symbol KEYVAR_GETINDEX 	= S("__getindex__");
	public static final Symbol KEYVAR_SETINDEX 	= S("__setindex__");
	public static final Symbol KEYVAR_HEAD 		= S("__head__");
	public static final Symbol KEYVAR_TAIL 		= S("__tail__");
	public static final Symbol KEYVAR_MAP 		= S("__map__");
	public static final Symbol KEYVAR_LEN 		= S("__len__");
	public static final Symbol KEYVAR_OR		= S("__or__");
	public static final Symbol KEYVAR_ROR		= S("__ror__");
	public static final Symbol KEYVAR_FLOAT 	= S("__float__");
	public static final Symbol KEYVAR_EACH 		= S("__each__");
	public static final Symbol KEYVAR_STR 		= S("__str__");
	public static final Symbol KEYVAR_REPR 		= S("__repr__");
	public static final Symbol KEYVAR_BOOL    	= S("__bool__");
	public static final Symbol KEYVAR_PUSHSELF  = S("__pushself__");
	public static final Symbol __CALL__    		= S("__call__");
	public static final Symbol __TYPE__    		= S("__type__");
	public static final Symbol __TYPE_CHECK__ 	= S("__type_check__");



	public static final Symbol UNDEF_VAR 		= S("undef_var_err");
	public static final Symbol SYNTAX_ERR 		= S("syntax_err");
	public static final Symbol VALUE_ERR 		= S("value_err");
	public static final Symbol TYPE_ERR 		= S("type_err");
	public static final Symbol INVALID_REF_ERR  = S("invalid_ref_err");
	public static final Symbol IO_ERR 			= S("io_err");
	public static final Symbol EMPTY_STACK_ERR  = S("empty_stack_err");
	public static final Symbol END_OF_INPUT_ERR = S("end_of_input_err");
	public static final Symbol PARSER_ERR 		= S("parser_err");
	public static final Symbol INDEX_ERR 		= S("index_err");
	public static final Symbol MATH_ERR 		= S("math_err");
	public static final Symbol NOT_AN_OP_ERROR  = S("not_an_op_err");
	public static final Symbol ASSERT_ERROR     = S("assert_err");
	public static final Symbol UNIMPL_ERROR     = S("unimpl_err");
	public static final Symbol THREAD_ERR       = S("thread_err");

	public static final Symbol EXCEPTION            = S("exception");
	public static final Symbol RUNTIME_EXCEPTION    = S("runtime_exception");

	public static final Symbol __CDICT__    = S("__cdict__");
	public static final Symbol UNDERSCORE   = S("_");
}
