package element.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import element.exceptions.ElementRuntimeException;
import element.obj.Obj;
import element.obj.number.Number;
import element.obj.list.List;
import element.obj.list.numberlist.NumberList;

public class ChartParams {
	
	public static final Random RAND = new Random();
	public static final int DEFAULT_HEIGHT = 367;
	public static final int DEFAULT_WIDTH = 560;
	
	public static final int LINE = 0;
	public static final int SCATTER = 1;
	
	public static Color[] DEFAULT_COLORS = {
			Color.BLUE,
			Color.RED, 
			Color.GREEN,
			Color.YELLOW,
			Color.MAGENTA,
			Color.ORANGE,
			Color.PINK,
			Color.CYAN
	};
	
	private int seriesCount;

	private int type;


	private int width;
	private int height;
	private String title;
	private String xlabel;
	private String ylabel;
	private Axis xaxis;
	private Axis yaxis;
	private float stroke;
	private ArrayList<Float> xvalues;
	private ArrayList<ArrayList<Number>> yvalues;
	private String filename;
	private boolean show;
	private boolean legend;
	private boolean horizontal;
	private ArrayList<Color> seriesColors;
	private ArrayList<Float> seriesStrokes;
	private ArrayList<String> seriesNames;

	public ChartParams() {
		this.type = LINE;
		this.seriesCount = 0;
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
		this.title = "";
		this.xlabel = "";
		this.ylabel = "";
		this.xaxis = null;
		this.yaxis = null;
		this.stroke = -1.0f;
		this.xvalues = null;
		this.yvalues = new ArrayList<>();
		this.filename = null;
		this.show = true;
		this.legend = true;
		this.horizontal = false;
		this.seriesStrokes = new ArrayList<>();
		this.seriesColors = new ArrayList<>();
		this.seriesNames = new ArrayList<>();
	}
	
	
	
	
	public static ChartParams parseParams(List params) {
		
		/*
		 Sample Input
		 [
			 ["x" [1 2 3]]
			 ["y"
			 	["sq" colors.blue.rgb [1 4 9]]
			 	["cube" [] [1 8 27]]
			 ]
		 ] MX
		 */
		
		
		ChartParams cp = new ChartParams();
		
		cp.setType((Number)getParam("type", params));
		cp.setWidth((Number)getParam("width", params));
		cp.setHeight((Number)getParam("height", params));
		cp.setStroke((Number)getParam("stroke", params));
		cp.setTitle(getParam("title", params).str());
		cp.setXlabel(getParam("xlabel", params).str());
		cp.setYlabel(getParam("ylabel", params).str());
		cp.setFilename(getParam("filename", params).str());
		cp.setShow(getParam("show", params).bool());
		cp.setLegend(getParam("legend", params).bool());
		cp.setHorizontal(getParam("horizontal", params).bool());
		
		List xaxisParam = (List)getParam("xaxis", params);
		if (xaxisParam != null) {
			Pair<Double, Double> pair = parseAxis(xaxisParam);
			cp.setXaxis(pair.first(), pair.second());
		}
		
		List yaxisParam = (List)getParam("yaxis", params);
		if (yaxisParam != null) {
			Pair<Double, Double> pair = parseAxis(yaxisParam);
			cp.setYaxis(pair.first(), pair.second());
		}
		
		cp.setXvalues(parseData((List)getParam("x", params)));
		
		
		List series = (List)getParam("y", params);
		
		//Parse the series (must be the last step)
		for (int i = 0; i < series.length(); i++) {
			//Every item in y must be a list of params
			if (series.get(i).isa(Obj.LIST)) {
				List list = (List)(series.get(i));
				//Each list must have a name, stroke, color and dataset
				if (list.length() == 4) {
					Obj o_name = list.get(0);
					Obj o_stroke = list.get(1);
					Obj o_colorList = list.get(2);
					Obj o_data = list.get(3);
					if (o_name.isa(Obj.STR) && o_stroke.isa(Obj.NUMBER) && o_colorList.isa(Obj.LIST) && o_data.isa(Obj.LIST)) {
						String name = o_name.str();
						Number stroke = (Number)o_stroke;
						Color color = parseColor((List)o_colorList);
						ArrayList<Number> data = parseData((List)o_data);
						
						cp.addYvalues(name, stroke.toFloat(), color, data);
						
					} else {
						throw new ElementRuntimeException("Plot expected name, RGB color list, and data list. Recieved:\n"
								+ "\t" + o_name.repr() + "\n"
								+ "\t" + o_stroke.repr() + "\n"
								+ "\t" + o_colorList.repr() + "\n"
								+ "\t" + o_data.repr());
					}
				} else {
					throw new ElementRuntimeException("Each series in y must have a name, color, and dataset (3 items). "
							+ "Recieved: " + series.get(i).repr());
				}
			} else {
				throw new ElementRuntimeException("Each series in y must have a name, color, and dataset (3 items in a list). "
						+ "Recieved: " + series.get(i).repr());
			}
		}
		
		
		return cp;
	}
	
	private static Color parseColor(List o_color) {
		if (o_color.length() == 0) {
			return null;
		} else if (o_color.length() == 3
				&& o_color.get(0).isa(Obj.NUMBER)
				&& o_color.get(1).isa(Obj.NUMBER)
				&& o_color.get(2).isa(Obj.NUMBER)) {
			return new Color(
					((Number)(o_color.get(0))).toInt(),
					((Number)(o_color.get(1))).toInt(),
					((Number)(o_color.get(2))).toInt()
					);
		} else {
			throw new ElementRuntimeException("Invalid color: " + o_color.repr());
		}
	}
	
	private static ArrayList<Number> parseData(List data) {
		if (!data.isa(Obj.NUMBERLIST)) {
			throw new ElementRuntimeException("Invalid datapoints in " + data.repr());
		} else {
			return ((NumberList)data).toArrayList();
		}
	}
	
	private static Pair<Double, Double> parseAxis(List list) {
		if (list.length() == 2) {
			if (list.get(0).isa(Obj.NUMBER) && list.get(1).isa(Obj.NUMBER)) {
				Number n_min = (Number) list.get(0);
				Number n_max = (Number) list.get(1);

				
				return new Pair<Double, Double>(n_max.toDouble(), n_min.toDouble());
			} else {
				throw new ElementRuntimeException("Axis max/min must have 2 numbers\n"
						+ "Recieved: " + list.repr());
			}
		} else {
			throw new ElementRuntimeException("Axis max/min must have both max and min values\n"
					+ "Recieved: " + list.repr());
		}
	}
	
	
	private static Obj getParam(String name, List params) {
		for (int i = 0; i < params.length(); i++) {
			if (params.get(i).isa(Obj.LIST)) {
				List list = (List)(params.get(i));
				if (list.length() > 1 && list.get(0).isa(Obj.STR)) {
					String s = list.get(0).str();
					if (s.equals(name)) {
						return list.get(1);
					}
				}
			}
		}
		
		return null;
	}
	
	public int getType() {
		return type;
	}

	private void setType(Number t) {
		//If input is null, leave it unchanged
		this.type = t == null ? this.type : t.toInt();
		
		if (this.type != SCATTER
				&& this.type != LINE) {
			this.type = LINE;
		}
	}
	
	private void setStroke(Number t) {
		//If input is null, leave it unchanged
		this.stroke = t == null ? this.stroke : t.toFloat();
	}
	
	public float getStroke(int i) {
		if (this.stroke == -1.0f) {
			return seriesStrokes.get(i);
		} else {
			return this.stroke;
		}
	}
	

	public boolean useCustomXaxis() {
		return this.xaxis != null;
	}
	public double getXmin() {
		return this.xaxis.min;
	}
	public double getXmax() {
		return this.xaxis.max;
	}
	
	
	public boolean useCustomYaxis() {
		return this.yaxis != null;
	}
	public double getYmin() {
		return this.yaxis.min;
	}
	public double getYmax() {
		return this.yaxis.max;
	}


	public void setXaxis(double max, double min) {
		this.xaxis = new Axis(max, min);
	}

	public void setYaxis(double max, double min) {
		this.yaxis = new Axis(max, min);
	}

	
	
	public boolean isHorizontal() {
		return horizontal;
	}




	public void setHorizontal(Boolean h) {
		//If input is null, leave it unchanged
		this.horizontal = h == null ? this.horizontal : h;
	}
	
	public String getFilename() {
		return filename;
	}




	public void setFilename(String filename) {
		this.filename = filename;
	}




	public boolean isShow() {
		return show;
	}




	public void setShow(Boolean s) {
		//If input is null, leave it unchanged
		this.show = s == null ? this.show : s;
	}
	
	public boolean isLegend() {
		return legend;
	}




	public void setLegend(Boolean l) {
		//If input is null, leave it unchanged
		this.legend = l == null ? this.legend : l;
	}
	
	
	
	public Color getColor(int i) {	
		Color c = seriesColors.get(i);
		if (c == null) {
			return DEFAULT_COLORS[i % DEFAULT_COLORS.length];
		} else {
			return c;
		}
	}
	

	
	public String getName(int i) {
		String out = seriesNames.get(i);
		return out == null ? ("series " + i) : out;
	}
	
	public void addYvalues(String name, float stroke, Color color, ArrayList<Number> vals) {
		if (vals.size() == xvalues.size()) {
			if (name.equals("")) {
				this.legend = false;
				seriesNames.add(randStr());
			} else {
				seriesNames.add(name);
			}
			yvalues.add(vals);
			seriesColors.add(color);
			seriesStrokes.add(stroke);
			seriesCount++;
		} else {
			throw new IllegalArgumentException("yvalues must be same length as x values");
		}
	}
	
	public float getX(int i) {
		return xvalues.get(i);
	}
	
	public float getY(int series, int index) {
		return yvalues.get(series).get(index).toFloat();
	}
	
	public int getSeriesCount() {
		return seriesCount;
	}

	public void setSeriesCount(int seriesCount) {
		this.seriesCount = seriesCount;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(Number w) {
		//If input is null, leave it unchanged
		this.width = w == null ? this.width : w.toInt();

	}

	public int getHeight() {
		return height;
	}

	public void setHeight(Number h) {
		//If input is null, leave it unchanged
		this.height = h == null ? this.height : h.toInt();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getXlabel() {
		return xlabel;
	}

	public void setXlabel(String xlabel) {
		this.xlabel = xlabel;
	}

	public String getYlabel() {
		return ylabel;
	}

	public void setYlabel(String ylabel) {
		this.ylabel = ylabel;
	}

	public ArrayList<Float> getXvalues() {
		return xvalues;
	}

	public void setXvalues(ArrayList<Number> x) {
		this.xvalues = new ArrayList<Float>(x.size());
		for (Number n : x) {
			xvalues.add(n.toFloat());
		}
	}

	public ArrayList<ArrayList<Number>> getYvalues() {
		return yvalues;
	}

	public void setYvalues(ArrayList<ArrayList<Number>> yvalues) {
		this.yvalues = yvalues;
	}
	
	private static String randStr() {
		Random rand = new Random();
		String out = "";
		for (int i = 0; i < 10; i++) {
			out += ((char)rand.nextInt(25)) + 'A';
		}
		return out;
	}
	
	private class Axis {
		double max;
		double min;
		public Axis(double max, double min) {
			if (max > min) {
				this.max = max;
				this.min = min;
			} else if (max < min) {
				this.max = min;
				this.min = max;
			} else {
				this.max = max;
				this.min = max - 1;
			}
		}
	}
	
	
}