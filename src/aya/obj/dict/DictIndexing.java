package aya.obj.dict;

import static aya.util.Casting.asList;

import java.util.ArrayList;
import java.util.Set;

import aya.entities.operations.Ops;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.obj.symbol.Symbol;

public class DictIndexing {

	/**
	 * Generic getindex interface
	 * @param dict
	 * @param index
	 */
	public static Obj getIndex(Dict dict, Obj index) {
		if (dict.hasMetaKey(Ops.KEYVAR_GETINDEX)) {
			Block b = new Block();
			b.push(index);
			b.callVariable(dict, Ops.KEYVAR_GETINDEX);
			b.eval();
			return b.pop();
		} else if (index.isa(Obj.STR)) {
			return dict.get(index.str());
		} else if (index.isa(Obj.SYMBOL)) {
			return dict.get(((Symbol)index).id());
		} else if (index.isa(Obj.LIST)) {
			List l = asList(index);
			ArrayList<Obj> out = new ArrayList<Obj>(l.length());
			for (int i = 0; i < l.length(); i++) {
				Obj idx = l.getExact(i);
				out.add(getIndex(dict, idx));
			}
			return new List(out);
		} else if (index.isa(Obj.BLOCK)) {
			return filter(dict, (Block)index);
		} else {
			throw new AyaKeyError(dict, index, true);
		}
	}

	public static Obj getIndex(Dict list, Obj index, Obj dflt_val) {
		try {
			return getIndex(list, index);
		} catch (AyaKeyError e) {
			return dflt_val;
		}
	}
	
	
	public static Dict map(Dict dict, Block block) {
		Dict out = new Dict();
		Block b = new Block();

		ArrayList<Long> symKeys = dict.symKeys();
		for (long key : symKeys) {
			b.addAll(block.getInstructions().getInstrucionList());
			b.push(Symbol.fromID(key));
			b.push(dict.get(key));
			b.eval();
			if (!b.stackEmpty()) {
				out.set(key, b.pop());
			}
			b.clear();
		}
		Set<String> strKeys = dict.strKeys();
		for (String key : strKeys) {
			b.addAll(block.getInstructions().getInstrucionList());
			b.push(List.fromString(key));
			b.push(dict.get(key));
			b.eval();
			if (!b.stackEmpty()) {
				out.set(key, b.pop());
			}
			b.clear();
		}
		
		return out;
	}

	public static Dict filter(Dict dict, Block block) {
		Dict out = new Dict();
		Block b = new Block();

		ArrayList<Long> symKeys = dict.symKeys();
		for (long key : symKeys) {
			b.addAll(block.getInstructions().getInstrucionList());
			b.push(Symbol.fromID(key));
			b.push(dict.get(key));
			b.eval();
			if (b.pop().bool()) {
				out.set(key, dict.get(key));
			}
			b.clear();
		}
		Set<String> strKeys = dict.strKeys();
		for (String key : strKeys) {
			b.addAll(block.getInstructions().getInstrucionList());
			b.push(List.fromString(key));
			b.push(dict.get(key));
			b.eval();
			if (b.pop().bool()) {
				out.set(key, dict.get(key));
			}
			b.clear();
		}
		
		return out;
	}

}
