package element.obj.dict;

import element.Element;
import element.entities.Block;
import element.variable.VariableSet;

public class DictFactory {
	Block b;
	
	public DictFactory(Block b) {
		this.b = b;
	}
	
	public Dict getDict() {
		
		//Capture all assignments within the scope
		VariableSet module = new VariableSet(true);
		
		//Ass the variable set to the stack
		Element.getInstance().getVars().add(module);
		
		//Run the block
		b.eval();
		
		//Retrieve the variableSet
		module = Element.getInstance().getVars().popGet();
		
		//Create a new dict using the assignments from the variable set
		return new Dict(module);
		
	}
}
