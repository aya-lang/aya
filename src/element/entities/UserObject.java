package element.entities;

import java.util.ArrayList;

import element.ElemTypes;
import element.variable.MemberVariable;
import element.variable.Module;

public class UserObject {
	private static MemberVariable MV_STR = new MemberVariable("show");
	
	private Module module;
	private ArrayList<Object> fields;
	
	public UserObject(Module module, ArrayList<Object> fields) {
		this.module = module;
		this.fields = fields;
	}
	
	/** Get the field at index `index` from the fields list */
	public Object getField(int index) {
		return fields.get(index);
	}
	
	/** Set the field ad index `index` to o */
	public void setField(int index, Object o) {
		fields.set(index, o);
	}
	
	/** Return the module for this user object */
	public Module getModule() {
		return module;
	}
	
	/** The number of fields this object has */
	public int fieldCount() {
		return fields.size();
	}
	
	/** The name of the module */
	public String name() {
		return module.name();
	}
	
	/** Calls the variable and dumps the result to the stack existing in the input block */
	public void callVariable(Block block, MemberVariable memVar, Object... push_first) {
		//Push self
		block.push(this);
		
		//push others
		for (Object o : push_first) {
			block.push(o);
		}
		
		Object obj = module.get(memVar);
		
		if(ElemTypes.isBlock(obj)) {
			Block blk = ((Block)obj).duplicate();
			block.getInstructions().addAll(blk.getInstructions().getInstrucionList());
		} else {
			block.push(obj);
		}
	}
	
	/** Convert this object to a string using elements .str call */
	public String str() {
		if(module.hasVar(MV_STR)) {
			Object obj_str = module.get(MV_STR);
			if(ElemTypes.isBlock(obj_str)) {
				Block blk_show = ((Block)obj_str).duplicate();
				blk_show.push(this);
				blk_show.eval();
				Object obj_res = blk_show.pop();
				return ElemTypes.castString(obj_res);
			} else {
				return ElemTypes.castString(obj_str);
			}
		} else {
			return module.toString() + ".show unimplemented";
		}
	}
	
	public String toString() {
		return this.str();
	}
	
	
}
