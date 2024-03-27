package aya.util;

import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;

public class Sym {
	public static Symbol sym(String s)
	{
		return SymbolTable.getSymbol(s);
	}	
}
