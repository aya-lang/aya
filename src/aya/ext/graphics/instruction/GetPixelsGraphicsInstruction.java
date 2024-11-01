package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.ext.image.AyaImage2;

public class GetPixelsGraphicsInstruction extends GraphicsInstruction {

	public GetPixelsGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "get_pixels", "");
		_doc = "canvas_id: get pixels ";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		AyaImage2 img = new AyaImage2( cvs.getBuffer() );
		blockEvaluator.push(img.toDict());
	}
}



