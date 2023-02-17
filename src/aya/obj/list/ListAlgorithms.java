package aya.obj.list;

import java.util.ArrayList;
import java.util.Collections;

import aya.ReprStream;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.number.Num;
import aya.obj.number.Number;

public class ListAlgorithms {

	/*
	 * Two head functions are provided:
	 *   headNoDeepcopyPad: Use this function if no objects in the list are containers.
	 *                      This function does not call deepcopy on the filler
	 *   headDeepcopyPad:   Use this function if objects may or may not be containers.
	 * 			            deepcopy is called on all filler objects
	 */

	public static <T extends Obj> ArrayList<T> headNoDeepcopyPad(ArrayList<T> list, int n, T pad) {
		ArrayList<T> out = new ArrayList<T>(n);
		
		if (n <= list.size()) {
			for (int i = 0; i < n; i++) {
				out.add(list.get(i));
			}
		} else {
			out.addAll(list);
			for (int i = list.size(); i < n; i++) {
				out.add(pad);
			}
		}
		return out;
	}

	public static ArrayList<Obj> headDeepcopyPad(ArrayList<Obj> list, int n, Obj pad) {
		ArrayList<Obj> out = new ArrayList<Obj>(n);
		
		if (n <= list.size()) {
			for (int i = 0; i < n; i++) {
				out.add(list.get(i));
			}
		} else {
			out.addAll(list);
			for (int i = list.size(); i < n; i++) {
				out.add(pad.deepcopy());
			}
		}
		return out;
	}

	public static double[] headNoDeepcopyPad(double[] list, int n, double pad) {
		double[] out = new double[n];
		if (n <= list.length) {
			for (int i = 0; i < n; i++) {
				out[i] = list[i];
			}
		} else {
			int ix = 0;
			for (int i = 0; i < list.length; i++) {
				out[ix] = list[i];
				ix++;
			}
			for (int i = list.length; i < n; i++) {
				out[ix]	= pad;
				ix++;
			}
		}
		return out;
	}



	public static <T extends Obj> ArrayList<T> tailNoDeepcopyPad(ArrayList<T> list, int n, T pad) {
		ArrayList<T> out = new ArrayList<T>(n);
		if (n <= list.size()) {
			for (int i = list.size()-n; i < list.size(); i++) {
				out.add(list.get(i));
			}
		} else {
			for (int i = 0; i < n-list.size(); i++) {
				out.add(pad);
			}
			out.addAll(list);
		}	
		return out;
	}


	public static double[] tailNoDeepcopyPad(double[] list, int n, double pad) {
		double[] out = new double[n];
		if (n <= list.length) {
			int ix = 0;
			for (int i = list.length-n; i < list.length; i++) {
				out[ix] = list[i];
				ix++;
			}
		} else {
			int ix = 0;
			for (int i = 0; i < n-list.length; i++) {
				out[ix]	= pad;
				ix++;
			}
			// copy the rest
			int i = 0;
			while (ix < out.length) {
				out[ix] = list[i];
				ix++;
				i++;
			}
		}
		return out;
	}


	public static ArrayList<Obj> tailDeepcopyPad(ArrayList<Obj> list, int n, Obj pad) {
		ArrayList<Obj> out = new ArrayList<Obj>(n);
		if (n <= list.size()) {
			for (int i = list.size()-n; i < list.size(); i++) {
				out.add(list.get(i));
			}
		} else {
			for (int i = 0; i < n-list.size(); i++) {
				out.add(pad.deepcopy());
			}
			out.addAll(list);
		}	
		return out;
	}

	public static void removeAll(ArrayList<?> list, int[] ixs) {
		for (int i = 0; i < ixs.length; i++) {
			list.set(ixs[i], null);
		}
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == null) {
				list.remove(i);
				i--;
			}
		}
	}

	/**
	 * Search for an object in a list
	 * @param list
	 * @param o
	 * @return The index of the first item found. If no item return -(list.length()+1)
	 */
	public static int find(ArrayList<? extends Obj> list, Obj o) {
		int ix;
		for (ix = 0; ix < list.size(); ix++) {
			if (o.equiv(list.get(ix))) {
				return ix;
			}
		}
		return -(list.size()+1);
	}

	/**
	 * Search for an object in a list
	 * @param list
	 * @param o
	 * @return The index of the first item found. If no item return -(list.length()+1)
	 */
	public static ArrayList<Number> findAll(ArrayList<? extends Obj> list, Obj o) {
		ArrayList<Number> out = new ArrayList<Number>();
		for (int ix = 0; ix < list.size(); ix++) {
			if (o.equiv(list.get(ix))) {
				out.add(Num.fromInt(ix));
			}
		}
		return out;
	}

	public static int findBack(ArrayList<? extends Obj> list, Obj o) {
		int ix;
		for (ix = list.size() - 1; ix >= 0; ix--) {
			if (o.equiv(list.get(ix))) {
				return ix;
			}
		}
		return -1;
	}

	public static int count(ArrayList<? extends Obj> list, Obj o) {
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			count += list.get(i).equiv(o) ? 1 : 0;
		}
		return count;
	}

	public static int count(double[] list, double o) {
		int count = 0;
		for (int i = 0; i < list.length; i++) {
			count += list[i] == o ? 1 : 0;
		}
		return count;
	}
	
    public static <T extends Obj> ArrayList<T> unique(ArrayList<T> list) {
    	ArrayList<T> unique = new ArrayList<T>();
		for (T l : list) {
			boolean alreadyContains = false;
			for (T u : unique) {
				if (l.equiv(u)) {
					alreadyContains = true;
					break;
				}
			}
			if (!alreadyContains) {
				unique.add(l);
			}
		}
		return unique;
    }

	public static <T extends Obj> ArrayList<T> slice(ArrayList<T> list, int i, int j) {
		if (i >= j) {
			throw new ValueError("Cannot slice list at indices " + i + " and " + j + ".");
		}

		ArrayList<T> out = new ArrayList<T>(j - i);
		for (int x = i; x < j; x++) {
			out.add(list.get(x));
		}
		return out;
	}
	
	
	public static <T extends Obj> ArrayList<ArrayList<T>> split(ArrayList<T> list, T o) {
		ArrayList<ArrayList<T>> out = new ArrayList<ArrayList<T>>();
		ArrayList<T> current = new ArrayList<T>();
	
		for (T item : list) {
			if (item.equiv(o)) {
				out.add(current);
				current = new ArrayList<T>();
			} else {
				current.add(item);
			}
		}
		
		if (current.size() != 0) out.add(current);

		return out;
	}
    
    public static <T extends Obj> ReprStream repr(ReprStream stream, ArrayList<T> list) {
    	// Does this list have containers? If not, print compact
    	if (!hasContainers(list)) {
    		return reprCompact(stream, list);
    	} else {
			stream.println("[");
			stream.incIndent();
			stream.currentLineMatchIndent();

			for (T o : list) {
				o.repr(stream);
				stream.println();
			}
			stream.delTrailingNewline();
			
			stream.decIndent();
			stream.println();
			stream.print("]");
			return stream;
		}
    }

    private static <T extends Obj> boolean hasContainers(ArrayList<T> list) {
    	for (T o : list) {
    		if ( (o.isa(Obj.LIST) && !o.isa(Obj.STR)) || o.isa(Obj.DICT) ) {
    			return true;
    		}
    	}
    	return false;
	}

	public static <T extends Obj> ReprStream reprCompact(ReprStream stream, ArrayList<T> list) {
		stream.print("[ ");
		for (T o : list) {
			o.repr(stream);
			stream.print(" ");
		}
		stream.print("]");
		return stream;
    }

	public static ReprStream reprCompact(ReprStream stream, double[] list) {
		stream.print("[ ");
		for (double o : list) {
			new Num(o).repr(stream);
			stream.print(" ");
		}
		stream.print("]");
		return stream;
    }
	
	public static <T extends Obj> String str(ArrayList<T> list) {
		StringBuilder sb = new StringBuilder("[ ");
		for (T o : list) {
			sb.append(o.repr(new ReprStream()) + " ");
		}
		return sb.append(']').toString();
	}

	public static <T extends Obj> void rotate(ArrayList<T> list, int n) {
		Collections.rotate(list, n);
	}
}
