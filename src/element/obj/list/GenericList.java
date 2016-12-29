package element.obj.list;

import java.util.ArrayList;
import java.util.Collections;

import element.exceptions.ElementRuntimeException;
import element.obj.Obj;
import element.obj.character.Char;
import element.obj.list.numberlist.NumberItemList;
import element.obj.number.Num;
import element.obj.number.Number;

/** List of objects of any type */
public class GenericList extends List {
		
	private ArrayList<Obj> _list;
	private int _chars;
	private int _nums;
	
	public GenericList(ArrayList<Obj> list) {
		_list = list;
		_chars = 0;
		_nums = 0;
		
		for (Obj o : _list) {
			incCharNumCounter(o);
		}
	}
	
	/** Create a new numeric list by repeating item, repeats times. 
	 * Uses deepcopy() to copy the items into the list 
	 */
	public GenericList(Obj item, int repeats) {
		_list = new ArrayList<Obj>(repeats);
		for (int i = 0; i < repeats; i++) {
			_list.add(item.deepcopy());
		}
		
		if (item.isa(Obj.CHAR)) {
			_chars = repeats;
		} else if (item.isa(NUMBER)) {
			_nums = repeats;
		}
	}
	
	////////////////////////////
	// CONVERSION & PROMOTION //
	////////////////////////////
	
	/** Return true if all elements are of type Char */
	private boolean isStr() {
		return _list.size() != 0 && _chars == _list.size();
	}
	
	/** Return true if all elements ar of type Number */
	private boolean isNumericList() {
		return _list.size() != 0 && _nums == _list.size();
	}
	
	/** Convert to string assuming all items are Char */
	private Str asStr() {
		char[] cs = new char[_list.size()];
		for (int i = 0; i < _list.size(); i++) {
			cs[i] = ((Char)(_list.get(i))).charValue();
		}
		return new Str(new String(cs));
	}
	
	@Override
	public NumberItemList toNumberList() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			if (!_list.get(i).isa(Obj.NUMBER)) {
				throw new ElementRuntimeException("Cannot convert list " + repr() + " to a numeric list.");
			} else {
				out.add((Number)(_list.get(i)));
			}
		}
		return new NumberItemList(out);
	}
	
	/** If all items in the list are a Number, convert this list to a
	 * NumericItemList, if all items in the list are a Char, convert
	 * to a Str, otherwise, return <code>this</code>.
	 * 
	 * @return
	 */
	public List promote() {
		if (isStr()) {
			return asStr();
		} else if (isNumericList()) {
			return toNumberList();
		} else {
			return this;
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
	public List head(int n) {
		n = List.index(n, _list.size());
		ArrayList<Obj> out = new ArrayList<Obj>(n);
		
		if (n <= _list.size()) {
			for (int i = 0; i < n; i++) {
				out.add(_list.get(i));
			}
		} else {
			out.addAll(_list);
			for (int i = _list.size(); i < n; i++) {
				out.add(Num.ZERO); //Pad with 0s
			}
		}
		return new GenericList(out);
	}

	@Override
	public List tail(int n) {
		n = List.index(n, _list.size());
		ArrayList<Obj> out = new ArrayList<Obj>(n);
		if (n <= _list.size()) {
			for (int i = _list.size()-n; i < _list.size(); i++) {
				out.add(_list.get(i));
			}
		} else {
			for (int i = 0; i < n-_list.size(); i++) {
				out.add(Num.ZERO); //Pad with 0s
			}
			out.addAll(_list);
		}	
		return new GenericList(out);
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
	public List slice(int i, int j) {
		if (i >= j) {
			throw new ElementRuntimeException("Cannot slice list at indices " + i + " and " + j + ".");
		}
		ArrayList<Obj> out = new ArrayList<Obj>(j - i);
		for (int x = i; x < j; x++) {
			out.add(_list.get(x));
		}
		return new GenericList(out);
	}

	@Override
	public Obj get(int i) {
		return _list.get(List.index(i, _list.size()));
	}
	
	@Override
	public Obj remove(int i) {
		return _list.remove(List.index(i, _list.size()));
	}
	
	@Override
	public void removeAll(Integer[] ixs) {
		int size = _list.size();
		
		for (int i = 0; i < ixs.length; i++) {
			_list.set(List.index(ixs[i], size), null);
		}
		
		for (int i = 0; i < _list.size(); i++) {
			if (_list.get(i) == null) {
				_list.remove(i);
				i--;
			}
		}
	}
	


	@Override
	public int find(Obj o) {
		int ix;
		for (ix = 0; ix < _list.size(); ix++) {
			if (o.equiv(_list.get(ix))) {
				return ix;
			}
		}
		return -1;
	}

	@Override
	public int findBack(Obj o) {
		int ix;
		for (ix = _list.size() - 1; ix >= 0; ix--) {
			if (o.equiv(_list.get(ix))) {
				return ix;
			}
		}
		return -1;
	}

	@Override
	public int count(Obj o) {
		int count = 0;
		for (int i = 0; i < _list.size(); i++) {
			count += _list.get(i).equiv(o) ? 1 : 0;
		}
		return count;
	}

	@Override
	public void sort() {
		// TODO: Sort strings
		throw new ElementRuntimeException("Cannot sort generic list: " + repr() + " Cast to numeric list or string first");
	}

	@Override
	public void set(int i, Obj o) {
		// Decrement the _char / _num counter
		Obj old = _list.get(List.index(i, _list.size()));
		decCharNumCounter(old);
		
		// Increment the _char / _num counter
		incCharNumCounter(o);
		
		_list.set(List.index(i, _list.size()), o);
	}
	
	@Override
	public ArrayList<Obj> getObjAL() {
		return _list;
	}
	
	@Override
	public GenericList unique() {
		ArrayList<Obj> unique = new ArrayList<Obj>();
		for (Obj l : _list) {
			boolean alreadyContains = false;
			for (Obj u : unique) {
				if (l.equiv(u)) {
					alreadyContains = true;
					break;
				}
			}
			if (!alreadyContains) {
				unique.add(l);
			}
		}
		return new GenericList(unique);
	}
	
	@Override
	public void addItem(Obj o) {
		incCharNumCounter(o);
		_list.add(o);
	}
	
	@Override
	public void addItem(int i, Obj o) {
		incCharNumCounter(o);
		_list.add(List.index(i, _list.size()), o);
	}

	@Override
	public void addAll(List l) {
		for (int i = 0; i < l.length(); i++) {
			incCharNumCounter(l.get(i));
			_list.add(l.get(i));
		}
	}

	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public Obj deepcopy() {
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
	public String repr() {
		StringBuilder sb = new StringBuilder("[ ");
		for (Obj o : _list) {
			sb.append(o.repr() + " ");
		}
		return sb.append(']').toString();
	}

	@Override
	public String str() {
		return repr();
	}

	@Override
	public boolean equiv(Obj o) {
		// Must be a list
		if (o instanceof List) {
			List list = (List)o;
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
		} else {
			return false;
		}
	}

	@Override
	public boolean isa(byte type) {
		return type == Obj.LIST || type == Obj.OBJLIST;
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
		} else if (o.isa(NUMBER)) {
			_nums += 1;
		}
	}
	
	private void decCharNumCounter(Obj o) {
		if (o.isa(Obj.CHAR)) {
			_chars += 1;
		} else if (o.isa(NUMBER)) {
			_nums += 1;
		}
	}

}
