package aya.ext.graphics.instruction;

import java.awt.geom.Ellipse2D;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.Block;

public class EllipseGraphicsInstruction extends GraphicsInstruction {

	public EllipseGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "ellipse", "NNNNN");
		_doc = "cx cy width height fill canvas_id: draw an ellipse";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, Block block) {
		boolean fill = _reader.popBool();
		double h = _reader.popDouble();
		double w = _reader.popDouble();
		double cy = _reader.popDouble();
		double cx = _reader.popDouble();

		if (fill) {
			cvs.getG2D().fill(new Ellipse2D.Double(cx-w/2, cy-h/2, w, h));	
		} else {
			cvs.getG2D().draw(new Ellipse2D.Double(cx-w/2, cy-h/2, w, h));	
		}
		
		cvs.refresh();
	}
	
}



