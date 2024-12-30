package aya.ext.plot.instruction;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.ext.plot.ColorCycle;
import aya.ext.plot.RenderConfig;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.util.Casting;
import aya.util.CircleIterator;
import aya.util.DictReader;
import aya.util.Sym;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.Color;
import java.util.ArrayList;

public class PieChartInstruction extends NamedOperator {
    public PieChartInstruction() {
        super("plot.pie_chart");
        _doc = ("pie_chart : renders a pie-chart\n"
                + "  title::str : plot title\n"
                + "  bgcolor::color/str : background color\n"
                + "  color_cycle::list : override the default color cycle\n"
                + "  legend::num (bool) : show legend. default=true\n"
                + "  labels::num (bool) : show labels. default=true\n"
                + "  labels_on_chart::num (bool) : overlay the labels onto the chart. default=false\n"
                + RenderConfig.getDocString("  ")
                + "  data::list : list of num or dict\n"
                + "      ::num : The value of the entry. Same as :{ num:value}\n"
                + "      ::dict :\n"
                + "          label::str : label of the entry. defaults to index\n"
                + "          value::str : value of the entry\n"
        );
    }

    @Override
    public void execute(BlockEvaluator blockEvaluator) {
        Obj a = blockEvaluator.pop();

        if (a.isa(Obj.DICT)) {
            Input input = new Input(new DictReader((Dict) a, this._name));
            createPieChart(this._name, input);
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
        public final boolean labels;
        public final boolean labels_on_chart;
        public final RenderConfig renderConfig;
        /**
         * Provided as a list, where each entry is either:
         * <p>- a dictionary, e.g. :{ "str":label 123:value}
         * <p>- a number, in which case the index is used as the label
         */
        public final java.util.List<DatasetEntry> data;

        public Input(DictReader d) {
            this.title = d.getString(Sym.sym("title"));
            this.bgcolor = d.getColor(Sym.sym("bgcolor"), Color.WHITE);
            this.color_cycle = new ColorCycle(d);
            this.legend = d.getBool(Sym.sym("legend"), true);
            this.labels = d.getBool(Sym.sym("labels"), true);
            this.labels_on_chart = d.getBool(Sym.sym("labels_on_chart"), false);

            // renderConfig is read from the same dictionary for backwards compatibility with the old XY-Plot instructions.
            this.renderConfig = new RenderConfig(d);

            this.data = new ArrayList<>();
            List dataList = d.getListEx(Sym.sym("data"));
            for (int i = 0; i < dataList.length(); i++) {
                Obj entry = dataList.getExact(i);
                if (entry.isa(Obj.DICT)) {
                    DictReader entryD = new DictReader((Dict) entry, d.get_err_name() + ".data[" + i + "]");
                    this.data.add(new DatasetEntry(entryD, i));
                } else if (entry.isa(Obj.NUMBER)) {
                    double value = Casting.asNumber(entry).toDouble();
                    this.data.add(new DatasetEntry("" + i, value));
                } else {
                    throw new ValueError(d.get_err_name() + ".data[" + i + "]: expected a dictionary or number, but found a " + Obj.typeName(entry.type()));
                }
            }
        }

        static class DatasetEntry {
            /** Optional: by default the index is used */
            public final String label;
            public final double value;

            public DatasetEntry(DictReader entryD, int i) {
                this.label = entryD.getString(Sym.sym("label"), "" + i);
                this.value = entryD.getDoubleEx(Sym.sym("value"));
            }

            public DatasetEntry(String label, double value) {
                this.label = label;
                this.value = value;
            }
        }
    }

    public static void createPieChart(String opName, Input input) {
        DefaultKeyedValues pieValues = new DefaultKeyedValues();
        for (Input.DatasetEntry entry : input.data) {
            pieValues.addValue(entry.label, entry.value);
        }
        DefaultPieDataset pieDataset = new DefaultPieDataset(pieValues);

        JFreeChart chart = ChartFactory.createPieChart(input.title, pieDataset, input.legend, true, false);

        // apply style
        ChartFactory.getChartTheme().apply(chart);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(input.bgcolor);
        plot.setSimpleLabels(input.labels_on_chart);
        if (!input.labels) {
            plot.setLabelGenerator(null);
        }
        CircleIterator<Color> colorIterator = input.color_cycle.makeIterator();
        for (Input.DatasetEntry entry : input.data) {
            plot.setSectionPaint(entry.label, colorIterator.next());
        }

        input.renderConfig.renderChart(opName, chart);
    }
}
