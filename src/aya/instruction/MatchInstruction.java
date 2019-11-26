package aya.instruction;

import java.util.ArrayList;
import java.util.LinkedList;

import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.block.Block;


/**
 * {[num_captures] ? [(test_expr)|o] initializer,
 *   condition1 res1,
 *   condition2 res2,
 *   fallback
 * }
 * @author npaul
 *
 */
public class MatchInstruction extends Instruction {
	
	private int _num_captures;
	private Block _initializer;
	private Block _test_expr;
	private ArrayList<Block> _conditions;
	private ArrayList<Block> _results;
	private Block _fallback;

	
	public MatchInstruction(int num_captures, Block initializer, Block test_expr) {
		_num_captures = num_captures;
		_initializer = initializer;
		_test_expr = test_expr;
		_fallback = null;
		_conditions = new ArrayList<Block>();
		_results = new ArrayList<Block>();
	}

	
	public void addCondition(Block condition, Block result) {
		_conditions.add(condition);
		_results.add(result);
	}

	
	public void setFallback(Block fallback) {
		if (_fallback == null) {
			_fallback = fallback;
		} else {
			throw new RuntimeException("MatchInstruction.setFallback: \n"
					+ "Attempt to set fallback to " + fallback + "\n"
					+ "Fallback already set to " + _fallback);
		}
	}
	

	@Override
	public void execute(Block b) {
		Block result = null;
		
		// Initializer
		Block init = null;
		if (_initializer == null) {
			init = new Block();
		} else {
			init = _initializer.duplicate();
		}

		for (int i = 0; i < _num_captures; i++) {
			init.pushBack(b.pop());
		}
		init.eval();

		// Evaluate conditions
		for (int i = 0; i < _conditions.size(); i++) {
			Block condition;
			if (_test_expr == null) {
				condition = new Block();
			} else {
				condition = _test_expr.duplicate();
			}
			condition.addAll(_conditions.get(i).getInstructions().getInstrucionList());
			
			condition.addStack(init);
			condition.eval();
			if (condition.stackEmpty()) {
				Block msg = _conditions.get(i).duplicate();
				msg.addStack(init);
				throw new AyaRuntimeException("Empty stack after execution condition " + msg.repr());
			}
			Obj cond = condition.pop();
			if (cond.bool()) {
				result = _results.get(i).duplicate();
				break;
			}
		}
	
		// Any condition return true?
		if (result == null) {
			if (_fallback != null) {
				result = _fallback;
			}
		}
		
		if (result != null) {
			b.addAll(result.getInstructions().getInstrucionList());
		}

	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		StringBuilder sb = new StringBuilder("{");
		// Captures
		if (_num_captures != 1) {
			sb.append(_num_captures);
		}
		sb.append("?");
		
		// Test Expr
		if (_test_expr != null) {
			if (_test_expr.getInstructions().size() == 1) {
				sb.append(_test_expr.getInstructions().peek(0).repr());
			} else {
				sb.append("(" + _test_expr.getInstructions().toString() + ")");
			}
		}
		sb.append(" ");
		
		// Initializer Expr
		if (_initializer != null) {
			sb.append(_initializer.getInstructions().toString());
		}
		sb.append(", ");
		
		// Conditions
		for (int i = 0; i < _conditions.size(); i++) {
			sb.append("(" + _conditions.get(i).getInstructions().toString() + ") ");
			sb.append(_results.get(i).toString());
			sb.append(", ");
		}
		
		if (_fallback != null) {
			sb.append(_fallback.toString());
		}
		
		sb.append("}");
		
		return sb.toString();
	}

}
