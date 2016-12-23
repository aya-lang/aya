package obj.number;

import org.apfloat.Apfloat;

import obj.Obj;

public class Num extends Number {

	double _val;
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public Num(int n) {
		_val = (double)n;
	}
	
	public Num(double d) {
		_val = d;
	}
	
//	public Num(BigNum bn) {
//		_val = bn.toDouble();
//	}
	
	public Num(String str) {
		try {
			_val = Double.parseDouble(str);
		} catch (NumberFormatException n) {
			_val = Double.NaN;
		}
	}	
	
	
	/////////////////
	// CONVERSIONS //
	/////////////////
	
	@Override
	public int toInt() {
		return (int)_val;
	}

	@Override
	public long toLong() {
		return (long)_val;
	}

	@Override
	public float toFloat() {
		return (float)_val;
	}

	@Override
	public double toDouble() {
		return _val;
	}

	@Override
	public Apfloat toApfloat() {
		return new Apfloat(_val);
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
		return new Num(-_val);
	}

	@Override
	public Number bnot() {
		return new Num((double)~((int)_val));
	}

	@Override
	public Number signnum() {
		if (_val == 0.0) {
			return new Num(0);
		} else if (_val > 0.0) {
			return new Num(1);
		} else {
			return new Num(-1);
		}
	}

	@Override
	public Number factorial() {
		return new Num(NumberMath.factorial((long)_val));
	}

	@Override
	public Number abs() {
		return new Num(Math.abs(_val));
	}

	@Override
	public Number sin() {
		return new Num(Math.sin(_val));
	}

	@Override
	public Number cos() {
		return new Num(Math.cos(_val));
	}

	@Override
	public Number tan() {
		return new Num(Math.tan(_val));
	}

	@Override
	public Number asin() {
		return new Num(Math.asin(_val));
	}

	@Override
	public Number acos() {
		return new Num(Math.acos(_val));
	}

	@Override
	public Number atan() {
		return new Num(Math.atan(_val));
	}

	@Override
	public Number log() {
		return new Num(Math.log10(_val));
	}

	@Override
	public Number ln() {
		return new Num(Math.log(_val));
	}

	@Override
	public Number sqrt() {
		return new Num(Math.sqrt(_val));
	}

	@Override
	public Number ceil() {
		return new Num(Math.ceil(_val));
	}

	@Override
	public Number floor() {
		return new Num(Math.floor(_val));
	}

	@Override
	public boolean isPrime() {
		return NumberMath.isPrime((long)_val);
	}
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////

	@Override
	public Obj deepcopy() {
		return new Num(_val);
	}

	@Override
	public boolean bool() {
		return _val != 0.0;
	}

	@Override
	public String repr() {
		if (_val % 1 == 0) {
			return String.format("%d",(long)_val);
		} else {
			return String.format("%s", _val);
		}
	}

	@Override
	public String str() {
		if (_val % 1 == 0) {
			return String.format("%d",(long)_val);
		} else {
			return String.format("%s", _val);
		}
	}

	@Override
	public boolean equiv(Obj o) {
		return o instanceof Number && ((Number)o).toDouble() == _val;
	}
	

	@Override
	public boolean isa(byte type) {
		return type == Obj.NUMBER || type == Obj.NUM;
	}

	@Override
	public byte type() {
		return Obj.NUM;
	}

	@Override
	public int compareTo(Number n) {
		//Ascending
		return (int)((_val - n.toDouble())); 
	}

}
