package element.obj.dict;

import element.variable.Variable;

public class KeyVariable extends Variable {

	public KeyVariable(long id) {
		super(id);
	}
	
	public KeyVariable(String s) {
		super(s);
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
