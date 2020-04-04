package aya.ext.sys;

import aya.instruction.named.NamedInstructionStore;

public class SystemInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		// JSON
		addInstruction(new LegacySystemInstruction());
	}
}
