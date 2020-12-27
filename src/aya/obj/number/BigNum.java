package aya.obj.number;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import aya.ReprStream;
import aya.obj.Obj;
import aya.util.MathUtils;
import ch.obermuhlner.math.big.BigDecimalMath;

/** Contains an ApFloat */
public class BigNum extends Number {
	
	public static final MathContext MC = new MathContext(128);
	public static final BigDecimal BD_NEG_ONE = new BigDecimal(-1);
	public static final BigNum ZERO = new BigNum(0);
	public static final BigNum ONE = new BigNum(1);
	public static final BigNum NEG_ONE = new BigNum(-1);
	
	private BigDecimal _val;
	
	//////////////////
	// CONSTURCTORS //
	//////////////////
	
	public BigNum(int n) {
		_val = new BigDecimal(n, MC);
	}
	
	public BigNum(double d) {
		_val = new BigDecimal(d, MC);
	}
	
	public BigNum(String str) {
		_val = new BigDecimal(str);
	}
	
	public BigNum(BigDecimal n) {
		_val = n;
	}

	public BigNum(BigInteger val) {
		_val = new BigDecimal(val);
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
	public BigDecimal toBigDecimal() {
		return _val;
	}
	
	///////////////////////
	// BINARY OPERATIONS //
	///////////////////////

	@Override
	public Number add(Number other) { return this.add((BigNum)other); }
	public BigNum add(BigNum other) { return new BigNum(_val.add(other._val)); }

	@Override
	public Number sub(Number other) { return this.sub((BigNum)other); }
	public BigNum sub(BigNum other) { return new BigNum(_val.subtract(other._val)); }

	@Override
	public Number mul(Number other) { return this.mul((BigNum)other); }
	public BigNum mul(BigNum other) { return new BigNum(_val.multiply(other._val)); }

	@Override
	public Number div(Number other) { return this.div((BigNum)other); }
	public BigNum div(BigNum other) { return new BigNum(_val.divide(other._val)); }

	@Override
	public Number idiv(Number other) { return this.idiv((BigNum)other); }
	public BigNum idiv(BigNum other) { return new BigNum(floor(_val.divide(other._val))); }
	
	@Override
	public Number mod(Number other) { return this.mod((BigNum)other); }
	public BigNum mod(BigNum other) { return new BigNum(_val.toBigInteger().mod(other._val.toBigInteger())); }

	@Override
	public Number pow(Number other) { return this.pow((BigNum)other); }
	public BigNum pow(BigNum other) { return new BigNum(BigDecimalMath.pow(_val, other._val, MC)); }

	@Override
	public Number gcd(Number other) { return this.gcd((BigNum)other); }
	public BigNum gcd(BigNum other) { return new BigNum(MathUtils.gcd(_val, other._val)); }

	@Override
	public Number lcm(Number other) { return this.lcm((BigNum)other); }
	public BigNum lcm(BigNum other) { return new BigNum(MathUtils.lcm(_val, other._val)); }

	

	/////////////////////
	// MATH OPERATIONS //
	/////////////////////
	
	@Override
	public Number negate() {
		return new BigNum(_val.negate());
	}
	
	@Override
	public Number inc() {
		return new BigNum(_val.add(BigDecimal.ONE));
	}

	@Override
	public Number dec() {
		return new BigNum(_val.subtract(BigDecimal.ONE));

	}

	@Override
	public Number signnum() {
		return new BigNum(_val.signum());
	}

	@Override
	public Number factorial() {
		return new BigNum(MathUtils.factorial(_val));
	}

	@Override
	public Number abs() {
		return new BigNum(_val.multiply(_val.signum() >= 0 ? BigDecimal.ONE : BD_NEG_ONE));
	}
	
	@Override
	public Number exp() {
		return new BigNum(BigDecimalMath.exp(_val, MC));
	}

	@Override
	public Number sin() {
		return new BigNum(BigDecimalMath.sin(_val, MC));
	}

	@Override
	public Number cos() {
		return new BigNum(BigDecimalMath.cos(_val, MC));
	}

	@Override
	public Number tan() {
		return new BigNum(BigDecimalMath.tan(_val, MC));
	}

	@Override
	public Number asin() {
		return new BigNum(BigDecimalMath.asin(_val, MC));
	}

	@Override
	public Number acos() {
		return new BigNum(BigDecimalMath.acos(_val, MC));
	}

	@Override
	public Number atan() {
		return new BigNum(BigDecimalMath.atan(_val, MC));
	}

	@Override
	public Number log() {
		return new BigNum(BigDecimalMath.log10(_val, MC));
	}

	@Override
	public Number ln() {
		return new BigNum(BigDecimalMath.log2(_val, MC));
	}

	@Override
	public Number sqrt() {
		return new BigNum(BigDecimalMath.sqrt(_val, MC));
	}

	@Override
	public Number ceil() {
		return new BigNum(ceil(_val));
	}

	@Override
	public Number floor() {
		return new BigNum(floor(_val));
	}

	@Override
	public boolean isPrime() {
		return MathUtils.isPrime(_val.toBigInteger());
	}

	@Override
	public boolean bool() {
		return !(_val.compareTo(BigDecimal.ZERO) == 0);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(trimZeros(_val.toString()));
		return stream;
	}

	@Override
	public String str() {
		return trimZeros(_val.toString());
	}

	@Override
	public boolean equiv(Obj o) {
		return o instanceof Number && ((Number)o).toBigDecimal().compareTo(_val) == 0;
	}
	
	@Override
	public boolean isa(byte type) {
		return type == Obj.NUMBER || type == Obj.BIGNUM;
	}

	@Override
	public byte type() {
		return Obj.BIGNUM;
	}
	
	

	@Override
	public int compareTo(Number n) {
		return _val.compareTo(n.toBigDecimal()); 
	}
	


	
	///////////////
	// CONSTANTS //
	///////////////
	
	@Override
	public BigNum one() {
		return ONE;
	}

	@Override
	public BigNum zero() {
		return ZERO;
	}

	@Override
	public BigNum negOne() {
		return NEG_ONE;
	}

	


	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
	private static BigDecimal ceil(BigDecimal val) {
		return val.setScale(0, RoundingMode.CEILING);
	}
	
	private static BigDecimal floor(BigDecimal val) {
		return val.setScale(0, RoundingMode.FLOOR);
	}
	
	private static String trimZeros(String s) {
		if(!s.contains("."))
			return s;
		
		int dsi = s.length()-1;
		while(s.charAt(dsi) == '0') {
			dsi--;
		}
		if(s.charAt(dsi) == '.') {
			dsi++;
		}
		return s.substring(0, dsi+1);
	}

	@Override
	protected Number convert(Number to_promote) {
		return new BigNum(to_promote.toBigDecimal());
	}

	@Override
	protected int numType() {
		return Number.TYPE_BIGNUM;
	}

}
