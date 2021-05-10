package aya.util;

import java.awt.Color;

import aya.Aya;
import aya.exceptions.runtime.AyaRuntimeException;
import aya.exceptions.runtime.IndexError;
import aya.exceptions.runtime.UndefVarException;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.numberlist.NumberList;
import aya.obj.number.Number;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;

public class DictReader {
	
	private final Symbol R;
	private final Symbol G;
	private final Symbol B;
	private final Symbol A;
	
	private Dict _dict;
	private String _err_name;
	
	public DictReader(Dict dict) {
		_dict = dict;
		_err_name = "DictReader";
		SymbolTable syms = Aya.getInstance().getSymbols();
		R = syms.getSymbol("r");
		G = syms.getSymbol("g");
		B = syms.getSymbol("b");
		A = syms.getSymbol("a");
	}

	public DictReader(Dict dict, String error_name) {
		this(dict);
		_err_name = error_name;
	}
	
	public void setErrorName(String message) {
		_err_name = message;
	}
	
	public AyaRuntimeException notFound(Symbol key) throws AyaRuntimeException {
		return new IndexError(_dict, key, "Error at " + _err_name);
	}
	
	public AyaRuntimeException badType(Symbol key, String type_expected, Obj got) throws AyaRuntimeException {
		return new ValueError(_err_name + ": Expected type ::" + type_expected +" for key '" + key.name() + "' but got " + got.repr());
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
		
		if (val.isa(Obj.DICT)) {
			DictReader c = new DictReader((Dict)val);
			int r = c.getInt(R, 0);
			int g = c.getInt(G, 0);
			int b = c.getInt(B, 0);
			int a = c.getInt(A, 255);
			try {
				return new Color(r,g,b,a);
			} catch (IllegalArgumentException e) {
				throw new ValueError("Invalid color: " + r + "," + g + "," + b + "," + a);
			}
		} else {
			throw badType(key, "sym", val);
		}
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
		Obj o = _dict.getSafe(key);
		if (o == null) {
			throw notFound(key);
		} else if (!o.isa(Obj.SYMBOL)) {
			throw badType(key, "sym", o);
		} else {
			return (Symbol)o;
		}
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


	public Color getColor(Symbol key) {
		Obj val = _dict.getSafe(key);
		
		if (val == null) {
			return null;
		} else {
			if ( val.isa(Obj.DICT)) {
				DictReader c = new DictReader((Dict)val);
				int r = c.getInt(R, 0);
				int g = c.getInt(G, 0);
				int b = c.getInt(B, 0);
				int a = c.getInt(A, 255);
				try {
					return new Color(r,g,b,a);
				} catch (IllegalArgumentException e) {
					throw new ValueError("Invalid color: " + r + "," + g + "," + b + "," + a);
				}
			} else if (val.isa(Obj.STR)) {
				return ColorFactory.valueOf(val.str());
			} else {
				return null;
			}
		}
	}
	
	public Color getColor(Symbol key, Color dflt) {
		Color c = null;
		try {
			c = getColor(key); // May return null
		} catch (ValueError e) {
			c = null;
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
		Obj o = _dict.getSafe(key);
		
		if (o == null || !o.isa(Obj.SYMBOL)) {
			return dflt;
		} else {
			return (Symbol)o;
		}
	}

	public boolean getBool(Symbol key, boolean dflt) {
		Obj o = _dict.getSafe(key);
		
		if (o == null) {
			return dflt;
		} else {
			return o.bool();
		}
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
			throw new ValueError("plot.ylim: expected list of length 2. got:\n" + l.str());
		}
	}
	

}
