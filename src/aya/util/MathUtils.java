package aya.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import aya.obj.number.BigNum;
import ch.obermuhlner.math.big.BigDecimalMath;

public class MathUtils {
	
	
	private static final BigInteger BI_ZERO = BigInteger.ZERO;
	private static final BigInteger BI_ONE = BigInteger.ONE;
	private static final BigInteger BI_TWO = BigInteger.valueOf(2);
	private static final BigInteger BI_THREE = BigInteger.valueOf(3);
	private static final BigInteger BI_SIX = BigInteger.valueOf(6);

	public static int signnum(double val) {
		if (val == 0.0) {
			return 0;
		} else if (val > 0.0) {
			return 1;
		} else {
			return -1;
		}
	}
	
	/** Primality Test */
	// https://stackoverflow.com/questions/2385909/what-would-be-the-fastest-method-to-test-for-primality-in-java
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
	
	/** Primality Test */
	public static boolean isPrime(BigInteger n) {
		if (!n.isProbablePrime(64)) return false;
	    if(n.compareTo(BI_TWO) < 0) return false;
	    if(n.equals(BI_TWO) || n.equals(BI_THREE)) return true;
	    if(n.mod(BI_TWO).equals(BI_ZERO) || n.mod(BI_THREE).equals(BI_ZERO)) return false;
	    BigInteger sqrtN = BigDecimalMath.sqrt(new BigDecimal(n), BigNum.MC).toBigInteger();
	    for(BigInteger i = BI_SIX; i.compareTo(sqrtN) <= 0; i = i.add(BI_SIX)) {
	        //  n%(i-1) == 0 || n%(i+1) == 0
	        if (n.mod(i.subtract(BI_ONE)).equals(BI_ZERO) || n.mod(i.add(BI_ONE)).equals(BI_ZERO)) return false;
	    }
	    return true;
	}	
	
	
	/** Factorial (long) */
	public static long factorial(long x) {
		long acc = 1;
		while (x > 0) {
			acc *= x;
			x--;
		}
		return acc;
	}

	public static BigDecimal factorial(BigDecimal x) {
		return new BigDecimal(factorial(x.toBigInteger()));
	}

	public static BigInteger factorial(BigInteger x) {
		BigInteger out = BI_ONE;
		int max = x.intValue();
		for (int i = 2; i <= max; i++) {
			out = out.multiply(BigInteger.valueOf(i));
		}
		return out;
	}
	
	/** GCD (long) */
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

	/** GCD (BigDecimal) */
	public static BigDecimal gcd(BigDecimal a, BigDecimal b) {
		return new BigDecimal(a.toBigInteger().gcd(b.toBigInteger()));
		
		
	}

	/** LCM (long) */
	public static long lcm(long a, long b) {
		return a * b / gcd(a,b);
	}
	
	public static BigDecimal lcm(BigDecimal a, BigDecimal b) {
		return a.multiply(b).divide(gcd(a, b));
	}
}
