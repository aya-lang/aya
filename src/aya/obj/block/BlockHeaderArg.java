package aya.obj.block;

import aya.exceptions.runtime.TypeError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

public class BlockHeaderArg extends AbstractBlockHeaderArg {

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
	
	public void assign(Dict vars, Obj o) {
		if (Obj.isInstance(o, this.type)) {
			if (this.copy) {
				vars.set(this.var, o.deepcopy());
			} else {
				vars.set(this.var, o);
			}
		} else {
			throw new TypeError("{ARGS}\n\tExpected:" + this.type.repr()
						+ "\n\tReceived:" + o);
		}
	}
	

	@Override
	public String toString() {
		String s = var.name() + (copy ? "$" : "");
		if (this.type.id() != SymbolConstants.ANY.id()) {
			s += "::" + type.name();
		}
		return s;
	}

	
	@Override
	protected void toDict(Dict d) {
		d.set(SymbolConstants.NAME, this.var);
		d.set(SymbolConstants.COPY, Num.fromBool(this.copy));
		d.set(SymbolConstants.TYPE, this.type);
	}

	@Override
	protected Symbol blockHeaderArgType() {
		return SymbolConstants.PLAIN;
	}
}
