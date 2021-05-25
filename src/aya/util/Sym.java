package aya.util;

import aya.Aya;
import aya.obj.symbol.Symbol;

public class Sym {
	public static Symbol sym(String s)
	{
		return Aya.getInstance().getSymbols().getSymbol(s);
	}	
}
