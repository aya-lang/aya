package element.variable;

import element.Element;
import element.entities.Block;

public class ModuleFactory {
	long id;
	Block b;
	
	public ModuleFactory(String name, Block b) {
		this.id = Variable.encodeString(name);
		this.b = b;
	}
	
	public Module getModule() {
		VariableSet module = new VariableSet(true);
		Element.getInstance().getVars().add(module);
		b.eval();
		module = Element.getInstance().getVars().popGet();
		return new Module(id, module);
	}
}
