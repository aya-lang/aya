package aya;

import java.util.ArrayList;
import java.util.EmptyStackException;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.AyaStackOverflowError;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.block.CheckReturnTypeInstance;
import aya.parser.SourceString;
import aya.parser.SourceStringRef;

/**
 * Utility class for tracing function calls during Aya execution
 * @author npaul
 *
 */
public class CallStack {

	private static int MAX_STACK_DEPTH = 1024;

	public static class CallStackFrame {
		private GetVariableInstruction _instruction;
		// For try/catch
		private boolean _is_checkpoint;
		// Size of the stack when this frame was added (minus arg len)
		private int _stack_size;
		// Return type check
		private CheckReturnTypeInstance _ret_type;
		
		public CallStackFrame() {
			reset(null, null, 0);
		}

		public void reset(GetVariableInstruction instruction, CheckReturnTypeInstance ret_type, int stack_size) {
			_instruction = instruction;
			_is_checkpoint = instruction == null;
			_stack_size = stack_size;
			_ret_type = ret_type;
		}
		
		public boolean isCheckpoint() {
			return _is_checkpoint;
		}
		
		public String toString() {
			if (_instruction != null) {
				StringBuilder sb = new StringBuilder();
				
				SourceStringRef ref = _instruction.getSource();
				SourceString source = ref.getSource();
				SourceString.IndexedSourceLine line = source.getIndexedLine(ref.getIndex());

				sb.append("  File '" + source.getFilename() + "', line " + line.lineNumber);
				sb.append(" in " + _instruction.toString() + ":\n");
				sb.append("    " + line.line + "\n");
				sb.append("    " + line.pointerStr() + "\n");

				return sb.toString();
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
	
	public void push(GetVariableInstruction var, CheckReturnTypeInstance ret_type, int arg_len, int stack_size) {
		if (_stack_index < _stack.length-1) {
			_stack_index++;
			_stack[_stack_index].reset(var, ret_type, stack_size - arg_len);
		} else {
			throw new AyaStackOverflowError("Call stack overflow");
		}
	}
	
	private CallStackFrame peek() {
		// >= : stack_index is allowed to get to -1 (empty stack)
		if (_stack_index >= 0) {
			return _stack[_stack_index];
		} else {
			throw new EmptyStackException();
		}
	}
	
	public CallStackFrame pop(final BlockEvaluator blockEvaluator) {
		// >= : stack_index is allowed to get to -1 (empty stack)
		if (_stack_index >= 0) {
			CallStackFrame frame = _stack[_stack_index];
			_stack_index--;
			// If there is a return type on the frame, check it
			if (frame._ret_type != null && blockEvaluator != null) {
				frame._ret_type.check(blockEvaluator, frame._stack_size);
			}
			return frame;
		} else {
			throw new EmptyStackException();
		}
	}
	
	public void setCheckpoint() {
		push(null, null, 0, 0);
	}
	
	public void popCheckpoint() {
		CallStackFrame csf = pop(null);
		if (!csf.isCheckpoint()) {
			throw new RuntimeException("Attempted to pop callstack checkpoint but the top of the stack was " + csf);
		}
	}
	
	public void rollbackCheckpoint() {
		try {
			CallStackFrame csf = pop(null);
			// If this loop throws an exception, there is a bug somewhere that
			// is either rolling back when there is no checkpoint or removing a checkpoint
			// when it should not be
			while (!csf.isCheckpoint()) {
				csf = pop(null);
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
		StringBuilder sb = new StringBuilder("Function call traceback:\n");
		ArrayList<CallStackFrame> stack_list = new ArrayList<CallStackFrame>(size());
		for (int i = size()-1; i >= 0; i--) {
			if (i == MAX_STACK_DEPTH) return "Overflow";
			stack_list.add(_stack[i]);
		}

		//Collections.reverse(stack_list);
		for (CallStackFrame l : stack_list)
		{
			if (l.isCheckpoint()) continue;
			sb.append(l.toString());
		}
		sb.append("\n");
		return sb.toString();
	}

	public boolean isEmpty() {
		// -1 means empty stack
		return _stack_index < 0;
	}
	
	public int getCurrentFrameStartStackSize() {
		return peek()._stack_size;
	}
}
