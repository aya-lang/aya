package element.obj.list.numberlist;

import java.util.ArrayList;

import element.obj.list.List;
import element.obj.number.Number;

public abstract class NumberList extends List {

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
	
	public abstract Integer[] toIntegerArray();
	
	///////////////////////////
	// VECTORIZED OPERATIONS //
	///////////////////////////
	
	/** Addition */
	public abstract NumberList add(Number n);
	
	/** Subtraction */
	public abstract NumberList sub(Number n);
	
	/** Division */
	public abstract NumberList div(Number n);
	
	/** Multiplication */
	public abstract NumberList mul(Number n);
	
	/** Modulus */
	public abstract NumberList mod(Number n);
	
	/** Integer devision */
	public abstract NumberList idiv(Number n);
	
	/** Power */
	public abstract NumberList pow(Number n);
	
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

	/** Return the list as an arrayList of numbers */
	public abstract ArrayList<Number> toArrayList();
	
	///////////////
	// OVERRIDES //
	///////////////
	
	public abstract Number get(int i);

}
