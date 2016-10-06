package element.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
	public static final int IMG_WIDTH = 480;
	public static final int IMG_HEIGHT = 330;
	public static final int X_MARGIN = 30;
	public static final int Y_MARGIN = 15;
	public static final int PLOT_WIDTH = IMG_WIDTH-40;
	public static final int Y_MID = (IMG_HEIGHT-2 + 12) / 2;
	public static final Color LINE_COLOR = new Color(105,197,255);
	public static final BasicStroke LINE_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	private double[] data;
	private BufferedImage plotImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
	
	public SimplePlot(double[] data) {
		this.data = data;
		render();
	}
	
	/** Draws the data to the image */
	public void render() {
		Graphics2D g2 = plotImage.createGraphics();
		
		//Antialiasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		//Initial Calculations
		double[] vals = minmax(data);
		double min = vals[0];
		double max = vals[1];
		double rangeSize = max-min;
		double y_step = 300/rangeSize;
		double x_step = (double) (PLOT_WIDTH)/(double) (data.length);
		int zeroHeight = IMG_HEIGHT/2;
		String y_maxLabel = String.format("%1.3f", max);
		String y_minLabel = String.format("%1.3f", min);
		String y_midLabel = String.format("%1.3f", min + ( (max-min) / 2 ) );
		
		
		//Make the image BG white
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);
		
		//Set up the axis
		g2.setColor(Color.BLACK);
		g2.drawLine(X_MARGIN, Y_MARGIN, X_MARGIN, Y_MID - 22);
		g2.drawLine(X_MARGIN, Y_MID - 7, X_MARGIN, IMG_HEIGHT-Y_MARGIN );	//Y-Axis Upper Line
		g2.setColor(Color.GRAY);
		g2.drawLine(X_MARGIN-10, zeroHeight, IMG_WIDTH-X_MARGIN, zeroHeight); //X-Axis
		
		//Axis Labels
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Sans-Serif", Font.PLAIN, 12));
		g2.drawString(y_maxLabel, 10, 12);
		g2.drawString(y_midLabel, 10, Y_MID - 9 );
		g2.drawString(y_minLabel, 10, IMG_HEIGHT-2);
		
		//Draw the data
		g2.setColor(LINE_COLOR);
		g2.setStroke(LINE_STROKE);
		GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD,data.length);
		polyline.moveTo(X_MARGIN+(0*x_step), -1*(((data[0]-min)*y_step)+Y_MARGIN)+IMG_HEIGHT);
		for (int i = 1; i < data.length; i++) {
			polyline.lineTo(X_MARGIN+(i*x_step), -1*(((data[i]-min)*y_step)+Y_MARGIN)+IMG_HEIGHT);
		}
		g2.draw(polyline);
		
	}
	
	/** Return [min, max] values of an array */
	private double[] minmax(double[] data) {
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		
		for(int i = 0; i < data.length; i++) {
			if(data[i] > max) {
				max = data[i];
			}
			
			if (data[i] < min) {
				min = data[i];
			}
		}
		
		double[] out = new double[2];
		out[0] = min;
		out[1] = max;
		return out;
	}

	/** Displays the image */
	public void show(Component comp) {
		JFrame frame = new JFrame("Simple Plot");
		frame.setPreferredSize(new Dimension(IMG_WIDTH+2, IMG_HEIGHT+28));
		frame.add(new ImageView(plotImage));
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(comp);
		frame.setVisible(true);
	}
	
	/** Shorthand for show(null) */
	public void show() {
		show(null);
	}
	
	/** Saves the plot to an output file */
	public void save(File outFile) throws IOException{
		ImageIO.write(plotImage, "png", outFile);
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
}
