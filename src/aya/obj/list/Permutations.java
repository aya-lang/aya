package aya.obj.list;

import java.util.ArrayList;

import aya.obj.Obj;
import aya.obj.character.Char;
import aya.obj.list.numberlist.DoubleList;
import aya.obj.number.Num;

public class Permutations {

	// Java Generics == :(
	// We need to define implementations for char[], double[], and ArrayList<Obj>

	private static void swap(char[] input, int a, int b) {
	    char tmp = input[a];
	    input[a] = input[b];
	    input[b] = tmp;
	}

	private static void swap(double[] input, int a, int b) {
	    double tmp = input[a];
	    input[a] = input[b];
	    input[b] = tmp;
	}
	
	private static <T extends Obj> void swap(ArrayList<T> input, int a, int b) {
		T tmp = input.get(a);
		input.set(a, input.get(b));
		input.set(b,  tmp);
	}

	private static void permutations(List agg, int n, char[] elements) {
	    if (n == 1) {
	    	agg.mutAdd(List.fromString(new String(elements)));
	    } else {
	        for (int i = 0; i < n - 1; i++) {
	            permutations(agg, n-1, elements);
	            if (n % 2 == 0) {
	                swap(elements, i, n - 1);
	            } else {
	                swap(elements, 0, n - 1);
	            }
	        }
	        permutations(agg, n-1, elements);
	    }
	}
	
	private static void permutations(List agg, int n, double[] elements) {
	    if (n == 1) {
	    	agg.mutAdd(new List(DoubleList.copyOf(elements)));
	    } else {
	        for (int i = 0; i < n - 1; i++) {
	            permutations(agg, n-1, elements);
	            if (n % 2 == 0) {
	                swap(elements, i, n - 1);
	            } else {
	                swap(elements, 0, n - 1);
	            }
	        }
	        permutations(agg, n-1, elements);
	    }
	}
	
	private static <T extends Obj> void permutations(List agg, int n, ArrayList<T> elements) {
	    if (n == 1) {
	    	ArrayList<Obj> copy = new ArrayList<Obj>(elements.size());
	    	copy.addAll(elements);
	    	agg.mutAdd(new List(new GenericList(copy).promote()));
	    } else {
	        for (int i = 0; i < n - 1; i++) {
	            permutations(agg, n-1, elements);
	            if (n % 2 == 0) {
	                swap(elements, i, n - 1);
	            } else {
	                swap(elements, 0, n - 1);
	            }
	        }
	        permutations(agg, n-1, elements);
	    }
	}
	
	
	public static List allPermutations(char[] elements) {
		List perms = new List();
		permutations(perms, elements.length, elements);
		return perms;
	}

	
	public static List allPermutations(double[] elements) {
		List perms = new List();
		permutations(perms, elements.length, elements);
		return perms;
	}
	
	
	public static <T extends Obj> List allPermutations(ArrayList<T> elements) {
		List perms = new List();
		permutations(perms, elements.size(), elements);
		return perms;
	}

	
	public static void main(String[] args) {
		System.out.println(allPermutations(new String("abc").toCharArray()).repr());
		double[] double_test = {1.0, 2.0, 3.0};
		System.out.println(allPermutations(double_test).repr());
		ArrayList<Obj> l = new ArrayList<Obj>();
		l.add(Num.ONE);
		l.add(Char.valueOf('c'));
		l.add(new List());
		System.out.println(allPermutations(l).repr());
	}

}
