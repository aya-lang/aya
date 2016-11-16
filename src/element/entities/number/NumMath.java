package element.entities.number;

import org.apfloat.ApfloatMath;
import org.apfloat.ApintMath;

import element.entities.operations.ElementMath;

public class NumMath {
	
	public static Num mul(Num a, Num b) {
		if (a instanceof BasicNum && b instanceof BasicNum) {
			return new BasicNum(a.toDouble() * b.toDouble());
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(a.toApfloat().multiply(b.toApfloat()));
		}
	}
	
	public static Num add(Num a, Num b) {
		if (a instanceof BasicNum && b instanceof BasicNum) {
			return new BasicNum(a.toDouble() + b.toDouble());
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(a.toApfloat().add(b.toApfloat()));
		}
	}
	
	public static Num sub(Num a, Num b) {
		if (a instanceof BasicNum && b instanceof BasicNum) {
			return new BasicNum(a.toDouble() - b.toDouble());
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(a.toApfloat().subtract(b.toApfloat()));
		}
	}
	
	public static Num div(Num a, Num b) {
		if (a instanceof BasicNum && b instanceof BasicNum) {
			return new BasicNum(a.toDouble() / b.toDouble());
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(a.toApfloat().divide(b.toApfloat()));
		}
	}
	
	public static Num idiv(Num a, Num b) {
		if (a instanceof BasicNum && b instanceof BasicNum) {
			return new BasicNum(Math.floor(a.toDouble() / b.toDouble()));
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(a.toApfloat().divide(b.toApfloat()).floor());
		}		
	}
	
	public static Num mod(Num a, Num b) {
		if (a instanceof BasicNum && b instanceof BasicNum) {
			return new BasicNum(a.toDouble() % b.toDouble());
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(a.toApfloat().mod(b.toApfloat()));
		}		
	}
	
	public static Num pow(Num a, Num b) {
		if (a instanceof BasicNum && b instanceof BasicNum) {
			return new BasicNum(Math.pow(a.toDouble(), b.toDouble()));
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(ApfloatMath.pow(a.toApfloat(), b.toApfloat()));
		}		
	}	
	
	/**
	 * the value 0 if a is numerically equal to a; a value less than 0 if a is numerically
	 * less than b; and a value greater than 0 if a is numerically greater than b.
	 * @param a
	 * @param b
	 * @return
	 */
	public static int compare(Num a, Num b) {
		if (a instanceof BasicNum && b instanceof BasicNum) {
			return Double.compare(a.toDouble(), b.toDouble());
		} else {
			//At least one is a BigNum, just use their values
			return a.toApfloat().compareTo(b.toApfloat());
		}		
	}	
	
	
	public static Num gcd(Num a, Num b) {
		if (a instanceof BasicNum && b instanceof BasicNum) {
			return new BasicNum(gcd(a.toLong(), b.toLong()));
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(ApintMath.gcd(a.toApfloat().floor(), b.toApfloat().floor()));
		}
	}
	
	public static Num lcm(Num a, Num b) {
		if (a instanceof BasicNum && b instanceof BasicNum) {
			return new BasicNum(lcm(a.toLong(), b.toLong()));
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(ApintMath.lcm(a.toApfloat().floor(), b.toApfloat().floor()));
		}
	}
	
	//***************************
	// BITWISE OPERATIONS
	//***************************
	
	public static BasicNum band(Num a, Num b) {
		return new BasicNum(a.toInt() & b.toInt());
	}
	
	public static BasicNum bor(Num a, Num b) {
		return new BasicNum(a.toInt() | b.toInt());
	}
	
	
	
	
	//***************************
	// HELPER METHODS
	//***************************
	
	private static long gcd(long a, long b) {
		long r = 0;
		long x, y;
		a = Math.abs(a);
		b = Math.abs(b);
		
		//make x larger than y
		if (a < b) {
			x = b;
			y = a;
		} else {
			x = a;
			y = b;
		}
		
		r = y;
		while (x % y > 0) {
			r = x % y;
			x = y;
			y = r;
		}
		
		return r;
	}
	
	/** lcm */
	private static long lcm(long a, long b) {
		return a * b / gcd(a,b);
	}
	
	
}
