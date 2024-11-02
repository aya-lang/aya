package aya.ext.date;

import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;

import java.util.Arrays;
import java.util.Collection;

public class DateInstructionStore implements NamedInstructionStore {

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(
				new ParseDateInstruction(),
				new FormatDateInstruction(),
				new DescribeDateInstruction()
		);
	}
}
