package aya.ext.json;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;

public class ToJSONInstruction extends NamedOperator {
	
	public ToJSONInstruction() {
		super("json.dumps");
		_doc = "Serialize a dict to a json string";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj a = blockEvaluator.pop();
		
		if (!a.isa(Obj.DICT)) {
			throw new TypeError(this, "::dict", a);
		}
		
		blockEvaluator.push(List.fromString(JSONUtils.encodeJSON(a, JSONUtils.JSONParams.getDefaultEncode())));
	}

}
