package element.obj.list.numberlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import element.exceptions.ElementRuntimeException;
import element.obj.Obj;
import element.obj.list.List;
import element.obj.number.Num;
import element.obj.number.Number;

/** List containing a list of Number objects */
public class NumberItemList extends NumberList {
	
	ArrayList<Number> _list;
	
	public NumberItemList(ArrayList<Number> list) {
		_list = list;
	}
	
	/** Create a new numeric list by repeating item, repeats times */
	public NumberItemList(Number item, int repeats) {
		_list = new ArrayList<Number>(repeats);
		for (int i = 0; i < repeats; i++) {
			_list.add(item);
		}
	}

	//////////////////////////
	// NUMBERLIST OVERRIDES //
	//////////////////////////
	
	@Override
	public Number max() {
		Number max = Num.MIN_VALUE;
		for (int i = 0; i < _list.size(); i++) {
			if (_list.get(i).compareTo(max) > 0) {
				max = _list.get(i);
			}
		}
		return max;
	}

	@Override
	public Number min() {
		Number min = Num.MAX_VALUE;
		for (int i = 0; i < _list.size(); i++) {
			if (_list.get(i).compareTo(min) < 0) {
				min = _list.get(i);
			}
		}
		return min;
	}

	@Override
	public Number mean() {
		return _list.size() == 0 ? Num.ZERO : sum().div(new Num(_list.size()));
	}

	@Override
	public Number sum() {
		Number total = Num.ZERO;
		for (int i = 0; i < _list.size(); i++) {
			total = total.add(_list.get(i));
		}
		return total;
	}
	
	@Override
	public Integer[] toIntegerArray() {
		Integer[] ints = new Integer[_list.size()];
		for (int i = 0; i < _list.size(); i++) {
			ints[i] = _list.get(i).toInt();
		}
		return ints;
	}
	
	@Override
	public double[] todoubleArray() {
		double[] ds = new double[_list.size()];
		for (int i = 0; i < _list.size(); i++) {
			ds[i] = _list.get(i).toDouble();
		}
		return ds;
	}

	@Override
	public NumberList add(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).add(n));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList sub(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).sub(n));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList div(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).div(n));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList mul(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).mul(n));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList mod(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).mod(n));
		}
		return new NumberItemList(out);
	}
	
	@Override
	public NumberList idiv(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).idiv(n));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList pow(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).pow(n));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList subFrom(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(n.sub(_list.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList divFrom(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(n.div(_list.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList modFrom(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(n.mod(_list.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList idivFrom(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(n.idiv(_list.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList powFrom(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(n.pow(_list.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList band(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).band(n));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList bandFrom(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(n.band(_list.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList bor(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).bor(n));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList borFrom(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(n.bor(_list.get(i)));
		}
		return new NumberItemList(out);
	}
	
	
	
	
	@Override
	public NumberList negate() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).negate());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList bnot() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).bnot());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList signnum() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).signnum());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList factorial() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).factorial());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList abs() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).abs());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList sin() {	
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).sin());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList cos() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).cos());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList tan() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).tan());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList asin() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).asin());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList acos() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).acos());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList atan() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).atan());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList log() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).log());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList ln() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).ln());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList sqrt() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).sqrt());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList ceil() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).ceil());
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList floor() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).floor());
		}
		return new NumberItemList(out);
	}
	
	@Override
	public ArrayList<Number> toArrayList() {
		return _list;
	}
	
	////////////////////
	// LIST OVERRIDES //
	////////////////////

	@Override
	public int length() {
		return _list.size();
	}

	@Override
	public NumberItemList head(int n) {	
		n = List.index(n, _list.size());
		ArrayList<Number> out = new ArrayList<Number>(n);
		
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
		return new NumberItemList(out);
	}

	@Override
	public NumberItemList tail(int n) {
		n = List.index(n, _list.size());
		ArrayList<Number> out = new ArrayList<Number>(n);
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
		return new NumberItemList(out);
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
	public Number pop() {
		return _list.remove(0);
	}

	@Override
	public Number popBack() {
		return _list.remove(_list.size()-1);
	}

	@Override
	public void reverse() {
		Collections.reverse(_list);
	}

	@Override
	public NumberItemList slice(int i, int j) {
		if (i >= j) {
			throw new ElementRuntimeException("Cannot slice list at indices " + i + " and " + j + ".");
		}
		ArrayList<Number> out = new ArrayList<Number>(j - i);
		for (int x = i; x < j; x++) {
			out.add(_list.get(x));
		}
		return new NumberItemList(out);
	}

	@Override
	public Number get(int i) {
		return _list.get(List.index(i, _list.size()));
	}
	
	@Override
	public Number remove(int i) {
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
		if (o.isa(Obj.NUMBER)) {
			_list.set(List.index(i, _list.size()), (Number)o);
		} else {
			throw new ElementRuntimeException("Cannot set item " + o.repr() + " in numeric list " + this.repr() + ". Item must be a number.");
		}
	}
	
	@Override
	public ArrayList<Obj> getObjAL() {
		ArrayList<Obj> l = new ArrayList<Obj>(_list.size());
		for (Number number : _list) {
			l.add(number);
		}
		return l;
	}
	
	@Override
	public NumberItemList unique() {
		ArrayList<Number> unique = new ArrayList<Number>();
		for (Number l : _list) {
			boolean alreadyContains = false;
			for (Number u : unique) {
				if (l.equiv(u)) {
					alreadyContains = true;
					break;
				}
			}
			if (!alreadyContains) {
				unique.add(l);
			}
		}
		return new NumberItemList(unique);
	}
	
	
	@Override
	public NumberItemList toNumberList() {
		return this;
	}

	@Override
	public void addItem(Obj o) {
		if (o.isa(Obj.NUMBER)) {
			_list.add((Number)o);
		} else {
			throw new ElementRuntimeException("Cannot append " + o.repr() + " to number list " + repr()
					+ ". Use convert list to a generic list to add the item");
		}
	}
	
	@Override
	public void addItem(int i, Obj o) {
		if (o.isa(Obj.NUMBER)) {
			_list.add(List.index(i, _list.size()) , (Number)o);
		} else {
			throw new ElementRuntimeException("Cannot append " + o.repr() + " to number list " + repr()
					+ ". Use convert list to a generic list to add the item");
		}
	}

	@Override
	public void addAll(List l) {
		for (int i = 0; i < l.length(); i++) {
			addItem(l.get(i));
		}
	}
	
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public NumberItemList deepcopy() {
		ArrayList<Number> copy = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			copy.add(_list.get(i).deepcopy());
		}
		return new NumberItemList(copy);	
	}

	@Override
	public boolean bool() {
		return _list.size() != 0;
	}

	@Override
	public String repr() {
		StringBuilder sb = new StringBuilder("[ ");
		for (Number n : _list) {
			sb.append(n.repr() + " ");
		}
		return sb.append(']').toString();
	}

	@Override
	public String str() {
		return repr();
	}

	@Override
	public boolean equiv(Obj o) {
		// Must be a numeric list
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
		return type == Obj.LIST || type == Obj.NUMBERLIST || type == Obj.NUMBERITEMLIST;
	}

	@Override
	public byte type() {
		return Obj.NUMBERITEMLIST;
	}

	//////////////////////
	// HELPER FUNCTIONS //
	//////////////////////
	
	/** Primes up to n **/
	public static NumberItemList primes(int n) {
		boolean[] flags = new boolean[n];
		Arrays.fill(flags, true);
		
		//Mark every space that is not prime
		for (int i = 2; i <= n; i++) {
			int z = 2*i;
			while (z <= n) {
				flags[z-1] = false; //zero index, so sub 1
				z += i;
			}
		}
		
		//Allocate new array
		ArrayList<Number> primeList = new ArrayList<Number>();
		
		//Add primes into array
		for (int i = 2; i <= n; i++) {
			if (flags[i-1]) {
				primeList.add(new Num(i));
			}
		}
		
		return new NumberItemList(primeList);
	}





	

	
	
	


}
