package aya.ext.unicode;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.list.numberlist.DoubleList;

public class UTF16ToCodeUnitsInstruction extends NamedOperator {
	public UTF16ToCodeUnitsInstruction() {
		super("utf16.to_code_units");
		_doc = "(S) convert string to list of code units (16 bit numbers) using utf-16 encoding";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj s = blockEvaluator.pop();
		if (!s.isa(Obj.STR)) {
			throw new TypeError(this, "S", s);
		}

		char[] chars = s.str().toCharArray();
		double[] codeUnits = new double[chars.length];
		for (int i = 0; i < chars.length; i++) {
			codeUnits[i] = chars[i] & 0xffff; // this works without extra effort because Java uses UTF-16 internally
		}
		blockEvaluator.push(new List(new DoubleList(codeUnits)));
	}
}
