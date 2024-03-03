package aya.instruction.op.overload;

import java.util.ArrayList;

import aya.eval.AyaThread;
import aya.eval.BlockEvaluator;
import aya.obj.Obj;
import aya.obj.symbol.Symbol;

public abstract class OpOverload {
	
	protected String _name;
	
	abstract public ArrayList<String> getNames();
	abstract public ArrayList<Symbol> getSymbols();

	public boolean execute(BlockEvaluator blockEvaluator, Obj a, Obj b) {
		throw new UnsupportedOperationException("Op overload '" + _name + "' does not support using two arguments");
	}

	public boolean execute(BlockEvaluator blockEvaluator, Obj a) {
		throw new UnsupportedOperationException("Op overload '" + _name + "' does not support using one argument");
	}

	/** Returns null of the no overload exists 
	 * @param context TODO*/
	public Obj executeAndReturn(AyaThread context, Obj a, Obj b) {
		throw new UnsupportedOperationException("Op overload '" + _name + "' does not support using two arguments");
	}

	/** Returns null of the no overload exists 
	 * @param context TODO*/
	public Obj executeAndReturn(AyaThread context, Obj a) {
		throw new UnsupportedOperationException("Op overload '" + _name + "' does not support using one argument");
	}


	public abstract String getSymName();
}
