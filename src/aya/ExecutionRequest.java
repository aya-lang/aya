package aya;

import java.util.Stack;

import aya.obj.Obj;
import aya.obj.block.StaticBlock;

public class ExecutionRequest {
	Stack<Obj> _data;
	StaticBlock _block;
	
	public ExecutionRequest(Stack<Obj> data, StaticBlock block) {
		_data = data;
		_block = block;
	}
	
	public ExecutionRequest(StaticBlock block) {
		_data = new Stack<Obj>();
		_block = block;
	}
	
	public StaticBlock getBlock() {
		return _block;
	}
}
