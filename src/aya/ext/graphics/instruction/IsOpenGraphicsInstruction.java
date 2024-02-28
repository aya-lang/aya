package aya.ext.graphics.instruction;

import aya.exceptions.runtime.TypeError;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.Obj;
import aya.obj.block.BlockEvaluator;
import aya.obj.number.Num;
import aya.util.Casting;

public class IsOpenGraphicsInstruction extends GraphicsInstruction {

	public IsOpenGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "isopen", "N");
		_doc = "canvas_id: test if the view is open";
	}
	
	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj o_id = blockEvaluator.pop();

		if (o_id.isa(Obj.NUMBER)) {
			Canvas cvs = _canvas_table.getCanvas(Casting.asNumber(o_id).toInt());
			if (cvs == null || !cvs.isOpen()) {
				blockEvaluator.push(Num.ZERO);
			} else {
				blockEvaluator.push(Num.ONE);
			}
		} else {
			throw new TypeError(this, "N", o_id);
		}
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) { }
	
}



