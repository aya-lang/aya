package aya.instruction.op.overload;

import java.util.ArrayList;

import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;

public class OpOverload1Arg extends OpOverload {
	
	private Symbol _var;
	
	public OpOverload1Arg(String name) {
		_name = name;
		
		if (name.length() > 8) {
			throw new IllegalArgumentException();
		}
		
		_var =  Symbol.fromStr("__" + name + "__");
	}
	
	public ArrayList<String> getNames() {
		ArrayList<String> out = new ArrayList<String>();
		out.add(_var.name());
		return out;
	}
	
	public ArrayList<Symbol> getSymbols() {
		ArrayList<Symbol> out = new ArrayList<Symbol>();
		out.add(Symbol.fromID(_var.id()));
		return out;
	}
	
	@Override
	public boolean execute(Block block, Obj a) {
		if (a.isa(Obj.DICT)) {
			block.callVariable((Dict)a, _var);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getSymName() {
		return _var.name();
	}
}
