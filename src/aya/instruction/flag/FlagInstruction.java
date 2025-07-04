package aya.instruction.flag;

import aya.ReprStream;
import aya.instruction.Instruction;
import aya.parser.SourceStringRef;

/**
 * Flags are special instructions that get passed to the interpreter.
 * Flags cannot be called from within the syntax and are generated
 * exclusively by the compiler.
 * @author npaul
 *
 */
public abstract class FlagInstruction extends Instruction {
	
	public FlagInstruction() {
		super(null);
	}
	
	public FlagInstruction(SourceStringRef source) {
		super(source);
	}


	@Override
	public ReprStream repr(ReprStream stream) {
		return stream;
	}
}
