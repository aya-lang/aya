package aya.ext.plot;

import aya.ext.plot.instruction.BoxPlotInstruction;
import aya.ext.plot.instruction.MultiPlotInstruction;
import aya.ext.plot.instruction.PieChartInstruction;
import aya.ext.plot.instruction.PlotInstruction;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;

import java.util.Arrays;
import java.util.Collection;

public class PlotInstructionStore implements NamedInstructionStore {

	@Override
	public Collection<NamedOperator> getNamedInstructions() {
		return Arrays.asList(
				new PlotInstruction(),
				new MultiPlotInstruction(),
				new BoxPlotInstruction(),
				new PieChartInstruction()
		);
	}
}
