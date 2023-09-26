package aya.instruction;

import aya.ReprStream;
import aya.obj.block.Block;
import aya.parser.SourceStringRef;

public abstract class Instruction {
	
	private SourceStringRef source;

	public Instruction() {
		this.source = null;
	}
	
	public Instruction(SourceStringRef source) {
		this.source = source;
	}

	public abstract void execute(Block block);
	
	public abstract ReprStream repr(ReprStream stream);
	
	public SourceStringRef getSource() {
		return this.source;
	}
	
	public void setSource(SourceStringRef source) {
		this.source = source;
	}
	
	@Override
	public String toString() {
		return repr(new ReprStream()).toStringOneline();
	}

}
