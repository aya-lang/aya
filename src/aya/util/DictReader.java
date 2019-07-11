package aya.util;

import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.dict.KeyVariable;
import aya.obj.list.List;
import aya.obj.number.Number;
import aya.obj.symbol.Symbol;
import aya.variable.Variable;

public class DictReader {
	private Dict _dict;
	private String _err_name;
	
	public DictReader(Dict dict) {
		_dict = dict;
		_err_name = "DictReader";
	}
	
	public void setErrorName(String message) {
		_err_name = message;
	}
	
	public AyaRuntimeException notFound(long key) throws AyaRuntimeException {
		String key_name = Variable.decodeLong(key);
		return new AyaRuntimeException(_err_name + ": Key '" + key_name + "' does not exist");
	}
	
	public AyaRuntimeException badType(long key, String type_expected, Obj got) throws AyaRuntimeException {
		String key_name = Variable.decodeLong(key);
		return new AyaRuntimeException(_err_name + ": Expected type ::" + type_expected +" for key '" + key_name + "' but got " + got.repr());
	}
	
	public String getSymStringEx(long key) throws AyaRuntimeException {
		Obj val = _dict.getSafe(key);
		if (val == null) throw notFound(key);
		
		if (val.isa(Obj.STR)) {
			return val.str();
		} else if (val.isa(Obj.SYMBOL)) {
			return ((Symbol)val).name();
		} else {
			throw badType(key, "sym", val);
		}
	}
	
	public double getDoubleEx(long key) {
		Obj o = _dict.getSafe(key);
		
		if (o == null) {
			throw notFound(key);
		} else if (!o.isa(Obj.NUM)) {
			throw badType(key, "num", o);
		} else {
			return ((Number)o).toDouble();
		}
	}
	
	
	public int getIntEx(long key) {
		Obj o = _dict.getSafe(key);
		
		if (o == null) {
			throw notFound(key);
		} else if (!o.isa(Obj.NUM)) {
			throw badType(key, "num", o);
		} else {
			return ((Number)o).toInt();
		}
	}
	
	public String getStringEx(long key) {
		Obj o = _dict.getSafe(key);
		
		if (o == null) {
			throw notFound(key);
		} else if (!o.isa(Obj.STR)) {
			throw badType(key, "str", o);
		} else {
			return o.str();
		}
	}
	
	public String getSymString(long key) {
		Obj val = _dict.getSafe(key);
		if (val == null) return null;
		
		if (val.isa(Obj.STR)) {
			return val.str();
		} else if (val.isa(Obj.SYMBOL)) {
			return ((Symbol)val).name();
		} else {
			return null;
		}
	}
	
	public String getSymString(long key, String dflt) {
		final String s = getSymString(key);
		if (s == null) {
			return dflt;
		} else {
			return s;
		}
	}
	
	public double getDouble(long key, double dflt) {
		Obj o = _dict.getSafe(key);
		
		if (o == null || !o.isa(Obj.NUM)) {
			return dflt;
		} else {
			return ((Number)o).toDouble();
		}
	}
	
	public int getInt(long key, int dflt) {
		Obj o = _dict.getSafe(key);
		
		if (o == null || !o.isa(Obj.NUM)) {
			return dflt;
		} else {
			return ((Number)o).toInt();
		}
	}
	
	
	public String getString(long key) {
		Obj o = _dict.getSafe(key);
		
		if (o == null || !o.isa(Obj.STR)) {
			return null;
		} else {
			return o.str();
		}
	}
	
	public String getString(long key, String dflt) {
		Obj o = _dict.getSafe(key);

		if (o == null || !o.isa(Obj.STR)) {
			return dflt;
		} else {
			return o.str();
		}
	}
	
}
