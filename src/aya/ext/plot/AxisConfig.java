package aya.ext.plot;

import static aya.util.Sym.sym;

import java.awt.Color;
import java.text.DecimalFormat;

import aya.exceptions.runtime.ValueError;
import aya.obj.number.Number;
import aya.util.DictReader;
import aya.util.Pair;
import aya.util.Sym;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;

public class AxisConfig {
    public static String getDocString(String padLeft) {
        return ("axis configuration\n"
                + padLeft + "gridlines::bool : show axis gridlines\n"
                + padLeft + "gridline_color::color/str : axis gridline color\n"
                + padLeft + "zeroline::bool : show axis zero line\n"
                + padLeft + "visible::bool : draw axis labels\n"
                + padLeft + "lim::list : min and max axis limits\n"
                + padLeft + "label::str : axis label\n"
                + padLeft + "numberformat::str : a pattern string as described in 'https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html'\n"
        );
    }

    public static class Limit {
        public Limit(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public final double min;
        public final double max;
    }

    public final boolean gridlines;
    public final Color gridline_color;
    public final boolean zeroline;
    public final boolean visible;
    public final Limit limit;
    public final String label;
    public final DecimalFormat numberformat;

    public AxisConfig() {
        gridlines = false;
        gridline_color = Color.DARK_GRAY;
        zeroline = true;
        visible = true;
        limit = null;
        label = "";
        numberformat = null;
    }

    public AxisConfig(DictReader d) {
        this(d, new AxisConfig());
    }

    public AxisConfig(DictReader d, AxisConfig defaults) {
        gridlines = d.getBool(Sym.sym("gridlines"), defaults.gridlines);
        gridline_color = d.getColor(Sym.sym("gridline_color"), defaults.gridline_color);
        zeroline = d.getBool(Sym.sym("zeroline"), defaults.zeroline);
        visible = d.getBool(Sym.sym("visible"), defaults.visible);

        if (d.hasKey(sym("lim"))) {
            Pair<Number, Number> lim = d.getNumberPairEx(sym("lim"));
            limit = new Limit(lim.first().toDouble(), lim.second().toDouble());
        } else {
            limit = defaults.limit;
        }

        label = d.getString(Sym.sym("label"), defaults.label);

        if (d.hasKey(Sym.sym("numberformat"))) {
            String formatStr = d.getString(sym("numberformat"));
            try {
                numberformat = new DecimalFormat(formatStr);
            } catch (Exception e) {
                throw new ValueError(d.get_err_name() + ".numberformat: " + e.getMessage());
            }
        } else {
            numberformat = defaults.numberformat;
        }
    }

    public void applyConfig(XYPlot plot, boolean isX) {
        if (isX) {
            plot.setDomainGridlinePaint(this.gridline_color);
            plot.setDomainGridlinesVisible(this.gridlines);
            plot.setDomainZeroBaselineVisible(this.zeroline);

            applyAxisConfig(plot.getDomainAxis());
        } else {
            plot.setRangeGridlinePaint(this.gridline_color);
            plot.setRangeGridlinesVisible(this.gridlines);
            plot.setRangeZeroBaselineVisible(this.zeroline);

            applyAxisConfig(plot.getRangeAxis());
        }
    }

    public void applyConfig(CategoryPlot plot, boolean isX) {
        if (isX) {
            plot.setDomainGridlinePaint(this.gridline_color);
            plot.setDomainGridlinesVisible(this.gridlines);
            // category plot has no concept of "zero" for the x-axis

            applyAxisConfig(plot.getDomainAxis());
        } else {
            plot.setRangeGridlinePaint(this.gridline_color);
            plot.setRangeGridlinesVisible(this.gridlines);
            plot.setRangeZeroBaselineVisible(this.zeroline);

            applyAxisConfig(plot.getRangeAxis());
        }
    }

    private void applyAxisConfig(Axis chartAxis) {
        chartAxis.setLabel(this.label);
        chartAxis.setVisible(this.visible);

        if (this.limit != null && chartAxis instanceof ValueAxis) {
            ValueAxis valueAxis = (ValueAxis) chartAxis;
            valueAxis.setRange(this.limit.min, this.limit.max);
        }

        if (chartAxis instanceof NumberAxis) {
            NumberAxis numberAxis = (NumberAxis) chartAxis;
            numberAxis.setAutoRangeIncludesZero(false);
            if (this.numberformat != null) {
                numberAxis.setNumberFormatOverride(this.numberformat);
            }
        }
    }
}