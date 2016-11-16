package element.test;

import java.util.ArrayList;

import element.Element;
import element.entities.Operation;

public class ElementTestCases {
	private static ArrayList<TestCase> testCases = new ArrayList<TestCase>();
	
	public static void addTestCase(String in, String res) {
		testCases.add(new TestCase(in, res, null));
	}
	
	public static void addTestCase(String in, String res, Operation op) {
		testCases.add(new TestCase(in, res, op));
	}
	
	public static boolean runTests() {
		for (TestCase tc : testCases) {
	        Element.instance.run(tc.getInput());
	        String res = Element.instance.getOut().dumpAsString();
	        	        
	        if(!tc.getExpected().equals(res)) {
	        	String out = "\nTest case failure:\n"
	        			+ "INPUT: " + tc.getInput() + "\n"
	        			+ "EXPEC: " + tc.getExpected() + "\n"
	        			+ "RETUR: " + res + "\n"
	        			+ (tc.getOp() == null ? "" : "OP: " + tc.getOp().name);
	        	throw new RuntimeException(out);
	        }	        
	    }
	    
	    //All tests passed
	    return true;
	}
	
}
