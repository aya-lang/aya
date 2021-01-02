package aya.ext.graphics.instruction;

import java.awt.geom.Ellipse2D;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.Block;

public class OvalGraphicsInstruction extends GraphicsInstruction {

	public OvalGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "oval", "NNNNN");
		_doc = "x y width height fill canvas_id: draw an oval";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, Block block) {
		boolean fill = _reader.popBool();
		int h = _reader.popInt();
		int w = _reader.popInt();
		int y = _reader.popInt();
		int x = _reader.popInt();

		if (fill) {
			cvs.getG2D().fill(new Ellipse2D.Double(x,y,w,h));	
		} else {
			cvs.getG2D().draw(new Ellipse2D.Double(x,y,w,h));	
		}
		
		cvs.refresh();
	}
	
}



