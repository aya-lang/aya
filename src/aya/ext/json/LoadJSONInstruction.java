package aya.plugin.json;

import aya.exceptions.TypeError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.Str;

public class LoadJSONInstruction extends NamedInstruction {
	
	public LoadJSONInstruction() {
		super("json.loads");
	}

	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		
		try {
			block.push(JSONInterface.decodeJSON(((Str)a).str(), JSONInterface.JSONParams.getDefaultDecode()));
		} catch (ClassCastException e) {
			throw new TypeError(this, "::str", a);
		}
	}

}
