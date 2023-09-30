package aya.obj.block;

import java.util.ArrayList;

import aya.Aya;
import aya.ReprStream;
import aya.instruction.Instruction;
import aya.instruction.flag.PopVarFlagInstruction;
import aya.instruction.variable.assignment.Assignment;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;

public class StaticBlock extends Obj {
	
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

	///////////////////////
	// String Conversion //
	///////////////////////

	/** If true, return "{instructions}" else just "instructions" */
	private ReprStream _repr(ReprStream stream, boolean print_braces) {
		if (print_braces) stream.print("{");
		
		// Header
		if (_locals != null) {
			// Args
			for (int i = _args.size()-1; i >= 0; i--) {
				stream.print(_args.get(i).toString());
				stream.print(" ");
			}
			
			// Non-arg locals
			if (_locals.size() != 0) {
				stream.print(": ");
			
				// Print locals
				for (Symbol key : _locals.keys()) {
					stream.print(key.name());
					stream.print("(");
					_locals.get(key).repr(stream);
					stream.print(")");
				}
			}
			
			// Trim off the final space
			stream.delTrailingSpaces();
			stream.print(",");
		}

		for(Instruction i : _instructions) {
			i.repr(stream);
			stream.print(" ");
		}

		// Remove trailing space
		stream.delTrailingSpaces();

		if (print_braces) stream.print("}");
		return stream;
	}
	

	public ReprStream repr(ReprStream stream, boolean print_braces) {
		if (stream.visit(this)) {
			_repr(stream, print_braces);
			stream.popVisited(this);
		} else {
			stream.print("{...}");
		}
		return stream;
	}
	
	@Override
	public String toString() {
		return repr(new ReprStream(), true).toStringOneline();
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
		return repr(stream, true);
	}

	@Override
	public String str() {
		return repr(new ReprStream(), true).toStringOneline();
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

}
