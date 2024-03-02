package aya.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NamedCharacters {
	
	private static HashMap<String, Character> character_names = null;

	
	//
	// Public API
	//

	/** Get the character given its name. Return null if name is unknown */
	public static Character get(String s) {
		if (character_names == null) {
			initMap();
		}
		
		return character_names.get(s);
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
		Iterator<Map.Entry<String, Character>> it = character_names.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Character> pair = (Map.Entry<String, Character>)it.next();
			if (special == pair.getValue()) {
				return pair.getKey();
		    }
		}
		return null;
	}
	
	public static void addChar(String name, char c) {
		character_names.put(name,c);
	}
	
	//
	// Private Helper Methods
	//
	
	
	private static void addChar(String name, String codepoint) {
		addChar(name, CharUtils.getCharUni(codepoint));
	}

	

	private static void initMap() {
		
		character_names = new HashMap<String, Character>();
		
		String[] htmlnames = {
			"nbsp","iexcl","cent","pound","curren","yen","brvbar","sect","Dot","copy",
			"ordf","laquo","not","shy","reg","macr","deg","plusmn","sup2","sup3","acute",
			"micro","para","middot","cedil","sup1","ordm","raquo","frac14","frac12",
			"frac34","iquest","Agrave","Aacute","Acirc","Atilde","Auml","Aring","AElig",
			"Ccedil","Egrave","Eacute","Ecirc","Euml","Igrave","Iacute","Icirc","Iuml",
			"ETH","Ntilde","Ograve","Oacute","Ocirc","Otilde","Ouml","times","Oslash",
			"Ugrave","Uacute","Ucirc","Uuml","Yacute","THORN","szlig","agrave","aacute",
			"acirc","atilde","auml","aring","aelig","ccedil","egrave","eacute","ecirc",
			"euml","igrave","iacute","icirc","iuml","eth","ntilde","ograve","oacute","ocirc",
			"otilde","ouml","divide","oslash","ugrave","uacute","ucirc","uuml","yacute","thorn","yuml"
		};
		
		int code = 160;
		for (String s : htmlnames) {
			addChar(s, (char)(code));
			code++;
		}
		
		// Some latex and misc names
		addChar("in","2208"); //âˆˆ
		addChar("ni","220b"); //âˆ‹
		addChar("leq","2264"); //â‰¤
		addChar("geq","2265"); //â‰¥
		addChar("ll","226a"); //â‰ª
		addChar("gg","226b"); //â‰«
		addChar("prec","227a"); //â‰º
		addChar("succ","227b"); //â‰»
		addChar("preceq","227c"); //â‰¼
		addChar("succeq","227d"); //â‰½
		addChar("sim","223c"); //âˆ¼
		addChar("cong","2245"); //â‰…
		addChar("simeq","2243"); //â‰ƒ
		addChar("approx","2248"); //â‰ˆ
		addChar("equiv","2261"); //â‰¡
		addChar("subset","2282"); //âŠ‚
		addChar("supset","2283"); //âŠƒ
		addChar("subseteq","2286"); //âŠ†
		addChar("supseteq","2287"); //âŠ‡
		addChar("sqsubseteq","2291"); //âŠ‘
		addChar("sqsupseteq","2292"); //âŠ’
		addChar("perp","22a5"); //âŠ¥
		addChar("models","22a7"); //âŠ§
		addChar("mid","2223"); //âˆ£
		addChar("parallel","2225"); //âˆ¥
		addChar("vdash","22a2"); //âŠ¢
		addChar("vvdash","22a9"); //âŠ©
		addChar("vddash","22a7"); //âŠ§
		addChar("dashv","22a3"); //âŠ£
		addChar("bowtie","22c8"); //â‹ˆ
		addChar("join","22c8"); //â‹ˆ
		addChar("pm","b1"); //Â±
		addChar("mp","2213"); //âˆ“
		addChar("times","d7"); //Ã—
		addChar("cdot","b7"); //Â·
		addChar("circ","2218"); //âˆ˜
		addChar("bigcirc","25ef"); //â—¯
		addChar("div","f7"); //Ã·
		addChar("diamond","22c4"); //â‹„
		addChar("ast","2217"); //âˆ—
		addChar("star","2606"); //â˜†
		addChar("cap","2229"); //âˆ©
		addChar("cup","222a"); //âˆª
		addChar("sqcap","2293"); //âŠ“
		addChar("sqcup","2294"); //âŠ�?
		addChar("wedge","2227"); //âˆ§
		addChar("vee","2228"); //âˆ¨
		addChar("trileft","25c3"); //â—ƒ
		addChar("triright","25b9"); //â–¹
		addChar("bigtriup","25b3"); //â–³
		addChar("bigtridown","25bd"); //â–½
		addChar("oplus","2295"); //âŠ•
		addChar("ominus","2296"); //âŠ–
		addChar("otimes","2297"); //âŠ—
		addChar("oslash","2298"); //âŠ˜
		addChar("odot","2299"); //âŠ™
		addChar("bullet","2022"); //â€¢
		addChar("dagger","2020"); //â€ 
		addChar("ddagger","2021"); //â€¡
		addChar("setminus","2216"); //âˆ–
		addChar("uplus","228e"); //âŠŽ
		addChar("wr","2240"); //â‰€
		addChar("amalg","2a3f"); //â¨¿
		addChar("lhd","22b2"); //âŠ²
		addChar("rhd","22b3"); //âŠ³
		addChar("unlhd","22b4"); //âŠ´
		addChar("unrhd","22b5"); //âŠµ
		addChar("dotplus","2214"); //âˆ�?
		addChar("centerdot","22c5"); //â‹…
		addChar("ltimes","22c9"); //â‹‰
		addChar("rtimes","22ca"); //â‹Š
		addChar("leftthreex","22cb"); //â‹‹
		addChar("rightthreex","22cc"); //â‹Œ
		addChar("circleddash","2296"); //âŠ–
		addChar("ssminus","2216"); //âˆ–
		addChar("barwedge","22bc"); //âŠ¼
		addChar("curlyvee","22ce"); //â‹Ž
		addChar("veebar","22bb"); //âŠ»
		addChar("intercal","22ba"); //âŠº
		addChar("cup","22d3"); //â‹“
		addChar("cap","22d2"); //â‹’
		addChar("circledast","229b"); //âŠ›
		addChar("circledcirc","229a"); //âŠš
		addChar("boxminus","229f"); //âŠŸ
		addChar("boxtimes","22a0"); //âŠ 
		addChar("boxdot","22a1"); //âŠ¡
		addChar("boxplus","229e"); //âŠž
		addChar("dividetimes","22c7"); //â‹‡
		addChar("happy","263a"); //â˜º
		addChar("invhappy","263b"); //â˜»
		addChar("heart","2665"); //â™¥
		addChar("diamnd","2666"); //â™¦
		addChar("club","2663"); //â™£
		addChar("spade","2660"); //â™ 
		addChar("dot","2022"); //â€¢
		addChar("between","226c"); //â‰¬
		addChar("pitchfork","22d4"); //â‹�?
		addChar("backepsilon","3f6"); //�?¶
		addChar("blktrileft","25c2"); //â—‚
		addChar("blktriright","25b8"); //â–¸
		addChar("therefore","2234"); //âˆ´
		addChar("because","2235"); //âˆµ
		addChar("ne","2260"); //â‰ 
		addChar("alpha","3b1"); //Î±
		addChar("kappa","3ba"); //Îº
		addChar("psi","3c8"); //�?ˆ
		addChar("ddelta","2206"); //âˆ†
		addChar("ttheta","398"); //Î˜
		addChar("beta","3b2"); //Î²
		addChar("lambda","3bb"); //Î»
		addChar("rho","3c1"); //�?�
		addChar("ggamma","393"); //Î“
		addChar("uupsilon","3a5"); //Î¥
		addChar("chi","3c7"); //�?‡
		addChar("mu","3bc"); //Î¼
		addChar("sigma","3c3"); //�?ƒ
		addChar("llambda","39b"); //Î›
		addChar("xxi","39e"); //Îž
		addChar("delta","3b4"); //Î´
		addChar("nu","3bd"); //Î½
		addChar("tau","3c4"); //�?„
		addChar("oomega","2126"); //â„¦
		addChar("theta","3b8"); //Î¸
		addChar("pphi","3a6"); //Î¦
		addChar("aleph","5d0"); //×�
		addChar("eta","3b7"); //Î·
		addChar("omega","3c9"); //�?‰
		addChar("upsilon","3c5"); //�?…
		addChar("ppi","3a0"); //Î 
		addChar("gamma","3b3"); //Î³
		addChar("phi","3c6"); //�?†
		addChar("xi","3be"); //Î¾
		addChar("ppsi","3a8"); //Î¨
		addChar("iota","3b9"); //Î¹
		addChar("pi","3c0"); //�?€
		addChar("zeta","3b6"); //Î¶
		addChar("ssigma","3a3"); //Î£
		addChar("gimel","5d2"); //×’
		addChar("inf","221e"); //âˆž
		addChar("forall","2200"); //âˆ€
		addChar("wp","2118"); //â„˜
		addChar("nabla","2207"); //âˆ‡
		addChar("exists","2203"); //âˆƒ
		addChar("angle","2220"); //âˆ 
		addChar("partial","2202"); //âˆ‚
		addChar("eth","f0"); //Ã°
		addChar("emptyset","2205"); //âˆ…
		addChar("para","b6"); //Â¶
		addChar("fourth","bc"); //Â¼
		addChar("half","bd"); //Â½
		addChar("threefourths","be"); //Â¾
		addChar("deg","b0"); //Â°
		addChar("square","b2"); //Â²
		addChar("cube","b3"); //Â³
		addChar("block","2588"); //â–ˆ
		addChar("rarrow","21a6"); //â†¦
		addChar("larrow","21a4"); //â†¤
		addChar("plane","2708"); //âœˆ
		addChar("cloud","2601"); //â˜�
		addChar("umbrella","2602"); //â˜‚
		addChar("snowman","2603"); //â˜ƒ
		addChar("comet","2604"); //â˜„
		addChar("fstar","2605"); //â˜…
		addChar("star","2606"); //â˜†
		addChar("bounce","2607"); //â˜‡
		addChar("rabounce","2608"); //â˜ˆ
		addChar("circdot","2609"); //â˜‰
		addChar("headphones","260a"); //â˜Š
		addChar("iheadphones","260b"); //â˜‹
		addChar("link","260c"); //â˜Œ
		addChar("handcuff","260d"); //â˜�
		addChar("fphone","260e"); //â˜Ž
		addChar("phone","260f"); //â˜�
		addChar("todo","2610"); //â˜�
		addChar("todocheck","2611"); //â˜‘
		addChar("todox","2612"); //â˜’
		addChar("xx","2613"); //â˜“
		addChar("wetumbrella","2614"); //â˜�?
		addChar("java","2615"); //â˜•
		addChar("homeplate","2616"); //â˜–
		addChar("fhomeplate","2617"); //â˜—
		addChar("clover","2618"); //â˜˜
		addChar("parsnip","2619"); //â˜™
		addChar("fhandleft","261a"); //â˜š
		addChar("fhandright","261b"); //â˜›
		addChar("handleft","261c"); //â˜œ
		addChar("handup","261d"); //â˜�
		addChar("handright","261e"); //â˜ž
		addChar("handdown","261f"); //â˜Ÿ
		addChar("dead","2620"); //â˜ 
		addChar("squiggle","2621"); //â˜¡
		addChar("radioactive","2622"); //â˜¢
		addChar("biohazard","2623"); //â˜£
		addChar("caduces","2624"); //â˜¤
		addChar("ankh","2625"); //â˜¥
		addChar("cross","2626"); //â˜¦
		addChar("xp","2627"); //â˜§
		addChar("crossb","2628"); //â˜¨
		addChar("plusserif","2629"); //â˜©
		addChar("cstar","262a"); //â˜ª
		addChar("adishakti","262b"); //â˜«
		addChar("bird","262c"); //â˜¬
		addChar("hamsickle","262d"); //â˜­
		addChar("peace","262e"); //â˜®
		addChar("balance","262f"); //â˜¯
		addChar("heaven","2630"); //â˜°
		addChar("lake","2631"); //â˜±
		addChar("fire","2632"); //â˜²
		addChar("thunder","2633"); //â˜³
		addChar("wind","2634"); //â˜´
		addChar("water","2635"); //â˜µ
		addChar("mountain","2636"); //â˜¶
		addChar("earth","2637"); //â˜·
		addChar("gear","2638"); //â˜¸
		addChar("sadface","2639"); //â˜¹
		addChar("happyface","263a"); //â˜º
		addChar("fhappyface","263b"); //â˜»
		addChar("sunb","263c"); //â˜¼
		addChar("lmoon","263d"); //â˜½
		addChar("rmoon","263e"); //â˜¾
		addChar("mercury","263f"); //â˜¿
		addChar("female","2640"); //â™€
		addChar("mfearth","2641"); //â™�
		addChar("male","2642"); //â™‚
		addChar("jupiter","2643"); //â™ƒ
		addChar("saturn","2644"); //â™„
		addChar("uranus","2645"); //â™…
		addChar("nuptune","2646"); //â™†
		addChar("pluto","2647"); //â™‡
		addChar("aries","2648"); //â™ˆ
		addChar("taturus","2649"); //â™‰
		addChar("gemini","264a"); //â™Š
		addChar("cancer","264b"); //â™‹
		addChar("leo","264c"); //â™Œ
		addChar("virgo","264d"); //â™�
		addChar("libra","264e"); //â™Ž
		addChar("scorpius","264f"); //â™�
		addChar("sagittarius","2650"); //â™�
		addChar("capricorn","2651"); //â™‘
		addChar("aquarius","2652"); //â™’
		addChar("pisces","2653"); //â™“
		addChar("chessking","2654"); //â™�?
		addChar("chessqueen","2655"); //â™•
		addChar("chessrook","2656"); //â™–
		addChar("chessbishop","2657"); //â™—
		addChar("chessknight","2658"); //â™˜
		addChar("chesspawn","2659"); //â™™
		addChar("fchessking","265a"); //â™š
		addChar("fchessqueen","265b"); //â™›
		addChar("fchessrook","265c"); //â™œ
		addChar("fchessbishop","265d"); //â™�
		addChar("fchessknight","265e"); //â™ž
		addChar("fchesspawn","265f"); //â™Ÿ
		addChar("fspade","2660"); //â™ 
		addChar("heart","2661"); //â™¡
		addChar("diamond","2662"); //â™¢
		addChar("fclub","2663"); //â™£
		addChar("spade","2664"); //â™¤
		addChar("fheart","2665"); //â™¥
		addChar("fdiamond","2666"); //â™¦
		addChar("club","2667"); //â™§
		addChar("hotsprings","2668"); //â™¨
		addChar("notequarter","2669"); //â™©
		addChar("noteeighth","266a"); //â™ª
		addChar("notebeighth","266b"); //â™«
		addChar("notebsix","266c"); //â™¬
		addChar("musicflat","266d"); //â™­
		addChar("musicnatural","266e"); //â™®
		addChar("musicsharp","266f"); //â™¯
	}
}
