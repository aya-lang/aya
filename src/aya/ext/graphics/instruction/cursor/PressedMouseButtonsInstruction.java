package aya.ext.graphics.instruction.cursor;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.obj.list.ListCollector;
import aya.obj.number.Num;

public class PressedMouseButtonsInstruction extends GraphicsInstruction {

	public PressedMouseButtonsInstruction(CanvasTable canvas_table) {
		super(canvas_table, "pressed_mouse_buttons", "N");
		_doc = "canvas_id: returns a list of currently held mouse buttons (list of integers). E.g. Button_1 (left click) will be '1'";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, Block block) {
		List buttonList = cvs.getCursorListener().getPressedButtons().stream()
				.map(Num::fromInt)
				.collect(new ListCollector());

		block.push(buttonList);
	}
}
