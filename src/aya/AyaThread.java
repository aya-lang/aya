package aya;

import java.io.PrintWriter;
import java.io.StringWriter;

import aya.exceptions.runtime.AyaRuntimeException;
import aya.obj.block.BlockEvaluator;
import aya.variable.VariableData;

public class AyaThread {

	private AyaStdIO _io;
	private VariableData _variables;
	private CallStack _callstack;

	protected AyaThread(AyaStdIO io) {
		_io = io;
		_variables = new VariableData();
		_callstack = new CallStack();
	}

	
	public AyaThread spawnChild() {
		AyaThread child = new AyaThread(_io);
		// TODO: This must be changed
		child._variables = _variables;
		return child;
	}
	
	public VariableData getVars() {
		return _variables;
	}
	
	public CallStack getCallStack() {
		return _callstack;
	}
	
	/** Run a blockEvaluator */
	protected void run(BlockEvaluator b) {
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
			_io.err().println(exToString(e));
			try {
				
				if (b.hasOutputState())
					_io.err().println("stack:\n\t" + b.getPrintOutputState());
				if (b.getInstructions().size() > 0)
					_io.err().println("just before:\n\t" + b.getInstructions().toString());
				if (!_callstack.isEmpty())
					_io.err().print(_callstack.toString());
			} catch (Exception e2) {
				_io.err().println("An additional error was thrown when attempting to print the stack state:");
				_io.err().println(exToString(e2));
				_io.err().println("This is likely caused by an error in an overloaded __str__ or __repr__ blockEvaluator.");
			} 
		} finally {
			_variables.reset();
			_callstack.reset();
		}
	}
	
	// TODO move to utils
	public static String exToString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(AyaPrefs.BUG_MESSAGE);
		e.printStackTrace(pw);
		return sw.toString();
	}
	

}
