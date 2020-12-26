package aya.obj.number;

import org.apfloat.Apfloat;

import aya.obj.Obj;

/** Abstract supertype for all numbers */

public abstract class Number extends Obj implements Comparable<Number> {

	public static final int TYPE_NUM = 0;
	public static final int TYPE_BIGNUM = 1;
	public static final int TYPE_FRACTION = 2;
	public static final int TYPE_COMPLEX = 2;
	
	public static final Apfloat AP_MAX_INT = new Apfloat(Integer.MAX_VALUE);
	
	private static int maxPrecision = 50;
	
	public static long getMaxPrecision() {
		return maxPrecision;
	}
	
	
	
	//////////////////////////
	// CONVERSION FUNCTIONS //
	//////////////////////////

	/** Convert the other number to the same type as this one */
	protected abstract Number convert(Number to_promote);

	/** Return the number type */
	protected abstract int numType();
	
	/** Convert to int using cast */
	public abstract int toInt();
	
	/** Convert to long using cast */
	public abstract long toLong();
	
	/** Convert to float using cast */
	public abstract float toFloat();
	
	/** Convert to double using cast */
	public abstract double toDouble();
	
	/** Convert to Apfloat */
	public abstract Apfloat toApfloat();
	
	/** Convert to byte */
	public byte toByte() {
		return (byte)toInt();
	}
	
	
	///////////////////////
	// BINARY OPERATIONS //
	///////////////////////
	
	/** Add */
	protected abstract Number add(Number other);
	
	/** Subtract */
	protected abstract Number sub(Number other);
	
	/** Multiply */
	protected abstract Number mul(Number other);
	
	/** Divide */
	protected abstract Number div(Number other);
	
	/** Integer division */
	protected abstract Number idiv(Number other);
	
	/** Modulus */
	protected abstract Number mod(Number other);
	
	/** Power */
	protected abstract Number pow(Number other);
	
	
	/////////////////////////////
	// MATHEMATICAL OPERATIONS //
	/////////////////////////////
		
	/** Negate */
	public abstract Number negate();
	
	/** Increment */
	public abstract Number inc();
	
	/** Decrement */
	public abstract Number dec();
	
	/** Signnum function */
	public abstract Number signnum();
	
	/** Integer factorial */
	public abstract Number factorial();
	
	/** Absolute value */
	public abstract Number abs();
	
	/** Exponential */
	public abstract Number exp();
	
	/** Trigonometric Sine */
	public abstract Number sin();
	
	/** Trigonometric Cosine */
	public abstract Number cos();
	
	/** Trigonometric Tangent */
	public abstract Number tan();
	
	/** Inverse Trigonometric Sine */ 
	public abstract Number asin();
	
	/** Inverse Trigonometric Cosine */
	public abstract Number acos();
	
	/** Inverse Trigonometric Tangent */
	public abstract Number atan();
	
	/** Log base 10 */
	public abstract Number log();
	
	/** Log base e */
	public abstract Number ln();
	
	/** Square Root */
	public abstract Number sqrt();
	
	/** Ceiling */
	public abstract Number ceil();
	
	/** Floor */
	public abstract Number floor();
	
	/** Primality test */
	public abstract boolean isPrime();
	
	@Override
	public Number deepcopy() { return this; }

	@Override
	public byte type() { return Obj.NUMBER; }

	//Common constants, return the same type
	public abstract Number one();
	public abstract Number zero();
	public abstract Number negOne();
	

}
