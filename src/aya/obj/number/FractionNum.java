package aya.obj.number;

import java.math.BigInteger;

import org.apfloat.Apfloat;

import com.github.kiprobinson.bigfraction.BigFraction;

import aya.ReprStream;
import aya.obj.Obj;

/** Represented by two long ints */
public class FractionNum extends Number {
	
	public static final FractionNum ONE = new FractionNum(1L, 1L);
	public static final FractionNum ZERO = new FractionNum(0L, 1L);
	public static final FractionNum NEG_ONE = new FractionNum(-1L, 1L);
	
	// For faster math, switch to LongFraction
	BigFraction _val;
	
	/////////////////
	// CONSTUCTORS //
	/////////////////
	
	public FractionNum(long num, long den) {
		_val = new BigFraction(num, den);
	}
	
	public FractionNum(int num, int den) {
		_val = new BigFraction(num, den);
	}
	
	/** Create a new rational num by approximating the value of the double */
	public FractionNum(double number) {
		_val = new BigFraction(number);
	}
	
	private FractionNum(BigFraction value) {
		_val = value;
	}

	public FractionNum(BigInteger n, BigInteger d) {
		_val = new BigFraction(n, d);
	}
	
	/////////////////
	// CONVERSIONS //
	/////////////////
	

	@Override
	public int toInt() {
		return _val.intValue();
	}

	@Override
	public long toLong() {
		return _val.longValue();
	}

	@Override
	public float toFloat() {
		return _val.floatValue();
	}

	@Override
	public double toDouble() {
		return _val.doubleValue();
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
	public Number add(Number other) { return this.add((FractionNum)other); }
	FractionNum add(FractionNum n) { return new FractionNum(_val.add(n._val)); }

	@Override
	public Number sub(Number other) { return this.sub((FractionNum)other); }
	FractionNum sub(FractionNum n) { return new FractionNum(_val.subtract(n._val)); }

	@Override
	public Number mul(Number other) { return this.mul((FractionNum)other); }
	FractionNum mul(FractionNum n) { return new FractionNum(_val.multiply(n._val)); }

	@Override
	public Number div(Number other) { return this.div((FractionNum)other); }
	FractionNum div(FractionNum n) { return new FractionNum(_val.divide(n._val)); }

	@Override
	public Number idiv(Number other) { return this.idiv((FractionNum)other); }
	FractionNum idiv(FractionNum n) { return new FractionNum(_val.divide(n._val).intValue()); }

	@Override
	public Number mod(Number other) { return this.mod((FractionNum)other); }
	FractionNum mod(FractionNum n) { return new FractionNum(_val.longValue() % n._val.longValue()); }

	@Override
	public Number pow(Number other) { return this.pow((FractionNum)other); }
	private static BigFraction EPS = new BigFraction(1e-5);
	FractionNum pow(FractionNum n) { return new FractionNum(_val.pow(n._val, EPS)); }

	
	
	/////////////////////
	// MATH OPERATIONS //
	/////////////////////
	
	@Override
	public Number negate() {
		return new FractionNum(_val.negate());
	}
	
	@Override
	public Number inc() {
		return this.add(ONE);
	}

	@Override
	public Number dec() {
		return this.sub(ONE);
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
		return new FractionNum(_val.abs());
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
		return new FractionNum(_val.intValue() + 1);
	}

	@Override
	public Number floor() {
		return new FractionNum(_val.intValue());
	}

	@Override
	public boolean isPrime() {
		return _val.getDenominator().intValue() == 1 && NumberMath.isPrime(_val.getNumerator().longValue());
	}
	


		
	///////////////
	// CONSTANTS //
	///////////////
	
	@Override
	public FractionNum one() {
		return ONE;
	}
	
	@Override
	public FractionNum zero() {
		return ZERO;
	}
	
	@Override
	public FractionNum negOne() {
		return NEG_ONE;
	}
		
		
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	
	@Override
	public boolean bool() {
		return _val.getNumerator().longValue() == 0;
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(str());
		return stream;
	}

	@Override
	public String str() {
		return ":" + _val.getNumerator() + "r" + _val.getDenominator();
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

	@Override
	protected Number convert(Number to_promote) {
		return new FractionNum(to_promote.toDouble());
	}

	@Override
	protected int numType() {
		return Number.TYPE_FRACTION;
	}
	
}
