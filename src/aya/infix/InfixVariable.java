package aya.infix;

import aya.variable.Variable;

public class InfixVariable extends InfixItem {

	private String var;
	
	public InfixVariable(String varname) {
		super(InfixItem.VARIABLE);
		var = varname;
	}
	
	public String getVarName() {
		return var;
	}

	@Override
	public Object generateElementCode() {
		return new Variable(var);
	}

	@Override
	public String typeString() {
		return "{v:" + var.toString() + "}";
	}

	@Override
	public void organize() {
		// Do Nothing
		
	}

	@Override
	public void desugar() {
		// Do nothing
	}

}
