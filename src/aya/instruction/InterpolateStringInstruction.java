package aya.instruction;

import aya.Aya;
import aya.ReprStream;
import aya.exceptions.runtime.UndefVarException;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.Obj;
import aya.obj.block.BlockEvaluator;
import aya.obj.list.List;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public class InterpolateStringInstruction extends Instruction  {
	String orig; // For printing
	InstructionStack instructions;
	
	public InterpolateStringInstruction(SourceStringRef source, String orig, InstructionStack is) {
		super(source);
		this.orig = orig;
		instructions = is;
	}
	
	public InterpolateStringInstruction duplicate() {
		return new InterpolateStringInstruction(getSource(), orig, instructions);
	}
	
	public String evalString() {
		InstructionStack is = this.instructions.duplicate();
		StringBuilder sb = new StringBuilder();
		
		while(!is.isEmpty()) {
			Instruction current = is.pop();
			
			if (current instanceof GetVariableInstruction) {
				GetVariableInstruction var = (GetVariableInstruction)current;
				try {
					sb.append(Aya.getInstance().getVars().getVar(var.getSymbol()).str());
				} catch (UndefVarException e) {
					e.setSource(var.getSource());
					throw e;
				}
			} else if (current instanceof DataInstruction) {
				Obj data = ((DataInstruction)current).getData();
				if (data.isa(Obj.BLOCK)) {
					BlockEvaluator b = new BlockEvaluator();
					b.dump(Casting.asStaticBlock(data));
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
	public void execute(BlockEvaluator b) {
		b.push(List.fromString(evalString()));
		
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("\"" + orig + "\"");
		return stream;
	}
}
