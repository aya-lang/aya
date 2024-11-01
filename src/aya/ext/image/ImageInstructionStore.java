package aya.ext.image;

import aya.ext.image.instruction.GetImageFormatsInstruction;
import aya.ext.image.instruction.ReadImageInstruction;
import aya.ext.image.instruction.WriteImageInstruction;
import aya.instruction.named.NamedInstructionStore;

public class ImageInstructionStore extends NamedInstructionStore {

	@Override
	protected void init() {
		addInstruction(new GetImageFormatsInstruction());
		addInstruction(new ReadImageInstruction());
		addInstruction(new WriteImageInstruction());
	}
}
