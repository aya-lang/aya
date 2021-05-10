package aya.ext.plot;
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
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
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.util.Casting;
import aya.util.DictReader;
import aya.util.Pair;

@SuppressWarnings("serial")
public class FreeChartInterface extends JFrame 
{	
	
	private static class PlotDataset {
		public XYDataset data;
		public XYLineAndShapeRenderer renderer;
	}
	
	private static ArrayList<PlotDataset> makeDataset(List datasets) {
		ArrayList<PlotDataset> out = new ArrayList<FreeChartInterface.PlotDataset>();
		
		for (int dataset_index = 0; dataset_index < datasets.length(); dataset_index++) {
			Obj dataset_obj = datasets.getExact(dataset_index);
			if (dataset_obj.isa(Obj.DICT)) {
				DictReader dataset = new DictReader(Casting.asDict(dataset_obj), "plot");
				double[] x = dataset.getListEx(SymbolConstants.X).toNumberList().todoubleArray();
				double[] y = dataset.getListEx(SymbolConstants.Y).toNumberList().todoubleArray();
				double yclip_min = -9e99;
				double yclip_max = 9e99;
				if (dataset.hasKey(sym("yclip"))) {
					Pair<Number, Number> yclip_pair = dataset.getNumberPairEx(sym("yclip"));
					yclip_min = yclip_pair.first().toDouble();
					yclip_max = yclip_pair.second().toDouble();
				}

				if (x.length == y.length) {
					// Copy data into XYSeries
					final XYSeries series = new XYSeries(dataset.getString(sym("label"), ""), false);
					for (int i = 0; i < x.length; i++) {
						double yi = y[i];
						if (yi < yclip_min || yi > yclip_max) yi = Double.NaN;
						if (Double.isNaN(yi)) {
							System.out.println(y[i]);
						}
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
					r.setSeriesPaint(0, dataset.getColor(sym("color"), Color.BLACK));
					r.setSeriesStroke(0, new BasicStroke((float) dataset.getDouble(sym("weight"), 1)));
					r.setBaseLinesVisible(dataset.getBool(sym("lines"), true));
					r.setBaseShapesVisible(dataset.getBool(sym("points"), false));
					
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
	
	private static JFreeChart drawChart2(Dict plot_dict) {
		DictReader d = new DictReader(plot_dict, "plot");
		
		ArrayList<PlotDataset> data = makeDataset(d.getListEx(sym("data")));
		
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
		
		// X Axis
		Dict xaxis_dict = d.getDict(sym("xaxis"));
		// If it is not provided, use an empty one so all defaults are used
		if (xaxis_dict == null) xaxis_dict = new Dict();
		DictReader ax = new DictReader(xaxis_dict, "plot.xaxis");
		plot.setDomainGridlinePaint(ax.getColor(sym("gridline_color"), Color.DARK_GRAY));
		plot.setDomainGridlinesVisible(ax.getBool(sym("gridlines"), false));
		plot.setDomainZeroBaselineVisible(ax.getBool(sym("zeroline"), true));

		// Y Axis
		Dict yaxis_dict = d.getDict(sym("yaxis"));
		// If it is not provided, use an empty one so all defaults are used
		if (yaxis_dict == null) yaxis_dict = new Dict();
		ax = new DictReader(yaxis_dict, "plot.yaxis");
		plot.setRangeGridlinePaint(ax.getColor(sym("gridline_color"), Color.DARK_GRAY));
		plot.setRangeGridlinesVisible(ax.getBool(sym("gridlines"), false));
		plot.setRangeZeroBaselineVisible(ax.getBool(sym("zeroline"), true));

		// X limits
		if (d.hasKey(sym("xlim"))) {
			Pair<Number, Number> lim = d.getNumberPairEx(sym("xlim"));
			plot.getDomainAxis().setRange(lim.first().toDouble(), lim.second().toDouble());
		}

		// Y limits
		if (d.hasKey(sym("ylim"))) {
			Pair<Number, Number> lim = d.getNumberPairEx(sym("ylim"));
			plot.getRangeAxis().setRange(lim.first().toDouble(), lim.second().toDouble());
		}
		
		return chart;
	}
	
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
		
	private static Symbol sym(String s)
	{
		return Aya.getInstance().getSymbols().getSymbol(s);
	}
	
	private static List str(String s)
	{
		return List.fromString(s);
	}

	public static void main( String[ ] args ) 
	{		
		List x1 = new List();
		for (double d = -5; d < 5; d += 0.1) {
			x1.mutAdd(new Num(d));
		}
			
		// Datasets
		Dict d1 = new Dict();
		Dict d2 = new Dict();
		List y1 = new List();
		List y2 = new List();
		
		for (int i = 0; i < x1.length(); i++) {
			Number n = (Number)x1.getExact(i);
			y1.mutAdd(n.sin());
			y2.mutAdd(n.cos());
		}
		
		Dict d3 = new Dict();
		List x3 = new List();
		List y3 = new List();
		for (double t = 0; t < (2 * 3.14); t += 0.1) {
			x3.mutAdd(new Num(3 * Math.cos(t)));
			y3.mutAdd(new Num(0.4 * Math.sin(t)));
		}
		
		d1.set(sym("x"), x1);
		d1.set(sym("y"), y1);
		d2.set(sym("x"), x1);
		d2.set(sym("y"), y2);
		d3.set(sym("x"), x3);
		d3.set(sym("y"), y3);
		
		d1.set(sym("color"), str("blue"));
		d1.set(sym("weight"), Num.fromInt(5));
		d1.set(sym("label"), str("sin(x)"));
		
		d2.set(sym("color"), str("orange"));
		d2.set(sym("label"), str("cos(x)"));
		d2.set(sym("lines"), Num.fromBool(false));
		d2.set(sym("points"), Num.fromBool(true));
		
		d3.set(sym("color"), str("green"));
		d3 .set(sym("label"), str("polar"));
		
		List datasets = new List();
		datasets.mutAdd(d1);
		datasets.mutAdd(d2);
		datasets.mutAdd(d3);

		Dict params = new Dict();
		params.set(sym("title"), str("Title"));
		params.set(sym("xlabel"), str("X Label"));
		params.set(sym("ylabel"), str("Y Label"));
		params.set(sym("data"), datasets);
		params.set(sym("yaxis_grid"), Num.fromBool(true));
		
		List xlim = new List();
		xlim.mutAdd(new Num(-3.0));
		xlim.mutAdd(new Num(8.0));
		params.set(sym("xlim"), xlim);

		List ylim = new List();
		ylim.mutAdd(new Num(-1.5));
		ylim.mutAdd(new Num(0.7));
		params.set(sym("ylim"), ylim);
		
		plot(params);
	}
	
	
}

