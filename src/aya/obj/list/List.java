package aya.obj.list;

import static aya.util.Casting.asBlock;
import static aya.util.Casting.asList;
import static aya.util.Casting.asNumber;
import static aya.util.Casting.asNumberList;

import java.util.ArrayList;

import aya.ReprStream;
import aya.exceptions.runtime.IndexError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.DataInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.block.StaticBlock;
import aya.obj.list.numberlist.DoubleList;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;
import aya.util.Casting;
import aya.util.Pair;

/** List supertype */
public class List extends Obj {
	
	private ListImpl _list;
	
	public List(ListImpl list) {
		this._list = list.promote();
	}
	
	public List(ArrayList<Obj> l) {
		this._list = new GenericList(l).promote();
	}

	public List() {
		this._list = new GenericList(new ArrayList<Obj>());
	}

	protected ListImpl impl() {
		return _list;
	}

	public static List fromString(String str) {
		return new List(new Str(str));
	}
	
	public static List fromStr(Str str) {
		return new List(str);
	}
	
	public static <T extends Obj> List fromTemplate(ArrayList<T> list) {
		List l = new List();
		for (Obj o : list) l.mutAdd(o);
		return l;
	}
	
	public static <T extends Obj> List from2DTemplate(ArrayList<ArrayList<T>> list2d) {
		List l = new List();
		for (ArrayList<T> sublist : list2d) {
			l.mutAdd(List.fromTemplate(sublist));
		}
		return l;
	}

	public static List from2D(ArrayList<ArrayList<Obj>> list2d) {
		List l = new List();
		for (ArrayList<Obj> sublist : list2d) {
			l.mutAdd(new List(sublist));
		}
		return l;
	}

	private void promote() {
		_list = _list.promote();
	}

	//////////////
	// Indexing //
	//////////////
	
	
	
	/** Completely flatten a nested list structure. Promote if possible */
	public List flatten() {
		return new List(_list.flatten());
	}
	
	/** Remove all occurrences of the items in objs from list */
	public List removeAllOccurances(List objs) {
		List uniq = objs.unique();
		List out = new List();
		
		for (int i = 0; i < length(); i++) {
			Obj o = getExact(i);
			if (uniq.find(o) < 0) {
				out.mutAdd(o);
			}
		}
		
		return out;
	}
	
	/** Is the list a rectangular 2d list? */
	public boolean isRect() {
		if (length() == 0) {
			return false;
		}
		
		if (!(getExact(0).isa(LIST))) {
			return false;
		}
		
		// Get col width
		int cols = asList(getExact(0)).length();
		
		for (int i = 1; i < length(); i++) {
			final Obj o = getExact(i);
			if (!o.isa(LIST)) {
				return false;
			} else {
				if ( asList(o).length() != cols ) {
					return false;
				}
			}
		}
		
		return true;	
	}
	
	private boolean noInnerLists() {
		for (int i = 0; i < length(); i++) {
			if (getExact(i).isa(LIST)) return false;
		}
		return true;
	}
	
	
	public List transpose() {
		if (length() == 0) {
			return new List();
		} else if (isRect()) {
			return _transpose2d();
		} else if (noInnerLists()) {
			// Wrap each item in a list
			ArrayList<Obj> outer_list = new ArrayList<Obj>();
			for (int i = 0; i < length(); i++) {
				List inner_list = new List();
				inner_list.mutAdd(getExact(i));
				outer_list.add(inner_list);
			}
			return new List(new GenericList(outer_list));
		} else {
			throw new ValueError("Cannot transpose list: " + repr());
		}
	}
	
	/** Transpose a 2d list of lists
	 *  input: a rectangular list */
	private List _transpose2d() {
		// Convert to list of lists
		ArrayList<List> lists = new ArrayList<List>(length());
		ArrayList<DoubleList> double_lists = new ArrayList<DoubleList>();
		boolean all_double_lists = true;
		for (int i = 0; i < length(); i++) {
			List list = Casting.asList(getExact(i));
			if (all_double_lists && list.isa(Obj.DOUBLELIST)) {
				double_lists.add((DoubleList)list.toNumberList());
			} else {
				all_double_lists = false;
			}
			lists.add(list);
		}

		if (all_double_lists) {
			return DoubleList.transpose2d(double_lists);
		} else {
			int cols = lists.get(0).length();
			
			if (cols == 0) {
				List out = new List();
				out.mutAdd(new List());
				return out;
			}
			
			List out = new List();
			for (int i = 0; i < cols; i++) {
				List os = new List();
				for (int j = 0; j < lists.size(); j++) {
					os.mutAdd(lists.get(j).getExact(i));
				}
				out.mutAdd(os);
			}
			
			return out;
		}
	}

	//Yes I know this is gross, i'll fix it later...
	public List reshape(NumberList dims) {
		if (dims.length() == 0)
			throw new ValueError("reshape: must have non-empty dims");
		
		if (dims.length() > 5)
			throw new ValueError("reshape: maximum rank of 5, recieved rank " 
					+ dims.length() + " resulting from " + dims.repr());
		
		NDListIterator iter = new NDListIterator(this, true);
		
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
		
		throw new ValueError("reshape: invalid dimensions: " + dims.repr());
	}
	
	private static List reshape(NDListIterator iter, int count) {
		ArrayList<Obj> out = new ArrayList<Obj>(count);
		for (int i = 0; i < count; i++) {
			out.add(iter.next());
		}
		return new List(new GenericList(out).promote());
	}
	
	private static List reshape(NDListIterator iter, int r, int c) {
		ArrayList<Obj> out = new ArrayList<Obj>(r);
		for (int i = 0; i < r; i++) {
			out.add(reshape(iter, c));
		}
		return new List(new GenericList(out));
	}
	
	private static List reshape(NDListIterator iter, int a, int b, int c) {
		ArrayList<Obj> out = new ArrayList<Obj>(a);
		for (int i = 0; i < a; i++) {
			out.add(reshape(iter, b, c));
		}
		return new List(new GenericList(out));
	}

	private static List reshape(NDListIterator iter, int a, int b, int c, int d) {
		ArrayList<Obj> out = new ArrayList<Obj>(a);
		for (int i = 0; i < a; i++) {
			out.add(reshape(iter, b, c, d));
		}
		return new List(new GenericList(out));
	}
	
	private static List reshape(NDListIterator iter, int a, int b, int c, int d, int e) {
		ArrayList<Obj> out = new ArrayList<Obj>(a);
		for (int i = 0; i < a; i++) {
			out.add(reshape(iter, b, c, d, e));
		}
		return new List(new GenericList(out));
	}
	
	
	
	
	/** Return a list of shape l1 whose values are 1 if l1[i,j,..] == l2[i,j,..] and 0 otherwise */
	public List equalsElementwise(List l2) {
		List out = deepcopy();
		NDListIterator iterOut = new NDListIterator(out);
		NDListIterator iter1 = new NDListIterator(this);
		NDListIterator iter2 = new NDListIterator(l2);
		
		while (true) {
			if (iter1.done() && iter2.done()) {
				break;
			}
			
			else if (iter1.done() || iter2.done()) {
				throw new ValueError("element-wise equals, dimension mismatch:\n\tlist1:\n"
						+ repr() + "\n\tlist2:\n" + l2.repr());
			}
			
			else {
				iterOut.setNext( iter1.next().equiv(iter2.next()) ? Num.ONE : Num.ZERO );
			}
		}
		
		out.promote();
		return out;
	}
	
	/** Return a list of shape l whose values are 1 if l[i,j,...] == o, and 0 otherwise */
	public List equalsElementwise(Obj o) {
		List out = deepcopy();
		NDListIterator iterOut = new NDListIterator(out);
		NDListIterator iter = new NDListIterator(this);
		
		while (!iter.done()) {			
			iterOut.setNext( iter.next().equiv(o) ? Num.ONE : Num.ZERO );
		}
		
		out.promote();
		return out;
	}
	

	public Pair<List, List> splitAtIndexed(int index) {
		ListImpl l = this._list;
		Pair<ListImpl, ListImpl> p;
		if (index == 0) {
			p = new Pair<ListImpl, ListImpl>(l.similarEmpty(), l);
		} else if(index >= l.length()) {
			p = new Pair<ListImpl, ListImpl>(l, l.similarEmpty());
		} else if  (index*-1 >= l.length()) {
			p = new Pair<ListImpl, ListImpl>(l.similarEmpty(), l);
		} else if (index > 0) {
			p = new Pair<ListImpl, ListImpl>(l.slice(0, index), l.slice(index, l.length()));
		} else if (index < 0) {
			p = new Pair<ListImpl, ListImpl>(l.slice(0, l.length()+index), l.slice(l.length()+index, l.length()));
		} else {
			return null;
		}
		
		return new Pair<List, List>(new List(p.first()), new List(p.second()));
	}


	/** Cast implmentation to Str and return */
	public static Str asStr(List l) {
		return (Str)(l.impl());
	}

	/** 
	 * Maps a block to a list and returns the new list. The block is not effected
	 */
	public List map(StaticBlock map) {
		int len = length();
		if (len > 0) {
			ArrayList<Obj> out = new ArrayList<Obj>(len);
			Block b = new Block();
			for (int i = 0; i < len; i++) {
				b.dump(map);
				b.add(new DataInstruction(getExact(i)));
				b.eval();
				out.addAll(b.getStack());
				b.clear();
			}
			return new List(out);
		} else {
			return this.deepcopy();
		}
	}
	/** 
	 * Same as map but push 1 additional item to the stack (shallow copied)
	 * Maps a block to a list and returns the new list. The block is not effected
	 */
	public List map1arg(StaticBlock expr, Obj obj) {
		int len = length();
		ArrayList<Obj> out = new ArrayList<Obj>(len);
		Block b = new Block();
		for (int i = 0; i < len; i++) {
			b.push(obj);
			b.addAll(expr.getInstructions().getInstrucionList());
			b.add(new DataInstruction(getExact(i)));
			b.eval();
			out.addAll(b.getStack());
			b.clear();
		}
		return new List(out);
	}

	/**
	 * Filter a list using the block
	 * 
	 * @param filter
	 * @param list
	 * @return
	 */
	public List filter(StaticBlock filter) {
		ArrayList<Obj> out = new ArrayList<Obj>();
		Block b = new Block();
		for (int i = 0; i < length(); i++) {
			final Obj o = getExact(i);
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
	 * Filter a list using the block
	 * 
	 * @param block
	 * @param list
	 * @return
	 */
	public List filter(Block block, Obj dflt) {
		ArrayList<Obj> out = new ArrayList<Obj>(length());
		Block b = new Block();
		for (int i = 0; i < length(); i++) {
			b.addAll(block.getInstructions().getInstrucionList());
			b.add(new DataInstruction(getExact(i)));
			b.eval();
			if(b.peek().bool()) {
				out.add(getExact(i));
			} else {
				out.add(dflt);
			}
			b.clear();
		}
		return new List(out);
	}

	/**
	 * Like filter but returns a list of true/false values representing
	 * the outcome of each applying the block to each item in the list
	 * @param list
	 * @return
	 */
	public boolean[] filterIndex(Block block) {
		final int len = length();
		boolean[] out = new boolean[len];
		Block b = new Block();
		for (int i = 0; i < len; i++) {
			b.addAll(block.getInstructions().getInstrucionList());
			b.add(new DataInstruction(getExact(i)));
			b.eval();
			out[i] = b.peek().bool();
			b.clear();
		}
		return out;
	}
	
	
	/////////////////////
	// LIST OPERATIONS //
	/////////////////////
	
	/** The number of elements in the list */
	public int length() {
		return _list.length();
	}
	
	/** Keep only the first N elements from the list */
	public List headIndexed(int i) {
		return new List(_list.head(index(i, length())));
	}
	
	/** Remove all but the last N elements of the list */
	public List tailIndexed(int i) {
		return new List(_list.tail(index(i, length())));
	}
	
	/** Return the head of the list */
	public Obj head() {
		return _list.head();
	}
	
	/** Return the back of the list */
	public Obj tail() {
		return _list.tail();
	}

	/** Return a rotated copy of the list */
	public List rotate(int n) {
		return new List(_list.rotate(n));
	}
	
	/** Remove and return the head of the list */
	public Obj mutPop() {
		final Obj o = _list.pop();
		promote();
		return o;
	}
	
	/** Remove and return the back of the list */
	public Obj mutPopBack() {
		final Obj o = _list.popBack();
		promote();
		return o;
	}
	
	/** Reverse the list in place */
	public void mutReverse() {
		_list.reverse();
	}

	
	/** Sort the list */
	public void mutSort() {
		_list.sort();
	}
	
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
	public List sliceExact(int i, int j) {
		//Swap the order if i > j
		if (i > j) {
			int t = i;
			i = j;
			j = t;
		}
		return new List(_list.slice(i, j));
	}

	public List sliceIndexed(int i, int j) {
		final int L = length();
		i = index(i, L);
		j = index(j, L);
		//Swap the order if i > j
		if (i > j) {
			int t = i;
			i = j;
			j = t;
		}
		return new List(_list.slice(i, j));
	}
	
	public List split(Obj o) {
		if (o.isa(Obj.BLOCK)) {
			// TODO: Split wherever the block evaluates to true
			return new List();
		} else {
			return _list.split(o);
		}
	}
	
	///////////////
	// Get Index //
	///////////////
		
	/** Get the 0-indexed item from the list */
	public Obj getExact(int i) {
		try {
			return _list.get(i);
		} catch (IndexOutOfBoundsException e) {
			throw new IndexError(this, Num.fromInt(i));
		}
	}

	/** Get the 0-indexed item from the list */
	public Obj getExact(int i, Obj dflt) {
		try {
			return _list.get(i);
		} catch (IndexOutOfBoundsException e) {
			return dflt;
		}
	}
	
	/** Get the 0-indexed items from the list */
	public List getExact(int[] is) {
		try {
			return new List(_list.get(is));
		} catch (IndexOutOfBoundsException e) {
			List l = new List();
			for (int i = 0; i < is.length; i++) l.mutAdd(Num.fromInt(is[i]));
			throw new IndexError(this, l);
		}
	}

	/** Get the 0-indexed items from the list */
	public List getExact(int[] is, Obj dflt) {
		List l = new List();
		for (int i : is) {
			l.mutAdd(getExact(i, dflt));
		}
		return l;
	}

	public Obj getIndexed(int i) {
		return getExact(index(i, length()));
	}

	public Obj getIndexed(int i, Obj dflt) {
		return getExact(index(i, length()), dflt);
	}
	
	/** Modifies index list */
	public List getIndexed(int[] is) {
		index(is, length());
		return getExact(is);
	}
	
	public List getIndexed(int[] is, Obj dflt) {
		index(is, length());
		return getExact(is, dflt);
	}

	/** General list indexing */
	public Obj getIndexed(Obj index) {
		if(index.isa(Obj.NUMBER)) {
			return getIndexed(asNumber(index).toInt());
		} else if (index.isa(Obj.CHAR) && index.str().equals("*")) {
			return deepcopy();
		} else if (index.isa(Obj.LIST)) {
			ListImpl idx = asList(index).impl();
			if (idx.length() == 0) {
				return similarEmpty();
			} else {
				// Optimization for numberlist
				if (index.isa(Obj.NUMBERLIST)) {
					return getIndexed(asNumberList(index).toIntArray());
				} else  {
					List index_list = asList(index);
					List out = new List();
					for (int i = 0; i < index_list.length(); i++) {
						out.mutAdd(getIndexed(index_list.getExact(i)));
					}
					return out;
				}
			}
		} 
		else if (index.isa(Obj.BLOCK)) {
			return filter((Block)index);
		} else {
			throw new TypeError("Cannot index list using object:\n"
					+ "list:\t" + repr() + "\n"
					+ "index:\t" + index.repr());
		}
	}

	public Obj getIndexed(Obj index, Obj dflt) {
		if(index.isa(Obj.NUMBER)) {
			return getIndexed(asNumber(index).toInt(), dflt);
		} else if (index.isa(Obj.CHAR) && index.str().equals("*")) {
			return deepcopy();
		} else if (index.isa(Obj.LIST)) {
			ListImpl idx = asList(index).impl();
			if (idx.length() == 0) {
				return similarEmpty();
			} else {
				// Optimization for numberlist
				if (index.isa(Obj.NUMBERLIST)) {
					return getIndexed(asNumberList(index).toIntArray(), dflt);
				} else  {
					List index_list = asList(index);
					List out = new List();
					for (int i = 0; i < index_list.length(); i++) {
						out.mutAdd(getIndexed(index_list.getExact(i), dflt));
					}
					return out;
				}
			}
		} 
		else if (index.isa(Obj.BLOCK)) {
			return filter((Block)index, dflt);
		} else {
			throw new TypeError("Cannot index list using object:\n"
					+ "list:\t" + repr() + "\n"
					+ "index:\t" + index.repr());
		}
	}
	
	/** Remove the 0-indexed item from the list */
	public Obj mutRemoveExact(int i) {
		final Obj o = _list.remove(i);
		promote();
		return o;
	}
	
	/** Remove each of the 0-indexed items from the list */
	public void mutRemoveAllExact(int[] ixs) {
		_list.removeAll(ixs);
		promote();
	}

	/** Remove the 0-indexed item from the list */
	public Obj mutRemoveIndexed(int i) {
		return mutRemoveExact(index(i, length()));
	}
	
	/** Remove each of the 0-indexed items from the list */
	public void mutRemoveAllIndexed(int[] ixs) {
		index(ixs, length());
		mutRemoveAllExact(ixs);
	}
	
	/** Return the index of the first occurrence of Obj in the list */
	public int find(Obj o) {
		return _list.find(o);
	}
	
	public List findAll(Obj o) {
		return new List(_list.findAll(o));
	}
	
	/** Return the index of the last occurrence of Obj in the list */
	public int findBack(Obj o) {
		return _list.findBack(o);
	}
	
	/** Return the number of occurrences of Obj in the list */
	public int count(Obj o) {
		return _list.count(o);
	}
	
	/** Return a list containing the unique elements of the original list */
	public List unique() {
		return new List(_list.unique());
	}
	
	/** Return an empty list that is the same type as the callee */
	public List similarEmpty() {
		return new List(_list.similarEmpty());
	}

	/** Return a null filled list that is the same shape as the callee */
	public List sameShapeNull() {
		return _list.sameShapeNull();
	}
	
	////////////////////////
	// LIST MODIFICATIONS //
	////////////////////////
	
	/** Set index i of the list to Obj o, throws an exception 
	 * if the list is of lower type than o. (Ex: insert "hello" 
	 * into a NumberList)
	 */
	public void mutSetExact(int i, Obj o) {
		try {
			_list.set(i, o);
		} catch (ClassCastException e) {
			_list = new GenericList(_list.getObjAL());
			_list.set(i, o);
			promote();
		}
	}

	/** Set index i of the list to Obj o, throws an exception 
	 * if the list is of lower type than o. (Ex: insert "hello" 
	 * into a NumberList)
	 */
	public void mutSetIndexed(int i, Obj o) {
		final int len = length();
		final int idx = index(i, len);
		if (idx < 0 || idx >= len) {
			throw new IndexError(this, Num.fromInt(idx));
		} else {
			mutSetExact(idx, o);
		}
	}
	
	
	/** Add item to the end of the list. throws an exception 
	 * if the list is of lower type than o. (Ex: insert "hello" 
	 * into a NumberList)
	 */
	public void mutAdd(Obj o) {
		// Only automatically run the promote if this is the first item in the list
		final boolean do_promote = length() == 0;
		try {
			_list.addItem(o);
			if (do_promote) promote();
		} catch (ClassCastException e) {
			_list = new GenericList(_list.getObjAL());
			_list.addItem(o);
			promote();
		}
	}
	
	/** Insert an item into the list at pos i. throws an exception 
	 * if the list is of lower type than o. (Ex: insert "hello" 
	 * into a NumberList)
	 */
	public void mutAddExact(int i, Obj o) {
		// Only automatically run the promote if this is the first item in the list
		final boolean do_promote = length() == 0;
		try {
			_list.addItem(i, o);
			if (do_promote) promote();
		} catch (ClassCastException e) {
			_list = new GenericList(_list.getObjAL());
			_list.addItem(i, o);
			promote();
		}
	}
	
	/** Extent the current list with l. throws an exception 
	 * if the list is of lower type than o.
	 * ( Ex. {@code [1 2 3].extend(['a 'b 'c])} )
	 */
	public void mutAddAll(List l) {
		// Only automatically run the promote if this is the first item in the list
		final boolean do_promote = length() == 0;
		try {
			_list.addAll(l._list);
			if (do_promote) promote();
		} catch (ClassCastException e) {
			_list = new GenericList(_list.getObjAL());
			_list.addAll(l._list);
			promote();
		}
	}

	/** Add obj to the end of the list. Always make a copy of the list */
	public List copyAddItem(Obj o) {
		List out = new List(impl().copy());
		out.mutAdd(o);
		return out;
	}
	
	/** Add obj to the front of the list. Always make a copy of the list */
	public List copyAddItemExact(int i, Obj o) {
		List out = new List(impl().copy());
		out.mutAddExact(i, o);
		return out;
	}
	
	/** Join two lists into a single list. Always make a copy of the list */
	public List copyAddAll(List list2) {
		List l = new List(impl().copy());
		l.mutAddAll(list2);
		return l;
	}

	/** General list setting **/
	public void mutSetIndexed(Obj index, Obj item) {
		if(index.isa(Obj.NUMBER)) {
			mutSetIndexed(asNumber(index).toInt(), item);
		} else if (index.isa(Obj.LIST)) {
			List l_index = asList(index);

			NDListIterator list_iter = new NDListIterator(this);
			NDListIterator index_iter = new NDListIterator(l_index);

			// Set index with mask
			if (item.isa(Obj.LIST)) {
				List l_item = (List)item;
				
				if (l_item.length() == 0) {
					throw new IndexError("Cannot set index of list using empty item list:\n"
							+ "list:\t" + repr() + "\n"
							+ "index:\t" +  index.repr() + "\n"
							+ "items:\t" + item.repr() + "\n");
				}
				
				NDListIterator value_iter = new NDListIterator(l_item);

				try {
					while (!list_iter.doneNoCheckLoop()) {
						if (index_iter.next().bool()) {
							list_iter.setNext(value_iter.next());
						} else {
							list_iter.skip();
							value_iter.skip();
						}
					}
				} catch (IndexError e) {
					throw new IndexError("Shape mismatch for masked setindex");
				}

			} else {
				try {
					while (!list_iter.doneNoCheckLoop()) {
						if (index_iter.next().bool()) {
							list_iter.setNext(item);
						} else {
							list_iter.skip();
						}
					}
				} catch (IndexError e) {
					throw new IndexError("Shape mismatch for masked setindex");
				}
			}
		} 
		else if (index.isa(Obj.BLOCK)) {
			boolean[] truthIdxs = filterIndex(asBlock(index));
			for (int i = 0; i < length(); i++) {
				if (truthIdxs[i]) {
					mutSetExact(i, item);
				}
			}
			
		} else {
			throw new TypeError("Cannot index list using object:\n"
					+ "\tlist: " + repr() + "\n"
					+ "\tindex: " + index.repr());
		}
	}
	
	/** Convert to numeric list. Throw an exception
	 * if the list contains non-numbers
	 */
	public NumberList toNumberList() {
		return _list.toNumberList();
	}
	
	//////////////////////
	// Helper Functions //
	//////////////////////

	/** Index from the back if negative */
	public static int index(int i, int length) {
		return i >= 0 ? i : length + i;
	}

	/** Index from the back if negative */
	public static void index(int[] is, int length) {
		for (int j = 0; j < is.length; j++) {
			is[j] = index(is[j], length);
		}
	}

	
	///////////////////
	// Obj Overrides //
	///////////////////

	@Override
	public List deepcopy() {
		return new List(_list.deepcopy());
	}

	@Override
	public boolean bool() {
		return _list.bool();
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		if (stream.visit(this)) {
			_list.repr(stream);
			stream.popVisited(this);
		} else {
			stream.print("[...]");
		}
		return stream;
	}

	@Override
	public String str() {
		return _list.str();
	}

	@Override
	public boolean equiv(Obj o) {
		return o instanceof List && _list.equiv(asList(o).impl());
	}

	@Override
	public boolean isa(byte type) {
		return _list.isa(type);
	}

	@Override
	public byte type() {
		return _list.type();
	}
	
	
}
