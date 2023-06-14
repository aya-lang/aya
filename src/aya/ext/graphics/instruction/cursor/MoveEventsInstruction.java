package aya.ext.graphics.instruction.cursor;

import aya.exceptions.runtime.ValueError;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasCursorListener;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.Block;
import aya.obj.list.ListCollector;

public class MoveEventsInstruction  extends GraphicsInstruction {

	public MoveEventsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "move_events", "N");
		_doc = "canvas_id: returns a list of dictionaries containing the cursor move events since the last time this instruction was called";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, Block block) {
		try {
			block.push(
					cvs.getCursorListener().getMoveHistory().stream()
							.map(CanvasCursorListener.MoveInfo::toDict)
							.collect(new ListCollector())
			);
		} catch (NullPointerException e) {
			throw new ValueError(e.getMessage());
		}
	}
}
