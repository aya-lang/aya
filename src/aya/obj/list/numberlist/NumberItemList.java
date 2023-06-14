package aya.obj.list.numberlist;

import static aya.util.Casting.asNumber;

import java.util.ArrayList;
import java.util.Collections;

import aya.ReprStream;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.list.ListAlgorithms;
import aya.obj.list.ListImpl;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.NumberMath;
import aya.util.Casting;

/** List containing a list of Number objects */
public class NumberItemList extends NumberList {
	
	ArrayList<Number> _list;
	private int _doubles = 0;
	
	// Use NumberList.fromNumberAL outside of package
	protected NumberItemList(ArrayList<Number> list) {
		_list = list;
		for (Number n : list) incDoubleCounter(n);
	}

	public static NumberItemList test_fromAL(ArrayList<Number> list) {
		return new NumberItemList(list);
	}

	// Use NumberList.fromNumberAL outside of package
	protected NumberItemList(ArrayList<Number> list, int num_doubles) {
		_list = list;
		_doubles = num_doubles;
	}

	@Override
	public NumberList promote() {
		final int len = _list.size();
		if (_doubles == len) {
			double[] out = new double[len];
			for (int i = 0; i < len; i++) {
				out[i] = _list.get(i).toDouble();
			}
			return new DoubleList(out);
		} else {
			return this;
		}
	}
	

	/** Create a new numeric list by repeating item, repeats times */
	// Use NumberList.repeat outside of package
	protected NumberItemList(Number item, int repeats) {
		_list = new ArrayList<Number>(repeats);
		for (int i = 0; i < repeats; i++) {
			_list.add(item);
		}
		if (item.isa(Obj.NUM)) {
			_doubles = _list.size();
		}
	}
	
	protected NumberItemList(Number lo, Number hi, Number inc) {
		//Calculate the number of items, this will return a negative value if array creation is impossible
 		int numOfItems = NumberMath.div(NumberMath.sub(hi, lo), inc).floor().toInt() + 1;
	
		if(numOfItems > 10000000) {
			throw new ValueError("Cannot create range with more than 10^7 elements"); 
		} else if (numOfItems < 0) {
			throw new ValueError("Cannot create range containing a negative number of elements in"
					+ " ["+ lo.repr(new ReprStream()) +" "+ NumberMath.add(lo,inc).repr(new ReprStream()) +" "+ hi.repr(new ReprStream()) +"]" );
		}
		
		_list = new ArrayList<Number>(numOfItems);
		
		
		
		//Increment up or down?
		if((lo.compareTo(hi) > 0 && inc.compareTo(inc.zero()) > 0) || (lo.compareTo(hi) < 0 && inc.compareTo(inc.zero()) < 0)) {
			for(int i = 0; i < numOfItems; i++, NumberMath.sub(lo, inc)) {
				_list.add(lo);
			}
		} else {
			for(int i = 0; i < numOfItems; i++, lo = NumberMath.add(lo, inc)) {
				_list.add(lo);
			}
		}
	}
	
	
	//////////////////////////
	// NUMBERLIST OVERRIDES //
	//////////////////////////
	
	@Override
	public Number max() {
		Number max = Num.MIN_VALUE;
		for (int i = 0; i < _list.size(); i++) {
			if (!Double.isNaN(_list.get(i).toDouble()) && _list.get(i).compareTo(max) > 0) {
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
		return _list.size() == 0 ? Num.ZERO : NumberMath.div(sum(), Num.fromInt(_list.size()));
	}

	@Override
	public Number sum() {
		Number total = Num.ZERO;
		for (int i = 0; i < _list.size(); i++) {
			total = NumberMath.add(total, _list.get(i));
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
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.add(b, n));
		return new NumberItemList(out);
	}

	@Override
	public NumberList sub(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.sub(b, n));
		return new NumberItemList(out);
	}

	@Override
	public NumberList div(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.div(b, n));
		return new NumberItemList(out);
	}

	@Override
	public NumberList mul(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.mul(b, n));
		return new NumberItemList(out);
	}

	@Override
	public NumberList mod(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.mod(b, n));
		return new NumberItemList(out);
	}
	
	@Override
	public NumberList idiv(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.idiv(b, n));
		return new NumberItemList(out);
	}

	@Override
	public NumberList pow(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.pow(b, n));
		return new NumberItemList(out);
	}

	@Override
	public NumberList subFrom(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.sub(n, b));
		return new NumberItemList(out);
	}

	@Override
	public NumberList divFrom(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.div(n, b));
		return new NumberItemList(out);
	}

	@Override
	public NumberList modFrom(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.mod(n, b));
		return new NumberItemList(out);
	}

	@Override
	public NumberList idivFrom(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.idiv(n, b));
		return new NumberItemList(out);
	}

	@Override
	public NumberList powFrom(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.pow(n, b));
		return new NumberItemList(out);
	}

	@Override
	public NumberList band(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.band(b, n));
		return new NumberItemList(out);
	}

	@Override
	public NumberList bandFrom(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.band(n, b));
		return new NumberItemList(out);
	}

	@Override
	public NumberList bor(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.bor(b, n));
		return new NumberItemList(out);
	}

	@Override
	public NumberList borFrom(Number n) {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.bor(n, b));
		return new NumberItemList(out);
	}
	
	
	
	
	@Override
	public NumberList negate() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.negate());
		return new NumberItemList(out);
	}

	@Override
	public NumberList bnot() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(NumberMath.bnot(b));
		return new NumberItemList(out);
	}

	@Override
	public NumberList signnum() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.signnum());
		return new NumberItemList(out);
	}

	@Override
	public NumberList factorial() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.factorial());
		return new NumberItemList(out);
	}

	@Override
	public NumberList abs() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.abs());
		return new NumberItemList(out);
	}
	
	@Override
	public NumberList exp() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.exp());
		return new NumberItemList(out);
	}

	@Override
	public NumberList sin() {	
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.sin());
		return new NumberItemList(out);
	}

	@Override
	public NumberList cos() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.cos());
		return new NumberItemList(out);
	}

	@Override
	public NumberList tan() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.tan());
		return new NumberItemList(out);
	}

	@Override
	public NumberList asin() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.asin());
		return new NumberItemList(out);
	}

	@Override
	public NumberList acos() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.acos());
		return new NumberItemList(out);
	}

	@Override
	public NumberList atan() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.atan());
		return new NumberItemList(out);
	}

	@Override
	public NumberList log() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.log());
		return new NumberItemList(out);
	}

	@Override
	public NumberList ln() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.ln());
		return new NumberItemList(out);
	}

	@Override
	public NumberList sqrt() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.sqrt());
		return new NumberItemList(out);
	}

	@Override
	public NumberList ceil() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.ceil());
		return new NumberItemList(out);
	}

	@Override
	public NumberList floor() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.floor());
		return new NumberItemList(out);
	}

	@Override
	public NumberList imag() {
		ArrayList<Number> out = emptyAL();
		for (Number b : _list) out.add(b.imag());
		return new NumberItemList(out);
	}
	
	@Override
	public ArrayList<Number> toArrayList() {
		return _list;
	}
	
	public ArrayList<Number> emptyAL() {
		return new ArrayList<Number>(_list.size());
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
		return new NumberItemList(ListAlgorithms.headNoDeepcopyPad(_list, n, Num.ZERO));
	}

	@Override
	public NumberItemList tail(int n) {
		return new NumberItemList(ListAlgorithms.tailNoDeepcopyPad(_list, n, Num.ZERO));
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
		final Number n = _list.remove(0);
		decDoubleCounter(n);
		return n;
	}

	@Override
	public Number popBack() {
		final Number n = _list.remove(_list.size()-1);
		decDoubleCounter(n);
		return n;
	}

	@Override
	public void reverse() {
		Collections.reverse(_list);
	}

	@Override
	public ListImpl rotate(int n) {
		ArrayList<Number> out = new ArrayList<>(_list);
		ListAlgorithms.rotate(out, n);
		return new NumberItemList(out);
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
		Number n = _list.remove(i);
		decDoubleCounter(n);
		return n;
	}
	
	@Override
	public void removeAll(int[] ixs) {
		ListAlgorithms.removeAll(_list, ixs);
		// Re-compute counters
		_doubles = 0;
		for (Number n : _list) incDoubleCounter(n);
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
		final Number n = (Number)o;
		_list.add(n);
		incDoubleCounter(n);
	}
	
	@Override
	public void addItem(int i, Obj o) {
		final Number n = (Number)o;
		_list.add(i, n);
		incDoubleCounter(n);
	}

	@Override
	public void addAll(ListImpl l) {
		for (int i = 0; i < l.length(); i++) {
			addItem(l.get(i));
		}
	}
	
	@Override
	public NumberItemList copy() {
		ArrayList<Number> out = emptyAL();
		out.addAll(_list);
		return new NumberItemList(out, _doubles);
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
	public List sameShapeNull() {
		return new List(new NumberItemList(Num.ZERO, length()));
	}

	@Override
	protected ListImpl flatten() {
		return copy();
	}
	
	@Override
	public List split(Obj o) {
		if (o.isa(Obj.NUMBER)) {
			return List.from2DTemplate(ListAlgorithms.split(this._list, Casting.asNumber(o)));
		} else {
			return new List();
		}
	}
	
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public NumberItemList deepcopy() {
		ArrayList<Number> copy = emptyAL();
		for (int i = 0; i < _list.size(); i++) {
			copy.add(_list.get(i).deepcopy());
		}
		return new NumberItemList(copy, _doubles);	
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
	
	private void boundsCheck(NumberList a, NumberList b) {
		if (a.length() != b.length())
			throw new ValueError("List length mismatch\n"
					+ "  " + a.str() + "\n  " + b.str());
	}
	
	@Override
	public NumberList add(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(NumberMath.add(this.get(i), ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList sub(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(NumberMath.sub(this.get(i), ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList subFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(NumberMath.sub(ns.get(i), this.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList div(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(NumberMath.div(this.get(i), ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList divFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(NumberMath.div(ns.get(i), this.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList mul(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(NumberMath.mul(this.get(i), ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList mod(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(NumberMath.mod(this.get(i), ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList modFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(NumberMath.mod(ns.get(i), this.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList idiv(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(NumberMath.idiv(this.get(i), ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList idivFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(NumberMath.idiv(ns.get(i), this.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList pow(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(NumberMath.pow(this.get(i), ns.get(i)));
		}
		return new NumberItemList(out);
	}

	@Override
	public NumberList powFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		ArrayList<Number> out = new ArrayList<Number>(len);
		for (int i = 0; i < len; i++) {
			out.add(NumberMath.pow(ns.get(i), this.get(i)));
		}
		return new NumberItemList(out);
	}




	@Override
	public NumberList band(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = NumberMath.band(this.get(i), ns.get(i)).toDouble();
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList bandFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = NumberMath.band(ns.get(i), this.get(i)).toDouble();
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList bor(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = NumberMath.bor(this.get(i), ns.get(i)).toDouble();
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList borFrom(NumberList ns) {
		boundsCheck(this, ns);
		int len = ns.length();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = NumberMath.bor(ns.get(i), this.get(i)).toDouble();
		}
		return new DoubleList(out);
	}

	
	
	@Override
	public NumberList lt(Number n) {
		int len = _list.size();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = _list.get(i).compareTo(n) < 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList lt(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.size();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = _list.get(i).compareTo(ns.get(i)) < 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList leq(Number n) {
		int len = _list.size();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = _list.get(i).compareTo(n) <= 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList leq(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.size();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = _list.get(i).compareTo(ns.get(i)) <= 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList gt(Number n) {
		int len = _list.size();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = _list.get(i).compareTo(n) > 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList gt(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.size();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = _list.get(i).compareTo(ns.get(i)) > 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList geq(Number n) {
		int len = _list.size();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = _list.get(i).compareTo(n) >= 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList geq(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.size();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = _list.get(i).compareTo(ns.get(i)) >= 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList eq(Number n) {
		int len = _list.size();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = _list.get(i).compareTo(n) == 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList eq(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.size();
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = _list.get(i).compareTo(ns.get(i)) == 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}


	private void incDoubleCounter(Number n) {
		if (n.isa(Obj.NUM)) {
			_doubles++;
		}
	}

	private void decDoubleCounter(Number n) {
		if (n.isa(Obj.NUM)) {
			_doubles--;
		}
	}


}
