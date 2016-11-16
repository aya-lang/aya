package element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ElementTestCases {
	private static HashMap<String, String> testCases = new HashMap<String, String>();
	
	public static void addTestCase(String in, String res) {
		testCases.put(in,  res);
	}
	
	public static boolean runTests() {
	    Iterator<Map.Entry<String, String>> it = testCases.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, String> pair = (Map.Entry<String, String>)it.next();

	        Element.instance.run(pair.getKey());
	        String res = Element.instance.getOut().dumpAsString();
	        	        
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
	
}
