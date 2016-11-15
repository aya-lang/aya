package element.entities.number;

import org.apfloat.Apfloat;

public abstract class Num {
	public abstract int toInt();
	public abstract double toDouble();
	public abstract boolean toBool();
	public abstract Apfloat toApfloat();
	
	public abstract Num negate();
	public abstract Num bnot();
}
