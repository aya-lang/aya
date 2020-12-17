package aya.util;

import java.awt.Color;

import aya.exceptions.AyaRuntimeException;
import aya.exceptions.UndefVarException;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Number;
import aya.obj.symbol.Symbol;
import aya.variable.Variable;

public class DictReader {
	
	private final long R = Variable.encodeString("r");
	private final long G = Variable.encodeString("g");
	private final long B = Variable.encodeString("b");
	private final long A = Variable.encodeString("a");
	
	private Dict _dict;
	private String _err_name;
	
	public DictReader(Dict dict) {
		_dict = dict;
		_err_name = "DictReader";
	}

	public DictReader(Dict dict, String error_name) {
		_dict = dict;
		_err_name = error_name;
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
	
	public double[] getDoubleArrayEx(long key) {
		Obj arr = _dict.getSafe(key);
		
		if (arr == null) {
			throw notFound(key);
		} if (arr.isa(Obj.LIST)) {
			return ((List)arr).toNumberList().todoubleArray();
		} else {
			throw badType(key, "list<num>", arr);
		}
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
	
	public Color getColorEx(long key) {
		Obj val = _dict.getSafe(key);
		if (val == null) throw notFound(key);
		
		if (val.isa(Obj.DICT)) {
			DictReader c = new DictReader((Dict)val);
			int r = c.getInt(R, 0);
			int g = c.getInt(G, 0);
			int b = c.getInt(B, 0);
			int a = c.getInt(A, 255);
			try {
				return new Color(r,g,b,a);
			} catch (IllegalArgumentException e) {
				throw new AyaRuntimeException("Invalid color: " + r + "," + g + "," + b + "," + a);
			}
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

	public List getListEx(long key) {
		try {
			return (List)_dict.get(key);
		} catch (ClassCastException e) {
			throw badType(key, "list", _dict.get(key));
		} catch (UndefVarException e2) {
			throw notFound(key);
		}
	}

	public NumberList getNumberListEx(long key) {
		try {
			return Casting.asNumberList(_dict.get(key));
		} catch (ClassCastException e) {
			throw badType(key, "list<num>", _dict.get(key));
		} catch (UndefVarException e2) {
			throw notFound(key);
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

	public Symbol getSymbolEx(long key) {
		Obj o = _dict.getSafe(key);
		if (o == null) {
			throw notFound(key);
		} else if (!o.isa(Obj.SYMBOL)) {
			throw badType(key, "sym", o);
		} else {
			return (Symbol)o;
		}
	}
	
	public Color getColor(long key) {
		Obj val = _dict.getSafe(key);
		
		if (val != null && val.isa(Obj.DICT)) {
			DictReader c = new DictReader((Dict)val);
			int r = c.getInt(R, 0);
			int g = c.getInt(G, 0);
			int b = c.getInt(B, 0);
			int a = c.getInt(A, 255);
			try {
				return new Color(r,g,b,a);
			} catch (IllegalArgumentException e) {
				throw new AyaRuntimeException("Invalid color: " + r + "," + g + "," + b + "," + a);
			}
		} else {
			return null;
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
	
	public float getFloat(long key, float dflt) {
		return (float)(getDouble(key, (double)dflt));
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
	
	public Symbol getSymbol(long key, Symbol dflt) {
		Obj o = _dict.getSafe(key);
		
		if (o == null || !o.isa(Obj.SYMBOL)) {
			return dflt;
		} else {
			return (Symbol)o;
		}
	}

	public boolean getBool(long key, boolean dflt) {
		Obj o = _dict.getSafe(key);
		
		if (o == null) {
			return dflt;
		} else {
			return o.bool();
		}
	}

}
