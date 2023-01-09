package aya.ext.graphics.instruction.keyboard;

import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.obj.list.ListCollector;
import aya.obj.character.Char;

public class TypedKeysInstruction extends GraphicsInstruction {

	public TypedKeysInstruction(CanvasTable canvas_table) {
		super(canvas_table, "typed_keys", "N");
		_doc = "canvas_id: returns a list of unicode characters that were typed since the last time this instruction was executed.";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, Block block) {
		List typedCharList = cvs.getKeyListener().consumeTypedChars().stream()
				.map(Char::valueOf)
				.collect(new ListCollector());

		block.push(typedCharList);
	}
}
