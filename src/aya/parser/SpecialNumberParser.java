package aya.parser;

import java.io.File;
import java.math.BigInteger;

import aya.AyaPrefs;
import aya.exceptions.ex.SyntaxError;
import aya.obj.Obj;
import aya.obj.list.List;
import aya.obj.list.Str;
import aya.obj.number.BigNum;
import aya.obj.number.ComplexNum;
import aya.obj.number.FractionNum;
import aya.obj.number.Num;
import aya.obj.number.Number;

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
	private final char PI = 'p';
	private final char ROOT = 'q';
	private final char SCI = 'e';
	private final char CONST = 'c';
	private final char STR = 's';
	
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
				|| c == RAT || c == BIG || c == PI || c == SCI;
	}
	
	public String toString() {
		return "{" + _fst + ", " + _sep + ", " + _snd + "}";
	}
	
	private boolean isSepChar(char c) {
		return c >= 'a' && c <= 'z';
	}
	
	public Obj toNumber() throws SyntaxError {
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
				return toFractionNumber();
			case PI:
				return toPiNumber();
			case BIG:
				return toBigNumber();
			case ROOT:
				return toRootNumber();
			case SCI:
				return toSciNumber();
			case CONST:
				return toConstVal();
			case STR:
				return List.fromString(toStrVal());
			default:
				throw new SyntaxError("Invalid special number: ':" + _fst + _sep + _snd + "'");
			}
		} catch (NumberFormatException nfe) {
			throw new SyntaxError("Invalid special number: ':" + _fst + _sep + _snd + "'");
		}
	}
	
	private Number toSciNumber() {
		double fst = Double.parseDouble(_fst);
		double snd = 0.0;
		if (_snd.equals("")) {
			snd = fst;
			fst = 1.0;
		} else {
			snd = Double.parseDouble(_snd);
		}
		return new Num(fst * Math.pow(10, snd));
	}

	private Number toRootNumber() {
		if (_snd.equals("")) {
			return new Num(Math.sqrt(Double.parseDouble(_fst)));
		} else {
			double fst = Double.parseDouble(_fst);
			double snd = Double.parseDouble(_snd);
			return new Num(Math.pow(fst, 1/snd));
		}
	}

	private Number toPiNumber() {
		if (_snd.equals("")) {
			return new Num(Math.PI * Double.parseDouble(_fst));
		} else {
			double fst = Double.parseDouble(_fst);
			double snd = Double.parseDouble(_snd);
			return new Num(Math.pow(Math.PI * fst, snd));
		}
	}
	
	private BigNum toBigNumber() {
		return new BigNum(_fst);
	}

	private FractionNum toFractionNumber() {
		if (_snd.equals("")) {
			if (_fst.contains(".")) {
				return new FractionNum(Double.parseDouble(_fst));
			} else {
				return new FractionNum(Long.parseLong(_fst),1);
			}
		} else {
			BigInteger n = new BigInteger(_fst);
			BigInteger d = new BigInteger(_snd);
			return new FractionNum(n, d);
		}
	}

	private Number toImagNumber() {
		if (_snd.equals("")) {
			return new ComplexNum(Double.parseDouble(_fst));
		} else {
			return new ComplexNum(Double.parseDouble(_fst), Double.parseDouble(_snd));
		}
	}

	private Number toBinNumber() {
		try {
			return new Num(Integer.parseInt(_snd, 2 ));
		} catch (NumberFormatException e) {
			BigInteger bigint = new BigInteger(_snd, 2);
			return new BigNum(bigint);
		}
	}

	private Number toHexNumber() {
		try {
			return new Num(Integer.parseInt(_snd, 16 ));
		} catch (NumberFormatException e) {
			BigInteger bigint = new BigInteger(_snd, 16);
			return new BigNum(bigint);
		}
	}

	private Num toNegativeNumber() {
		double d = Double.parseDouble(_fst);
		if (d <= 0) {
			return new Num(d);
		} else {
			return new Num(-d);
		}
	}
	
	private Obj toConstVal() throws SyntaxError {
		int i = Integer.parseInt(_fst);
		if (i < 0 || i > AyaPrefs.CONSTS.length) {
			throw new SyntaxError(":" + _fst + 
					"c is out of range. Max val =" + AyaPrefs.CONSTS.length);
		}
		return AyaPrefs.CONSTS[i];
	}
	
	
	public static String STR_CONSTANTS_HELP = "string constants follow the format :Ns where N is:\n"
			+ "  0: \"Hello, world!\"\n"
			+ "  1/-1: A-Z / a-z\n"
			+ "  2/-2: a-zA-Z / A-Za-z\n"
			+ "  3/-3: a-zA-Z0-9 / A-Za-z0-9\n"
			+ "  4/-4: \"bcdfghjklmnpqrstvwxyz\" / \"aeiou\"\n"
			+ "  5/-5: \"bcdfghjklmnpqrstvwxz\" / \"aeiouy\"\n"
			+ "  6/-6: 0-9 / 9-0\n"
			+ "  7/-7: ascii (0-255) / printable ascii (23-128)\n";
	
	/* 0*/ public static final String S_HW = "Hello, world!";
	/* 1*/ public static final String S_AZ = "ABCDEFGHIJKLMONPQRSTUVWXYZ";
	/*-1*/ public static final String S_az = "abcdefghijklmnopqrstuvwxyz";
	/* 2*/ public static final String S_azAZ = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMONPQRSTUVWXYZ";
	/*-2*/ public static final String S_AZaz = "ABCDEFGHIJKLMONPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	/* 3*/ public static final String S_azAZ09 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMONPQRSTUVWXYZ0123456789";
	/*-3*/ public static final String S_AZaz09 = "ABCDEFGHIJKLMONPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	/* 4*/ public static final String S_CTSY = "bcdfghjklmnpqrstvwxyz";
	/*-4*/ public static final String S_VOWEL = "aeiou";
	/* 5*/ public static final String S_CTS = "bcdfghjklmnpqrstvwxz";
	/*-5*/ public static final String S_VOWELY = "aeiouy";
	/* 6*/ public static final String S_09 = "0123456789";
	/*-6*/ public static final String S_90= "9876543210";
	/* 7*/ public static final String S_ASCII = new Str((char)0,(char)255).str();
	/*-7*/ public static final String S_ASCIIP = new Str((char)32,(char)128).str();
	/* 8*/ public static final String S_SYS_LINE_SEPARATOR = System.lineSeparator();
	/*-8*/ public static final String S_FILE_PATH_SEPARATOR = File.pathSeparator;
	/* 9*/ public static final String S_FILE_SEPARATOR = File.separator;
	/* ?*/ public static final String S_EMPTY = "";
	
	
	private String toStrVal() throws SyntaxError {
		if (_snd.equals("")) {
			int id = 0;
			try {
				id = Integer.parseInt(_fst);
			} catch (NumberFormatException nfe) {
				throw new SyntaxError("Invalid special number: :" + _fst + "s" + _snd);
			}
			
			switch (id) {
			case  0: return S_HW;
			case  1: return S_AZ;
			case -1: return S_az;
			case  2: return S_azAZ;
			case -2: return S_AZaz;
			case  3: return S_azAZ09;
			case -3: return S_AZaz09;
			case  4: return S_CTSY;
			case -4: return S_VOWEL;
			case  5: return S_CTS;
			case -5: return S_VOWELY;
			case  6: return S_09;
			case -6: return S_90;
			case  7: return S_ASCII;
			case -7: return S_ASCIIP;
			case  8: return S_SYS_LINE_SEPARATOR;
			case -8: return S_FILE_PATH_SEPARATOR;
			case  9: return S_FILE_SEPARATOR;
			
			default: return S_EMPTY;
			}
		}
		else {
			return "";
		}
	}
}

