package aya.ext.graphics;

import aya.exceptions.TypeError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;

public class LegacyGraphicsInstruction extends NamedInstruction {

	private CanvasInterface canvas;
	
	public LegacyGraphicsInstruction() {
		super("graphics.MG");
		_doc = "JDN 2D graphics interface";

		this.canvas = new CanvasInterface();
	}

	@Override
	public void execute(Block block) {
		final Obj o_id = block.pop();
		final Obj o_params = block.pop();
		final Obj o_command = block.pop();
		
		
		if (o_id.isa(Obj.NUM) && o_params.isa(Obj.DICT) && o_command.isa(Obj.SYMBOL)) {
			int id = ((aya.obj.number.Number)o_id).toInt();
			Dict params = (Dict)o_params;
			Symbol command = (Symbol)o_command;
			
			int result = this.canvas.doCommand(id, command, params);
			block.push(Num.fromInt(result));
		} else {
			throw new TypeError(this, "JDN", o_id, o_params, o_command);
		}
	}

}



