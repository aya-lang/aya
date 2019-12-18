package aya.instruction.op;

import java.util.LinkedList;

import aya.instruction.Instruction;
import aya.instruction.op.overload.OpOverload;

/**
 * The Operation Class
 * Every operator has some basic information (name, desc, argtypes)
 * and an execute method. The execute method is called by the interpreter
 * at run time and can manipulate a block
 * 
 * @author npaul
 *
 */
public abstract class OpInstruction extends Instruction {
	public String name;
	public OpOverload overload = null;
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	protected String repr(LinkedList<Long> visited) {
		return name;
	}
}
