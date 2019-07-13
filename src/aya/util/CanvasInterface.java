package aya.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;

import aya.AyaPrefs;
import aya.exceptions.AyaRuntimeException;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.variable.Variable;

public class CanvasInterface {
	private CanvasTable _table;
	
	// Keys
	private final long AUTOFLUSH = Variable.encodeString("autoflush");
	private final long EXTENT = Variable.encodeString("extent");
	private final long ANGLE = Variable.encodeString("angle");
	private final long HEIGHT = Variable.encodeString("height");
	private final long WIDTH = Variable.encodeString("width");
	private final long NAME = Variable.encodeString("name");
	private final long FILE = Variable.encodeString("file");
	private final long FILL = Variable.encodeString("fill");
	private final long SHOW = Variable.encodeString("show");
	private final long JOIN = Variable.encodeString("join");
	private final long CAP = Variable.encodeString("cap");
	private final long DH = Variable.encodeString("dh");
	private final long DV = Variable.encodeString("dv");
	private final long X1 = Variable.encodeString("xa");
	private final long Y1 = Variable.encodeString("ya");
	private final long X2 = Variable.encodeString("xb");
	private final long Y2 = Variable.encodeString("yb");
	private final long W = Variable.encodeString("w");
	private final long H = Variable.encodeString("h");
	private final long X = Variable.encodeString("x");
	private final long Y = Variable.encodeString("y");
	private final long R = Variable.encodeString("r");
	private final long G = Variable.encodeString("g");
	private final long B = Variable.encodeString("b");
	
	public CanvasInterface() {
		_table = new CanvasTable();
	}
	
	public int doCommand(int canvas_id, Symbol command, Dict d) throws AyaRuntimeException {
		DictReader params = new DictReader(d);
		params.setErrorName("MG (command: " + command.str() + ")");
		
		String cmd = command.name();
		
		if (cmd.equals("new")) {
			return cmdNew(params);
		} else if (cmd.equals("close")) {
			_table.close(canvas_id);
			return 1;
		}
		
		Canvas cvs = _table.getCanvas(canvas_id);
		
		// Initial checks
		if (cvs == null) {
			throw new AyaRuntimeException("Canvas with id '" + canvas_id + "' does not exist");
		} else if (!cvs.isOpen()) {
			throw new AyaRuntimeException("Canvas with id '" + canvas_id + "' has been closed");
		}
		
		switch (cmd) {
		case "line": return cmdLine(params, cvs);
		case "rect": return cmdRect(params, cvs);
		case "roundrect": return cmdRoundRect(params, cvs);
		case "oval": return cmdOval(params, cvs);
		case "ellipse": return cmdEllipse(params, cvs);
		case "arc": return cmdArc(params, cvs);
		case "set_color": return cmdSetColor(params, cvs);
		case "set_stroke": return cmdSetStroke(params, cvs);
		case "show": return cmdShow(params, cvs);
		case "save": return cmdSave(params, cvs);
		default:
			throw new AyaRuntimeException("Canvas: Unknown command '" + command.name() + "'.");
		}

	}
	
	private int cmdNew(DictReader params) {
		int id = _table.newCanvas(params.getString(NAME, "Canvas"), params.getIntEx(WIDTH), params.getIntEx(HEIGHT));
		Canvas c = _table.getCanvas(id);
		c.setShowOnRefresh(params.getInt(AUTOFLUSH, 0) != 0);
		if (params.getInt(SHOW, 1) == 1) {
			c.show();
		}
		return id;
	}
	
	private int cmdLine(DictReader params, Canvas cvs) {
		cvs.getG2D().drawLine(params.getIntEx(X1),
							  params.getIntEx(Y1),
							  params.getIntEx(X2),
							  params.getIntEx(Y2));
		cvs.refresh();
		return 1;
	}
	
	private int cmdRect(DictReader params, Canvas cvs) {
		if (params.getInt(FILL, 0) == 1) {
			cvs.getG2D().fillRect(params.getIntEx(X),
					  params.getIntEx(Y),
					  params.getIntEx(W),
					  params.getIntEx(H));	
		} else {
			cvs.getG2D().drawRect(params.getIntEx(X),
								  params.getIntEx(Y),
								  params.getIntEx(W),
								  params.getIntEx(H));
		}
		
		cvs.refresh();
		return 1;
	}
	
	private int cmdRoundRect(DictReader params, Canvas cvs) {
		if (params.getInt(FILL, 0) == 1) {
			cvs.getG2D().fillRoundRect(params.getIntEx(X),
									   params.getIntEx(Y),
									   params.getIntEx(W),
									   params.getIntEx(H),
									   params.getInt(DH, 0),
									   params.getInt(DV, 0));	
		} else {
			cvs.getG2D().drawRoundRect(params.getIntEx(X),
									   params.getIntEx(Y),
									   params.getIntEx(W),
									   params.getIntEx(H),
									   params.getInt(DH, 0),
									   params.getInt(DV, 0));		  
			}
		
		cvs.refresh();
		return 1;
	}
	
	private int cmdArc(DictReader params, Canvas cvs) {
		if (params.getInt(FILL, 0) == 1) {
			cvs.getG2D().fillArc(params.getIntEx(X),
								 params.getIntEx(Y),
								 params.getIntEx(W),
								 params.getIntEx(H),
								 params.getInt(ANGLE, 0),
								 params.getInt(EXTENT, 0));	
		} else {
			cvs.getG2D().drawArc(params.getIntEx(X),
								 params.getIntEx(Y),
								 params.getIntEx(W),
								 params.getIntEx(H),
								 params.getInt(ANGLE, 0),
								 params.getInt(EXTENT, 0));		  
			}
		
		cvs.refresh();
		return 1;
	}
	
	private int cmdOval(DictReader params, Canvas cvs) {
		int x = params.getIntEx(X);
		int y = params.getIntEx(Y);
		int w = params.getIntEx(W);
		int h = params.getIntEx(H);
		if (params.getInt(FILL, 0) == 1) {
			cvs.getG2D().fill(new Ellipse2D.Double(x,y,w,h));	
		} else {
			cvs.getG2D().draw(new Ellipse2D.Double(x,y,w,h));	
		}
		
		cvs.refresh();
		return 1;
	}
	
	private int cmdEllipse(DictReader params, Canvas cvs) {
		double cx = params.getDoubleEx(X);
		double cy = params.getDoubleEx(Y);
		double w = params.getDoubleEx(W);
		double h = params.getDoubleEx(H);
		if (params.getInt(FILL, 0) == 1) {
			cvs.getG2D().fill(new Ellipse2D.Double(cx-w/2, cy-h/2, w, h));	
		} else {
			cvs.getG2D().draw(new Ellipse2D.Double(cx-w/2, cy-h/2, w, h));	
		}
		
		cvs.refresh();
		return 1;
	}
	
	private int cmdSetColor(DictReader params, Canvas cvs) {
		cvs.getG2D().setColor(new Color(params.getInt(R, 0),
							  			params.getInt(G, 0),
							  			params.getInt(B, 0)));
		cvs.refresh();
		return 1;
	}
	
	private int symToCap(String s) {
		switch (s) {
		case "butt": return BasicStroke.CAP_BUTT;
		case "round": return BasicStroke.CAP_ROUND;
		case "square": return BasicStroke.CAP_SQUARE;
		default: return -1;
		}
	}
	
	private int symToJoin(String s) {
		switch (s) {
		case "bevel": return BasicStroke.JOIN_BEVEL;
		case "miter": return BasicStroke.JOIN_MITER;
		case "round": return BasicStroke.JOIN_ROUND;
		default: return -1;
		}
	}
	
	private int cmdSetStroke(DictReader params, Canvas cvs) {
		BasicStroke prev = (BasicStroke)(cvs.getG2D().getStroke());
		int cap  = symToCap(params.getSymString(CAP, ""));
		int join = symToCap(params.getSymString(JOIN, ""));
		cvs.getG2D().setStroke(new BasicStroke(
				params.getFloat(WIDTH, prev.getLineWidth()),
				cap  == -1 ? prev.getEndCap() : cap,
				join == -1 ? prev.getLineJoin() : join
				));
		return 1;
	}
	
	private int cmdShow(DictReader params, Canvas cvs) {
		cvs.show();
		return 1;
	}
	
	private int cmdSave(DictReader params, Canvas cvs) {
		String filename = params.getStringEx(FILE);
		
		try {
			File f = new File(FileUtils.workingRelative(filename));
			cvs.save(f);
		} catch (IOException e) {
			return 0;
		}
		
		return 1;
	}

}
