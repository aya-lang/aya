package aya.instruction.variable;

import aya.Aya;
import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public class QuoteGetVariableInstruction extends VariableInstruction {

	public QuoteGetVariableInstruction(SourceStringRef source, Symbol var) {
		super(source, var);
	}
	
	@Override
	public void execute(Block b) {
		Obj o = Aya.getInstance().getVars().getVar(variable_);
		b.push(o);
	}
	
	/**
	 * If o is a block, dump it's instructions. Otherwise, add it to the stack
	 * @param o
	 * @param b
	 */
	public static void addOrDumpVar(Obj o, Block evaluator) {
		if (o.isa(Obj.BLOCK)) {
			//evaluator.getInstructions().addAll(((Block)o).getInstructions().getInstrucionList());
			evaluator.dump(Casting.asStaticBlock(o));
		} else {
			evaluator.push(o);
		}
	}
	
	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(variable_.name() + ".`");
		return stream;
	}
}
