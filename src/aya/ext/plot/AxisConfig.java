package aya.ext.plot;

import static aya.util.Sym.sym;

import java.awt.Color;

import aya.obj.number.Number;
import aya.util.DictReader;
import aya.util.Pair;

class AxisConfig {
	
	public static class Limit {
		public Limit(double min, double max) {
			this.min = min;
			this.max = max;
		}
		public double min;
		public double max;
	}
	
	Color gridline_color;
	boolean gridlines;
	boolean zeroline;
	String numberformat;
	Limit limit;
	boolean visible;
	String label;
	
	public AxisConfig() {
		gridline_color = Color.DARK_GRAY;
		gridlines = false;
		zeroline = true;
		numberformat = null;
		limit = null;
		visible = true;
		label = "";
	}

	public AxisConfig(DictReader d) {
		this();
		this.update(d);
	}

	public AxisConfig updateCopy(DictReader d) {
		AxisConfig ax = copy();
		ax.update(d);
		return ax;
	}
	
	private AxisConfig copy() {
		AxisConfig ax = new AxisConfig();
		ax.gridline_color = gridline_color;
		ax.gridlines = gridlines;
		ax.zeroline = zeroline;
		ax.numberformat = numberformat;
		ax.visible = visible;
		ax.limit = limit;
		ax.label = label;
		return ax;
	}
	
	private void update(DictReader d) {
		gridline_color = d.getColor(sym("gridline_color"), gridline_color);
		gridlines = d.getBool(sym("gridlines"), gridlines);
		zeroline = d.getBool(sym("zeroline"), zeroline);
		visible = d.getBool(sym("visible"), visible);
		label = d.getString(sym("label"), label);

		if (d.hasKey(sym("numberformat"))) {
			numberformat = d.getString(sym("numberformat"));
		}

		if (d.hasKey(sym("lim"))) {
			Pair<Number, Number> lim = d.getNumberPairEx(sym("lim"));
			limit = new Limit(lim.first().toDouble(), lim.second().toDouble());
		}

	}
}