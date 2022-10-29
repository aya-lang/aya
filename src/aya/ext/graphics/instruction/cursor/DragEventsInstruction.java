package aya.ext.graphics.instruction.cursor;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasCursorListener;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.Block;
import aya.obj.list.ListCollector;

public class DragEventsInstruction extends GraphicsInstruction {

	public DragEventsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "drag_events", "N");
		_doc = "canvas_id: returns a list of dictionaries containing the drag events since the last time this instruction was called";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, Block block) {
		block.push(
				cvs.getCursorListener().getDragHistory().stream()
						.map(CanvasCursorListener.CursorInfo::toDict)
						.collect(new ListCollector())
		);
	}
}
