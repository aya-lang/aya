package aya.obj.dict;

import aya.Element;
import aya.obj.block.Block;
import aya.variable.VariableSet;

/** DictFactories sit on the instruction stack. When evoked, they generate a dict
 * given the current scope of variables
 * @author Nick
 *
 */
public class DictFactory {
	Block b;
	
	public DictFactory(Block b) {
		this.b = b;
	}
	
	/** Run the dict, collect variables, return the Dict object */
	public Dict getDict() {
		
		//Capture all assignments within the scope
		VariableSet module = new VariableSet(true);
		
		//Ass the variable set to the stack
		Element.getInstance().getVars().add(module);
		
		//Run the block
		b.duplicate().eval();
		
		//Retrieve the variableSet
		module = Element.getInstance().getVars().popGet();
		
		//Create a new dict using the assignments from the variable set
		return new Dict(module);
	}
	
	@Override
	public String toString() {
		return b.toString();
	}
}
