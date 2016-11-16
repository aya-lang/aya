package element.entities.number;

import org.apfloat.Apfloat;

public abstract class Numeric implements Comparable<Numeric> {
	public static final Apfloat AP_MAX_INT = new Apfloat(Integer.MAX_VALUE);
	
	private static int maxPrecision = 50;
	
	public abstract int toInt();
	public abstract long toLong();
	public abstract float toFloat();
	public abstract double toDouble();
	public abstract boolean toBool();
	public abstract Apfloat toApfloat();
		
	public abstract Numeric negate();
	public abstract Numeric bnot();
	public abstract Num signnum();
	/** Integer factorial */
	public abstract Numeric factorial();
	public abstract Numeric abs();
	public abstract Numeric sin();
	public abstract Numeric cos();
	public abstract Numeric tan();
	public abstract Numeric asin();
	public abstract Numeric acos();
	public abstract Numeric atan();
	/** Log base 10 */
	public abstract Numeric log();
	/** Log base e */
	public abstract Numeric ln();
	public abstract Numeric sqrt();
	public abstract Numeric ceil();
	public abstract Numeric floor();
	/** Primality test */
	public abstract boolean isPrime();
	
	
	public static long getMaxPrecision() {
		return maxPrecision;
	}
	
}
