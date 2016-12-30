package test;

import aya.entities.Operation;

public class TestCase {
	String input;
	String expected;
	Operation caller;
	
	public TestCase(String input, String expected, Operation caller) {
		this.input = input;
		this.expected = expected;
		this.caller = caller;
	}
	
	public TestCase(String input, String expected) {
		this.input = input;
		this.expected = expected;
		this.caller = null;
	}

	public String getInput() {
		return input;
	}	
	
	public String getExpected() {
		return expected;
	}

	public Operation getOp() {
		return caller;
	}
}
