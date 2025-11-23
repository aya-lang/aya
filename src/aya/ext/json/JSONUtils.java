package aya.ext.json;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import aya.exceptions.ex.AyaException;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.util.Pair;

public class JSONUtils {
	
	public static class JSONParams {
		private static JSONParams default_encode = null;
		private static JSONParams default_decode = null;
		private static Symbol FALSE_SYM = SymbolTable.getSymbol("__json_false");
		private static Symbol TRUE_SYM =  SymbolTable.getSymbol("__json_true");
		private static Symbol NULL_SYM =  SymbolTable.getSymbol("__json_null");
		
		public JSONParams(Obj obj_null, Obj obj_true, Obj obj_false, boolean parse_symbol) {
			this.obj_null = obj_null;
			this.obj_true = obj_true;
			this.obj_false = obj_false;
			this.parse_symbol = parse_symbol;
			
		}
		
		public static JSONParams getDefaultEncode() {
			if (default_encode == null) {
				default_encode = new JSONParams(
						NULL_SYM,
						TRUE_SYM,
						FALSE_SYM,
						false);
			}
			return default_encode;
		}

		public static JSONParams getDefaultDecode() {
			if (default_decode == null) {
				default_decode = new JSONParams(
						NULL_SYM,
						Num.ONE,
						Num.ZERO,
						false);
			}
			return default_decode;
		}

		private Obj obj_null;
		private Obj obj_true;
		private Obj obj_false;
		private boolean parse_symbol;
	}
	
	private static class VisitedChecker {
		private Set<Obj> _visited;
		public VisitedChecker() {
			_visited = new HashSet<Obj>();
		}
		public void push(Obj o) {
			// Only add container types
			_visited.add(o);
		}
		public void pop(Obj o) {
			_visited.remove(o);
		}
		public boolean hasVisited(Obj o) {
			return _visited.contains(o);
		}
		
	}
	
	
	private static Object readJSON(String str) {
		JSONTokener tokener = new JSONTokener(str);
		return tokener.nextValue();
	}
	
	private static JSONObject readXML(String str) {
		JSONObject json = XML.toJSONObject(str);
		return json;
	}
	
	private static Obj toObj(Object object, JSONParams params) throws AyaException {
		if (JSONObject.NULL.equals(object)) {
			return params.obj_null;
		} else if (object instanceof JSONObject) {
			JSONObject json = (JSONObject)object;
			Dict d = new Dict();
			for (String key : json.keySet()) {
				Symbol sym = SymbolTable.getSymbol(key);
				if (json.isNull(key)) {
					d.set(sym, params.obj_null);
				} else {
					final Object o = json.get(key);
					d.set(sym, toObj(o, params));
				}
			}
			return d;
		} else if (object instanceof JSONArray) {
			JSONArray jarr = (JSONArray)object;
			ArrayList<Obj> arr = new ArrayList<Obj>(jarr.length());
			for (int i = 0; i < jarr.length(); i++) {
				if (jarr.isNull(i)) {
					arr.add(params.obj_null);
				} else {
					arr.add(toObj(jarr.get(i), params));
				}
			}
			return new List(arr);
		} else if (object instanceof Integer) {
			return Num.fromInt((Integer)object);
		} else if (object instanceof Number) {
			return new Num(((Number)object).doubleValue());
		} else if (object instanceof String) {
			String str = (String)object;
			if (params.parse_symbol && str.startsWith("::")) {
				String name = str.substring(2);
				if (SymbolTable.isBasicSymbolString(name)) {
					return SymbolTable.getSymbol(name);
				} else {
					return List.fromString(str);
				}
			} else {
				return List.fromString(str);
			}
		} else if (object instanceof Boolean) {
			return (Boolean)object ? params.obj_true : params.obj_false;
		} else {
			throw new ValueError("Unsupported type: " + object.toString());
		}
	}
	
	private static Object toJSON(Obj o, JSONParams params, VisitedChecker vc) {
		if (o.equiv(params.obj_null)) {
			return JSONObject.NULL;
		} else if (o.equiv(params.obj_false)) {
			return false;
		} else if (o.equiv(params.obj_true)) {
			return true;
		} else if (o.isa(Obj.DICT)) {
			JSONObject out = new JSONObject();
			Dict d = (Dict)o;
			vc.push(d);
			ArrayList<Pair<Symbol, Obj>> items = d.items();
			for (Pair<Symbol, Obj> e : items) {
				if (vc.hasVisited(e.second())) {
					throw new ValueError("JSON: Circular reference detected when serializing json object. key: " + e.first());
				} else {
					out.put(e.first().name(), toJSON(e.second(), params, vc));
				}
			}
			vc.pop(d);
			return out;
		} else if (o.isa(Obj.LIST) && !o.isa(Obj.STR)) {
			JSONArray arr = new JSONArray();
			List l = (List)o;
			vc.push(l);
			for (int i = 0; i < l.length(); i++) {
				Obj e = l.getExact(i);
				if (vc.hasVisited(e)) {
					throw new ValueError("JSON: Circular reference detected when serializing list at index " + i);
				} else {
					arr.put(toJSON(e, params, vc));
				}
			}
			vc.pop(l);
			return arr;
		} else if (o.isa(Obj.NUMBER)) {
			double n = ((aya.obj.number.Number)o).toDouble();
			return n;
		} else if (o.isa(Obj.STR) || o.isa(Obj.CHAR)) {
			return o.str();
		} else if (o.isa(Obj.SYMBOL)) {
			Symbol sym = (Symbol)o;
			if (params.parse_symbol) {
				return sym.str();
			} else {
				return sym.name();
			}
		} else {
			throw new ValueError("Unable to convert object to json: " + o.repr());
		}
	}
	
	public static String encodeJSON(Obj o, JSONParams params) {
		VisitedChecker vc = new VisitedChecker();
		return toJSON(o, params, vc).toString();
	}
	
	public static Obj decodeJSON(String s, JSONParams params) {
		try {
			return toObj(readJSON(s), params);
		} catch (JSONException e) {
			throw new ValueError(e.getLocalizedMessage());
		} catch (AyaException e) {
			throw new ValueError(e.getMessage());
		}
	}

	public static Obj decodeXML(String s, JSONParams params) {
		try {
			return toObj(readXML(s), params);
		} catch (JSONException e) {
			throw new ValueError(e.getLocalizedMessage());
		} catch (AyaException e) {
			throw new ValueError(e.getMessage());
		}
	}
	
	
	
	public static void main(String args[]) {
		String example = "{\n"
				+ "  \"id\": \"file\",\n"
				+ "  \"value\": \"File\",\n"
				+ "  \"popup\": {\n"
				+ "    \"menuitem\": [\n"
				+ "      {\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},\n"
				+ "      {\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},\n"
				+ "      {\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}\n"
				+ "    ]\n"
				+ "  },\n"
				+ "  \"width\": 500,\n"
				+ "  \"Hello world!\": 200,\n"
				+ "  \"hash\": 1234567898765432345679876,\n"
				+ "  \"deci\": 23789456789987545678987654.4567896434567897654345678865456789,\n"
				+ "  \"arr\": [1,2,3,4],\n"
				+ "  \"isgood\": false,\n"
				+ "  \"other\": null\n"
				+ "}";
		
		Object json = readJSON(example);
		try {
			Obj o  = toObj(json, JSONUtils.JSONParams.getDefaultDecode());
			System.out.println(o.repr());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
 

	}

}
