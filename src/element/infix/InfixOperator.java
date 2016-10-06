package element.infix;

import element.entities.InstructionStack;

/** A wrapper for the operation class that includes the operators
 * precedence so that it can be parsed using infix notation.
 * @author Nick
 *
 */
public class InfixOperator extends InfixItem {
	
	public static final boolean LEFT_TO_RIGHT = true;
	public static final boolean RIGHT_TO_LEFT = false;
	//public static final int EQUALS = 1;
	
	/** The operator's precedence */
	private int precedence;
	/** The operator's corresponding operation */
	private InstructionStack operation;
	private boolean associativity; //Use constants LEFT_TO_RIGHT and RIGHT_TO_LEFT
	private String name;
	private int flag;
	
	/**
	 * 
	 * @param name The operator as it appears in the source
	 * @param operation The equivalent element code
	 * @param precedence The operators precedence
	 * @param associativity Use constants LEFT_TO_RIGHT and RIGHT_TO_LEFT
	 */
	public InfixOperator(String name, InstructionStack operation, int precedence, boolean associativity) {
		super(InfixItem.OP);
		this.operation = operation;
		this.precedence = precedence;
		this.name = name;
		this.associativity = associativity;
	}
	
	/** Return the operators equivelent element code */
	public InstructionStack getElementCode() {
		return operation;
	}
	
	/** Associativity shorthand */
	public boolean isLeftToRight() {
		//the constant LEFT_TO_RIGHT = true
		return associativity;
	}
	
	/** Returns the operators precedence */
	public int getPrecedence() {
		return precedence;
	}
	
	/** returns the operators name as it appears in the source */
	public String getName() {
		return this.name;
	}
	
	/** Returns the operators flag id */
	public int getFlag() {
		return flag;
	}
	
	/** Set the flag id (use InfixOperator constants only */
	public void setFlag(int f) {
		flag = f;
	}
	
	
	/** Shorthand for getPrecedence (please use getOp().getPrecedence() instead) */
	public int getPrimaryKey() {
		return precedence;
	}

	@Override
	public Object generateElementCode() {
		return operation;
	}

	@Override
	public String typeString() {
		return "{o:" + name + "}";
	}

	@Override
	public void organize() {
		//Do Nothing
		
	}

	@Override
	public void desugar() {
		//Do Nothing
		
	}

	
	
}
