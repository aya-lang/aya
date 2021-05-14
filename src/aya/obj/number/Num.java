package aya.obj.number;

import java.math.BigDecimal;

import aya.obj.Obj;
import aya.util.MathUtils;
import aya.util.StringUtils;

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
		} else if (i == -1) {
			return NEG_ONE;
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
	public BigDecimal toBigDecimal() {
		return new BigDecimal(_val);
	}
	
	
	
	
	///////////////////////
	// BINARY OPERATIONS //
	///////////////////////
	
	@Override
	public Number add(Number other) { return this.add((Num)other); }
	public Num add(Num other) { return new Num(_val + other._val); }

	@Override
	public Number sub(Number other) { return this.sub((Num)other); }
	public Num sub(Num other) { return new Num(_val - other._val); }

	@Override
	public Number mul(Number other) { return this.mul((Num)other); }
	public Num mul(Num other) { return new Num(_val * other._val); }

	@Override
	public Number div(Number other) { return this.div((Num)other); }
	public Num div(Num other) { return new Num(_val / other._val); }

	@Override
	public Number idiv(Number other) { return this.idiv((Num)other); }
	public Num idiv(Num other) { return new Num(Math.floor(_val / other._val)); }
	
	@Override
	public Number mod(Number other) { return this.mod((Num)other); }
	public Num mod(Num other) { return new Num(_val % other._val); }

	@Override
	public Number pow(Number other) { return this.pow((Num)other); }
	public Num pow(Num other) { return new Num(Math.pow(_val, other._val)); }
	
	@Override
	public Number gcd(Number other) { return this.gcd((Num)other); }
	public Num gcd(Num other) { return new Num(MathUtils.gcd((long)_val, (long)other._val)); }
	
	@Override
	public Number lcm(Number other) { return this.lcm((Num)other); }
	public Num lcm(Num other) { return new Num(MathUtils.lcm((long)_val, (long)other._val)); }

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
		return Num.fromInt(MathUtils.signnum(_val));
	}

	@Override
	public Number factorial() {
		return new Num(MathUtils.factorial((long)_val));
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
		return MathUtils.isPrime((long)_val);
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
	public boolean bool() {
		return _val != 0.0;
	}


	@Override
	public String str() {
		return StringUtils.doubleToString(_val);
	}

	@Override
	public boolean equiv(Obj o) {
		return o instanceof Number && ((Number)o).compareTo(this) == 0;
	}
	

	@Override
	public boolean isa(byte type) {
		return type == Obj.NUMBER || type == Obj.NUM;
	}

	@Override
	public int compareTo(Number n) {
		// Add 0.0 to both numbers to prevent Double.compare(-0, 0) == -1
		return Double.compare(_val + 0.0, n.toDouble() + 0.0); 
	}

	@Override
	protected Num convert(Number to_promote) {
		return new Num(to_promote.toDouble());
	}

	@Override
	protected int numType() {
		return Number.TYPE_NUM;
	}


}
