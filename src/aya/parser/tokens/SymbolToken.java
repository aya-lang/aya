package aya.parser.tokens;

import aya.obj.Obj;
import aya.obj.symbol.Symbol;

public class SymbolToken extends StdToken {
		
	public SymbolToken(String data) {
		super(data, Token.SYMBOL);
	}

	
	@Override
	public Obj getAyaObj() {
		return Symbol.fromStr(data);
	}

	@Override
	public String typeString() {
		return "symbol";
	}
}
