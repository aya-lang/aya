package aya.obj.block;

import java.util.ArrayList;

import aya.Aya;
import aya.ReprStream;
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
	
	public StaticBlock(ArrayList<Instruction> instructions, Dict locals, ArrayList<Assignment> args) {
		// If args is empty, just use null
		if (args != null && args.size() == 0) args = null;

		_locals = locals;
		_args = args;
		_instructions = instructions;

		// If we have args, locals is implied
		if (_args != null && _locals == null) {
			_locals = new Dict();
		}
	}

	public StaticBlock(ArrayList<Instruction> instructions) {
		this(instructions, null, null);
	}
	
	public void dumpToBlockEvaluator(Block b) {
		if (_locals != null) {
			Dict locals = _locals.clone();
			for (Assignment arg : _args) {
				arg.assign(locals, b.pop());
			}	
			Aya.getInstance().getVars().add(locals);
			
			b.addAll(_instructions);
			
			b.add(PopVarFlagInstruction.INSTANCE);
		} else {
			b.addAll(_instructions);
		}
	}
	
	public boolean hasLocals() {
		return _locals != null;
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
