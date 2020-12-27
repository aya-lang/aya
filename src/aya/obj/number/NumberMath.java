package aya.obj.number;

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
	
	public static Number gcd(Number a, Number b) {
		NumberPair pair = promote(a, b);
		return pair.a.gcd(pair.b);
	}

	public static Number lcm(Number a, Number b) {
		NumberPair pair = promote(a, b);
		return pair.a.lcm(pair.b);
	}
	
	/** Greatest common denominator */
//	public static Number gcd(Number a, Number b) {
//		if (a instanceof Num && b instanceof Num) {
//			return new Num(gcd(a.toLong(), b.toLong()));
//		} else {
//			//At least one is a BigNum, just use their values
//			return new BigNum(a.toBigDecimal().toBigInteger().gcd(b.toBigDecimal().toBigInteger()));
//		}
//	}
	
	/** Least common multiple */
//	public static Number lcm(Number a, Number b) {
//		if (a instanceof Num && b instanceof Num) {
//			return new Num(lcm(a.toLong(), b.toLong()));
//		} else {
//			//At least one is a BigNum, just use their values
//			return new BigNum(a.toBigDecimal().toBigInteger().lcm(b.toBigDecimal().toBigInteger()));
//		}
//	}
//	
	
	
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
