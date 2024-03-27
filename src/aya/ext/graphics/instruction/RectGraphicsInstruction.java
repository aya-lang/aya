package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class RectGraphicsInstruction extends GraphicsInstruction {

	public RectGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "rect", "NNNNNN");
		_doc = "x y w h fill canvas_id: draw a rectangle";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		boolean fill = _reader.popBool();
		int h = _reader.popInt();
		int w = _reader.popInt();
		int y = _reader.popInt();
		int x = _reader.popInt();
	
		if (fill) {
			cvs.getG2D().fillRect(x, y, w, h);
		} else {
			cvs.getG2D().drawRect(x, y, w, h);
		}
		
		cvs.refresh();
	}
}



