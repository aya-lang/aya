package obj.dict;

import element.variable.Variable;
import element.variable.VariableSet;
import obj.Obj;

public class Dict extends Obj {
	
	private VariableSet _vars;
	
	public Dict() {
		_vars = new VariableSet(false);
	}
	
	public Obj get(Variable v) {
		// TODO remove obj cast
		return (Obj)_vars.getObject(v);
	}
	
	public Obj get(String s) {
		// TODO Revmoe Obj cast
		return (Obj)_vars.getObject(Variable.encodeString(s));
	}
	
	public void set(Variable v, Obj o) {
		_vars.setVar(v, o);
	}
	
	public void set(String s, Obj o) {
		_vars.setVar(Variable.encodeString(s), o);
	}
	
	public int size() {
		return _vars.getMap().size();
	}
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public Obj deepcopy() {
		return null;
	}

	@Override
	public boolean bool() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String repr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String str() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equiv(Obj o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isa(byte type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte type() {
		// TODO Auto-generated method stub
		return 0;
	}

}
