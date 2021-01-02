package aya.ext.graphics.instruction;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.Block;

public class ArcGraphicsInstruction extends GraphicsInstruction {

	public ArcGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "arc", "NNNNNNNN");
		_doc = "x y w h angle extent fill canvas_id: draw an arc";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, Block block) {
		boolean fill = _reader.popBool();
		int extent = _reader.popInt();
		int angle = _reader.popInt();
		int h = _reader.popInt();
		int w = _reader.popInt();
		int y = _reader.popInt();
		int x = _reader.popInt();
	
		if (fill) {
			cvs.getG2D().fillArc(x, y, w, h, angle, extent);
		} else {
			cvs.getG2D().drawArc(x, y, w, h, angle, extent);
		}
	
		cvs.refresh();
	}
}



