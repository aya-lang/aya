package aya.util;

import java.awt.Color;
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
	private final long WIDTH = Variable.encodeString("width");
	private final long HEIGHT = Variable.encodeString("height");
	private final long NAME = Variable.encodeString("name");
	private final long FILE = Variable.encodeString("file");
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
		case "set_color": return cmdSetColor(params, cvs);
		case "save": return cmdSave(params, cvs);
		default:
			throw new AyaRuntimeException("Canvas: Unknown command '" + command.name() + "'.");
		}

	}
	
	private int cmdNew(DictReader params) {
		int id = _table.newCanvas(params.getString(NAME, "Canvas"), params.getIntEx(WIDTH), params.getIntEx(HEIGHT));
		Canvas c = _table.getCanvas(id);
		c.setShowOnRefresh(true);
		c.show();
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
		cvs.getG2D().drawRect(params.getIntEx(X),
							  params.getIntEx(Y),
							  params.getIntEx(W),
							  params.getIntEx(H));
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
