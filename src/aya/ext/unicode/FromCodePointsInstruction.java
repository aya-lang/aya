package aya.ext.unicode;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.list.numberlist.DoubleList;
import aya.obj.list.numberlist.NumberList;
import aya.util.Casting;
import aya.util.UTF16;

public class FromCodePointsInstruction extends NamedOperator {
	public FromCodePointsInstruction() {
		super("unicode.from_code_points");
		_doc = "(L|N) convert code-point(s) to unicode string";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj l = blockEvaluator.pop();
		final NumberList codePoints;
		if (l.isa(Obj.NUMBERLIST)) {
			codePoints = Casting.asNumberList(l);
		}else if(l.isa(Obj.NUMBER)) {
			codePoints = new DoubleList(Casting.asNumber(l).toDouble(), 1);
		}else{
			throw new TypeError(this, "L|N", l);
		}

		StringBuilder s = new StringBuilder();
		for (int i = 0; i < codePoints.length(); i++) {
			UTF16.codePointToStr(codePoints.get(i).toInt(), s);
		}
		blockEvaluator.push(List.fromString(s.toString()));
	}
}
