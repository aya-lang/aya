package aya.ext.json;

import aya.eval.BlockEvaluator;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;

public class ToJSONInstruction extends NamedOperator {
	
	public ToJSONInstruction() {
		super("json.dumps");
		_doc = "object::any -> json::str\n" +
				"Serialize an object to a json string";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		
		blockEvaluator.push(List.fromString(JSONUtils.encodeJSON(a, JSONUtils.JSONParams.getDefaultEncode())));
	}

}
