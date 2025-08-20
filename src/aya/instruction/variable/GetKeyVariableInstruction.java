package aya.instruction.variable;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.IndexError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public class GetKeyVariableInstruction extends GetVariableInstruction {

	public GetKeyVariableInstruction(SourceStringRef source, Symbol var) {
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

				if (o.isa(Obj.BLOCK)) {
					// If user object function, leave it as the first item on the stack
					if (dict.pushSelf()) b.push(dict);
					dumpBlock(Casting.asStaticBlock(o), b);
				} else {
					b.push(o);
				}
			}
		} else {
			Dict builtin_dict = b.getContext().getVars().getBuiltinMeta(kv_obj);
			try {
				for (Symbol variable : variables_) {
					Obj o = builtin_dict.get(variable);
					if (variable != SymbolConstants.KEYVAR_META) b.push(kv_obj); // Don't push if we are accessing the meta dict
					this.addOrDumpVar(o, b);
				}
			} catch (IndexError e) {
				throw new IndexError("Built in type " + Obj.IDToSym(kv_obj.type()) + 
						" does not contain member '" + varName() + "'");
			}
			
		}
	}
	
	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("." + varName_);
		return stream;
	}
	
	
}
