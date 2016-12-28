package element.obj.list;

import java.util.ArrayList;
import java.util.Collection;

import element.obj.Obj;
import element.obj.list.numberlist.NumberList;

public abstract class List extends Obj {
	
	/** Index from the back if negative */
	public static int index(int i, int length) {
		return i >= 0 ? i : length + i;
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
		
	/** Get the 0-indexed item from the stack */
	public abstract Obj get(int i);
	
	/** Return the index of the first occurrence of Obj in the list */
	public abstract int find(Obj o);
	
	/** Return the index of the last occurrence of Obj in the list */
	public abstract int findBack(Obj o);
	
	/** Return the number of occurrences of Obj in the list */
	public abstract int count(Obj o);
	
	/** Return the underlying Obj ArrayList */
	public abstract ArrayList<Obj> getObjAL();
	
	////////////////////////
	// LIST MODIFICATIONS //
	////////////////////////
	
	public abstract void set(int i, Obj o);
	
	public abstract void add(Obj o);
	
	public abstract void add(int i, Obj o);
	
	public abstract void addAll(List l);
	
	public abstract NumberList toNumberList();

	
	
}
