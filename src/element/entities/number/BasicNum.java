package element.entities.number;

import org.apfloat.Apfloat;

import element.ElemTypes;

public class BasicNum extends Num {
	private double value;
	
	public BasicNum(int n) {
		this.value = (double)n;
	}
	
	public BasicNum(double d) {
		this.value = d;
	}
	
	public BasicNum(BigNum bn) {
		this.value = bn.toDouble();
	}
	
	public BasicNum(String str) {
		try {
			this.value = Double.parseDouble(str);
		} catch (NumberFormatException n) {
			this.value = Double.NaN;
		}
	}
	
	public double value() {
		return this.value;
	}
	
	
	//*******************************
	//   CONVERSIONS
	//*******************************
	
	public int toInt() {
		return (int)this.value;
	}
	
	public boolean toBool() {
		return this.value != 0.0;
	}
	
	public double toDouble() {
		return this.value;
	}
	
	public Apfloat toApfloat() {
		return new Apfloat(this.value);
	}
	
	public String toString() {
		return ElemTypes.trimZeros(String.format("%1.10f", this.value));
	}
	
	//*******************************
	//   COMPARISONS
	//*******************************
	
	public boolean eq(BasicNum other) {
		return other.value == this.value;
	}
	
	public boolean lt(BasicNum other) {
		return this.value < other.value;
	}
	
	public boolean gt(BasicNum other) {
		return this.value > other.value;
	}
	
	//*******************************
	//   OPERATIONS
	//*******************************
	
	public BasicNum add(BasicNum other) {
		return new BasicNum(this.value + other.value);
	}
	
	public BasicNum sub(BasicNum other) {
		return new BasicNum(this.value - other.value);

	}
	
	public BasicNum mul(BasicNum other) {
		return new BasicNum(this.value * other.value);

	}
	
	public BasicNum div(BasicNum other) {
		return new BasicNum(this.value / other.value);

	}
	
	public BasicNum idiv(BasicNum other) {
		return new BasicNum(Math.floor(this.value / other.value));
	}
	
	public BasicNum mod(BasicNum other) {
		return new BasicNum(this.value % other.value);
	}
	
	public BasicNum pow(BasicNum other) {
		return new BasicNum(Math.pow(this.value, other.value));
	}
	
	public BasicNum negate() {
		return new BasicNum(this.value * -1.0);
	}
	
	public BasicNum bnot() {
		return new BasicNum((double)~((int)this.value));
	}

}
