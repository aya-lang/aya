package aya.ext.graphics.instruction;

import java.awt.Color;
import java.awt.GradientPaint;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class SetPaintGradGraphicsInstruction extends GraphicsInstruction {

	public SetPaintGradGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "set_paint_grad", "NNNNNNNNNNNN");
		_doc = "x1 y1 x2 y2 start_r start_g start_b start_a end_r end_g end_b end_a cycle canvas_id: set a gradient paint";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		boolean cycle = _reader.popBool();
		int end_a = _reader.popInt();
		int end_b = _reader.popInt();
		int end_g = _reader.popInt();
		int end_r = _reader.popInt();
		int start_a = _reader.popInt();
		int start_b = _reader.popInt();
		int start_g = _reader.popInt();
		int start_r = _reader.popInt();
		float y2 = (float)(_reader.popDouble());
		float x2 = (float)(_reader.popDouble());
		float y1 = (float)(_reader.popDouble());
		float x1 = (float)(_reader.popDouble());

		Color start = new Color(start_r, start_g, start_b, start_a);
		Color end = new Color(end_r, end_g, end_b, end_a);
			
		cvs.getG2D().setPaint(new GradientPaint(x1, y1, start, x2, y2, end, cycle));
	}
	
}



