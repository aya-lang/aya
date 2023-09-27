package test;

import aya.instruction.op.Operator;

public class TestCase {
	String input;
	String expected;
	Operator caller;
	
	public TestCase(String input, String expected, Operator caller) {
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

	public Operator getOp() {
		return caller;
	}
}
