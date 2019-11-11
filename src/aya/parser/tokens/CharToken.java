package aya.parser.tokens;

import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.obj.character.Char;

public class CharToken extends StdToken {
		
	public CharToken(String data) {
		super(data, Token.CHAR);
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
