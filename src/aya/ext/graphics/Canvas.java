package aya.ext.graphics;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import aya.exceptions.runtime.ValueError;

/**
 * A very basic plotting tool. Plots an arbitrary number of doubles to a graph
 * where the x location is the location in the array and the y location
 * is the value of the double
 * 
 * @author Nick
 *
 */
public class Canvas {
	private BufferedImage _plotImage; // = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
	private Graphics2D _g2d;
	private int _width;
	private int _height;
	private double _scale;
	private String _name;
	private JFrame _frame;
	private boolean _show_on_refresh; // If true, call show() when refresh() is called, if false no-op
	private AffineTransform _at;
	private CanvasCursorListener _cursor_listener;
	private CanvasKeyListener _key_listener;
	
	
	public Canvas(String name, int width, int height, double scale) {

		_name = name;
		_width = width;
		_height = height;
		_scale = scale;
		_plotImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		_at = new AffineTransform();
		_at.scale(scale, scale);
		
		_g2d = _plotImage.createGraphics();
		_g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		_g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		// Black as default color
		_g2d.setColor(Color.BLACK);
		
		// White as default background
		_g2d.setBackground(new Color(255, 255, 255));
		_g2d.clearRect(0, 0, _width, _height);
	}
	
	public int getWidth() {
		return _width;
	}
	
	public int getHeight() {
		return _height;
	}

	public CanvasCursorListener getCursorListener() {
		if (_cursor_listener != null) {
			return _cursor_listener;
		} else {
			throw new ValueError("Cannot get mouse information: canvas has not yet been opened");
		}
	}

	public CanvasKeyListener getKeyListener() {
		if (_key_listener != null) {
			return _key_listener;
		} else {
			throw new ValueError("Cannot get keyboard information: canvas has not yet been opened");
		}
	}

	public void setShowOnRefresh(boolean b) {
		_show_on_refresh = b;
	}
	
	public void refresh() {
		if (_show_on_refresh) {
			show();
		}
	}

	/** Displays the image */
	public void show(Component comp) {
		// Canvas was closed
		if (!isOpen()) return;
		
		if (_frame == null) {
			_frame = new JFrame(_name);
			Container contentPane = _frame.getContentPane();
			contentPane.setPreferredSize(new Dimension((int)(_width * _scale), (int)(_height * _scale)));

			_cursor_listener = new CanvasCursorListener();
			contentPane.addMouseListener(_cursor_listener);
			contentPane.addMouseMotionListener(_cursor_listener);

			_key_listener = new CanvasKeyListener();
			_frame.addKeyListener(_key_listener);

			_frame.add(new ImageView(_plotImage));
			_frame.setResizable(false);
			_frame.pack();
			_frame.setLocationRelativeTo(comp);
			_frame.setVisible(true);
			
			_frame.addWindowListener(new WindowAdapter() {
				@Override
		        public void windowClosing(WindowEvent event) {
		            close();
		        }
			});
		} else {
			_frame.repaint();
		}
	}
	
	public void hide() {
		_frame.setVisible(false);
	}
	
	public void close() {
		if (_frame != null) _frame.dispose();
		_plotImage = null;
		_g2d = null;
		_frame = null;
	}
	
	public boolean isOpen() {
		return _plotImage != null;
	}
	
	/** Shorthand for show(null) */
	public void show() {
		show(null);
	}
	
	/** Saves the plot to an output file */
	public void save(File outFile) throws IOException{
		ImageIO.write(_plotImage, "png", outFile);
	}

	
	@SuppressWarnings("serial")
	public class ImageView extends JPanel {	
		private BufferedImage img;
		public ImageView(BufferedImage imgIn) {
			img = imgIn;
			setSize(_width, _height);
			setPreferredSize(new Dimension(_width, _height));
		}
		
		@Override protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D)g).setTransform(_at);
			g.drawImage(img, 0, 0, this);
		}
	}	
	
	public Graphics2D getG2D() {
		return _g2d;
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Hello Canvas");
				
		Canvas c = new Canvas("Canvas", 200, 200, 2);
		c.show();
		
		c.getG2D().setColor(Color.BLACK);
		
		//Enter data using BufferReader 
        BufferedReader reader =  
                   new BufferedReader(new InputStreamReader(System.in)); 
		
		for (int i = 0; i < 10; i++)
		{
			c.getG2D().drawLine(i*10, i*10, (i+1)*10, (i+1)*10);
	        String s = reader.readLine();
	        System.out.println("Got: '" + s + "'");
	        c.show();
		}
		
		reader.close();
	}
}
