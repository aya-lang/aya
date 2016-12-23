package obj;

public abstract class Obj {	
	
	public abstract Obj deepcopy();
	public abstract boolean bool();
	public abstract String repr();
	public abstract String str();
	public abstract boolean equiv(Obj o);
	
}
