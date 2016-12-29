package element.obj.list;

import java.util.ArrayList;

import element.obj.Obj;
import element.obj.list.numberlist.NumberList;

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
		return new ObjList(out).promote();
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
