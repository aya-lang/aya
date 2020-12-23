package aya.instruction.flag;

import java.util.Arrays;

import aya.ReprStream;
import aya.exceptions.AyaRuntimeException;
import aya.obj.block.Block;

/**
 *  Evaluate the block on the stack
 */
public class TickFlagInstruction extends FlagInstruction {
	
	int amount;

	// Pre-create a few of the most common uses
	public static final TickFlagInstruction[] TICK_TABLE = new TickFlagInstruction[6];
	public static final String[] REPR_TABLE = new String[6];

	static {
		TICK_TABLE[0] = null;
		TICK_TABLE[1] = new TickFlagInstruction(1);
		TICK_TABLE[2] = new TickFlagInstruction(2);
		TICK_TABLE[3] = new TickFlagInstruction(3);
		TICK_TABLE[4] = new TickFlagInstruction(4);
		TICK_TABLE[5] = new TickFlagInstruction(5);
		
		REPR_TABLE[0] = null;
		REPR_TABLE[1] = "`";
		REPR_TABLE[2] = "``";
		REPR_TABLE[3] = "```";
		REPR_TABLE[4] = "````";
		REPR_TABLE[5] = "`````";
	}
	
	public TickFlagInstruction(int amount) {
		this.amount = amount;
	}
	
	@Override
	public void execute(Block b) {
		try {
			b.getInstructions().holdNext(amount);
		} catch (IndexOutOfBoundsException e) {
			throw new AyaRuntimeException("Tick Operator: Error attempting to move object back " + (amount) + " instructions");
		}
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(toString());
		return stream;
	}
	
	@Override
	public String toString() {
		if (amount <= 5) {
			return REPR_TABLE[amount];
		} else {
			char[] exmarks = new char[amount];
			Arrays.fill(exmarks, '`');
			return new String(exmarks);
		}
	}
}
