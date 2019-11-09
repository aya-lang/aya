package aya.parser.tokens;

import aya.instruction.flag.TickFlagInstruction;

public class TickToken extends StdToken {
		
	public TickToken(int ticks) {
		super(""+ticks, Token.TICK);
	}

	
	@Override
	public Object getAyaObj() {
		int ticks = Integer.parseInt(data);
		if (ticks < TickFlagInstruction.TICK_TABLE.length) {
			return TickFlagInstruction.TICK_TABLE[ticks];
		} else {
			return new TickFlagInstruction(ticks);
		}
	}

	@Override
	public String typeString() {
		return "tick";
	}
}
