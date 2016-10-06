package element.entities;

import static element.ElemTypes.castString;
import static element.ElemTypes.isBlock;
import static element.ElemTypes.isString;
import static element.ElemTypes.isVar;
import static element.ElemTypes.toBlock;
import static element.ElemTypes.toVar;

import element.Element;
import element.variable.Variable;

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
			
			if (isString(current)) {
				sb.append(castString(current));
			} else if (isVar(current)) {
				sb.append(castString(Element.getInstance().getVars().getVar(toVar(current))));
			} else if (isBlock(current)) {
				Block b = toBlock(current).duplicate();
				b.eval();
				if (b.getStack().size() == 1) {
					sb.append(castString(b.getStack().pop()));
				} else {
					sb.append("[ " + b.getPrintOutputState() + "]");
				}
			} else {
				throw new RuntimeException("Invalid object in InterpolteString \"" + this.toString() + "\": " + castString(current));
			}
		}
		
		return sb.toString();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Object o : instructions.instructions) {
			if (isString(o)) {
				sb.append(castString(o));
			} else if (isVar(o)) {
				sb.append("$").append(Variable.decodeLong(toVar(o).getID()));
			} else if (isBlock(o)) {
				sb.append("$(").append(toBlock(o).toString()).append(")"); 
			} else {
				throw new RuntimeException("Invalid object in InterpolteString \"" + this.toString() + "\": " + castString(o));
			}
		}
		return "\""+sb.toString()+"\"";
	}
}
