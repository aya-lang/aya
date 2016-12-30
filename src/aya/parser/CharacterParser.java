package aya.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import aya.exceptions.SyntaxError;

public class CharacterParser {
	public static final String TAB_STR = "  ";
	public static final char INVALID = Character.MAX_VALUE;
	public static boolean mapExists = false;
	public static HashMap<String, Character> specialCharacters = new HashMap<String, Character>(); //Special Characters
	
	public static void add_char(String name, char c) {
		specialCharacters.put(name,c);
	}
	
	public static void add_char(String name, String codepoint) {
		add_char(name, getCharUni(codepoint));
	}
	
	/** return SUCCESS if successful, return error message if unsuccessful */
	public static String initMap() {
		if (mapExists) return "SUCCESS";
		
		
		add_char("in","2208"); //âˆˆ
		add_char("ni","220b"); //âˆ‹
		add_char("leq","2264"); //â‰¤
		add_char("geq","2265"); //â‰¥
		add_char("ll","226a"); //â‰ª
		add_char("gg","226b"); //â‰«
		add_char("prec","227a"); //â‰º
		add_char("succ","227b"); //â‰»
		add_char("preceq","227c"); //â‰¼
		add_char("succeq","227d"); //â‰½
		add_char("sim","223c"); //âˆ¼
		add_char("cong","2245"); //â‰…
		add_char("simeq","2243"); //â‰ƒ
		add_char("approx","2248"); //â‰ˆ
		add_char("equiv","2261"); //â‰¡
		add_char("subset","2282"); //âŠ‚
		add_char("supset","2283"); //âŠƒ
		add_char("subseteq","2286"); //âŠ†
		add_char("supseteq","2287"); //âŠ‡
		add_char("sqsubseteq","2291"); //âŠ‘
		add_char("sqsupseteq","2292"); //âŠ’
		add_char("perp","22a5"); //âŠ¥
		add_char("models","22a7"); //âŠ§
		add_char("mid","2223"); //âˆ£
		add_char("parallel","2225"); //âˆ¥
		add_char("vdash","22a2"); //âŠ¢
		add_char("vvdash","22a9"); //âŠ©
		add_char("vddash","22a7"); //âŠ§
		add_char("dashv","22a3"); //âŠ£
		add_char("bowtie","22c8"); //â‹ˆ
		add_char("join","22c8"); //â‹ˆ
		add_char("pm","b1"); //Â±
		add_char("mp","2213"); //âˆ“
		add_char("times","d7"); //Ã—
		add_char("cdot","b7"); //Â·
		add_char("circ","2218"); //âˆ˜
		add_char("bigcirc","25ef"); //â—¯
		add_char("div","f7"); //Ã·
		add_char("diamond","22c4"); //â‹„
		add_char("ast","2217"); //âˆ—
		add_char("star","2606"); //â˜†
		add_char("cap","2229"); //âˆ©
		add_char("cup","222a"); //âˆª
		add_char("sqcap","2293"); //âŠ“
		add_char("sqcup","2294"); //âŠ�?
		add_char("wedge","2227"); //âˆ§
		add_char("vee","2228"); //âˆ¨
		add_char("trileft","25c3"); //â—ƒ
		add_char("triright","25b9"); //â–¹
		add_char("bigtriup","25b3"); //â–³
		add_char("bigtridown","25bd"); //â–½
		add_char("oplus","2295"); //âŠ•
		add_char("ominus","2296"); //âŠ–
		add_char("otimes","2297"); //âŠ—
		add_char("oslash","2298"); //âŠ˜
		add_char("odot","2299"); //âŠ™
		add_char("bullet","2022"); //â€¢
		add_char("dagger","2020"); //â€ 
		add_char("ddagger","2021"); //â€¡
		add_char("setminus","2216"); //âˆ–
		add_char("uplus","228e"); //âŠŽ
		add_char("wr","2240"); //â‰€
		add_char("amalg","2a3f"); //â¨¿
		add_char("lhd","22b2"); //âŠ²
		add_char("rhd","22b3"); //âŠ³
		add_char("unlhd","22b4"); //âŠ´
		add_char("unrhd","22b5"); //âŠµ
		add_char("dotplus","2214"); //âˆ�?
		add_char("centerdot","22c5"); //â‹…
		add_char("ltimes","22c9"); //â‹‰
		add_char("rtimes","22ca"); //â‹Š
		add_char("leftthreex","22cb"); //â‹‹
		add_char("rightthreex","22cc"); //â‹Œ
		add_char("circleddash","2296"); //âŠ–
		add_char("ssminus","2216"); //âˆ–
		add_char("barwedge","22bc"); //âŠ¼
		add_char("curlyvee","22ce"); //â‹Ž
		add_char("veebar","22bb"); //âŠ»
		add_char("intercal","22ba"); //âŠº
		add_char("cup","22d3"); //â‹“
		add_char("cap","22d2"); //â‹’
		add_char("circledast","229b"); //âŠ›
		add_char("circledcirc","229a"); //âŠš
		add_char("boxminus","229f"); //âŠŸ
		add_char("boxtimes","22a0"); //âŠ 
		add_char("boxdot","22a1"); //âŠ¡
		add_char("boxplus","229e"); //âŠž
		add_char("dividetimes","22c7"); //â‹‡
		add_char("happy","263a"); //â˜º
		add_char("invhappy","263b"); //â˜»
		add_char("heart","2665"); //â™¥
		add_char("diamnd","2666"); //â™¦
		add_char("club","2663"); //â™£
		add_char("spade","2660"); //â™ 
		add_char("dot","2022"); //â€¢
		add_char("between","226c"); //â‰¬
		add_char("pitchfork","22d4"); //â‹�?
		add_char("backepsilon","3f6"); //�?¶
		add_char("blktrileft","25c2"); //â—‚
		add_char("blktriright","25b8"); //â–¸
		add_char("therefore","2234"); //âˆ´
		add_char("because","2235"); //âˆµ
		add_char("ne","2260"); //â‰ 
		add_char("alpha","3b1"); //Î±
		add_char("kappa","3ba"); //Îº
		add_char("psi","3c8"); //�?ˆ
		add_char("ddelta","2206"); //âˆ†
		add_char("ttheta","398"); //Î˜
		add_char("beta","3b2"); //Î²
		add_char("lambda","3bb"); //Î»
		add_char("rho","3c1"); //�?�
		add_char("ggamma","393"); //Î“
		add_char("uupsilon","3a5"); //Î¥
		add_char("chi","3c7"); //�?‡
		add_char("mu","3bc"); //Î¼
		add_char("sigma","3c3"); //�?ƒ
		add_char("llambda","39b"); //Î›
		add_char("xxi","39e"); //Îž
		add_char("delta","3b4"); //Î´
		add_char("nu","3bd"); //Î½
		add_char("tau","3c4"); //�?„
		add_char("oomega","2126"); //â„¦
		add_char("theta","3b8"); //Î¸
		add_char("pphi","3a6"); //Î¦
		add_char("aleph","5d0"); //×�
		add_char("eta","3b7"); //Î·
		add_char("omega","3c9"); //�?‰
		add_char("upsilon","3c5"); //�?…
		add_char("ppi","3a0"); //Î 
		add_char("gamma","3b3"); //Î³
		add_char("phi","3c6"); //�?†
		add_char("xi","3be"); //Î¾
		add_char("ppsi","3a8"); //Î¨
		add_char("iota","3b9"); //Î¹
		add_char("pi","3c0"); //�?€
		add_char("zeta","3b6"); //Î¶
		add_char("ssigma","3a3"); //Î£
		add_char("gimel","5d2"); //×’
		add_char("inf","221e"); //âˆž
		add_char("forall","2200"); //âˆ€
		add_char("wp","2118"); //â„˜
		add_char("nabla","2207"); //âˆ‡
		add_char("exists","2203"); //âˆƒ
		add_char("angle","2220"); //âˆ 
		add_char("partial","2202"); //âˆ‚
		add_char("eth","f0"); //Ã°
		add_char("emptyset","2205"); //âˆ…
		add_char("para","b6"); //Â¶
		add_char("fourth","bc"); //Â¼
		add_char("half","bd"); //Â½
		add_char("threefourths","be"); //Â¾
		add_char("deg","b0"); //Â°
		add_char("square","b2"); //Â²
		add_char("cube","b3"); //Â³
		add_char("block","2588"); //â–ˆ
		add_char("rarrow","21a6"); //â†¦
		add_char("larrow","21a4"); //â†¤
		add_char("plane","2708"); //âœˆ
		add_char("cloud","2601"); //â˜�
		add_char("umbrella","2602"); //â˜‚
		add_char("snowman","2603"); //â˜ƒ
		add_char("comet","2604"); //â˜„
		add_char("fstar","2605"); //â˜…
		add_char("star","2606"); //â˜†
		add_char("bounce","2607"); //â˜‡
		add_char("rabounce","2608"); //â˜ˆ
		add_char("circdot","2609"); //â˜‰
		add_char("headphones","260a"); //â˜Š
		add_char("iheadphones","260b"); //â˜‹
		add_char("link","260c"); //â˜Œ
		add_char("handcuff","260d"); //â˜�
		add_char("fphone","260e"); //â˜Ž
		add_char("phone","260f"); //â˜�
		add_char("todo","2610"); //â˜�
		add_char("todocheck","2611"); //â˜‘
		add_char("todox","2612"); //â˜’
		add_char("xx","2613"); //â˜“
		add_char("wetumbrella","2614"); //â˜�?
		add_char("java","2615"); //â˜•
		add_char("homeplate","2616"); //â˜–
		add_char("fhomeplate","2617"); //â˜—
		add_char("clover","2618"); //â˜˜
		add_char("parsnip","2619"); //â˜™
		add_char("fhandleft","261a"); //â˜š
		add_char("fhandright","261b"); //â˜›
		add_char("handleft","261c"); //â˜œ
		add_char("handup","261d"); //â˜�
		add_char("handright","261e"); //â˜ž
		add_char("handdown","261f"); //â˜Ÿ
		add_char("dead","2620"); //â˜ 
		add_char("squiggle","2621"); //â˜¡
		add_char("radioactive","2622"); //â˜¢
		add_char("biohazard","2623"); //â˜£
		add_char("caduces","2624"); //â˜¤
		add_char("ankh","2625"); //â˜¥
		add_char("cross","2626"); //â˜¦
		add_char("xp","2627"); //â˜§
		add_char("crossb","2628"); //â˜¨
		add_char("plusserif","2629"); //â˜©
		add_char("cstar","262a"); //â˜ª
		add_char("adishakti","262b"); //â˜«
		add_char("bird","262c"); //â˜¬
		add_char("hamsickle","262d"); //â˜­
		add_char("peace","262e"); //â˜®
		add_char("balance","262f"); //â˜¯
		add_char("heaven","2630"); //â˜°
		add_char("lake","2631"); //â˜±
		add_char("fire","2632"); //â˜²
		add_char("thunder","2633"); //â˜³
		add_char("wind","2634"); //â˜´
		add_char("water","2635"); //â˜µ
		add_char("mountain","2636"); //â˜¶
		add_char("earth","2637"); //â˜·
		add_char("gear","2638"); //â˜¸
		add_char("sadface","2639"); //â˜¹
		add_char("happyface","263a"); //â˜º
		add_char("fhappyface","263b"); //â˜»
		add_char("sunb","263c"); //â˜¼
		add_char("lmoon","263d"); //â˜½
		add_char("rmoon","263e"); //â˜¾
		add_char("mercury","263f"); //â˜¿
		add_char("female","2640"); //â™€
		add_char("mfearth","2641"); //â™�
		add_char("male","2642"); //â™‚
		add_char("jupiter","2643"); //â™ƒ
		add_char("saturn","2644"); //â™„
		add_char("uranus","2645"); //â™…
		add_char("nuptune","2646"); //â™†
		add_char("pluto","2647"); //â™‡
		add_char("aries","2648"); //â™ˆ
		add_char("taturus","2649"); //â™‰
		add_char("gemini","264a"); //â™Š
		add_char("cancer","264b"); //â™‹
		add_char("leo","264c"); //â™Œ
		add_char("virgo","264d"); //â™�
		add_char("libra","264e"); //â™Ž
		add_char("scorpius","264f"); //â™�
		add_char("sagittarius","2650"); //â™�
		add_char("capricorn","2651"); //â™‘
		add_char("aquarius","2652"); //â™’
		add_char("pisces","2653"); //â™“
		add_char("chessking","2654"); //â™�?
		add_char("chessqueen","2655"); //â™•
		add_char("chessrook","2656"); //â™–
		add_char("chessbishop","2657"); //â™—
		add_char("chessknight","2658"); //â™˜
		add_char("chesspawn","2659"); //â™™
		add_char("fchessking","265a"); //â™š
		add_char("fchessqueen","265b"); //â™›
		add_char("fchessrook","265c"); //â™œ
		add_char("fchessbishop","265d"); //â™�
		add_char("fchessknight","265e"); //â™ž
		add_char("fchesspawn","265f"); //â™Ÿ
		add_char("fspade","2660"); //â™ 
		add_char("heart","2661"); //â™¡
		add_char("diamond","2662"); //â™¢
		add_char("fclub","2663"); //â™£
		add_char("spade","2664"); //â™¤
		add_char("fheart","2665"); //â™¥
		add_char("fdiamond","2666"); //â™¦
		add_char("club","2667"); //â™§
		add_char("hotsprings","2668"); //â™¨
		add_char("notequarter","2669"); //â™©
		add_char("noteeighth","266a"); //â™ª
		add_char("notebeighth","266b"); //â™«
		add_char("notebsix","266c"); //â™¬
		add_char("musicflat","266d"); //â™­
		add_char("musicnatural","266e"); //â™®
		add_char("musicsharp","266f"); //â™¯

		mapExists = true;
		return "SUCCESS";
	}
	
	/** Test if a string contains al lowercase alphabetical letters */
	public static boolean lalpha(String str) {
		for (char c : str.toCharArray()) {
			if (!('a' <= c && c <= 'z')) {
				return false;
			}
		}
		return true;
	}
	
	/** Returns a character given its unicode value as a hex string */
	public static char getCharUni(String unicode) {
		return (char)Integer.parseInt(unicode, 16);
	}
	
	/** Returns "(char) (name)" for each entry in the character map */
	public static ArrayList<String> getAllCharDiscs() {
		ArrayList<String> out = new ArrayList<String>();
		Iterator<Map.Entry<String, Character>> entries = specialCharacters.entrySet().iterator();
		while (entries.hasNext()) {
		  Entry<String, Character> thisEntry = (Entry<String, Character>) entries.next();
		  out.add(thisEntry.getValue() + " " + thisEntry.getKey() + "\n(special character)");
		}
		return out;
	}
	
	public static boolean isSpecialChar(char c) {
		return specialCharacters.containsValue(c);
	}
	
	/** Returns the name of a character from the special character set. If the character is
	 *  a-z, return the character as a string.
	 *  If the character is not in the special character set, 
	 *  and if the character is not a-z,
	 *  return null. */
	public static String getName(char special) {
		if(special >= 'a' && special <= 'z') {
			return special+"";
		}
		Iterator<Map.Entry<String, Character>> it = specialCharacters.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Character> pair = (Map.Entry<String, Character>)it.next();
			if (special == pair.getValue()) {
				return pair.getKey();
		    }
		}
		return null;
	}
	
	/**  
	 * Returns Character.MAX_VALUE if char is invalid */
	public static char parse(String in) {
		String s = in;

		//Character.initMap() should be called in main somewhere
		if(!mapExists) return INVALID;

		//Invalid Character
		if (s.length() < 1) {
			return INVALID;
		}
		
		//Single Character
		else if (s.length() == 1) {
			char c = s.charAt(0);
			switch (c) {
			case 'n':
				return '\n';
			case 't':
				return '\t';
			case 'r':
				return '\r';
			case 'b':
				return '\b';
			case 'f':
				return '\f';
				
			default:
				return c;
			}
		}
		
		//Hex Character
		else if (s.charAt(0) == 'U') {
			s = s.substring(1, s.length()).trim();
			if (isHex(s)) {
				try {
					return (char)Integer.parseInt(s, 16);
				} catch (NumberFormatException e) {
					throw new SyntaxError("Cannot parse character as hex value in " + in);
				}
			} else {
				return INVALID;
			}
		} 
		
		//Special character from list
		else {
			Character c = specialCharacters.get(s);
			if(c == null) {
				return INVALID;
			}
			return c;
		}
	}
		
	/** Returns true if all characters in a string are digits */
	public static boolean allDigits(String s) {
		for(char c : s.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}	
	
	/** Returns true if the string is less than 5
	 * chararacters and are all valid hex characters */
	public static boolean isHex(String s) {
		for(char c : s.toCharArray()) {
			if(Character.isUpperCase(c)) {
				c = Character.toLowerCase(c);
			}
			if ( !((c >= '0' && c <= '9') || (c >= 'A' || c <= 'F')) ) {
				return false;
			}
		}
		return true;
	}

	
	public static String convertCharTabPress(String s) {
		char[] chars = s.toCharArray();
		
		if(s.length() == 0) {
			return TAB_STR;
		}
		
		StringBuilder charName = new StringBuilder();
		StringBuilder otherText = new StringBuilder();
		int i = chars.length-1;
		while (chars[i] != '\\') {
			charName.append(chars[i--]);
			if(i < 0) {
				return s+TAB_STR; //No special character
			}
		}
		i--; //Skip the '\'
		while (i >= 0) {
			otherText.append(chars[i--]);
		}
		
		charName.reverse();
		otherText.reverse();
		
		char c = parse(charName.toString());
		if (c == INVALID || charName.length() == 1) {
			return s+TAB_STR; //No valid character, append the normal tab
		} else {
			return otherText.toString() + c;
		}
	}
}
