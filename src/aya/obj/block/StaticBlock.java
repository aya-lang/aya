package aya.obj.block;

import java.util.ArrayList;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.instruction.Instruction;
import aya.instruction.flag.PopVarFlagInstruction;
import aya.instruction.variable.assignment.Assignment;
import aya.obj.Obj;
import aya.obj.dict.Dict;

public class StaticBlock extends Obj {
	
	public static StaticBlock EMPTY = new StaticBlock(new ArrayList<Instruction>());
	
	private Dict _locals;
	private ArrayList<Instruction> _instructions;
	private ArrayList<Assignment> _args;
	private NewLocalsInstruction _new_locals_instruction;
	
	protected StaticBlock(ArrayList<Instruction> instructions, Dict locals, ArrayList<Assignment> args) {
		// If args is empty, just use null
		if (args != null && args.size() == 0) args = null;

		_locals = locals;
		_args = args;
		_instructions = instructions;

		// If we have args, locals is implied
		if (_args != null && _locals == null) {
			_locals = new Dict();
		}
		
		if (_locals == null) {
			_new_locals_instruction = null;
		} else {
			_new_locals_instruction = new NewLocalsInstruction(_locals, _args);
		}
	}

	protected StaticBlock(ArrayList<Instruction> instructions) {
		this(instructions, null, null);
	}
	
	public void dumpToBlockEvaluator(BlockEvaluator b) {
		if (_locals != null) {
			// Pop the variable frame when the blockEvaluator is done
			b.add(PopVarFlagInstruction.INSTANCE);
			b.addAll(_instructions);
			b.add(_new_locals_instruction);
		} else {
			b.addAll(_instructions);
		}
	}
	
	public boolean hasLocals() {
		return _locals != null;
	}
	
	public int getNumArgs() {
		if (_args == null) {
			return 0;
		} else {
			return _args.size();
		}
	}
	
	//////////////////////
	// Used By BlockOps //
	//////////////////////
	
	protected Dict getLocals() {
		return _locals;
	}

	protected ArrayList<Instruction> getInstructions() {
		return _instructions;
	}

	protected ArrayList<Assignment> getArgs() {
		return _args;
	}



	///////////////////
	// OBJ OVERRIDES //
	///////////////////
	
	
	@Override
	public Obj deepcopy() {
		return this;
	}

	@Override
	public boolean bool() {
		return true;
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		return BlockUtils.repr(stream, this, true, null);
	}

	@Override
	public String str() {
		return BlockUtils.repr(new ReprStream(), this, true, null).toStringOneline();
	}

	@Override
	public boolean equiv(Obj o) {
		// Always return false
		return false;
	}

	@Override
	public boolean isa(byte type) {
		return type == Obj.BLOCK;
	}

	@Override
	public byte type() {
		return Obj.BLOCK;
	}
	
	@Override
	public String toString() {
		return this.str();
	}

}
