package aya.infix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import aya.Aya;

public class CompilerTestCases {
	private HashMap<String, String> testCases;
	
	public CompilerTestCases() {
		testCases = new HashMap<String, String>();
		
		//Basic Operator Organizing
		z("1+1", "2");
		z("5+6", "11");
		z("6+1/2*4", "8.0");
		z("1/2", "0.5");
		z("9-5", "4");
		z("1+4/2", "3.0");
		z("3-4/2+1", "2.0");
		z("2^2*3+1", "13");
		z("1+4/2^2", "2");
		z("8/3/1^3+9^2/1^1+3^3^3", "7625597485070.666666666666667");
		z("3^3^3", "7625597484987");
		z("2-5-4", "-7");
		z("3+3*5^9-4^1/5-3+6+7^8*7-5-9^9-7-7-3", "-341207523.8");
		z("5/1*7-3*5-6/6+3*8+1^3/9+3*5*2-6^4-8+6+6-7+1^7^2+6^4-4/2-2+5-7+8-8-9*4-7+5+6-5+4+7+1^2*6+8/5*3/4/8-8", "37.26111111111111112220446049250313080847263336181640625");
		
		//Parenthesis
		z("2*((8^2)^(1+1)/8+1/2-4^2*2)", "961.0");
		
		//Functions
		z("cos(ceil(21941 + 251 * 524 / cos(5827655 * 395)))", "0.8843052269");
		z("24 / 46 - sin(ceil(52 * 57))", "1.5174484618");
		z("floor(abs(50 / 90 - cos(floor(sin(51 / 89 ^ abs(45) ^ sqrt(71)) + abs(floor(45 + 31 % sin(97 / 36 * 60))) * ceil(13 + 85) + 60 - 35 / 59 - 81 ^ 59 / 65 * ceil(85 ^ 79))) ^ 68 + 71) * 70)", "5008");
	}
	
	public void z(String in, String res) {
		//testCases.put(in,  res);
	}
	
	public static void initElement() {
		Compiler.run("assign(sqrt, eval(\"{Mq}\"))", false);
	}
	
	public boolean testAll(boolean debug) {
		initElement();
	    Iterator<Map.Entry<String, String>> it = testCases.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, String> pair = (Map.Entry<String, String>)it.next();

	        Aya.instance.run(Compiler.compile(pair.getKey(), debug));
	        String res = Aya.instance.getOut().dumpAsString();
	        
	        if(debug) System.out.println("\tOUT   : " + res);
	        
	        if(!pair.getValue().equals(res)) {
	        	throw new RuntimeException("\n"
	        			+ "INPUT: " + pair.getKey() + "\n"
	        			+ "EXPEC: " + pair.getValue() + "\n"
	        			+ "RETUR: " + res + "\n");
	        	
	        }
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    
	    //All tests passed
	    return true;
	}
	
	public void add(String test, String expectedResult) {
		testCases.put(test, expectedResult);
	}
}
