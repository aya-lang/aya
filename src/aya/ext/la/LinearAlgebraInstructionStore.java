package aya.ext.la;

import aya.instruction.named.NamedInstructionStore;

public class LinearAlgebraInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		addInstruction(new MatMulInstruction());
	}
}
