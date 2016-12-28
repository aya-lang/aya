package element.entities;

import java.util.ArrayList;
import java.util.Stack;

import org.apfloat.Apfloat;

import element.exceptions.ElementRuntimeException;
import element.obj.Obj;
import element.obj.character.Char;
import element.obj.list.List;
import element.obj.list.ObjList;
import element.obj.list.Str;
import element.obj.list.numberlist.NumberItemList;
import element.obj.number.BigNum;
import element.obj.number.Num;
import element.obj.number.Number;

public class ListBuilder {
	
	private static final Apfloat AP_NEG_ONE = Apfloat.ONE.multiply(new Apfloat(-1));
	private static final Apfloat AP_MAX_INT = new Apfloat(Integer.MAX_VALUE);

	
	private Block initialList;
	private Block map;
	private Block[] filters;
	private int pops;

	public ListBuilder(Block initial, Block map, Block[] filters, int pops) {
		this.initialList = initial;
		this.map = map;
		this.filters = filters;
		this.pops = pops;
	}
	
	public List createList(Stack<Obj> outerStack) {
		Block initial = initialList.duplicate();

		for (int p = 0; p < pops; p++) {
			initial.add(outerStack.pop());
		}
		
		initial.eval();
		
		ArrayList<Obj> res = new ArrayList<Obj>();			//Initialize the argument list
		res.addAll(initial.getStack());						//Copy the results into the argument list
		
		boolean allLists = false;							//Check if all arguments are lists
		if(res.size() > 1) {
			allLists = true;								//All arguments may be a list
			for (Obj o : res) {
				if(!o.isa(Obj.LIST)) {
					allLists = false;
					break;
				}
			}
		}
		
		ArrayList<Obj> list = null;
		List outList = null;
		
		//If all arguments are lists, dump each list's respective element onto the stack of the map block
		// [[1 2][3 4], +] => 1 3 +, 2 4 + => [4 6]
		if (allLists) {
			ArrayList<List> listArgs = new ArrayList<List>(res.size());
			int size = -1;
			
			//Check lengths and cast objects
			for(int i = 0; i < res.size(); i++) {
				listArgs.add((List)(res.get(i)));
				if(size == -1) {
					size = listArgs.get(0).length();
				} else if (size != listArgs.get(i).length()) {
					throw new ElementRuntimeException("List Builder: All lists must be same length");
				}
			}
			
			list = new ArrayList<Obj>(size);
			
			//Dump items from the lists into the blocks and apply the map if needed
			for(int i = 0; i < size; i++) {
				Block b = new Block();
				for (int j = 0; j < listArgs.size(); j++) {
					b.push(listArgs.get(j).get(i));
				}
				
				//Apply the map
				if(map != null) {
					b.addAll(map.getInstructions().getInstrucionList());
					b.eval();		
				}
				list.addAll(b.getStack());
			}
			
			outList = new ObjList(list).promote();
			
		} else {
			outList = buildRange(new ObjList(res));							//Create the initial range
			if(map != null) {
				outList = this.map.mapTo(outList);				//Map 'map' to the list
			}
		}
		
		if(filters != null) {								//Apply the filters to the list
			for (Block filter : filters) {
				outList = filter.filter(outList);
			}
		}
		return outList;
	}
	
	public static NumberItemList buildRange(Num n) {
		double d = n.toDouble();
		int inc = 1;
		if(d < 0) inc = -1;
		return arrToAL(doubleRange(1, d, inc));
	}
	
	public static Str buildRange(char c) {
		int inc = 1;
		if(c < 0) inc = -1;
		return arrToAL(charRange((char)1, (char)(c), inc));
	}
	
	public static NumberItemList buildRange(BigNum n) {
		Apfloat af = n.toApfloat();
		if(af.compareTo(Apfloat.ZERO) < 0) {
			return arrToAL(apfloatRange(AP_NEG_ONE, af, AP_NEG_ONE));
		} else {
			return arrToAL(apfloatRange(Apfloat.ONE, af, Apfloat.ONE));
		}
	}
	
	public static List buildRange(List args) {
		
		switch(args.length()) {
		
		//List range has one argument
		case 1:
			Obj o = args.get(0);
			switch (o.type()) {
			case Obj.NUM:
				return buildRange((Num)o);
			case Obj.BIGNUM:
				return buildRange((BigNum)o);
			case Obj.CHAR:
				return arrToAL(charRange('a', ((Char)o).charValue(), 1));
			case Obj.STR:
				return (List)o;
			case Obj.LIST:
				return (List)o;
			default:
				throw new ElementRuntimeException("ListBuilder: Cannot create list from " + args.repr());	
			}

		//List range has 2 arguments
		case 2:
			Obj a = args.get(0);
			Obj b = args.get(1);
			if(a.isa(Obj.NUMBER) && b.isa(Obj.NUMBER)) {
				if (a.isa(Obj.BIGNUM) || b.isa(Obj.BIGNUM)) {
					Apfloat lo = ((Number)a).toApfloat();
					Apfloat hi = ((Number)b).toApfloat();
					Apfloat inc = Apfloat.ONE;
					if(lo.compareTo(hi) > 0) {
						inc = AP_NEG_ONE;
					}
					return arrToAL(apfloatRange(lo,hi,inc));
				} else if (a.isa(Obj.NUM) || b.isa(Obj.NUM)) {
					double lo = ((Number)a).toDouble();
					double hi = ((Number)b).toDouble();
					double inc = 1.0;
					if(lo > hi) {
						inc = -1.0;
					}
					return arrToAL(doubleRange(lo,hi,inc));
				} else {
					throw new ElementRuntimeException("ListBuilder: Cannot create list from " + args.repr());
				}
			} else if (a.isa(Obj.CHAR) && b.isa(Obj.CHAR)) {
				char lo = ((Char)a).charValue();
				char hi = ((Char)b).charValue();
				int inc = 1;
				if (lo > hi) {
					inc = -1;
				}
				return arrToAL(charRange(lo,hi,inc));
			} else {
				throw new ElementRuntimeException("ListBuilder: Cannot create list from " + args.repr());
			}
			
		//List range has 3 arguments
		case 3:
			Obj x = args.get(0);
			Obj y = args.get(1);
			Obj z = args.get(2);
			if(x.isa(Obj.NUMBER) && y.isa(Obj.NUMBER) && z.isa(Obj.NUMBER)) {
				if(x.isa(Obj.BIGNUM) || y.isa(Obj.BIGNUM) || z.isa(Obj.BIGNUM)) {
					return arrToAL(apfloatRange(((Number)x).toApfloat(), ((Number)z).toApfloat(), ((Number)y).toApfloat().subtract(((Number)x).toApfloat())));
				} else {
					return arrToAL(doubleRange(((Number)x).toDouble(), ((Number)z).toDouble(), ((Number)y).toDouble()-((Number)x).toDouble()));
				} 
			} else if(x.isa(Obj.CHAR) || y.isa(Obj.CHAR) || z.isa(Obj.CHAR)) {
				return arrToAL(charRange(((Char)x).charValue(), ((Char)z).charValue(), ((Char)y).charValue() - (((Char)x)).charValue()));
			} else {
				throw new ElementRuntimeException("ListBuilder: Cannot create list from " + args.repr());
			}
		
		//List range has 3 or more arguments
		default:
			throw new ElementRuntimeException("ListBuilder: Cannot create list from " + args.repr());
		}
	}
	
	
	/**
	 * Creates an array of doubles from lower to upper using the increment
	 * @throws NegativeArraySizeException if an array cannot be created. EX: Lower: -19, Upper: 4, Inc: -3
	 */
	private static double[] doubleRange(double lower, double upper, double inc) {
		//Calculate the number of items, this will return a negative value if array creation is impossible
		double numOfItemsDouble = 1 + Math.floor((upper-lower)/inc);
		
		//Check for overflow
		if(numOfItemsDouble > Integer.MAX_VALUE) {
			throw new ElementRuntimeException("Cannot create range with more than 10^7 elements"); 
		}
		
		int numOfItems = (int)numOfItemsDouble;
		
		if(numOfItems > 10000000) {
			throw new ElementRuntimeException("Cannot create range with more than 10^7 elements"); 
		} else if (numOfItems < 0) {
			throw new ElementRuntimeException("Cannot create range containing a negative number of elements in"
					+ " ["+lower+" "+(lower+inc)+" "+upper+"]" );
		}
		
		double[] arr = new double[numOfItems];
		
		
		
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
	
	/**
	 * Creates an array of Apfloats from lower to upper using the increment
	 * @throws NegativeArraySizeException if an array cannot be created. EX: Lower: -19, Upper: 4, Inc: -3
	 */
	private static Apfloat[] apfloatRange(Apfloat lower, Apfloat upper, Apfloat inc) {
		//Calculate the number of items, this will return a negative value if array creation is impossible
		Apfloat numOfItemsBD = upper.subtract(lower).divide(inc).floor().add(Apfloat.ONE);
		
		//Check for overflow
		if(numOfItemsBD.compareTo(AP_MAX_INT) >= 0) {
			throw new ElementRuntimeException("Cannot create range with more than 10^7 elements"); 
		}
		
		int numOfItems = numOfItemsBD.intValue();
		if(numOfItems > 10000000) {
			throw new ElementRuntimeException("Cannot create range with more than 10^7 elements"); 
		} else if (numOfItems < 0) {
			throw new ElementRuntimeException("Cannot create range containing a negative number of elements in"
					+ " ["+lower+" "+(lower.add(inc))+" "+upper+"]" );
		}
		Apfloat[] arr = new Apfloat[numOfItems];
		
		//Increment up or down?
		if((lower.compareTo(upper) > 0 && inc.compareTo(Apfloat.ZERO) > 0) || ( lower.compareTo(upper) < 0 && inc.compareTo(Apfloat.ZERO) < 0 )) {
			for(int i = 0; i < arr.length; i++, lower = lower.subtract(inc)) {
				arr[i] = lower;
			}
		} else {
			for(int i = 0; i < arr.length; i++, lower = lower.add(inc)) {
				arr[i] = lower;
			}
		}
		
		return arr;
	}
	
	/**
	 * Creates an array of chars from lower to upper using the increment
	 * @throws NegativeArraySizeException if an array cannot be created. EX: Lower: -19, Upper: 4, Inc: -3
	 */
	private static char[] charRange(char lower, char upper, int inc) {
		//Calculate the number of items, this will return a negative value if array creation is impossible
		int numOfItems = 1 + (int) Math.floor((upper-lower)/inc);
		
		if(numOfItems > 10000000) {
			throw new ElementRuntimeException("Cannot create range with more than 10^7 elements"); 
		} else if (numOfItems < 0) {
			throw new ElementRuntimeException("Cannot create range containing a negative number of elements in"
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
	
	public static List arrToAL(Obj[] os) {
		ArrayList<Obj> list = new ArrayList<Obj>(os.length);
		for(int i = 0; i < os.length; i++) {
			list.add(os[i]);
		}
		return new ObjList(list);
	}
	
	public static Obj arrToAL(int[] os) {
		ArrayList<Number> list = new ArrayList<Number>(os.length);
		for(int i = 0; i < os.length; i++) {
			list.add(new Num(os[i]));
		}
		return new NumberItemList(list);
	}
	
	public static NumberItemList arrToAL(double[] os) {
		ArrayList<Number> list = new ArrayList<Number>(os.length);
		for(int i = 0; i < os.length; i++) {
			list.add(new Num(os[i]));
		}
		return new NumberItemList(list);
	}
	
	public static NumberItemList arrToAL(Apfloat[] os) {
		ArrayList<Number> list = new ArrayList<Number>(os.length);
		for(int i = 0; i < os.length; i++) {
			list.add(new BigNum(os[i]));
		}
		return new NumberItemList(list);
	}
	
	
	
	public static Str arrToAL(char[] os) {
		return new Str(new String(os));
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append(initialList.toString(false) + ", ");
		if(map != null) {
			sb.append(map.toString(false) + ", ");
		}
		if(filters != null) {
			for (Block b : filters) {
				sb.append(b.toString(false) + ", ");
			}
		}
		sb.setLength(sb.length()-2);
		sb.append("]");
		return sb.toString();
	}

	
}
