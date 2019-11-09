package aya.entities;

import aya.Aya;
import aya.obj.block.Block;
import aya.obj.list.Str;
import aya.variable.Variable;

public class InterpolateString  {
	String orig; // For printing
	InstructionStack instructions;
	
	public InterpolateString(String orig, InstructionStack is) {
		this.orig = orig;
		instructions = is;
	}
	
	public InterpolateString duplicate() {
		InterpolateString is = new InterpolateString(this.orig, this.instructions);
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
		return "\"" + orig + "\"";
	}
}
