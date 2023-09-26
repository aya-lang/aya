package aya.instruction.variable;

import aya.Aya;
import aya.ReprStream;
import aya.instruction.flag.PopCallstackInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public class GetVariableInstruction extends VariableInstruction {

	public GetVariableInstruction(SourceStringRef source, Symbol var) {
		super(var);
		this.setSource(source);
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
			dumpBlock(Casting.asBlock(o), b);
		} else {
			b.push(o);
		}
	}

	public void dumpBlock(Block block_to_dump, Block b) {
		Aya.getInstance().getCallStack().push(this);
		b.add(PopCallstackInstruction.INSTANCE);
		b.getInstructions().addAll(block_to_dump.getInstructions().getInstrucionList());
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(variable_.name());
		return stream;
	}
}
