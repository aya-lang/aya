package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class ShearGraphicsInstruction extends GraphicsInstruction {

	public ShearGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "shear", "NNN");
		_doc = "x y canvas_id: shear the canvas";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		double y = _reader.popDouble();
		double x = _reader.popDouble();
	
		cvs.getG2D().shear(x, y);
	
		cvs.refresh();
	}
}



