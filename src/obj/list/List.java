package obj.list;

import obj.Obj;

public abstract class List extends Obj {
	
	/////////////////////
	// LIST OPERATIONS //
	/////////////////////
	
	/** The number of elements in the list */
	public abstract int length();
	
	/** Keep only the first N elements from the list */
	public abstract void head(int i);
	
	/** Remove all but the last N elements of the list */
	public abstract void tail(int i);
	
	/** Return the head of the list */
	public abstract Obj head();
	
	/** Return the back of the list */
	public abstract Obj tail();
	
	/** Remove and return the head of the list */
	public abstract Obj pop();
	
	/** Remove and return the back of the list */
	public abstract Obj popBack();
	
	/** In-place reverse the list */
	public abstract void reverse();
	
	/** Remove the head and tail of a list */
	public abstract void slice(int i, int j);
	
	/** Get the 0-indexed item from the stack */
	public abstract Obj get(int i);
	
	/** Return the index of the first occurrence of Obj in the list */
	public abstract int find(Obj o);
	
	/** Return the index of the last occurrence of Obj in the list */
	public abstract int findBack(Obj o);
	
	/** Return the number of occurrences of Obj in the list */
	public abstract int count(Obj o);
}
