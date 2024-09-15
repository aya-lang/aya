package aya.ext.graphics.instruction;

import java.awt.BasicStroke;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.ValueError;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class SetStrokeGraphicsInstruction extends GraphicsInstruction {

	public SetStrokeGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "set_stroke", "NSSN");
		_doc = "width cap join canvas_id: set the stroke of the pen";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		int join = strToJoin(_reader.popString());
		int cap  = strToCap(_reader.popString());
		float width = (float)(_reader.popDouble());
		
		try {
			cvs.getG2D().setStroke(new BasicStroke(width, cap, join));
		} catch (IllegalArgumentException e) {
			throw new ValueError("Invalid parameters for setstroke: " + width + " " + cap + " " + join);
		}
	}

	
	private int strToCap(String s) {
		switch (s) {
		case "butt": return BasicStroke.CAP_BUTT;
		case "round": return BasicStroke.CAP_ROUND;
		case "square": return BasicStroke.CAP_SQUARE;
		default: return -1;
		}
	}
	
	private int strToJoin(String s) {
		switch (s) {
		case "bevel": return BasicStroke.JOIN_BEVEL;
		case "miter": return BasicStroke.JOIN_MITER;
		case "round": return BasicStroke.JOIN_ROUND;
		default: return -1;
		}
	}
	
}



