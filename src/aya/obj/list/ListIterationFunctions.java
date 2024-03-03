package aya.obj.list;

import java.util.ArrayList;

import aya.eval.ExecutionContext;
import aya.eval.BlockEvaluator;
import aya.instruction.DataInstruction;
import aya.obj.Obj;
import aya.obj.block.StaticBlock;

public class ListIterationFunctions {

	/** 
	 * Maps a blockEvaluator to a list and returns the new list. The blockEvaluator is not effected
	 */
	public static List map(ExecutionContext context, List list, StaticBlock map) {
		int len = list.length();
		if (len > 0) {
			ArrayList<Obj> out = new ArrayList<Obj>(len);
			BlockEvaluator b = context.createEvaluator();
			for (int i = 0; i < len; i++) {
				b.dump(map);
				b.add(new DataInstruction(list.getExact(i)));
				b.eval();
				out.addAll(b.getStack());
				b.clear();
			}
			return new List(out);
		} else {
			return list.deepcopy();
		}
	}
	/** 
	 * Same as map but push 1 additional item to the stack (shallow copied)
	 * Maps a blockEvaluator to a list and returns the new list. The blockEvaluator is not effected
	 */
	public static List map1arg(ExecutionContext context, List list, StaticBlock expr, Obj obj) {
		int len = list.length();
		ArrayList<Obj> out = new ArrayList<Obj>(len);
		BlockEvaluator b = context.createEvaluator();
		for (int i = 0; i < len; i++) {
			b.push(obj);
			b.dump(expr);
			b.add(new DataInstruction(list.getExact(i)));
			b.eval();
			out.addAll(b.getStack());
			b.clear();
		}
		return new List(out);
	}

	/**
	 * Filter a list using the blockEvaluator
	 * 
	 * @param filter
	 * @param list
	 * @return
	 */
	public static List filter(ExecutionContext context, List list, StaticBlock filter) {
		ArrayList<Obj> out = new ArrayList<Obj>();
		BlockEvaluator b = context.createEvaluator();
		for (int i = 0; i < list.length(); i++) {
			final Obj o = list.getExact(i);
			b.dump(filter);
			b.add(new DataInstruction(o));
			b.eval();
			if(b.peek().bool()) {
				out.add(o);
			}
			b.clear();
		}
		return new List(out);
	}
	
	/**
	 * Filter a list using the blockEvaluator
	 * 
	 * @param staticBlock
	 * @param list
	 * @return
	 */
	public static List filter(ExecutionContext context, List list, StaticBlock staticBlock, Obj dflt) {
		ArrayList<Obj> out = new ArrayList<Obj>(list.length());
		BlockEvaluator b = context.createEvaluator();
		for (int i = 0; i < list.length(); i++) {
			b.dump(staticBlock);
			b.add(new DataInstruction(list.getExact(i)));
			b.eval();
			if(b.peek().bool()) {
				out.add(list.getExact(i));
			} else {
				out.add(dflt);
			}
			b.clear();
		}
		return new List(out);
	}

	/**
	 * Like filter but returns a list of true/false values representing
	 * the outcome of each applying the blockEvaluator to each item in the list
	 * @param list
	 * @return
	 */
	public static boolean[] filterIndex(ExecutionContext context, List list, StaticBlock staticBlock) {
		final int len = list.length();
		boolean[] out = new boolean[len];
		BlockEvaluator b = context.createEvaluator();
		for (int i = 0; i < len; i++) {
			b.dump(staticBlock);
			b.add(new DataInstruction(list.getExact(i)));
			b.eval();
			out[i] = b.peek().bool();
			b.clear();
		}
		return out;
	}
	
	
}
