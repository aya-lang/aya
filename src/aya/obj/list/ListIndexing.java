package aya.obj.list;

import java.util.ArrayList;

import aya.exceptions.TypeError;
import aya.instruction.DataInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Number;

public class ListIndexing {

	public static Obj get(List list, Number index) {
		return list.get(index.toInt());
	}

	public static Obj get(List list, Number index, Obj dflt) {
		try {
			return list.get(index.toInt());
		} catch (IndexOutOfBoundsException e) {
			return dflt;
		}
	}
	
	/** General list indexing */
	public static Obj getIndex(List list, Obj index) {
		if(index.isa(Obj.NUMBER)) {
			return get(list, (Number)index);
		} else if (index.isa(Obj.CHAR) && index.str().equals("*")) {
			return list.deepcopy();
		} else if (index.isa(Obj.LIST)) {
			List idx = (List)index;
			if (idx.length() == 0) {
				return list.similarEmpty();
			} else {
				// Optimization for numberlist
				if (index.isa(Obj.NUMBERLIST)) {
					return list.get( ((NumberList)index).toIntArray() );
				} else  {
					List index_list = (List)index;
					ArrayList<Obj> out = new ArrayList<Obj>(index_list.length());
					for (int i = 0; i < index_list.length(); i++) {
						out.add(getIndex(list, index_list.get(i)));
					}
					return new GenericList(out).promote();
				}
			}
		} 
		else if (index.isa(Obj.BLOCK)) {
			return filter(list, (Block)index);
		} else {
			throw new TypeError("Cannot index list using object:\n"
					+ "list:\t" + list.repr() + "\n"
					+ "index:\t" + index.repr());
		}
	}

	public static Obj getIndex(List list, Obj index, Obj dflt_val) {
		if(index.isa(Obj.NUMBER)) {
			return get(list, (Number)index, dflt_val);
		} else if (index.isa(Obj.CHAR) && index.str().equals("*")) {
			return list.deepcopy();
		} else if (index.isa(Obj.LIST)) {
			List idx = (List)index;
			if (idx.length() == 0) {
				return list.similarEmpty();
			} else {
				List index_list = (List)index;
				ArrayList<Obj> out = new ArrayList<Obj>(index_list.length());
				for (int i = 0; i < index_list.length(); i++) {
					out.add(getIndex(list, index_list.get(i), dflt_val));
				}
				return new GenericList(out).promote();
			}
		} 
		else if (index.isa(Obj.BLOCK)) {
			return filter(list, (Block)index, dflt_val);
		} else {
			throw new TypeError("Cannot index list using object:\n"
					+ "list:\t" + list.repr() + "\n"
					+ "index:\t" + index.repr());
		}
	}

	public static void setIndex(List list, Obj index, Obj value) {
		if (index.isa(Obj.NUMBER)) {
			list.set(((Number)index).toInt(), value);
		} else if (index.isa(Obj.CHAR) && index.str().equals("*")) {
			for (int i = 0; i < list.length(); i++) {
				list.set(i, value);
			}
		} else if (index.isa(Obj.LIST)) {
			NumberList index_list = ((List)index).toNumberList();
			for (int i = 0; i < index_list.length(); i++) {
				list.set(index_list.get(i).toInt(), value);
			}
		} else {
			throw new TypeError("Cannot index list using object:\n"
					+ "list:\t" + list.repr() + "\n"
					+ "index:\t" + index.repr());
		}
	}
	

	/**
	 * Filter a list using the block
	 * 
	 * @param block
	 * @param list
	 * @return
	 */
	public static List filter(List list, Block block) {
		ArrayList<Obj> out = new ArrayList<Obj>();
		Block b = new Block();
		for (int i = 0; i < list.length(); i++) {
			b.addAll(block.getInstructions().getInstrucionList());
			b.add(new DataInstruction(list.get(i)));
			b.eval();
			if(b.peek().bool()) {
				out.add(list.get(i));
			}
			b.clear();
		}
		return new GenericList(out).promote();
	}

	/**
	 * Filter a list using the block
	 * 
	 * @param block
	 * @param list
	 * @return
	 */
	public static List filter(List list, Block block, Obj dflt) {
		ArrayList<Obj> out = new ArrayList<Obj>(list.length());
		Block b = new Block();
		for (int i = 0; i < list.length(); i++) {
			b.addAll(block.getInstructions().getInstrucionList());
			b.add(new DataInstruction(list.get(i)));
			b.eval();
			if(b.peek().bool()) {
				out.add(list.get(i));
			} else {
				out.add(dflt);
			}
			b.clear();
		}
		return new GenericList(out).promote();
	}

	/**
	 * Like filter but returns a list of true/false values representing
	 * the outcome of each applying the block to each item in the list
	 * @param list
	 * @return
	 */
	public static boolean[] filterIndex(List list, Block block) {
		boolean[] out = new boolean[list.length()];
		Block b = new Block();
		for (int i = 0; i < list.length(); i++) {
			b.addAll(block.getInstructions().getInstrucionList());
			b.add(new DataInstruction(list.get(i)));
			b.eval();
			out[i] = b.peek().bool();
			b.clear();
		}
		return out;
	}

	/** 
	 * Maps a block to a list and returns the new list. The block is not effected
	 */
	public static List map(List list, Block block) {
		ArrayList<Obj> out = new ArrayList<Obj>(list.length());
		Block b = new Block();
		for (int i = 0; i < list.length(); i++) {
			b.addAll(block.getInstructions().getInstrucionList());
			b.add(new DataInstruction(list.get(i)));
			b.eval();
			out.addAll(b.getStack());
			b.clear();
		}
		return new GenericList(out).promote();
	}
	
	/** 
	 * Maps a block to a list and returns the new list. The block is not effected 
	 */
	public static List mapToPushStack(List list, Block block, Obj obj) {
		ArrayList<Obj> out = new ArrayList<Obj>(list.length());
		Block b = new Block();
		for (int i = 0; i < list.length(); i++) {
			b.push(obj.deepcopy());
			b.addAll(block.getInstructions().getInstrucionList());
			b.add(new DataInstruction(list.get(i)));
			b.eval();
			out.addAll(b.getStack());
			b.clear();
		}
		return new GenericList(out).promote();
	}

	
	
}
