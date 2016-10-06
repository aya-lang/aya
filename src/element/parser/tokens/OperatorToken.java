package element.parser.tokens;

import element.entities.Operation;
import element.entities.operations.DotOps;
import element.entities.operations.MathOps;
import element.entities.operations.Ops;
import element.exceptions.SyntaxError;

public class OperatorToken extends StdToken {
	
	public static final int STD = 0;
	public static final int DOT = 1;
	public static final int MATH = 2;
	
	private int op_type;
	
	public OperatorToken(String data, int op_type) {
		super(data, Token.OP);
		this.op_type = op_type;
	}

	
	public int getOpType() {
		return this.op_type;
	}
	
	@Override
	public Object getElementObj() {
		Operation op = null;
		switch (op_type) {
		case STD:
			op = Ops.getOp(data.charAt(0));
			break;
		case DOT:
			op = DotOps.getOp(data.charAt(0));
			break;
		case MATH:
			op = MathOps.getOp(data.charAt(0));
			break;
		}
		if (op == null) {
			throw new SyntaxError("Operator '" + getOpTypeLetter() + data.charAt(0) + "' does not exist");
		}
		return op;
	}
	
	public String getOpTypeLetter() {
		switch (op_type) {
		case DOT:
			return ".";
		case MATH:
			return "M";
		default:
			return "";
		}
	}

	@Override
	public String typeString() {
		return "num";
	}
}
