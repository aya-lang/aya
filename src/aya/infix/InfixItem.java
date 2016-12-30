package aya.infix;

public abstract class InfixItem {
	
	//Infix Item Types
	public static final int LITERAL = 0;
	public static final int OP = 1;
	public static final int TUPLE = 2;
	public static final int FUNCTION = 3;
	public static final int VARIABLE = 4;
	public static final int EXPRESSION = 5;
	public static final int SPECIAL = 6;
	public static final int BLOCK = 7;
	
	/** The type of the infix item */
	private int type  = -1;
	
	/** The subtype of the item (LiteralType, etc) */
	protected int subtype = -1;

	/** Type constructor */
	public InfixItem(int t) {
		this.type = t;
	}
	
	//Getters
	public int getType() {return type;}
	public int getPrimaryKey() {return subtype;}
	

	/** Returns the equivelent element code for the item */
	public abstract Object generateElementCode();
	
	/** Returns a debug string in the format "{[type][subtype]:[item]}" */
	public abstract String typeString();
	
	/** Organizes the code using the Compiler.organize method */
	public abstract void organize();
	
	/** Desugars the code using the Compiler.desugar method */
	public abstract void desugar();
	
	//Quick Type Checking
	public boolean isLiteral() {return this.type == LITERAL;}
	public boolean isOperator() {return this.type == OP;}
	public boolean isTuple() {return this.type == TUPLE;}
	public boolean isFunction() {return this.type == FUNCTION;}
	public boolean isVariable() {return this.type == VARIABLE;}
	public boolean isExpression() {return this.type == EXPRESSION;}
	public boolean isSpecial() {return this.type == SPECIAL;}

	//Quick Type Casting
	/** Casts the item to a tuple */
	public InfixTuple toTuple() {
		if (isTuple()) {
			return (InfixTuple)this;
		} else {
			throw new RuntimeException("Cannot cast from type " + typeString() + " to tuple");
		}
	}

	/** Casts the item to a variable */
	public InfixVariable toVariable() {
		if (isVariable()) {
			return (InfixVariable)this;
		} else {
			throw new RuntimeException("Cannot cast from type " + typeString() + " to variable");
		}
	}
	
	/** Casts the item to an operator */
	public InfixOperator toOp() {
		if (isOperator()) {
			return (InfixOperator)this;
		} else {
			throw new RuntimeException("Cannot cast from type " + typeString() + " to operator");
		}
	}
	
	/** Casts the item to an expression */
	public InfixExpression toExpression() {
		if (isExpression()) {
			return (InfixExpression)this;
		} else {
			throw new RuntimeException("Cannot cast from type " + typeString() + " to expression");
		}
	}
	
	/** Casts the item to a function */
	public InfixFunction toFunction() {
		if (isFunction()) {
			return (InfixFunction)this;
		} else {
			throw new RuntimeException("Cannot cast from type " + typeString() + " to function");
		}
	}
	
	/** Casts the item to a special */
	public InfixSpecial toSpecial() {
		if (isSpecial()) {
			return (InfixSpecial)this;
		} else {
			throw new RuntimeException("Cannot cast from type " + typeString() + " to function");
		}
	}
	
	/** Returns the debug type string */
	public String toString() {
		return typeString();
	}
}
