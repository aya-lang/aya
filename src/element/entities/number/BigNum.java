package element.entities.number;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import element.ElemTypes;

public class BigNum extends Num {
	private Apfloat value;
	
	public BigNum(int n) {
		//Infinite precision
		this.value = new Apfloat(n);
	}
	
	public BigNum(double d) {
		//16 digit precision
		this.value = new Apfloat(d);
	}
	
	public BigNum(BasicNum n) {
		//16 digit precision
		this.value = new Apfloat(n.value());
	}
	
	public BigNum(String str) {
		//precision dependent on string
		try {
			this.value = new Apfloat(str);
		} catch (NumberFormatException e) {
			this.value = new Apfloat(0.0);
		}
	}
	
	public BigNum(Apfloat a) {
		this.value = a;
	}

	public Apfloat value() {
		return this.value;
	}
	
	//*******************************
	//   CONVERSIONS
	//*******************************
	
	public int toInt() {
		return this.value.intValue();
	}
	
	public double toDouble() {
		return this.value.doubleValue();
	}
	
	public boolean toBool() {
		return this.value.intValue() != 0;
	}
	
	public Apfloat toApfloat() {
		return this.value;
	}
	
	public String toString() {
		return ElemTypes.trimZeros(value.toString(true));
	}
	
	//*******************************
	//   COMPARISONS
	//*******************************
	
	public boolean eq(BigNum other) {
		return this.value.compareTo(other.value) == 0;
	}
	
	public boolean lt(BigNum other) {
		return this.value.compareTo(other.value) < 0;
	}
	
	public boolean gt(BigNum other) {
		return this.value.compareTo(other.value) > 0;
	}
	
	//*******************************
	//   OPERATIONS
	//*******************************
	
	public BigNum add(BigNum other) {
		return new BigNum(this.value.add(other.value));
	}
	
	public BigNum sub(BigNum other) {
		return new BigNum(this.value.subtract(other.value));
	}
	
	public BigNum mul(BigNum other) {
		return new BigNum(this.value.multiply(other.value));
	}
	
	public BigNum div(BigNum other) {
		return new BigNum(this.value.divide(other.value));
	}
	
	public BigNum idiv(BigNum other) {
		return new BigNum(this.value.divide(other.value).floor());
	}
	
	public BigNum mod(BigNum other) {
		return new BigNum(this.value.mod(other.value));
	}
	
	public BigNum pow(BigNum other) {
		return new BigNum(ApfloatMath.pow(this.value, other.value));
	}
	
	@Override
	public BigNum negate() {
		return new BigNum(this.value.negate());
	}
	
	@Override
	public BigNum bnot() {
		return new BigNum((double)~((int)this.toInt()));
	}
	
}
