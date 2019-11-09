package test;

import aya.instruction.op.OpInstruction;

public class TestCase {
	String input;
	String expected;
	OpInstruction caller;
	
	public TestCase(String input, String expected, OpInstruction caller) {
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

	public OpInstruction getOp() {
		return caller;
	}
}
