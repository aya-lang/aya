package aya.obj.list.numberlist;

import static aya.util.Casting.asNumber;

import java.util.ArrayList;
import java.util.Arrays;

import aya.ReprStream;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.list.ListAlgorithms;
import aya.obj.list.ListImpl;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.util.Casting;
import aya.util.MathUtils;

/** List containing a list of Number objects */
public class DoubleList extends NumberList {
	
	double[] _list;
	
	public DoubleList(double[] list) {
		_list = list;
	}
	
	/** Create a new numeric list by repeating item, repeats times */
	public DoubleList(double item, int repeats) {
		_list = new double[repeats];
		if (item != 0.0) {
			for (int i = 0; i < repeats; i++) {
				_list[i] = item;
			}
		}
	}
	
	public DoubleList(double lo, double hi, double inc) {
		//Calculate the number of items, this will return a negative value if array creation is impossible
		int numOfItems = (int)Math.floor((hi - lo) / inc) + 1;
	
		if(numOfItems > 10000000) {
			throw new ValueError("Cannot create range with more than 10^7 elements"); 
		} else if (numOfItems < 0) {
			throw new ValueError("Cannot create range containing a negative number of elements in"
					+ " ["+ (new Num(lo)).repr() +" "+ (new Num(lo+inc)).repr() +" "+ (new Num(hi)).repr() +"]" );
		}
		
		_list = new double[numOfItems];
		
		//Increment up or down?
		if ( (lo > hi && inc > 0) || ((lo < hi) && inc < 0) ) {
			for(int i = 0; i < numOfItems; i++, lo -= inc) {
				_list[i] = lo;
			}
		} else {
			for(int i = 0; i < numOfItems; i++, lo += inc) {
				_list[i] = lo;
			}
		}
	}
	
	
	//////////////
	// Creation //
	//////////////

	public static DoubleList fromBytes(byte[] bytes) {
		double[] out = new double[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			out[i] = (double)bytes[i];
		}
		return new DoubleList(out);
	}

	//////////////////////////
	// NUMBERLIST OVERRIDES //
	//////////////////////////
	
	@Override
	public Number max() {
		double max = Num.MIN_VALUE.toDouble();
		for (int i = 0; i < _list.length; i++) {
			if (!Double.isNaN(_list[i]) && _list[i] > max) {
				max = _list[i];
			}
		}
		return new Num(max);
	}

	@Override
	public Number min() {
		double min = Num.MAX_VALUE.toDouble();
		for (int i = 0; i < _list.length; i++) {
			if (!Double.isNaN(_list[i]) && _list[i] < min) {
				min = _list[i];
			}
		}
		return new Num(min);
	}

	@Override
	public Number mean() {
		return _list.length == 0 ? Num.ZERO : new Num(sum().toDouble() / (double)_list.length);
	}

	@Override
	public Number sum() {
		double total = 0;
		for (int i = 0; i < _list.length; i++) {
			total += _list[i];
		}
		return new Num(total);
	}
	
	@Override
	public Integer[] toIntegerArray() {
		Integer[] ints = new Integer[_list.length];
		for (int i = 0; i < _list.length; i++) {
			ints[i] = (int)_list[i];
		}
		return ints;
	}
			
	@Override
	public int[] toIntArray() {
		int[] ints = new int[_list.length];
		for (int i = 0; i < _list.length; i++) {
			ints[i] = (int)_list[i];
		}
		return ints;
	}
	
	@Override
	public double[] todoubleArray() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) {
			out[i] = _list[i];
		}
		return out;
	}
	
	@Override
	public byte[] toByteArray() {
		byte[] bs = new byte[_list.length];
		for (int i = 0; i < _list.length; i++) {
			bs[i] = (byte)_list[i];
		}
		return bs;
	}

	
	@Override
	public NumberList add(Number n) {
		if (n.isa(Obj.NUM)) {
			double N = n.toDouble();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = _list[i] + N;
			return new DoubleList(out);
		} else {
			return toNumberItemList().add(n);
		}
	}

	@Override
	public NumberList sub(Number n) {
		if (n.isa(Obj.NUM)) {
			double N = n.toDouble();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = _list[i] - N;
			return new DoubleList(out);
		} else {
			return toNumberItemList().sub(n);
		}
	}

	@Override
	public NumberList div(Number n) {
		if (n.isa(Obj.NUM)) {
			double N = n.toDouble();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = _list[i] / N;
			return new DoubleList(out);
		} else {
			return toNumberItemList().div(n);
		}
	}

	@Override
	public NumberList mul(Number n) {
		if (n.isa(Obj.NUM)) {
			double N = n.toDouble();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = _list[i] * N;
			return new DoubleList(out);
		} else {
			return toNumberItemList().mul(n);
		}
	}

	@Override
	public NumberList mod(Number n) {
		if (n.isa(Obj.NUM)) {
			double N = n.toDouble();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = _list[i] % N;
			return new DoubleList(out);
		} else {
			return toNumberItemList().mod(n);
		}
	}
	
	@Override
	public NumberList idiv(Number n) {
		if (n.isa(Obj.NUM)) {
			double N = n.toDouble();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = Math.floor(_list[i] / N);
			return new DoubleList(out);
		} else {
			return toNumberItemList().idiv(n);
		}
	}

	@Override
	public NumberList pow(Number n) {
		if (n.isa(Obj.NUM)) {
			double N = n.toDouble();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = Math.pow(_list[i], N);
			return new DoubleList(out);
		} else {
			return toNumberItemList().pow(n);
		}
	}

	@Override
	public NumberList subFrom(Number n) {
		if (n.isa(Obj.NUM)) {
			double N = n.toDouble();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = N - _list[i];
			return new DoubleList(out);
		} else {
			return toNumberItemList().subFrom(n);
		}
	}

	@Override
	public NumberList divFrom(Number n) {
		if (n.isa(Obj.NUM)) {
			double N = n.toDouble();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = N / _list[i];
			return new DoubleList(out);
		} else {
			return toNumberItemList().divFrom(n);
		}
	}

	@Override
	public NumberList modFrom(Number n) {
		if (n.isa(Obj.NUM)) {
			double N = n.toDouble();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = N % _list[i];
			return new DoubleList(out);
		} else {
			return toNumberItemList().modFrom(n);
		}
	}

	@Override
	public NumberList idivFrom(Number n) {
		if (n.isa(Obj.NUM)) {
			double N = n.toDouble();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = Math.floor(N / _list[i]);
			return new DoubleList(out);
		} else {
			return toNumberItemList().idivFrom(n);
		}
	}

	@Override
	public NumberList powFrom(Number n) {
		if (n.isa(Obj.NUM)) {
			double N = n.toDouble();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = Math.pow(N, _list[i]);
			return new DoubleList(out);
		} else {
			return toNumberItemList().powFrom(n);
		}
	}

	@Override
	public NumberList band(Number n) {
		if (n.isa(Obj.NUM)) {
			int N = n.toInt();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = (double)((int)_list[i] & N);
			return new DoubleList(out);
		} else {
			return toNumberItemList().band(n).promote();
		}
	}

	@Override
	public NumberList bandFrom(Number n) {
		if (n.isa(Obj.NUM)) {
			int N = n.toInt();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = (double)(N & (int)_list[i]);
			return new DoubleList(out);
		} else {
			return toNumberItemList().bandFrom(n).promote();
		}
	}

	@Override
	public NumberList bor(Number n) {
		if (n.isa(Obj.NUM)) {
			int N = n.toInt();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = (double)((int)_list[i] | N);
			return new DoubleList(out);
		} else {
			return toNumberItemList().bor(n).promote();
		}
	}

	@Override
	public NumberList borFrom(Number n) {
		if (n.isa(Obj.NUM)) {
			int N = n.toInt();
			double[] out = new double[_list.length];
			for (int i = 0; i < _list.length; i++) out[i] = (double)(N | (int)_list[i]);
			return new DoubleList(out);
		} else {
			return toNumberItemList().borFrom(n).promote();
		}
	}
	
	

	
	@Override
	public NumberList negate() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = _list[i] * -1;
		return new DoubleList(out);
	}

	@Override
	public NumberList bnot() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = ~((int)_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList signnum() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = MathUtils.signnum(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList factorial() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = MathUtils.factorial((long)_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList abs() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.abs(_list[i]);
		return new DoubleList(out);
	}
	
	@Override
	public NumberList exp() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.exp(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList sin() {	
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.sin(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList cos() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.cos(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList tan() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.tan(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList asin() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.asin(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList acos() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.acos(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList atan() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.atan(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList log() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.log10(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList ln() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.log(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList sqrt() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.sqrt(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList ceil() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.ceil(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList floor() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = Math.floor(_list[i]);
		return new DoubleList(out);
	}

	@Override
	public NumberList imag() {
		return toNumberItemList().imag();
	}

	@Override
	public ArrayList<Number> toArrayList() {
		ArrayList<Number> out = new ArrayList<Number>(_list.length);
		for (int i = 0; i < _list.length; i++) {
			out.add(new Num(_list[i]));
		}
		return out;
	}
	
	////////////////////
	// LIST OVERRIDES //
	////////////////////

	@Override
	public int length() {
		return _list.length;
	}

	@Override
	public DoubleList head(int n) {
		return new DoubleList(ListAlgorithms.headNoDeepcopyPad(_list, n, 0));
	}

	@Override
	public DoubleList tail(int n) {
		return new DoubleList(ListAlgorithms.tailNoDeepcopyPad(_list, n, 0));
	}

	@Override
	public Obj head() {
		return new Num(_list[0]);
	}

	@Override
	public Obj tail() {
		return new Num(_list[_list.length-1]);
	}

	@Override
	public Number pop() {
		double n = _list[0];
		_list = Arrays.copyOfRange(_list, 1, _list.length);
		return new Num(n);
	}

	@Override
	public Number popBack() {
		double n = _list[_list.length-1];
		_list = Arrays.copyOf(_list, _list.length-1);
		return new Num(n);
	}

	@Override
	public void reverse() {
		final int len = _list.length;

        if(len <= 1){
            return;
        }       
        
		double tmp;
        for (int i = 0; i < len / 2; i++) {
            tmp = _list[i];
            _list[i] = _list[len - 1 - i];
            _list[len - 1 - i] = tmp;
        }
	}

	@Override
	public ListImpl rotate(int n) {
		if (n == 0) {
			return new DoubleList(Arrays.copyOf(_list, _list.length));
		} else {
			final int len = _list.length;
			double[] out = new double[len];
			if (n > 0) {
				System.arraycopy(_list, 0, out, n, len - n);
				System.arraycopy(_list, len-n, out, 0, n);
			} else {
				n *= -1;
				System.arraycopy(_list, 0, out, len-n, n);
				System.arraycopy(_list, n, out, 0, len-n);
			}
			return new DoubleList(out);
		}
	}

	@Override
	public DoubleList slice(int i, int j) {
		return new DoubleList(Arrays.copyOfRange(_list, i, j));
	}

	@Override
	public Number get(int i) {
		return new Num(_list[i]);
	}
	
	@Override
	public DoubleList get(int[] is) {
		double[] out = new double[is.length];
		for (int i = 0; i < is.length; i++) {
			out[i] = _list[is[i]];
		}
		return new DoubleList(out);
	}
	
	@Override
	public Number remove(int i) {
		// TODO: DoubleList implementation
		NumberItemList ns = toNumberItemList();
		Number n = ns.remove(i);
		_list = ns.todoubleArray();
		return n;
	}
	
	@Override
	public void removeAll(int[] ixs) {
		// TODO: DoubleList implementation
		NumberItemList ns = toNumberItemList();
		ns.removeAll(ixs);
		_list = ns.todoubleArray();
	}

	@Override
	public int find(Obj o) {
		if (o.isa(Obj.NUM)) {
			double d = Casting.asNumber(o).toDouble();
			for (int i = 0; i < _list.length; i++) {
				if (d == _list[i]) {
					return i;
				}
			}
		}
		return -(_list.length + 1);
	}

	@Override
	public NumberList findAll(Obj o) {
		// TODO: DoubleList implementation
		return toNumberItemList().findAll(o);
	}

	@Override
	public int findBack(Obj o) {
		// TODO: DoubleList implementation
		return toNumberItemList().findBack(o);
	}

	@Override
	public int count(Obj o) {
		if (o.isa(Obj.NUMBER)) {
			return ListAlgorithms.count(_list, asNumber(o).toDouble());
		} else {
			return 0;
		}
	}
	
	@Override
	public void sort() {
		Arrays.sort(_list);
	}
	
	@Override
	public void set(int i, Obj o) {
		_list[i] = ((Num)o).toDouble();
	}
	
	@Override
	public ArrayList<Obj> getObjAL() {
		ArrayList<Obj> l = new ArrayList<Obj>(_list.length);
		for (int i = 0; i < _list.length; i++) {
			l.add(new Num(_list[i]));
		}
		return l;
	}
	
	@Override
	public DoubleList unique() {
		// TODO: DoubleList implementation
		return new DoubleList(toNumberItemList().unique().toNumberList().todoubleArray());
	}
	
	
	@Override
	public DoubleList toNumberList() {
		return this;
	}


	@Override
	public void addItem(Obj o) {
		double[] list = Arrays.copyOf(_list, _list.length + 1);
		list[list.length - 1] = ((Num)o).toDouble();
		_list = list;
	}
	
	@Override
	public void addItem(int i, Obj o) {
		double[] list = Arrays.copyOf(_list, _list.length + 1);
		// Move everything after the index over one
		for (int j = list.length-1; j > i; j--) {
			list[j] = list[j-1];
		}
		list[i] = ((Num)o).toDouble();
		_list = list;
	}

	@Override
	public void addAll(ListImpl l) {
		double[] other = ((DoubleList)l)._list;
		final int len = _list.length;
		final int o_len = other.length;

		double[] c = new double[len + o_len];
		System.arraycopy(_list, 0, c, 0, len);
		System.arraycopy(other, 0, c, len, o_len);
		_list = c;
	}
	
	@Override
	public DoubleList copy() {
		double[] out = new double[_list.length];
		for (int i = 0; i < _list.length; i++) out[i] = _list[i];
		return new DoubleList(out);
	}
	
	@Override
	public boolean canInsert(Obj o) {
		return o.isa(Obj.NUM);
	}

	@Override
	public DoubleList similarEmpty() {
		return new DoubleList(new double[0]);
	}

	@Override
	public List sameShapeNull() {
		return new List(new DoubleList(0, length()));
	}

	@Override
	protected ListImpl flatten() {
		return copy();
	}
	
	@Override
	public List split(Obj o) {
		// TODO: DoubleList implementation
		return toNumberItemList().split(o);
	}
	
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public DoubleList deepcopy() {
		return copy();
	}

	@Override
	public boolean bool() {
		return _list.length != 0;
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("d");
		return ListAlgorithms.reprCompact(stream, _list);
	}

	@Override
	public String str() {
		return toNumberItemList().str();
	}

	@Override
	public boolean equiv(ListImpl list) {
		// Must have the same length
		if (list.length() == this.length()) {
			// Every corresponding item must be equivalent
			for (int i = 0; i < this.length(); i++) {
				if (!list.get(i).equiv(new Num(_list[i]))) {
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
		return type == Obj.LIST || type == Obj.NUMBERLIST || type == Obj.DOUBLELIST;
	}

	@Override
	public byte type() {
		return Obj.DOUBLELIST;
	}

	//////////////////////
	// HELPER FUNCTIONS //
	//////////////////////
	
	/** Primes up to n **/
	public static DoubleList primes(int n) {
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
				primeList.add(Num.fromInt(i));
			}
		}

		double[] out = new double[primeList.size()];
		for (int i = 0; i < out.length; i++) out[i] = primeList.get(i).toDouble();
		return new DoubleList(out);
	}




	private void boundsCheck(NumberList a, NumberList b) {
		if (a.length() != b.length())
			throw new ValueError("List length mismatch\n"
					+ "  " + a.str() + "\n  " + b.str());
	}
	
	@Override
	public NumberList add(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = _list[i] + NS._list[i];
			return new DoubleList(out);
		} else {
			return toNumberItemList().add(ns);
		}
	}

	@Override
	public NumberList sub(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = _list[i] - NS._list[i];
			return new DoubleList(out);
		} else {
			return toNumberItemList().sub(ns);
		}
	}

	@Override
	public NumberList subFrom(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = NS._list[i] - _list[i];
			return new DoubleList(out);
		} else {
			return toNumberItemList().subFrom(ns);
		}
	}


	@Override
	public NumberList div(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = _list[i] / NS._list[i];
			return new DoubleList(out);
		} else {
			return toNumberItemList().div(ns);
		}
	}

	@Override
	public NumberList divFrom(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = NS._list[i] / _list[i];
			return new DoubleList(out);
		} else {
			return toNumberItemList().divFrom(ns);
		}
	}

	@Override
	public NumberList mul(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = _list[i] * NS._list[i];
			return new DoubleList(out);
		} else {
			return toNumberItemList().mul(ns);
		}
	}

	@Override
	public NumberList mod(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = _list[i] % NS._list[i];
			return new DoubleList(out);
		} else {
			return toNumberItemList().mod(ns);
		}
	}

	@Override
	public NumberList modFrom(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = NS._list[i] % _list[i];
			return new DoubleList(out);
		} else {
			return toNumberItemList().modFrom(ns);
		}
	}

	@Override
	public NumberList idiv(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = Math.floor(_list[i] / NS._list[i]);
			return new DoubleList(out);
		} else {
			return toNumberItemList().idiv(ns);
		}
	}

	@Override
	public NumberList idivFrom(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = Math.floor(NS._list[i] / _list[i]);
			return new DoubleList(out);
		} else {
			return toNumberItemList().idivFrom(ns);
		}
	}

	@Override
	public NumberList pow(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = Math.pow(_list[i], NS._list[i]);
			return new DoubleList(out);
		} else {
			return toNumberItemList().pow(ns);
		}
	}

	@Override
	public NumberList powFrom(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = Math.pow(NS._list[i], _list[i]);
			return new DoubleList(out);
		} else {
			return toNumberItemList().powFrom(ns);
		}
	}

	@Override
	public NumberList band(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = ((int)_list[i]) & ((int)NS._list[i]);
			return new DoubleList(out);
		} else {
			return toNumberItemList().band(ns).promote();
		}
	}

	@Override
	public NumberList bandFrom(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = ((int)NS._list[i]) & ((int)_list[i]);
			return new DoubleList(out);
		} else {
			return toNumberItemList().bandFrom(ns).promote();
		}
	}

	@Override
	public NumberList bor(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = ((int)_list[i]) | ((int)NS._list[i]);
			return new DoubleList(out);
		} else {
			return toNumberItemList().bor(ns).promote();
		}
	}

	@Override
	public NumberList borFrom(NumberList ns) {
		boundsCheck(this, ns);
		final int len = _list.length;
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			double[] out = new double[len];
			for (int i = 0; i < len; i++) out[i] = ((int)NS._list[i]) | ((int)_list[i]);
			return new DoubleList(out);
		} else {
			return toNumberItemList().borFrom(ns).promote();
		}
	}

	
	
	@Override
	public NumberList lt(Number n) {
		final int len = _list.length;
		double[] out = new double[len];
		if (n.isa(Obj.NUM)) {
			final double N = n.toDouble();
			for (int i = 0; i < len; i++) out[i] = _list[i] < N ? 1 : 0;
		} else {
			for (int i = 0; i < len; i++) out[i] = new Num(_list[i]).compareTo(n) < 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList lt(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.length;
		double[] out = new double[len];
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			for (int i = 0; i < len; i++) out[i] = _list[i] < NS._list[i] ? 1 : 0;
		} else  {
			for (int i = 0; i < len; i++) out[i] = new Num(_list[i]).compareTo(ns.get(i)) < 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList leq(Number n) {
		final int len = _list.length;
		double[] out = new double[len];
		if (n.isa(Obj.NUM)) {
			final double N = n.toDouble();
			for (int i = 0; i < len; i++) out[i] = _list[i] <= N ? 1 : 0;
		} else {
			for (int i = 0; i < len; i++) out[i] = new Num(_list[i]).compareTo(n) <= 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList leq(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.length;
		double[] out = new double[len];
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			for (int i = 0; i < len; i++) out[i] = _list[i] <= NS._list[i] ? 1 : 0;
		} else  {
			for (int i = 0; i < len; i++) out[i] = new Num(_list[i]).compareTo(ns.get(i)) <= 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList gt(Number n) {
		final int len = _list.length;
		double[] out = new double[len];
		if (n.isa(Obj.NUM)) {
			final double N = n.toDouble();
			for (int i = 0; i < len; i++) out[i] = _list[i] > N ? 1 : 0;
		} else {
			for (int i = 0; i < len; i++) out[i] = new Num(_list[i]).compareTo(n) > 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList gt(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.length;
		double[] out = new double[len];
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			for (int i = 0; i < len; i++) out[i] = _list[i] > NS._list[i] ? 1 : 0;
		} else  {
			for (int i = 0; i < len; i++) out[i] = new Num(_list[i]).compareTo(ns.get(i)) > 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList geq(Number n) {
		final int len = _list.length;
		double[] out = new double[len];
		if (n.isa(Obj.NUM)) {
			final double N = n.toDouble();
			for (int i = 0; i < len; i++) out[i] = _list[i] >= N ? 1 : 0;
		} else {
			for (int i = 0; i < len; i++) out[i] = new Num(_list[i]).compareTo(n) >= 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList geq(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.length;
		double[] out = new double[len];
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			for (int i = 0; i < len; i++) out[i] = _list[i] >= NS._list[i] ? 1 : 0;
		} else  {
			for (int i = 0; i < len; i++) out[i] = new Num(_list[i]).compareTo(ns.get(i)) >= 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList eq(Number n) {
		final int len = _list.length;
		double[] out = new double[len];
		if (n.isa(Obj.NUM)) {
			final double N = n.toDouble();
			for (int i = 0; i < len; i++) out[i] = _list[i] == N ? 1 : 0;
		} else {
			for (int i = 0; i < len; i++) out[i] = new Num(_list[i]).compareTo(n) == 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	@Override
	public NumberList eq(NumberList ns) {
		boundsCheck(this, ns);
		int len = _list.length;
		double[] out = new double[len];
		if (ns instanceof DoubleList) {
			final DoubleList NS = (DoubleList)ns;
			for (int i = 0; i < len; i++) out[i] = _list[i] == NS._list[i] ? 1 : 0;
		} else  {
			for (int i = 0; i < len; i++) out[i] = new Num(_list[i]).compareTo(ns.get(i)) == 0 ? 1 : 0;
		}
		return new DoubleList(out);
	}

	private NumberItemList toNumberItemList() {
		ArrayList<Number> out = new ArrayList<Number>();
		for (double d : _list) out.add(new Num(d));
		return new NumberItemList(out, _list.length);
	}

	@Override
	public DoubleList promote() {
		return this;
	}

	public static List transpose2d(ArrayList<DoubleList> lists) {
		final int in_rows = lists.size();
		final int in_cols = lists.get(0).length();
		double[][] trans = new double[in_cols][in_rows];
		ArrayList<Obj> out = new ArrayList<Obj>(in_cols);
		for (int i = 0; i < in_cols; i++) {
			for (int j = 0; j < in_rows; j++) {
				trans[i][j] = lists.get(j)._list[i];
			}
			out.add(new List(new DoubleList(trans[i])));
		}
		return new List(out);
	}
}
