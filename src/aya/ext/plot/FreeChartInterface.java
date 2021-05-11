package aya.ext.plot;
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import aya.Aya;
import aya.AyaPrefs;
import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.number.Number;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.util.Casting;
import aya.util.CircleIterator;
import aya.util.ColorFactory;
import aya.util.DictReader;
import aya.util.ObjToColor;
import aya.util.Pair;

public class FreeChartInterface
{	

	///////////////////////////
	// Public Plot Interface //
	///////////////////////////

	/**
	 * Generate a plot using a dictionary
	 * 
	 * @param plot_dict
	 */
	public static void plot(Dict plot_dict)
	{
		DictReader d = new DictReader(plot_dict, "plot");
		JFrame frame = new JFrame();
		JFreeChart chart = drawChart2(plot_dict);
		ChartPanel cp = new ChartPanel(chart);
		
		int width  = d.getInt(SymbolConstants.WIDTH, 500);
		int height = d.getInt(SymbolConstants.HEIGHT, 400);
		cp.setPreferredSize(new java.awt.Dimension(width, height));
		frame.setContentPane(cp);

		// Save chart
		String filename = d.getString(sym("filename"), "");
		if (!filename.equals("")) {
			String path = AyaPrefs.getWorkingDir() + filename;
			File file;
			try {
				if (path.contains(".png")) {
					file = new File(path); 
					ChartUtilities.saveChartAsPNG(file, chart, width, height);
				} else if (path.contains(".jpg")) {
					file = new File(path); 
					ChartUtilities.saveChartAsJPEG(file, chart, width, height);
				} else {
					throw new ValueError("Plot: Please specify either '*.png' ot '*.jpg' in the filename\n"
							+ "Received: " + filename);
				}
			} catch (IOException e) {
				throw new IOError("plot", path, e);
			}
		}
		
		if (d.getBool(sym("show"), true)) {
			frame.pack();
			RefineryUtilities.centerFrameOnScreen(frame);
			frame.setVisible(true);
		}
		
		
	}


	////////////////////////////
	// Private Helper Methods //
	////////////////////////////

	/**
	 * Configuration object for a single dataset (or series) in a plot
	 * 
	 * @author npaul
	 *
	 */
	private static class SeriesConfig {
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
	
	private static class PlotDataset {
		public XYDataset data;
		public XYLineAndShapeRenderer renderer;
	}
	
	/**
	 * Convert a aya.obj.list.List into an ArrayList<PlotDataset> objects
	 * 
	 * @param datasets List to load datasets from 
	 * @param colors Color cycle iterator to load colors from if they are not provided by the dataset
	 * @param default_series_config Default configuration to load from if a value is not provided by the dataset
	 * @return
	 */
	private static ArrayList<PlotDataset> makeDataset(List datasets,
													  CircleIterator<Color> colors,
													  SeriesConfig default_series_config) {

		ArrayList<PlotDataset> out = new ArrayList<FreeChartInterface.PlotDataset>();
		
		for (int dataset_index = 0; dataset_index < datasets.length(); dataset_index++) {
			Obj dataset_obj = datasets.getExact(dataset_index);
			if (dataset_obj.isa(Obj.DICT)) {
				DictReader dataset = new DictReader(Casting.asDict(dataset_obj), "plot");
				double[] x = dataset.getListEx(SymbolConstants.X).toNumberList().todoubleArray();
				double[] y = dataset.getListEx(SymbolConstants.Y).toNumberList().todoubleArray();
				SeriesConfig cfg = SeriesConfig.fromDict(dataset, default_series_config);

				if (x.length == y.length) {
					// Copy data into XYSeries
					final XYSeries series = new XYSeries(dataset.getString(sym("label"), ""), false);
					for (int i = 0; i < x.length; i++) {
						double yi = y[i];
						if (yi < cfg.yclip_min || yi > cfg.yclip_max) yi = Double.NaN;
						series.add(x[i], yi);
					}

					// Create XYDataset from the XYSeries
					XYSeriesCollection col = new XYSeriesCollection();
					try {
						col.addSeries(series);
					} catch (IllegalArgumentException e) {
						throw new ValueError("Plot: Each series must have a unique (or empty) name");
					}
					
					// Create renderer
					XYLineAndShapeRenderer r = new XYLineAndShapeRenderer();
					Color c = dataset.getColor(sym("color"));
					if (cfg.use_color_cycle) {
						r.setSeriesPaint(0, colors.next());
					} else {
						r.setSeriesPaint(0, c);
					}

					r.setSeriesStroke(0, new BasicStroke(cfg.stroke));
					r.setBaseLinesVisible(cfg.lines);
					r.setBaseShapesVisible(cfg.points);
					
					PlotDataset pd = new PlotDataset();
					pd.data = col;
					pd.renderer = r;
					
					out.add(pd);
				} else {
					throw new ValueError("plot: x/y values in dataset must be equal length");
				}
			} else {
				throw new TypeError("plot: Expected dataset value to be a dict, got:\n " + dataset_obj.repr());
			}
		}
		
		return out;
	}
	
	private static CircleIterator<Color> makeColorIterator(DictReader d) {
		ArrayList <Color> colors = new ArrayList<Color>();
		if (d.hasKey(sym("color_cycle"))) {
			List colors_obj = d.getListEx(sym("color_cycle"));
			if (colors_obj.length() > 0) {
				for (int i = 0; i < colors_obj.length(); i++) {
					colors.add(ObjToColor.objToColorEx(colors_obj.getExact(i), "plot.color_cycle"));
				}
			} else {
				throw new ValueError("plot.color_cycle: Expected non-empty list for 'color_cycle', got empty list");
			}
		} else {
			// matplotlib default color cycle
			colors.add(ColorFactory.web("#1f77b4"));
			colors.add(ColorFactory.web("#ff7f0e"));
			colors.add(ColorFactory.web("#2ca02c"));
			colors.add(ColorFactory.web("#d62728"));
			colors.add(ColorFactory.web("#9467bd"));
			colors.add(ColorFactory.web("#8c564b"));
			colors.add(ColorFactory.web("#e377c2"));
			colors.add(ColorFactory.web("#7f7f7f"));
			colors.add(ColorFactory.web("#bcbd22"));
			colors.add(ColorFactory.web("#17becf"));
		}
		return new CircleIterator<Color>(colors);
	}
	
	
	private static void configAxes(String type, DictReader plot_dr, XYPlot plot) {
		boolean is_x = type.equals("xaxis");
		Dict axis_dict = plot_dr.getDict(sym(type));
		// If it is not provided, use an empty one so all defaults are used
		if (axis_dict == null) axis_dict = new Dict(); 
		
		DictReader d = new DictReader(axis_dict, "plot." + type);

		if (is_x) {
			plot.setDomainGridlinePaint(d.getColor(sym("gridline_color"), Color.DARK_GRAY));
			plot.setDomainGridlinesVisible(d.getBool(sym("gridlines"), false));
			plot.setDomainZeroBaselineVisible(d.getBool(sym("zeroline"), true));
		} else {
			plot.setRangeGridlinePaint(d.getColor(sym("gridline_color"), Color.DARK_GRAY));
			plot.setRangeGridlinesVisible(d.getBool(sym("gridlines"), false));
			plot.setRangeZeroBaselineVisible(d.getBool(sym("zeroline"), true));
		}
		
		ValueAxis ax = is_x ? plot.getDomainAxis() : plot.getRangeAxis();
		ax.setVisible(d.getBool(sym("visible"), true));

		if (ax instanceof NumberAxis) {
			NumberAxis nax = (NumberAxis)ax;
			nax.setAutoRangeIncludesZero(false);
			
			if (d.hasKey(sym("numberformat"))) {
				try {
					nax.setNumberFormatOverride(new DecimalFormat(d.getString(sym("numberformat"))));
				} catch (IllegalArgumentException e) {
					throw new ValueError(e.getMessage());
				}
			}
		}
		
		// View limits
		Symbol lim_key = sym(is_x ? "xlim" : "ylim");
		if (plot_dr.hasKey(lim_key)) {
			Pair<Number, Number> lim = plot_dr.getNumberPairEx(lim_key);
			ax.setRange(lim.first().toDouble(), lim.second().toDouble());
		}

	}
	
	
	private static JFreeChart drawChart2(Dict plot_dict) {
		DictReader d = new DictReader(plot_dict, "plot");
		
		SeriesConfig defaults = SeriesConfig.fromDict(d, new SeriesConfig());
		ArrayList<PlotDataset> data = makeDataset(d.getListEx(sym("data")), makeColorIterator(d), defaults);
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				d.getString(sym("title"), ""),
				d.getString(sym("xlabel"), ""),
				d.getString(sym("ylabel"), ""),
				null);

		// Chart config
		if (!d.getBool(sym("legend"), true)) {
			chart.removeLegend();
		}
		
		// Add data
		XYPlot plot = (XYPlot) chart.getPlot();
		for (int i = 0; i < data.size(); i++)
		{
			PlotDataset pd = data.get(i);
			plot.setDataset(i, pd.data);
			plot.setRenderer(i, pd.renderer);
		}
		
		// Plot config
		plot.setBackgroundPaint(d.getColor(sym("bgcolor"), Color.WHITE));
		
		// Axes config
		configAxes("xaxis", d, plot);
		configAxes("yaxis", d, plot);
		
		return chart;
	}
	
	
		
	private static Symbol sym(String s)
	{
		return Aya.getInstance().getSymbols().getSymbol(s);
	}
}

