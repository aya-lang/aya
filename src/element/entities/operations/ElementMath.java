package element.entities.operations;

import java.math.BigDecimal;
import java.util.Arrays;

public class ElementMath {
	
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
