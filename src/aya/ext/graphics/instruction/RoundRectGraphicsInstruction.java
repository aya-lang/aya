package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class RoundRectGraphicsInstruction extends GraphicsInstruction {

	public RoundRectGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "round_rect", "NNNNNNNN");
		_doc = "x y w h dh dv fill canvas_id: draw a rounded rectangle";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		boolean fill = _reader.popBool();
		int dv = _reader.popInt();
		int dh = _reader.popInt();
		int h = _reader.popInt();
		int w = _reader.popInt();
		int y = _reader.popInt();
		int x = _reader.popInt();
	
		if (fill) {
			cvs.getG2D().fillRoundRect(x, y, w, h, dh, dv);
		} else {
			cvs.getG2D().drawRoundRect(x, y, w, h, dh, dv);
		}
		
		cvs.refresh();
	}
}



