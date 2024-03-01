package aya.ext.graphics.instruction;

import java.io.File;
import java.io.IOException;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.number.Num;
import aya.util.FileUtils;

public class SaveGraphicsInstruction extends GraphicsInstruction {

	public SaveGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "save", "N");
		_doc = "filename canvas_id: save the canvas as an image";
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		String filename = _reader.popString();
		
		try {
			File f = new File(FileUtils.workingRelative(filename));
			cvs.save(f);
		} catch (IOException e) {
			blockEvaluator.push(Num.ZERO);
		}
		
		blockEvaluator.push(Num.ONE);
	}
	
}



