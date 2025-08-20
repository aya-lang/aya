package aya.ext.unicode;

import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;

import java.util.Arrays;
import java.util.Collection;

public class UnicodeInstructionStore implements NamedInstructionStore {

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(
				new FromCodePointsInstruction(),
				new ToCodePointsInstruction(),
				new UTF16FromCodeUnitsInstruction(),
				new UTF16ToCodeUnitsInstruction()
		);
	}
}
