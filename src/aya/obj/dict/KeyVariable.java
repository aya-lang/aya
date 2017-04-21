package aya.obj.dict;

import aya.variable.Variable;

/** Key variables are used for accessing/assigning dict variables */
public class KeyVariable extends Variable {

	public KeyVariable(long id) {
		super(id);
	}
	
	public KeyVariable(String s) {
		super(s);
	}

	public String name() {
		return decodeLong(id);
	}
	
	@Override
	public String toString() {
		String name = decodeLong(id);
		if (referanceVariable) {
			return "." + name;
		} else {
			return ".:" + name;
		}
	}
}
