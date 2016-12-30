package aya.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.Str;
import aya.obj.number.Num;
import aya.obj.number.Number;

public class ChartParams {
	
	public static final Random RAND = new Random();
	public static final int DEFAULT_HEIGHT = 367;
	public static final int DEFAULT_WIDTH = 560;
	public static final String NONE = "";
	public static final Str STRNONE = new Str(NONE);
	
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
		this.filename = NONE;
		this.show = true;
		this.legend = true;
		this.horizontal = false;
		this.seriesStrokes = new ArrayList<>();
		this.seriesColors = new ArrayList<>();
		this.seriesNames = new ArrayList<>();
	}
	
	
	
	
	public static ChartParams parseParams(Dict params) {
		
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
		
		cp.setType((Number)getNumber("type", params, null));
		cp.setWidth((Number)getNumber("width", params, null));
		cp.setHeight((Number)getNumber("height", params, null));
		cp.setStroke((Number)getNumber("stroke", params, null));
		cp.setTitle(getParam("title", params, STRNONE).str());
		cp.setXlabel(getParam("xlabel", params, STRNONE).str());
		cp.setYlabel(getParam("ylabel", params, STRNONE).str());
		cp.setFilename(getParam("filename", params, STRNONE).str());
		cp.setShow(getParam("show", params, Num.ONE).bool());
		cp.setLegend(getParam("legend", params, Num.ZERO).bool());
		cp.setHorizontal(getParam("horizontal", params, Num.ZERO).bool());
		
		List xaxisParam = getList("xaxis", params, null);
		if (xaxisParam != null) {
			Pair<Double, Double> pair = parseAxis(xaxisParam);
			cp.setXaxis(pair.first(), pair.second());
		}
		
		List yaxisParam = getList("yaxis", params, null);
		if (yaxisParam != null) {
			Pair<Double, Double> pair = parseAxis(yaxisParam);
			cp.setYaxis(pair.first(), pair.second());
		}
		
		List xValues = getList("x", params, null);
		if (xValues == null) {
			throw new AyaRuntimeException("MX: input dict does not contain key 'x'");
		} else {
			cp.setXvalues(parseData(xValues));
		}
		
		List series = getList("y", params, null);
		
		if (series == null) {
			throw new AyaRuntimeException("MX: input dict does not contain key 'y'");
		}
		
		//Parse the series (must be the last step)
		for (int i = 0; i < series.length(); i++) {
			//Every item in y must be a list of params
			if (series.get(i).isa(Obj.DICT)) {
				Dict dict = (Dict)(series.get(i));
				//Each list must have a name, stroke, color and dataset
				Obj o_name = getParam("name", dict, Str.EMPTY);
				String name = o_name.str();
				
				Obj o_stroke = getParam("stroke", dict, Num.ONE);
				Number stroke = Num.ONE;
				if (o_stroke.isa(Obj.NUMBER)) {
					stroke = (Number)o_stroke;
				} else {
					throw new AyaRuntimeException("Series key 'stroke' must be a number");
				}
				
				Obj o_color = getParam("color", dict, Num.ZERO);  // Use '0' if not given
				Color color = Color.BLUE;
				if (o_color.isa(Obj.LIST)) {
					color = parseColor((List)o_color);
				} else {
					throw new AyaRuntimeException("Series key 'color' must be a list of numbers [r g b]");
				}
				
				Obj o_data = getParam("data", dict, Num.ZERO);  // Use '0' if not given
				ArrayList<Number> data = null;
				if (o_data.isa(Obj.LIST)) {
					data = ((List)o_data).toNumberList().toArrayList();
				} else {
					throw new AyaRuntimeException("Series key 'data' must be a list of numbers");
				}
				
				
				cp.addYvalues(name, stroke.toFloat(), color, data);
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
			throw new AyaRuntimeException("Invalid color: " + o_color.repr());
		}
	}
	
	private static ArrayList<Number> parseData(List data) {
		if (!data.isa(Obj.LIST)) {
			throw new AyaRuntimeException("Invalid datapoints in " + data.repr());
		} else {
			return data.toNumberList().toArrayList();
		}
	}
	
	private static Pair<Double, Double> parseAxis(List list) {
		if (list.length() == 2) {
			if (list.get(0).isa(Obj.NUMBER) && list.get(1).isa(Obj.NUMBER)) {
				Number n_min = (Number) list.get(0);
				Number n_max = (Number) list.get(1);

				
				return new Pair<Double, Double>(n_max.toDouble(), n_min.toDouble());
			} else {
				throw new AyaRuntimeException("Axis max/min must have 2 numbers\n"
						+ "Recieved: " + list.repr());
			}
		} else {
			throw new AyaRuntimeException("Axis max/min must have both max and min values\n"
					+ "Recieved: " + list.repr());
		}
	}
	
	private static Number getNumber(String name, Dict params, Obj dflt) {
		Obj o = getParam(name, params, dflt);
		if (o instanceof Number) {
			return (Number)o;
		} else if (o != null) {
			throw new AyaRuntimeException("MX: Param name '" + name + "' should be a number."
					+ " Recieved " + o.repr());
		} else {
			return null;
		}
	}
	
	private static List getList(String name, Dict params, Obj dflt) {
		Obj o = getParam(name, params, dflt);
		if (o instanceof List) {
			return (List)o;
		} else if (o != null) {
			throw new AyaRuntimeException("MX: Param name '" + name + "' should be a list."
					+ " Recieved " + o.repr());
		} else {
			return null;
		}
	}
	
	private static Obj getParam(String name, Dict params, Obj dflt) {
		if (params.containsKey(name)) {
			return params.get(name);
		} else {
			return dflt;
		}
		
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
		this.filename = filename.equals(NONE) ? null : filename;
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
		this.title = title.equals(NONE) ? null : title;
	}

	public String getXlabel() {
		return xlabel;
	}

	public void setXlabel(String xlabel) {
		this.xlabel = xlabel.equals(NONE) ? null : xlabel;
	}

	public String getYlabel() {
		return ylabel;
	}

	public void setYlabel(String ylabel) {
		this.ylabel = ylabel.equals(NONE) ? null : ylabel;
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