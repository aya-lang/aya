package obj;

public abstract class Obj {	
	
	public static final byte NUMBER = 1;
	public static final byte NUM = 11;
	public static final byte BIGNUM = 12;
	public static final byte RATIONAL_NUMBER = 13;
	
	public static final byte LIST = 2;
	public static final byte STR = 21;
	public static final byte NUMBERLIST = 22;
	public static final byte NUMBERITEMLIST = 23;
	
	public static final byte CHAR = 3;
	
	public abstract Obj deepcopy();
	public abstract boolean bool();
	public abstract String repr();
	public abstract String str();
	public abstract boolean equiv(Obj o);
	public abstract boolean isa(byte type);
	public abstract byte type();
	
	@Override
	public String toString() {
		return this.repr();
	}
	
	@Override
	public boolean equals(Object o) {
		return this.equiv((Obj)o);
	}
}
