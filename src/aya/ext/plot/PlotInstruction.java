package aya.ext.plot;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.dict.Dict;

public class PlotInstruction extends NamedOperator {
	
	public PlotInstruction() {
		super("plot.plot");
		_doc = ("plot\n"
			 + "  title::str : plot title\n"
			 + "  bgcolor::color/str : background color\n"
			 + "  color_cycle::list : override the default color cycle\n"
			 + "  x,y::dict : axis configuration\n"
			 + "      gridlines::bool : show axis gridlines\n"
			 + "      gridline_color::color/str : axis gridline color\n"
			 + "      zeroline::bool : show axis zero line\n"
			 + "      visible::bool : draw axis labels\n"
			 + "      lim::list : min and max axis limits\n"
			 + "      label::str : axis label\n"
			 + "  <any value in dataset params will be used as the default value>\n"
			 + "      for example, setting stroke to 10 here will make it the default\n"
			 + "      for all lines that do not explicitly provide one\n"
			 + "  data::list :\n"
			 + "      x::list : domain data\n"
			 + "      y::list : range data\n"
			 + "      label::str : name of the dataset\n"
			 + "      color::color/str : color of line/shapes\n"
			 + "      stroke::num : stroke weight (may be non-integer)\n"
			 + "      lines::num (bool) : draw lines between points\n"
			 + "      points::num (bool) : draw points\n"
			 + "      yclip::list ([min,max]) : y values outside of this range will not\n"
			 + "          be rendered, they will create a jump in the graph\n");
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj a = blockEvaluator.pop();

		if (a.isa(Obj.DICT)) {
			FreeChartInterface.plot((Dict)a);
		} else {
			throw new TypeError(this, "D", a);
		}
	}

}
