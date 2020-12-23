package aya.obj.list.numberlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import aya.ReprStream;
import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.list.ListAlgorithms;
import aya.obj.list.ListImpl;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.NumberMath;

import static aya.util.Casting.asNumber;

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
	
	public NumberItemList(Number lo, Number hi, Number inc) {
		//Calculate the number of items, this will return a negative value if array creation is impossible
 		int numOfItems = hi.sub(lo).div(inc).floor().toInt() + 1;
	
		if(numOfItems > 10000000) {
			throw new AyaRuntimeException("Cannot create range with more than 10^7 elements"); 
		} else if (numOfItems < 0) {
			throw new AyaRuntimeException("Cannot create range containing a negative number of elements in"
					+ " ["+ lo.repr(new ReprStream()) +" "+ lo.add(inc).repr(new ReprStream()) +" "+ hi.repr(new ReprStream()) +"]" );
		}
		
		_list = new ArrayList<Number>(numOfItems);
		
		
		
		//Increment up or down?
		if((lo.compareTo(hi) > 0 && inc.compareTo(inc.zero()) > 0) || (lo.compareTo(hi) < 0 && inc.compareTo(inc.zero()) < 0)) {
			for(int i = 0; i < numOfItems; i++, lo.sub(inc)) {
				_list.add(lo);
			}
		} else {
			for(int i = 0; i < numOfItems; i++, lo = lo.add(inc)) {
				_list.add(lo);
			}
		}
	}
	
	
	//////////////
	// Creation //
	//////////////

	public static NumberItemList fromBytes(byte[] bytes) {
		ArrayList<Number> out = new ArrayList<Number>(bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			out.add(Num.fromByte(bytes[i]));
		}
		return new NumberItemList(out);
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
	public int[] toIntArray() {
		int[] ints = new int[_list.size()];
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
	public byte[] toByteArray() {
		byte[] bs = new byte[_list.size()];
		for (int i = 0; i < _list.size(); i++) {
			bs[i] = _list.get(i).toByte();
		}
		return bs;
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
			out.add( NumberMath.band(_list.get(i), n) );
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList bandFrom(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add( NumberMath.band(n, _list.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList bor(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add( NumberMath.bor(_list.get(i), n));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList borFrom(Number n) {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add( NumberMath.bor(n, _list.get(i)));
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
			out.add( NumberMath.bnot(_list.get(i)));
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
	public NumberList exp() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		for (int i = 0; i < _list.size(); i++) {
			out.add(_list.get(i).exp());
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
		return new NumberItemList(ListAlgorithms.head(_list, n, Num.ZERO));
	}

	@Override
	public NumberItemList tail(int n) {
		return new NumberItemList(ListAlgorithms.tail(_list, n, Num.ZERO));
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
		return new NumberItemList(ListAlgorithms.slice(_list, i, j));
	}

	@Override
	public Number get(int i) {
		return _list.get(i);
	}
	
	@Override
	public NumberItemList get(int[] is) {
		ArrayList<Number> out = new ArrayList<Number>(is.length);
		for (int i : is) {
			out.add(_list.get(i));
		}
		return new NumberItemList(out);
	}
	
	@Override
	public Number remove(int i) {
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
	public int findBack(Obj o) {
		return ListAlgorithms.findBack(_list, o);
	}

	@Override
	public int count(Obj o) {
		return ListAlgorithms.count(_list, o);
	}
	
	@Override
	public void sort() {
		Collections.sort(_list);
	}
	
	@Override
	public void set(int i, Obj o) {
		_list.set(i, asNumber(o));
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
		return new NumberItemList(ListAlgorithms.unique(_list));
	}
	
	
	@Override
	public NumberItemList toNumberList() {
		return this;
	}


	@Override
	public void addItem(Obj o) {
		_list.add((Number)o);
	}
	
	@Override
	public void addItem(int i, Obj o) {
		_list.add(i, (Number)o);
	}

	@Override
	public void addAll(ListImpl l) {
		for (int i = 0; i < l.length(); i++) {
			addItem(l.get(i));
		}
	}
	
	@Override
	public NumberItemList copy() {
		ArrayList<Number> out = new ArrayList<Number>(_list.size());
		out.addAll(_list);
		return new NumberItemList(out);
	}
	
	@Override
	public boolean canInsert(Obj o) {
		return o.isa(Obj.NUMBER);
	}

	@Override
	public NumberItemList similarEmpty() {
		return new NumberItemList(new ArrayList<Number>());
	}

	@Override
	protected ListImpl flatten() {
		return copy();
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
	public ReprStream repr(ReprStream stream) {
		return ListAlgorithms.reprCompact(stream, _list);
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

	private void boundsCheck(NumberList a, NumberList b) {
		if (a.length() != b.length())
			throw new AyaRuntimeException("List length mismatch\n"
					+ "  " + a.str() + "\n  " + b.str());
	}
	
	@Override
	public NumberList add(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(this.get(i).add(ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList sub(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(this.get(i).sub(ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList subFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(ns.get(i).sub(this.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList div(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(this.get(i).div(ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList divFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(ns.get(i).div(this.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList mul(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(this.get(i).mul(ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList mod(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(this.get(i).mod(ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList modFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(ns.get(i).mod(this.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList idiv(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(this.get(i).idiv(ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList idivFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(ns.get(i).idiv(this.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList pow(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(this.get(i).pow(ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList powFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(ns.get(i).pow(this.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList band(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add( NumberMath.band(this.get(i), ns.get(i)) );
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList bandFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add( NumberMath.band(ns.get(i), this.get(i)) );
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList bor(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add( NumberMath.bor(this.get(i), ns.get(i)) );
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList borFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add( NumberMath.bor(ns.get(i), this.get(i)) );
		}
		return new NumberItemList(out);
	}

	
	
	@Override
	public NumberList lt(Number n) {
		int len = _list.size();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(Num.fromBool(_list.get(i).compareTo(n) < 0));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList lt(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.size();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(Num.fromBool(_list.get(i).compareTo(ns.get(i)) < 0));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList leq(Number n) {
		int len = _list.size();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(Num.fromBool(_list.get(i).compareTo(n) <= 0));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList leq(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.size();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(Num.fromBool(_list.get(i).compareTo(ns.get(i)) <= 0));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList gt(Number n) {
		int len = _list.size();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(Num.fromBool(_list.get(i).compareTo(n) > 0));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList gt(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.size();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(Num.fromBool(_list.get(i).compareTo(ns.get(i)) > 0));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList geq(Number n) {
		int len = _list.size();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(Num.fromBool(_list.get(i).compareTo(n) >= 0));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList geq(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.size();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(Num.fromBool(_list.get(i).compareTo(ns.get(i)) >= 0));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList eq(Number n) {
		int len = _list.size();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(Num.fromBool(_list.get(i).compareTo(n) == 0));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList eq(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.size();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(Num.fromBool(_list.get(i).compareTo(ns.get(i)) == 0));
		}
		return new NumberItemList(out);
	}

}
