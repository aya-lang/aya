package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class ScaleGraphicsInstruction extends GraphicsInstruction {

	public ScaleGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "scale", "NNN");
		_doc = "x y canvas_id: scale the canvas";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		double y = _reader.popDouble();
		double x = _reader.popDouble();
	
		cvs.getG2D().scale(x, y);
	
		cvs.refresh();
	}
}



