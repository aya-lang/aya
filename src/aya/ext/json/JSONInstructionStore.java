package aya.ext.json;

import aya.instruction.named.NamedInstructionStore;

public class JSONInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		// JSON
		addInstruction(new ToJSONInstruction());
		addInstruction(new LoadJSONInstruction());
	}
}
