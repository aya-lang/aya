package aya.ext.graphics.instruction;

import java.awt.BasicStroke;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.ValueError;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class SetStrokeWidthGraphicsInstruction extends GraphicsInstruction {

	public SetStrokeWidthGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "set_stroke_width", "NN");
		_doc = "width canvas_id: set the stroke width of the pen";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		float width = (float)(_reader.popDouble());

		BasicStroke prev = (BasicStroke)(cvs.getG2D().getStroke());
		
		try {
			cvs.getG2D().setStroke(new BasicStroke(width, prev.getEndCap(), prev.getLineJoin()));
		} catch (IllegalArgumentException e) {
			throw new ValueError("Invalid parameters for set_stroke_width: " + width);
		}
	}
	
}



