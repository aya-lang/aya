package aya.ext.plot;
import static aya.util.Sym.sym;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JFrame;

import aya.util.FileUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.util.Casting;
import aya.util.CircleIterator;
import aya.util.ColorFactory;
import aya.util.DictReader;
import aya.util.ObjToColor;

import static aya.obj.symbol.SymbolConstants.X;
import static aya.obj.symbol.SymbolConstants.Y;

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
		JFreeChart chart;
		
		// Get top-level configs
		SeriesConfig series_cfg_defaults = SeriesConfig.fromDict(d, new SeriesConfig());
		AxisConfig domain_config = new AxisConfig();
		AxisConfig range_config = new AxisConfig();
		if (d.hasKey(X)) domain_config = domain_config.updateCopy(new DictReader(d.getDictEx(X)));
		if (d.hasKey(Y)) range_config = range_config.updateCopy(new DictReader(d.getDictEx(Y)));
		
		// Normal plot or multi-plot?
		if (d.hasKey(sym("subplots"))) {
			List subplots_obj = d.getListEx(sym("subplots"));
			ArrayList<DictReader> subplots = new ArrayList<DictReader>();
			for (int i = 0; i < subplots_obj.length(); i++) {
				Obj o = subplots_obj.getExact(i);
				if (o.isa(Obj.DICT)) {
					subplots.add(new DictReader(Casting.asDict(o)));
				} else {
					throw new ValueError("plot.subplots: expected list of plot objects. Got:\n" + o.str());
				}
			}

			chart = createMultiPlot(subplots, new MultiPlotConfig(d), series_cfg_defaults, domain_config, range_config);
		} else {
			chart = createSinglePlot(d, series_cfg_defaults, domain_config, range_config);
		}
		ChartPanel cp = new ChartPanel(chart);
		
		int width  = d.getInt(SymbolConstants.WIDTH, 500);
		int height = d.getInt(SymbolConstants.HEIGHT, 400);
		cp.setPreferredSize(new java.awt.Dimension(width, height));
		JFrame frame = new JFrame();
		frame.setContentPane(cp);

		// Save chart
		String filename = d.getString(sym("filename"), "");
		if (!filename.equals("")) {
			File exportFile = FileUtils.resolveFile(filename);
			try {
				if (filename.toLowerCase().endsWith(".png")) {
					ChartUtilities.saveChartAsPNG(exportFile, chart, width, height);
				} else if (filename.toLowerCase().endsWith(".jpg")) {
					ChartUtilities.saveChartAsJPEG(exportFile, chart, width, height);
				} else {
					throw new ValueError("Plot: Please specify either '*.png' ot '*.jpg' in the filename\n"
							+ "Received: " + filename);
				}
			} catch (IOException e) {
				throw new IOError("plot", exportFile.getAbsolutePath(), e);
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

		ArrayList<PlotDataset> out = new ArrayList<PlotDataset>();
		
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
	
	
	private static void configAxes(String type, XYPlot plot, AxisConfig config) {
		boolean is_x = type.equals("x");
		

		if (is_x) {
			plot.setDomainGridlinePaint(config.gridline_color);
			plot.setDomainGridlinesVisible(config.gridlines);
			plot.setDomainZeroBaselineVisible(config.zeroline);
		} else {
			plot.setRangeGridlinePaint(config.gridline_color);
			plot.setRangeGridlinesVisible(config.gridlines);
			plot.setRangeZeroBaselineVisible(config.zeroline);
		}
		
		ValueAxis ax = is_x ? plot.getDomainAxis() : plot.getRangeAxis();
		ax.setLabel(config.label);
		ax.setVisible(config.visible);
		// View limits
		if (config.limit != null) ax.setRange(config.limit.min, config.limit.max);

		if (ax instanceof NumberAxis) {
			NumberAxis nax = (NumberAxis)ax;
			nax.setAutoRangeIncludesZero(false);
			
			if (config.numberformat != null) {
				try {
					nax.setNumberFormatOverride(new DecimalFormat(config.numberformat));
				} catch (IllegalArgumentException e) {
					throw new ValueError(e.getMessage());
				}
			}
		}
	}
	
	private static void addXYData(XYPlot plot,
								  DictReader d,
								  SeriesConfig defaults,
								  AxisConfig domain_config,
								  AxisConfig range_config) {
		ArrayList<PlotDataset> data = makeDataset(d.getListEx(sym("data")), makeColorIterator(d), defaults);

		// Add data
		for (int i = 0; i < data.size(); i++)
		{
			PlotDataset pd = data.get(i);
			plot.setDataset(i, pd.data);
			plot.setRenderer(i, pd.renderer);
		}
		
		// Plot config
		plot.setBackgroundPaint(d.getColor(sym("bgcolor"), Color.WHITE));
		
		// Axes config
		if (d.hasKey(X)) domain_config = domain_config.updateCopy(new DictReader(d.getDictEx(X)));
		if (d.hasKey(Y)) range_config  = range_config.updateCopy(new DictReader(d.getDictEx(Y)));
		configAxes("x", plot, domain_config);
		configAxes("y", plot, range_config);
	}
	
	private static JFreeChart createSinglePlot(DictReader d,
											   SeriesConfig series_cfg_defaults,
											   AxisConfig domain_config,
											   AxisConfig range_config) {

		JFreeChart chart = ChartFactory.createXYLineChart(
				d.getString(sym("title"), ""), null, null, null);
		
		// Add XY data
		addXYData(chart.getXYPlot(), d, series_cfg_defaults, domain_config, range_config);

		// Chart config
		if (!d.getBool(sym("legend"), true)) {
			chart.removeLegend();
		}
		
		return chart;
	}
	

	/////////////////
	//   SUBPLOTS  //
	/////////////////

	private static class MultiPlotConfig {
		double gap;
		PlotOrientation orientation;
		String suptitle;
		
		public MultiPlotConfig(DictReader dict) {
			gap = dict.getDouble(sym("gap"), 10.0);
			suptitle = dict.getString(sym("suptitle"), "");
			Symbol orientation_sym = dict.getSymbol(sym("orient"), sym("vertical"));
			if (orientation_sym.equals(sym("vertical"))) {
				orientation = PlotOrientation.VERTICAL;
			} else if (orientation_sym.equals(sym("horizontal"))) {
				orientation = PlotOrientation.HORIZONTAL;
			} else {
				throw new ValueError("plot: ::orient must be one of [::vertical ::horizontal]");
			}
		}
	}
	
	private static JFreeChart createMultiPlot(ArrayList<DictReader> plots,
											  MultiPlotConfig config,
											  SeriesConfig series_cfg_defaults,
											  AxisConfig domain_config,
											  AxisConfig range_config) {
		final CombinedDomainXYPlot combined_plot = new CombinedDomainXYPlot();
		combined_plot.setGap(config.gap);


		for (DictReader d : plots) {
			JFreeChart chart = ChartFactory.createXYLineChart(
					d.getString(sym("title"), ""), null, null, null);
			
			AxisConfig d_conf = domain_config;
			AxisConfig r_conf = range_config;
			if (d.hasKey(SymbolConstants.X)) d_conf = d_conf.updateCopy(new DictReader(d.getDictEx(X)));
			if (d.hasKey(SymbolConstants.Y)) r_conf = r_conf.updateCopy(new DictReader(d.getDictEx(Y)));
			
			// Add XY data
			addXYData(chart.getXYPlot(), d, series_cfg_defaults, d_conf, r_conf);

			// Chart config
			if (!d.getBool(sym("legend"), true)) {
				chart.removeLegend();
			}
		
			combined_plot.add(chart.getXYPlot());
		}
		combined_plot.setOrientation(config.orientation);
		
		return new JFreeChart(config.suptitle, JFreeChart.DEFAULT_TITLE_FONT, combined_plot, true);
	}
	
		
	
}

