package aya.ext.image;

import aya.ext.image.instruction.GetImageFormatsInstruction;
import aya.ext.image.instruction.ReadImageInstruction;
import aya.ext.image.instruction.WriteImageInstruction;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;

import java.util.Arrays;
import java.util.Collection;

public class ImageInstructionStore implements NamedInstructionStore {

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(
			new GetImageFormatsInstruction(),
			new ReadImageInstruction(),
			new WriteImageInstruction()
		);
	}
}
