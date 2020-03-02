package aya.instruction.op.overload;

import java.util.ArrayList;

import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.symbol.Symbol;

public abstract class OpOverload {
	
	protected String _name;
	
	abstract public ArrayList<String> getNames();
	abstract public ArrayList<Symbol> getSymbols();

	public boolean execute(Block block, Obj a, Obj b) {
		throw new UnsupportedOperationException("Op overload '" + _name + "' does not support using two arguments");
	}

	public boolean execute(Block block, Obj a) {
		throw new UnsupportedOperationException("Op overload '" + _name + "' does not support using one argument");
	}

	public abstract String getSymName();
}
