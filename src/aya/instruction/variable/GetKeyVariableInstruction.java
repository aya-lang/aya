package aya.instruction.variable;

import java.util.LinkedList;

import aya.Aya;
import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.AyaKeyError;
import aya.obj.dict.Dict;
import aya.obj.symbol.SymbolEncoder;

public class GetKeyVariableInstruction extends GetVariableInstruction {

	public GetKeyVariableInstruction(long id) {
		super(id);
	}
	
	private static long META = SymbolEncoder.encodeString("__meta__");
	
	@Override
	public void execute(Block b) {
		final Obj kv_obj = b.pop();
		if (kv_obj.isa(Obj.DICT)) {
			Dict dict;
			dict = (Dict)kv_obj;
			Obj o = dict.get(variable_);
			// If user object function, leave it as the first item on the stack
			if (dict.pushSelf() && o.isa(Obj.BLOCK)) {
				b.push(dict);
			}
			this.addOrDumpVar(o, b);
		} else {
			Dict builtin_dict = Aya.getInstance().getVars().getBuiltinMeta(kv_obj);
			Dict dict = (Dict)builtin_dict;
			Obj o;
			try {
				o = dict.get(variable_);
				if (variable_ != META) b.push(kv_obj); // Don't push if we are accessing the meta dict
				this.addOrDumpVar(o, b);
			} catch (AyaKeyError e) {
				throw new AyaRuntimeException("Built in type " + Obj.IDToSym(kv_obj.type()) + 
						" does not contain member '" + varName() + "'");
			}
			
		}
	}
	
	@Override
	protected String repr(LinkedList<Long> visited) {
		return "." + SymbolEncoder.decodeLong(variable_);
	}
}
