package element.entities.number;

import java.io.File;

import org.apfloat.Apfloat;

import element.ElemTypes;
import element.exceptions.ElementRuntimeException;

public class Num extends Numeric {
	
	public static Num PI = new Num(Math.PI);
	public static Num E = new Num(Math.E);
	public static Num DOUBLE_MAX = new Num(Double.MAX_VALUE);
	public static Num DOUBLE_MIN = new Num(Double.MIN_VALUE);
	public static Num DOUBLE_NAN = new Num(Double.NaN); 
	public static Num DOUBLE_INF = new Num(Double.POSITIVE_INFINITY);
	public static Num DOUBLE_NINF = new Num(Double.NEGATIVE_INFINITY);
	public static Num INT_MAX = new Num(Integer.MAX_VALUE);
	public static Num INT_MIN = new Num(Integer.MIN_VALUE);


	private double value;
	
	public Num(int n) {
		this.value = (double)n;
	}
	
	public Num(double d) {
		this.value = d;
	}
	
	public Num(BigNum bn) {
		this.value = bn.toDouble();
	}
	
	public Num(String str) {
		try {
			this.value = Double.parseDouble(str);
		} catch (NumberFormatException n) {
			this.value = Double.NaN;
		}
	}	
	
	
	
	//*******************************
	//   CONVERSIONS
	//*******************************
	
	public int toInt() {
		return (int)this.value;
	}
	
	public long toLong() {
		return (long)(this.value);
	}
	
	public float toFloat() {
		return (float)(this.value);
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
		if (value % 1 == 0) {
			return String.format("%d",(long)value);
		} else {
			return String.format("%s", value);
		}
	}
	
	//*******************************
	//   COMPARISONS
	//*******************************
	
	public boolean eq(Num other) {
		return other.value == this.value;
	}
	
	public boolean lt(Num other) {
		return this.value < other.value;
	}
	
	public boolean gt(Num other) {
		return this.value > other.value;
	}
	
	//*******************************
	//   OPERATIONS
	//*******************************
	
	public Num add(Num other) {
		return new Num(this.value + other.value);
	}
	
	public Num sub(Num other) {
		return new Num(this.value - other.value);

	}
	
	public Num mul(Num other) {
		return new Num(this.value * other.value);

	}
	
	public Num div(Num other) {
		return new Num(this.value / other.value);

	}
	
	public Num idiv(Num other) {
		return new Num(Math.floor(this.value / other.value));
	}
	
	public Num mod(Num other) {
		return new Num(this.value % other.value);
	}
	
	public Num pow(Num other) {
		return new Num(Math.pow(this.value, other.value));
	}
	
	
	//*******************************
	//   OVERRIDES
	//*******************************
	
	public static Num fromObj(Object o) {
		if (o instanceof Num) {
			return (Num)o;
		} else if (o instanceof BigNum) {
			return new Num((BigNum)o);
		} else {
			throw new ElementRuntimeException("Cannot cast " + o + " to a basic number");
		}
	}
	
	@Override
	public Num negate() {
		return new Num(this.value * -1.0);
	}
	
	@Override
	public Num bnot() {
		return new Num((double)~((int)this.value));
	}

	@Override
	public Num signnum() {
		if (this.value == 0.0) {
			return new Num(0);
		} else if (this.value > 0.0) {
			return new Num(1);
		} else {
			return new Num(-1);
		}
	}

	@Override
	public Num factorial() {
		return new Num(factorial((long)value));
	}
	
	@Override
	public Num abs() {
		return new Num(Math.abs(value));
	}

	@Override
	public Num sin() {
		return new Num(Math.sin(value));
	}

	@Override
	public Num cos() {
		return new Num(Math.cos(value));
	}

	@Override
	public Num tan() {
		return new Num(Math.tan(value));
	}

	@Override
	public Num asin() {
		return new Num(Math.asin(value));
	}

	@Override
	public Num acos() {
		return new Num(Math.acos(value));
	}

	@Override
	public Num atan() {
		return new Num(Math.atan(value));
	}

	@Override
	public Num log() {
		// Logarithm base 10
		return new Num(Math.log10(value));
	}

	@Override
	public Num ln() {
		//Natural Logarithm
		return new Num(Math.log(value));
	}

	@Override
	public Num sqrt() {
		return new Num(Math.sqrt(value));
	}

	@Override
	public Num ceil() {
		return new Num(Math.ceil(value));
	}

	@Override
	public Num floor() {
		return new Num(Math.floor(value));
	}
	
	@Override
	public boolean isPrime() {
		return NumMath.isPrime((long)value);
	}
	
	//*******************************
	//   IMPLEMENTS COMPARABLE
	//*******************************
	
	@Override
	public int compareTo(Numeric n) {
		//Ascending
		return (int)((this.value - n.toDouble())); 
	}
		
	
	//*******************************
	//   MATH
	//*******************************

	
	/** logGamma: Uses Lanczos approximation formula */ 
	private static double logGamma(double x) {
		 double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
		 double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
				 + 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
				 +  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
		 return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
	   }
	
	/** Gamma Function */
	private static double gamma(double x) { return Math.exp(logGamma(x)); }
	
	
	/** Integer Factorial */
	private static long factorial(long x) {
		long acc = 1;
		while (x > 0) {
			acc *= x;
			x--;
		}
		return acc;
	}
	
	
	
}
