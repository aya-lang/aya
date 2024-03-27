package aya.ext.graphics.instruction;

import java.awt.geom.GeneralPath;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.ValueError;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;

public class PathGraphicsInstruction extends GraphicsInstruction {

	public PathGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "path", "N");
		_doc = "xs ys fill canvas_id: draw a path";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		boolean fill = _reader.popBool();
		double[] ys = _reader.popNumberList().todoubleArray();
		double[] xs = _reader.popNumberList().todoubleArray();
		
		if (xs.length != ys.length) {
			throw new ValueError(
					":{graphics.path} : 'xs' and 'ys' must be the same length. Got xs(" +
					xs.length + "), ys(" + ys.length + ")");
		}
		
		if (xs.length == 0) {
			throw new ValueError(
					":{graphics.path} : 'xs' and 'ys' must contain at least 1 point.");
		}
		
		GeneralPath path = new GeneralPath();
		path.moveTo(xs[0], ys[0]);
		
		for (int i = 1; i < xs.length; i++) {
			path.lineTo(xs[i], ys[i]);
		}
		
		path.closePath();
		
		if (fill) {
			cvs.getG2D().fill(path);
		} else {
			cvs.getG2D().draw(path);
		}
	}
	
}



