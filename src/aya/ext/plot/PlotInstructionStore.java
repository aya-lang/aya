package aya.ext.plot;

import aya.ext.plot.instruction.BoxPlotInstruction;
import aya.ext.plot.instruction.MultiPlotInstruction;
import aya.ext.plot.instruction.PieChartInstruction;
import aya.ext.plot.instruction.PlotInstruction;
import aya.instruction.named.NamedInstructionStore;

public class PlotInstructionStore extends NamedInstructionStore {

    @Override
    protected void init() {
        addInstruction(new PlotInstruction());
        addInstruction(new MultiPlotInstruction());
        addInstruction(new BoxPlotInstruction());
        addInstruction(new PieChartInstruction());
    }
}
