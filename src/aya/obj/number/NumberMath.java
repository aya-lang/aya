package aya.obj.number;

import org.apfloat.ApintMath;
import aya.obj.number.Num;

/** Various static functions for doing math on numbers */
public class NumberMath {

	// Utility class
	static class NumberPair {
		NumberPair(Number a, Number b) {
			this.a = a;
			this.b = b;
		}
		Number a;
		Number b;
	}
	
	static int compType(Number a, Number b) {
		return Integer.compare(a.numType(), b.numType());
	}
	
	static NumberPair promote(Number a, Number b) {
		int comp = compType(a, b);
		if (comp == 0) {
			return new NumberPair(a, b);
		} else if (comp > 0) {
			return new NumberPair(a, a.convert(b));
		} else {
			return new NumberPair(b.convert(a), b);
		}
	}
	
	public static Number add(Number a, Number b) {
		NumberPair pair = promote(a, b);
		return pair.a.add(pair.b);
	}

	public static Number sub(Number a, Number b) {
		NumberPair pair = promote(a, b);
		return pair.a.sub(pair.b);
	}

	public static Number mul(Number a, Number b) {
		NumberPair pair = promote(a, b);
		return pair.a.mul(pair.b);
	}

	public static Number div(Number a, Number b) {
		NumberPair pair = promote(a, b);
		return pair.a.div(pair.b);
	}

	public static Number idiv(Number a, Number b) {
		NumberPair pair = promote(a, b);
		return pair.a.idiv(pair.b);
	}

	public static Number mod(Number a, Number b) {
		NumberPair pair = promote(a, b);
		return pair.a.mod(pair.b);
	}

	public static Number pow(Number a, Number b) {
		NumberPair pair = promote(a, b);
		return pair.a.pow(pair.b);
	}
	
	/** Greatest common denominator */
	public static Number gcd(Number a, Number b) {
		if (a instanceof Num && b instanceof Num) {
			return new Num(gcd(a.toLong(), b.toLong()));
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(ApintMath.gcd(a.toApfloat().floor(), b.toApfloat().floor()));
		}
	}
	
	/** Least common multiple */
	public static Number lcm(Number a, Number b) {
		if (a instanceof Num && b instanceof Num) {
			return new Num(lcm(a.toLong(), b.toLong()));
		} else {
			//At least one is a BigNum, just use their values
			return new BigNum(ApintMath.lcm(a.toApfloat().floor(), b.toApfloat().floor()));
		}
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
	
	/** Integer Factorial */
	public static long factorial(long x) {
		long acc = 1;
		while (x > 0) {
			acc *= x;
			x--;
		}
		return acc;
	}
	
	/** Greatest common denominator (long) */
	public static long gcd(long a, long b) {
		if (a == 0) return Math.abs(b);
		if (b == 0) return Math.abs(a);
		
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
	
	/** Greatest common denominator (int) */
	public static int gcd(int a, int b) {
		int r = 0;
		int x, y;
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
	
	/** least common multiple (long) */
	public static long lcm(long a, long b) {
		return a * b / gcd(a,b);
	}
	
	/** least common multiple (int) */
	public static long lcm(int a, int b) {
		return a * b / gcd(a,b);
	}
	
	public static Num bnot(Number a) {
		return new Num((double)(~(a.toInt())));
	}
	
	public static Num band(Number a, Number b) {
		return new Num((double)(a.toInt() & b.toInt()));
	}
	
	public static Num bor(Number a, Number b) {
		return new Num((double)(a.toInt() | b.toInt()));
	}
	
	public static Num bxor(Number a, Number b) {
		return new Num((double)(a.toInt() ^ b.toInt()));
	}
	
	public static Num leftShift(Number a, Number b) {
		return new Num((double)(a.toInt() << b.toInt()));
	}
	
	public static Num signedRightShift(Number a, Number b) {
		return new Num((double)(a.toInt() >> b.toInt()));
	}
	
	public static Num unsignedRightShift(Number a, Number b) {
		return new Num((double)(a.toInt() >>> b.toInt()));
	}
	
	
}
