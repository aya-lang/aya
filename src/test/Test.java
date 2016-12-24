package test;

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
}
