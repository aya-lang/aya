package aya.instruction.variable;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.obj.Obj;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public class QuoteGetVariableInstruction extends VariableInstruction {

	public QuoteGetVariableInstruction(SourceStringRef source, Symbol var) {
		super(source, var);
	}
	
	@Override
	public void execute(BlockEvaluator b) {
		for (Symbol variable : variables_) {
			Obj o = b.getContext().getVars().getVar(variable);
			b.push(o);
		}
	}
	
	/**
	 * If o is a blockEvaluator, dump it's instructions. Otherwise, add it to the stack
	 * @param o
	 * @param b
	 */
	public static void addOrDumpVar(Obj o, BlockEvaluator evaluator) {
		if (o.isa(Obj.BLOCK)) {
			//evaluator.getInstructions().addAll(((BlockEvaluator)o).getInstructions().getInstrucionList());
			evaluator.dump(Casting.asStaticBlock(o));
		} else {
			evaluator.push(o);
		}
	}
	
	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(varName_ + ".`");
		return stream;
	}
}
