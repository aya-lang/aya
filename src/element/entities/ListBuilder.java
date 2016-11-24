package element.entities;

import static element.ElemTypes.anyNum;
import static element.ElemTypes.anyBigNum;
import static element.ElemTypes.bothChar;
import static element.ElemTypes.bothNumeric;
import static element.ElemTypes.debugString;
import static element.ElemTypes.getString;
import static element.ElemTypes.isNum;
import static element.ElemTypes.isBigNum;
import static element.ElemTypes.isChar;
import static element.ElemTypes.isList;
import static element.ElemTypes.isNumeric;
import static element.ElemTypes.isString;
import static element.ElemTypes.toNum;
import static element.ElemTypes.toBigNum;
import static element.ElemTypes.toChar;
import static element.ElemTypes.toCharList;
import static element.ElemTypes.toList;
import static element.ElemTypes.toNumeric;

import java.util.ArrayList;
import java.util.Stack;

import org.apfloat.Apfloat;

import element.entities.number.Num;
import element.entities.number.BigNum;
import element.exceptions.ElementRuntimeException;

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
	
	public ArrayList<Object> createList(Stack<Object> outerStack) {
		Block initial = initialList.duplicate();

		for (int p = 0; p < pops; p++) {
			initial.add(outerStack.pop());
		}
		
		initial.eval();
		
		ArrayList<Object> res = new ArrayList<Object>();	//Initialize the argument list
		res.addAll(initial.getStack());						//Copy the results into the argument list
		
		boolean allLists = false;							//Check if all arguments are lists
		if(res.size() > 1) {
			allLists = true;								//All arguments may be a list
			for (Object o : res) {
				if(!isList(o)) {
					allLists = false;
					break;
				}
			}
		}
		
		ArrayList<Object> list = null;
		
		//If all arguments are lists, dump each list's respective element onto the stack of the map block
		// [[1 2][3 4], +] => 1 3 +, 2 4 + => [4 6]
		if (allLists) {
			ArrayList<ArrayList<Object>> listArgs = new ArrayList<ArrayList<Object>>(res.size());
			int size = -1;
			
			//Check lengths and cast objects
			for(int i = 0; i < res.size(); i++) {
				listArgs.add(toList(res.get(i)));
				if(size == -1) {
					size = listArgs.get(0).size();
				} else if (size != listArgs.get(i).size()) {
					throw new ElementRuntimeException("List Builder: All lists must be same length");
				}
			}
			
			list = new ArrayList<Object>(size);
			
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
			
		} else {
			list = buildRange(res);								//Create the initial range
			if(map != null) {
				list = this.map.mapTo(list);					//Map 'map' to the list
			}
		}
		
		if(filters != null) {								//Apply the filters to the list
			for (Block filter : filters) {
				list = filter.filter(list);
			}
		}
		return list;
	}
	
	public static ArrayList<Object> buildRange(Num n) {
		double d = n.toDouble();
		int inc = 1;
		if(d < 0) inc = -1;
		return arrToAL(doubleRange(1, d, inc));
	}
	
	public static ArrayList<Object> buildRange(char c) {
		int inc = 1;
		if(c < 0) inc = -1;
		return arrToAL(charRange((char)1, (char)(c), inc));
	}
	
	public static ArrayList<Object> buildRange(BigNum n) {
		Apfloat af = n.toApfloat();
		if(af.compareTo(Apfloat.ZERO) < 0) {
			return arrToAL(apfloatRange(AP_NEG_ONE, af, AP_NEG_ONE));
		} else {
			return arrToAL(apfloatRange(Apfloat.ONE, af, Apfloat.ONE));
		}
	}
	
	public static ArrayList<Object> buildRange(ArrayList<Object> args) {
		
		switch(args.size()) {
		
		//List range has one argument
		case 1:
			Object o = args.get(0);
			if(isNum(o)) {
				return buildRange(toNum(o));
			} else if (isBigNum(o)) {
				return buildRange(toBigNum(o));
			} else if (isChar(o)) {
				return arrToAL(charRange('a', toChar(o), 1));
			} else if (isString(o)) {
				return new ArrayList<Object>(toCharList(getString(o)));
			} else if (isList(o)) {
				return toList(o);
			} else {
				throw new ElementRuntimeException("ListBuilder: Cannot create list from " + debugString(args));
			}
			
		//List range has 2 arguments
		case 2:
			Object a = args.get(0);
			Object b = args.get(1);
			if(bothNumeric(a,b)) {
				if (anyBigNum(a,b)) {
					Apfloat lo = toNumeric(a).toApfloat();
					Apfloat hi = toNumeric(b).toApfloat();
					Apfloat inc = Apfloat.ONE;
					if(lo.compareTo(hi) > 0) {
						inc = AP_NEG_ONE;
					}
					return arrToAL(apfloatRange(lo,hi,inc));
				} else if (anyNum(a,b)) {
					double lo = toNumeric(a).toDouble();
					double hi = toNumeric(b).toDouble();
					double inc = 1.0;
					if(lo > hi) {
						inc = -1.0;
					}
					return arrToAL(doubleRange(lo,hi,inc));
				} else {
					throw new ElementRuntimeException("ListBuilder: Cannot create list from " + debugString(args));
				}
			} else if (bothChar(a,b)) {
				char lo = toChar(a);
				char hi = toChar(b);
				int inc = 1;
				if (lo > hi) {
					inc = -1;
				}
				return arrToAL(charRange(lo,hi,inc));
			} else {
				throw new ElementRuntimeException("ListBuilder: Cannot create list from " + debugString(args));
			}
			
		//List range has 3 arguments
		case 3:
			Object x = args.get(0);
			Object y = args.get(1);
			Object z = args.get(2);
			if(bothNumeric(x,y) && isNumeric(z)) {
				if(anyBigNum(x,y) || isBigNum(z)) {
					return arrToAL(apfloatRange(toNumeric(x).toApfloat(), toNumeric(z).toApfloat(), toNumeric(y).toApfloat().subtract(toNumeric(x).toApfloat())));
				} else {
					return arrToAL(doubleRange(toNumeric(x).toDouble(), toNumeric(z).toDouble(), toNumeric(y).toDouble()-toNumeric(x).toDouble()));
				} 
			} else if(bothChar(x,y) && isChar(z)) {
				return arrToAL(charRange(toChar(x), toChar(z), toChar(y)-toChar(x)));
			} else {
				throw new ElementRuntimeException("ListBuilder: Cannot create list from " + debugString(args));
			}
		
		//List range has 3 or more arguments
		default:
			throw new ElementRuntimeException("ListBuilder: Cannot create list from " + debugString(args));
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
	
	public static ArrayList<Object> arrToAL(Object[] os) {
		ArrayList<Object> list = new ArrayList<Object>(os.length);
		for(int i = 0; i < os.length; i++) {
			list.add(os[i]);
		}
		return list;
	}
	
	public static Object arrToAL(int[] os) {
		ArrayList<Object> list = new ArrayList<Object>(os.length);
		for(int i = 0; i < os.length; i++) {
			list.add(new Num(os[i]));
		}
		return list;
	}
	
	public static ArrayList<Object> arrToAL(double[] os) {
		ArrayList<Object> list = new ArrayList<Object>(os.length);
		for(int i = 0; i < os.length; i++) {
			list.add(new Num(os[i]));
		}
		return list;
	}
	
	public static ArrayList<Object> arrToAL(Apfloat[] os) {
		ArrayList<Object> list = new ArrayList<Object>(os.length);
		for(int i = 0; i < os.length; i++) {
			list.add(new BigNum(os[i]));
		}
		return list;
	}
	
	
	
	public static ArrayList<Object> arrToAL(char[] os) {
		ArrayList<Object> list = new ArrayList<Object>(os.length);
		for(int i = 0; i < os.length; i++) {
			list.add(os[i]);
		}
		return list;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append(initialList + ", ");
		if(map != null) {
			sb.append(map + ", ");
		}
		if(filters != null) {
			for (Block b : filters) {
				sb.append(b + ", ");
			}
		}
		sb.setLength(sb.length()-2);
		sb.append("]");
		return sb.toString();
	}

	
}
