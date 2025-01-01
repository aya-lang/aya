package aya.parser.tokens;

import aya.exceptions.parser.ParserException;
import aya.instruction.Instruction;
import aya.instruction.StringLiteralInstruction;
import aya.parser.SourceStringRef;

public class CurrentFileToken extends StdToken {

	public CurrentFileToken(SourceStringRef source) {
		super(source.getSource().getFilename(), Token.STRING, source);
	}

	@Override
	public Instruction getInstruction() throws ParserException {
		return new StringLiteralInstruction(source, this.source.getSource().getFilename());
	}

	@Override
	public String typeString() {
		return "constant_current_file";
	}
}
