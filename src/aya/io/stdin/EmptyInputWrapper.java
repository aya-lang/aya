package aya.io.stdin;

import aya.exceptions.runtime.IOError;

public class EmptyInputWrapper extends InputWrapper {

	@Override
	public String nextLine() {
		throw new IOError("", "EmptyInputWrapper.nextLine()", "Next line called!");
	}

}
