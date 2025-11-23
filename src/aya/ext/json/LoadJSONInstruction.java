package aya.ext.json;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;

public class LoadJSONInstruction extends NamedOperator {
	
	public LoadJSONInstruction() {
		super("json.loads");
		_doc = "json::str -> object::any\n" +
				"Convert a JSON string into an object";
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
