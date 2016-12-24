package obj;

public abstract class Obj {	
	
	public static byte NUMBER = 1;
	public static byte NUM = 11;
	public static byte BIGNUM = 12;
	public static byte RATIONAL_NUMBER = 13;
	
	public static byte LIST = 2;
	public static byte STR = 21;
	public static byte NUMBERLIST = 22;
	public static byte NUMBERITEMLIST = 23;
	
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
