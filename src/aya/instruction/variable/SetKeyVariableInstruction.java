package aya.instruction.variable;

import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

public class SetKeyVariableInstruction extends VariableInstruction {

	public SetKeyVariableInstruction(SourceStringRef source, Symbol var) {
		super(source, var);
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
		stream.print(".:" + variable_.name());
		return stream;
	}
}
