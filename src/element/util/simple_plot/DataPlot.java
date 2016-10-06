package element.util.simple_plot;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DataPlot {
	private int width;
	private int height;
	private BufferedImage plot;
	private Line[] lines;
	private Graphics2D graphics;
	private double max;
	private double min;
	private boolean plotCreated = false;
	

	
	//Properties
	private boolean zeroCenter = false;
	private double yMaxConstraint = 100;//Double.MAX_VALUE;
	private double yMinConstraint = -100;//-1*Double.MAX_VALUE;
	
		
	void createPlot(int width, int height, Line[] lines) {
		if(plotCreated) {
			throw new RuntimeException("Plot already created");
		}
		
		this.width = width;
		this.height = height;
		this.lines = lines;
		this.plot = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.graphics = this.plot.createGraphics();	
		
		plotCreated = true;
	}
	
	public void setZeroCenter(boolean b) {
		this.zeroCenter = b;
	}
	
	public void setyMaxConstraint(double yMaxConstraint) {
		this.yMaxConstraint = yMaxConstraint;
	}

	public void setyMinConstraint(double yMinConstraint) {
		this.yMinConstraint = yMinConstraint;
	}

	public double getMax() {
		if(!plotCreated) {
			throw new RuntimeException("Plot has not been created");
		}
		
		return max;
	}
	
	public double getMin() {
		if(!plotCreated) {
			throw new RuntimeException("Plot has not been created");
		}
		
		return min;
	}
	
	public BufferedImage getPlot() {
		if(!plotCreated) {
			throw new RuntimeException("Plot has not been created");
		}
		
		return plot;
	}
	
	public void draw() {
		if(!plotCreated) {
			throw new RuntimeException("Plot has not been created");
		}
		
		Graphics2D g2 = this.graphics;
		
		//Antialiasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		
		
		//Minimum and Maximum Values
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
		int maxSetLength = 0;
		for (int i = 0; i < lines.length; i++) {
			double[] vals = lines[i].minmax();
			min = vals[0] < min ? vals[0] : min;
			max = vals[1] > max ? vals[1] : max;
			maxSetLength = lines[i].size() > maxSetLength ? lines[i].size() : maxSetLength;
		}
		
		//Max/Min Constraint
		if(min < yMinConstraint) {
			min = yMinConstraint;
		}
		if (max > yMaxConstraint){
			max = yMaxConstraint;
		}
		
		//Zero Center
		if(zeroCenter) {
			if (Math.abs(max) > Math.abs(min)) {
				min = -1*max;
			} else {
				max = -1*min;
			}
		}
		
		//Create slight margins on top and bottom
		double margin_max = max*1.05;
		double margin_min = min*1.05;
		
		//Range Calculations
		double vRangeSize = margin_max-margin_min;
		double y_step = (height)/vRangeSize;
		double x_step = (double)(width) / (double)(maxSetLength);
		
		
		//Make the image BG grey
		g2.setColor(new Color(0.96f, 0.96f, 0.96f));
		g2.fillRect(0, 0, width, height);
		
		for (int i = 0; i < lines.length; i++) {
			double[] data = lines[i].getData();
			
			g2.setColor(lines[i].getColor());
			g2.setStroke(new BasicStroke(lines[i].getStroke(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
			
			GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, data.length);
			
			polyline.moveTo(0*x_step, -1*(((data[0]-margin_min)*y_step))+height);
			
			for (int j = 1; j < data.length; j++) {
				polyline.lineTo(j*x_step, -1*((data[j]-margin_min)*y_step)+height);
			}
			
			g2.draw(polyline);
		}
	}
	
	

	
	
	
	/***************** TEMP METHODS ***************/
	
	
	@SuppressWarnings("serial")
	public class ImageView extends JPanel {	
		private BufferedImage img;
		public ImageView(BufferedImage imgIn) {
			img = imgIn;
		}
		
		@Override protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, 0, 0, this);
		}
	}	
	
	/** Displays the image */
	public void show() {
		JFrame frame = new JFrame("Simple Plot");
		frame.setPreferredSize(new Dimension(width+2, height+28));
		frame.add(new ImageView(plot));
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
