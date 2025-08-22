package aya.obj.symbol;

import aya.ReprStream;
import aya.obj.Obj;
import aya.util.StringUtils;

public class Symbol extends Obj {
	
	int _id;

	protected Symbol(int id) {
		_id = id;
	}
	
	public int id() {
		return _id;
	}
	
	public String name() {
		return SymbolTable.getName(this);
	}
	
	/**
	 * Quote the name if it is not all lowercase alpha
	 * @return
	 */
	public String reprName() {
		String n = name();
		if (StringUtils.lalphau(n)) {
			return n;
		} else {
			return StringUtils.quote(n);
		}
	}
	
	@Override
	public int hashCode() {
		return _id;
	}
	
	@Override
	public Obj deepcopy() {
		return this;
	}

	@Override
	public boolean bool() {
		return true;
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("::");
		stream.print(reprName());
		return stream;
	}

	@Override
	public String str() {
		return "::" + StringUtils.quote(name());
	}

	@Override
	public boolean equiv(Obj o) {
		return o instanceof Symbol && ((Symbol)o)._id == _id;
	}

	@Override
	public boolean isa(byte type) {
		return type == Obj.SYMBOL;
	}

	@Override
	public byte type() {
		return Obj.SYMBOL;
	}

}
