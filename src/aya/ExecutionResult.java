package aya;

public abstract class ExecutionResult {

	public static final int TYPE_SUCCESS = 1;
	public static final int TYPE_EXCEPTION = 2;
	
	private int _type; 
	private long _id;
	
	public ExecutionResult(int type, long id) {
		_id = id;
		_type = type;
	}
	
	public long id() {
		return _id;
	}
	
	public int getType() {
		return _type;
	}
	
}
