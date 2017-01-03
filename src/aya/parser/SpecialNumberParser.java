package aya.parser;

import java.math.BigInteger;

import org.apfloat.Apfloat;

import aya.exceptions.SyntaxError;
import aya.obj.number.BigNum;
import aya.obj.number.Num;
import aya.obj.number.Number;
import aya.obj.number.RationalNum;

/*
 * 
exp (ends on non-digit)
:3e4       # 3*10^4
:-1.2e-4   # -1.2*10^-4
:7e        # 1,000,000

imaginary (ends on non-digit)
:-4i6
:1i
:2.5j3

rational (ends on non-digit)
:5r-4     5/-4
:3r       3/1
:1.75r    7/4
:1.0r4.0  1/4

negative (ends on non-digit)
:-1.5
:-3

hex (ends on non-hex)
:0x6h7a

binary (ends on non-1/0)
:0b101101

big (ends on z)
:123752.882z
:-92z

 */

public class SpecialNumberParser {
	String _fst;
	String _snd;
	char _sep;
	
	private final char NEG = '\0';
	private final char HEX = 'x';
	private final char BIN = 'b';
	private final char IMAG = 'i';
	private final char RAT = 'r';
	private final char BIG = 'z';
	
	public SpecialNumberParser(String s) {
		_sep = NEG;
		int sepindex;
		
		//Find the sepIndex
		for (sepindex = 0; sepindex < s.length(); sepindex++) {
			if (isSepChar(s.charAt(sepindex))) {
				_sep = s.charAt(sepindex);
				break;
			}
		}
		
		_fst = s.substring(0, sepindex);
		
		if (sepindex != s.length()) {
			_snd = s.substring(sepindex+1, s.length());
		} else {
			_snd = "";
		}
	}
	
	public boolean isSepValid(SpecialNumberParser np) {
		char c = np._sep;
		return c == NEG || c == HEX || c == BIN || c == IMAG
				|| c == RAT || c == BIG;
	}
	
	public String toString() {
		return "{" + _fst + ", " + _sep + ", " + _snd + "}";
	}
	
	private boolean isSepChar(char c) {
		return c >= 'a' && c <= 'z';
	}
	
	public Number toNumber() {
		try {
			switch (_sep) {
			case NEG:
				return toNegativeNumber();
			case HEX:
				return toHexNumber();
			case BIN:
				return toBinNumber();
			case IMAG:
				return toImagNumber();
			case RAT:
				return toRationalNumber();
			case BIG:
				return toBigNumber();
			default:
				throw new SyntaxError("Invalid special number: ':" + _fst + _sep + _snd + "'");
			}
		} catch (NumberFormatException nfe) {
			throw new SyntaxError("Invalid special number: ':" + _fst + _sep + _snd + "'");
		}
	}

	private BigNum toBigNumber() {
		return new BigNum(new Apfloat(_fst));
	}

	private RationalNum toRationalNumber() {
		if (_snd.equals("")) {
			return new RationalNum(Long.parseLong(_fst),1);
		} else {
			long n = Long.parseLong(_fst);
			long d = Long.parseLong(_snd);
			return new RationalNum(n, d);
		}
	}

	private Number toImagNumber() {
		throw new SyntaxError("Imag numbers not implemented");
	}

	private Number toBinNumber() {
		try {
			return new Num(Integer.parseInt(_snd, 2 ));
		} catch (NumberFormatException e) {
			BigInteger bigint = new BigInteger(_snd, 2);
			return new BigNum(new Apfloat(bigint));
		}
	}

	private Number toHexNumber() {
		try {
			return new Num(Integer.parseInt(_snd, 16 ));
		} catch (NumberFormatException e) {
			BigInteger bigint = new BigInteger(_snd, 16);
			return new BigNum(new Apfloat(bigint));
		}
	}

	private Num toNegativeNumber() {
		return new Num(Double.parseDouble(_fst));
	}
}

