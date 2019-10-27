package aya.obj.dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import aya.entities.operations.Ops;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.UndefVarException;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.symbol.Symbol;
import aya.util.Pair;
import aya.variable.Variable;
import aya.variable.VariableSet;

/**
 * A set of key-value pairs accessible as a runtime object
 * @author Nick
 *
 */
public class Dict extends Obj {
	
	public static Variable META = new Variable("__meta__");
	
	/** The map of key-value pairs */
	protected VariableSet _vars;
	private Dict _meta; // Quick lookup for meta
	private HashMap<String, Obj> _string_vars;

	/** Create a new empty dict, use the input dict as the metatable */
	public Dict(VariableSet vars, Dict metatable) {
		if (vars == null) {
			_vars = new VariableSet(false);
		} else {
			_vars = vars;
		}
		
		if (metatable != null)
		{
			_vars.setVar(META, metatable);
			_meta = metatable;
		}
		_string_vars = new HashMap<>();
	}
	
	/** Create a new dict given a variable set */
	public Dict(VariableSet vars) {
		_vars = vars;
		// Check if the VariableSet has a __meta__ key
		Obj maybe_meta = vars.getObject(META);
		if (maybe_meta != null && maybe_meta.isa(DICT)) {
			_meta = (Dict)(maybe_meta);
		} else {
			_meta = null;
		}
		_string_vars = new HashMap<>();
	}
	
	/** Create a new empty dict */
	public Dict() {
		_vars = new VariableSet(false);
		_meta = null;
		_string_vars = new HashMap<>();
	}

	/** Set the metatable to the input dict */
	public void setMetaTable(Dict d) {
		_vars.setVar(META, d);
		_meta = d;
	}

	//////////////////
	// STRING TABLE //
	//////////////////
	
	public Obj strGet(String s) {
		return _string_vars.get(s);
	}
	
	public void strSet(String s, Obj o) {
		_string_vars.put(s, o);
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
		return get(Symbol.convToSymbol(s).id());
	}
	
	/** throws exception if key not found */
	public Obj get(long id) {
		Obj o = _get(id, null);
		
		if (o == null) {
			throw new UndefVarException("Dict does not contain key '" 
					+ KeyVariable.fromID(id).toString() + "'");
		} else {
			return o;
		}
	}
	
	
	
	/** returns null if key not found */
	private Obj _get(long id) {
		return _get(id, null);
	}
	
	/** Returns null if key not found */
	public Obj getSafe(long id) {
		return _get(id, null);
	}
	
	/** Returns null if no key found */
	private Obj _get(long id, ArrayList<Integer> visited) {
		Obj o = _vars.getObject(id);
		if (o != null)
		{
			return o;
		}
		else {
			// Check meta
			if (_meta != null) {
				// Add this dict as visited
				// Only allocate the visited array of needed
				if (visited == null) visited = new ArrayList<Integer>();
				visited.add(this.hashCode());
				
				// Have we already visited this?
				int hash = _meta.hashCode();
				boolean visited_meta = false;
				for (Integer i : visited) {
					if (hash == i.intValue()) {
						visited_meta = true;
						break;
					}
				}
				
				// Already visited, do not look up
				if (visited_meta) {
					return null;
				} else {
					// Not visited, search it (and it's metas) for the key
					o = _meta._get(id, visited);
					return o;
				}
				
			} else {
				// Key not found in dict and this dict has no meta
				return null;
			}
		}
	}
	
	/** Get from metatable. If no such key, return null */
	public Obj getFromMetaTableOrNull(long id) {
		if (_meta == null) {
			return null;
		} else {
			return _meta._get(id);

		}
	}
	
	/** Returns true if this dict contains the input key */
	public boolean containsKey(long id) {
		return _get(id) != null;
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
		set(v.getID(), o);
	}
	
	/** Set a key-value pair.
	 * If a pair exists, overwrite
	 * if not, create a new pair
	 */ 
	public void set(Long id, Obj o) {
		if (o.equals(this)) {
			throw new AyaRuntimeException("Error assigning '" + (new Variable(id)).toString() + "': "
					+ "Cannot assign dict as member of itself");
		} else {
			_vars.setVar(id, o);
			
			if (id == META.getID() && o.isa(Obj.DICT))
			{
				_meta = (Dict)o;
			}
		}
	}
	
	/** Set a key-value pair.
	 * If a pair exists, overwrite
	 * if not, create a new pair
	 */ 
	public void set(String s, Obj o) {
		set(Variable.encodeString(s), o);
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
		// Don't deep copy the meta
		if (_meta != null) {
			_vars.unsetVar(META);
		}
		
		Dict d = new Dict(_vars.deepcopy(), _meta);
		
		// Re-attach the meta if it exists
		if (_meta != null) {
			d.setMetaTable(_meta);
		}
		return d;
	}

	@Override
	public boolean bool() {
		return true;
	}

	@Override
	public String repr() {
		if (_meta != null) {
			Obj repr = _meta._get(Ops.KEYVAR_REPR.getID());
			if (repr != null) {
				if (repr.isa(Obj.BLOCK)) {
					Block blk_repr = ((Block)repr).duplicate();
					blk_repr.push(this);
					blk_repr.eval();
					Obj obj_res = blk_repr.pop();
					return obj_res.str();
				} else {
					return repr.repr();
				}
			}
		}
		
		return dictRepr();
	}
		
	@Override
	public String str() {
		if (_meta != null) {
			Obj str = _meta._get(Ops.KEYVAR_REPR.getID());
			if (str != null) {
				if (str.isa(Obj.BLOCK)) {
					Block blk_str = ((Block)str).duplicate();
					blk_str.push(this);
					blk_str.eval();
					Obj obj_res = blk_str.pop();
					return obj_res.str();
				} else {
					return str.str();
				}
			}
		}
		
		return dictStr();
	}

	@Override
	public boolean equiv(Obj o) {
		if (this == o) return true;
		
		if (o instanceof Dict) {
			Dict other = (Dict)o;
			if (other.size() == this.size()) {
				for (Long l : this._vars.getMap().keySet()) {
					Obj elem = other._vars.getMap().get(l);
					if (elem == null || !elem.equiv(this._vars.getMap().get(l))) {
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
			if (l != META.getID()) {
				sb.append(_vars.getMap().get(l).repr() + ":" + Variable.decodeLong(l) + "; ");
			}
		}
		for (HashMap.Entry<String, Obj> e : _string_vars.entrySet()) {
			sb.append(e.getValue().repr() + ":\"" + e.getKey() + "\"; ");
		}
		sb.append("}");
		return sb.toString();
	}
	
	/** Return a string representation of the dict */
	private String dictRepr() {
		LinkedList<Dict> visited = new LinkedList<Dict>();
		return dictRepr(0, visited);
	}
	
	private String dictRepr(int depth, LinkedList<Dict> visited) {
		final int width = 2;
		if (visited.contains(this)) {
			return "{, ...}";
		} else {
			visited.add(this);
			StringBuilder sb = new StringBuilder("{,\n");
			for (Long l : _vars.getMap().keySet()) {
				if (l != META.getID()) {
					sb.append(spaces((depth + 1) * width));
					sb.append(pairString(depth+1, visited, Variable.decodeLong(l), _vars.getObject(l)));
					sb.append('\n');
				}
			}
			for (HashMap.Entry<String, Obj> e : _string_vars.entrySet()) {
				sb.append(spaces((depth + 1) * width));
				sb.append(pairString(depth+1, visited, e.getKey(), e.getValue()));
				sb.append('\n');
			}
			sb.append(spaces(depth*width)).append("}");
			return sb.toString();

			
		}
	}
	
	private String pairString(int depth, LinkedList<Dict> visited, String key, Obj o) {
		String object;
		if (o.isa(DICT)) {
			object = ((Dict)o).dictRepr(depth, visited);
		} else {
	
			object = o.repr();
		}
		
		return object + ":" + key + ";";
	}
	
	private String spaces(int n) {
		char[] margin_ch = new char[n];
		for (int i = 0; i < margin_ch.length; i++) margin_ch[i] = ' ';
		return new String(margin_ch);	
	}

	
	public Obj getMetaDict() {
		if (_meta == null) setMetaTable(new Dict());
		return _meta;
	}
	
	////////////////////
	// STATIC METHODS //
	////////////////////
	
	/** Given a block, swap all references to variables defined in the dict
	 * with the values corresponding to the keys in the dict.
	 * @param d
	 * @param b
	 */
	public static void assignVarValues(Dict d, Block b) {
		for (Pair<Variable, Obj> pair : d._vars.getAllVars()) {
			b.getInstructions().assignVarValue(pair.first().getID(), pair.second());
		}
	}

	/** Returns true if the metatable defines a given key name */
	public boolean hasMetaKey(String str) {
		return _meta != null && _meta.containsKey(Variable.encodeString(str));
	}
	
	/** Returns true if the metatable defines a given key */
	public boolean hasMetaKey(KeyVariable kv) {
		return _meta != null && _meta.containsKey(kv.getID());
	}

	/** General getindex */
	public static Obj getIndex(Dict dict, Obj index) {
		if (index.isa(Obj.STR)) {
			return dict.get(index.str());
		} else if (index.isa(Obj.SYMBOL)) {
			return dict.get(((Symbol)index).id());
		} else {
			throw new AyaRuntimeException("Cannot access dict at index " + index.repr() + "\n" + dict.repr());
		}
	}
	
	/** General setindex */
	public static void setIndex(Dict dict, Obj index, Obj value) {
		if (index.isa(Obj.STR)) {
			dict.set(index.str(), value);
		} else if (index.isa(Obj.SYMBOL)) {
			dict.set(((Symbol)index).id(), value);
		} else {
			throw new AyaRuntimeException("Cannot set value of dict at index " + index.repr() + "\n" + dict.repr());
		}
	}

	



}
