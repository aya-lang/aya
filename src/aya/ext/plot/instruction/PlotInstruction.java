package aya.ext.plot.instruction;

import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.ext.plot.AxisConfig;
import aya.ext.plot.ColorCycle;
import aya.ext.plot.RenderConfig;
import aya.ext.plot.SeriesConfig;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.symbol.SymbolConstants;
import aya.util.CircleIterator;
import aya.util.DictReader;
import aya.util.Sym;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Color;
import java.util.ArrayList;

public class PlotInstruction extends NamedInstruction {

    public PlotInstruction() {
        super("plot.plot");
        _doc = ("plot : renders an XY-plot\n"
                + "  title::str : plot title\n"
                + "  bgcolor::color/str : background color\n"
                + "  color_cycle::list : override the default color cycle\n"
                + "  legend::num (bool) : show legend. default=true\n"
                + "  x,y::dict : " + AxisConfig.getDocString("      ")
                + RenderConfig.getDocString("  ")
                + "  <any value in dataset params will be used as the default value>\n"
                + "      for example, setting stroke to 10 here will make it the default\n"
                + "      for all lines that do not explicitly provide one\n"
                + "  data::list :\n"
                + "      x::list : domain data\n"
                + "      y::list : range data\n"
                + "      label::str : name of the dataset\n"
                + SeriesConfig.getDocString("      ")
        );
    }

    @Override
    public void execute(Block block) {
        Obj a = block.pop();

        if (a.isa(Obj.DICT)) {
            Input input = new Input(new DictReader((Dict) a, this._name));
            createXYPlot(this._name, input);
        } else {
            throw new TypeError(this, "D", a);
        }
    }

    /**
     * This separates all input-reading logic from the actual plotting logic.
     */
    public static class Input {
        public final String title;
        /** Background color for the plot - default: White */
        public final Color bgcolor;
        public final ColorCycle color_cycle;
        public final boolean legend;
        public final AxisConfig x;
        public final AxisConfig y;
        public final RenderConfig renderConfig;
        public final java.util.List<DatasetEntry> data;

        public Input(DictReader d) {
            this.title = d.getString(Sym.sym("title"));
            this.bgcolor = d.getColor(Sym.sym("bgcolor"), Color.WHITE);
            this.color_cycle = new ColorCycle(d);
            this.legend = d.getBool(Sym.sym("legend"), true);
            this.x = new AxisConfig(d.getDictReader(SymbolConstants.X));
            this.y = new AxisConfig(d.getDictReader(SymbolConstants.Y));

            // renderConfig is read from the same dictionary for backwards compatibility with the old XY-Plot instructions.
            this.renderConfig = new RenderConfig(d);

            SeriesConfig seriesDefaults = new SeriesConfig(d);

            this.data = new ArrayList<>();
            List dataList = d.getListEx(Sym.sym("data"));
            for (int i = 0; i < dataList.length(); i++) {
                Obj entry = dataList.getExact(i);
                if (!entry.isa(Obj.DICT)) {
                    throw new ValueError(d.get_err_name() + ".data[" + i + "] must be a dict");
                }

                DictReader entryD = new DictReader((Dict) entry, d.get_err_name() + ".data[" + i + "]");
                this.data.add(new DatasetEntry(entryD, seriesDefaults));
            }
        }

        public static class DatasetEntry {
            public final double[] x;
            public final double[] y;
            public final String label;
            public final SeriesConfig seriesConfig;

            public DatasetEntry(DictReader entryD, SeriesConfig seriesDefaults) {
                this.x = entryD.getListEx(SymbolConstants.X).toNumberList().todoubleArray();
                this.y = entryD.getListEx(SymbolConstants.Y).toNumberList().todoubleArray();
                this.label = entryD.getString(Sym.sym("label"), "");
                this.seriesConfig = new SeriesConfig(entryD, seriesDefaults);

                if (x.length != y.length) {
                    throw new ValueError(entryD.get_err_name() + ": x/y values in dataset must be equal length");
                }
            }
        }
    }

    public static void createXYPlot(String opName, Input input) {
        JFreeChart chart = ChartFactory.createXYLineChart(input.title, null, null, null);
        XYPlot xyPlot = chart.getXYPlot();

        CircleIterator<Color> colorCycle = input.color_cycle.makeIterator();
        for (int idx_dataset = 0; idx_dataset < input.data.size(); idx_dataset++) {
            Input.DatasetEntry dataset = input.data.get(idx_dataset);
            XYSeries series = new XYSeries(dataset.label, false);
            for (int i = 0; i < dataset.x.length; i++) {
                double x = dataset.x[i];
                double y = dataset.seriesConfig.clip(dataset.y[i]);
                series.add(x, y);
            }

            XYSeriesCollection col = new XYSeriesCollection();
            try {
                col.addSeries(series);
            } catch (IllegalArgumentException e) {
                throw new ValueError(opName + ": Each series must have a unique (or empty) name");
            }

            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            dataset.seriesConfig.apply(renderer, colorCycle);

            xyPlot.setDataset(idx_dataset, col);
            xyPlot.setRenderer(idx_dataset, renderer);
        }

        xyPlot.setBackgroundPaint(input.bgcolor);
        input.x.applyConfig(xyPlot, true);
        input.y.applyConfig(xyPlot, false);
        if (!input.legend) {
            chart.removeLegend();
        }

        input.renderConfig.renderChart(opName, chart);
    }

}
