package aya.variable;

import static aya.util.Casting.asDict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import aya.CLIOptions;
import aya.StaticData;
import aya.exceptions.runtime.UndefVarException;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

/**
 * Static class containing all variables for the current session.
 * Variable scope is not recursive but handled by a list of dicts
 * When variable bindings are requested, this starts by looking in the
 * most local scope and moving outward until a variable is found in the global
 * scope or not at all. 
 * 
 * @author Nick
 *
 */
public class VariableData {
	
	class Scope {
		Dict dict;
		boolean capture_all_assignments;
		
		Scope(Dict dict, boolean capture_all_assignments) {
			this.dict = dict;
			this.capture_all_assignments = capture_all_assignments;
		}
		
		Scope(Dict dict) {
			this(dict, false);
		}
	};
	
	private ArrayList<Scope> _var_sets = new ArrayList<Scope>();
	private Stack<Integer> _checkpoints = new Stack<Integer>();

	private final Dict OBJ_STR = new Dict();
	private final Dict OBJ_SYM = new Dict();
	private final Dict OBJ_LIST = new Dict();
	private final Dict OBJ_NUM = new Dict();
	private final Dict OBJ_CHAR = new Dict();
	private final Dict OBJ_BLOCK = new Dict();
	
	// Builtins are static across all threads
	private static final Dict BUILTINS = new Dict();

	public final Dict OBJ_NIL = new Dict();

	public VariableData() {
	}
	
	public VariableData duplicateWithGlobals() {
		VariableData v = new VariableData();
		
		//v.add(getGlobals());
		v.add(new Dict());
		
		// Add object and builtins
		v.OBJ_STR.update(this.OBJ_STR);
		v.OBJ_SYM.update(this.OBJ_SYM);
		v.OBJ_LIST.update(this.OBJ_LIST);
		v.OBJ_NUM.update(this.OBJ_NUM);
		v.OBJ_CHAR.update(this.OBJ_CHAR);
		v.OBJ_BLOCK.update(this.OBJ_BLOCK);
		v.OBJ_NIL.update(this.OBJ_NIL);
		
		return v;
	}
	
	public void initGlobals() {
		Dict globals = new Dict();
		
		globals.set(SymbolConstants.VERSION, List.fromString(StaticData.VERSION_NAME));
		globals.set(SymbolConstants.HELP, List.fromString(CLIOptions.HELP_TEXT));

		globals.set(SymbolConstants.E, Num.E);				
		globals.set(SymbolConstants.PI, Num.PI);
		
		OBJ_CHAR.set(SymbolConstants.KEYVAR_META,  OBJ_CHAR);
		OBJ_SYM.set(SymbolConstants.KEYVAR_META,  OBJ_SYM);
		OBJ_BLOCK.set(SymbolConstants.KEYVAR_META,  OBJ_BLOCK);
		OBJ_LIST.set(SymbolConstants.KEYVAR_META,  OBJ_LIST);
		OBJ_NUM.set(SymbolConstants.KEYVAR_META,  OBJ_NUM);
		OBJ_STR.set(SymbolConstants.KEYVAR_META,  OBJ_STR);

		if (BUILTINS.size() == 0) {
			BUILTINS.set(SymbolConstants.CHAR,  OBJ_CHAR);
			BUILTINS.set(SymbolConstants.SYM,   OBJ_SYM);
			BUILTINS.set(SymbolConstants.BLOCK, OBJ_BLOCK);
			BUILTINS.set(SymbolConstants.LIST,  OBJ_LIST);
			BUILTINS.set(SymbolConstants.NUM,   OBJ_NUM);
			BUILTINS.set(SymbolConstants.STR,   OBJ_STR);
		}
		
		_var_sets.add(new Scope(globals));
	}
	
	public Dict getBuiltinMeta(Obj o) {
		return asDict(BUILTINS.get(Obj.IDToSym(o.type())));
	}
	
	/** Returns the set containing the global variables */
	public Dict getGlobals() {
		return _var_sets.get(0).dict;
	}
	
	/** clears all but the global variables */
	public void reset() {
		for (int i = _var_sets.size()-1; i >=1; i--) {
			_var_sets.remove(i);
		}
	}
	
	public void setGlobalVar(Symbol v, Obj o) {
		_var_sets.get(0).dict.set(v, o);
	}
	
	public void setCheckpoint() {
		_checkpoints.push(_var_sets.size());
	}
	
	public void popCheckpoint() {
		_checkpoints.pop();
	}
	
	public void rollbackCheckpoint() {
		int stack_size = _checkpoints.pop();
		while (_var_sets.size() > stack_size) {
			pop();
		}
	}
	
	public void pop() {
		if (_var_sets.size() == 1) {
			throw new RuntimeException("Variable state error!!");
		}
		_var_sets.remove(_var_sets.size()-1); // .dict.clear();
	}
	
	public Dict peek() {
		return _var_sets.get(_var_sets.size()-1).dict;
	}
	
	public Dict popGet() {
		return _var_sets.remove(_var_sets.size()-1).dict;
	}
	
	public void setVar(Symbol v, Obj o) {
		// Cond is >=1 because we don't need to check the globals
		for(int i = _var_sets.size()-1; i >=1; i--) {
			Scope scope = _var_sets.get(i);
			if(scope.dict.containsKey(v) || scope.capture_all_assignments) {
				scope.dict.set(v, o);
				return;
			}
		}
		
		//The var was not found in any of the sets
		//Create a global instance of it
		setGlobalVar(v, o);
	}
	
	public Obj getVar(Symbol var) {
		Obj res = getVarOrNull(var);
		if (res == null) {
			throw new UndefVarException(var);
		} else {
			return res;
		}
	}
	
	private Obj getVarOrNull(Symbol id) {
		Obj res = null;
		for(int i = _var_sets.size()-1; i >= 0; i--) {
			res = _var_sets.get(i).dict.getOrNullNoMeta(id);
			if(res != null) {
				return res;
			}
		}
		return null;
	}
	
	public void add(Dict vars, boolean b) {
		_var_sets.add(new Scope(vars, b));
	}

	public void add(Dict vars) {
		_var_sets.add(new Scope(vars, false));
	}

	public boolean isDefined(Symbol id) {
		return getVarOrNull(id) != null;
	}

	public void setVars(Dict vars) {
		for (HashMap.Entry<Symbol, Obj> p : vars.getMap().entrySet()) {
			setVar(p.getKey(), p.getValue());
		}
	}

	public Obj getDictList() {
		List l = new List();
		for (Scope s : _var_sets) l.mutAdd(s.dict);
		return l;
	}

}
