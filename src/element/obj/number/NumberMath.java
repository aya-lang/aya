package element.obj.number;

public class NumberMath {
	
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
}
