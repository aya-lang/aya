package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class LineGraphicsInstruction extends GraphicsInstruction {

	public LineGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "line", "NNNNN");
		_doc = "x1 y1 x2 y2 canvas_id: draw a line";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		int y2 = _reader.popInt();
		int x2 = _reader.popInt();
		int y1 = _reader.popInt();
		int x1 = _reader.popInt();
	
		cvs.getG2D().drawLine(x1, y1, x2, y2);
		cvs.refresh();
	}
}



