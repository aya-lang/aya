package aya.instruction.op.overload;

import java.util.ArrayList;

import aya.eval.BlockEvaluator;
import aya.eval.ExecutionContext;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;

public class OpOverload1Arg extends OpOverload {
	
	private Symbol _var;
	
	public OpOverload1Arg(String name) {
		_name = name;
		
		if (name.length() > 8) {
			throw new IllegalArgumentException();
		}
		
		_var =  SymbolTable.getSymbol("__" + name + "__");
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
	public boolean execute(BlockEvaluator blockEvaluator, Obj a) {
		if (a.isa(Obj.DICT)) {
			blockEvaluator.callVariable((Dict)a, _var);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public Obj executeAndReturn(ExecutionContext context, Obj a) {
		if (a.isa(Obj.DICT)) {
			BlockEvaluator blockEvaluator = context.createEvaluator();
			blockEvaluator.callVariable((Dict)a, _var);
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
