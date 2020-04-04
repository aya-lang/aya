package aya.ext.dialog;

import aya.instruction.named.NamedInstructionStore;

public class DialogInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		// JSON
		addInstruction(new LegacyDialogInstruction());
	}
}
