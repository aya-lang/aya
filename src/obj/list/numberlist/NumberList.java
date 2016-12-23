package obj.list.numberlist;

import obj.Obj;
import obj.list.List;
import obj.number.Number;

public abstract class NumberList extends List {

	/////////////////////
	// LIST OPERATIONS //
	/////////////////////
	
	/** Return the max value of the list */
	public abstract Number max();
	
	/** Return the minimum value of the list */
	public abstract Number min();
	
	/** Return the mean value of the list */
	public abstract Number mean();
	
	/** Return the sum of the list */
	public abstract Number sum();
	
	
	
	
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
	
	
	
	
	
	
	////////////////////
	// LIST OVERRIDES //
	////////////////////
	
	@Override
	public abstract int length();

	@Override
	public abstract void head(int i);

	@Override
	public abstract void tail(int i);

	@Override
	public abstract Obj head();

	@Override
	public abstract Obj tail();

	@Override
	public abstract Obj pop();

	@Override
	public abstract Obj popBack();

	@Override
	public abstract void reverse();

	@Override
	public abstract void slice(int i, int j);

	@Override
	public abstract Obj get(int i);

	@Override
	public abstract int find(Obj o);

	@Override
	public abstract int findBack(Obj o);

	@Override
	public abstract int count(Obj o);
	
	
	
	
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	
	@Override
	public abstract Obj deepcopy();

	@Override
	public abstract boolean bool();

	@Override
	public abstract String repr();

	@Override
	public abstract String str();

	@Override
	public abstract boolean equiv(Obj o);

}
