package test;

import java.util.ArrayList;

import aya.instruction.op.Operator;

public class AyaTestCases {
	private static ArrayList<TestCase> testCases = new ArrayList<TestCase>();
//	private static boolean testsRan = false;
	
	public static void add(String in, String res) {
		testCases.add(new TestCase(in, res, null));
	}
	
	public static void add(String in, String res, Operator op) {
		testCases.add(new TestCase(in, res, op));
	}
	
	
//	private static void init() {
//		if (testsRan) return;
//		
//		add("1 1 +", "2");
//		add("1 3 +", "4");
//		add("(-1) 3 +.T", "'D'");
//
//	}
	
//	public static String runTests() {
//		init();
//		
//		StringBuilder sb = new StringBuilder();
//		
//		for (TestCase tc : testCases) {
//	        Aya.instance.run(tc.getInput());
//	        String res = Aya.instance.getOut().dumpAsString();
//	        	        
//	        if(!tc.getExpected().equals(res)) {
//	        	sb.append("\nTest case failure:\n"
//	        			+ "INPUT: " + tc.getInput() + "\n"
//	        			+ "EXPEC: " + tc.getExpected() + "\n"
//	        			+ "RETUR: " + res + "\n"
//	        			+ (tc.getOp() == null ? "" : "OP: " + tc.getOp().name));
//	        	sb.append("\n");
//	        }	        
//	    }
//	    
//		testsRan = true;
//		
//		if (sb.length() == 0) {
//			sb.append("All tests passed! (" + caseCount() + " tests ran)");
//		}
//		
//	    //All tests passed
//	    return sb.toString();
//	}
	
	public static int caseCount() {
		return testCases.size();
	}
}
