package aya.ext.json;

import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.block.BlockEvaluator;

public class LoadJSONInstruction extends NamedOperator {
	
	public LoadJSONInstruction() {
		super("json.loads");
		_doc = "Convert a JSON string into a dict";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		
		if (a.isa(Obj.STR)) {
			blockEvaluator.push(JSONUtils.decodeJSON(a.str(), JSONUtils.JSONParams.getDefaultDecode()));
		} else {
			throw new TypeError(this, "::str", a);
		}
	}

}
