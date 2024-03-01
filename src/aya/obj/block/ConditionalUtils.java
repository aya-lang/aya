package aya.obj.block;

import java.util.ArrayList;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.BlockLiteralInstruction;
import aya.instruction.Instruction;
import aya.obj.Obj;

public class ConditionalUtils {

	private static boolean evalCondition(Instruction instruction) {
		BlockEvaluator b = new BlockEvaluator();
		b.add(instruction);
		b.eval();
		if (!b.getStack().isEmpty()) {
			return b.pop().bool();
		} else {
			ReprStream rs = new ReprStream();
			instruction.repr(rs);
			throw new TypeError("Condition did not return a result: " + rs.toString());
		}
	}
	
	private static Obj evalResult(Instruction instruction) {
		BlockEvaluator b = new BlockEvaluator();
		if (instruction instanceof BlockLiteralInstruction) {
			b.dump(((BlockLiteralInstruction)instruction).getRawBlock());
		} else {
			b.add(instruction);
		}
		b.eval();
		if (!b.getStack().isEmpty()) {
			return b.pop();
		} else {
			return null;
		}
	}
	
	public static Obj runConditional(StaticBlock block) {
		ArrayList<Instruction> instructions = block.getInstructions();
		int i;
		for (i = instructions.size()-1; i > 0; i-=2) {
			if (evalCondition(instructions.get(i))) {
				return evalResult(instructions.get(i-1));
			}
		}
		if (i == 0) {
			return evalResult(instructions.get(i));
		} else {
			return null;
		}
	}
}
