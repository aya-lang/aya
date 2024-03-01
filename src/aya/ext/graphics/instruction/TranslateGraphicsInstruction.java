package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class TranslateGraphicsInstruction extends GraphicsInstruction {

	public TranslateGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "translate", "NNN");
		_doc = "x y canvas_id: translate the canvas";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		double y = _reader.popDouble();
		double x = _reader.popDouble();
	
		cvs.getG2D().translate(x, y);
	
		cvs.refresh();
	}
}



