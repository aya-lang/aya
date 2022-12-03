package aya.obj.list;

import java.util.ArrayList;

import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.list.numberlist.NumberList;
import aya.util.Pair;

/** List supertype */
public abstract class ListImpl {
	
	/////////////////////
	// LIST OPERATIONS //
	/////////////////////
	
	/** Create a shallow copy of this list */
	public abstract ListImpl copy();
	
	/** The number of elements in the list */
	public abstract int length();
	
	/** Keep only the first N elements from the list */
	public abstract ListImpl head(int i);
	
	/** Remove all but the last N elements of the list */
	public abstract ListImpl tail(int i);
	
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
	public abstract ListImpl slice(int i, int j);
		
	/** Get the 0-indexed item from the list */
	public abstract Obj get(int i);
	
	/** Get the 0-indexed items from the list */
	public abstract ListImpl get(int[] is);
	
	/** Remove the 0-indexed item from the list */
	public abstract Obj remove(int i);
	
	/** Remove each of the 0-indexed items from the list */
	public abstract void removeAll(int[] ixs);
	
	/** Return the index of the first occurrence of Obj in the list */
	public abstract int find(Obj o);

	/** Return a list of indices for each occurrence of Obj in the list */
	public abstract ListImpl findAll(Obj o);
	
	/** Return the index of the last occurrence of Obj in the list */
	public abstract int findBack(Obj o);
	
	/** Return the number of occurrences of Obj in the list */
	public abstract int count(Obj o);
	
	/** Return the underlying Obj ArrayList */
	public abstract ArrayList<Obj> getObjAL();
	
	/** Return a list containing the unique elements of the original list */
	public abstract ListImpl unique();
	
	/** Return true if the input object can be a valid member of the list */
	public abstract boolean canInsert(Obj o);
	
	/** Return an empty list that is the same type as the callee */
	public abstract ListImpl similarEmpty();

	/** Return a flattened version of the list */
	protected abstract ListImpl flatten();
	
	public abstract List split(Obj o);
	
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
	public abstract void addAll(ListImpl l);
	
	/** Convert to numeric list. Throw an exception
	 * if the list contains non-numbers
	 */
	public abstract NumberList toNumberList();

	protected abstract byte type();

	protected abstract boolean isa(byte type);

	protected abstract boolean equiv(ListImpl o);

	public abstract String str();

	protected abstract ReprStream repr(ReprStream stream);

	protected abstract boolean bool();

	protected abstract ListImpl deepcopy();
	
	protected ListImpl promote() { 
		return this;
	}

	protected String repr() {
		return repr(new ReprStream()).toStringOneline();
	}



	
}
