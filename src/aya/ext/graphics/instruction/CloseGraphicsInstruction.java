package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.Obj;
import aya.util.Casting;

public class CloseGraphicsInstruction extends GraphicsInstruction {

	public CloseGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "close", "DN");
		_doc = "canvas_id: close the window and canvas";
	}
	
	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj o_id = blockEvaluator.pop();

		if (o_id.isa(Obj.NUMBER)) {
			_canvas_table.close(Casting.asNumber(o_id).toInt());
		} else {
			throw new TypeError(this, "N", o_id);
		}
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		// noop
	}
	
}