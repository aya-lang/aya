package element.entities;

import java.util.ArrayList;

import element.obj.Obj;
import element.variable.MemberVariable;
import element.variable.Module;

public class UserObject extends Obj {
	private static MemberVariable MV_STR = new MemberVariable("show");
	
	private Module module;
	private ArrayList<Obj> fields;
	
	public UserObject(Module module, ArrayList<Obj> fields) {
		this.module = module;
		this.fields = fields;
	}
	
	/** Get the field at index `index` from the fields list */
	public Object getField(int index) {
		return fields.get(index);
	}
	
	/** Set the field ad index `index` to o */
	public void setField(int index, Obj o) {
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
	public void callVariable(Block block, MemberVariable memVar, Obj... push_first) {
		//Push self
		block.push(this);
		
		//push others
		for (Obj o : push_first) {
			block.push(o);
		}
		
		Obj obj = module.get(memVar);
		
		if(obj.isa(Obj.BLOCK)) {
			Block blk = ((Block)obj).duplicate();
			block.getInstructions().addAll(blk.getInstructions().getInstrucionList());
		} else {
			block.push(obj);
		}
	}
	
	/** Convert this object to a string using elements .str call */
	public String str() {
		if(module.hasVar(MV_STR)) {
			Obj obj_str = module.get(MV_STR);
			if(obj_str.isa(Obj.BLOCK)) {
				Block blk_show = ((Block)obj_str).duplicate();
				blk_show.push(this);
				blk_show.eval();
				Obj obj_res = blk_show.pop();
				return obj_res.str();
			} else {
				return obj_str.str();
			}
		} else {
			return "<" + module.toString() + " instance>";
		}
	}
	
	public String toString() {
		return this.str();
	}

	@Override
	public Obj deepcopy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean bool() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String repr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equiv(Obj o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isa(byte type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte type() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
