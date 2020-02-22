package aya;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.management.RuntimeErrorException;

import aya.instruction.variable.GetVariableInstruction;

/**
 * Utility class for tracing function calls during Aya execution
 * @author npaul
 *
 */
public class CallStack {

	public static final CallStackFrame CHECKPOINT = new CallStackFrame(null);

	public static class CallStackFrame {
		private GetVariableInstruction _instruction;
		private boolean _is_checkpoint;
		
		
		public CallStackFrame(GetVariableInstruction instruction) {
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
	
	private Stack<CallStackFrame> _stack;
	
	public CallStack() {
		_stack = new Stack<CallStackFrame>();
	}
	
	public void push(GetVariableInstruction var) {
		_stack.push(new CallStackFrame(var));
	}
	
	public void pop() {
		_stack.pop();
	}
	
	public void setCheckpoint() {
		_stack.push(CHECKPOINT);
	}
	
	public void popCheckpoint() {
		CallStackFrame csf = _stack.pop();
		if (!csf.isCheckpoint()) {
			throw new RuntimeException("Attempted to pop callstack checkpoint but the top of the stack was " + csf);
		}
	}
	
	public void rollbackCheckpoint() {
		try {
			CallStackFrame csf = _stack.pop();
			// If this loop throws an exception, there is a bug somewhere that
			// is either rolling back when there is no checkpoint or removing a checkpoint
			// when it should not be
			while (!csf.isCheckpoint()) {
				csf = _stack.pop();
			}
		} catch (EmptyStackException e) {
			throw new RuntimeException("Failed attempt to rollback chackpoint in call stack");
		}
	}
	
	public void reset() {
		_stack.clear();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("Function call traceback:\n  Error ");
		ArrayList<CallStackFrame> stack_list = new ArrayList<CallStackFrame>(_stack.size());
		for (CallStackFrame l : _stack) stack_list.add(l);
		Collections.reverse(stack_list);
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
		return _stack.isEmpty();
	}
}
