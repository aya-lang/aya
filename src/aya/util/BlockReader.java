package aya.util;

import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.numberlist.NumberList;
import aya.obj.symbol.Symbol;

public class BlockReader {

	Block _block;
	NamedOperator _inst;

	public BlockReader(NamedOperator instruction) {
		_block = null;
		_inst = instruction;
	}
	
	public void setBlock(Block b) {
		_block = b;
	}
	
	public double popDouble() {
		final Obj o = _block.pop();
		if (o.isa(Obj.NUMBER)) {
			return Casting.asNumber(o).toDouble();
		} else {
			throw new TypeError(_inst, "N", o);
		}
	}

	public int popInt() {
		final Obj o = _block.pop();
		if (o.isa(Obj.NUMBER)) {
			return Casting.asNumber(o).toInt();
		} else {
			throw new TypeError(_inst, "N", o);
		}
	}

	public String popString() {
		final Obj o = _block.pop();
		if (o.isa(Obj.STR)) {
			return o.str();
		} else {
			throw new TypeError(_inst, "S", o);
		}
	}

	public boolean popBool() {
		return popInt() != 0;
	}

	public NumberList popNumberList() {
		final Obj o = _block.pop();
		if (o.isa(Obj.NUMBERLIST)) {
			return Casting.asNumberList(o);
		} else {
			throw new TypeError(_inst, "L", o);
		}
	}

	public Symbol popSymbol() {
		final Obj o = _block.pop();
		if (o.isa(Obj.SYMBOL)) {
			return Casting.asSymbol(o);
		} else {
			throw new TypeError(_inst, "J", o);
		}
	}
}
