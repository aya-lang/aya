package aya.ext.graphics.instruction;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.BlockEvaluator;

public class RotateGraphicsInstruction extends GraphicsInstruction {

	public RotateGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "rotate", "NN");
		_doc = "theta canvas_id: rotate the canvas";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		double theta = _reader.popInt();
	
		cvs.getG2D().rotate(theta);
	
		cvs.refresh();
	}
}



