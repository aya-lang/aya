package aya.ext.graphics.instruction;

import java.awt.Color;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.Block;

public class SetColorGraphicsInstruction extends GraphicsInstruction {

	public SetColorGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "set_color", "NNNN");
		_doc = "r g b canvas_id: set the color of the pen";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, Block block) {
		int b = _reader.popInt();
		int g = _reader.popInt();
		int r = _reader.popInt();

		cvs.getG2D().setColor(new Color(r, g, b));
	}
	
}



