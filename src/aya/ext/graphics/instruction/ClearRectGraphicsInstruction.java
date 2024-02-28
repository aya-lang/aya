package aya.ext.graphics.instruction;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.BlockEvaluator;

public class ClearRectGraphicsInstruction extends GraphicsInstruction {

	public ClearRectGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "clear_rect", "NNNNN");
		_doc = "x y w h canvas_id: clear a rectangle";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		int h = _reader.popInt();
		int w = _reader.popInt();
		int y = _reader.popInt();
		int x = _reader.popInt();

		cvs.getG2D().clearRect(x, y, w, h);
		
		cvs.refresh();
	}
}



