package aya.ext.graphics.instruction;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.ext.image.AyaImage;
import aya.obj.Obj;
import aya.util.Casting;
import aya.util.DictReader;

public class DrawImageGraphicsInstruction extends GraphicsInstruction {

	public DrawImageGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "drawimage", "NND");
		_doc = "x_offset y_offset image";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		var img_data = blockEvaluator.pop();
		int y_offset = _reader.popInt();
		int x_offset = _reader.popInt();
		if (img_data.isa(Obj.DICT)) {
			var image = new AyaImage(new DictReader(Casting.asDict(img_data)));
			cvs.drawImage(image.toBufferedImage(), x_offset, y_offset);
			cvs.refresh();
		} else {
			throw new TypeError("canvas.drawimage");
		}
	}
}



