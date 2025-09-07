package aya.util;

import java.awt.Color;

import aya.StaticData;
import aya.exceptions.runtime.AyaRuntimeException;
import aya.exceptions.runtime.UndefVarException;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Number;
import aya.obj.symbol.Symbol;

public class DictReader {
	
	private Dict _dict;
	private String _err_name;
	
	public DictReader(Dict dict) {
		_dict = dict;
		_err_name = "DictReader";
	}

	public DictReader(Dict dict, String error_name) {
		this(dict);
		_err_name = error_name;
	}

	public String get_err_name() {
		return _err_name;
	}

	public void setErrorName(String message) {
		_err_name = message;
	}
	
	public AyaRuntimeException notFound(Symbol key) throws AyaRuntimeException {
		return StaticDictReader.notFound(_err_name, _dict, key);
	}
	
	public AyaRuntimeException badType(Symbol key, String type_expected, Obj got) throws AyaRuntimeException {
		return StaticDictReader.badType(_err_name, key, type_expected, got);
	}
	
	public double[] getDoubleArrayEx(Symbol key) {
		Obj arr = _dict.getSafe(key);
		
		if (arr == null) {
			throw notFound(key);
		} if (arr.isa(Obj.LIST)) {
			return ((List)arr).toNumberList().todoubleArray();
		} else {
			throw badType(key, "list<num>", arr);
		}
	}
	
	public String getSymStringEx(Symbol key) throws AyaRuntimeException {
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
	
	public Color getColorEx(Symbol key) {
		Obj val = _dict.getSafe(key);
		if (val == null) throw notFound(key);
		return ObjToColor.objToColorEx(val);
	}
	
	public double getDoubleEx(Symbol key) {
		Obj o = _dict.getSafe(key);
		
		if (o == null) {
			throw notFound(key);
		} else if (!o.isa(Obj.NUM)) {
			throw badType(key, "num", o);
		} else {
			return ((Number)o).toDouble();
		}
	}
	
	
	public int getIntEx(Symbol key) {
		Obj o = _dict.getSafe(key);
		
		if (o == null) {
			throw notFound(key);
		} else if (!o.isa(Obj.NUM)) {
			throw badType(key, "num", o);
		} else {
			return ((Number)o).toInt();
		}
	}

	public List getListEx(Symbol key) {
		try {
			return (List)_dict.get(key);
		} catch (ClassCastException e) {
			throw badType(key, "list", _dict.get(key));
		} catch (UndefVarException e2) {
			throw notFound(key);
		}
	}

	public NumberList getNumberListEx(Symbol key) {
		try {
			return Casting.asNumberList(_dict.get(key));
		} catch (ClassCastException e) {
			throw badType(key, "list<num>", _dict.get(key));
		} catch (UndefVarException e2) {
			throw notFound(key);
		}
	}
	
	public String getStringEx(Symbol key) {
		Obj o = _dict.getSafe(key);
		
		if (o == null) {
			throw notFound(key);
		} else if (!o.isa(Obj.STR)) {
			throw badType(key, "str", o);
		} else {
			return o.str();
		}
	}

	public Symbol getSymbolEx(Symbol key) {
		return StaticDictReader.getSymbolEx(_dict, key, _err_name);
	}
	

	public Dict getDictEx(Symbol key) {
		Obj o = _dict.getSafe(key);
		if (o == null) {
			throw notFound(key);
		} else if (!o.isa(Obj.DICT)) {
			throw badType(key, "dict", o);
		} else {
			return (Dict)o;
		}
	}

	public Dict getDict(Symbol key) {
		Obj o = _dict.getSafe(key);
		if (o == null || !o.isa(Obj.DICT)) {
			return null;
		} else {
			return Casting.asDict(o);
		}
	}

	public DictReader getDictReaderEx(Symbol key) {
		Dict d = getDictEx(key);
		return new DictReader(d, get_err_name() + "." + key.name());
	}

	public DictReader getDictReader(Symbol key) {
		Dict d = getDict(key);
		return new DictReader(d == null ? new Dict() : d, get_err_name() + "." + key.name());
	}

	public Color getColor(Symbol key) {
		Obj val = _dict.getSafe(key);
		if (val == null) {
			return null;
		} else {
			return ObjToColor.objToColor(val);
		}
	}
	
	public Color getColor(Symbol key, Color dflt) {
		Color c = null;
		try {
			c = getColor(key); // May return null
		} catch (Exception e) {
			// We should probably notify the user that his input was invalid
			String errName = get_err_name() + "." + key.name();
			StaticData.IO.err().println(errName + ": Failed to read color (will use default). Cause: " + e.getMessage());
		}

		if (c == null) {
			return dflt;
		} else {
			return c;
		}
	}
	
	
	public String getSymString(Symbol key) {
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
	
	public String getSymString(Symbol key, String dflt) {
		final String s = getSymString(key);
		if (s == null) {
			return dflt;
		} else {
			return s;
		}
	}
	
	public float getFloat(Symbol key, float dflt) {
		return (float)(getDouble(key, (double)dflt));
	}
	
	public double getDouble(Symbol key, double dflt) {
		Obj o = _dict.getSafe(key);
		
		if (o == null || !o.isa(Obj.NUM)) {
			return dflt;
		} else {
			return ((Number)o).toDouble();
		}
	}
	
	public int getInt(Symbol key, int dflt) {
		Obj o = _dict.getSafe(key);
		
		if (o == null || !o.isa(Obj.NUM)) {
			return dflt;
		} else {
			return ((Number)o).toInt();
		}
	}
	
	
	public String getString(Symbol key) {
		Obj o = _dict.getSafe(key);
		
		if (o == null || !o.isa(Obj.STR)) {
			return null;
		} else {
			return o.str();
		}
	}
	
	public String getString(Symbol key, String dflt) {
		Obj o = _dict.getSafe(key);

		if (o == null || !o.isa(Obj.STR)) {
			return dflt;
		} else {
			return o.str();
		}
	}
	
	public Symbol getSymbol(Symbol key, Symbol dflt) {
		return StaticDictReader.getSymbol(_dict, key, dflt);
	}

	public boolean getBool(Symbol key, boolean dflt) {
		return StaticDictReader.getBool(_dict, key, dflt);
	}

	public boolean hasKey(Symbol key) {
		return _dict.containsKey(key);
	}
	
	public Pair<Number, Number> getNumberPairEx(Symbol key) {
		List l = this.getListEx(key);
		if (l.length() == 2) {
			NumberList ab = l.toNumberList();
			return new Pair<Number, Number>(ab.get(0), ab.get(1));
		} else {
			throw badType(key, "[::num ::num]::list", l);
		}
	}
	

}
