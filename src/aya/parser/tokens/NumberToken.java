package aya.parser.tokens;

import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.obj.number.Num;
import aya.parser.SpecialNumberParser;

public class NumberToken extends StdToken {
	private boolean isSpecNum = false;
	
	public NumberToken(String data) {
		super(data, Token.NUMERIC);
	}

	public NumberToken(String data, boolean b) {
		super(data, Token.NUMERIC);
		isSpecNum = true;
	}

	@Override
	public Instruction getInstruction() {
		if (isSpecNum) {
			return new DataInstruction(new SpecialNumberParser(data).toNumber());
		} else {
			try {
				int i = Integer.parseInt(data);
				return new DataInstruction(Num.fromInt(i));
			} catch (NumberFormatException e) {
				return new DataInstruction(new Num(Double.parseDouble(data)));
			}
		}
	}
	
	/** Return the numeric value of this token.
	 * Throw a {@link NumberFormatException} if the token is a special number that is non-numeric
	 * @return
	 */
	public aya.obj.number.Number numValue() {
		if (isSpecNum) {
			Obj o = (new SpecialNumberParser(data)).toNumber();
			if (o instanceof aya.obj.number.Number) {
				return (aya.obj.number.Number)o;
			} else {
				throw new NumberFormatException();
			}
		}
		else {
			try {
				int i = Integer.parseInt(data);
				return Num.fromInt(i);
			} catch (NumberFormatException e) {
				return new Num(Double.parseDouble(data));
			}
		}
	}

	@Override
	public String typeString() {
		return "num";
	}
}
