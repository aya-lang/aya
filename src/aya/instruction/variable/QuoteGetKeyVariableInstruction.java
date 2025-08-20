package aya.instruction.variable;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IndexError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

public class QuoteGetKeyVariableInstruction extends VariableInstruction {

	public QuoteGetKeyVariableInstruction(SourceStringRef source, Symbol var) {
		super(source, var);
	}
	
	@Override
	public void execute(BlockEvaluator b) {
		final Obj kv_obj = b.pop();
		if (kv_obj.isa(Obj.DICT)) {
			Dict dict;
			dict = (Dict)kv_obj;
			for (Symbol variable : variables_) {
				Obj o = dict.get(variable);
				b.push(o);
			}
		} else {
			Symbol typeSym = Obj.IDToSym(kv_obj.type());
			Obj builtin_dict = b.getContext().getVars().getGlobals().get(typeSym);
			if (builtin_dict.isa(Obj.DICT)) {
				Dict dict = (Dict)builtin_dict;
				try {
					for (Symbol variable : variables_) {
						Obj o = dict.get(variable);
						//b.push(kv_obj);
						b.push(o);
					}
				} catch (IndexError e) {
					throw new IndexError("Built in type " + typeSym + 
							" does not contain member '" + varName() + "'");
				}
			} else {
				throw new RuntimeException("Built in type " + typeSym + " was redefined to " + builtin_dict);
			}
			
		}
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("." + varName_ + ".`");
		return stream;
	}
}
