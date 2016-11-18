package element.entities.number;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApintMath;

import element.ElemTypes;
import element.exceptions.ElementRuntimeException;

public class BigNum extends Numeric {
	private Apfloat value;
	
	public BigNum(int n) {
		//Infinite precision
		this.value = new Apfloat(n);
	}
	
	public BigNum(double d) {
		//16 digit precision
		this.value = new Apfloat(d);
	}
	
	public BigNum(Num n) {
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
	
	public int toIndex(int size) {
		int i = this.value.intValue();
		return i >= 0 ? i : size + i;
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
	
	public static BigNum fromObj(Object o) {
		if (o instanceof BigNum) {
			return (BigNum)o;
		} else if (o instanceof Num) {
			return new BigNum((Num)o);
		} else {
			throw new ElementRuntimeException("Cannot cast " + o + " to a big number");
		}
	}	
	
	@Override
	public BigNum negate() {
		return new BigNum(this.value.negate());
	}
	
	@Override
	public BigNum bnot() {
		return new BigNum((double)~((int)this.toInt()));
	}
	
	public Num signnum() {
		return new Num(this.value.signum());
	}

	@Override
	public Numeric factorial() {
		return new BigNum(ApintMath.factorial(value.longValue()));
	}
	
	@Override
	public Numeric abs() {
		return new BigNum(ApfloatMath.abs(value));
	}

	@Override
	public Numeric sin() {
		return new BigNum(ApfloatMath.sin(value));
	}

	@Override
	public Numeric cos() {
		return new BigNum(ApfloatMath.cos(value));
	}

	@Override
	public Numeric tan() {
		return new BigNum(ApfloatMath.tan(value));
	}

	@Override
	public Numeric asin() {
		return new BigNum(ApfloatMath.asin(value));
	}

	@Override
	public Numeric acos() {
		return new BigNum(ApfloatMath.acos(value));
	}

	@Override
	public Numeric atan() {
		return new BigNum(ApfloatMath.atan(value));
	}

	@Override
	public Numeric log() {
		return new BigNum(ApfloatMath.log(value, new Apfloat(10)));
	}

	@Override
	public Numeric ln() {
		return new BigNum(ApfloatMath.log(value));
	}

	@Override
	public Numeric sqrt() {
		return new BigNum(ApfloatMath.sqrt(value));
	}

	@Override
	public Numeric ceil() {
		return new BigNum(value.ceil());
	}

	@Override
	public Numeric floor() {
		return new BigNum(value.floor());
	}
	
	@Override
	public boolean isPrime() {
		return NumMath.isPrime(value.longValue());
	};
	//*******************************
	//   IMPLEMENTS COMPARABLE
	//*******************************
	
	@Override
	public int compareTo(Numeric n) {
		//Ascending
		return (this.value.subtract(n.toApfloat())).intValue(); 
	}
	
}
