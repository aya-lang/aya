package aya.ext.date;

import aya.instruction.named.NamedInstructionStore;

public class DateInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		addInstruction(new ParseDateInstruction());
		addInstruction(new FormatDateInstruction());
		addInstruction(new DescribeDateInstruction());
	}
}
