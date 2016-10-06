package element.entities.operations;

import java.math.BigDecimal;
import java.util.Arrays;

public class ElementMath {
	//private static final BigDecimal BD_ONE = new BigDecimal("1");
	
	/** logGamma: Uses Lanczos approximation formula */ 
	static double logGamma(double x) {
		 double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
		 double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
				 + 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
				 +  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
		 return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
	   }
	
	/** Gamma Function */
	static double gamma(double x) { return Math.exp(logGamma(x)); }
	
	/** Factorial */
	static double factorial(double x) {return gamma(x+1);}
	
	static BigDecimal factorial(int i) {
		BigDecimal acc = new BigDecimal("1");
		while(i > 0) {
			acc = acc.multiply(BigDecimal.valueOf(i));
			i--;
		}
		return acc;
	}
	
	public static int addExact(int n, int m) {
		return Math.addExact(n, m);
	}
	
	public static int multiplyExact(int n, int m) {
		return Math.multiplyExact(n, m);
	}
	
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
	
	/** lcm */
	public static int lcm(int a, int b) {
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
	
	/** isprime */
	public static boolean isPrime(int n) {
	    if(n < 2) return false;
	    if(n == 2 || n == 3) return true;
	    if(n%2 == 0 || n%3 == 0) return false;
	    int sqrtN = (int)Math.sqrt(n)+1;
	    for(int i = 6; i <= sqrtN; i += 6) {
	        if(n%(i-1) == 0 || n%(i+1) == 0) return false;
	    }
	    return true;
	}
	
	/** Primes up to n **/
	public static int[] primes(int n) {
		boolean[] flags = new boolean[n];
		Arrays.fill(flags, true);
		
		//Mark every space that is not prime
		for (int i = 2; i <= n; i++) {
			int z = 2*i;
			while (z <= n) {
				flags[z-1] = false; //zero index, so sub 1
				z += i;
			}
		}
	
		//Count the number of primes
		int primeCount = 0;
		for (int i = 2; i <= n; i++) {
			primeCount += flags[i-1] ? 1 : 0;
		}
		
		//Allocate new array
		int [] primes = new int[primeCount];
		
		//Add primes into array
		int ix = 0;
		for (int i = 2; i <= n; i++) {
			if (flags[i-1]) {
				primes[ix] = i;
				ix++;
			}
		}
		
		return primes;
	}
}
