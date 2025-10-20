package aya.instruction.variable.assignment;

import java.util.ArrayList;
import java.util.List;

import aya.eval.ExecutionContext;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;

public class SimpleAssignment extends Assignment {
	
	protected Symbol _var;
	
	public SimpleAssignment(SourceStringRef source, Symbol var) {
		super(source);
		_var = var;
	}
	
	public Symbol getVarName() {
		return _var;
	}

	@Override
	public void assign(Dict vars, Obj o, ExecutionContext ctx) {
		vars.set(this._var, o);
	}

	@Override
	public void toDict(Dict d) {
		d.set(SymbolConstants.NAME, _var);
	}

	@Override
	public String toString() {
		return _var.name();
	}

	@Override
	public ArrayList<Symbol> getNames() {
		return new ArrayList<>(List.of(_var));
	}

}
