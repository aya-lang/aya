package aya.obj.list;

import static aya.util.Casting.asList;

import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.character.Char;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Number;
import aya.obj.number.NumberMath;

public class ListRangeUtils {

	public static NumberList buildRange(Number n) {
		if(n.compareTo(n.zero()) < 0) {
			// :4 R => [-4 -3 -2 -1]
			return NumberList.range(n, n.negOne(), n.one());
		} else {
			// r R => [1 2 3 4]
			return NumberList.range(n.one(), n, n.one());
		}
	}

	
	public static NumberList buildRange(Number lo, Number hi) {
		Number inc = lo.one();
		if(lo.compareTo(hi) > 0) inc = lo.negOne();
		return NumberList.range(lo, hi, inc);
	}
	
	
	public static Str buildRange(char c) {
		int inc = 1;
		if(c < 0) inc = -1;
		return new Str(charRange((char)1, (char)(c), inc));
	}
	
	
	/**
	 * Creates an array of chars from lower to upper using the increment
	 * @throws NegativeArraySizeException if an array cannot be created. EX: Lower: -19, Upper: 4, Inc: -3
	 */
	private static char[] charRange(char lower, char upper, int inc) {
		//Calculate the number of items, this will return a negative value if array creation is impossible
		int numOfItems = 1 + (int) Math.floor((upper-lower)/inc);
		
		if(numOfItems > 10000000) {
			throw new ValueError("Cannot create range with more than 10^7 elements"); 
		} else if (numOfItems < 0) {
			throw new ValueError("Cannot create range containing a negative number of elements in"
					+ " ["+lower+" "+(lower+inc)+" "+upper+"]" );
		}
		
		char[] arr = new char[numOfItems];
		
		//Increment up or down?
		if((lower > upper && inc > 0) || (lower < upper && inc < 0)) {
			for(int i = 0; i < arr.length; i++, lower -= inc) {
				arr[i] = lower;
			}
		} else {
			for(int i = 0; i < arr.length; i++, lower += inc) {
				arr[i] = lower;
			}
		}
		
		return arr;
	}
	
	
	public static ListImpl buildRange(List args) {
		
		switch(args.length()) {
		
		//List range has one argument
		case 1:
			Obj o = args.getExact(0);
			if (o.isa(Obj.NUMBER)) {
				return buildRange((Number)o);
			}
			else if (o.isa(Obj.CHAR)) {
				return new Str(charRange('a', ((Char)o).charValue(), 1));
			}
			else if (o.isa(Obj.LIST)) {
				return asList(o).impl();
			} else {
				throw new ValueError("ListBuilder: Cannot create list from " + args.repr());	
			}

		//List range has 2 arguments
		case 2:
			Obj a = args.getExact(0);
			Obj b = args.getExact(1);
			if(a.isa(Obj.NUMBER) && b.isa(Obj.NUMBER)) {
				return buildRange((Number)a, (Number)b);
			} else if (a.isa(Obj.CHAR) && b.isa(Obj.CHAR)) {
				char lo = ((Char)a).charValue();
				char hi = ((Char)b).charValue();
				int inc = 1;
				if (lo > hi) {
					inc = -1;
				}
				return new Str(charRange(lo,hi,inc));
			} else {
				throw new ValueError("ListBuilder: Cannot create list from " + args.repr());
			}
			
		//List range has 3 arguments
		case 3:
			Obj x = args.getExact(0);
			Obj y = args.getExact(1);
			Obj z = args.getExact(2);
			if(x.isa(Obj.NUMBER) && y.isa(Obj.NUMBER) && z.isa(Obj.NUMBER)) {
				Number lo = (Number)x;
				Number hi = (Number)z;
				Number inc = NumberMath.sub((Number)y, lo);
				return NumberList.range(lo, hi, inc);
			} else if(x.isa(Obj.CHAR) || y.isa(Obj.CHAR) || z.isa(Obj.CHAR)) {
				return new Str(charRange(((Char)x).charValue(), ((Char)z).charValue(), ((Char)y).charValue() - (((Char)x)).charValue()));
			} else {
				throw new ValueError("ListBuilder: Cannot create list from " + args.repr());
			}
		
		//List range has 3 or more arguments
		default:
			throw new ValueError("ListBuilder: Cannot create list from " + args.repr());
		}
	}	
	
	
}
