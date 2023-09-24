package aya.parser.tokens;

import aya.exceptions.ex.ParserException;
import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.obj.number.Num;
import aya.parser.SourceStringRef;
import aya.parser.SpecialNumberParser;

public class NumberToken extends StdToken {
	private boolean isSpecNum = false;
	
	public NumberToken(String data, SourceStringRef source) {
		super(data, Token.NUMERIC, source);
	}

	public NumberToken(String data, boolean b, SourceStringRef source) {
		super(data, Token.NUMERIC, source);
		isSpecNum = true;
	}

	@Override
	public Instruction getInstruction() throws ParserException {
		if (isSpecNum) {
			return new DataInstruction(new SpecialNumberParser(data, this.getSourceStringRef()).toNumber());
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
			try {
				Obj o = (new SpecialNumberParser(data, this.getSourceStringRef())).toNumber();
				if (o instanceof aya.obj.number.Number) {
					return (aya.obj.number.Number)o;
				} else {
					throw new NumberFormatException();
				}
			} catch (ParserException e) {
				// Not a valid numeric
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
