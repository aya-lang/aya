package aya.exceptions.runtime;

import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

@SuppressWarnings("serial")
public class ThreadError extends AyaRuntimeException {

	public ThreadError(String message) {
		super("(thread <anon>) " + message);
	}

	public ThreadError(int thread_id, String message) {
		super("(thread " + thread_id + ") " + message);
	}
	
	public ThreadError(InterruptedException ex) {
		this("thread interrupted");
	}

	public ThreadError(int thread_id, InterruptedException ex) {
		this(thread_id, "thread interrupted");
	}
	
	@Override
	public Symbol typeSymbol() {
		return SymbolConstants.THREAD_ERR;
	}

}
