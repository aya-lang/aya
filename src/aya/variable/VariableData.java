package aya.variable;

import static aya.util.Casting.asDict;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import aya.Aya;
import aya.InteractiveAya;
import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.Parser;

/**
 * Static class containing all variables for the current session.
 * Variable scope is not recursive but handled by a list of variable sets.
 * When variable bindings are requested, this starts by looking in the
 * most local scope and moving outward until a variable is found in the global
 * scope or not at all. 
 * 
 * @author Nick
 *
 */
public class VariableData {
	private ArrayList<VariableSet> varSets = new ArrayList<VariableSet>();
	private Stack<Integer> _checkpoints = new Stack<Integer>();

	private final Dict OBJ_STR = new Dict();
	private final Dict OBJ_SYM = new Dict();
	private final Dict OBJ_LIST = new Dict();
	private final Dict OBJ_NUM = new Dict();
	private final Dict OBJ_CHAR = new Dict();
	private final Dict OBJ_BLOCK = new Dict();
	private final Dict BUILTINS = new Dict();

	public final Dict OBJ_NIL = new Dict();

	public VariableData(Aya aya) {
		initGlobals(aya);
	}
	
	private void initNil(Aya aya) {
		Dict nil_meta = new Dict();
		nil_meta.set(SymbolConstants.KEYVAR_TYPE, Aya.getInstance().getSymbols().getSymbol("__nil"));
		List nil_str = List.fromString("nil");
		nil_meta.set(SymbolConstants.KEYVAR_STR, nil_str);
		nil_meta.set(SymbolConstants.KEYVAR_REPR, nil_str);
		nil_meta.set(SymbolConstants.KEYVAR_PUSHSELF, Num.ONE);
		nil_meta.set(SymbolConstants.KEYVAR_EQ, Parser.compile("; :T ::__nil =", aya));
		
		OBJ_NIL.setMetaTable(nil_meta);
	}
	
	public void initGlobals(Aya aya) {
		VariableSet globals = new VariableSet(null, null, null);
		
		globals.setVar(SymbolConstants.VERSION, List.fromString(Aya.VERSION_NAME));
		globals.setVar(SymbolConstants.HELP, List.fromString(InteractiveAya.HELP_TEXT));

		globals.setVar(SymbolConstants.E, Num.E);				
		globals.setVar(SymbolConstants.PI, Num.PI);
		
		OBJ_CHAR.set(SymbolConstants.KEYVAR_META,  OBJ_CHAR);
		OBJ_SYM.set(SymbolConstants.KEYVAR_META,  OBJ_SYM);
		OBJ_BLOCK.set(SymbolConstants.KEYVAR_META,  OBJ_BLOCK);
		OBJ_LIST.set(SymbolConstants.KEYVAR_META,  OBJ_LIST);
		OBJ_NUM.set(SymbolConstants.KEYVAR_META,  OBJ_NUM);
		OBJ_STR.set(SymbolConstants.KEYVAR_META,  OBJ_STR);

		BUILTINS.set(SymbolConstants.CHAR,  OBJ_CHAR);
		BUILTINS.set(SymbolConstants.SYM,   OBJ_SYM);
		BUILTINS.set(SymbolConstants.BLOCK, OBJ_BLOCK);
		BUILTINS.set(SymbolConstants.LIST,  OBJ_LIST);
		BUILTINS.set(SymbolConstants.NUM,   OBJ_NUM);
		BUILTINS.set(SymbolConstants.STR,   OBJ_STR);
		
		initNil(aya);
		globals.setVar(SymbolConstants.NIL, OBJ_NIL);

		varSets.add(globals);
	}
	
	public Dict getBuiltinMeta(Obj o) {
		return asDict(BUILTINS.get(Obj.IDToSym(o.type())));
	}
	
	/** Returns the set containing the global variables */
	public VariableSet getGlobals() {
		return varSets.get(0);
	}
	
	/** Returns (varname) = (debugString(o)) ((default variable)) for each global variable */
	public ArrayList<String> getDefaultVariableDiscs(Aya aya) {
		if(varSets.size() < 1) {
			initGlobals(aya);
		}
		
		ArrayList<String> out = new ArrayList<String>();
		Iterator<Map.Entry<Symbol, Obj>> entries = varSets.get(0).getMap().entrySet().iterator();
		while (entries.hasNext()) {
		  Entry<Symbol, Obj> thisEntry = (Entry<Symbol, Obj>) entries.next();
		  out.add(thisEntry.getKey().repr() + " = " + thisEntry.getValue().repr() + "\n(default variable)");
		}
		return out;
	}
	
	/** clears all but the global variables */
	public void reset() {
		for (int i = varSets.size()-1; i >=1; i--) {
			varSets.remove(i);
		}
	}
	
	public void setGlobalVar(Symbol v, Obj o) {
		varSets.get(0).setVar(v, o);
	}
	
	public void setCheckpoint() {
		_checkpoints.push(varSets.size());
	}
	
	public void popCheckpoint() {
		_checkpoints.pop();
	}
	
	public void rollbackCheckpoint() {
		int stack_size = _checkpoints.pop();
		while (varSets.size() > stack_size) {
			pop();
		}
	}
	
	public void pop() {
		if (varSets.size() == 1) {
			throw new RuntimeException("Variable state error!!");
		}
		varSets.remove(varSets.size()-1).clear();
	}
	
	public VariableSet peek() {
		return varSets.get(varSets.size()-1);
	}
	
	public VariableSet popGet() {
		return varSets.remove(varSets.size()-1);
	}
	
	public void setVar(Symbol v, Obj o) {
		//Pre 2016.Feb.24 Code:
		//varSets.get(varSets.size()-1).setVar(v, o);
		
		// Cond is >=1 because we don't need to check the globals
		for(int i = varSets.size()-1; i >=1; i--) {
			if(varSets.get(i).hasVar(v) || varSets.get(i).isModule()) {
				varSets.get(i).setVar(v, o);
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
			throw new AyaRuntimeException("Variable " + var.name() + " not found");
		} else {
			return res;
		}
	}
	
	private Obj getVarOrNull(Symbol id) {
		Obj res = null;
		for(int i = varSets.size()-1; i >= 0; i--) {
			res = varSets.get(i).getObj(id);
			if(res != null) {
				return res;
			}
		}
		return null;
	}
	
	public void add(VariableSet vs) {
		varSets.add(vs);
	}

	public boolean isDefined(Symbol id) {
		return getVarOrNull(id) != null;
	}

	public void setVars(VariableSet varSet) {
		for (Map.Entry<Symbol, Obj> p : varSet.getMap().entrySet()) {
			setVar(p.getKey(), p.getValue());
		}
	}
}
