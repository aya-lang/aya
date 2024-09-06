package aya.instruction.index;

import aya.Aya;
import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

public class SetVarIndexInstruction extends SetIndexInstruction {
	
	private Symbol _var;

	public SetVarIndexInstruction(SourceStringRef source, Symbol var) {
		super(source);
		_var = var;
	}
	
	@Override
	protected Obj getIndex() {
		return Aya.getInstance().getVars().getVar(_var);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(".:[");
		stream.print(_var.name());
		stream.print("]");
		return stream;
	}
}
