package aya.instruction.op.overload;

import java.util.ArrayList;

import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.variable.Variable;

public class OpOverload2Arg extends OpOverload {
	
	private Variable _var;
	private Variable _rvar;

	public OpOverload2Arg(String name) {
		_name = name;

		if (name.length() > 7) {
			throw new IllegalArgumentException();
		}
		
		_var  =  new Variable("__"  + name + "__");
		_rvar =  new Variable("__r" + name + "__");
	}

	public ArrayList<String> getNames() {
		ArrayList<String> out = new ArrayList<String>();
		out.add(_var.toString());
		out.add(_rvar.toString());
		return out;
	}

	public ArrayList<Symbol> getSymbols() {
		ArrayList<Symbol> out = new ArrayList<Symbol>();
		out.add(Symbol.fromID(_var.getID()));
		out.add(Symbol.fromID(_rvar.getID()));
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
	public String getSymName() {
		return _var.name();
	}

}
