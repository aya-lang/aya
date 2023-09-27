package aya.parser.tokens;

import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.obj.character.Char;
import aya.parser.SourceStringRef;

public class CharToken extends StdToken {
		
	public CharToken(String data, SourceStringRef source) {
		super(data, Token.CHAR, source);
	}

	
	@Override
	public Instruction getInstruction() {
		return new DataInstruction(Char.valueOf(data.charAt(0)));
	}

	@Override
	public String typeString() {
		return "char";
	}
}
