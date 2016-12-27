package element.obj.number;

import org.apfloat.Apfloat;

import element.exceptions.ElementRuntimeException;
import element.obj.Obj;

public class RationalNum extends Number {
	
	long _num;
	long _den;
	
	/////////////////
	// CONSTUCTORS //
	/////////////////
	
	public RationalNum(long num, long den) {
		if (num == 0L)	{
			_num = 0L;
			_den = 1L;
		} else if (den == 0L) {
			throw new ElementRuntimeException("RationalNum: Division by zero");
		} else {
			long gcd = NumberMath.gcd(num, den);
			_num = num / gcd;
			_den = den / gcd;
		}
	}
	
	public RationalNum(int num, int den) {
		if (num == 0)	{
			_num = 0;
			_den = 1;
		} else if (den == 0) {
			throw new ElementRuntimeException("RationalNum: Division by zero");
		} else {
			int gcd = NumberMath.gcd(num, den);
			_num = num / gcd;
			_den = den / gcd;
		}
	}
	
	//Algorithm from:
	// http://stackoverflow.com/questions/13222664/convert-floating-point-number-into-a-rational-number-in-java
	public RationalNum(double val) {
		
		// 0/1
		if (val == 0.0) {
			_num = 0L;
			_den = 1L;
		}
		// NaN
		else if (Double.isNaN(val)) {
			throw new ElementRuntimeException("Cannont convert NaN to rational");
		}

		else {
			//Algorithm from:
			// http://stackoverflow.com/questions/13222664/convert-floating-point-number-into-a-rational-number-in-java

			long bits = Double.doubleToLongBits(val);
	
			long sign = bits >>> 63;
			long exponent = ((bits >>> 52) ^ (sign << 11)) - 1023;
			long fraction = bits << 12; // bits are "reversed" but that's not a problem
	
			_num = 1L;
			_den = 1L;
	
			for (int i = 63; i >= 12; i--) {
			    _num = _num * 2 + ((fraction >>> i) & 1);
			    _den *= 2;
			}
	
			if (exponent > 0)
			    _num *= 1 << exponent;
			else
			    _den *= 1 << -exponent;
	
			if (sign == 1)
			    _num *= -1;
		}

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
		byte type = other.type();
		switch (type) {
		case Obj.RATIONAL_NUMBER:
			return this.r_add((RationalNum)other);
		default:
			return this.r_add(new RationalNum(other.toDouble()));
		}
	}
	
	

	@Override
	public Number sub(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.RATIONAL_NUMBER:
			return this.r_sub((RationalNum)other);
		default:
			return this.r_sub(new RationalNum(other.toDouble()));
		}
	}

	@Override
	public Number mul(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.RATIONAL_NUMBER:
			return this.r_mul((RationalNum)other);
		default:
			return this.r_mul(new RationalNum(other.toDouble()));
		}
	}

	@Override
	public Number div(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.RATIONAL_NUMBER:
			return this.r_div((RationalNum)other);
		default:
			return this.r_div(new RationalNum(other.toDouble()));
		}
	}

	@Override
	public Number idiv(Number other) {
		return new RationalNum(this.toLong() / other.toLong(), 1L);
	}

	@Override
	public Number mod(Number other) {
		return new RationalNum(this.toDouble() % other.toDouble());
	}

	@Override
	public Number pow(Number other) {
		return new RationalNum(Math.pow(this.toDouble(), other.toDouble()));
	}
	
	
	
	
	
	/////////////////////
	// MATH OPERATIONS //
	/////////////////////
	
	@Override
	public Number negate() {
		return new RationalNum(-_num, _den);
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
		return new RationalNum(Math.abs(_num), Math.abs(_den));
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
		return new RationalNum(1L+(_num/_den), 1L);
	}

	@Override
	public Number floor() {
		return new RationalNum(_num/_den, 1L);
	}

	@Override
	public boolean isPrime() {
		return _den == 1 && NumberMath.isPrime(_num);
	}

	
	
	
	
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	
	@Override
	public RationalNum deepcopy() {
		return new RationalNum(_num, _den);
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
	
	//////////////////////////
	// RATIONAL NUMBER MATH //
	//////////////////////////
	
	RationalNum r_add(RationalNum n) {
		if(_den == n._den) {
            return new RationalNum(n._num + _num, _den);
        } else {
          long den = _den * n._den;
          long num = _num * n._num;
          num += n._num * _den;
          return new RationalNum(num, den);
        }
	}
	
	RationalNum r_sub(RationalNum n) {
		if(_den == n._den) {
            return new RationalNum(_num - n._num, _den);
        } else {
          long den = _den / n._den;
          long num = _num / n._num;
          num -= n._num * _den;
          return new RationalNum(num, den);
        }
	}
	
	RationalNum r_mul(RationalNum n) {
		return new RationalNum(_num * n._num, _den * n._den);
	}
	
	RationalNum r_div(RationalNum n) {
		return new RationalNum(_num * n._den, _den * n._num);
	}

}
