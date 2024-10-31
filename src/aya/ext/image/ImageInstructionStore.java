package aya.ext.image;

import aya.ext.image.instruction.GetImageFormatsInstruction;
import aya.ext.image.instruction.ReadImage2Instruction;
import aya.ext.image.instruction.ReadImageInstruction;
import aya.ext.image.instruction.WriteImage2Instruction;
import aya.ext.image.instruction.WriteImageInstruction;
import aya.instruction.named.NamedInstructionStore;

public class ImageInstructionStore extends NamedInstructionStore {

	@Override
	protected void init() {
		addInstruction(new ReadImageInstruction());
		addInstruction(new WriteImageInstruction());
		addInstruction(new GetImageFormatsInstruction());
		addInstruction(new ReadImage2Instruction());
		addInstruction(new WriteImage2Instruction());
	}
}
