package aya.ext.graphics.instruction.cursor;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasCursorListener;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.Block;
import aya.obj.list.ListCollector;

public class ClickEventsInstruction extends GraphicsInstruction {

	public ClickEventsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "click_events", "N");
		_doc = "canvas_id: returns a list of dictionaries containing the click events since the last time this instruction was called";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, Block block) {
		block.push(
				cvs.getCursorListener().getClickHistory().stream()
						.map(CanvasCursorListener.CursorInfo::toDict)
						.collect(new ListCollector())
		);
	}
}
