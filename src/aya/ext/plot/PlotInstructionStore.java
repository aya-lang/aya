package aya.ext.plot;

import aya.instruction.named.NamedInstructionStore;

public class PlotInstructionStore extends NamedInstructionStore {
	
	@Override
	protected void init() {
		// JSON
		addInstruction(new LegacyPlotInstruction());
	}
}
