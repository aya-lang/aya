package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class TextGraphicsInstruction extends GraphicsInstruction {

	public TextGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "text", "NNNNN");
		_doc = "text x y canvas_id: draw text";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		int y = _reader.popInt();
		int x = _reader.popInt();
		String text = _reader.popString();

		cvs.getG2D().drawString(text, x, y);	
		
		cvs.refresh();
	}
}



