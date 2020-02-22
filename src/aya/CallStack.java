package aya;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import aya.instruction.variable.GetVariableInstruction;

/**
 * Utility class for tracing function calls during Aya execution
 * @author npaul
 *
 */
public class CallStack {
	private Stack<GetVariableInstruction> _stack;
	
	public CallStack() {
		_stack = new Stack<GetVariableInstruction>();
	}
	
	public void push(GetVariableInstruction var) {
		_stack.push(var);
	}
	
	public void pop() {
		_stack.pop();
	}
	
	public void reset() {
		_stack.clear();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("Function call traceback:\n  Error ");
		ArrayList<GetVariableInstruction> stack_list = new ArrayList<GetVariableInstruction>(_stack.size());
		for (GetVariableInstruction l : _stack) stack_list.add(l);
		Collections.reverse(stack_list);
		for (GetVariableInstruction l : stack_list)
		{
			sb.append("in: ");
			sb.append(l.toString());
			sb.append("\n  ");
		}
		return sb.toString();
	}

	public boolean isEmpty() {
		return _stack.isEmpty();
	}
}
