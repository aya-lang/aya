package aya.obj.dict;

import java.util.ArrayList;

import aya.exceptions.ElementRuntimeException;
import aya.exceptions.UndefVarException;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.variable.Variable;
import aya.variable.VariableSet;

/**
 * A set of key-value pairs accessible as a runtime object
 * @author Nick
 *
 */
public class Dict extends Obj {
	
	public static final long STR = Variable.encodeString("str");
	public static final long REPR = Variable.encodeString("repr");
	
	/** The map of key-value pairs */
	protected VariableSet _vars;
	
	/** Metatable for the dict */
	protected VariableSet _meta;

	/** Create a new empty dict, use the unput dict as the metatable */
	public Dict(Dict metatable) {
		_vars = new VariableSet(false);
		_meta = metatable._vars;
	}
	
	/** Create a new dict given a variable set */
	public Dict(VariableSet vars) {
		_vars = vars;
		_meta = null;
	}
	
	/** Set the metatable to the input dict */
	public void setMetaTable(Dict d) {
		_meta = d._vars;
	}
	
	/** Set the metatable to the input variable set */
	public void setMetaTable(VariableSet vs) {
		_meta = vs;
	}
	
	
	/////////////
	// GETTERS //
	/////////////
	
	/** Get the object assigned to key {@code v} 
	 * If this key is unassigned, throw an error */
	public Obj get(KeyVariable v) {
		return get(v.getID());
	}
	
	/** Get the object assigned to key whos name is {@code s} 
	 * If this key is unassigned, throw an error
	 */
	public Obj get(String s) {
		return get(Variable.encodeString(s));
	}
	
	public Obj get(long id) {
		// First search object vars...
		Obj o = _vars.getObject(id);
		if (o == null) {
			// ...then search meta vars if there are any
			o = _meta == null ? null : _meta.getObject(id);
			if (o == null) {
				throw new UndefVarException("Dict does not contain key '" 
						+ new KeyVariable(id).toString() + "'");
			}
		}
		return o;
	}
	
	/** Returns true if this dict contains the input key */
	public boolean containsKey(long id) {
		// First search object vars...
		Obj o = _vars.getObject(id);
		if (o == null) {
			// ...then search meta vars if there are any
			o = _meta == null ? null : _meta.getObject(id);
			if (o == null) {
				return false;
			}
		}
		return true;
	}
	
	/** Returns true if this dict contains the input key */
	public boolean containsKey(String s) {
		return containsKey(Variable.encodeString(s));
	}
	
	/** Returns true if this dict contains the input key */
	public boolean containsKey(KeyVariable k) {
		return containsKey(k.getID());
	}	
	
	/** Return the inner variable set object */
	public VariableSet getVarSet() {
		return _vars;
	}
	
	/////////////
	// SETTERS //
	/////////////
	
	/** Set a key-value pair.
	 * If a pair exists, overwrite
	 * if not, create a new pair
	 */ 
	public void set(KeyVariable v, Obj o) {
		if (o.equals(this)) {
			throw new ElementRuntimeException("Error assigning '" + v.toString() + "': "
					+ "Cannot assign dict as member of itself");
		} else {
			_vars.setVar(v, o);
		}
	}
	
	/** Set a key-value pair.
	 * If a pair exists, overwrite
	 * if not, create a new pair
	 */ 
	public void set(String s, Obj o) {
		if (o.equals(this)) {
			throw new ElementRuntimeException("Error assigning '" + s + "': "
					+ "Cannot assign dict as member of itself");
		} else {
			_vars.setVar(Variable.encodeString(s), o);
		}
	}
	
	/** The number of items in this dict */
	public int size() {
		return _vars.getMap().size();
	}
	
	/** A list of keys */
	public ArrayList<Long> keys() {
		return _vars.keys();
	}
	
	/** A list of values */
	public ArrayList<Obj> values() {
		return _vars.values();
	}
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public Obj deepcopy() {
		// deep copy only the vars, not the metatable
		Dict d = new Dict(_vars.deepcopy());
		d.setMetaTable(_meta);
		return d;
	}

	@Override
	public boolean bool() {
		return true;
	}

	@Override
	public String repr() {
		if (_meta != null &&_meta.getObject(REPR) != null) {
			Obj obj_str = _meta.getObject(REPR);
			if(obj_str.isa(Obj.BLOCK)) {
				Block blk_show = ((Block)obj_str).duplicate();
				blk_show.push(this);
				blk_show.eval();
				Obj obj_res = blk_show.pop();
				return obj_res.str();
			} else {
				return obj_str.str();
			}
		} else {
			return dictStr();
		}
	}

	@Override
	public String str() {
		if (_meta != null && _meta.getObject(STR) != null) {
			Obj obj_str = _meta.getObject(STR);
			if(obj_str.isa(Obj.BLOCK)) {
				Block blk_show = ((Block)obj_str).duplicate();
				blk_show.push(this);
				blk_show.eval();
				Obj obj_res = blk_show.pop();
				return obj_res.str();
			} else {
				return obj_str.str();
			}
		} else {
			return dictStr();
		}
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

	public boolean hasMetaTable() {
		return _meta != null; 
	}
	
	////////////////////
	// HELPER METHODS //
	////////////////////
	
	/** Return a string representation of the dict */
	private String dictStr() {
		StringBuilder sb = new StringBuilder("{, ");
		for (Long l : _vars.getMap().keySet()) {
			sb.append(_vars.getMap().get(l).repr() + ":" + Variable.decodeLong(l) + "; ");
		}
		sb.append("}");
		return sb.toString();
	}

	



}
