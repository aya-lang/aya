package aya.instruction.variable;

import java.util.LinkedList;

import aya.Aya;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.UndefVarException;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.variable.Variable;

public class GetKeyVariableInstruction extends VariableInstruction {

	public GetKeyVariableInstruction(long id) {
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
			if (dict.hasMetaTable() && o.isa(Obj.BLOCK)) {
				b.push(dict);
			}
			GetVariableInstruction.addOrDumpVar(o, b);
		} else {
			Symbol typeSym = Obj.IDToSym(kv_obj.type());
			Obj builtin_dict = Aya.getInstance().getVars().getGlobals().getObject(typeSym.id());
			if (builtin_dict.isa(Obj.DICT)) {
				Dict dict = (Dict)builtin_dict;
				Obj o;
				try {
					o = dict.get(variable_);
					b.push(kv_obj);
					GetVariableInstruction.addOrDumpVar(o, b);
				} catch (UndefVarException e) {
					throw new AyaRuntimeException("Built in type " + typeSym + 
							" does not contain member '" + varName() + "'");
				}
			} else {
				throw new AyaRuntimeException("Built in type " + typeSym + " was redefined to " + builtin_dict);
			}
			
		}
	}
	
	@Override
	protected String repr(LinkedList<Long> visited) {
		return Variable.decodeLong(variable_);
	}
}