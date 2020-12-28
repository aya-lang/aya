package aya.obj.list.numberlist;

import java.util.ArrayList;

import aya.obj.list.ListImpl;
import aya.obj.number.Number;

/** Supertype for all lists containing only numbers */
public abstract class NumberList extends ListImpl {

	/////////////////////
	// LIST OPERATIONS //
	/////////////////////
	
	/** Return the max value of the list. If list.length() = 0, return Num.MIN_VALUE */
	public abstract Number max();
	
	/** Return the minimum value of the list. If list.length() = 0, return Num.MAX_VALUE */
	public abstract Number min();
	
	/** Return the mean value of the list, If list.length() = 0, return 0 */
	public abstract Number mean();
	
	/** Return the sum of the list */
	public abstract Number sum();
	
	/////////////////
	// CONVERSIONS //
	/////////////////
	
	/** Convert to Integer[] */
	public abstract Integer[] toIntegerArray();
	
	/** Convert to int[] */
	public abstract int[] toIntArray();
	
	/** Convert to double[] */
	public abstract double[] todoubleArray();
	
	/** Convert to byte[] */
	public abstract byte[] toByteArray();
	
	///////////////////////////
	// VECTORIZED OPERATIONS //
	///////////////////////////
	
	/** Addition */
	public abstract NumberList add(Number n);
	public abstract NumberList add(NumberList ns);
	
	/** Subtraction */
	public abstract NumberList sub(Number n);
	public abstract NumberList sub(NumberList ns);
	
	/** Reverse Subtraction */
	public abstract NumberList subFrom(Number n);
	public abstract NumberList subFrom(NumberList ns);
	
	/** Division */
	public abstract NumberList div(Number n);
	public abstract NumberList div(NumberList ns);
	
	/** Reverse Division */
	public abstract NumberList divFrom(Number n);
	public abstract NumberList divFrom(NumberList ns);
	
	/** Multiplication */
	public abstract NumberList mul(Number n);
	public abstract NumberList mul(NumberList ns);
	
	/** Modulus */
	public abstract NumberList mod(Number n);
	public abstract NumberList mod(NumberList ns);
	
	/** Reverse Modulus */
	public abstract NumberList modFrom(Number n);
	public abstract NumberList modFrom(NumberList ns);
	
	/** Integer division */
	public abstract NumberList idiv(Number n);
	public abstract NumberList idiv(NumberList ns);
	
	/** Reverse division */
	public abstract NumberList idivFrom(Number n);
	public abstract NumberList idivFrom(NumberList ns);
	
	/** Power */
	public abstract NumberList pow(Number n);
	public abstract NumberList pow(NumberList ns);
	
	/** Reverse Power */
	public abstract NumberList powFrom(Number n);
	public abstract NumberList powFrom(NumberList ns);
	
	/** Binary And */
	public abstract NumberList band(Number n);
	public abstract NumberList band(NumberList ns);
	
	/** Reverse Binary And */
	public abstract NumberList bandFrom(Number n);
	public abstract NumberList bandFrom(NumberList ns);
	
	/** Binary Or */
	public abstract NumberList bor(Number n);
	public abstract NumberList bor(NumberList ns);
	
	/** Reverse Binary Or */
	public abstract NumberList borFrom(Number n);
	public abstract NumberList borFrom(NumberList ns);
	
	
	/** Negate */
	public abstract NumberList negate();
	
	/** Bitwise Not */
	public abstract NumberList bnot();
	
	/** Signnum function */
	public abstract NumberList signnum();
	
	/** Integer factorial */
	public abstract NumberList factorial();
	
	/** Absolute value */
	public abstract NumberList abs();
	
	/** Exponential */
	public abstract NumberList exp();
	
	/** Trigonometric Sine */
	public abstract NumberList sin();
	
	/** Trigonometric Cosine */
	public abstract NumberList cos();
	
	/** Trigonometric Tangent */
	public abstract NumberList tan();
	
	/** Inverse Trigonometric Sine */ 
	public abstract NumberList asin();
	
	/** Inverse Trigonometric Cosine */
	public abstract NumberList acos();
	
	/** Inverse Trigonometric Tangent */
	public abstract NumberList atan();
	
	/** Log base 10 */
	public abstract NumberList log();
	
	/** Log base e */
	public abstract NumberList ln();
	
	/** Square Root */
	public abstract NumberList sqrt();
	
	/** Ceiling */
	public abstract NumberList ceil();
	
	/** Floor */
	public abstract NumberList floor();

	/** Imag */
	public abstract ListImpl imag();

	/** Return the list as an arrayList of numbers */
	public abstract ArrayList<Number> toArrayList();
	
	/////////////////
	// COMPARISONS //
	/////////////////
	
	/** Less than */
	public abstract NumberList lt(Number n);
	public abstract NumberList lt(NumberList ns);
	
	/** Less than or equal to */
	public abstract NumberList leq(Number n);
	public abstract NumberList leq(NumberList ns);
	
	/** Greater than */
	public abstract NumberList gt(Number n);
	public abstract NumberList gt(NumberList ns);
	
	/** Greater than or equal to */
	public abstract NumberList geq(Number n);
	public abstract NumberList geq(NumberList ns);
	
	/** Element-wise equal to */
	public abstract NumberList eq(Number n);
	public abstract NumberList eq(NumberList ns);
	
	///////////////
	// OVERRIDES //
	///////////////
	
	@Override
	public abstract Number get(int i);


	

}
