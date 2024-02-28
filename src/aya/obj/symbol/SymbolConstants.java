package aya.obj.symbol;

import aya.Aya;

public class SymbolConstants {
	private static final SymbolTable S = Aya.getInstance().getSymbols();
	
	public static final Symbol E 		= S.getSymbol("e");
	public static final Symbol X 		= S.getSymbol("x");
	public static final Symbol Y 		= S.getSymbol("y");
	public static final Symbol R 		= S.getSymbol("r");
	public static final Symbol G 		= S.getSymbol("g");
	public static final Symbol B 		= S.getSymbol("b");
	public static final Symbol A 		= S.getSymbol("a");

	public static final Symbol ANY 		= S.getSymbol("any");
	public static final Symbol CHAR 	= S.getSymbol("char");
	public static final Symbol NUM 		= S.getSymbol("num");
	public static final Symbol BLOCK 	= S.getSymbol("blockEvaluator");
	public static final Symbol LIST 	= S.getSymbol("list");
	public static final Symbol STR 		= S.getSymbol("str");
	public static final Symbol DICT 	= S.getSymbol("dict");
	public static final Symbol SYM	 	= S.getSymbol("sym");
	public static final Symbol UNKNOWN 	= S.getSymbol("unknown");

	public static final Symbol HELP 	= S.getSymbol("help");
	public static final Symbol VERSION 	= S.getSymbol("version");
	public static final Symbol PI 		= S.getSymbol("pi");
	public static final Symbol NIL 		= S.getSymbol("nil");
	public static final Symbol OVERLOAD = S.getSymbol("overload");
	public static final Symbol NAME 	= S.getSymbol("name");
	public static final Symbol TYPE 	= S.getSymbol("type");
	public static final Symbol DOC 		= S.getSymbol("doc");
	public static final Symbol CALL		= S.getSymbol("call");
	public static final Symbol STD		= S.getSymbol("std");
	public static final Symbol DOT		= S.getSymbol("dot");
	public static final Symbol COLON	= S.getSymbol("colon");
	public static final Symbol MISC		= S.getSymbol("misc");
	public static final Symbol ARGS 	= S.getSymbol("args");
	public static final Symbol ARG  	= S.getSymbol("arg");
	public static final Symbol ARGTYPES = S.getSymbol("argtypes");
	public static final Symbol LOCALS 	= S.getSymbol("locals");
	public static final Symbol COPY 	= S.getSymbol("copy");
	public static final Symbol PLAIN 	= S.getSymbol("plain");
	public static final Symbol QUESTION = S.getSymbol("question");
	public static final Symbol WARN 	= S.getSymbol("warn");
	public static final Symbol ERROR 	= S.getSymbol("error");
	public static final Symbol WIDTH 	= S.getSymbol("width");
	public static final Symbol HEIGHT 	= S.getSymbol("height");
	public static final Symbol SCALE 	= S.getSymbol("scale");
	public static final Symbol AUTOFLUSH= S.getSymbol("autoflush");
	public static final Symbol SHOW 	= S.getSymbol("show");
	public static final Symbol MSG    	= S.getSymbol("msg");

	public static final Symbol DAY_OF_WEEK 	= S.getSymbol("day_of_week");
	public static final Symbol YEAR 		= S.getSymbol("year");
	public static final Symbol MONTH 		= S.getSymbol("month");
	public static final Symbol DAY_OF_MONTH = S.getSymbol("day_of_month");
	public static final Symbol HOUR			= S.getSymbol("hour");
	public static final Symbol MINUTE		= S.getSymbol("minute");
	public static final Symbol SECOND		= S.getSymbol("second");
	public static final Symbol MS 			= S.getSymbol("ms");
	public static final Symbol STAMP		= S.getSymbol("stamp");
	public static final Symbol MESSAGE		= S.getSymbol("message");
	public static final Symbol EXPECTED		= S.getSymbol("expected");
	public static final Symbol RECEIVED		= S.getSymbol("received");
	public static final Symbol SOURCE 		= S.getSymbol("source");
	public static final Symbol OPTYPE 		= S.getSymbol("optype");
	public static final Symbol VECTORIZED   = S.getSymbol("vectorized");
	public static final Symbol OPS          = S.getSymbol("ops");
	public static final Symbol FILE         = S.getSymbol("file");
	public static final Symbol LINE         = S.getSymbol("line");
	public static final Symbol COL          = S.getSymbol("col");
	public static final Symbol CONTEXT      = S.getSymbol("context");
	public static final Symbol DATA         = S.getSymbol("data");
	public static final Symbol SLURP        = S.getSymbol("slurp");
	public static final Symbol UNPACK       = S.getSymbol("unpack");
	public static final Symbol ARGTYPE      = S.getSymbol("argtype");
	public static final Symbol SIMPLE       = S.getSymbol("simple");
	public static final Symbol TYPED        = S.getSymbol("typed");

	public static final Symbol KEYVAR_EQ		= S.getSymbol("__eq__");
	public static final Symbol KEYVAR_NEW 		= S.getSymbol("__new__");
	public static final Symbol KEYVAR_TYPE 		= S.getSymbol("__type__");
	public static final Symbol KEYVAR_META 		= S.getSymbol("__meta__");
	public static final Symbol KEYVAR_GETINDEX 	= S.getSymbol("__getindex__");
	public static final Symbol KEYVAR_SETINDEX 	= S.getSymbol("__setindex__");
	public static final Symbol KEYVAR_HEAD 		= S.getSymbol("__head__");
	public static final Symbol KEYVAR_TAIL 		= S.getSymbol("__tail__");
	public static final Symbol KEYVAR_MAP 		= S.getSymbol("__map__");
	public static final Symbol KEYVAR_LEN 		= S.getSymbol("__len__");
	public static final Symbol KEYVAR_OR		= S.getSymbol("__or__");
	public static final Symbol KEYVAR_ROR		= S.getSymbol("__ror__");
	public static final Symbol KEYVAR_FLOAT 	= S.getSymbol("__float__");
	public static final Symbol KEYVAR_EACH 		= S.getSymbol("__each__");
	public static final Symbol KEYVAR_STR 		= S.getSymbol("__str__");
	public static final Symbol KEYVAR_REPR 		= S.getSymbol("__repr__");
	public static final Symbol KEYVAR_BOOL    	= S.getSymbol("__bool__");
	public static final Symbol KEYVAR_PUSHSELF  = S.getSymbol("__pushself__");
	public static final Symbol __CALL__    		= S.getSymbol("__call__");

	public static final Symbol UNDEF_VAR 		= S.getSymbol("undef_var_err");
	public static final Symbol SYNTAX_ERR 		= S.getSymbol("syntax_err");
	public static final Symbol VALUE_ERR 		= S.getSymbol("value_err");
	public static final Symbol TYPE_ERR 		= S.getSymbol("type_err");
	public static final Symbol INVALID_REF_ERR  = S.getSymbol("invalid_ref_err");
	public static final Symbol IO_ERR 			= S.getSymbol("io_err");
	public static final Symbol EMPTY_STACK_ERR  = S.getSymbol("empty_stack_err");
	public static final Symbol END_OF_INPUT_ERR = S.getSymbol("end_of_input_err");
	public static final Symbol PARSER_ERR 		= S.getSymbol("parser_err");
	public static final Symbol INDEX_ERR 		= S.getSymbol("index_err");
	public static final Symbol MATH_ERR 		= S.getSymbol("math_err");
	public static final Symbol NOT_AN_OP_ERROR  = S.getSymbol("not_an_op_err");
	public static final Symbol ASSERT_ERROR     = S.getSymbol("assert_err");
	public static final Symbol UNIMPL_ERROR     = S.getSymbol("unimpl_err");

	public static final Symbol EXCEPTION            = S.getSymbol("exception");
	public static final Symbol RUNTIME_EXCEPTION    = S.getSymbol("runtime_exception");

	public static final Symbol __CDICT__    = S.getSymbol("__cdict__");



}
