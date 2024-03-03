package aya.eval;

import aya.AyaStdIO;
import aya.CallStack;
import aya.DebugUtils;
import aya.exceptions.runtime.AyaRuntimeException;
import aya.obj.block.StaticBlock;
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
		final ExecutionContext at = new ExecutionContext(new AyaStdIO(System.out, System.err, System.in));
		at.getVars().add(new Dict()); // Add empty globals
		return at;
	}
	
	public static ExecutionContext createRoot(AyaStdIO io) {
		return new ExecutionContext(io);
	}
	
	public ExecutionContext createChild() {
		ExecutionContext child = new ExecutionContext(_io);
		// TODO: This must be changed
		child._variables = _variables;
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
	
	/** Run a blockEvaluator */
	public void run(StaticBlock block) {
		BlockEvaluator b = createEvaluator();
		b.dump(block);
		try {
			b.eval();
			String s = b.getPrintOutputState();
			if (!s.equals("")) {
				_io.out().println(s);
			}
		} catch (AyaRuntimeException ex) {
			ex.print(_io.err());
			if (!_callstack.isEmpty()) {
				_io.err().print(_callstack.toString());
			}
		} catch (Exception e) {
			_io.err().println(DebugUtils.exToString(e));
			try {
				
				if (b.hasOutputState())
					_io.err().println("stack:\n\t" + b.getPrintOutputState());
				if (b.getInstructions().size() > 0)
					_io.err().println("just before:\n\t" + b.getInstructions().toString());
				if (!_callstack.isEmpty())
					_io.err().print(_callstack.toString());
			} catch (Exception e2) {
				_io.err().println("An additional error was thrown when attempting to print the stack state:");
				_io.err().println(DebugUtils.exToString(e2));
				_io.err().println("This is likely caused by an error in an overloaded __str__ or __repr__ blockEvaluator.");
			} 
		} finally {
			_variables.reset();
			_callstack.reset();
		}
	}

}
