package aya.instruction.variable;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

public class SetKeyVariableInstruction extends VariableInstruction {

	public SetKeyVariableInstruction(SourceStringRef source, Symbol var) {
		super(source, var);
	}
	
	@Override
	public void execute(BlockEvaluator b) {
		final Obj kv_obj = b.pop();
		if (kv_obj.isa(Obj.DICT)) {
			Dict dict;
			dict = (Dict)kv_obj;
			for (int i = variables_.length - 1; i >= 0; i--) {
				dict.set(variables_[i], b.pop());
			}
			b.push(dict);
		}
	}
	
	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(".:" + varName_);
		return stream;
	}
}
