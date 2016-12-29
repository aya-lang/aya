package element.infix;

import java.util.ArrayList;

import element.entities.Flag;
import element.entities.InstructionStack;
import element.exceptions.SyntaxError;
import element.obj.block.Block;
import element.variable.Variable;
import element.variable.VariableSet;

public class InfixBlock extends InfixLiteral {

	private VariableSet variables;
	private InfixExpression block;
	
	public InfixBlock(InfixTuple tuple) {
		super(tuple.toString(), InfixLiteral.BLOCK);
		
		if (tuple.items.size() == 1) {
			block = tuple.items.get(0);
		} else if(tuple.items.size() == 2) {
			ArrayList<InfixItem> var_items = tuple.items.get(0).getItems();
			ArrayList<Variable> vars = new ArrayList<Variable>();
			for (InfixItem ifx : var_items) {
				vars.add(new Variable(ifx.toVariable().getVarName()));
			}
			variables = new VariableSet(vars.toArray(new Variable[vars.size()]), null);
			block = tuple.items.get(1);
		} else {
			throw new SyntaxError("Cannot create expression from " + tuple);
		}
		
		
	}

	@Override
	public Object generateElementCode() {
		Block b = new Block();
		
		if(variables != null) {
			b.add(Flag.getFlag(Flag.POPVAR));
		}

		Object o = block.generateElementCode();
		
		if(o instanceof InstructionStack) {
			b.addAll(((InstructionStack)o).getInstrucionList());
		}
		
		if(variables != null) {
			b.add(variables);
		}
		
		return b;
	}

	@Override
	public String typeString() {
		if (variables == null) {
			return "{b:"+ block.toString() + "}";
		}
		return "{b:" + variables.toString() + ", " + block.toString() + "}";
	}

	@Override
	public void organize() {
		block.organize();
	}

	@Override
	public void desugar() {
		block.desugar();
	}

}
