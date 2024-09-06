package aya.ext.graphics.instruction;

import java.awt.AlphaComposite;
import java.awt.Color;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class SetColorAlphaGraphicsInstruction extends GraphicsInstruction {

	public SetColorAlphaGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "set_color_alpha", "NNNNN");
		_doc = "r g b a canvas_id: set the alpha of the pen";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		double a = _reader.popDouble();
		int b = _reader.popInt();
		int g = _reader.popInt();
		int r = _reader.popInt();
		
		double alpha = Double.max(0, Double.min(1.0, a));
		AlphaComposite alcom = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, (float)alpha);
        cvs.getG2D().setComposite(alcom);

		cvs.getG2D().setColor(new Color(r, g, b));
	}
	
}



