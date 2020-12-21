package aya.obj.list;

import java.util.ArrayList;

import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;

public class ListAlgorithms {

	public static <T extends Obj> ArrayList<T> head(ArrayList<T> list, int n, T pad) {
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

	public static <T extends Obj> ArrayList<T> tail(ArrayList<T> list, int n, T pad) {
		ArrayList<T> out = new ArrayList<T>(n);
		if (n <= list.size()) {
			for (int i = list.size()-n; i < list.size(); i++) {
				out.add(list.get(i));
			}
		} else {
			for (int i = 0; i < n-list.size(); i++) {
				out.add(pad); //Pad with 0s
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

	public static int find(ArrayList<? extends Obj> list, Obj o) {
		int ix;
		for (ix = 0; ix < list.size(); ix++) {
			if (o.equiv(list.get(ix))) {
				return ix;
			}
		}
		return -1;
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
			throw new AyaRuntimeException("Cannot slice list at indices " + i + " and " + j + ".");
		}

		ArrayList<T> out = new ArrayList<T>(j - i);
		for (int x = i; x < j; x++) {
			out.add(list.get(x));
		}
		return out;
	}
    
    public static <T extends Obj> String repr(ArrayList<T> list) {
		StringBuilder sb = new StringBuilder("[ ");
		
		if (list.size() < 50) {
			//Output the whole list
			for (T o : list) {
				sb.append(o.repr() + " ");
			}
		} else {
			// Output 10 front
			for (int i = 0; i < 10; i++) {
				sb.append(list.get(i).repr() + " ");
			}
			
			sb.append(" ... ");
			
			// Output 10 back
			for (int i = list.size() - 11; i < list.size(); i++) {
				sb.append(list.get(i).repr() + " ");
			}
		}
		
		return sb.append(']').toString();
    }
	
	public static <T extends Obj> String str(ArrayList<T> list) {
		StringBuilder sb = new StringBuilder("[ ");
		for (T o : list) {
			sb.append(o.repr() + " ");
		}
		return sb.append(']').toString();
	}

}
