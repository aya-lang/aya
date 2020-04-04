package aya.ext.fstream;

import aya.instruction.named.NamedInstructionStore;

public class FStreamInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		// JSON
		addInstruction(new LegacyFStreamInstruction());
	}
}
