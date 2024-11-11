package aya.ext.debug;

import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;

import java.util.Arrays;
import java.util.Collection;

public class DebugInstructionStore implements NamedInstructionStore {

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(
				new PauseDebugInstruction()
		);
	}
}
