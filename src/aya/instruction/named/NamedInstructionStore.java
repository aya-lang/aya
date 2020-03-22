package aya.instruction.named;

import java.util.HashMap;

import aya.Aya;

public abstract class NamedInstructionStore {
	
	private HashMap<String, NamedInstruction> _instructions;

	public NamedInstructionStore() {
		_instructions = new HashMap<String, NamedInstruction>();
		init();
	}
	
	protected void addInstruction(NamedInstruction inst) {
		_instructions.put(inst.getName(), inst);
	}

	public NamedInstruction getInstruction(String name) {
		return _instructions.get(name);
	}
	
	public void initHelpData(Aya aya) {
		for (HashMap.Entry<String, NamedInstruction> pair : _instructions.entrySet()) {
			NamedInstruction i = pair.getValue();
			String doc = i._doc;
			if (doc == null || doc.equals("")) continue;
			aya.addHelpText(":{" + i.getName() + "}\n  " + doc);
		}
	}
	
	protected abstract void init();
}
