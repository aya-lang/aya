package aya.parser.tokens;

import aya.exceptions.parser.ParserException;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.Instruction;
import aya.instruction.op.ColonOps;
import aya.instruction.op.DotOps;
import aya.instruction.op.MiscOps;
import aya.instruction.op.OperatorInstruction;
import aya.instruction.op.Ops;
import aya.parser.SourceStringRef;

public class OperatorToken extends StdToken {
	
	public static final int STD_OP = 0;
	public static final int DOT_OP = 1;
	public static final int MATH_OP = 2;
	public static final int COLON_OP = 3;
	
	private int op_type;
	
	public OperatorToken(String data, int op_type, SourceStringRef source) {
		super(data, Token.OP, source);
		this.op_type = op_type;
	}

	
	public int getOpType() {
		return this.op_type;
	}
	
	@Override
	public Instruction getInstruction() throws ParserException {
		OperatorInstruction op = null;
		switch (op_type) {
		case STD_OP:
			op = Ops.getOp(data.charAt(0), this.getSourceStringRef());
			break;
		case DOT_OP:
			op = DotOps.getOp(data.charAt(0), this.getSourceStringRef());
			break;
		case MATH_OP:
			op = MiscOps.getOp(data.charAt(0), this.getSourceStringRef());
			break;
		case COLON_OP:
			op = ColonOps.getOp(data.charAt(0), this.getSourceStringRef());
		}
		if (op == null) {
			throw new SyntaxError("Operator '" + getOpTypeLetter() + data.charAt(0) + "' does not exist", this.getSourceStringRef());
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
