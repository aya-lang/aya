package aya.obj.block;

import java.util.ArrayList;

import aya.Aya;
import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.instruction.Instruction;
import aya.instruction.variable.assignment.Assignment;
import aya.obj.dict.Dict;

public class NewLocalsInstruction extends Instruction {

	private Dict _locals;
	private ArrayList<Assignment> _args;
	
		
	/** This instruction should only be created by dumpToBlockEvaluator */
	protected NewLocalsInstruction(Dict locals, ArrayList<Assignment> args) {
		super(null);
		_locals = locals;
		_args = args;
		if (_locals == null) throw new AssertionError();

	}
	
	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		Dict locals = _locals.clone();

		if (_args != null) {
			// Assign in reverse order
			// aya> 1 2 3 {a b c, a b}
			// 1 2
			for (int i = _args.size()-1; i >= 0; i--) {
				_args.get(i).assign(locals, blockEvaluator.pop());
			}	
		}
		
		// Add a new variable frame to the variable stack
		Aya.getInstance().getVars().add(locals);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		return stream;
	}

}
