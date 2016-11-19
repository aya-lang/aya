package element.util;
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import element.ElemPrefs;
import element.entities.number.Num;
import element.entities.number.Numeric;
import element.exceptions.ElementRuntimeException;

@SuppressWarnings("serial")
public class FreeChartInterface extends JFrame 
{
   @SuppressWarnings("deprecation")
   public FreeChartInterface(ChartParams cp)
   {
      super(cp.getTitle());
      
      JFreeChart chart;
      
      if (cp.getType() == ChartParams.LINE) {
	      chart = ChartFactory.createXYLineChart(
	         cp.getTitle(),
	         cp.getXlabel(),
	         cp.getYlabel(),
	         createDataset(cp),
	         cp.isHorizontal() ? PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL,
	         cp.isLegend(),
	         true , false);
      } else {
	      chart = ChartFactory.createScatterPlot(cp.getTitle(),
	         cp.getXlabel(),
	         cp.getYlabel(),
	         createDataset(cp),
	         cp.isHorizontal() ? PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL,
	         cp.isLegend(),
	         true , false);
      }
      
      
      ChartPanel chartPanel = new ChartPanel(chart);
      chartPanel.setPreferredSize(new java.awt.Dimension(cp.getWidth(), cp.getHeight()));
      final XYPlot plot = chart.getXYPlot();
      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
      
      //Set series colors
      for (int i = 0; i < cp.getSeriesCount(); i++) {
    	  renderer.setSeriesPaint(i, cp.getColor(i));
      }
      
      //Set the series strokes
      for (int i = 0; i < cp.getSeriesCount(); i++) {
    	  renderer.setSeriesStroke(i, new BasicStroke(cp.getStroke(i)));
      }
      
      if (cp.useCustomXaxis()) {
	      ValueAxis domain = plot.getDomainAxis();
	      domain.setRange(cp.getXmin(), cp.getXmax());
      }
      if (cp.useCustomYaxis()) {
    	  ValueAxis range = plot.getRangeAxis();
    	  range.setRange(cp.getYmin(), cp.getYmax());
      }
      
      
      if (cp.getType() == ChartParams.SCATTER) {
    	  renderer.setShapesVisible(true); 
    	  renderer.setLinesVisible(false);
      } else {
    	  renderer.setShapesVisible(false); 
    	  renderer.setLinesVisible(true);
      }
            
      plot.setRenderer(renderer); 
      setContentPane(chartPanel); 
      
	   // Save chart
	   if (cp.getFilename() != null) {
		   String path = ElemPrefs.getWorkingDir() + cp.getFilename();
		   File file;
		   try {
			   if (path.contains(".png")) {
				   file = new File(path); 
				   ChartUtilities.saveChartAsPNG(file, chart, cp.getWidth(), cp.getHeight());
			   } else if (path.contains(".jpg")) {
				   file = new File(path); 
				   ChartUtilities.saveChartAsJPEG(file, chart, cp.getWidth(), cp.getHeight());
			   } else {
				   throw new ElementRuntimeException("Plot: Please specify either '*.png' ot '*.jpg' in the filename\n"
						   + "Recieved: " + cp.getFilename());
			   }
		   } catch (IOException e) {
			   throw new ElementRuntimeException("Unable to save plot to " + path);
		   }
	   }
   }
   
   private XYDataset createDataset(ChartParams cp)
   {
	   final XYSeriesCollection dataset = new XYSeriesCollection( );          

	      
	   for (int i = 0; i < cp.getSeriesCount(); i++) {
		   final XYSeries series = new XYSeries(cp.getName(i));
		   for (int j = 0; j < cp.getXvalues().size(); j++) {
			   series.add(cp.getX(j), cp.getY(i, j));
		   }
		   dataset.addSeries(series);
	   }
	         
      return dataset;
   }
   
   public static void drawChart(ChartParams cp) {
	   FreeChartInterface chart = new FreeChartInterface(cp);
	   chart.pack();
	   RefineryUtilities.centerFrameOnScreen(chart);
	   chart.setVisible(cp.isShow());
   }

   public static void main( String[ ] args ) 
   {
      ChartParams cp = new ChartParams();
      cp.setTitle("Title");
      cp.setXlabel("X Label");
      cp.setYlabel("Y Label");
      
      cp.setYaxis(10, -10);
      
      ArrayList<Numeric> xvals = new ArrayList<Numeric>();
      for (double d = 0.0; d < 7.0; d += 0.1) {
    	  xvals.add(new Num(d));
      }
      
      //Create y values
      ArrayList<Numeric> ycos = new ArrayList<>();
      ArrayList<Numeric> ysin = new ArrayList<>();
            
      for (Numeric n : xvals) {
    	  ycos.add(new Num(Math.sin(n.toDouble())));
    	  ysin.add(new Num(Math.tan(n.toDouble())));
      }
      
      cp.setXvalues(xvals);
      cp.addYvalues("sin", null,  ysin);
      cp.addYvalues("cos", Color.YELLOW, ycos);
      
      
      drawChart(cp);
   }
   
   
}

