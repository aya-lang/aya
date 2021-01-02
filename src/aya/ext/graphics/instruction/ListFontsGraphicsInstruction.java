package aya.ext.graphics.instruction;

import java.awt.GraphicsEnvironment;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.Block;
import aya.obj.list.List;

public class ListFontsGraphicsInstruction extends GraphicsInstruction {

	public ListFontsGraphicsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "list_fonts", "");
		_doc = "-: List available graphics fonts";
	}
	
	@Override
	public void execute(Block block) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] font_names = ge.getAvailableFontFamilyNames();
		List l = new List();
		for (int i = 0; i < font_names.length; i++) {
			l.mutAdd(List.fromString(font_names[i]));
		}
		block.push(l);
	}
	

	@Override
	protected void doCanvasCommand(Canvas cvs, Block block) {
		// noop
	}
	
}