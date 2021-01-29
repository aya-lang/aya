package aya.obj.dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import aya.Aya;
import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.util.Callable;
import aya.util.Pair;

/**
 * A set of key-value pairs accessible as a runtime object
 * @author Nick
 *
 */
public class Dict extends Obj {

	private static Symbol PUSH_SELF = Aya.getInstance().getSymbols().getSymbol("__pushself__");
	public static Symbol META = Aya.getInstance().getSymbols().getSymbol("__meta__");
	
	/** The map of key-value pairs */
	private HashMap<Symbol, Obj> _vars;
	private Dict _meta; // Quick lookup for meta

	/** Create a new empty dict, use the input dict as the metatable */
	public Dict(HashMap<Symbol, Obj> vars, Dict metatable) {
		if (vars == null) {
			_vars = new HashMap<Symbol, Obj>();
		} else {
			_vars = vars;
		}
		
		if (metatable != null)
		{
			_vars.put(META, metatable);
			_meta = metatable;
		}
	}
	
	/** Create a new dict given a variable set */
	public Dict(HashMap<Symbol, Obj> vars) {
		_vars = vars;
		// Check if the VariableSet has a __meta__ key
		Obj maybe_meta = vars.get(META);
		if (maybe_meta != null && maybe_meta.isa(DICT)) {
			_meta = (Dict)(maybe_meta);
		} else {
			_meta = null;
		}
	}
	
	/** Create a new empty dict */
	public Dict() {
		_vars = new HashMap<Symbol, Obj>();
		_meta = null;
	}

	/** Set the metatable to the input dict */
	public void setMetaTable(Dict d) {
		_vars.put(META, d);
		_meta = d;
	}
	

	/////////////
	// GETTERS //
	/////////////
	
	public boolean pushSelf() {
		return hasMetaKey(PUSH_SELF);
	}

	/** returns default value if key not found */
	public Obj get(Symbol key, Obj dflt) {
		Obj x = _get(key, null);
		if (x == null) {
			return dflt;
		} else {
			return x;
		}
	}
	
	/** throws exception if key not found */
	public Obj get(Symbol key) {
		Obj o = _get(key, null);
		if (o == null) {
			throw new AyaKeyError(this, key);
		} else {
			return o;
		}
	}

	/** returns null if key not found */
	private Obj _get(Symbol typeId) {
		return _get(typeId, null);
	}
	
	/** Returns null if key not found */
	public Obj getSafe(Symbol key) {
		return _get(key, null);
	}
	
	/** Returns null if no key found */
	private Obj _get(Symbol typeId, ArrayList<Integer> visited) {
		Obj o = _vars.get(typeId);
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
					o = _meta._get(typeId, visited);
					return o;
				}
				
			} else {
				// Key not found in dict and this dict has no meta
				return null;
			}
		}
	}
	
	/** Get from metatable. If no such key, return null */
	public Obj getFromMetaTableOrNull(Symbol typeId) {
		if (_meta == null) {
			return null;
		} else {
			return _meta._get(typeId);

		}
	}
	
	/** Returns true if this dict contains the input key */
	public boolean containsKey(Symbol key) {
		return _get(key) != null;
	}
	
	/** The number of items in this dict */
	public int size() {
		return _vars.size();
	}
	
	/** A list of keys */
	public ArrayList<Symbol> keys() {
		ArrayList<Symbol> out = new ArrayList<Symbol>();
		Iterator<HashMap.Entry<Symbol, Obj>> it = _vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	HashMap.Entry<Symbol,Obj> pair = (HashMap.Entry<Symbol, Obj>)it.next();
	    	out.add(pair.getKey());
	    }
	    return out;
	}
	
	/** All key/value pairs */
	public ArrayList<Pair<Symbol, Obj>> items() {
		ArrayList<Pair<Symbol,Obj>> out = new ArrayList<Pair<Symbol, Obj>>();
		Iterator<HashMap.Entry<Symbol, Obj>> it = _vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	HashMap.Entry<Symbol,Obj> pair = (HashMap.Entry<Symbol, Obj>)it.next();
	    	out.add(new Pair<Symbol, Obj>(pair.getKey(), pair.getValue()));
	    }
	    return out;
	}
	
	/** A list of values */
	public ArrayList<Obj> values() {
		ArrayList<Obj> out = new ArrayList<Obj>(size());
		out.addAll(_vars.values());
		return out;
	}
	
	/////////////
	// SETTERS //
	/////////////
	
	/** Set a key-value pair.
	 * If a pair exists, overwrite
	 * if not, create a new pair
	 */ 
	public void set(Symbol key, Obj o) {
		_vars.put(key,  o);
		
		if (key.id() == META.id() && o.isa(Obj.DICT))
		{
			_meta = (Dict)o;
		}
	}
	
	/** Update values in this dict to the values from the input dict */
	public void update(Dict other) {
		_vars.putAll(other._vars);
	}
	
	
	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	@Override
	public Obj deepcopy() {
		// Don't deep copy the meta
		if (_meta != null) {
			_vars.remove(META);
		}
		
		Dict d = new Dict(deepcopyHashMap(), _meta);
		
		// Re-attach the meta if it exists
		if (_meta != null) {
			d.setMetaTable(_meta);
		}
		return d;
	}

	@Override
	public boolean bool() {
		if (_meta != null) {
			Obj bool = get(SymbolConstants.KEYVAR_BOOL);
			if (bool.isa(Obj.BLOCK)) {
				Block blk_bool = ((Block)bool).duplicate();
				blk_bool.push(this);
				blk_bool.eval();
				Obj obj_res = blk_bool.pop();
				return obj_res.bool();
			} else {
				return bool.bool();
			}
		}

		return true;
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		if (stream.visit(this)) {
			dictRepr(stream);
			stream.popVisited(this);
		} else {
			stream.print("{, ...}");
		}
		return stream;
	}
		
	@Override
	public String str() {
		if (_meta != null) {
			Obj str = _meta._get(SymbolConstants.KEYVAR_REPR);
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
			if (other._vars.size() == this._vars.size()) {
				for (Symbol sym : this._vars.keySet()) {
					Obj elem = other._vars.get(sym);
					if (elem == null || !elem.equiv(this._vars.get(sym))) {
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
		return dictRepr(new ReprStream()).toStringOneline();
	}
	
	/** Return a string representation of the dict 
	 * @param stream */
	private ReprStream dictRepr(ReprStream stream) {
		// Metatable?
		if (_meta != null) {
			Obj repr = _meta._get(SymbolConstants.KEYVAR_REPR);
			if (repr != null) {
				Block callable = Callable.getCallable(repr);
				if (callable != null) {
					Block blk_repr = callable.duplicate();
					blk_repr.push(this);
					blk_repr.eval();
					Obj obj_res = blk_repr.pop();
					if (obj_res.isa(Obj.STR)) {
						stream.print(obj_res.str());
						return stream;
					} else {
						return obj_res.repr(stream);

					}
				} else if (repr.isa(Obj.STR)) {
					stream.print(repr.str());
					return stream;
				} else {
					return repr.repr(stream);
				}
			}
		}
	
		// Normal repr
		if (_vars.size() == 0) {
			stream.print("{,}");
		} else {
			stream.println("{,");
			stream.incIndent();
			stream.currentLineMatchIndent();
			for (Symbol sym : _vars.keySet()) {
				if (sym.id() != META.id()) {
					_vars.get(sym).repr(stream);
					stream.println(":" + sym.name() + ";");
				}
			}
			stream.decIndent();
			stream.currentLineMatchIndent();
			stream.print("}");
		}
		return stream;
	}

	///////////
	// OTHER //
	///////////
	
	public Obj getMetaDict() {
		if (_meta == null) setMetaTable(new Dict());
		return _meta;
	}
	
	public HashMap<Symbol, Obj> getMap() {
		return _vars;
	}
	
	public void clear() {
		_vars.clear();
		_meta = null;
	}

	@Override
	public Dict clone() {
		Dict out = new Dict();
		out.update(this);
		return out;
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
		for (Pair<Symbol, Obj> pair : d.getAllVars()) {
			b.getInstructions().assignVarValue(pair.first(), pair.second());
		}
	}

	/** Returns true if the metatable defines a given key */
	public boolean hasMetaKey(Symbol v) {
		return _meta != null && _meta.containsKey(v);
	}

	
	/** General setindex */
	public static void setIndex(Dict dict, Obj index, Obj value) {
		if (index.isa(Obj.STR)) {
			dict.set(Aya.getInstance().getSymbols().getSymbol(index.str()), value);
		} else if (index.isa(Obj.SYMBOL)) {
			dict.set((Symbol)index, value);
		} else {
			throw new AyaKeyError(dict, index);
		}
	}

	public void remove(Symbol key) {
		_vars.remove(key);
	}
	


	/** Return all variables as a list of pairs */
	private ArrayList<Pair<Symbol, Obj>> getAllVars() {
		ArrayList<Pair<Symbol,Obj>> out = new ArrayList<Pair<Symbol, Obj>>();
		Iterator<HashMap.Entry<Symbol, Obj>> it = _vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	HashMap.Entry<Symbol,Obj> pair = (HashMap.Entry<Symbol, Obj>)it.next();
	    	out.add(new Pair<Symbol, Obj>(pair.getKey(), pair.getValue()));
	    }
	    return out;
	}
	
	private HashMap<Symbol, Obj> deepcopyHashMap() {
		// Copy the hash map
		HashMap<Symbol, Obj> vars_copy = new HashMap<Symbol, Obj>();
		for (Symbol l : _vars.keySet()) {
			vars_copy.put(l, _vars.get(l).deepcopy());
		}
		return vars_copy;
	}

	

	/**
	 * Output the variable set as a space separated list of name(value) items
	 * If the value is 0, do not print the value or the parenthesis
	 */
	public ReprStream reprHeader(ReprStream stream) {
		Iterator<HashMap.Entry<Symbol, Obj>> it = _vars.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry<Symbol, Obj> pair = (HashMap.Entry<Symbol, Obj>)it.next();
			stream.print(pair.getKey().name());

			final Obj obj = pair.getValue();
			if (obj.equiv(Num.ZERO)) {
				stream.print(" ");
			} else {
				stream.print("(");
				obj.repr(stream);
				stream.print(")");
			}
		}
		return stream;
	}

	/** Merge variables from the given variable set only if they are defined in this one */
	public void mergeDefined(Dict other) {
		for (HashMap.Entry<Symbol, Obj> e : other._vars.entrySet()) {
			if (_vars.containsKey(e.getKey())) {
				_vars.put(e.getKey(), e.getValue());
			}
		}
	}
	
	
}
