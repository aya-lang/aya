package element.parser.tokens;

import element.entities.Flag;

public class TickToken extends StdToken {
		
	public TickToken(int ticks) {
		super(""+ticks, Token.TICK);
	}

	
	@Override
	public Object getElementObj() {
		int ticks = Integer.parseInt(data);
		return new Flag((byte) (-1*ticks));
	}

	@Override
	public String typeString() {
		return "tick";
	}
}
