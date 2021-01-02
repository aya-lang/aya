package aya.ext.graphics.instruction;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.Block;

public class ClearGraphicsInstruction extends GraphicsInstruction {

	public ClearGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "clear", "N");
		_doc = "canvas_id: clear the canvas";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, Block block) {
		cvs.getG2D().clearRect(0, 0, cvs.getWidth(), cvs.getHeight());
		cvs.refresh();
	}
	
}