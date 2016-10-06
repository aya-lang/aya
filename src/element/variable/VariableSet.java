package element.variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import element.ElemTypes;
import element.entities.Block;
import element.exceptions.TypeError;
import element.util.Pair;

public class VariableSet {
	private Variable[] argNames;
	private byte[] argTypes;
	private HashMap<Long, Object> vars;
	private boolean is_module;

	
	public VariableSet(Variable[] argNames, byte[] argTypes) {
		this.argTypes = argTypes;
		this.argNames = argNames;
		this.vars = new HashMap<Long, Object>();
		this.is_module = false;
	}
	
	public VariableSet(boolean is_module) {
		this.argNames = null;
		this.argTypes = null;
		this.vars = new HashMap<Long, Object>();
		this.is_module = is_module;
	}
	
	public boolean isModule() {
		return is_module;
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
				Object o = b.pop();
				if(!ElemTypes.isType(argTypes[i], o)) {
					throw new TypeError("{ARGS}", ElemTypes.getTypeNameFromID(argTypes[i]), o);
				}
					//throw new RuntimeException("Invalid type in block argument. Expected (" + TypeUtils.getTypeNameFromID(argTypes[i]) + "). Recieved " + TypeUtils.debugString(o)+ " (" + TypeUtils.getTypeName(o) + ")");
				
				vars.put(argNames[i].getID(), o);
			}
		}
	}
	
	public void setVar(Variable v, Object o) {
		vars.put(v.getID(),o);
	}
	
	
	public void setVar(long v, Object o) {
		vars.put(v,o);
	}
	
	public Object getObject(long id) {
		return vars.get(id);
	}
	
	public Object getObject(Variable v) {
		return vars.get(v.getID());
	}
	
	public HashMap<Long, Object> getMap() {
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
		StringBuilder sb = new StringBuilder("{");
		for(Variable v : argNames) {
			sb.append(v.toString() + " ");
		}
		sb.append("|");
		Iterator<Entry<Long, Object>> it = vars.entrySet().iterator();
		boolean addComma = false;
	    while (it.hasNext()) {
	    	if(addComma) {
	    		sb.append(", ");
	    	}
	        Map.Entry<Long, Object> pair = (Map.Entry<Long, Object>)it.next();
	        sb.append(Variable.decodeLong(pair.getKey()) + " = " + pair.getValue());
	        addComma = true;
	    }
	    sb.append("}");
		return sb.toString();
	}
	
	
	public String show() {
		StringBuilder sb = new StringBuilder("");
		if(argNames != null) {
			for(Variable v : argNames) {
				sb.append(v.toString() + " ");
			}
		}
		sb.append(",");
		return sb.toString();
	}
	
	/** Sets all vars in the VariableSet */
	public void setAllVars(HashMap<Long, Object> map) {
		this.vars = map;
	}
	
	@SuppressWarnings("unchecked")
	@Override public VariableSet clone() {
		VariableSet out = new VariableSet(argNames, argTypes);
		out.setAllVars((HashMap<Long, Object>)vars.clone());
		return out;
	}

	public void clear() {
		vars.clear();
	}
	
	/** Add all variables from `other` to this var set. Overwrite if needed */
	public void merge(VariableSet other) {
		Iterator<Entry<Long, Object>> it = other.vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Long,Object> pair = (Map.Entry<Long, Object>)it.next();
	    	this.setVar(pair.getKey(), pair.getValue());
	    }
	}

	/** Return all variables as a list of pairs */
	public ArrayList<Pair<Variable, Object>> getAllVars() {
		ArrayList<Pair<Variable,Object>> out = new ArrayList<Pair<Variable, Object>>();
		Iterator<Entry<Long, Object>> it = vars.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Long,Object> pair = (Map.Entry<Long, Object>)it.next();
	    	out.add(new Pair<Variable, Object>(new Variable(pair.getKey()), pair.getValue()));
	    }
	    return out;
	}
	
}
