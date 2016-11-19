package element.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import element.ElemTypes;
import element.entities.number.Numeric;
import element.exceptions.ElementRuntimeException;

public class ChartParams {
	
	public static final Random RAND = new Random();
	public static final int DEFAULT_HEIGHT = 367;
	public static final int DEFAULT_WIDTH = 560;
	
	public static final int LINE = 0;
	public static final int SCATTER = 1;
	
	private int seriesCount;

	private int type;


	private int width;
	private int height;
	private String title;
	private String xlabel;
	private String ylabel;
	private Axis xaxis;
	private Axis yaxis;
	private ArrayList<Float> xvalues;
	private ArrayList<ArrayList<Numeric>> yvalues;
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
	
	
	
	
	@SuppressWarnings("unchecked")
	public static ChartParams parseParams(ArrayList<Object> params) {
		
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
		
		cp.setType((Numeric)getParam("type", params));
		cp.setWidth((Numeric)getParam("width", params));
		cp.setHeight((Numeric)getParam("height", params));
		cp.setTitle((String)getParam("title", params));
		cp.setXlabel((String)getParam("xlabel", params));
		cp.setYlabel((String)getParam("ylabel", params));
		cp.setFilename((String)getParam("filename", params));
		cp.setShow((Boolean)getParam("show", params));
		cp.setLegend((Boolean)getParam("legend", params));
		cp.setHorizontal((Boolean)getParam("horizontal", params));
		
		ArrayList<Object> xaxisParam = (ArrayList<Object>)getParam("xaxis", params);
		if (xaxisParam != null) {
			Pair<Double, Double> pair = parseAxis(xaxisParam);
			cp.setXaxis(pair.first(), pair.second());
		}
		
		ArrayList<Object> yaxisParam = (ArrayList<Object>)getParam("yaxis", params);
		if (yaxisParam != null) {
			Pair<Double, Double> pair = parseAxis(yaxisParam);
			cp.setYaxis(pair.first(), pair.second());
		}
		
		cp.setXvalues((ArrayList<Numeric>)getParam("x", params));
		
		
		ArrayList<Object> series = (ArrayList<Object>)getParam("y", params);
		
		//Parse the series
		for (Object o : series) {
			//Every item in y must be a list of params
			if (o instanceof ArrayList) {
				ArrayList<Object> list = (ArrayList<Object>)o;
				//Each list must have a name, stroke, color and dataset
				if (list.size() == 4) {
					Object o_name = list.get(0);
					Object o_stroke = list.get(1);
					Object o_colorList = list.get(2);
					Object o_data = list.get(3);
					if (o_name instanceof String && o_stroke instanceof Numeric && o_colorList instanceof ArrayList && o_data instanceof ArrayList) {
						String name = (String)o_name;
						Numeric stroke = (Numeric)o_stroke;
						Color color = parseColor((ArrayList<Object>)o_colorList);
						ArrayList<Numeric> data = parseData((ArrayList<Object>)o_data);
						
						cp.addYvalues(name, stroke.toFloat(), color, data);
						
					} else {
						throw new ElementRuntimeException("Plot expected name, RGB color list, and data list. Recieved:\n"
								+ "\t" + str(o_name) + "\n"
								+ "\t" + str(o_stroke) + "\n"
								+ "\t" + str(o_colorList) + "\n"
								+ "\t" + str(o_data));
					}
				} else {
					throw new ElementRuntimeException("Each series in y must have a name, color, and dataset (3 items). "
							+ "Recieved: " + str(o));
				}
			} else {
				throw new ElementRuntimeException("Each series in y must have a name, color, and dataset (3 items in a list). "
						+ "Recieved: " + str(o));
			}
		}
		
		
		return cp;
	}
	
	private static Color parseColor(ArrayList<Object> o_color) {
		if (o_color.size() == 3
				&& o_color.get(0) instanceof Numeric
				&& o_color.get(1) instanceof Numeric
				&& o_color.get(2) instanceof Numeric) {
			return new Color(
					((Numeric)(o_color.get(0))).toInt(),
					((Numeric)(o_color.get(1))).toInt(),
					((Numeric)(o_color.get(2))).toInt()
					);
		} else {
			throw new ElementRuntimeException("Invalid color: " + str(o_color));
		}
	}
	
	private static ArrayList<Numeric> parseData(ArrayList<Object> data) {
		ArrayList<Numeric> nums = new ArrayList<Numeric>(data.size());
		for (Object o : data) {
			if (o instanceof Numeric) {
				nums.add((Numeric)o);
			} else {
				throw new ElementRuntimeException("Invalid datapoint " + str(o) + " in " + str(data));
			}
		}
		return nums;
	}
	
	private static Pair<Double, Double> parseAxis(ArrayList<Object> list) {
		if (list.size() == 2) {
			if (list.get(0) instanceof Numeric && list.get(1) instanceof Numeric) {
				Numeric n_min = (Numeric) list.get(0);
				Numeric n_max = (Numeric) list.get(1);

				
				return new Pair<Double, Double>(n_max.toDouble(), n_min.toDouble());
			} else {
				throw new ElementRuntimeException("Axis max/min must have 2 numbers\n"
						+ "Recieved: " + str(list));
			}
		} else {
			throw new ElementRuntimeException("Axis max/min must have both max and min values\n"
					+ "Recieved: " + str(list));
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private static Object getParam(String name, ArrayList<Object> params) {
		for (Object o : params) {
			if (o instanceof ArrayList) {
				ArrayList<Object> list = (ArrayList<Object>)o;
				if (list.size() > 1 && list.get(0) instanceof String) {
					String s = (String) list.get(0);
					if (s.equals(name)) {
						return list.get(1);
					}
				}
			}
		}
		
		return null;
	}
	
	private static String str(Object o) {
		return ElemTypes.castString(o);
	}
	

	
	public int getType() {
		return type;
	}

	private void setType(Numeric t) {
		//If input is null, leave it unchanged
		this.type = t == null ? this.type : t.toInt();
		
		if (this.type != SCATTER
				&& this.type != LINE) {
			this.type = LINE;
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
		return seriesColors.get(i);
	}
	
	public float getStroke(int i) {
		return seriesStrokes.get(i);
	}
	
	public String getName(int i) {
		String out = seriesNames.get(i);
		return out == null ? ("series " + i) : out;
	}
	
	public void addYvalues(String name, float stroke, Color color, ArrayList<Numeric> vals) {
		if (vals.size() == xvalues.size()) {
			yvalues.add(vals);
			seriesNames.add(name);
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

	public void setWidth(Numeric w) {
		//If input is null, leave it unchanged
		this.width = w == null ? this.width : w.toInt();

	}

	public int getHeight() {
		return height;
	}

	public void setHeight(Numeric h) {
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

	public void setXvalues(ArrayList<Numeric> x) {
		this.xvalues = new ArrayList<Float>(x.size());
		for (Numeric n : x) {
			xvalues.add(n.toFloat());
		}
	}

	public ArrayList<ArrayList<Numeric>> getYvalues() {
		return yvalues;
	}

	public void setYvalues(ArrayList<ArrayList<Numeric>> yvalues) {
		this.yvalues = yvalues;
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