package element.util.simple_plot;
import java.awt.Color;

public class Line {
	private double[] data;
	private Color color;


	private float stroke;
	
	public Line(double[] data, Color color, float stroke) {
		this.data = data;
		this.color = color;
		this.stroke = stroke;
	}
	
	public double[] getData() {
		return data;
	}
	public void setData(double[] data) {
		this.data = data;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public float getStroke() {
		return stroke;
	}

	public void setStroke(float stroke) {
		this.stroke = stroke;
	}
	public int size() {
		return data.length;
	}
	
	/** Return [min, max] values of an array */
	public double[] minmax() {
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		
		for(int i = 0; i < data.length; i++) {
			max = data[i] > max ? data[i] : max;
			min = data[i] < min ? data[i] : min;
		}
		
		double[] out = new double[2];
		out[0] = min;
		out[1] = max;
		return out;
	}
}
