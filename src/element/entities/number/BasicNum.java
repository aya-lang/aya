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
	
	
	//*******************************
	//   OVERRIDES
	//*******************************
	
	@Override
	public BasicNum negate() {
		return new BasicNum(this.value * -1.0);
	}
	
	@Override
	public BasicNum bnot() {
		return new BasicNum((double)~((int)this.value));
	}

	@Override
	public BasicNum signnum() {
		if (this.value == 0.0) {
			return new BasicNum(0);
		} else if (this.value > 0.0) {
			return new BasicNum(1);
		} else {
			return new BasicNum(-1);
		}
	}

	@Override
	public BasicNum factorial() {
		return new BasicNum(factorial((long)value));
	}
	
	@Override
	public BasicNum abs() {
		return new BasicNum(Math.abs(value));
	}

	@Override
	public BasicNum sin() {
		return new BasicNum(Math.sin(value));
	}

	@Override
	public BasicNum cos() {
		return new BasicNum(Math.cos(value));
	}

	@Override
	public BasicNum tan() {
		return new BasicNum(Math.tan(value));
	}

	@Override
	public BasicNum asin() {
		return new BasicNum(Math.asin(value));
	}

	@Override
	public BasicNum acos() {
		return new BasicNum(Math.acos(value));
	}

	@Override
	public BasicNum atan() {
		return new BasicNum(Math.atan(value));
	}

	@Override
	public BasicNum log() {
		// Logarithm base 10
		return new BasicNum(Math.log10(value));
	}

	@Override
	public BasicNum ln() {
		//Natural Logarithm
		return new BasicNum(Math.log(value));
	}

	@Override
	public BasicNum sqrt() {
		return new BasicNum(Math.sqrt(value));
	}

	@Override
	public BasicNum ceil() {
		return new BasicNum(Math.ceil(value));
	}

	@Override
	public BasicNum floor() {
		return new BasicNum(Math.floor(value));
	}
	
	//*******************************
	//   IMPLEMENTS COMPARABLE
	//*******************************
	
	@Override
	public int compareTo(Num n) {
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
		}
		return acc;
	}
	
	
	
}
