package element.infix;

import java.util.ArrayList;
import element.entities.InstructionStack;


public class InfixTuple extends InfixItem {
	public ArrayList<InfixExpression> items;

	public InfixTuple() {
		super(InfixItem.TUPLE);
		items = new ArrayList<InfixExpression>();
	}
	
	public InfixTuple(InfixExpression... exps) {
		super(InfixItem.TUPLE);
		items = new ArrayList<InfixExpression>();
		for (InfixExpression e : exps) {
			items.add(e);
		}
	}
	
	public ArrayList<InfixExpression> getItems() {
		return this.items; 
	}
	
	public void add(InfixExpression exp) {
		items.add(exp);
	}
	
	@Override
	public Object generateElementCode() {
		InstructionStack is = new InstructionStack();
		for (InfixExpression e : items) {
			is.addISorOBJ(e.generateElementCode());
		}
		return is;
	}

	@Override
	public String typeString() {
		StringBuilder sb = new StringBuilder("{t:");
		for (InfixExpression e: items) {
			sb.append(e.typeString());
			sb.append(", ");
		}
		String out = sb.toString().substring(0, sb.length()-2);
		return out + "}";
	}

	@Override
	public void organize() {
		for (InfixExpression e: items) {
			e.organize();
		}
		
	}

	@Override
	public void desugar() {
		for (InfixExpression e: items) {
			e.desugar();
		}
	}
	
	
}
