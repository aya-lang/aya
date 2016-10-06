package element.entities;

import static element.ElemTypes.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Stack;

import element.exceptions.ElementRuntimeException;

public class ListBuilder {
	
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
				list.add(b.pop());
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
	
	public static ArrayList<Object> buildRange(ArrayList<Object> args) {
		
		switch(args.size()) {
		
		//List range has one argument
		case 1:
			Object o = args.get(0);
			if(isInt(o)) {
				return arrToAL(basicRange(toInt(o)));
			} else if (isDouble(o)) {
				double d = toDouble(o);
				int inc = 1;
				if(d < 0) inc = -1;
				return arrToAL(doubleRange(1, d-1, inc));
			} else if (isBig(o)) {
				BigDecimal bd = toBig(o);
				if(bd.compareTo(BD_ZERO) < 0) {
					return arrToAL(bigDecimalRange(BD_NEG_ONE, bd, BD_NEG_ONE));
				} else {
					return arrToAL(bigDecimalRange(BD_ONE, bd, BD_ONE));
				}
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
			if(bothNum(a,b)) {
				if (anyBig(a,b)) {
					BigDecimal lo = castBig(a);
					BigDecimal hi = castBig(b);
					BigDecimal inc = BD_ONE;
					if(lo.compareTo(hi) > 0) {
						inc = BD_NEG_ONE;
					}
					return arrToAL(bigDecimalRange(lo,hi,inc));
				} else if (anyDouble(a,b)) {
					double lo = castDouble(a);
					double hi = castDouble(b);
					double inc = 1.0;
					if(lo > hi) {
						inc = -1.0;
					}
					return arrToAL(doubleRange(lo,hi,inc));
				}  else if (anyInt(a,b)) {
					int lo = castInt(a);
					int hi = castInt(b);
					int inc = 1;
					if(lo > hi) {
						inc = -1;
					}
					return arrToAL(intRange(lo, hi, inc));
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
			if(bothNum(x,y) && isNum(z)) {
				if(anyBig(x,y) || isBig(z)) {
					return arrToAL(bigDecimalRange(castBig(x), castBig(z), castBig(y).subtract(castBig(x))));
				} else if (anyDouble(x,y) || isDouble(z)) {
					return arrToAL(doubleRange(castDouble(x), castDouble(z), castDouble(y)-castDouble(x)));
				} else /*Ints*/ {
					return arrToAL(intRange(castInt(x), castInt(z), castInt(y)-castInt(x)));
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

	/** Creates a range from 0 to n incrementing by 1. 
	 * If n < 0, create a range from -1 to n. 
	 * If n == 0 create an empty list*/
	public static int[] basicRange(int n) {
		if(n == 1) {
			int[] arr = {1};
			return arr;
		} else if (n < 0) {
			return intRange(-1, n, -1);
		} else {
			return intRange(1, n, 1);
		}
	}
	
	/**
	 * Creates an array of ints from lower to upper using the increment
	 * @throws NegativeArraySizeException if an array cannot be created. EX: Lower: -19, Upper: 4, Inc: -3
	 */
	public static int[] intRange(int lower, int upper, int inc) {
		//Calculate the number of items, this will return a negative value if array creation is impossible
		int numOfItems = 1 + (int) Math.floor((upper-lower)/inc);
		
		if(numOfItems > 10000000) {
			throw new ElementRuntimeException("Cannot create range with more than 10^7 elements"); 
		} else if (numOfItems < 0) {
			throw new ElementRuntimeException("Cannot create range containing a negative number of elements in"
					+ " ["+lower+" "+(lower+inc)+" "+upper+"]" );
		}
		
		int[] arr = new int[numOfItems];
		
		
		
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
	 * Creates an array of doubles from lower to upper using the increment
	 * @throws NegativeArraySizeException if an array cannot be created. EX: Lower: -19, Upper: 4, Inc: -3
	 */
	public static double[] doubleRange(double lower, double upper, double inc) {
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
	 * Creates an array of BigDecimals from lower to upper using the increment
	 * @throws NegativeArraySizeException if an array cannot be created. EX: Lower: -19, Upper: 4, Inc: -3
	 */
	public static BigDecimal[] bigDecimalRange(BigDecimal lower, BigDecimal upper, BigDecimal inc) {
		//Calculate the number of items, this will return a negative value if array creation is impossible
		BigDecimal numOfItemsBD = upper.subtract(lower).divide(inc, MathContext.DECIMAL64).setScale(0, RoundingMode.FLOOR).add(BD_ONE);
		
		//Check for overflow
		if(numOfItemsBD.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) >= 0) {
			throw new ElementRuntimeException("Cannot create range with more than 10^7 elements"); 
		}
		
		int numOfItems = numOfItemsBD.intValue();
		if(numOfItems > 10000000) {
			throw new ElementRuntimeException("Cannot create range with more than 10^7 elements"); 
		} else if (numOfItems < 0) {
			throw new ElementRuntimeException("Cannot create range containing a negative number of elements in"
					+ " ["+lower+" "+(lower.add(inc))+" "+upper+"]" );
		}
		BigDecimal[] arr = new BigDecimal[numOfItems];
		
		//Increment up or down?
		if((lower.compareTo(upper) > 0 && inc.compareTo(BD_ZERO) > 0) || ( lower.compareTo(upper) < 0 && inc.compareTo(BD_ZERO) < 0 )) {
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
	public static char[] charRange(char lower, char upper, int inc) {
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
	public static ArrayList<Object> arrToAL(int[] os) {
		ArrayList<Object> list = new ArrayList<Object>(os.length);
		for(int i = 0; i < os.length; i++) {
			list.add(os[i]);
		}
		return list;
	}
	public static ArrayList<Object> arrToAL(double[] os) {
		ArrayList<Object> list = new ArrayList<Object>(os.length);
		for(int i = 0; i < os.length; i++) {
			list.add(os[i]);
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
