package aya.obj.symbol;

import aya.Aya;

public class SymbolConstants {
	private static final SymbolTable S = Aya.getInstance().getSymbols();
	
	public static final Symbol E 		= S.getSymbol("e");
	public static final Symbol X 		= S.getSymbol("x");
	public static final Symbol Y 		= S.getSymbol("y");

	public static final Symbol ANY 		= S.getSymbol("any");
	public static final Symbol CHAR 	= S.getSymbol("char");
	public static final Symbol NUM 		= S.getSymbol("num");
	public static final Symbol BLOCK 	= S.getSymbol("block");
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
	public static final Symbol AUTOFLUSH 	= S.getSymbol("autoflush");
	public static final Symbol SHOW 	= S.getSymbol("show");

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
	public static final Symbol KEYVAR_PUSHSELF    	= S.getSymbol("__pushself__");
}
