package aya.ext.plot;

import static aya.util.Sym.sym;

import java.awt.Color;

import aya.obj.number.Number;
import aya.util.DictReader;
import aya.util.Pair;

/**
 * Configuration object for a single dataset (or series) in a plot
 * 
 * @author npaul
 *
 */
class SeriesConfig {
	Color color;
	boolean use_color_cycle;
	float stroke;
	boolean lines;
	boolean points;
	double yclip_min;
	double yclip_max;

	public SeriesConfig() {
		color = Color.BLACK;
		use_color_cycle = true;
		stroke = 1.0f;
		lines = true;
		points = false;
		yclip_min = -9e99;
		yclip_max = 9e99;
	}
	
	public SeriesConfig copy() {
		SeriesConfig cfg = new SeriesConfig();
		cfg.color = color;
		cfg.use_color_cycle = use_color_cycle;
		cfg.stroke = stroke;
		cfg.lines = lines;
		cfg.points = points;
		cfg.yclip_min = yclip_min;
		cfg.yclip_max = yclip_max;
		return cfg;
	}

	/**
	 * Load configuration from a dict
	 * 
	 * @param d The dict to read from
	 * @param defaults If a value is missing from d, use the value here instead
	 * @return
	 */
	public static SeriesConfig fromDict(DictReader d, SeriesConfig defaults) {
		SeriesConfig cfg = defaults.copy();

		cfg.stroke = (float)(d.getDouble(sym("stroke"), defaults.stroke));
		cfg.points = d.getBool(sym("points"), defaults.points);
		cfg.lines = d.getBool(sym("lines"), defaults.lines);
		
		Color c = d.getColor(sym("color"));
		if (c == null) {
			cfg.use_color_cycle = true;
		} else {
			cfg.use_color_cycle = false;
			cfg.color = c;
		}

		if (d.hasKey(sym("yclip"))) {
			Pair<Number, Number> yclip_pair = d.getNumberPairEx(sym("yclip"));
			cfg.yclip_min = yclip_pair.first().toDouble();
			cfg.yclip_max = yclip_pair.second().toDouble();
		}
		
		return cfg;
	}
}