package aya.ext.plot;

import static aya.util.Sym.sym;

import java.awt.BasicStroke;
import java.awt.Color;

import aya.obj.number.Number;
import aya.util.CircleIterator;
import aya.util.DictReader;
import aya.util.Pair;
import aya.util.Sym;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 * Configuration object for a single dataset (or series) in a plot
 * @author npaul
 */
public class SeriesConfig {
    public static String getDocString(String leftPad) {
        return (leftPad + "color::str/dict : default=uses the color_cycle\n"
                + leftPad + "stroke::num : stroke-weight. default=1.0\n"
                + leftPad + "points::num (bool) : draw points. default=false\n"
                + leftPad + "lines::num (bool) : draw lines between points. default=true\n"
                + leftPad + "yclip::list ([min,max]) : y values outside of this range will not\n"
                + leftPad + "    be rendered, they will create a jump in the graph. default=no clipping\n"
        );
    }

    public final Color color;
    public final boolean use_color_cycle;
    public final float stroke;
    public final boolean lines;
    public final boolean points;
    public final double yclip_min;
    public final double yclip_max;

    /** Constructs the default SeriesConfig */
    public SeriesConfig() {
        this.color = Color.BLACK;
        this.use_color_cycle = true;
        this.stroke = 1.0f;
        this.lines = true;
        this.points = false;
        this.yclip_min = -9e99;
        this.yclip_max = 9e99;
    }

    /** Reads a SeriesConfig from the Dict, using the default values as a fallback */
    public SeriesConfig(DictReader d) {
        this(d, new SeriesConfig());
    }

    public SeriesConfig(DictReader d, SeriesConfig defaults) {
        Color c = d.getColor(Sym.sym("color"));
        this.color = c == null ? defaults.color : c;
        this.use_color_cycle = c == null;
        this.stroke = (float) d.getDouble(Sym.sym("stroke"), defaults.stroke);
        this.points = d.getBool(Sym.sym("points"), defaults.points);
        this.lines = d.getBool(Sym.sym("lines"), defaults.lines);
        if (d.hasKey(Sym.sym("yclip"))) {
            Pair<Number, Number> yclip_pair = d.getNumberPairEx(sym("yclip"));
            this.yclip_min = yclip_pair.first().toDouble();
            this.yclip_max = yclip_pair.second().toDouble();
        } else {
            this.yclip_min = defaults.yclip_min;
            this.yclip_max = defaults.yclip_max;
        }
    }

    public double clip(double y) {
        return (y < yclip_min || y > yclip_max) ? Double.NaN : y;
    }

    public void apply(XYLineAndShapeRenderer renderer, CircleIterator<Color> colorCycle) {
        renderer.setSeriesPaint(0, use_color_cycle ? colorCycle.next() : color);
        renderer.setSeriesStroke(0, new BasicStroke(stroke));
        renderer.setBaseLinesVisible(lines);
        renderer.setBaseShapesVisible(points);
    }

}