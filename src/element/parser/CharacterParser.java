package element.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import element.exceptions.SyntaxError;

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
		
		
		add_char("in","2208"); //∈
		add_char("ni","220b"); //∋
		add_char("leq","2264"); //≤
		add_char("geq","2265"); //≥
		add_char("ll","226a"); //≪
		add_char("gg","226b"); //≫
		add_char("prec","227a"); //≺
		add_char("succ","227b"); //≻
		add_char("preceq","227c"); //≼
		add_char("succeq","227d"); //≽
		add_char("sim","223c"); //∼
		add_char("cong","2245"); //≅
		add_char("simeq","2243"); //≃
		add_char("approx","2248"); //≈
		add_char("equiv","2261"); //≡
		add_char("subset","2282"); //⊂
		add_char("supset","2283"); //⊃
		add_char("subseteq","2286"); //⊆
		add_char("supseteq","2287"); //⊇
		add_char("sqsubseteq","2291"); //⊑
		add_char("sqsupseteq","2292"); //⊒
		add_char("perp","22a5"); //⊥
		add_char("models","22a7"); //⊧
		add_char("mid","2223"); //∣
		add_char("parallel","2225"); //∥
		add_char("vdash","22a2"); //⊢
		add_char("vvdash","22a9"); //⊩
		add_char("vddash","22a7"); //⊧
		add_char("dashv","22a3"); //⊣
		add_char("bowtie","22c8"); //⋈
		add_char("join","22c8"); //⋈
		add_char("pm","b1"); //±
		add_char("mp","2213"); //∓
		add_char("times","d7"); //×
		add_char("cdot","b7"); //·
		add_char("circ","2218"); //∘
		add_char("bigcirc","25ef"); //◯
		add_char("div","f7"); //÷
		add_char("diamond","22c4"); //⋄
		add_char("ast","2217"); //∗
		add_char("star","2606"); //☆
		add_char("cap","2229"); //∩
		add_char("cup","222a"); //∪
		add_char("sqcap","2293"); //⊓
		add_char("sqcup","2294"); //⊔
		add_char("wedge","2227"); //∧
		add_char("vee","2228"); //∨
		add_char("trileft","25c3"); //◃
		add_char("triright","25b9"); //▹
		add_char("bigtriup","25b3"); //△
		add_char("bigtridown","25bd"); //▽
		add_char("oplus","2295"); //⊕
		add_char("ominus","2296"); //⊖
		add_char("otimes","2297"); //⊗
		add_char("oslash","2298"); //⊘
		add_char("odot","2299"); //⊙
		add_char("bullet","2022"); //•
		add_char("dagger","2020"); //†
		add_char("ddagger","2021"); //‡
		add_char("setminus","2216"); //∖
		add_char("uplus","228e"); //⊎
		add_char("wr","2240"); //≀
		add_char("amalg","2a3f"); //⨿
		add_char("lhd","22b2"); //⊲
		add_char("rhd","22b3"); //⊳
		add_char("unlhd","22b4"); //⊴
		add_char("unrhd","22b5"); //⊵
		add_char("dotplus","2214"); //∔
		add_char("centerdot","22c5"); //⋅
		add_char("ltimes","22c9"); //⋉
		add_char("rtimes","22ca"); //⋊
		add_char("leftthreex","22cb"); //⋋
		add_char("rightthreex","22cc"); //⋌
		add_char("circleddash","2296"); //⊖
		add_char("ssminus","2216"); //∖
		add_char("barwedge","22bc"); //⊼
		add_char("curlyvee","22ce"); //⋎
		add_char("veebar","22bb"); //⊻
		add_char("intercal","22ba"); //⊺
		add_char("cup","22d3"); //⋓
		add_char("cap","22d2"); //⋒
		add_char("circledast","229b"); //⊛
		add_char("circledcirc","229a"); //⊚
		add_char("boxminus","229f"); //⊟
		add_char("boxtimes","22a0"); //⊠
		add_char("boxdot","22a1"); //⊡
		add_char("boxplus","229e"); //⊞
		add_char("dividetimes","22c7"); //⋇
		add_char("happy","263a"); //☺
		add_char("invhappy","263b"); //☻
		add_char("heart","2665"); //♥
		add_char("diamnd","2666"); //♦
		add_char("club","2663"); //♣
		add_char("spade","2660"); //♠
		add_char("dot","2022"); //•
		add_char("between","226c"); //≬
		add_char("pitchfork","22d4"); //⋔
		add_char("backepsilon","3f6"); //϶
		add_char("blktrileft","25c2"); //◂
		add_char("blktriright","25b8"); //▸
		add_char("therefore","2234"); //∴
		add_char("because","2235"); //∵
		add_char("ne","2260"); //≠
		add_char("alpha","3b1"); //α
		add_char("kappa","3ba"); //κ
		add_char("psi","3c8"); //ψ
		add_char("ddelta","2206"); //∆
		add_char("ttheta","398"); //Θ
		add_char("beta","3b2"); //β
		add_char("lambda","3bb"); //λ
		add_char("rho","3c1"); //ρ
		add_char("ggamma","393"); //Γ
		add_char("uupsilon","3a5"); //Υ
		add_char("chi","3c7"); //χ
		add_char("mu","3bc"); //μ
		add_char("sigma","3c3"); //σ
		add_char("llambda","39b"); //Λ
		add_char("xxi","39e"); //Ξ
		add_char("delta","3b4"); //δ
		add_char("nu","3bd"); //ν
		add_char("tau","3c4"); //τ
		add_char("oomega","2126"); //Ω
		add_char("theta","3b8"); //θ
		add_char("pphi","3a6"); //Φ
		add_char("aleph","5d0"); //א
		add_char("eta","3b7"); //η
		add_char("omega","3c9"); //ω
		add_char("upsilon","3c5"); //υ
		add_char("ppi","3a0"); //Π
		add_char("gamma","3b3"); //γ
		add_char("phi","3c6"); //φ
		add_char("xi","3be"); //ξ
		add_char("ppsi","3a8"); //Ψ
		add_char("iota","3b9"); //ι
		add_char("pi","3c0"); //π
		add_char("zeta","3b6"); //ζ
		add_char("ssigma","3a3"); //Σ
		add_char("gimel","5d2"); //ג
		add_char("inf","221e"); //∞
		add_char("forall","2200"); //∀
		add_char("wp","2118"); //℘
		add_char("nabla","2207"); //∇
		add_char("exists","2203"); //∃
		add_char("angle","2220"); //∠
		add_char("partial","2202"); //∂
		add_char("eth","f0"); //ð
		add_char("emptyset","2205"); //∅
		add_char("para","b6"); //¶
		add_char("fourth","bc"); //¼
		add_char("half","bd"); //½
		add_char("threefourths","be"); //¾
		add_char("deg","b0"); //°
		add_char("square","b2"); //²
		add_char("cube","b3"); //³
		add_char("block","2588"); //█
		add_char("rarrow","21a6"); //↦
		add_char("larrow","21a4"); //↤
		add_char("plane","2708"); //✈
		add_char("cloud","2601"); //☁
		add_char("umbrella","2602"); //☂
		add_char("snowman","2603"); //☃
		add_char("comet","2604"); //☄
		add_char("fstar","2605"); //★
		add_char("star","2606"); //☆
		add_char("bounce","2607"); //☇
		add_char("rabounce","2608"); //☈
		add_char("circdot","2609"); //☉
		add_char("headphones","260a"); //☊
		add_char("iheadphones","260b"); //☋
		add_char("link","260c"); //☌
		add_char("handcuff","260d"); //☍
		add_char("fphone","260e"); //☎
		add_char("phone","260f"); //☏
		add_char("todo","2610"); //☐
		add_char("todocheck","2611"); //☑
		add_char("todox","2612"); //☒
		add_char("xx","2613"); //☓
		add_char("wetumbrella","2614"); //☔
		add_char("java","2615"); //☕
		add_char("homeplate","2616"); //☖
		add_char("fhomeplate","2617"); //☗
		add_char("clover","2618"); //☘
		add_char("parsnip","2619"); //☙
		add_char("fhandleft","261a"); //☚
		add_char("fhandright","261b"); //☛
		add_char("handleft","261c"); //☜
		add_char("handup","261d"); //☝
		add_char("handright","261e"); //☞
		add_char("handdown","261f"); //☟
		add_char("dead","2620"); //☠
		add_char("squiggle","2621"); //☡
		add_char("radioactive","2622"); //☢
		add_char("biohazard","2623"); //☣
		add_char("caduces","2624"); //☤
		add_char("ankh","2625"); //☥
		add_char("cross","2626"); //☦
		add_char("xp","2627"); //☧
		add_char("crossb","2628"); //☨
		add_char("plusserif","2629"); //☩
		add_char("cstar","262a"); //☪
		add_char("adishakti","262b"); //☫
		add_char("bird","262c"); //☬
		add_char("hamsickle","262d"); //☭
		add_char("peace","262e"); //☮
		add_char("balance","262f"); //☯
		add_char("heaven","2630"); //☰
		add_char("lake","2631"); //☱
		add_char("fire","2632"); //☲
		add_char("thunder","2633"); //☳
		add_char("wind","2634"); //☴
		add_char("water","2635"); //☵
		add_char("mountain","2636"); //☶
		add_char("earth","2637"); //☷
		add_char("gear","2638"); //☸
		add_char("sadface","2639"); //☹
		add_char("happyface","263a"); //☺
		add_char("fhappyface","263b"); //☻
		add_char("sunb","263c"); //☼
		add_char("lmoon","263d"); //☽
		add_char("rmoon","263e"); //☾
		add_char("mercury","263f"); //☿
		add_char("female","2640"); //♀
		add_char("mfearth","2641"); //♁
		add_char("male","2642"); //♂
		add_char("jupiter","2643"); //♃
		add_char("saturn","2644"); //♄
		add_char("uranus","2645"); //♅
		add_char("nuptune","2646"); //♆
		add_char("pluto","2647"); //♇
		add_char("aries","2648"); //♈
		add_char("taturus","2649"); //♉
		add_char("gemini","264a"); //♊
		add_char("cancer","264b"); //♋
		add_char("leo","264c"); //♌
		add_char("virgo","264d"); //♍
		add_char("libra","264e"); //♎
		add_char("scorpius","264f"); //♏
		add_char("sagittarius","2650"); //♐
		add_char("capricorn","2651"); //♑
		add_char("aquarius","2652"); //♒
		add_char("pisces","2653"); //♓
		add_char("chessking","2654"); //♔
		add_char("chessqueen","2655"); //♕
		add_char("chessrook","2656"); //♖
		add_char("chessbishop","2657"); //♗
		add_char("chessknight","2658"); //♘
		add_char("chesspawn","2659"); //♙
		add_char("fchessking","265a"); //♚
		add_char("fchessqueen","265b"); //♛
		add_char("fchessrook","265c"); //♜
		add_char("fchessbishop","265d"); //♝
		add_char("fchessknight","265e"); //♞
		add_char("fchesspawn","265f"); //♟
		add_char("fspade","2660"); //♠
		add_char("heart","2661"); //♡
		add_char("diamond","2662"); //♢
		add_char("fclub","2663"); //♣
		add_char("spade","2664"); //♤
		add_char("fheart","2665"); //♥
		add_char("fdiamond","2666"); //♦
		add_char("club","2667"); //♧
		add_char("hotsprings","2668"); //♨
		add_char("notequarter","2669"); //♩
		add_char("noteeighth","266a"); //♪
		add_char("notebeighth","266b"); //♫
		add_char("notebsix","266c"); //♬
		add_char("musicflat","266d"); //♭
		add_char("musicnatural","266e"); //♮
		add_char("musicsharp","266f"); //♯

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
