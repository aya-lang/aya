package element.obj.dict;

import element.exceptions.UndefVarException;
import element.obj.Obj;
import element.variable.Variable;
import element.variable.VariableSet;

/**
 * A set of key-value pairs accessible as a runtime object
 * @author Nick
 *
 */
public class Dict extends Obj {
	
	/** The map of key-value pairs */
	private VariableSet _vars;
	
	/** Create a new dict given a variable set */
	public Dict(VariableSet vars) {
		_vars = vars;
	}

	/** Get the object assigned to key {@code v} 
	 * If this key is unassigned, throw an error */
	public Obj get(KeyVariable v) {
		Obj o = _vars.getObject(v);
		if (o == null) {
			throw new UndefVarException("Dict does not contain key '" + v.toString() + "'");
		} else {
			return o;
		}
	}
	
	/** Get the object assigned to key whos name is {@code s} 
	 * If this key is unassigned, throw an error
	 */
	public Obj get(String s) {
		Obj o = _vars.getObject(Variable.encodeString(s));
		if (o == null) {
			throw new UndefVarException("Dict does not contain key \"" + s + "\"");
		} else {
			return o;
		}
	}
	
	/** Set a key-value pair.
	 * If a pair exists, overwrite
	 * if not, create a new pair
	 */ 
	public void set(KeyVariable v, Obj o) {
		_vars.setVar(v, o);
	}
	
	/** Set a key-value pair.
	 * If a pair exists, overwrite
	 * if not, create a new pair
	 */ 
	public void set(String s, Obj o) {
		_vars.setVar(Variable.encodeString(s), o);
	}
	
	/** The number of items in this dict */
	public int size() {
		return _vars.getMap().size();
	}
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public Obj deepcopy() {
		// TODO: Decide how to perform a deepcopy
		return this;
	}

	@Override
	public boolean bool() {
		return true;
	}

	@Override
	public String repr() {
		StringBuilder sb = new StringBuilder("{@, ");
		for (Long l : _vars.getMap().keySet()) {
			sb.append(_vars.getMap().get(l).repr() + ":" + Variable.decodeLong(l) + "; ");
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public String str() {
		StringBuilder sb = new StringBuilder("{@, ");
		for (Long l : _vars.getMap().keySet()) {
			sb.append(_vars.getMap().get(l).repr() + ":" + Variable.decodeLong(l) + "; ");
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
