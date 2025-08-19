package aya.ext.unicode;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.list.numberlist.DoubleList;
import aya.util.UTF16;

public class ToCodePointsInstruction extends NamedOperator {
	public ToCodePointsInstruction() {
		super("unicode.to_code_points");
		_doc = "(S) convert string to unicode code-points (list of numbers)";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Obj s = blockEvaluator.pop();
		if (!s.isa(Obj.STR)) {
			throw new TypeError(this, "S", s);
		}

		char[] chars = s.str().toCharArray();
		double[] codePoints = new double[chars.length]; // upper bound
		int cpOffset = 0;
		for (int i = 0; i < chars.length; i++, cpOffset++) {
			if (UTF16.isHighSurrogate(chars[i]) && (i + 1) < chars.length) {
				codePoints[cpOffset] = Character.toCodePoint(chars[i], chars[i + 1]);
				i++;
			} else {
				codePoints[cpOffset] = chars[i] & 0xffff;
			}
		}

		if (cpOffset < codePoints.length) {
			double[] trimmedCodePoints = new double[cpOffset];
			System.arraycopy(codePoints, 0, trimmedCodePoints, 0, cpOffset);
			codePoints = trimmedCodePoints;
		}

		blockEvaluator.push(new List(new DoubleList(codePoints)));
	}
}
