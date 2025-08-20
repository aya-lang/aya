package aya.ext.unicode;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.list.numberlist.DoubleList;
import aya.obj.list.numberlist.NumberList;
import aya.util.Casting;

public class UTF16FromCodeUnitsInstruction extends NamedOperator {

	public UTF16FromCodeUnitsInstruction() {
		super("utf16.from_code_units");
		_doc = "(L) convert list of code units (16 bit numbers) to a string using utf-16 encoding";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj l = blockEvaluator.pop();
		final NumberList codeUnits;
		if (l.isa(Obj.NUMBERLIST)) {
			codeUnits = Casting.asNumberList(l);
		} else if (l.isa(Obj.NUMBER)) {
			codeUnits = new DoubleList(Casting.asNumber(l).toDouble(), 1);
		} else {
			throw new TypeError(this, "L|N", l);
		}

		char[] chars = new char[codeUnits.length()];
		for (int i = 0; i < codeUnits.length(); i++) {
			int codeUnit = codeUnits.get(i).toInt() & 0xffff;
			chars[i] = (char) codeUnit; // this works without extra effort because Java uses UTF-16 internally
		}
		blockEvaluator.push(List.fromString(new String(chars)));
	}
}
