package aya.instruction.op.overload;

import java.util.ArrayList;

import aya.Aya;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;

public class OpOverload2Arg extends OpOverload {
	
	private Symbol _var;
	private Symbol _rvar;

	public OpOverload2Arg(String name) {
		_name = name;

		if (name.length() > 7) {
			throw new IllegalArgumentException();
		}
		
		_var  =  Aya.getInstance().getSymbols().getSymbol("__" + name + "__");
		_rvar =  Aya.getInstance().getSymbols().getSymbol("__r" + name + "__");
	}

	public ArrayList<String> getNames() {
		ArrayList<String> out = new ArrayList<String>();
		out.add(_var.name());
		out.add(_rvar.name());
		return out;
	}

	public ArrayList<Symbol> getSymbols() {
		ArrayList<Symbol> out = new ArrayList<Symbol>();
		out.add(_var);
		out.add(_rvar);
		return out;
	}
	

	@Override
	public boolean execute(Block block, Obj a, Obj b) {
		if (a.isa(Obj.DICT)) {
			block.push(b);
			block.callVariable((Dict)a, _var);
			return true;
		} else if (b.isa(Obj.DICT)) {
			block.callVariable((Dict)b, _rvar, a);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Obj executeAndReturn(Obj a, Obj b) {
		if (a.isa(Obj.DICT)) {
			Block block = new Block();
			block.push(b);
			block.callVariable((Dict)a, _var);
			block.eval();
			return block.pop();
		} else if (b.isa(Obj.DICT)) {
			Block block = new Block();
			block.callVariable((Dict)b, _rvar, a);
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
