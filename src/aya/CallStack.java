package aya;

import java.util.ArrayList;
import java.util.EmptyStackException;

import aya.exceptions.runtime.AyaStackOverflowError;
import aya.instruction.variable.GetVariableInstruction;

/**
 * Utility class for tracing function calls during Aya execution
 * @author npaul
 *
 */
public class CallStack {

	private static int MAX_STACK_DEPTH = 1024;

	public static class CallStackFrame {
		private GetVariableInstruction _instruction;
		private boolean _is_checkpoint;
		
		public CallStackFrame() {
			reset(null);
		}

		public void reset(GetVariableInstruction instruction) {
			_instruction = instruction;
			_is_checkpoint = instruction == null;
		}
		
		public boolean isCheckpoint() {
			return _is_checkpoint;
		}
		
		public String toString() {
			if (_instruction != null) {
				return _instruction.toString();
			} else {
				return "(CallStackFrame Checkpoint)";
			}
		}
	}

	
	private CallStackFrame[] _stack;
	// Points to the most recently filled stack frame
	// -1 for empty stack
	private int _stack_index;
	
	public CallStack() {
		_stack = new CallStackFrame[MAX_STACK_DEPTH];
		for (int i = 0; i < MAX_STACK_DEPTH; i++) {
			_stack[i] = new CallStackFrame();
		}
		_stack_index = -1;
	}
	
	public void push(GetVariableInstruction var) {
		if (_stack_index < _stack.length-1) {
			_stack_index++;
			_stack[_stack_index].reset(var);
		} else {
			throw new AyaStackOverflowError("Call stack overflow");
		}
	}
	
	public CallStackFrame pop() {
		// >= : stack_index is allowed to get to -1 (empty stack)
		if (_stack_index >= 0) {
			CallStackFrame frame = _stack[_stack_index];
			_stack_index--;
			return frame;
		} else {
			throw new EmptyStackException();
		}
	}
	
	public void setCheckpoint() {
		push(null);
	}
	
	public void popCheckpoint() {
		CallStackFrame csf = pop();
		if (!csf.isCheckpoint()) {
			throw new RuntimeException("Attempted to pop callstack checkpoint but the top of the stack was " + csf);
		}
	}
	
	public void rollbackCheckpoint() {
		try {
			CallStackFrame csf = pop();
			// If this loop throws an exception, there is a bug somewhere that
			// is either rolling back when there is no checkpoint or removing a checkpoint
			// when it should not be
			while (!csf.isCheckpoint()) {
				csf = pop();
			}
		} catch (EmptyStackException e) {
			throw new RuntimeException("Failed attempt to rollback checkpoint in call stack");
		}
	}
	
	public void reset() {
		_stack_index = -1;
	}

	public int size() {
		return _stack_index + 1;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("Function call traceback:\n  Error ");
		ArrayList<CallStackFrame> stack_list = new ArrayList<CallStackFrame>(size());
		for (int i = size()-1; i >= 0; i--) {
			if (i == MAX_STACK_DEPTH) return "Overflow";
			stack_list.add(_stack[i]);
		}

		for (CallStackFrame l : stack_list)
		{
			if (l.isCheckpoint()) continue;
			sb.append("in: ");
			sb.append(l.toString());
			sb.append("\n  ");
		}
		sb.append("\n");
		return sb.toString();
	}

	public boolean isEmpty() {
		// -1 means empty stack
		return _stack_index < 0;
	}
}
