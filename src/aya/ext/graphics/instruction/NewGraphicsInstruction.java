package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.Obj;
import aya.obj.number.Num;
import aya.obj.symbol.SymbolConstants;
import aya.util.Casting;
import aya.util.DictReader;

public class NewGraphicsInstruction extends GraphicsInstruction {

	public NewGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "new", "D");
		_doc = "params: create a new canvas";
	}
	
	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj o_params = blockEvaluator.pop();
		
		if (o_params.isa(Obj.DICT)) {
			DictReader params = new DictReader(Casting.asDict(o_params));
			params.setErrorName(":(graphics.new)");
			int id = _canvas_table.newCanvas(params.getString(SymbolConstants.NAME, "Canvas"),
									  params.getIntEx(SymbolConstants.WIDTH),
									  params.getIntEx(SymbolConstants.HEIGHT),
									  params.getDouble(SymbolConstants.SCALE, 1.0));
			Canvas c = _canvas_table.getCanvas(id);
			c.setShowOnRefresh(params.getInt(SymbolConstants.AUTOFLUSH, 0) != 0);
			if (params.getInt(SymbolConstants.SHOW, 1) == 1) {
				c.show();
			}
			blockEvaluator.push(Num.fromInt(id));
		} else {
			throw new TypeError(this, "D", o_params);
		}
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		// noop
	}
	
}