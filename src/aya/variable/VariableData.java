package aya.variable;

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
import aya.obj.list.Str;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
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

	public final Dict OBJ_NIL = new Dict();

	public VariableData(Aya aya) {
		initGlobals(aya);
	}
	
	private void initNil(Aya aya) {
		Dict nil_meta = new Dict();
		nil_meta.set("__type__", Symbol.fromStr("__nil"));
		Str nil_str = new Str("nil");
		nil_meta.set("__str__", nil_str);
		nil_meta.set("__repr__", nil_str);
		nil_meta.set("__pushself__", Num.ONE);

		nil_meta.set("__eq__", Parser.compile("; :T ::__nil =", aya));
		
		OBJ_NIL.setMetaTable(nil_meta);
	}
	
	public void initGlobals(Aya aya) {
		VariableSet globals = new VariableSet(null, null, null);
		
		globals.setVar(new Variable("import"), Parser.compile("`(\".aya\"+G~)", aya));
		globals.setVar(new Variable("version"), new Str(Aya.VERSION_NAME));
		globals.setVar(new Variable("help"), new Str(InteractiveAya.HELP_TEXT));

		globals.setVar(new Variable("e"), Num.E);				
		globals.setVar(new Variable("pi"), Num.PI);
		
		globals.setVar(new Variable("block"), OBJ_BLOCK);
		globals.setVar(new Variable("sym"), OBJ_SYM);
		globals.setVar(new Variable("list"), OBJ_LIST);
		globals.setVar(new Variable("num"), OBJ_NUM);
		globals.setVar(new Variable("char"), OBJ_CHAR);
		globals.setVar(new Variable("str"), OBJ_STR);
		
		initNil(aya);
		globals.setVar(Variable.encodeString("nil"), OBJ_NIL);

		varSets.add(globals);
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
		Iterator<Map.Entry<Long, Obj>> entries = varSets.get(0).getMap().entrySet().iterator();
		while (entries.hasNext()) {
		  Entry<Long, Obj> thisEntry = (Entry<Long, Obj>) entries.next();
		  out.add(Variable.decodeLong(thisEntry.getKey()) + " = " + thisEntry.getValue().repr() + "\n(default variable)");
		}
		return out;
	}
	
	/** clears all but the global variables */
	public void reset() {
		for (int i = varSets.size()-1; i >=1; i--) {
			varSets.remove(i);
		}
	}
	
	public void setGlobalVar(Variable v, Obj o) {
		varSets.get(0).setVar(v, o);
	}
	
	public void setGlobalVar(long v, Obj o) {
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
	
	public void setVar(Variable v, Obj o) {
		setVar(v.getID(), o);
	}
	
	public void setVar(long v, Obj o) {
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
	
	public Obj getVar(Variable v) {
		return getVar(v.getID());
	}
	
	public Obj getVar(long id) {
		Obj res = null;
		for(int i = varSets.size()-1; i >= 0; i--) {
			res = varSets.get(i).getObj(id);
			if(res != null) {
				return res;
			}
		}
		throw new AyaRuntimeException("Variable " + Variable.decodeLong(id) + " not found");
	}
	
	public void add(VariableSet vs) {
		varSets.add(vs);
	}
}
