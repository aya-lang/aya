package aya.ext.graphics;

import aya.instruction.named.NamedInstructionStore;

public class GraphicsInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		// JSON
		addInstruction(new LegacyGraphicsInstruction());
	}
}
