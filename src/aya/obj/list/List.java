package aya.obj.list;

import java.util.ArrayList;

import aya.exceptions.AyaRuntimeException;
import aya.exceptions.TypeError;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;
import aya.obj.number.Number;

/** List supertype */
public abstract class List extends Obj {
	
	/** Index from the back if negative */
	public static int index(int i, int length) {
		return i >= 0 ? i : length + i;
	}
	
	/** General list indexing */
	public static Obj getIndex(List list, Obj index) {
		if(index.isa(NUMBER)) {
			return list.get(((Number)index).toInt());
		} 
		else if (index.isa(LIST)) {
			List idx = (List)index;
			if (idx.length() == 0) {
				return list.deepcopy();
			} 
			else if (idx.length() == 1) {
				return List.getIndex(list, idx.get(0));
			}
			else {
				int[] is = ((List)index).toNumberList().toIntArray();
				return list.get(is);
			}
		} 
		else if (index.isa(BLOCK)) {
			return ((Block)index).filter(list);
		} else {
			throw new TypeError("Cannot index list using object:\n"
					+ "list:\t" + list.repr() + "\n"
					+ "index:\t" + index.repr());
		}
	}
	

	
	/** General list setting **/
	public static void setIndex(List list, Obj index, Obj item) {
		if(index.isa(NUMBER)) {
			list.set(((Number)index).toInt(), item);
		} 
		
		
		else if (index.isa(LIST)) {
			NumberList l_index = ((List)index).toNumberList();

			// If both are list, assign corresponding values to indices.
			// wrap if needed
			// Ex [1 2 3 4].set([1 2], ['a 'b]) = [1 'a 'b 4]
			//    [1 2 3 4].set([1 2], ['a])    = [1 'a 'a 4]
			if (item.isa(LIST)) {
				List l_item = (List)item;
				
				if (l_item.length() == 0) {
					throw new AyaRuntimeException("Cannot set index of list using empty item list:\n"
							+ "list:\t" + list.repr() + "\n"
							+ "index:\t" +  index.repr() + "\n"
							+ "items:\t" + item.repr() + "\n");
				}
				
				int itlen = l_item.length();
				int itix = 0;
				
				for (int i = 0; i < l_index.length(); i++) {
					itix = itix >= itlen ? 0 : itix;
					list.set(l_index.get(i).toInt(), l_item.get(itix++));
				}
			} else {
				for (int i = 0; i < l_index.length(); i++) {
					list.set(l_index.get(i).toInt(), item);
				}
			}
			
		} 
		else if (index.isa(BLOCK)) {
			boolean[] truthIdxs = ((Block)index).truthIdxs(list);
			for (int i = 0; i < list.length(); i++) {
				if (truthIdxs[i]) {
					list.set(i, item);
				}
			}
			
		} else {
			throw new TypeError("Cannot index list using object:\n"
					+ "\tlist: " + list.repr() + "\n"
					+ "\tindex: " + index.repr());
		}
	}
	
	/** Completely flatten a nested list structure. Promote if possible */
	public static List flatten(List l) {
		ArrayList<Obj> out = new ArrayList<Obj>();
		for (int i = 0; i < l.length(); i++) {
			if (l.get(i).isa(Obj.LIST)) {
				out.addAll(flatten((List)l.get(i)).getObjAL());
			} else {
				out.add(l.get(i));
			}
		}
		return new GenericList(out).promote();
	}
	
	/** Remove all occurrences of the items in objs from list */
	public static void removeAllOccurances(List list, List objs) {
		List unique = objs.unique();
		 
		// Loop through each item in the unique list
		for (int i = 0; i < unique.length(); i++) {
			// As long as there exists this item in the list, remove it
			boolean moreItems = true;
			do {
				int idx = list.find(unique.get(i));
				if (idx == -1) {
					moreItems = false;
				} else {
					list.remove(idx);
				}
			} while (moreItems);
		}
	}
	
	/** Is the list a rectangular 2d list? */
	public static boolean isRect(List list) {
		if (list.length() == 0) {
			return false;
		}
		
		if (!list.get(0).isa(LIST)) {
			return false;
		}
		
		// Get col width
		int cols = ((List)(list.get(0))).length();
		
		for (int i = 1; i < list.length(); i++) {
			final Obj o = list.get(i);
			if (!o.isa(LIST)) {
				return false;
			} else {
				if ( ((List)o).length() != cols ) {
					return false;
				}
			}
		}
		
		return true;	
	}
	
	/** Transpose a 2d list of lists */
	public static List transpose(List list) {
		
		if (!isRect(list)) {
			throw new AyaRuntimeException("Cannot transpose list, must be rectangular: " + list.repr());
		}
		
		// Convert to list of lists
		ArrayList<List> lists = new ArrayList<List>(list.length());
		for (int i = 0; i < list.length(); i++) {
			lists.add((List)list.get(i));
		}
		
		int cols = lists.get(0).length();
		
		ArrayList<Obj> out = new ArrayList<Obj>(cols);
		for (int i = 0; i < cols; i++) {
			ArrayList<Obj> os = new ArrayList<Obj>();
			for (int j = 0; j < lists.size(); j++) {
				os.add(lists.get(j).get(i));
			}
			out.add(new GenericList(os).promote());
		}
		
		return new GenericList(out);
	}
	
	
	//Yes I know this is gross, i'll fix it later...
	public static List reshape(List l, NumberList dims) {
		if (dims.length() == 0)
			throw new AyaRuntimeException("reshape: must have non-empty dims");
		
		if (dims.length() > 5)
			throw new AyaRuntimeException("reshape: maximum rank of 5, recieved rank " 
					+ dims.length() + " resulting from " + dims.repr());
		
		NDListIterator<Obj> iter = new NDListIterator<Obj>(l);
		iter.setLoop(true);
		
		Integer[] ds = dims.toIntegerArray();
		
		switch (dims.length()) {
		case 1:
			return reshape(iter, ds[0]);
		case 2:
			return reshape(iter, ds[0], ds[1]);
		case 3:
			return reshape(iter, ds[0], ds[1], ds[2]);
		case 4:
			return reshape(iter, ds[0], ds[1], ds[2], ds[3]);
		case 5:
			return reshape(iter, ds[0], ds[1], ds[2], ds[3], ds[4]);
		}
		
		throw new AyaRuntimeException("reshape: invalid dimensions: " + dims.repr());
	}
	
	private static List reshape(NDListIterator<Obj> iter, int count) {
		ArrayList<Obj> out = new ArrayList<Obj>(count);
		for (int i = 0; i < count; i++) {
			out.add(iter.next());
		}
		return new GenericList(out).promote();
	}
	
	private static List reshape(NDListIterator<Obj> iter, int r, int c) {
		ArrayList<Obj> out = new ArrayList<Obj>(r);
		for (int i = 0; i < r; i++) {
			out.add(reshape(iter, c));
		}
		return new GenericList(out);
	}
	
	private static List reshape(NDListIterator<Obj> iter, int a, int b, int c) {
		ArrayList<Obj> out = new ArrayList<Obj>(a);
		for (int i = 0; i < a; i++) {
			out.add(reshape(iter, b, c));
		}
		return new GenericList(out);
	}

	private static List reshape(NDListIterator<Obj> iter, int a, int b, int c, int d) {
		ArrayList<Obj> out = new ArrayList<Obj>(a);
		for (int i = 0; i < a; i++) {
			out.add(reshape(iter, b, c, d));
		}
		return new GenericList(out);
	}
	
	private static List reshape(NDListIterator<Obj> iter, int a, int b, int c, int d, int e) {
		ArrayList<Obj> out = new ArrayList<Obj>(a);
		for (int i = 0; i < a; i++) {
			out.add(reshape(iter, b, c, d, e));
		}
		return new GenericList(out);
	}
	
	
	
	
	/** Return a list of shape l1 whose values are 1 if l1[i,j,..] == l2[i,j,..] and 0 otherwise */
	public static List equalsElementwise(List l1, List l2) {
		List out;
		if (l1.isa(Obj.STR)) {
			out = ((Str)l1).toNumberList();
		} else {
			out = (List)l1.deepcopy();
		}
		NDListIterator<Obj> iterOut = new NDListIterator<>(out);
		NDListIterator<Obj> iter1 = new NDListIterator<>(l1);
		NDListIterator<Obj> iter2 = new NDListIterator<>(l2);
		
		while (true) {
			if (iter1.done() && iter2.done()) {
				break;
			}
			
			else if (iter1.done() || iter2.done()) {
				throw new AyaRuntimeException("element-wise equals, dimension mismatch:\n\tlist1:\n"
						+ l1.repr() + "\n\tlist2:\n" + l2.repr());
			}
			
			else {
				iterOut.setNext( iter1.next().equiv(iter2.next()) ? Num.ONE : Num.ZERO );
			}
		}
		
		return out;
	}
	
	/** Return a list of shape l whose values are 1 if l[i,j,...] == o, and 0 otherwise */
	public static List equalsElementwise(List l, Obj o) {
		List out;
		if (l.isa(Obj.STR)) {
			out = ((Str)l).toNumberList();
		} else {
			out = (List)l.deepcopy();
		}
		NDListIterator<Obj> iterOut = new NDListIterator<>(out);
		NDListIterator<Obj> iter = new NDListIterator<>(l);
		
		while (!iter.done()) {			
			iterOut.setNext( iter.next().equiv(o) ? Num.ONE : Num.ZERO );
		}
		
		return out;
	}
	
	/** Add obj to the end of the list. Always make a copy of the list */
	public static List joinBack(List list, Obj o) {
		if (list.canInsert(o)) {
			List l = list.copy();
			l.addItem(o);
			return l;
		} else {
			GenericList g = new GenericList(list.copy().getObjAL());
			g.addItem(o);
			return g;
		}
	}
	
	/** Add obj to the front of the list. Always make a copy of the list */
	public static List joinFront(Obj o, List list) {
		if (list.canInsert(o)) {
			List l = list.copy();
			l.addItem(0, o);
			return l;
		} else {
			GenericList g = new GenericList(list.copy().getObjAL());
			g.addItem(0, o);
			return g;
		}
	}
	
	/** Join two lists into a single list. Always make a copy of the list */
	public static List joinLists(List list1, List list2) {
		List a = list1.copy();
		if (a.isa(NUMBERLIST) && list2.isa(NUMBERLIST)) {
			a.addAll(list2);
			return a;
		} else if (a.isa(STRLIST) && list2.isa(STRLIST)) {
			a.addAll(list2);
			return a;
		} else {
			GenericList g = new GenericList(a.getObjAL());
			g.addAll(list2);
			return g.promote();
		}
	}
	
	
	/////////////////////
	// LIST OPERATIONS //
	/////////////////////
	
	/** Create a shallow copy of this list */
	public abstract List copy();
	
	/** The number of elements in the list */
	public abstract int length();
	
	/** Keep only the first N elements from the list */
	public abstract List head(int i);
	
	/** Remove all but the last N elements of the list */
	public abstract List tail(int i);
	
	/** Return the head of the list */
	public abstract Obj head();
	
	/** Return the back of the list */
	public abstract Obj tail();
	
	/** Remove and return the head of the list */
	public abstract Obj pop();
	
	/** Remove and return the back of the list */
	public abstract Obj popBack();
	
	/** Return a reversed copy of the list */
	public abstract void reverse();
	
	/** Sort the list */
	public abstract void sort();
	
	/**
	 * Returns a list that is a sublist of this list. The sublist 
	 * begins at the specified <code>i</code> and extends to the item
	 * at index <code>j - 1</code>. Thus the length of the sublist is 
	 * <code>j - i</code>. 
	 * <br />
	 * Examples:<br />
	 * <code>
	 * "hamburger".slice(4, 8) returns "urge"<br />
	 * "smiles".slice(1, 5) returns "mile"
	 * </code>
	 */
	public abstract List slice(int i, int j);
		
	/** Get the 0-indexed item from the list */
	public abstract Obj get(int i);
	
	/** Get the 0-indexed items from the list */
	public abstract List get(int[] is);
	
	/** Remove the 0-indexed item from the list */
	public abstract Obj remove(int i);
	
	/** Remove each of the 0-indexed items from the list */
	public abstract void removeAll(Integer[] ixs);
	
	/** Return the index of the first occurrence of Obj in the list */
	public abstract int find(Obj o);
	
	/** Return the index of the last occurrence of Obj in the list */
	public abstract int findBack(Obj o);
	
	/** Return the number of occurrences of Obj in the list */
	public abstract int count(Obj o);
	
	/** Return the underlying Obj ArrayList */
	public abstract ArrayList<Obj> getObjAL();
	
	/** Return a list containing the unique elements of the original list */
	public abstract List unique();
	
	/** Return true if the input object can be a valid member of the list */
	public abstract boolean canInsert(Obj o);
	
	/** Return an empty list that is the same type as the callee */
	public abstract List similarEmpty();
	
	////////////////////////
	// LIST MODIFICATIONS //
	////////////////////////
	
	/** Set index i of the list to Obj o, throws an exception 
	 * if the list is of lower type than o. (Ex: insert "hello" 
	 * into a NumberList)
	 */
	public abstract void set(int i, Obj o);
	
	/** Add item to the end of the list. throws an exception 
	 * if the list is of lower type than o. (Ex: insert "hello" 
	 * into a NumberList)
	 */
	public abstract void addItem(Obj o);
	
	/** Insert an item into the list at pos i. throws an exception 
	 * if the list is of lower type than o. (Ex: insert "hello" 
	 * into a NumberList)
	 */
	public abstract void addItem(int i, Obj o);
	
	/** Extent the current list with l. throws an exception 
	 * if the list is of lower type than o.
	 * ( Ex. {@code [1 2 3].extend(['a 'b 'c])} )
	 */
	public abstract void addAll(List l);
	
	/** Convert to numeric list. Throw an exception
	 * if the list contains non-numbers
	 */
	public abstract NumberList toNumberList();

	
	
}
