package aya.parser.tokens;

import aya.Aya;
import aya.exceptions.AyaBuildException;
import aya.instruction.Instruction;
import aya.instruction.named.NamedInstruction;

public class NamedOpToken extends StdToken {

	public NamedOpToken(String data) {
		super(data, Token.NAMED_OP);
	}

	@Override
	public Instruction getInstruction() {
		NamedInstruction instruction = Aya.getInstance().getNamedInstruction(data);
		if (instruction != null) {
			return instruction;
		} else {
			throw new AyaBuildException("Named instruction :{" + data + "} does not exist");
		}
	}

	@Override
	public String typeString() {
		return "named_op";
	}
}
