package aya.plugin.builtins;

import java.util.HashMap;

import aya.instruction.named.NamedInstruction;
import aya.instruction.named.NamedInstructionStore;
import aya.plugin.json.LoadJSONInstruction;
import aya.plugin.json.ToJSONInstruction;

public class BuiltinInstructionStore extends NamedInstructionStore {

	private HashMap<String, NamedInstruction> _instructions;
	
	public BuiltinInstructionStore() {
		_instructions = new HashMap<String, NamedInstruction>();
		init();
	}
	
	private void addInstruction(NamedInstruction inst) {
		_instructions.put(inst.getName(), inst);
	}

	@Override
	public NamedInstruction getInstruction(String name) {
		return _instructions.get(name);
	}
	
	private void init() {
		// JSON
		addInstruction(new ToJSONInstruction());
		addInstruction(new LoadJSONInstruction());
	}
}
