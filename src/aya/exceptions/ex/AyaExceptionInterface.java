package aya.exceptions.ex;

import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

// Interface shared by all Aya Exceptions
//   - AyaException
//   - AyaRuntimeException
public interface AyaExceptionInterface {
	
	public Symbol typeSymbol();
	public Dict getDict();
	public SourceStringRef getSource();

}
