package aya.ext.graphics.instruction.keyboard;

import aya.eval.BlockEvaluator;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.CanvasTable;
import aya.ext.graphics.GraphicsInstruction;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.ListCollector;
import aya.obj.list.Str;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.util.AWTKeyDecoder;

public class PressedKeysInstruction extends GraphicsInstruction {

	private static final Symbol KEYCODE = SymbolTable.getSymbol("keycode");
	private static final Symbol KEY_NAME = SymbolTable.getSymbol("key_name");
	private static final Symbol LOCATION = SymbolTable.getSymbol("location");
	private static final Symbol LOCATION_NAME = SymbolTable.getSymbol("location_name");

	public PressedKeysInstruction(CanvasTable canvas_table) {
		super(canvas_table, "pressed_keys", "N");
		_doc = "canvas_id: returns a list of dictionaries containing the currently held keys {keycode; key_name; location; location_name;}";
	}

	@Override
	protected void doCanvasCommand(Canvas cvs, BlockEvaluator blockEvaluator) {
		List keyList = cvs.getKeyListener().getPressedKeys().stream()
				.map(event -> {
					Dict keyDict = new Dict();

					int keycode = event.getKeyCode();
					keyDict.set(KEYCODE, Num.fromInt(keycode));
					keyDict.set(KEY_NAME, new List(new Str(AWTKeyDecoder.findNamedKey(keycode))));

					int keyLocation = event.getKeyLocation();
					keyDict.set(LOCATION, Num.fromInt(keyLocation));
					keyDict.set(LOCATION_NAME, new List(new Str(AWTKeyDecoder.findLocationName(keyLocation))));

					return keyDict;
				})
				.collect(new ListCollector());

		blockEvaluator.push(keyList);
	}
}
