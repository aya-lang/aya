package aya.instruction;

import java.util.LinkedList;

import aya.Aya;
import aya.entities.InstructionStack;
import aya.obj.block.Block;
import aya.obj.list.Str;
import aya.variable.Variable;

public class InterpolateStringInstruction extends Instruction  {
	String orig; // For printing
	InstructionStack instructions;
	
	public InterpolateStringInstruction(String orig, InstructionStack is) {
		this.orig = orig;
		instructions = is;
	}
	
	public InterpolateStringInstruction duplicate() {
		return new InterpolateStringInstruction(orig, instructions);
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

	@Override
	public void execute(Block b) {
		b.push(new Str(evalString()));
		
	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		return "\"" + orig + "\"";
	}
}
