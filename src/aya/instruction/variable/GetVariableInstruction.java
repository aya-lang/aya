package aya.instruction.variable;

import aya.Aya;
import aya.ReprStream;
import aya.instruction.flag.PopCallstackInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.block.StaticBlock;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public class GetVariableInstruction extends VariableInstruction {

	public GetVariableInstruction(SourceStringRef source, Symbol var) {
		super(source, var);
	}
	
	@Override
	public void execute(Block b) {
		Obj o = Aya.getInstance().getVars().getVar(variable_);
		this.addOrDumpVar(o, b);
	}
	
	/**
	 * If o is a block, dump it's instructions. Otherwise, add it to the stack
	 * @param o
	 * @param b
	 */
	public void addOrDumpVar(Obj o, Block b) {
		if (o.isa(Obj.BLOCK)) {
			dumpBlock(Casting.asStaticBlock(o), b);
		} else {
			b.push(o);
		}
	}

	public void dumpBlock(StaticBlock block_to_dump, Block evaluator) {
		Aya.getInstance().getCallStack().push(this);
		evaluator.add(PopCallstackInstruction.INSTANCE);
		//b.getInstructions().addAll(block_to_dump.getInstructions().getInstrucionList());
		evaluator.dump(block_to_dump);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(variable_.name());
		return stream;
	}
}
