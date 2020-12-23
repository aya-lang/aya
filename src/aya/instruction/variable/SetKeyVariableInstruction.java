package aya.instruction.variable;

import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.symbol.SymbolEncoder;

public class SetKeyVariableInstruction extends VariableInstruction {

	public SetKeyVariableInstruction(long id) {
		this.variable_ = id;
	}
	
	@Override
	public void execute(Block b) {
		final Obj kv_obj = b.pop();
		if (kv_obj.isa(Obj.DICT)) {
			Dict dict;
			dict = (Dict)kv_obj;
			dict.set(variable_, b.pop());
			b.push(dict);
		}
	}
	
	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(".:" + SymbolEncoder.decodeLong(variable_));
		return stream;
	}
}
