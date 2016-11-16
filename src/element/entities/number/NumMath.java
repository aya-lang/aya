package element.entities.number;

import org.apfloat.ApfloatMath;
import org.apfloat.ApintMath;

import element.entities.operations.ElementMath;

public class NumMath {
	
	public static Numeric mul(Numeric a, Numeric b) {
		if (a instanceof Num && b instanceof Num) {
			return new Num(a.toDouble() * b.toDouble());
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(a.toApfloat().multiply(b.toApfloat()));
		}
	}
	
	public static Numeric add(Numeric a, Numeric b) {
		if (a instanceof Num && b instanceof Num) {
			return new Num(a.toDouble() + b.toDouble());
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(a.toApfloat().add(b.toApfloat()));
		}
	}
	
	public static Numeric sub(Numeric a, Numeric b) {
		if (a instanceof Num && b instanceof Num) {
			return new Num(a.toDouble() - b.toDouble());
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(a.toApfloat().subtract(b.toApfloat()));
		}
	}
	
	public static Numeric div(Numeric a, Numeric b) {
		if (a instanceof Num && b instanceof Num) {
			return new Num(a.toDouble() / b.toDouble());
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(a.toApfloat().divide(b.toApfloat()));
		}
	}
	
	public static Numeric idiv(Numeric a, Numeric b) {
		if (a instanceof Num && b instanceof Num) {
			return new Num(Math.floor(a.toDouble() / b.toDouble()));
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(a.toApfloat().divide(b.toApfloat()).floor());
		}		
	}
	
	public static Numeric mod(Numeric a, Numeric b) {
		if (a instanceof Num && b instanceof Num) {
			return new Num(a.toDouble() % b.toDouble());
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(a.toApfloat().mod(b.toApfloat()));
		}		
	}
	
	public static Numeric pow(Numeric a, Numeric b) {
		if (a instanceof Num && b instanceof Num) {
			return new Num(Math.pow(a.toDouble(), b.toDouble()));
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
	public static int compare(Numeric a, Numeric b) {
		if (a instanceof Num && b instanceof Num) {
			return Double.compare(a.toDouble(), b.toDouble());
		} else {
			//At least one is a BigNum, just use their values
			return a.toApfloat().compareTo(b.toApfloat());
		}		
	}	
	
	
	public static Numeric gcd(Numeric a, Numeric b) {
		if (a instanceof Num && b instanceof Num) {
			return new Num(gcd(a.toLong(), b.toLong()));
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(ApintMath.gcd(a.toApfloat().floor(), b.toApfloat().floor()));
		}
	}
	
	public static Numeric lcm(Numeric a, Numeric b) {
		if (a instanceof Num && b instanceof Num) {
			return new Num(lcm(a.toLong(), b.toLong()));
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(ApintMath.lcm(a.toApfloat().floor(), b.toApfloat().floor()));
		}
	}
	
	//***************************
	// BITWISE OPERATIONS
	//***************************
	
	public static Num band(Numeric a, Numeric b) {
		return new Num(a.toInt() & b.toInt());
	}
	
	public static Num bor(Numeric a, Numeric b) {
		return new Num(a.toInt() | b.toInt());
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
	
	/** isprime */
	public static boolean isPrime(long n) {
	    if(n < 2) return false;
	    if(n == 2 || n == 3) return true;
	    if(n%2 == 0 || n%3 == 0) return false;
	    long sqrtN = (long)Math.sqrt(n)+1;
	    for(long i = 6L; i <= sqrtN; i += 6) {
	        if(n%(i-1) == 0 || n%(i+1) == 0) return false;
	    }
	    return true;
	}	
}
