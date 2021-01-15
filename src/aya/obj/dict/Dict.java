package aya.obj.dict;

import java.util.ArrayList;

import aya.Aya;
import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.util.Pair;
import aya.variable.VariableSet;

/**
 * A set of key-value pairs accessible as a runtime object
 * @author Nick
 *
 */
public class Dict extends Obj {

	private static Symbol PUSH_SELF = Aya.getInstance().getSymbols().getSymbol("__pushself__");
	public static Symbol META = Aya.getInstance().getSymbols().getSymbol("__meta__");
	
	/** The map of key-value pairs */
	protected VariableSet _vars;
	private Dict _meta; // Quick lookup for meta

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
	}
	
	/** Create a new dict given a variable set */
	public Dict(VariableSet vars) {
		_vars = vars;
		// Check if the VariableSet has a __meta__ key
		Obj maybe_meta = vars.getObj(META);
		if (maybe_meta != null && maybe_meta.isa(DICT)) {
			_meta = (Dict)(maybe_meta);
		} else {
			_meta = null;
		}
	}
	
	/** Create a new empty dict */
	public Dict() {
		_vars = new VariableSet(false);
		_meta = null;
	}

	/** Set the metatable to the input dict */
	public void setMetaTable(Dict d) {
		_vars.setVar(META, d);
		_meta = d;
	}
	

	/////////////
	// GETTERS //
	/////////////
	
	public boolean pushSelf() {
		return hasMetaKey(PUSH_SELF);
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
		Obj o = _vars.getObj(typeId);
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
	
	/** Return the inner variable set object */
	public VariableSet getVarSet() {
		return _vars;
	}

	/** The number of items in this dict */
	public int size() {
		return _vars.getMap().size();
	}
	
	/** A list of keys */
	public ArrayList<Symbol> keys() {
		return _vars.keys();
	}
	
	/** All key/value pairs */
	public ArrayList<Pair<Symbol, Obj>> items() {
		return _vars.getAllVars();
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
		_vars.setVar(key, o);
		
		if (key.id() == META.id() && o.isa(Obj.DICT))
		{
			_meta = (Dict)o;
		}
	}
	
	/** Update values in this dict to the values from the input dict */
	public void update(Dict other) {
		_vars.update(other._vars);
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
				for (Symbol sym : this._vars.getMap().keySet()) {
					Obj elem = other._vars.getMap().get(sym);
					if (elem == null || !elem.equiv(this._vars.getMap().get(sym))) {
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
				if (repr.isa(Obj.BLOCK)) {
					Block blk_repr = ((Block)repr).duplicate();
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
			for (Symbol sym : _vars.getMap().keySet()) {
				if (sym.id() != META.id()) {
					_vars.getObj(sym).repr(stream);
					stream.println(":" + sym.name() + ";");
				}
			}
			stream.decIndent();
			stream.currentLineMatchIndent();
			stream.print("}");
		}
		return stream;
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
		for (Pair<Symbol, Obj> pair : d._vars.getAllVars()) {
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

}
