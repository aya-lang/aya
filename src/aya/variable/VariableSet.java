package aya.variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import aya.exceptions.TypeError;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.util.Pair;

public class VariableSet {
	private Variable[] argNames;
	private long[] argTypes;
	private HashMap<Long, Obj> vars;
	// Setting this to true will tell VariableData to assign new variables here
	// It emulates every variable being declared local
	private boolean captureAllAssignments;

	
	public VariableSet(Variable[] argNames, long[] argTypes) {
		this.argTypes = argTypes;
		this.argNames = argNames;
		this.vars = new HashMap<Long, Obj>();
		this.captureAllAssignments = false;
	}
	
	public VariableSet(boolean is_module) {
		this.argNames = null;
		this.argTypes = null;
		this.vars = new HashMap<Long, Obj>();
		this.captureAllAssignments = is_module;
	}
	
	public boolean isModule() {
		return captureAllAssignments;
	}
	
	public void setArgs(Block b) {
		if(argNames == null) {
			return;
		}
		
		//Assign in reverse order

		if(argTypes == null) {
			for(int i = argNames.length-1; i >= 0; i--) {
				vars.put(argNames[i].getID(), b.pop());
			}
		} else {
			for(int i = argNames.length-1; i >= 0; i--){
				Obj o = b.pop();
				boolean typematch = false; // The type matches the assertion
				
				// Check user defined type 
				if (o.isa(Obj.DICT)) {
					long otype = -1;

					Obj dtype = ((Dict)o).getFromMetaTableOrNull(Obj.SYM_TYPE.id());
					if (dtype != null && dtype.isa(Obj.SYMBOL)) {
						otype = ((Symbol)dtype).id(); 
					} else {
						otype = Obj.SYM_DICT.id();
					}
					
					if (otype == argTypes[i]) {
						typematch = true;
					}
				} else if (argTypes[i] == Obj.SYM_ANY.id() || o.isa(Obj.symToID(argTypes[i]))) {
					typematch = true;
				}
				
				if( !typematch ) {
					throw new TypeError("{ARGS}", Symbol.fromID(argTypes[i]).repr(), o);
				}
				
				vars.put(argNames[i].getID(), o);
			}
		}
	}
	
	public void setVar(Variable v, Obj o) {
		vars.put(v.getID(),o);
	}
	
	
	public void setVar(long v, Obj o) {
		vars.put(v,o);
	}
	
	public Obj getObject(long id) {
		return vars.get(id);
	}
	
	public Obj getObject(Variable v) {
		return vars.get(v.getID());
	}
	
	public HashMap<Long, Obj> getMap() {
		return vars;
	}
	
	/** Returns true if this set contains a definition for the variable v */
	public boolean hasVar(Variable v) {
		return vars.containsKey(v.getID());
	}
	
	/** Returns true if this set contains a definition for the variable v */
	public boolean hasVar(long v) {
		return vars.containsKey(v);
	}
	
	public String toString() {
//		StringBuilder sb = new StringBuilder("{");
//		for(Variable v : argNames) {
//			sb.append(v.toString() + " ");
//		}
//		sb.append("|");
//		Iterator<Entry<Long, Obj>> it = vars.entrySet().iterator();
//		boolean addComma = false;
//	    while (it.hasNext()) {
//	    	if(addComma) {
//	    		sb.append(", ");
//	    	}
//	        Map.Entry<Long, Obj> pair = (Map.Entry<Long, Obj>)it.next();
//	        sb.append(Variable.decodeLong(pair.getKey()) + " = " + pair.getValue());
//	        addComma = true;
//	    }
//	    sb.append("}");
//		return sb.toString();
		return show();
	}
	
	
	public String show() {
		StringBuilder sb = new StringBuilder("");
		if(argNames != null) {
			for(Variable v : argNames) {
				sb.append(v.toString() + " ");
			}
		}
		
		if(vars != null && vars.size() > 0) {
			sb.append(": ");
			Iterator<Entry<Long, Obj>> it = vars.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Long, Obj> pair = (Map.Entry<Long, Obj>)it.next();
				sb.append(Variable.decodeLong(pair.getKey()) + " ");
			}
		}
		
		sb.append(",");
		return sb.toString();
	}
	
	/** Sets all vars in the VariableSet */
	public void setAllVars(HashMap<Long, Obj> map) {
		this.vars = map;
	}
	
	@SuppressWarnings("unchecked")
	@Override public VariableSet clone() {
		VariableSet out = new VariableSet(argNames, argTypes);
		out.setAllVars((HashMap<Long, Obj>)vars.clone());
		return out;
	}
	
	/** Create a deep copy of the variable set */
	public VariableSet deepcopy() {
		VariableSet out = new VariableSet(argNames, argTypes);
		for (Long l : vars.keySet()) {
			out.setVar(l, vars.get(l).deepcopy());
		}
		return out;
	}

	public void clear() {
		vars.clear();
	}
	
	/** Add all variables from `other` to this var set. Overwrite if needed */
	public void merge(VariableSet other) {
		Iterator<Entry<Long, Obj>> it = other.vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Long,Obj> pair = (Map.Entry<Long, Obj>)it.next();
	    	this.setVar(pair.getKey(), pair.getValue());
	    }
	}

	/** Return all variables as a list of pairs */
	public ArrayList<Pair<Variable, Obj>> getAllVars() {
		ArrayList<Pair<Variable,Obj>> out = new ArrayList<Pair<Variable, Obj>>();
		Iterator<Entry<Long, Obj>> it = vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Long,Obj> pair = (Map.Entry<Long, Obj>)it.next();
	    	out.add(new Pair<Variable, Obj>(new Variable(pair.getKey()), pair.getValue()));
	    }
	    return out;
	}

	/** Return all variables */
	public ArrayList<Long> keys() {
		ArrayList<Long> out = new ArrayList<Long>();
		Iterator<Entry<Long, Obj>> it = vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Long,Obj> pair = (Map.Entry<Long, Obj>)it.next();
	    	out.add(pair.getKey());
	    }
	    return out;
	}
	
	/** Return all Objs */
	public ArrayList<Obj> values() {
		ArrayList<Obj> out = new ArrayList<Obj>();
		Iterator<Entry<Long, Obj>> it = vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Long,Obj> pair = (Map.Entry<Long, Obj>)it.next();
	    	out.add(pair.getValue());
	    }
	    return out;
	}
	
	
}
