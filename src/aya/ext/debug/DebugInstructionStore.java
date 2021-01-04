package aya.ext.debug;

import aya.instruction.named.NamedInstructionStore;

public class DebugInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		// JSON
		addInstruction(new PauseDebugInstruction());
	}
}
