package aya.ext.plot.instruction;

import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.ext.plot.AxisConfig;
import aya.ext.plot.ColorCycle;
import aya.ext.plot.RenderConfig;
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
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BoxPlotInstruction extends NamedInstruction {
    public BoxPlotInstruction() {
        super("plot.box_plot");
        _doc = ("box_plot : renders a box-and-whisker plot\n"
                + "  title::str : plot title\n"
                + "  bgcolor::color/str : background color\n"
                + "  color_cycle::list : override the default color cycle\n"
                + "  legend::num (bool) : default=true\n"
                + "  show_median::num (bool) : show median line on each entry. default=true\n"
                + "  show_average::num (bool) : show average-circle on each entry. default=false\n"
                + "  show_far_outliers::num (bool) : mark statistical outliers at the top/bottom of the plot with triangles. default=false\n"
                + "  x,y::dict : " + AxisConfig.getDocString("      ")
                + RenderConfig.getDocString("  ")
                + "  data::list : list of dictionaries\n"
                + "      entry::str : The label that is shared across each group. Entries with the same label receive the same color.\n"
                + "      group::str : The label of the group this entry belongs to. The entries in a group are placed directly next to each other.\n"
                + "      values::list : number list containing the values of this entry\n"
        );
    }

    @Override
    public void execute(Block block) {
        Obj a = block.pop();

        if (a.isa(Obj.DICT)) {
            Input input = new Input(new DictReader((Dict) a, this._name));
            createBoxPlot(this._name, input);
        } else {
            throw new TypeError(this, "D", a);
        }
    }

    /**
     * This separates all input-reading logic from the actual plotting logic.
     */
    private static class Input {
        public final String title;
        /** Background color for the plot - default: White */
        public final Color bgcolor;
        public final ColorCycle color_cycle;
        public final boolean legend;
        public final boolean show_median;
        public final boolean show_average;
        public final boolean show_far_outliers;
        public final AxisConfig x;
        public final AxisConfig y;
        public final RenderConfig renderConfig;
        public final java.util.List<DatasetEntry> data;

        public Input(DictReader d) {
            this.title = d.getString(Sym.sym("title"));
            this.bgcolor = d.getColor(Sym.sym("bgcolor"), Color.WHITE);
            this.color_cycle = new ColorCycle(d);
            this.legend = d.getBool(Sym.sym("legend"), true);
            this.show_median = d.getBool(Sym.sym("show_median"), true);
            this.show_average = d.getBool(Sym.sym("show_average"), false);
            this.show_far_outliers = d.getBool(Sym.sym("show_far_outliers"), false);
            this.x = new AxisConfig(d.getDictReader(SymbolConstants.X));
            this.y = new AxisConfig(d.getDictReader(SymbolConstants.Y));

            // renderConfig is read from the same dictionary for backwards compatibility with the old XY-Plot instructions.
            this.renderConfig = new RenderConfig(d);

            this.data = new ArrayList<>();
            List dataList = d.getListEx(Sym.sym("data"));
            for (int i = 0; i < dataList.length(); i++) {
                Obj entry = dataList.getExact(i);
                if (!entry.isa(Obj.DICT)) {
                    throw new ValueError(d.get_err_name() + ".data[" + i + "] must be a dict");
                }

                DictReader entryD = new DictReader((Dict) entry, d.get_err_name() + ".data[" + i + "]");
                this.data.add(new DatasetEntry(entryD, i));
            }
        }

        static class DatasetEntry {
            /**
             * The label that is shared across each group.
             * <p> Datasets with the same entry receive the same color.
             */
            public final String entry;

            /**
             * The label of the group this dataset belongs to.
             * <p> The entries in a group are placed directly next to each other.
             */
            public final String group;

            public final double[] values;

            public DatasetEntry(DictReader entryD, int i) {
                this.entry = entryD.getString(Sym.sym("entry"), "" + i);
                this.group = entryD.getString(Sym.sym("group"), "");
                try {
                    this.values = entryD.getListEx(Sym.sym("values")).toNumberList().todoubleArray();
                } catch (Exception e) {
                    throw new ValueError(entryD.get_err_name() + ".values: " + e.getMessage());
                }
            }
        }

    }

    public static void createBoxPlot(String opName, Input input) {
        DefaultBoxAndWhiskerCategoryDataset jDataset = new DefaultBoxAndWhiskerCategoryDataset();
        for (Input.DatasetEntry entry : input.data) {
            java.util.List<Double> valueList = Arrays.stream(entry.values).boxed().collect(Collectors.toList());
            BoxAndWhiskerItem boxItem = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(valueList);
            jDataset.add(new BoxAndWhiskerItemWrapper(boxItem, input.show_far_outliers), entry.entry, entry.group);
        }

        BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        CategoryPlot plot = new CategoryPlot(jDataset, new CategoryAxis(null), new NumberAxis(null), renderer);
        JFreeChart chart = new JFreeChart(input.title, JFreeChart.DEFAULT_TITLE_FONT, plot, input.legend);

        // apply style
        ChartFactory.getChartTheme().apply(chart);

        input.x.applyConfig(plot, true);
        input.y.applyConfig(plot, false);
        plot.setBackgroundPaint(input.bgcolor);

        renderer.setBaseToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        renderer.setMedianVisible(input.show_median);
        renderer.setMeanVisible(input.show_average);
        CircleIterator<Color> colorIterator = input.color_cycle.makeIterator();
        for (Object rowKey : jDataset.getRowKeys()) {
            int i = jDataset.getRowIndex((Comparable<?>) rowKey);
            Color color = colorIterator.next();
            renderer.setSeriesFillPaint(i, color);
            renderer.setSeriesPaint(i, color);
        }

        input.renderConfig.renderChart(opName, chart);
    }

    private static class BoxAndWhiskerItemWrapper extends BoxAndWhiskerItem {

        public BoxAndWhiskerItemWrapper(BoxAndWhiskerItem other, boolean enableFarOutlier) {
            super(
                    other.getMean(), other.getMedian(), other.getQ1(), other.getQ3(),
                    other.getMinRegularValue(), other.getMaxRegularValue(),
                    enableFarOutlier ? other.getMinOutlier() : new Double(Double.NEGATIVE_INFINITY),
                    enableFarOutlier ? other.getMaxOutlier() : new Double(Double.POSITIVE_INFINITY),
                    other.getOutliers()
            );
        }
    }
}
