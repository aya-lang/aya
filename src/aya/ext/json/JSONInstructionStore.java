package aya.ext.json;

import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;

import java.util.Arrays;
import java.util.Collection;

public class JSONInstructionStore implements NamedInstructionStore {

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(
				new ToJSONInstruction(),
				new LoadJSONInstruction()
		);
	}
}
