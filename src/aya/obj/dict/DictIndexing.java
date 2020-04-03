package aya.obj.dict;

import java.util.ArrayList;
import java.util.Set;

import aya.obj.block.Block;
import aya.obj.list.Str;
import aya.obj.symbol.Symbol;

public class DictIndexing {
	
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
			b.push(new Str(key));
			b.push(dict.get(key));
			b.eval();
			if (!b.stackEmpty()) {
				out.set(key, b.pop());
			}
			b.clear();
		}
		
		return out;
	}

}
