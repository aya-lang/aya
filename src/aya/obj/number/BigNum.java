package aya.obj.number;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApintMath;

import aya.ReprStream;
import aya.obj.Obj;

/** Contains an ApFloat */
public class BigNum extends Number {
	
	private static Apfloat ZERO_AP = new Apfloat(0);
	
	public static BigNum ZERO = new BigNum(0);
	public static BigNum ONE = new BigNum(1);
	public static BigNum NEG_ONE = new BigNum(1);
	
	private Apfloat _val;
	
	//////////////////
	// CONSTURCTORS //
	//////////////////
	
	public BigNum(int n) {
		//Infinite precision
		_val = new Apfloat(n);
	}
	
	public BigNum(double d) {
		//16 digit precision
		_val = new Apfloat(d);
	}
	
	public BigNum(String str) {
		//precision dependent on string
		try {
			_val = new Apfloat(str);
		} catch (NumberFormatException e) {
			_val = new Apfloat(0.0);
		}
	}
	
	public BigNum(Apfloat a) {
		_val = a;
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
	public BigNum idiv(BigNum other) { return new BigNum(_val.divide(other._val).floor()); }
	
	@Override
	public Number mod(Number other) { return this.mod((BigNum)other); }
	public BigNum mod(BigNum other) { return new BigNum(_val.mod(other._val)); }

	@Override
	public Number pow(Number other) { return this.pow((BigNum)other); }
	public BigNum pow(BigNum other) { return new BigNum(ApfloatMath.pow(_val, other._val)); }

	

	/////////////////////
	// MATH OPERATIONS //
	/////////////////////
	
	@Override
	public Number negate() {
		return new BigNum(_val.negate());
	}
	
	@Override
	public Number inc() {
		return new BigNum(_val.add(Apfloat.ONE));
	}

	@Override
	public Number dec() {
		return new BigNum(_val.subtract(Apfloat.ONE));

	}

	@Override
	public Number signnum() {
		return new BigNum(_val.signum());
	}

	@Override
	public Number factorial() {
		return new BigNum(ApintMath.factorial(_val.longValue()));
	}

	@Override
	public Number abs() {
		return new BigNum(ApfloatMath.abs(_val));
	}
	
	@Override
	public Number exp() {
		return new BigNum(ApfloatMath.exp(_val));
	}

	@Override
	public Number sin() {
		return new BigNum(ApfloatMath.sin(_val));
	}

	@Override
	public Number cos() {
		return new BigNum(ApfloatMath.cos(_val));
	}

	@Override
	public Number tan() {
		return new BigNum(ApfloatMath.tan(_val));
	}

	@Override
	public Number asin() {
		return new BigNum(ApfloatMath.asin(_val));
	}

	@Override
	public Number acos() {
		return new BigNum(ApfloatMath.acos(_val));
	}

	@Override
	public Number atan() {
		return new BigNum(ApfloatMath.atan(_val));
	}

	@Override
	public Number log() {
		return new BigNum(ApfloatMath.log(_val, new Apfloat(10)));
	}

	@Override
	public Number ln() {
		return new BigNum(ApfloatMath.log(_val));
	}

	@Override
	public Number sqrt() {
		return new BigNum(ApfloatMath.sqrt(_val));
	}

	@Override
	public Number ceil() {
		return new BigNum(_val.ceil());
	}

	@Override
	public Number floor() {
		return new BigNum(_val.floor());
	}

	@Override
	public boolean isPrime() {
		return NumberMath.isPrime(_val.longValue());
	}

	@Override
	public boolean bool() {
		return !(_val.compareTo(ZERO_AP) == 0);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(trimZeros(_val.toString(true)));
		return stream;
	}

	@Override
	public String str() {
		return trimZeros(_val.toString(true));
	}

	@Override
	public boolean equiv(Obj o) {
		return o instanceof Number && ((Number)o).toApfloat().compareTo(_val) == 0;
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
		return _val.compareTo(n.toApfloat()); 
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
		return new BigNum(to_promote.toApfloat());
	}

	@Override
	protected int numType() {
		return Number.TYPE_BIGNUM;
	}

}
