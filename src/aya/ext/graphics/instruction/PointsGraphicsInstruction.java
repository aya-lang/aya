package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IndexError;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.list.numberlist.NumberList;
import aya.util.Casting;

public class PointsGraphicsInstruction extends GraphicsInstruction {

	public PointsGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "points", "LNNN");
		_doc = "points width height canvas_id: draw a batch of points";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		final int h = _reader.popInt();
		final int w = _reader.popInt();
		List points = _reader.popList();
		
		final int half_h = h/2;
		final int half_w = w/2;
		
		try {
			for (int i = 0; i < points.length(); i++) {
				Obj point_obj = points.getExact(i);
				if (point_obj.isa(Obj.LIST)) {
					NumberList point = Casting.asList(points.getExact(i)).toNumberList();
					int x = point.get(0).toInt();
					int y = point.get(1).toInt();
			
					cvs.getG2D().fillRect(x-half_w, y-half_h, w, h);	
				}
			}
		} catch (IndexOutOfBoundsException e) {
			throw new IndexError("All points must have length 2");
		}

		cvs.refresh();
	}	
}



