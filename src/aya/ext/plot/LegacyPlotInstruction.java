package aya.ext.plot;

import aya.exceptions.TypeError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;

public class LegacyPlotInstruction extends NamedInstruction {
	
	public LegacyPlotInstruction() {
		super("plot.plot");
		_doc = ("plot\n"
				+ "  parameters:\n"
				+ "    plottype (::line ::scatter)\n"
				+ "    title S\n"
				+ "    xlabel S\n"
				+ "    ylabel S\n"
				+ "    height D\n"
				+ "    width D\n"
				+ "    xaxis [minD maxD]\n"
				+ "    yaxis [minD maxD]\n"
				+ "    x L<N>\n"
				+ "    y [[nameS strokeD color[r g b] dataL], ..]\n"
				+ "    show B\n"
				+ "    legend B\n"
				+ "    horizontal B\n"
				+ "    filename S\n");
	}

	@Override
	public void execute(Block block) {
		Obj a = block.pop();

		if (a.isa(Obj.DICT)) {
			ChartParams cp = ChartParams.parseParams((Dict)a);
			FreeChartInterface.drawChart(cp);
		} else {
			throw new TypeError(this, "D", a);
		}
	}

}
