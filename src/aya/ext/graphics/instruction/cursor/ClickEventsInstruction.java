package aya.ext.graphics.instruction.cursor;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasCursorListener;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.list.ListCollector;

public class ClickEventsInstruction extends GraphicsInstruction {

	public ClickEventsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "click_events", "N");
		_doc = "canvas_id: returns a list of dictionaries containing the click events since the last time this instruction was called";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		blockEvaluator.push(
				cvs.getCursorListener().getClickHistory().stream()
						.map(CanvasCursorListener.ClickInfo::toDict)
						.collect(new ListCollector())
		);
	}
}
