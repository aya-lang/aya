package aya.obj.block;

import java.util.ArrayList;

import aya.ReprStream;
import aya.eval.ExecutionContext;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.BlockLiteralInstruction;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.util.Casting;

public class ConditionalUtils {

	private static boolean evalCondition(ExecutionContext context, Instruction instruction) {
		BlockEvaluator b = context.createEvaluator();
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
	
	private static Obj evalResult(ExecutionContext context, Instruction instruction) {
		BlockEvaluator b = context.createEvaluator();
		if (instruction instanceof BlockLiteralInstruction) {
			//Convert the block instruction into an actual block
			BlockEvaluator b2 = context.createEvaluator();
			b2.add(instruction);
			b2.eval();
			b.dump(Casting.asStaticBlock(b2.pop()));
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
	
	public static Obj runConditional(ExecutionContext context, StaticBlock block) {
		ArrayList<Instruction> instructions = block.getInstructions();
		int i;
		for (i = instructions.size()-1; i > 0; i-=2) {
			if (evalCondition(context, instructions.get(i))) {
				return evalResult(context, instructions.get(i-1));
			}
		}
		if (i == 0) {
			return evalResult(context, instructions.get(i));
		} else {
			return null;
		}
	}
}
