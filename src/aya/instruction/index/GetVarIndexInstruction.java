package aya.instruction.index;

import aya.Aya;
import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.symbol.SymbolEncoder;

public class GetVarIndexInstruction extends GetIndexInstruction {
	
	private long _var;

	public GetVarIndexInstruction(long var) {
		_var = var;
	}
	
	@Override
	protected Obj getIndex() {
		return Aya.getInstance().getVars().getVar(_var);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(".[");
		stream.print(SymbolEncoder.decodeLong(_var));
		stream.print("]");
		return stream;
	}
}
