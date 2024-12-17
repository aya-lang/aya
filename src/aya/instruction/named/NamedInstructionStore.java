package aya.instruction.named;

import java.util.HashMap;

import aya.StaticData;

public abstract class NamedInstructionStore {
	
	private HashMap<String, NamedOperator> _instructions;

	public NamedInstructionStore() {
		_instructions = new HashMap<String, NamedOperator>();
		init();
	}
	
	protected void addInstruction(NamedOperator inst) {
		_instructions.put(inst.getName(), inst);
	}

	public NamedOperator getInstruction(String name) {
		return _instructions.get(name);
	}
	
	public void initHelpData(StaticData staticData) {
		for (HashMap.Entry<String, NamedOperator> pair : _instructions.entrySet()) {
			NamedOperator i = pair.getValue();
			String doc = i._doc;
			if (doc == null || doc.equals("")) continue;
			staticData.addHelpText(":(" + i.getName() + ")\n  " + doc);
		}
	}
	
	protected abstract void init();
}
