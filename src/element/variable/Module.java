package element.variable;

import java.util.ArrayList;

import element.exceptions.UndefVarException;
import element.util.Pair;

public class Module {
	private VariableSet vars;
	public long id;
	
	public Module(long name, VariableSet vs) {
		id = name;
		this.vars = vs;
	}
	
	public boolean hasVar(MemberVariable v) {
		return vars.hasVar(v.id);
	}
	
	public Object get(MemberVariable v) {
		Object o = vars.getObject(v.id);
		if (o == null) {
			throw new UndefVarException("Variable " + Variable.decodeLong(id) + v.toString() + " is not defined");
		} else {
			return o;
		}
	}
	
	public String name() {
		return Variable.decodeLong(id);
	}
	
	public String toString() {
		return Variable.decodeLong(id);
	}

	public ArrayList<Pair<Variable, Object>> getAllVars() {
		return vars.getAllVars();
	}

	/** Return the variable set */
	public VariableSet getVarSet() {
		return this.vars;
	}
}
