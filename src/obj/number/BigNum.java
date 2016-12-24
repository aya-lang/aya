package obj.number;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApintMath;

import obj.Obj;

public class BigNum extends Number {
	
	private static Apfloat ZERO = new Apfloat(0);
	
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
	public Number add(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.BIGNUM:
			return new BigNum(_val.add(other.toApfloat()));
		case Obj.RATIONAL_NUMBER:
			return new RationalNum(_val.add(other.toApfloat()).doubleValue());
		case Obj.NUM:
			return new BigNum(_val.add(other.toApfloat()));
		default:
			return null;
		}
	}

	@Override
	public Number sub(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.BIGNUM:
			return new BigNum(_val.subtract(other.toApfloat()));
		case Obj.RATIONAL_NUMBER:
			return new RationalNum(_val.subtract(other.toApfloat()).doubleValue());
		case Obj.NUM:
			return new BigNum(_val.subtract(other.toApfloat()));
		default:
			return null;
		}
	}

	@Override
	public Number mul(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.BIGNUM:
			return new BigNum(_val.multiply(other.toApfloat()));
		case Obj.RATIONAL_NUMBER:
			return new RationalNum(_val.multiply(other.toApfloat()).doubleValue());
		case Obj.NUM:
			return new BigNum(_val.multiply(other.toApfloat()));
		default:
			return null;
		}
	}

	@Override
	public Number div(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.BIGNUM:
			return new BigNum(_val.divide(other.toApfloat()));
		case Obj.RATIONAL_NUMBER:
			return new RationalNum(_val.divide(other.toApfloat()).doubleValue());
		case Obj.NUM:
			return new BigNum(_val.divide(other.toApfloat()));
		default:
			return null;
		}
	}

	@Override
	public Number idiv(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.BIGNUM:
			return new BigNum(_val.divide(other.toApfloat()).floor());
		case Obj.RATIONAL_NUMBER:
			return new RationalNum(_val.divide(other.toApfloat()).floor().longValue(), 1L);
		case Obj.NUM:
			return new BigNum(_val.divide(other.toApfloat()).floor());
		default:
			return null;
		}
	}

	@Override
	public Number mod(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.BIGNUM:
			return new BigNum(_val.mod(other.toApfloat()));
		case Obj.RATIONAL_NUMBER:
			return new RationalNum(_val.mod(other.toApfloat()).longValue(), 1L);
		case Obj.NUM:
			return new BigNum(_val.mod(other.toApfloat()));
		default:
			return null;
		}
	}

	@Override
	public Number pow(Number other) {
		byte type = other.type();
		switch (type) {
		case Obj.BIGNUM:
			return new BigNum(ApfloatMath.pow(_val, other.toApfloat()));
		case Obj.RATIONAL_NUMBER:
			return new RationalNum(ApfloatMath.pow(_val, other.toApfloat()).doubleValue());
		case Obj.NUM:
			return new BigNum(ApfloatMath.pow(_val, other.toApfloat()));
		default:
			return null;
		}
	}
	
	/////////////////////
	// MATH OPERATIONS //
	/////////////////////
	
	@Override
	public Number negate() {
		return new BigNum(_val.negate());
	}

	@Override
	public Number bnot() {
		return new BigNum(~(_val.intValue()));
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
	public BigNum deepcopy() {
		return new BigNum(_val.add(ZERO));
	}

	@Override
	public boolean bool() {
		return !(_val.compareTo(ZERO) == 0);
	}

	@Override
	public String repr() {
		return trimZeros(_val.toString());
	}

	@Override
	public String str() {
		return trimZeros(_val.toString());
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
		//Ascending
		return (_val.subtract(n.toApfloat())).intValue(); 
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



}
