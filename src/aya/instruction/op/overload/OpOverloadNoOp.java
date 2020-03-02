package aya.instruction.op.overload;

import java.util.ArrayList;

import aya.obj.symbol.Symbol;

public class OpOverloadNoOp extends OpOverload {

	public OpOverloadNoOp(String name) {
		_name = name;
	}

	@Override
	public ArrayList<String> getNames() {
		ArrayList<String> out = new ArrayList<String>();
		out.add("__" + _name + "__");
		return out;
	}

	@Override
	public ArrayList<Symbol> getSymbols() {
		ArrayList<Symbol> out = new ArrayList<Symbol>();
		out.add(Symbol.fromStr("__" + _name + "__"));
		return out;
	}

	@Override
	public String getSymName() {
		return "__" + _name + "__";
	}
	
	

}
