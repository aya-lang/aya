package obj.list;

import java.util.ArrayList;
import java.util.Collections;

import element.exceptions.ElementRuntimeException;
import obj.Obj;
import obj.character.Char;
import obj.number.Num;
import obj.number.Number;
import obj.list.numberlist.NumberItemList;

public class ObjList extends List {
		
	private ArrayList<Obj> _list;
	private int _chars;
	private int _nums;
	
	public ObjList(ArrayList<Obj> list) {
		_list = list;
		_chars = 0;
		_nums = 0;
		
		for (Obj o : _list) {
			if (o.isa(Obj.CHAR)) {
				_chars++;
			} else if (o.isa(Obj.NUMBER)) {
				_nums++;
			}
		}
	}
	
	////////////////////////////
	// CONVERSION & PROMOTION //
	////////////////////////////
	
	private boolean isStr() {
		return _list.size() != 0 && _chars == _list.size();
	}
	
	private boolean isNumericList() {
		return _list.size() != 0 && _nums == _list.size();
	}
	
	private Str asStr() {
		char[] cs = new char[_list.size()];
		for (int i = 0; i < _list.size(); i++) {
			cs[i] = ((Char)(_list.get(i))).charValue();
		}
		return new Str(new String(cs));
	}
	
	private NumberItemList asNumericList() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add((Number)(_list.get(i)));
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
			return asNumericList();
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
		return new ObjList(out);
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
		return new ObjList(out);
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
		return _list.remove(0);
	}

	@Override
	public Obj popBack() {
		return _list.remove(_list.size()-1);
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
		return new ObjList(out);
	}

	@Override
	public Obj get(int i) {
		return _list.get(List.index(i, _list.size()));
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

	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public Obj deepcopy() {
		ArrayList<Obj> out = new ArrayList<Obj>(_list.size());
		for (Obj o : _list) {
			out.add(o.deepcopy());
		}
		return new ObjList(out);
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
		// Must be an ObjList
		if (o instanceof ObjList) {
			ObjList objList = (ObjList)o;
			// Must have the same length
			if (objList.length() == this.length()) {
				// Every corresponding item must be equivalent
				for (int i = 0; i < this.length(); i++) {
					if (!objList.get(i).equiv(_list.get(i))) {
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

}
