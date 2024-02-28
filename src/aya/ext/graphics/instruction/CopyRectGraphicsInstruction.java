package aya.ext.graphics.instruction;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.BlockEvaluator;

public class CopyRectGraphicsInstruction extends GraphicsInstruction {

	public CopyRectGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "copy", "NNNNNNNN");
		_doc = "x y w h dx dy canvas_id: copy an area to another location";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		int dy = _reader.popInt();
		int dx = _reader.popInt();
		int h = _reader.popInt();
		int w = _reader.popInt();
		int y = _reader.popInt();
		int x = _reader.popInt();
	
		cvs.getG2D().copyArea(x, y, w, h, dx, dy);
		cvs.refresh();
	}
}



