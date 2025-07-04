package aya.parser.tokens;

import aya.exceptions.parser.ParserException;
import aya.instruction.Instruction;
import aya.instruction.flag.TieFlagInstruction;
import aya.parser.SourceStringRef;

public class TieToken extends Token {
	
	public TieToken(SourceStringRef source) {
		super(Token.TIE, source);
	}

	@Override
	public Instruction getInstruction() throws ParserException {
		//throw new ParserException("TieToken can not be used as an instruction directly", source);
		return new TieFlagInstruction(source);
	}

	@Override
	public String typeString() {
		return "..";
	}

}
