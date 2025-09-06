package aya.obj.block;

import java.util.ArrayList;
import java.util.Stack;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.util.Triple;
import aya.util.TypeUtils;

public class CheckReturnTypeInstance {

	private ArrayList<Triple<Symbol, Dict, StaticBlock>> _items;
	
	public CheckReturnTypeInstance(ArrayList<Triple<Symbol, Dict, StaticBlock>> items) {
		_items = items;
	}


	public void check(final BlockEvaluator blockEvaluator, int start_stack_size) {
		checkSize(blockEvaluator, start_stack_size);
		
		final Stack<Obj> stk = blockEvaluator.getStack();
		for (int i = 0; i < _items.size(); i++) {
			if (!TypeUtils.isInstance(stk.get(start_stack_size + i), _items.get(i).second(), blockEvaluator.getContext())) {
				typeErr(_items.get(i).second(), _items.get(i).third());
			}
		}
	}
	
	private static void typeErr(Obj value, StaticBlock type_block) throws TypeError {
		ReprStream msg = new ReprStream();
		msg.setTight(true);
		msg.print("Expected type ");
		type_block.repr(msg);
		msg.print(", received: ");
		msg.setTight(false);
		value.repr(msg);
		throw new TypeError(msg.toString());
	}

	/**
	 * Verify the stack has the new items using length only
	 * This means that the function could have added and removed additional
	 *  items not in its return type list as long as the stack ends up being
	 *  the same size. But this should cover most cases just fine
	 * @param blockEvaluator
	 * @throws TypeError
	 */
	private void checkSize(final BlockEvaluator blockEvaluator, int start_stack_size) throws TypeError {
		
		int current_size = blockEvaluator.getStack().size();
		int expected_size = start_stack_size + _items.size();
		if (current_size > expected_size) {
			
			// Create the error message
			ReprStream msg = new ReprStream();
			msg.println("Function returned " + (current_size - expected_size) + " additional values");
			Stack<Obj> stk = blockEvaluator.getStack();
			for (int i = stk.size() - 1; i >= expected_size; i--) {
				msg.print("  - ");
				stk.get(i).repr(msg);
				msg.println();
			}
			throw new TypeError(msg.toString());
			
		} else if (current_size < expected_size) {
			
			// Create error message
			ReprStream msg = new ReprStream();
			msg.println("Function missing return values. Previous stack size was " + start_stack_size + ". Current stack size is " + current_size);
			throw new TypeError(msg.toString());
		
		} else {
			// All good!
		}
	}

	public ReprStream repr(ReprStream stream) {
		boolean tight = stream.isTight();
		stream.setTight(true);
		for (var item : _items) {
			stream.print(item.first().name());
			stream.print("::");
			item.third().repr(stream);
			stream.print(" ");
		}
		// return back to original state
		stream.setTight(tight);
		return stream;
	}
}
