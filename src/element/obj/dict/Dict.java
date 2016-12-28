package element.obj.dict;

import element.obj.Obj;
import element.variable.Variable;
import element.variable.VariableSet;

public class Dict extends Obj {
	
	private VariableSet _vars;
	
	public Dict(VariableSet vars) {
		_vars = vars;
	}

	public Obj get(Variable v) {
		return _vars.getObject(v);
	}
	
	public Obj get(String s) {
		return _vars.getObject(Variable.encodeString(s));
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
		// TODO Dict.deepcopy()
		return null;
	}

	@Override
	public boolean bool() {
		return true;
	}

	@Override
	public String repr() {
		StringBuilder sb = new StringBuilder("{@, ");
		for (Long l : _vars.getMap().keySet()) {
			//TODO: Change to repr()
			sb.append(_vars.getMap().get(l).toString() + ":" + Variable.decodeLong(l) + "; ");
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public String str() {
		StringBuilder sb = new StringBuilder("{@, ");
		for (Long l : _vars.getMap().keySet()) {
			//TODO: Change to repr()
			sb.append(_vars.getMap().get(l).toString() + ":" + Variable.decodeLong(l) + "; ");
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public boolean equiv(Obj o) {
		if (o instanceof Dict) {
			Dict other = (Dict)o;
			if (other.size() == this.size()) {
				for (Long l : this._vars.getMap().keySet()) {
					if (!other._vars.getMap().get(l).equiv(this._vars.getMap().get(l))) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean isa(byte type) {
		return type == Obj.DICT;
	}

	@Override
	public byte type() {
		return Obj.DICT;
	}

}
