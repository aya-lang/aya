package aya.obj.number;

import org.apfloat.Apfloat;

import aya.obj.Obj;

/** Abstract supertype for all numbers */
public abstract class Number extends Obj implements Comparable<Number> {
	
	public static final Apfloat AP_MAX_INT = new Apfloat(Integer.MAX_VALUE);
	
	private static int maxPrecision = 50;
	
	public static long getMaxPrecision() {
		return maxPrecision;
	}
	
	
	
	//////////////////////////
	// CONVERSION FUNCTIONS //
	//////////////////////////
	
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
	
	
	///////////////////////
	// BINARY OPERATIONS //
	///////////////////////
	
	/** Add */
	public abstract Number add(Number other);
	
	/** Subtract */
	public abstract Number sub(Number other);
	
	/** Multiply */
	public abstract Number mul(Number other);
	
	/** Divide */
	public abstract Number div(Number other);
	
	/** Integer division */
	public abstract Number idiv(Number other);
	
	/** Modulus */
	public abstract Number mod(Number other);
	
	/** Power */
	public abstract Number pow(Number other);
	
	/** Binary AND */
	public abstract Number band(Number other);
	
	/** Binary OR */
	public abstract Number bor(Number other);
	
	
	
	
	/////////////////////////////
	// MATHEMATICAL OPERATIONS //
	/////////////////////////////
		
	/** Negate */
	public abstract Number negate();
	
	/** Increment */
	public abstract Number inc();
	
	/** Decrement */
	public abstract Number dec();
	
	/** Bitwise Not */
	public abstract Number bnot();
	
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
	public abstract Number deepcopy();


	// Slight performance improvement for list builders
	public abstract Number subEq(Number v);
	public abstract Number addEq(Number v);

}
