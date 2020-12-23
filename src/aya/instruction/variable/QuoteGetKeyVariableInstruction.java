package aya.instruction.variable;

import aya.Aya;
import aya.ReprStream;
import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.AyaKeyError;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolEncoder;

public class QuoteGetKeyVariableInstruction extends VariableInstruction {

	public QuoteGetKeyVariableInstruction(long id) {
		this.variable_ = id;
	}
	
	@Override
	public void execute(Block b) {
		final Obj kv_obj = b.pop();
		if (kv_obj.isa(Obj.DICT)) {
			Dict dict;
			dict = (Dict)kv_obj;
			Obj o = dict.get(variable_);
			// If user object function, leave it as the first item on the stack
			//if (dict.hasMetaTable() && o.isa(Obj.BLOCK)) {
			//	b.push(dict);
			//}
			b.push(o);
		} else {
			Symbol typeSym = Obj.IDToSym(kv_obj.type());
			Obj builtin_dict = Aya.getInstance().getVars().getGlobals().getObj(typeSym.id());
			if (builtin_dict.isa(Obj.DICT)) {
				Dict dict = (Dict)builtin_dict;
				Obj o;
				try {
					o = dict.get(variable_);
					//b.push(kv_obj);
					b.push(o);
				} catch (AyaKeyError e) {
					throw new AyaRuntimeException("Built in type " + typeSym + 
							" does not contain member '" + varName() + "'");
				}
			} else {
				throw new AyaRuntimeException("Built in type " + typeSym + " was redefined to " + builtin_dict);
			}
			
		}
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("." + SymbolEncoder.decodeLong(variable_) + ".`");
		return stream;
	}
}
