package aya.ext.image;

import aya.instruction.named.NamedInstructionStore;

public class ImageInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		// JSON
		addInstruction(new ReadImageInstruction());
		addInstruction(new WriteImageInstruction());
	}
}
