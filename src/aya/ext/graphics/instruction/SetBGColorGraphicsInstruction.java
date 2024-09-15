package aya.ext.graphics.instruction;

import java.awt.Color;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class SetBGColorGraphicsInstruction extends GraphicsInstruction {

	public SetBGColorGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "set_bgcolor", "NNNN");
		_doc = "r g b canvas_id: set the background color of the pen";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		int b = _reader.popInt();
		int g = _reader.popInt();
		int r = _reader.popInt();

		cvs.getG2D().setBackground(new Color(r, g, b));
	}
	
}



