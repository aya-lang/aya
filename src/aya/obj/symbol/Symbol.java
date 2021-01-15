package aya.obj.symbol;

import aya.Aya;
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
	
	public String rawName() {
		return Aya.getInstance().getSymbols().getName(this);
	}
		
	public String name() {
		String s = Aya.getInstance().getSymbols().getName(this);
		if (SymbolTable.isBasicSymbolString(s)) {
			return s;
		} else {
			return StringUtils.quote(s);
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
		stream.print("::" + name());
		return stream;
	}

	@Override
	public String str() {
		return "::" + name();
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
