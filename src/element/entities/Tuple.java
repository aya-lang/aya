package element.entities;

import java.util.ArrayList;
import java.util.EmptyStackException;

public class Tuple {	
	Block[] elements;
	
	/** 
	 * Tuples will not need to be resized during runtime
	 * @param blocks
	 */
	public Tuple(Block[] blocks) {
		elements = blocks;
	}
	
	/**
	 * evals each of the blocks and returns an array containing each
	 * of the results
	 * @return
	 */
	public ArrayList<Object> evalToResults() {
		ArrayList<Object> out = new ArrayList<Object>(elements.length);
		for (int i = 0; i < elements.length; i++) {
			try {
				elements[i].eval();
			} catch (EmptyStackException e) {
				throw new RuntimeException("Empty Stack in tuple");
			}
			out.add(elements[i].pop());
		}
		return out;
	}
	
	public String toString() {
		String s = "(";
		for(int i = 0; i < elements.length; i++) {
			if(i!=0) {
				s += ", ";
			}
			s += elements[i].toString();
		}
		return s + ")";
	}
	
}
