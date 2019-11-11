package aya.instruction;

import java.util.LinkedList;

import aya.Aya;
import aya.entities.InstructionStack;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.Str;

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
			Instruction current = is.pop();
			
			if (current instanceof GetVariableInstruction) {
				GetVariableInstruction var = (GetVariableInstruction)current;
				sb.append(Aya.getInstance().getVars().getVar(var.getID()).str());
			} else if (current instanceof DataInstruction) {
				Obj data = ((DataInstruction)current).getData();
				if (data.isa(Obj.BLOCK)) {
					Block b = ((Block)data).duplicate();
					b.eval();
					if (b.getStack().size() == 1) {
						sb.append(b.getStack().pop().str());
					} else {
						sb.append("[ " + b.getPrintOutputState() + "]");
					}
				} else if (data.isa(Obj.STR)) {
					sb.append(data.str());
				} else {
					badItemInString(current);
				}
			} else {
				badItemInString(current);
			}
		}
		
		return sb.toString();
	}
	
	private void badItemInString(Instruction item) {
		throw new RuntimeException("Invalid object in InterpolteString \"" + this.toString() + "\": " + item.toString());
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
