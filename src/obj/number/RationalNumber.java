package obj.number;

import org.apfloat.Apfloat;

import obj.Obj;

public class RationalNumber extends Number {
	
	long _num;
	long _den;
	
	/////////////////
	// CONSTUCTORS //
	/////////////////
	
	public RationalNumber(long num, long den) {
		// TODO: Reduce
		_num = num;
		_den = den;
	}
	
	public RationalNumber(int num, int den) {
		// TODO: Reduce
		_num = num;
		_den = den;
	}
	
	public RationalNumber(double val) {
		// TODO
	}
	
	

	/////////////////
	// CONVERSIONS //
	/////////////////
	
	@Override
	public int toInt() {
		return (int)this.toDouble();
	}

	@Override
	public long toLong() {
		return (long)this.toDouble();
	}

	@Override
	public float toFloat() {
		return (float)this.toDouble();
	}

	@Override
	public double toDouble() {
		return ((double)_num) / ((double)_den);
	}

	@Override
	public Apfloat toApfloat() {
		// Avoid max precision error for now
		return new Apfloat(this.toDouble());
	}
	
	
	
	
	
	
	///////////////////////
	// BINARY OPERATIONS //
	///////////////////////
	
	@Override
	public Number add(Number other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number sub(Number other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number mul(Number other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number div(Number other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number idiv(Number other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number mod(Number other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number pow(Number other) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	/////////////////////
	// MATH OPERATIONS //
	/////////////////////
	
	@Override
	public Number negate() {
		return new RationalNumber(-_num, _den);
	}

	@Override
	public Number bnot() {
		return new Num(~(this.toInt()));
	}

	@Override
	public Number signnum() {
		double val = this.toDouble();
		if (val == 0.0) {
			return new Num(0);
		} else if (val > 0.0) {
			return new Num(1);
		} else {
			return new Num(-1);
		}
	}

	@Override
	public Number factorial() {
		return new BigNum(NumberMath.factorial(this.toLong()));
	}

	@Override
	public Number abs() {
		return new RationalNumber(Math.abs(_num), Math.abs(_den));
	}

	@Override
	public Number sin() {
		return new Num(Math.sin(this.toDouble()));
	}

	@Override
	public Number cos() {
		return new Num(Math.cos(this.toDouble()));
	}

	@Override
	public Number tan() {
		return new Num(Math.tan(this.toDouble()));
	}

	@Override
	public Number asin() {
		return new Num(Math.asin(this.toDouble()));
	}

	@Override
	public Number acos() {
		return new Num(Math.acos(this.toDouble()));
	}

	@Override
	public Number atan() {
		return new Num(Math.atan(this.toDouble()));
	}

	@Override
	public Number log() {
		return new Num(Math.log10(this.toDouble()));
	}

	@Override
	public Number ln() {
		return new Num(Math.log(this.toDouble()));
	}

	@Override
	public Number sqrt() {
		return new Num(Math.sqrt(this.toDouble()));
	}

	@Override
	public Number ceil() {
		// TODO
		return null;
	}

	@Override
	public Number floor() {
		// TODO
		return null;
	}

	@Override
	public boolean isPrime() {
		return _den == 1 && NumberMath.isPrime(_num);
	}

	
	
	
	
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	
	@Override
	public Obj deepcopy() {
		return new RationalNumber(_num, _den);
	}

	@Override
	public boolean bool() {
		return _num == 0;
	}

	@Override
	public String repr() {
		return ":" + _num + "r" + _den;
	}

	@Override
	public String str() {
		return ":" + _num + "r" + _den;
	}

	@Override
	public boolean equiv(Obj o) {
		return o instanceof Number && ((Number)o).toDouble() == this.toDouble();
	}

	@Override
	public boolean isa(byte type) {
		return type == Obj.NUMBER || type == Obj.RATIONAL_NUMBER;
	}

	@Override
	public byte type() {
		return Obj.RATIONAL_NUMBER;
	}
	
	
	
	////////////////
	// COMPARABLE //
	////////////////
	
	@Override
	public int compareTo(Number n) {
		//Ascending
		return (int)((this.toDouble() - n.toDouble())); 
	}

}
