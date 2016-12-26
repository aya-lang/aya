package test;

import test.obj.list.numberlist.NumberItemListTest;
import test.obj.number.NumberBinaryOpsTest;
import test.obj.number.RationalNumTest;

public abstract class Test {
	public abstract void runTests();
	
	public void tru(boolean val, String message) {
		if (!val) {
			throw new RuntimeException(message);
		}
	}
	
	public static void eq(Object input, Object expected, String message) {
		if (!input.equals(expected)) {
			message += "\n\tExpected: " + expected.toString() + "\n"
			+ "\tRecieved: " + input.toString();
			throw new RuntimeException(message);
		}
	}
	
	public static void main(String[] args) {
		new NumberItemListTest().runTests();
		new NumberBinaryOpsTest().runTests();
		new RationalNumTest().runTests();
	}
}
