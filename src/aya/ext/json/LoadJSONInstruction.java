package aya.ext.json;

import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;

public class LoadJSONInstruction extends NamedInstruction {
	
	public LoadJSONInstruction() {
		super("json.loads");
		_doc = "Convert a JSON string into a dict";
	}

	@Override
	public void execute(Block block) {
		final Obj a = block.pop();
		
		if (a.isa(Obj.STR)) {
			block.push(JSONUtils.decodeJSON(a.str(), JSONUtils.JSONParams.getDefaultDecode()));
		} else {
			throw new TypeError(this, "::str", a);
		}
	}

}
