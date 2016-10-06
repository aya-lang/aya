package element.util.simple_plot;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A very basic plotting tool. Plots an arbitrary number of doubles to a graph
 * where the x location is the location in the array and the y location
 * is the value of the double
 * 
 * @author Nick
 *
 */
public class SimplePlot {
	//public static final int IMG_WIDTH = 480;
	//public static final int IMG_HEIGHT = 330;
	//public static final int Y_MID = (IMG_HEIGHT-2 + 12) / 2;
	//public static final int x_margin = 30;
	//public static final int y_margin = 15;
	//public static final int PLOT_WIDTH = IMG_WIDTH-40;
	
	private Line[] lines;
	private BufferedImage plotImage;
	private Graphics2D graphics;
	
	private int height;
	private int width;
	
	private int topMargin;
	private int bottomMargin;
	private int leftMargin;
	private int rightMargin;
	private int yMid;
	
	private String title;
	private int titleWidth;
	private String xLabel;
	
	private DataPlot dataPlot;
	
	private Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
	private Font axisFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

	
	public SimplePlot(int width, int height, Line[] lines, String title, String xLabel) {
		this.width = width;
		this.height = height;
		this.lines = lines;
		this.title = title;
		this.xLabel = xLabel;

		plotImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		graphics = plotImage.createGraphics();

		
		topMargin = bottomMargin = (int)((0.03)*(double)width);
		if (topMargin < 12) {
			topMargin = bottomMargin = 12;
		}
		
		if (title != null) {
			Dimension size = getStrDim(title, titleFont);
			topMargin += (int)size.getHeight();
			titleWidth = (int)size.getWidth();
		}
		
		if (xLabel != null) {
			Dimension size = getStrDim(xLabel, axisFont);
			bottomMargin += (int)size.getHeight();
		}
		
		
		leftMargin = rightMargin = (int)((0.05)*(double)height);
		if (leftMargin < 50) {
			leftMargin = 50;
		}
		
		yMid = (height - topMargin - bottomMargin) / 2 + topMargin;
		
		dataPlot = new DataPlot();
	}
	
	private Dimension getStrDim(String str, Font font) {
		// get metrics from the graphics
		FontMetrics metrics = graphics.getFontMetrics(font);
		// get the height of a line of text in this
		// font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font
		// and render context
		int adv = metrics.stringWidth(str);
		// calculate the size of a box to hold the
		// text with some padding.
		return new Dimension(adv+2, hgt+2);
	}
	
	/** Draws the data to the image */
	public void draw() {
		Graphics2D g2 = this.graphics;
		
		//Antialiasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);		
		
		//Make the image BG white
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, width, height);
		
		//Draw the plot
		int plotWidth = width-leftMargin-rightMargin;
		int plotHeight = height-topMargin-bottomMargin;
		dataPlot.createPlot(plotWidth, plotHeight, lines);
		dataPlot.draw();
		g2.drawImage(dataPlot.getPlot(), null, leftMargin, topMargin);
		
		//Set up the axis
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		g2.setColor(Color.BLACK);
		g2.drawLine(leftMargin, topMargin, leftMargin, height-bottomMargin); //Y Axis
		g2.setColor(Color.GRAY);
		g2.drawLine(leftMargin-10, yMid, width-rightMargin, yMid); //X-Axis
				
		//Numeric Axis Labels
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Sans-Serif", Font.PLAIN, 12));
		String y_maxLabel = String.format("%1.3f", dataPlot.getMax());
		String y_midLabel = String.format("%1.3f", dataPlot.getMin() + ( (dataPlot.getMax()-dataPlot.getMin()) / 2 ) );
		String y_minLabel = String.format("%1.3f", dataPlot.getMin());
		g2.drawString(y_maxLabel, leftMargin-45, topMargin + 10);
		g2.drawString(y_minLabel, leftMargin-45, height-bottomMargin);
		g2.drawString(y_midLabel, leftMargin-45, yMid - 5);
		
		//Title
		if (title != null) {
			g2.setFont(titleFont);
			g2.drawString(title, (width/2)-(titleWidth/2), topMargin/2 );
		}
		
		//Labels
		if (xLabel != null) {
			g2.setFont(axisFont);
			g2.drawString(xLabel, leftMargin + plotWidth/2, height - bottomMargin/2);
		}
		
		
		graphics.finalize();
		graphics.dispose();
	}
	
	public DataPlot getDataPlot() {
		return dataPlot;
	}

	/** Displays the image */
	public void show() {
		JFrame frame = new JFrame("Simple Plot");
		frame.setPreferredSize(new Dimension(width+2, height+28));
		frame.add(new ImageView(plotImage));
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	/** Saves the plot to an output file */
	public void save(File outFile) {
		try {
			ImageIO.write(plotImage, "png", outFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
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
	
	public static void main(String[] args) {
		double[] data = new double[500];
		for (int i = 0; i < data.length; i++) {
			//data[i] = Math.exp(i*0.05);
			data[i] = i%50-13;
		}
		
		for (int i = 0; i < 20; i++) {
			data[i+40] = Double.NaN;
		}
		
		double[] data2 = new double[1000];
		double[] data3 = new double[1000];
		for (int i = 0; i < data3.length; i++) {
			data2[i] = 15*Math.cos(i*0.025)-40;
			data3[i] = 15*Math.tan(i*0.01)-33;
		}
		
		Line[] lines = new Line[3];
		lines[0] = new Line(data, Color.RED, 1);
		lines[1] = new Line(data2, Color.BLUE, 3);
		lines[2] = new Line(data3, Color.MAGENTA, 1.5f);
		
		String title = "My Functions";
		String xLabel = "X Label";
		
		Random rand = new Random();
		for (int i = 0; i < 1; i++) {
			SimplePlot sp = new SimplePlot(rand.nextInt(500)+200, rand.nextInt(500)+200, lines, title, xLabel);
			sp.getDataPlot().setZeroCenter(rand.nextBoolean());
			sp.draw();
			sp.show();
		}
		
	}
}
