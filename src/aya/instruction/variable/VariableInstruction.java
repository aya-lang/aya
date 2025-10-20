package aya.instruction.variable;

import aya.instruction.Instruction;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.parser.SourceStringRef;
import aya.util.UTF16;

public abstract class VariableInstruction extends Instruction {

	protected final String varName_;
	protected final Symbol originalVar_;
	protected final Symbol[] variables_;

	protected VariableInstruction(SourceStringRef source, Symbol var) {
		super(source);
		originalVar_ = var;
		varName_ = var.name();
		if (varName_.length() == 2 && UTF16.isHighSurrogate(varName_.charAt(0))) {
			// surrogate pair
			String highSurrogate = "" + ((char) (varName_.charAt(0) & 0b11_1111_1111)); // ignore the leading 6 bits (surrogate identifier)
			String lowSurrogate = "" + ((char) (varName_.charAt(1) & 0b11_1111_1111));
			variables_ = new Symbol[]{SymbolTable.getSymbol(highSurrogate), SymbolTable.getSymbol(lowSurrogate)};
		} else {
			// regular variable name
			variables_ = new Symbol[]{var};
		}
	}

	public String varName() {
		return varName_;
	}

	/**
	 * @return the initial variable, before it was reinterpreted into tuples.
	 */
	public Symbol getOriginalVar() {
		return originalVar_;
	}

	/**
	 * @return the variables of this tuple
	 */
	public Symbol[] getSymbols() {
		return variables_;
	}

}
