package element.entities.number;

import org.apfloat.Apfloat;

public abstract class Num implements Comparable<Num> {
	public abstract int toInt();
	public abstract long toLong();
	public abstract float toFloat();
	public abstract double toDouble();
	public abstract boolean toBool();
	public abstract Apfloat toApfloat();
	
	public abstract Num negate();
	public abstract Num bnot();
	public abstract BasicNum signnum();
	/** Integer factorial */
	public abstract Num factorial();
	public abstract Num abs();
	public abstract Num sin();
	public abstract Num cos();
	public abstract Num tan();
	public abstract Num asin();
	public abstract Num acos();
	public abstract Num atan();
	/** Log base 10 */
	public abstract Num log();
	/** Log base e */
	public abstract Num ln();
	public abstract Num sqrt();
	public abstract Num ceil();
	public abstract Num floor();
	
}
