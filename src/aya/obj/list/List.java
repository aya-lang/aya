package aya.obj.list;

import java.util.ArrayList;

import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;

/** List supertype */
public abstract class List extends Obj {
	
	/** Index from the back if negative */
	public static int index(int i, int length) {
		return i >= 0 ? i : length + i;
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
	
	
	/////////////////////
	// LIST OPERATIONS //
	/////////////////////
	
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
