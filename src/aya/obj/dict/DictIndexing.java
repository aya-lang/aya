package aya.obj.dict;

import static aya.util.Casting.asList;

import java.util.ArrayList;

import aya.Aya;
import aya.eval.AyaThread;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IndexError;
import aya.obj.Obj;
import aya.obj.block.StaticBlock;
import aya.obj.list.List;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.util.Casting;

public class DictIndexing {
	
	public static Symbol getSym(String str) {
		return Aya.getInstance().getSymbols().getSymbol(str);
	}

	/**
	 * Generic getindex interface
	 * @param context TODO
	 * @param dict
	 * @param index
	 */
	public static Obj getIndex(AyaThread context, Dict dict, Obj index) {
		if (dict.hasMetaKey(SymbolConstants.KEYVAR_GETINDEX)) {
			BlockEvaluator b = context.createEvaluator();
			b.push(index);
			b.callVariable(dict, SymbolConstants.KEYVAR_GETINDEX);
			b.eval();
			return b.pop();
		} else if (index.isa(Obj.STR)) {
			return dict.get(getSym(index.str()));
		} else if (index.isa(Obj.SYMBOL)) {
			return dict.get((Symbol)index);
		} else if (index.isa(Obj.LIST)) {
			List l = asList(index);
			ArrayList<Obj> out = new ArrayList<Obj>(l.length());
			for (int i = 0; i < l.length(); i++) {
				Obj idx = l.getExact(i);
				out.add(getIndex(context, dict, idx));
			}
			return new List(out);
		} else if (index.isa(Obj.BLOCK)) {
			return filter(context, dict, Casting.asStaticBlock(index));
		} else {
			throw new IndexError(dict, index, true);
		}
	}

	public static Obj getIndex(AyaThread context, Dict list, Obj index, Obj dflt_val) {
		try {
			return getIndex(context, list, index);
		} catch (IndexError e) {
			return dflt_val;
		}
	}
	
	
	public static Dict map(AyaThread context, Dict dict, StaticBlock mapBlock) {
		Dict out = new Dict();
		BlockEvaluator b = context.createEvaluator();

		ArrayList<Symbol> symKeys = dict.keys();
		for (Symbol key : symKeys) {
			b.dump(mapBlock);
			b.push(key);
			b.push(dict.get(key));
			b.eval();
			if (!b.stackEmpty()) {
				out.set(key, b.pop());
			}
			b.clear();
		}
		return out;
	}


	public static Dict filter(AyaThread context, Dict dict, StaticBlock filterBlock) {
		Dict out = new Dict();
		BlockEvaluator b = context.createEvaluator();

		ArrayList<Symbol> symKeys = dict.keys();
		for (Symbol key : symKeys) {
			b.dump(filterBlock);
			b.push(key);
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
