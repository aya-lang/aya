package element.variable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import element.ElemTypes;
import element.Element;
import element.InteractiveElement;
import element.exceptions.SyntaxError;
import element.parser.Parser;

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

	public VariableData(Element elem) {
		initGlobals(elem);
	}
	
	public void initGlobals(Element elem) {
		VariableSet globals = new VariableSet(null, null);
		
		globals.setVar(new Variable("import"), Parser.compile("`(\".elem\"+G~)", elem));
		globals.setVar(new Variable("version"), Element.VERSION_NAME);
		globals.setVar(new Variable("help"), InteractiveElement.HELP_TEXT);

		globals.setVar(new Variable("e"), Math.E);				
		globals.setVar(new Variable("pi"), Math.PI);

		varSets.add(globals);
	}
	
	/** Returns the set containing the global variables */
	public VariableSet getGlobals() {
		return varSets.get(0);
	}
	
	/** Returns (varname) = (debugString(o)) ((default variable)) for each global variable */
	public ArrayList<String> getDefaultVariableDiscs(Element elem) {
		if(varSets.size() < 1) {
			initGlobals(elem);
		}
		
		ArrayList<String> out = new ArrayList<String>();
		Iterator<Map.Entry<Long, Object>> entries = varSets.get(0).getMap().entrySet().iterator();
		while (entries.hasNext()) {
		  Entry<Long, Object> thisEntry = (Entry<Long, Object>) entries.next();
		  out.add(Variable.decodeLong(thisEntry.getKey()) + " = " + ElemTypes.show(thisEntry.getValue()) + "\n(default variable)");
		}
		return out;
	}
	
	/** clears all but the global variables */
	public void reset() {
		for (int i = varSets.size()-1; i >=1; i--) {
			varSets.remove(i);
		}
	}
	
	public void setGlobalVar(Variable v, Object o) {
		varSets.get(0).setVar(v, o);
	}
	
	public void setGlobalVar(long v, Object o) {
		varSets.get(0).setVar(v, o);
	}
	
	public void pop() {
		varSets.remove(varSets.size()-1).clear();
	}
	
	public VariableSet popGet() {
		return varSets.remove(varSets.size()-1);
	}
	
	public void setVar(Variable v, Object o) {
		setVar(v.getID(), o);
	}
	
	public void setVar(long v, Object o) {
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
	
	public Object getVar(Variable v) {
		Object res = null;
		for(int i = varSets.size()-1; i >= 0; i--) {
			res = varSets.get(i).getObject(v);
			if(res != null) {
				return res;
			}
		}
		throw new SyntaxError("Variable " + v.toString() + " not found");
	}
	
	public void add(VariableSet vs) {
		varSets.add(vs);
	}
}
