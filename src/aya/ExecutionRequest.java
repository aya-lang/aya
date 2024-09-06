package aya;

import java.util.Stack;

import aya.obj.Obj;
import aya.obj.block.StaticBlock;

public class ExecutionRequest {
	long _id;
	Stack<Obj> _data;
	StaticBlock _block;
	
	public ExecutionRequest(long id, Stack<Obj> data, StaticBlock block) {
		_id = id;
		_data = data;
		_block = block;
	}
	
	public ExecutionRequest(long id, StaticBlock block) {
		this(id, new Stack<Obj>(), block);
	}
	
	public StaticBlock getBlock() {
		return _block;
	}
	
	public long id() {
		return _id;
	}
}
