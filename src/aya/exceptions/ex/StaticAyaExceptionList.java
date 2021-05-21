package aya.exceptions.ex;

import java.util.HashMap;

import aya.exceptions.runtime.AyaRuntimeException;
import aya.exceptions.runtime.EmptyStackError;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.IndexError;
import aya.exceptions.runtime.InvalidReferenceError;
import aya.exceptions.runtime.MathError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.UndefVarException;
import aya.exceptions.runtime.ValueError;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

public class StaticAyaExceptionList {
	static HashMap<Symbol, AyaException> exceptions = null;
	static HashMap<Symbol, AyaRuntimeException> runtime_exceptions = null;
	
	public static HashMap<Symbol, AyaException> getExceptions() {
		if (exceptions == null) {
			exceptions = new HashMap<Symbol, AyaException>();
			AyaException ex;

			ex = new EndOfInputError("");
			exceptions.put(ex.typeSymbol(), ex);

			ex = new NotAnOperatorError("");
			exceptions.put(ex.typeSymbol(), ex);

			ex = new ParserException("");
			exceptions.put(ex.typeSymbol(), ex);

			ex = new SyntaxError("");
			exceptions.put(ex.typeSymbol(), ex);
		}
		return exceptions;
	}

	public static HashMap<Symbol, AyaRuntimeException> getRuntimeExceptions() {
		if (runtime_exceptions == null) {
			runtime_exceptions = new HashMap<Symbol, AyaRuntimeException>();
			AyaRuntimeException ex;

			ex = new EmptyStackError("");
			runtime_exceptions.put(ex.typeSymbol(), ex);

			ex = new IndexError("");
			runtime_exceptions.put(ex.typeSymbol(), ex);

			ex = new InvalidReferenceError("", 0);
			runtime_exceptions.put(ex.typeSymbol(), ex);

			ex = new IOError("", "", "");
			runtime_exceptions.put(ex.typeSymbol(), ex);
			
			ex = new MathError("");
			runtime_exceptions.put(ex.typeSymbol(), ex);
			
			ex = new TypeError("");
			runtime_exceptions.put(ex.typeSymbol(), ex);

			ex = new UndefVarException(SymbolConstants.A);
			runtime_exceptions.put(ex.typeSymbol(), ex);

			ex = new ValueError("");
			runtime_exceptions.put(ex.typeSymbol(), ex);
		}
		return runtime_exceptions;
	}
}
