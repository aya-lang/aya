package aya;

import aya.exceptions.runtime.AyaRuntimeException;

public class ExecutionResultException extends ExecutionResult {

	private AyaRuntimeException _ex;
	private String _callstack;
	
	public ExecutionResultException(long id, AyaRuntimeException ex, CallStack callstack) {
		super(ExecutionResult.TYPE_EXCEPTION, id);
		_ex = ex;
		_callstack = callstack.toString();
	}
	
	public String callstack() {
		return _callstack;
	}
	
	public AyaRuntimeException ex() {
		return _ex;
	}
}
