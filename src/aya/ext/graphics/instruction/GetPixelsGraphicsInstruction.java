package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.ext.image.AyaImage;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;

public class GetPixelsGraphicsInstruction extends GraphicsInstruction {

	private static final Symbol DATA = SymbolTable.getSymbol("data");

	public GetPixelsGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "get_pixels", "");
		_doc = "canvas_id: get pixels ";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		AyaImage img = AyaImage.fromBufferedImage(cvs.getBuffer());
		blockEvaluator.push(img.toDict());
	}
}



