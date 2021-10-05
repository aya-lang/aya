package aya.obj.block;

import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

public class BlockHeaderArg {

	public Symbol var;
	public Symbol type;
	public boolean copy;
	
	public BlockHeaderArg(Symbol var) {
		this.var = var;
		this.type = SymbolConstants.ANY;
		this.copy = false;
	}
	
	public BlockHeaderArg(Symbol var, Symbol type, boolean copy) {
		this.var = var;
		this.type = type;
		this.copy = copy;
	}
	
	public String toString() {
		return str();
	}

	public String str() {
		String s = var.name() + (copy ? "$" : "");
		if (this.type.id() != SymbolConstants.ANY.id()) {
			s += "::" + type.name();
		}
		return s;
	}
}
