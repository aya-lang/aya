package element.util.simple_plot;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

public class SimplePlotInterface {
	
	int height;
	int width;
	ArrayList<Line> lines;
	String title;
	String xLabel;
	boolean centerZero;
	boolean show;
	File file;
	double yMinConstraint;
	double yMaxConstraint;
	
	
	Color[] colorList = {
			Color.RED,
			Color.BLUE,
			Color.GREEN,
			Color.YELLOW,
			Color.MAGENTA,
			Color.ORANGE,
			Color.PINK,
	};
	int colorIndex = 0;
	
	private float[] strokeList = {
			1.5f
	};
	int strokeIndex = 0;
	
	public SimplePlotInterface() {
		height = 400;
		width = 500;
		lines = new ArrayList<Line>();
		title = null;
		xLabel = null;
		centerZero = false;
		show = true;
		file = null;
		yMaxConstraint = Double.MAX_VALUE;
		yMinConstraint = -1*Double.MAX_VALUE;
	}
	
	private Color nextColor() {
		if (colorIndex >= colorList.length) {
			colorIndex = 0;
		}
		return colorList[colorIndex++];
	}
	
	private float nextStroke() {
		if (strokeIndex >= strokeList.length) {
			strokeIndex = 0;
		}
		return strokeList[strokeIndex++];
	}
	
	public void setYMinConstraint(double d) {
		this.yMinConstraint = d;
	}
	
	public void setYMaxConstraint(double ymax) {
		this.yMaxConstraint = ymax;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setxLabel(String xLabel) {
		this.xLabel = xLabel;
	}
	
	public void addLine(Line line) {
		this.lines.add(line);
	}
	
	
	/**
	 * Add a line to the plot
	 * 
	 * @param data
	 * @param color if null, use next color in list
	 * @param stroke if -1, use default stroke
	 */
	public void addLine(double[] data, Color color, float stroke) {
		if (color == null) {
			color = nextColor();
		}
		
		if (stroke == -1) {
			stroke = 1.5f;
		}
		
		addLine(new Line(data, color, stroke));
	}
	
	/**
	 * Add a line to the plot using default color and stroke
	 * @param data
	 */
	public void addLine(double[] data) {
		addLine(new Line(data, nextColor(), nextStroke()));
	}
	
	
	public void go() {
		SimplePlot sp = new SimplePlot(width, height, linesArr(), title, xLabel);
		
		//DataPlot Params
		sp.getDataPlot().setZeroCenter(centerZero);
		sp.getDataPlot().setyMaxConstraint(yMaxConstraint);
		sp.getDataPlot().setyMinConstraint(yMinConstraint);
		
		sp.draw();
		
		if(show) {
			sp.show();
		}
		
		if (file != null) {
			sp.save(file);
		}
	}

	
	private Line[] linesArr() {
		return lines.toArray(new Line[lines.size()]);
	}
	
	
	
	

	public static void main(String[] args) {
		double[] data = new double[500];
		for (int i = 0; i < data.length; i++) {
			//data[i] = Math.exp(i*0.05);
			data[i] = i%50-13;
		}
		
		double[] data2 = new double[1000];
		double[] data3 = new double[1000];
		for (int i = 0; i < data3.length; i++) {
			data2[i] = 15*Math.cos(i*0.025)-40;
			data3[i] = 15*Math.sin(i*0.01)-33;
		}
		
		SimplePlotInterface spi = new SimplePlotInterface();
		
		spi.addLine(data);
		spi.addLine(data2, Color.BLACK, 4);
		spi.addLine(data3);
		spi.setTitle("Some Functions I Made");
		spi.setxLabel("x-value");
		
		
		
//		Line[] lines = new Line[3];
//		lines[0] = new Line(data, Color.RED, 1);
//		lines[1] = new Line(data2, Color.BLUE, 3);
//		lines[2] = new Line(data3, Color.MAGENTA, 1.5f);
		
		//String title = "My Functions";
		//String xLabel = "X Label";
		
//		Random rand = new Random();
//		for (int i = 0; i < 3; i++) {
//			SimplePlot sp = new SimplePlot(rand.nextInt(500)+200, rand.nextInt(500)+200, lines, title, xLabel);
//			sp.getDataPlot().setZeroCenter(rand.nextBoolean());
//			sp.draw();
//			sp.show();
//		}
		
	}
	
	public void setColorList(Color[] colors) {
		this.colorList = colors;
	}

	public void setColor(Color color) {
		Color[] cs = new Color[1];
		cs[0] = color;
		this.colorList = cs;		
	}
	
	public void setStrokeList(float[] fs) {
		this.strokeList = fs;
	}

	public void setStroke(float f) {
		float[] fs = new float[1];
		fs[0] = f;
		this.strokeList = fs;		
	}

	public void setCenterZero(boolean bool) {
		this.centerZero = bool;
	}
	
	public void setShow(boolean b) {
		this.show = b;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
}
