package aya.entities;

import aya.Aya;
import aya.obj.block.Block;
import aya.obj.list.Str;
import aya.variable.Variable;

public class InterpolateString  {
	InstructionStack instructions;
	
	public InterpolateString(InstructionStack is) {
		instructions = is;
	}
	
	public InterpolateString duplicate() {
		InterpolateString is = new InterpolateString(this.instructions);
		return is;
	}
	
	public String evalString() {
		InstructionStack is = this.instructions.duplicate();
		StringBuilder sb = new StringBuilder();
		
		while(!is.isEmpty()) {
			Object current = is.pop();
			
			if (current instanceof Str) {
				sb.append(((Str)current).str());
			} else if (current instanceof Variable) {
				sb.append(Aya.getInstance().getVars().getVar((Variable)current).str());
			} else if (current instanceof Block) {
				Block b = ((Block)current).duplicate();
				b.eval();
				if (b.getStack().size() == 1) {
					sb.append(b.getStack().pop().str());
				} else {
					sb.append("[ " + b.getPrintOutputState() + "]");
				}
			} else {
				throw new RuntimeException("Invalid object in InterpolteString \"" + this.toString() + "\": " + current.toString());
			}
		}
		
		return sb.toString();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Object o : instructions.instructions) {
			if (o instanceof Str) {
				sb.append(((Str)o).str());
			} else if (o instanceof Variable) {
				sb.append("$").append(Variable.decodeLong(((Variable)o).getID()));
			} else if (o instanceof Block) {
				sb.append("$(").append(((Block)o).str()).append(")"); 
			} else {
				throw new RuntimeException("Invalid object in string: " + o.toString());
			}
		}
		return "\""+sb.toString()+"\"";
	}
}
