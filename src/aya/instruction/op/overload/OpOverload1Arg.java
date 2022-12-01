package aya.instruction.op.overload;

import java.util.ArrayList;

import aya.Aya;
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
		
		_var =  Aya.getInstance().getSymbols().getSymbol("__" + name + "__");
	}
	
	public ArrayList<String> getNames() {
		ArrayList<String> out = new ArrayList<String>();
		out.add(_var.name());
		return out;
	}
	
	public ArrayList<Symbol> getSymbols() {
		ArrayList<Symbol> out = new ArrayList<Symbol>();
		out.add(_var);
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
	public Obj executeAndReturn(Obj a) {
		if (a.isa(Obj.DICT)) {
			Block block = new Block();
			block.callVariable((Dict)a, _var);
			block.eval();
			return block.pop();
		} else {
			return null;
		}
	}

	@Override
	public String getSymName() {
		return _var.name();
	}
}
