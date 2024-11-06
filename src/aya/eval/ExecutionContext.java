package aya.eval;

import aya.AyaStdIO;
import aya.CallStack;
import aya.StaticData;
import aya.obj.dict.Dict;
import aya.variable.VariableData;

public class ExecutionContext {

	private AyaStdIO _io;
	private VariableData _variables;
	private CallStack _callstack;

	private ExecutionContext(AyaStdIO io) {
		_io = io;
		_variables = new VariableData();
		_callstack = new CallStack();
	}
	
	public static ExecutionContext createIsolatedContext() {
		final ExecutionContext at = new ExecutionContext(StaticData.IO);
		
		at.getVars().add(new Dict()); // Add empty globals
		return at;
	}
	
	public static ExecutionContext createRoot(AyaStdIO io) {
		return new ExecutionContext(io);
	}
	
	public ExecutionContext createChild() {
		ExecutionContext child = new ExecutionContext(_io);
		child._variables = _variables.duplicateWithGlobals();
		return child;
	}
	
	public BlockEvaluator createEvaluator() {
		return new BlockEvaluator(this);
	}
	
	public VariableData getVars() {
		return _variables;
	}
	
	public CallStack getCallStack() {
		return _callstack;
	}
	


}
