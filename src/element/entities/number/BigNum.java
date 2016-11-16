package element.entities.number;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApintMath;

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
		this.value = new Apfloat(n.toDouble());
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
	
	public long toLong() {
		return this.value.longValue();
	}
	
	public float toFloat() {
		return this.value.floatValue();
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
		
	
	//*******************************
	//   OVERRIDES
	//*******************************
	
	
	
	@Override
	public BigNum negate() {
		return new BigNum(this.value.negate());
	}
	
	@Override
	public BigNum bnot() {
		return new BigNum((double)~((int)this.toInt()));
	}
	
	public BasicNum signnum() {
		return new BasicNum(this.value.signum());
	}

	@Override
	public Num factorial() {
		return new BigNum(ApintMath.factorial(value.longValue()));
	}
	
	@Override
	public Num abs() {
		return new BigNum(ApfloatMath.abs(value));
	}

	@Override
	public Num sin() {
		return new BigNum(ApfloatMath.sin(value));
	}

	@Override
	public Num cos() {
		return new BigNum(ApfloatMath.cos(value));
	}

	@Override
	public Num tan() {
		return new BigNum(ApfloatMath.tan(value));
	}

	@Override
	public Num asin() {
		return new BigNum(ApfloatMath.asin(value));
	}

	@Override
	public Num acos() {
		return new BigNum(ApfloatMath.acos(value));
	}

	@Override
	public Num atan() {
		return new BigNum(ApfloatMath.atan(value));
	}

	@Override
	public Num log() {
		return new BigNum(ApfloatMath.log(value, new Apfloat(10)));
	}

	@Override
	public Num ln() {
		return new BigNum(ApfloatMath.log(value));
	}

	@Override
	public Num sqrt() {
		return new BigNum(ApfloatMath.sqrt(value));
	}

	@Override
	public Num ceil() {
		return new BigNum(value.ceil());
	}

	@Override
	public Num floor() {
		return new BigNum(value.floor());
	}
	
	//*******************************
	//   IMPLEMENTS COMPARABLE
	//*******************************
	
	@Override
	public int compareTo(Num n) {
		//Ascending
		return (this.value.subtract(n.toApfloat())).intValue(); 
	}
	
}
