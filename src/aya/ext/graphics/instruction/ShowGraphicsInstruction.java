package aya.ext.graphics.instruction;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.BlockEvaluator;

public class ShowGraphicsInstruction extends GraphicsInstruction {

	public ShowGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "show", "N");
		_doc = "canvas_id: display the canvas";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		cvs.show();
	}
	
}



