package aya;

import java.util.ArrayList;
import java.util.Stack;

import aya.obj.Obj;

public class ExecutionResultSuccess extends ExecutionResult {

	ArrayList<Obj> _data;
	
	public ExecutionResultSuccess(long id, Stack<Obj> data) {
		super(ExecutionResult.TYPE_SUCCESS, id);
		_data = new ArrayList<Obj>(data.size());
		for (Obj o : data) {
			_data.add(o);
		}
	}
	
	public ArrayList<Obj> getData() {
		return _data;
	}
}
