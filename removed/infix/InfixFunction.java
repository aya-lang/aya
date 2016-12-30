package aya.infix;

import aya.entities.InstructionStack;

public class InfixFunction extends InfixItem {
	private String name;
	private int numOfArgs;
	InstructionStack elementCode;
	InfixTuple args;
	
	/** Sets the function arguments to the InfixTuple */
	public void setArgs(InfixTuple args) {
		this.args = args;
	}
	
	/** Returns the functions name as it appears in the source */
	public String getName() {
		return name;
	}

	/** Returns the number of arguments needed for the function */
	public int getNumOfArgs() {
		return numOfArgs;
	}
	
	/** Returns the function's args */
	public InfixTuple getArgs() {
		return this.args;
	}

	/**
	 * 
	 * @param name The function's name as it appears in the source
	 * @param numOfArgs The number of functions the argument needs
	 * @param elementCode The equivalent element code (override this method for more complicated functions)
	 */
	public InfixFunction(String name, int numOfArgs, InstructionStack elementCode) {
		super(InfixItem.FUNCTION);
		this.name = name;
		this.numOfArgs = numOfArgs;
		this.elementCode = elementCode;
	}
	
//	/** Copy consturctor **/
//	public InfixFunction(InfixFunction other) {
//		super(InfixItem.FUNCTION);
//		this.name = other.name;
//		this.numOfArgs = other.numOfArgs;
//		if(other.elementCode == null) {
//			this.elementCode = null;
//		} else {
//			this.elementCode = other.elementCode.duplicate();
//		}
//	}
	
	public InfixFunction duplicate() {
		return new InfixFunction(this.name, this.numOfArgs, this.copyElementCode());
	}
	
	public InstructionStack copyElementCode() {
		if(this.elementCode == null) {
			return null;
		} else {
			return this.elementCode.duplicate();
		}
	}
	
	@Override
	public Object generateElementCode() {
		InstructionStack is = new InstructionStack();
		is.addISorOBJ(elementCode);
		is.addISorOBJ(args.generateElementCode());
		return is;
	}

	@Override
	public String typeString() {
		if (args != null) {
			return "{f:" + name + "(" + args.toString() + ")}";
		} else {
			return "{f:" + name + "()}";

		}
	}

	@Override
	public void organize() {
		args.organize();
	}

	@Override
	public void desugar() {
		args.desugar();	
	}
	
	@Override
	public Object clone(){  
	    try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
