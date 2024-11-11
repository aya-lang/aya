package aya.ext.la;

import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;

import java.util.Arrays;
import java.util.Collection;

public class LinearAlgebraInstructionStore implements NamedInstructionStore {

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(
				new MatMulInstruction()
		);
	}
}
