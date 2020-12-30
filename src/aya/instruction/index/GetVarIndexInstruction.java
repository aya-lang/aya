package aya.instruction.index;

import aya.Aya;
import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.symbol.Symbol;

public class GetVarIndexInstruction extends GetIndexInstruction {
	
	private Symbol _var;

	public GetVarIndexInstruction(Symbol var) {
		_var = var;
	}
	
	@Override
	protected Obj getIndex() {
		return Aya.getInstance().getVars().getVar(_var);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(".[");
		stream.print(_var.name());
		stream.print("]");
		return stream;
	}
}
