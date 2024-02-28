package aya.instruction.op.overload;

import java.util.ArrayList;

import aya.Aya;
import aya.obj.Obj;
import aya.obj.block.BlockEvaluator;
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
	public boolean execute(BlockEvaluator blockEvaluator, Obj a, Obj b) {
		if (a.isa(Obj.DICT)) {
			blockEvaluator.push(b);
			blockEvaluator.callVariable((Dict)a, _var);
			return true;
		} else if (b.isa(Obj.DICT)) {
			blockEvaluator.callVariable((Dict)b, _rvar, a);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Obj executeAndReturn(Obj a, Obj b) {
		if (a.isa(Obj.DICT)) {
			BlockEvaluator blockEvaluator = new BlockEvaluator();
			blockEvaluator.push(b);
			blockEvaluator.callVariable((Dict)a, _var);
			blockEvaluator.eval();
			return blockEvaluator.pop();
		} else if (b.isa(Obj.DICT)) {
			BlockEvaluator blockEvaluator = new BlockEvaluator();
			blockEvaluator.callVariable((Dict)b, _rvar, a);
			blockEvaluator.eval();
			return blockEvaluator.pop();
		} else {
			return null;
		}
	}

	@Override
	public String getSymName() {
		return _var.name();
	}

}
