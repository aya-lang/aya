package aya.infix;

import java.util.ArrayList;

import aya.entities.*;

public class InfixExpression extends InfixItem {

	private ArrayList<InfixItem> items;
	
	public InfixExpression() {
		super(InfixItem.EXPRESSION);
		items = new ArrayList<InfixItem>();
	}
	
	public InfixExpression(InfixItem ifx) {
		super(InfixItem.EXPRESSION);
		items = new ArrayList<InfixItem>();
		items.add(ifx);
	}
	
	public InfixExpression(ArrayList<InfixItem> ifxs) {
		super(InfixItem.EXPRESSION);
		items = ifxs;
	}
	
	@SuppressWarnings("unchecked")
	public InfixExpression(InfixExpression other) {
		super(InfixItem.EXPRESSION);
		this.items = (ArrayList<InfixItem>)other.items.clone();
	}
	
	public ArrayList<InfixItem> getItems() {
		return items;
	}
	
	public void add(InfixItem item) {
		items.add(item);
	}
	
	@Override
	public Object generateElementCode() {
		InstructionStack is = new InstructionStack();
		for (InfixItem t : items) {
			is.addISorOBJ(t.generateElementCode());
		}
		return is;
	}

	@Override
	public String typeString() {
		StringBuilder sb = new StringBuilder();
		for (InfixItem t : items) {
			sb.append(t.typeString() + " ");
		}
		String out = sb.toString().substring(0, sb.length()-1);
		return "{e:" + out + "}";
	}

	@Override
	public void organize() {
		this.items = Compiler.organize(items);
		
	}

	@Override
	public void desugar() {
		items = Compiler.desugar(new InfixItemList(items)).getArrayList();
	}

	
	public void assemble() {
		items = Compiler.assemble(new InfixItemList(items)).getArrayList();
	}
}
