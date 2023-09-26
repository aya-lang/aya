package aya.parser.tokens;

import aya.Aya;
import aya.exceptions.ex.ParserException;
import aya.instruction.Instruction;
import aya.instruction.named.NamedOperator;
import aya.instruction.named.NamedOperatorInstruction;
import aya.parser.SourceStringRef;

public class NamedOpToken extends StdToken {

	public NamedOpToken(String data, SourceStringRef source) {
		super(data, Token.NAMED_OP, source);
	}

	@Override
	public Instruction getInstruction() throws ParserException {
		NamedOperator instruction = Aya.getInstance().getNamedInstruction(data);
		if (instruction != null) {
			return new NamedOperatorInstruction(this.getSourceStringRef(), instruction);
		} else {
			throw new ParserException("Named instruction :{" + data + "} does not exist", source);
		}
	}

	@Override
	public String typeString() {
		return "named_op";
	}
}
