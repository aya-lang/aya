package aya.parser.tokens;

import aya.exceptions.parser.ParserException;
import aya.instruction.Instruction;
import aya.instruction.named.NamedOperatorInstruction;
import aya.parser.SourceStringRef;

public class NamedOpToken extends StdToken {

	public NamedOpToken(String data, SourceStringRef source) {
		super(data, Token.NAMED_OP, source);
	}

	@Override
	public Instruction getInstruction() throws ParserException {
		return new NamedOperatorInstruction(this.getSourceStringRef(), data);
	}

	@Override
	public String typeString() {
		return "named_op";
	}
}
