package aya.obj.list;

import java.util.ArrayList;
import java.util.Collections;

import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.list.numberlist.NumberList;

/** List of objects of any type */
public class StrList extends List {
		
	private ArrayList<Str> _list;
	
	public StrList(ArrayList<Str> list) {
		_list = list;
	}
	
	/** Create a new numeric list by repeating item, repeats times. 
	 */
	public StrList(Str item, int repeats) {
		_list = new ArrayList<Str>(repeats);
		for (int i = 0; i < repeats; i++) {
			_list.add(new Str(item.str()));
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
	public StrList head(int n) {
		n = List.index(n, _list.size());
		ArrayList<Str> out = new ArrayList<Str>(n);
		
		if (n <= _list.size()) {
			for (int i = 0; i < n; i++) {
				out.add(_list.get(i));
			}
		} else {
			out.addAll(_list);
			for (int i = _list.size(); i < n; i++) {
				out.add(Str.EMPTY); //Pad with empty strings
			}
		}
		return new StrList(out);
	}

	@Override
	public StrList tail(int n) {
		n = List.index(n, _list.size());
		ArrayList<Str> out = new ArrayList<Str>(n);
		if (n <= _list.size()) {
			for (int i = _list.size()-n; i < _list.size(); i++) {
				out.add(_list.get(i));
			}
		} else {
			for (int i = 0; i < n-_list.size(); i++) {
				out.add(Str.EMPTY); //Pad with 0s
			}
			out.addAll(_list);
		}	
		return new StrList(out);
	}

	@Override
	public Str head() {
		return _list.get(0);
	}

	@Override
	public Str tail() {
		return _list.get(_list.size()-1);
	}

	@Override
	public Str pop() {
		final Str o = _list.remove(0);
		return o;
	}

	@Override
	public Str popBack() {
		final Str o = _list.remove(_list.size()-1);
		return o;
	}

	@Override
	public void reverse() {
		Collections.reverse(_list);
	}

	@Override
	public StrList slice(int i, int j) {
		if (i >= j) {
			throw new AyaRuntimeException("Cannot slice list at indices " + i + " and " + j + ".");
		}
		ArrayList<Str> out = new ArrayList<Str>(j - i);
		for (int x = i; x < j; x++) {
			out.add(_list.get(x));
		}
		return new StrList(out);
	}

	@Override
	public Str get(int i) {
		return _list.get(List.index(i, _list.size()));
	}
	
	@Override
	public StrList get(int[] is) {
		ArrayList<Str> out = new ArrayList<Str>(is.length);
		for (int i : is) {
			out.add( _list.get(List.index(i, _list.size())) );
		}
		return new StrList(out);
	}
	
	@Override
	public Str remove(int i) {
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
		Collections.sort(_list);
	}

	@Override
	public void set(int i, Obj o) {
		if (o.isa(Obj.STR)) {
			_list.set(List.index(i, _list.size()), (Str)o);
		} else {
			throw new AyaRuntimeException("Cannot set item " + o.repr() + " in string list " + this.repr() + ". Item must be a string.");
		}
	}
	
	@Override
	public ArrayList<Obj> getObjAL() {
		//return _list;
		ArrayList<Obj> out = new ArrayList<Obj>(_list.size());
		for (Str s : _list) {
			out.add(s);
		}
		return out;
	}
	
	@Override
	public StrList unique() {
		ArrayList<Str> unique = new ArrayList<Str>();
		for (Str l : _list) {
			boolean alreadyContains = false;
			for (Str u : unique) {
				if (l.equiv(u)) {
					alreadyContains = true;
					break;
				}
			}
			if (!alreadyContains) {
				unique.add(l);
			}
		}
		return new StrList(unique);
	}
	
	@Override
	public void addItem(Obj o) {
		if (o.isa(Obj.STR)) {
			_list.add((Str)o);
		} else {
			throw new AyaRuntimeException("Cannot append " + o.repr() + " to string list " + repr()
					+ ". Convert the list to a generic list to add the item");
		}
	}
	
	@Override
	public void addItem(int i, Obj o) {
		if (o.isa(Obj.STR)) {
			_list.add(List.index(i, _list.size()) , (Str)o);
		} else {
			throw new AyaRuntimeException("Cannot append " + o.repr() + " to string list " + repr()
					+ ". Convert list to a generic list to add the item");
		}
	}

	@Override
	public void addAll(List l) {
		for (int i = 0; i < l.length(); i++) {
			addItem(l.get(i));
		}
	}

	

	@Override
	public NumberList toNumberList() {
		throw new AyaRuntimeException("Cannot convert list " + repr() + " to a numeric list.");
	}
	
	public Str sum() {
		StringBuilder sb = new StringBuilder();
		for (Str s : _list) {
			sb.append(s.str());
		}
		return new Str(sb.toString());
	}
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public StrList deepcopy() {
		ArrayList<Str> out = new ArrayList<Str>(_list.size());
		for (Str o : _list) {
			out.add(o.deepcopy());
		}
		return new StrList(out);
	}

	@Override
	public boolean bool() {
		return _list.size() != 0;
	}

	@Override
	public String repr() {
		StringBuilder sb = new StringBuilder("[ ");
		
		
		if (_list.size() < 50) {
			//Output the whole list
			for (Obj o : _list) {
				sb.append(o.repr() + " ");
			}
		} else {
			// Output 10 front
			for (int i = 0; i < 10; i++) {
				sb.append(_list.get(i).repr() + " ");
			}
			
			sb.append(" ... ");
			
			// Output 10 back
			for (int i = _list.size() - 11; i < _list.size(); i++) {
				sb.append(_list.get(i).repr() + " ");
			}
		}
		
		return sb.append(']').toString();
	}

	@Override
	public String str() {
		StringBuilder sb = new StringBuilder("[ ");
		for (Obj o : _list) {
			sb.append(o.repr() + " ");
		}
		return sb.append(']').toString();
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
		return type == Obj.LIST || type == Obj.STRLIST;
	}

	@Override
	public byte type() {
		return Obj.STRLIST;
	}


}
