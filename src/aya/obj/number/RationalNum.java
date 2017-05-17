package aya.obj.number;

import org.apfloat.Apfloat;

import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;

/** Represented by two long ints */
public class RationalNum extends Number {
	
	public static final RationalNum ONE = new RationalNum(1L, 1L);
	public static final RationalNum ZERO = new RationalNum(0L, 1L);
	public static final RationalNum NEG_ONE = new RationalNum(-1L, 1L);
	
	private final int LARGEST_RIGHT_OF_DECIMAL = 8;
	private final long SECOND_MULTIPLIER_MAX = (long)Math.pow(10, LARGEST_RIGHT_OF_DECIMAL - 1);
	private final long FIRST_MULTIPLIER_MAX = SECOND_MULTIPLIER_MAX * 10L;
	private final double ERROR = Math.pow(10, -LARGEST_RIGHT_OF_DECIMAL - 1);
	
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
			throw new AyaRuntimeException("RationalNum: Division by zero");
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
			throw new AyaRuntimeException("RationalNum: Division by zero");
		} else {
			int gcd = NumberMath.gcd(num, den);
			_num = num / gcd;
			_den = den / gcd;
		}
	}
	
	/** Create a new rational num by approximating the value of the double */
	public RationalNum(double number) {
		
		// 0/1
		if (number == 0.0) {
			_num = 0L;
			_den = 1L;
		} 
		// NaN
		else if (Double.isNaN(number)) {
			throw new AyaRuntimeException("Cannont convert NaN to rational");
		}
		else if (Math.floor(number) == number) {
			_num = (long)number;
			_den = 1L;
		}
		else {
			//Algorithm from:
			// http://stackoverflow.com/questions/14014158/double-to-fraction-in-java
			
			long sign = 1;
			if(number < 0){
			    number = -number;
			    sign = -1;
			}

			
			long firstMultiplier = 1;
			long secondMultiplier = 1;
			boolean notIntOrIrrational = false;
			long truncatedNumber = (long)number;
			_num = (long)(sign * number * FIRST_MULTIPLIER_MAX);
			_den = FIRST_MULTIPLIER_MAX;
			
			double error = number - truncatedNumber;
			while( (error >= ERROR) && (firstMultiplier <= FIRST_MULTIPLIER_MAX)){
			    secondMultiplier = 1;
			    firstMultiplier *= 10;
			    while( (secondMultiplier <= SECOND_MULTIPLIER_MAX) && (secondMultiplier < firstMultiplier) ){
			        double difference = (number * firstMultiplier) - (number * secondMultiplier);
			        truncatedNumber = (long)difference;
			        error = difference - truncatedNumber;
			        if(error < ERROR){
			            notIntOrIrrational = true;
			            break;
			        }
			        secondMultiplier *= 10;
			    }
			}

			if(notIntOrIrrational){
				_num = sign * truncatedNumber;
				_den = firstMultiplier - secondMultiplier;
			}
		}
		
		//this.simplify();
	}

	public void simplify() {
		if (_num == _den) {
			_num = 1L;
			_den = 1L;
		} else {
			long gcd = NumberMath.gcd(_num, _den);
			_num = _num / gcd;
			_den = _den / gcd;
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
	public RationalNum add(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.RATIONAL_NUMBER:
			return this.r_add((RationalNum)other);
		default:
			return this.r_add(new RationalNum(other.toDouble()));
		}
	}
	
	

	@Override
	public RationalNum sub(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.RATIONAL_NUMBER:
			return this.r_sub((RationalNum)other);
		default:
			return this.r_sub(new RationalNum(other.toDouble()));
		}
	}

	@Override
	public RationalNum mul(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.RATIONAL_NUMBER:
			return this.r_mul((RationalNum)other);
		default:
			return this.r_mul(new RationalNum(other.toDouble()));
		}
	}

	@Override
	public RationalNum div(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.RATIONAL_NUMBER:
			return this.r_div((RationalNum)other);
		default:
			return this.r_div(new RationalNum(other.toDouble()));
		}
	}

	@Override
	public RationalNum idiv(Number other) {
		return new RationalNum(this.toLong() / other.toLong(), 1L);
	}

	@Override
	public RationalNum mod(Number other) {
		return new RationalNum(this.toDouble() % other.toDouble());
	}

	@Override
	public RationalNum pow(Number other) {
		double exp = other.toDouble();
		if (exp > 0) {
			return new RationalNum((long)Math.pow(_num, exp), (long)Math.pow(_den, exp));
		}
		else {
			return new RationalNum(Math.pow(this.toDouble(), other.toDouble()));
		}
	}
	
	@Override
	public RationalNum band(Number other) {
		return new RationalNum(this.toLong() & other.toLong());
	}
	
	@Override
	public RationalNum bor(Number other) {
		return new RationalNum(this.toLong() | other.toLong());
	}
	
	
	
	/////////////////////
	// MATH OPERATIONS //
	/////////////////////
	
	@Override
	public Number negate() {
		return new RationalNum(-_num, _den);
	}
	
	@Override
	public Number inc() {
		return this.r_add(ONE);
	}

	@Override
	public Number dec() {
		return this.r_sub(ONE);
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
	public Number exp() {
		return new Num(Math.exp(this.toDouble()));
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
	
	//////////////////
	// TRANSFORMERS //
	//////////////////
	
	@Override
	public RationalNum subEq(Number v) {
		RationalNum n = this.sub(v);
		this._den = n._den;
		this._num = n._num;
		return this;
	}

	@Override
	public RationalNum addEq(Number v) {
		RationalNum n = this.add(v);
		this._den = n._den;
		this._num = n._num;
		return this;
	}



		
	///////////////
	// CONSTANTS //
	///////////////
	
	@Override
	public RationalNum one() {
		return ONE;
	}
	
	@Override
	public RationalNum zero() {
		return ZERO;
	}
	
	@Override
	public RationalNum negOne() {
		return NEG_ONE;
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
		return o instanceof Number && ((Number)o).compareTo(this) == 0;
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
		return Double.compare(this.toDouble(), n.toDouble()); 
	}
	
	//////////////////////////
	// RATIONAL NUMBER MATH //
	//////////////////////////
	
	/** Add two rational numbers */
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
	
	/** Subtract two rational numbers */
	RationalNum r_sub(RationalNum n) {
		if(_den == n._den) {
            return new RationalNum(_num - n._num, _den);
        } else {
          long den = _den * n._den;
          long num = _num * n._num;
          num -= n._num * _den;
          return new RationalNum(num, den);
        }
	}
	
	/** Multiply two rational  numbers */
	RationalNum r_mul(RationalNum n) {
		return new RationalNum(_num * n._num, _den * n._den);
	}
	
	/** Divide two rational numbers */
	RationalNum r_div(RationalNum n) {
		return new RationalNum(_num * n._den, _den * n._num);
	}


	
}
