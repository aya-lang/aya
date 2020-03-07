package aya.obj.number;

import java.text.DecimalFormat;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import aya.obj.Obj;

/** Contains a double */
public class Num extends Number {

	public static final Num ZERO = new Num(0.0);
	public static final Num ONE = new Num(1.0);
	public static final Num NEG_ONE = new Num(-1.0);
	public static final Num MIN_VALUE = new Num(Double.MIN_VALUE);
	public static final Num MAX_VALUE = new Num(Double.MAX_VALUE);
	public static final Num PI = new Num(Math.PI);
	public static final Num E = new Num(Math.E);
	public static final Num DOUBLE_MAX = new Num(Double.MAX_VALUE);
	public static final Num DOUBLE_MIN = new Num(Double.MIN_VALUE);
	public static final Num DOUBLE_NAN = new Num(Double.NaN);
	public static final Num DOUBLE_INF = new Num(Double.POSITIVE_INFINITY);
	public static final Num DOUBLE_NINF = new Num(Double.NEGATIVE_INFINITY);
	public static final Num INT_MAX = new Num(Integer.MAX_VALUE);
	public static final Num INT_MIN = new Num(Integer.MIN_VALUE);
	public static final Num[] BYTES = new Num[256];
	static {
		BYTES[0] = ZERO;
		BYTES[1] = ONE;
		for (int i = 2; i < 256; i++) BYTES[i] = new Num(i);
	}
	
	
	double _val;
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public Num(double d) {
		_val = d;
	}
	
	public Num(String str) {
		try {
			_val = Double.parseDouble(str);
		} catch (NumberFormatException n) {
			_val = Double.NaN;
		}
	}	

	//////////////
	// CREATION //
	//////////////
	
	public static Num fromByte(byte b) {
		return BYTES[b & 0xff];
	}
	
	public static Num fromInt(int i) {
		if (i >= 0 && i <= 255) {
			return BYTES[i];
		} else {
			return new Num(i);
		}
	}
	
	public static Num fromBool(boolean b) {
		return b ? ONE : ZERO;
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
		byte type = other.type();
		switch (type) {
		case Obj.NUM: 
			return new Num(_val + other.toDouble());
		case Obj.BIGNUM:
			return new BigNum(other.toApfloat().add(new Apfloat(_val)));
		case Obj.RATIONAL_NUMBER:
			return ((RationalNum)other).r_add(new RationalNum(_val));
		default:
			return null;
		}
	}

	@Override
	public Number sub(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.NUM: 
			return new Num(_val - other.toDouble());
		case Obj.BIGNUM:
			return new BigNum(other.toApfloat().subtract(new Apfloat(_val)));
		case Obj.RATIONAL_NUMBER:
			return new RationalNum(_val).r_sub((RationalNum)other);
		default:
			return null;
		}
	}

	@Override
	public Number mul(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.NUM: 
			return new Num(_val * other.toDouble());
		case Obj.BIGNUM:
			return new BigNum(other.toApfloat().multiply(new Apfloat(_val)));
		case Obj.RATIONAL_NUMBER:
			return ((RationalNum)other).r_mul(new RationalNum(_val));
		default:
			return null;
		}
	}

	@Override
	public Number div(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.NUM: 
			return new Num(_val / other.toDouble());
		case Obj.BIGNUM:
			return new BigNum(other.toApfloat().divide(new Apfloat(_val)));
		case Obj.RATIONAL_NUMBER:
			return new RationalNum(_val).r_div((RationalNum)other);
		default:
			return null;
		}
	}

	@Override
	public Number idiv(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.NUM: 
			return new Num(Math.floor(_val / other.toDouble()));
		case Obj.BIGNUM:
			return new BigNum(other.toApfloat().divide(new Apfloat(_val)).floor());
		case Obj.RATIONAL_NUMBER:
			return new RationalNum((long)Math.floor(_val / other.toDouble()), 1L);
		default:
			return null;
		}
	}

	@Override
	public Number mod(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.NUM: 
			return new Num(_val % other.toDouble());
		case Obj.BIGNUM:
			return new BigNum(other.toApfloat().mod(new Apfloat(_val)));
		case Obj.RATIONAL_NUMBER:
			return new RationalNum(_val % other.toDouble());
		default:
			return null;
		}
	}

	@Override
	public Number pow(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.NUM: 
			return new Num(Math.pow(_val, other.toDouble()));
		case Obj.BIGNUM:
			return new BigNum(ApfloatMath.pow(new Apfloat(_val), other.toApfloat()));
		case Obj.RATIONAL_NUMBER:
			return new RationalNum(Math.pow(_val, other.toDouble()));
		default:
			return null;
		}
	}	
	
	/////////////////////
	// MATH OPERATIONS //
	/////////////////////
	
	@Override
	public Number negate() {
		return new Num(-_val);
	}
	
	@Override
	public Number inc() {
		return new Num(_val + 1.0);
	}

	@Override
	public Number dec() {
		return new Num(_val - 1.0);
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
	public Num exp() {
		return new Num(Math.exp(_val));
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
	
	//////////////////
	// TRANSFORMERS //
	//////////////////
	
	
	@Override
	public Num subEq(Number v) {
		_val -= v.toDouble();
		return this;
	}


	@Override
	public Num addEq(Number v) {
		_val += v.toDouble();
		return this;
	}

	
	
	
	///////////////
	// CONSTANTS //
	///////////////
	
	@Override
	public Num one() {
		return ONE;
	}
	
	@Override
	public Num zero() {
		return ZERO;
	}
	
	@Override
	public Num negOne() {
		return NEG_ONE;
	}
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////

	@Override
	public Num deepcopy() {
		return new Num(_val);
	}

	@Override
	public boolean bool() {
		return _val != 0.0;
	}

	private static final DecimalFormat _df = new DecimalFormat("#");
    static {_df.setMaximumFractionDigits(8);}
    
	@Override
	public String repr() {
		if (_val % 1 == 0) {
			return String.format("%d",(long)_val);
		} else {
			return _df.format(_val);
		}
	}

	@Override
	public String str() {
		if (_val % 1 == 0) {
			return String.format("%d",(long)_val);
		} else {
			return _df.format(_val);
		}
	}

	@Override
	public boolean equiv(Obj o) {
		return o instanceof Number && ((Number)o).compareTo(this) == 0.0;
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
		return Double.compare(_val, n.toDouble()); 
	}





}
