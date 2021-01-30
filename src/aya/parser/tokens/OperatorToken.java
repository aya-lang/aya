package aya.parser.tokens;

import aya.exceptions.ex.ParserException;
import aya.exceptions.ex.SyntaxError;
import aya.instruction.Instruction;
import aya.instruction.op.ColonOps;
import aya.instruction.op.DotOps;
import aya.instruction.op.MiscOps;
import aya.instruction.op.OpInstruction;
import aya.instruction.op.Ops;

public class OperatorToken extends StdToken {
	
	public static final int STD_OP = 0;
	public static final int DOT_OP = 1;
	public static final int MATH_OP = 2;
	public static final int COLON_OP = 3;
	
	private int op_type;
	
	public OperatorToken(String data, int op_type) {
		super(data, Token.OP);
		this.op_type = op_type;
	}

	
	public int getOpType() {
		return this.op_type;
	}
	
	@Override
	public Instruction getInstruction() throws ParserException {
		OpInstruction op = null;
		switch (op_type) {
		case STD_OP:
			op = Ops.getOp(data.charAt(0));
			break;
		case DOT_OP:
			op = DotOps.getOp(data.charAt(0));
			break;
		case MATH_OP:
			op = MiscOps.getOp(data.charAt(0));
			break;
		case COLON_OP:
			op = ColonOps.getOp(data.charAt(0));
		}
		if (op == null) {
			throw new SyntaxError("Operator '" + getOpTypeLetter() + data.charAt(0) + "' does not exist");
		}
		return op;
	}
	
	public String getOpTypeLetter() {
		switch (op_type) {
		case DOT_OP:
			return ".";
		case MATH_OP:
			return "M";
		case COLON_OP:
			return ":";
		default:
			return "";
		}
	}

	@Override
	public String typeString() {
		return "num";
	}
}
