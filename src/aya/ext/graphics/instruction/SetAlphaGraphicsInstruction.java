package aya.ext.graphics.instruction;

import java.awt.AlphaComposite;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.Block;

public class SetAlphaGraphicsInstruction extends GraphicsInstruction {

	public SetAlphaGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "set_alpha", "NN");
		_doc = "a canvas_id: set the alpha of the pen";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, Block block) {
		double a = _reader.popDouble();
		
		double alpha = Double.max(0, Double.min(1.0, a));
		AlphaComposite alcom = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, (float)alpha);
        cvs.getG2D().setComposite(alcom);
	}
	
}



