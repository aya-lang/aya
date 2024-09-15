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
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.util.CircleIterator;
import aya.util.DictReader;
import aya.util.Sym;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Color;
import java.util.ArrayList;

public class MultiPlotInstruction extends NamedInstruction {
    public MultiPlotInstruction() {
        super("plot.multiplot");
        _doc = ("multiplot : renders multiple XY-plots\n"
                + "  title::str : plot title\n"
                + "  gap::num : the gap between subplots in pixels. default=10.0\n"
                + "  orient::sym (one of [::vertical ::horizontal]) : orientation for arranging subplots. default=::vertical\n"
                + "  bgcolor::color/str : background color\n"
                + "  legend::num (bool) : show legend. default=true\n"
                + RenderConfig.getDocString("  ")
                + "  subplots::list<dict> :\n"
                + "      bgcolor::color/str : override the background color of this subplot\n"
                + "      color_cycle::list : override the default color cycle\n"
                + "      x,y::dict : " + AxisConfig.getDocString("          ")
                + "      data::list<dict> :\n"
                + "          x::list : domain data\n"
                + "          y::list : range data\n"
                + "          label::str : name of the dataset\n"
                + SeriesConfig.getDocString("          ")
                + "      <the options [color stroke points lines yclip] can be specified here as well, to provide defaults for all data entries>\n"
                + "  <the options [color_cycle x y color stroke points lines yclip] can be specified here as well, to provide defaults for all subplots>\n"
        );
    }

    @Override
    public void execute(Block block) {
        Obj a = block.pop();

        if (a.isa(Obj.DICT)) {
            Input input = new Input(new DictReader((Dict) a, this._name));
            createXYMultiPlot(this._name, input);
        } else {
            throw new TypeError(this, "D", a);
        }
    }

    /**
     * This separates all input-reading logic from the actual plotting logic.
     */
    private static class Input {
        public final String title;
        public final double gap;
        public final PlotOrientation orientation;
        /** Background color for the plot - default: White */
        public final Color bgcolor;
        public final boolean legend;
        public final RenderConfig renderConfig;
        public final java.util.List<SubPlot> subPlots;

        public Input(DictReader d) {
            this.title = d.getString(Sym.sym("title"));
            this.gap = d.getDouble(Sym.sym("gap"), 10.0);

            Symbol orient = d.getSymbol(Sym.sym("orient"), Sym.sym("vertical"));
            if(orient.equals(Sym.sym("vertical"))) {
                orientation = PlotOrientation.VERTICAL;
            }else if(orient.equals(Sym.sym("horizontal"))) {
                orientation = PlotOrientation.HORIZONTAL;
            }else{
                throw new ValueError(d.get_err_name() + ".orient : must be one of [::vertical ::horizontal]");
            }

            this.bgcolor = d.getColor(Sym.sym("bgcolor"), Color.WHITE);
            this.legend = d.getBool(Sym.sym("legend"), true);

            // renderConfig is read from the same dictionary for backwards compatibility with the old XY-Plot instructions.
            this.renderConfig = new RenderConfig(d);

            ColorCycle defaultColors = new ColorCycle(d);
            AxisConfig defaultX = new AxisConfig(d.getDictReader(SymbolConstants.X));
            AxisConfig defaultY = new AxisConfig(d.getDictReader(SymbolConstants.Y));
            SeriesConfig defaultSeries = new SeriesConfig(d);

            subPlots = new ArrayList<>();
            List subPlotList = d.getListEx(Sym.sym("subplots"));
            for (int i = 0; i < subPlotList.length(); i++) {
                Obj entry = subPlotList.getExact(i);
                if(!entry.isa(Obj.DICT)) {
                    throw new ValueError(d.get_err_name() + ".subplots[" + i + "] must be a dict");
                }

                DictReader entryD = new DictReader((Dict) entry, d.get_err_name() + ".subplots[" + i + "]");
                this.subPlots.add(new SubPlot(entryD, bgcolor, defaultColors, defaultX, defaultY, defaultSeries));
            }

        }

        /** This class mirrors {@link PlotInstruction.Input} however, only those options that make sense for a multi-plot. */
        static class SubPlot {
            public final Color bgcolor;
            public final ColorCycle color_cycle;
            public final AxisConfig x;
            public final AxisConfig y;
            public final java.util.List<PlotInstruction.Input.DatasetEntry> data;

            public SubPlot(DictReader entryD, Color defaultBg, ColorCycle defaultColors, AxisConfig defaultX, AxisConfig defaultY, SeriesConfig defaultSeries) {
                this.bgcolor = entryD.getColor(Sym.sym("bgcolor"), defaultBg);
                this.color_cycle = ColorCycle.getOrDefault(entryD, defaultColors);
                x = new AxisConfig(entryD.getDictReader(SymbolConstants.X), defaultX);
                y = new AxisConfig(entryD.getDictReader(SymbolConstants.Y), defaultY);
                defaultSeries = new SeriesConfig(entryD, defaultSeries);

                this.data = new ArrayList<>();
                List dataList = entryD.getListEx(Sym.sym("data"));
                for (int i = 0; i < dataList.length(); i++) {
                    Obj dataEntry = dataList.getExact(i);
                    if (!dataEntry.isa(Obj.DICT)) {
                        throw new ValueError(entryD.get_err_name() + ".data[" + i + "] must be a dict");
                    }

                    DictReader dataD = new DictReader((Dict) dataEntry, entryD.get_err_name() + ".data[" + i + "]");
                    this.data.add(new PlotInstruction.Input.DatasetEntry(dataD, defaultSeries));
                }
            }
        }
    }

    public static void createXYMultiPlot(String opName, Input input) {
        CombinedDomainXYPlot multiPlot = new CombinedDomainXYPlot();
        multiPlot.setGap(input.gap);
        multiPlot.setOrientation(input.orientation);
        multiPlot.setBackgroundPaint(input.bgcolor);

        for (Input.SubPlot subPlot : input.subPlots) {
            XYPlot xyPlot = ChartFactory.createXYLineChart(null, null, null, null).getXYPlot();

            CircleIterator<Color> colorCycle = subPlot.color_cycle.makeIterator();
            for (int idx_dataset = 0; idx_dataset < subPlot.data.size(); idx_dataset++) {
                PlotInstruction.Input.DatasetEntry dataset = subPlot.data.get(idx_dataset);
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

            xyPlot.setBackgroundPaint(subPlot.bgcolor);
            subPlot.x.applyConfig(xyPlot, true);
            subPlot.y.applyConfig(xyPlot, false);

            multiPlot.add(xyPlot);
        }

        JFreeChart multiChart = new JFreeChart(input.title, JFreeChart.DEFAULT_TITLE_FONT, multiPlot, input.legend);
        multiChart.setBackgroundPaint(input.bgcolor);
        input.renderConfig.renderChart(opName, multiChart);
    }
}
