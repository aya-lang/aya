package aya.obj.list;

import static aya.util.Casting.asList;

import java.util.ArrayList;
import java.util.Collections;

import aya.ReprStream;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.character.Char;
import aya.obj.list.numberlist.DoubleList;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.util.Casting;

/** List of objects of any type */
public class GenericList extends ListImpl {
		
	private ArrayList<Obj> _list;
	private int _chars;
	private int _nums;
	private int _doubles;
	
	public GenericList(ArrayList<Obj> l) {
		_list = l;
		_chars = 0;
		_nums = 0;
		_doubles = 0;
		
		for (Obj o : _list) {
			incCharNumCounter(o);
		}
	}
	
	/** Create a new list by repeating item, repeats times. 
	 * Uses deepcopy() to copy the items into the list 
	 */
	public GenericList(Obj item, int repeats) {
		_list = new ArrayList<Obj>(repeats);
		for (int i = 0; i < repeats; i++) {
			_list.add(item.deepcopy());
		}
		
		if (item.isa(Obj.CHAR)) {
			_chars = repeats;
		} else if (item.isa(Obj.NUMBER)) {
			_nums = repeats;
		}
	}
	
	////////////////////////////
	// CONVERSION & PROMOTION //
	////////////////////////////
	
	/** Convert to string assuming all items are Char */
	private Str asStr() {
		char[] cs = new char[_list.size()];
		for (int i = 0; i < _list.size(); i++) {
			cs[i] = ((Char)(_list.get(i))).charValue();
		}
		return new Str(new String(cs));
	}
	
	@Override
	public NumberList toNumberList() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			if (!_list.get(i).isa(Obj.NUMBER)) {
				throw new ValueError("Cannot convert list " + repr() + " to a numeric list.");
			} else {
				out.add((Number)(_list.get(i)));
			}
		}
		return NumberList.fromNumberAL(out);
	}

	private DoubleList toDoubleList() {
		final int len = length();
		double[] ds = new double[len];
		for (int i = 0; i < len; i++) {
			ds[i] = ((Number)(_list.get(i))).toDouble();
		}
		return new DoubleList(ds);
	}


	/** If all items in the list are a Number, convert this list to a
	 * NumericItemList, if all items in the list are a Char, convert
	 * to a Str, otherwise, return <code>this</code>.
	 * 
	 * @return
	 */
	public ListImpl promote() {
		final int len = _list.size();
		if (len == 0) {
			return this;
		} else {
			if (_chars == len) {
				return asStr();
			} else if (_doubles == len) {
				return toDoubleList();
			} else if (_nums == len) {
				return toNumberList();
			} else {
				return this;
			}
		}
	}
	
	
	////////////////////
	// LIST OVERRIDES //
	////////////////////
	
	
	@Override
	public int length() {
		return _list.size();
	}

	@Override
	public ListImpl head(int n) {
		Obj fill = tail();
		if (fill.isa(Obj.LIST)) {
			fill = asList(fill).sameShapeNull();
		} else {
			fill = Num.ZERO;
		}
		return new GenericList(ListAlgorithms.headDeepcopyPad(_list, n, fill)).promote();
	}

	@Override
	public ListImpl tail(int n) {
		Obj fill = head();
		if (fill.isa(Obj.LIST)) {
			fill = asList(fill).sameShapeNull();
		} else {
			fill = Num.ZERO;
		}
		return new GenericList(ListAlgorithms.tailDeepcopyPad(_list, n, fill)).promote();
	}

	@Override
	public Obj head() {
		return _list.get(0);
	}

	@Override
	public Obj tail() {
		return _list.get(_list.size()-1);
	}

	@Override
	public Obj pop() {
		final Obj o = _list.remove(0);
		decCharNumCounter(o);
		return o;
	}

	@Override
	public Obj popBack() {
		final Obj o = _list.remove(_list.size()-1);
		decCharNumCounter(o);
		return o;
	}

	@Override
	public void reverse() {
		Collections.reverse(_list);
	}

	@Override
	public void rotate(int n) {
		ListAlgorithms.rotate(_list, n);
	}

	@Override
	public ListImpl slice(int i, int j) {
		return new GenericList(ListAlgorithms.slice(_list, i, j)).promote();
	}

	@Override
	public Obj get(int i) {
		return _list.get(i);
	}
	
	@Override
	public ListImpl get(int[] is) {
		GenericList out = new GenericList(new ArrayList<Obj>(is.length));
		for (int i : is) {
			out.addItem( _list.get(i));
		}
		return out.promote();
	}
	
	@Override
	public Obj remove(int i) {
		return _list.remove(i);
	}
	
	@Override
	public void removeAll(int[] ixs) {
		ListAlgorithms.removeAll(_list, ixs);
	}

	@Override
	public int find(Obj o) {
		return ListAlgorithms.find(_list, o);
	}

	@Override
	public NumberList findAll(Obj o) {
		return NumberList.fromNumberAL(ListAlgorithms.findAll(_list, o));
	}

	@Override
	public int findBack(Obj o) {
		return ListAlgorithms.findBack(_list, o);
	}

	@Override
	public int count(Obj o) {
		return ListAlgorithms.count(_list, o);
	}

	@Override
	public void sort() {
		if (_list.size() == 0) return;

		// Are they all strings?
		ArrayList<Str> strs = new ArrayList<Str>();
		for (Obj o : _list) {
			if (o.isa(Obj.STR)) {
				strs.add(Casting.asStr(o));
			} else {
				throw new ValueError("Cannot sort generic list: " + repr());
			}
		}
		Collections.sort(strs);
		_list.clear();
		for (Str s : strs) _list.add(List.fromStr(s));
	}

	@Override
	public void set(int i, Obj o) {
		if (o.hashCode() == this.hashCode())
		{
			throw new ValueError("Cannot set list as member of itself");
		}
		// Decrement the _char / _num counter
		Obj old = _list.get(i);
		decCharNumCounter(old);
		
		// Increment the _char / _num counter
		incCharNumCounter(o);
		
		_list.set(i, o);
	}
	
	@Override
	public ArrayList<Obj> getObjAL() {
		return _list;
	}
	
	@Override
	public ListImpl unique() {
		return new GenericList(ListAlgorithms.unique(_list)).promote();
	}
	
	@Override
	public void addItem(Obj o) {
		incCharNumCounter(o);
		_list.add(o);
	}
	
	@Override
	public void addItem(int i, Obj o) {
		incCharNumCounter(o);
		_list.add(i, o);
	}

	@Override
	public void addAll(ListImpl l) {
		for (int i = 0; i < l.length(); i++) {
			incCharNumCounter(l.get(i));
			_list.add(l.get(i));
		}
	}
	
	@Override
	public ListImpl copy() {
		ArrayList<Obj> out = new ArrayList<>(_list.size());
		out.addAll(_list);
		return new GenericList(out);
	}
	
	@Override
	public boolean canInsert(Obj o) {
		return true;
	}
	
	@Override
	public GenericList similarEmpty() {
		return new GenericList(new ArrayList<Obj>());
	}

	@Override
	public List sameShapeNull() {
		if (length() == 0) {
			return new List();
		} else {
			Obj head = this.head();
			if (head.isa(Obj.LIST)) {
				return new List(new GenericList(asList(head).sameShapeNull(), length()));
			} else {
				return new List(NumberList.repeat(Num.ZERO, length()));
			}
		}
	}

	@Override
	protected ListImpl flatten() {
		GenericList out = new GenericList(new ArrayList<Obj>());
		for (int i = 0; i < length(); i++) {
			Obj o = get(i);
			if (o.isa(Obj.LIST)) {
				out.addAll(asList(o).impl().flatten());
			} else {
				out.addItem(o);
			}
		}
		return out.promote();
	}

	@Override
	public List split(Obj o) {
		return List.from2D(ListAlgorithms.split(this._list, o));
	}
	
	
	////////////////////
	// Obj OVERRIDES //
	////////////////////
	
	@Override
	public ListImpl deepcopy() {
		ArrayList<Obj> out = new ArrayList<Obj>(_list.size());
		for (Obj o : _list) {
			out.add(o.deepcopy());
		}
		return new GenericList(out);
	}

	@Override
	public boolean bool() {
		return _list.size() != 0;
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		return ListAlgorithms.repr(stream, _list);
	}

	@Override
	public String str() {
		return ListAlgorithms.str(_list);
	}

	@Override
	public boolean equiv(ListImpl list) {
		// Must have the same length
		if (list.length() == this.length()) {
			// Every corresponding item must be equivalent
			for (int i = 0; i < this.length(); i++) {
				if (!list.get(i).equiv(_list.get(i))) {
					return false;
				}
			}
			return true;
		} else { 
			return false;
		}
	}

	@Override
	public boolean isa(byte type) {
		return type == Obj.LIST || type == Obj.OBJLIST || (length() == 0 && (type == Obj.NUMBERLIST || type == Obj.NUMBERITEMLIST));
	}

	@Override
	public byte type() {
		return Obj.OBJLIST;
	}


	////////////////////
	// HELPER METHODS //
	////////////////////
	
	private void incCharNumCounter(Obj o) {
		if (o.isa(Obj.CHAR)) {
			_chars += 1;
		} else if (o.isa(Obj.NUM)) {
			_nums += 1;
			_doubles += 1;
		} else if (o.isa(Obj.NUMBER)) {
			_nums += 1;
		} 
	}
	
	private void decCharNumCounter(Obj o) {
		if (o.isa(Obj.CHAR)) {
			_chars -= 1;
		} else if (o.isa(Obj.NUM)) {
			_nums -= 1;
			_doubles -= 1;
		} else if (o.isa(Obj.NUMBER)) {
			_nums -= 1;
		} 
	}
	
}
