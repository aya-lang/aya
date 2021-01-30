package aya.obj.number;

import java.math.BigDecimal;

import com.mathlibrary.exception.CalculatorException;
import com.mathlibrary.function.Complex;

import aya.exceptions.runtime.MathError;
import aya.obj.Obj;
import aya.util.MathUtils;
import aya.util.StringUtils;

/** Contains a BigDecimal */
public class ComplexNum extends Number {
	
	public static final Complex COMPLEX_ONE = new Complex(1.0, 0.0);
	public static final Complex COMPLEX_ZERO = new Complex(1.0, 0.0);

	public static final ComplexNum ZERO = new ComplexNum(COMPLEX_ZERO);
	public static final ComplexNum ONE = new ComplexNum(COMPLEX_ONE);
	public static final ComplexNum NEG_ONE = new ComplexNum(-1);

	
	private Complex _val;
	
	//////////////////
	// CONSTURCTORS //
	//////////////////
	
	private ComplexNum(Complex c) {
		_val = c;
	}

	public ComplexNum(int n) {
		_val = new Complex(n, 0);
	}
	
	public ComplexNum(double d) {
		_val = new Complex(d, 0);
	}
	
	public ComplexNum(BigDecimal n) {
		_val = new Complex(n.doubleValue(), 0);
	}
	
	public ComplexNum(double r, double i) {
		_val = new Complex(r, i);
	}

	public double i() {
		return _val.i;
	}
	
	public double r() {
		return _val.r;
	}

	/////////////////
	// CONVERSIONS //
	/////////////////
	

	@Override
	public int toInt() {
		return (int)_val.r;
	}

	@Override
	public long toLong() {
		return (long)_val.r;
	}

	@Override
	public float toFloat() {
		return (float)_val.r;
	}

	@Override
	public double toDouble() {
		return _val.r;
	}

	@Override
	public BigDecimal toBigDecimal() {
		return new BigDecimal(_val.r);
	}
	
	///////////////////////
	// BINARY OPERATIONS //
	///////////////////////

	@Override
	public Number add(Number other) { return this.add((ComplexNum)other); }
	public ComplexNum add(ComplexNum other) { return new ComplexNum(Complex.add(_val, other._val)); }

	@Override
	public Number sub(Number other) { return this.sub((ComplexNum)other); }
	public ComplexNum sub(ComplexNum other) { return new ComplexNum(Complex.sub(_val, other._val)); }

	@Override
	public Number mul(Number other) { return this.mul((ComplexNum)other); }
	public ComplexNum mul(ComplexNum other) { return new ComplexNum(Complex.mul(_val, other._val)); }

	@Override
	public Number div(Number other) { return this.div((ComplexNum)other); }
	public ComplexNum div(ComplexNum other) {
		try {
			return new ComplexNum(Complex.div(_val, other._val));
		} catch (CalculatorException e) {
			throw new MathError(e.getMessage());
		}
	}

	@Override
	public Number idiv(Number other) { return this.idiv((ComplexNum)other); }
	public ComplexNum idiv(ComplexNum other) {
		try {
			return new ComplexNum(floor(Complex.div(_val, other._val)));
		} catch (CalculatorException e) {
			throw new MathError(e.getMessage());
		}
	}
	
	@Override
	public Number mod(Number other) { return this.mod((ComplexNum)other); }
	public ComplexNum mod(ComplexNum other) {
		return new ComplexNum(new Complex(((long)_val.r) % ((long)other._val.r), 0.0));
	}

	@Override
	public Number pow(Number other) { return this.pow((ComplexNum)other); }
	public ComplexNum pow(ComplexNum other) { 
		Complex a = Complex.mul(other._val, _val.ln());
		a = a.exp();
		return new ComplexNum(a);
	}

	@Override
	public Number gcd(Number other) { return this.gcd((ComplexNum)other); }
	public ComplexNum gcd(ComplexNum other) { return new ComplexNum(MathUtils.gcd((long)_val.r, (long)other._val.r)); }

	@Override
	public Number lcm(Number other) { return this.lcm((ComplexNum)other); }
	public ComplexNum lcm(ComplexNum other) { return new ComplexNum(MathUtils.lcm((long)_val.r, (long)other._val.r)); }

	

	/////////////////////
	// MATH OPERATIONS //
	/////////////////////
	
	@Override
	public Number negate() {
		return new ComplexNum(_val.negate());
	}
	
	@Override
	public Number inc() {
		return new ComplexNum(Complex.add(_val, COMPLEX_ONE));
	}

	@Override
	public Number dec() {
		return new ComplexNum(Complex.sub(_val, COMPLEX_ONE));

	}

	@Override
	public Number signnum() {
		return Num.fromInt(MathUtils.signnum(_val.r));
	}

	@Override
	public Number factorial() {
		return new Num(MathUtils.factorial((long)_val.r));
	}

	@Override
	public Number abs() {
		return new Num(Complex.abs(_val));
	}
	
	@Override
	public Number exp() {
		return new ComplexNum(_val.exp());
	}

	@Override
	public Number sin() {
		return new ComplexNum(_val.sin());
	}

	@Override
	public Number cos() {
		return new ComplexNum(_val.cos());
	}

	@Override
	public Number tan() {
		try {
			return new ComplexNum(_val.tan());
		} catch (CalculatorException e) {
			throw new MathError(e.getMessage());
		}
	}

	@Override
	public Number asin() {
		return new ComplexNum(_val.asin());
	}

	@Override
	public Number acos() {
		return new ComplexNum(_val.acos());
	}

	@Override
	public Number atan() {
		try {
			return new ComplexNum(_val.atan());
		} catch (CalculatorException e) {
			throw new MathError(e.getMessage());
		}
	}

	@Override
	public Number log() {
		return new ComplexNum(_val.log());
	}

	@Override
	public Number ln() {
		return new ComplexNum(_val.ln());
	}

	@Override
	public Number sqrt() {
		return new ComplexNum(_val.sqrt());
	}

	@Override
	public Number ceil() {
		return new ComplexNum(ceil(_val));
	}

	@Override
	public Number floor() {
		return new ComplexNum(floor(_val));
	}
	
	@Override
	public Number imag() {
		return new Num(_val.i);
	}

	@Override
	public boolean isPrime() {
		return MathUtils.isPrime((long)_val.r);
	}

	@Override
	public boolean bool() {
		return _val.r != 0.0;
	}

	@Override
	public String str() {
		return ":" + StringUtils.doubleToString(_val.r) + "i" + StringUtils.doubleToString(_val.i);
	}

	@Override
	public boolean equiv(Obj o) {
		if (o instanceof ComplexNum) {
			return ((ComplexNum)o)._val.equals(_val);
		} else if (o instanceof Number && _val.i == 0.0) {
			return ((Number)o).toDouble() == _val.r;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isa(byte type) {
		return type == Obj.NUMBER || type == Obj.COMPLEXNUM;
	}

	@Override
	public byte type() {
		return Obj.COMPLEXNUM;
	}
	
	

	@Override
	public int compareTo(Number n) {
		return Double.compare(_val.r, n.toDouble());
	}
	


	
	///////////////
	// CONSTANTS //
	///////////////
	
	@Override
	public ComplexNum one() {
		return ONE;
	}

	@Override
	public ComplexNum zero() {
		return ZERO;
	}

	@Override
	public ComplexNum negOne() {
		return NEG_ONE;
	}

	


	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
	private static Complex ceil(Complex complex) {
		return new Complex(Math.ceil(complex.r), complex.i);
	}
	
	private static Complex floor(Complex complex) {
		return new Complex(Math.floor(complex.r), complex.i);
	}
	
	@Override
	protected Number convert(Number to_promote) {
		return new ComplexNum(to_promote.toDouble(), 0);
	}

	@Override
	protected int numType() {
		return Number.TYPE_BIGNUM;
	}

}
