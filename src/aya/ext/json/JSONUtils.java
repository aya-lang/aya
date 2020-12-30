package aya.ext.json;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import aya.Aya;
import aya.exceptions.AyaException;
import aya.exceptions.AyaRuntimeException;
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
		private static Symbol FALSE_SYM = Aya.getInstance().getSymbols().getSymbol("__json_false");
		private static Symbol TRUE_SYM =  Aya.getInstance().getSymbols().getSymbol("__json_true");
		
		public JSONParams(Obj obj_null, Obj obj_true, Obj obj_false, boolean parse_symbol) {
			this.obj_null = obj_null;
			this.obj_true = obj_true;
			this.obj_false = obj_false;
			this.parse_symbol = parse_symbol;
			
		}
		
		public static JSONParams getDefaultEncode() {
			if (default_encode == null) {
				default_encode = new JSONParams(
						Aya.getInstance().getVars().OBJ_NIL,
						TRUE_SYM,
						FALSE_SYM,
						false);
			}
			return default_encode;
		}

		public static JSONParams getDefaultDecode() {
			if (default_decode == null) {
				default_decode = new JSONParams(
						Aya.getInstance().getVars().OBJ_NIL,
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
	
	
	private static JSONObject readJSON(String str) {
		JSONTokener tokener = new JSONTokener(str);
		JSONObject json = new JSONObject(tokener);
		return json;
	}
	
	private static JSONObject readXML(String str) {
		JSONObject json = XML.toJSONObject(str);
		return json;
	}
	
	private static Obj toObj(Object object, JSONParams params) throws AyaException {
		if (object == null) {
			return params.obj_null;
		} else if (object instanceof JSONObject) {
			JSONObject json = (JSONObject)object;
			Dict d = new Dict();
			for (String key : json.keySet()) {
				if (json.isNull(key)) {
					d.set(key, Aya.getInstance().getVars().OBJ_NIL);
				} else {
					final Object o = json.get(key);
					d.set(key, toObj(o, params));
				}
			}
			return d;
		} else if (object instanceof JSONArray) {
			JSONArray jarr = (JSONArray)object;
			ArrayList<Obj> arr = new ArrayList<Obj>(jarr.length());
			for (int i = 0; i < jarr.length(); i++) {
				if (jarr.isNull(i)) {
					arr.add(Aya.getInstance().getVars().OBJ_NIL);
				} else {
					arr.add(toObj(jarr.get(i), params));
				}
			}
			return new List(arr);
		} else if (object instanceof Integer) {
			return new Num((Integer)object);
		} else if (object instanceof Double) {
			return new Num((Double)object);
		} else if (object instanceof String) {
			String str = (String)object;
			if (params.parse_symbol && str.startsWith("::")) {
				String name = str.substring(2);
				if (SymbolTable.isBasicSymbolString(name)) {
					return Aya.getInstance().getSymbols().getSymbol(name);
				} else {
					return List.fromString(str);
				}
			} else {
				return List.fromString(str);
			}
		} else if (object instanceof Boolean) {
			return (Boolean)object ? params.obj_true : params.obj_false;
		} else {
			throw new AyaException("Unsupported type: " + object.toString());
		}
	}
	
	private static Object toJSON(Obj o, JSONParams params, VisitedChecker vc) {
		if (o.equiv(params.obj_null)) {
			return null;
		} else if (o.equiv(params.obj_false)) {
			return false;
		} else if (o.equiv(params.obj_true)) {
			return true;
		} else if (o.isa(Obj.DICT)) {
			JSONObject out = new JSONObject();
			Dict d = (Dict)o;
			vc.push(d);
			ArrayList<Pair<String, Obj>> items = d.items();
			for (Pair<String, Obj> e : items) {
				if (vc.hasVisited(e.second())) {
					throw new AyaRuntimeException("JSON: Circular reference detected when serializing json object. key: " + e.first());
				} else {
					out.put(e.first(), toJSON(e.second(), params, vc));
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
					throw new AyaRuntimeException("JSON: Circular reference detected when serializing list at index " + i);
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
				return "::" + sym.name();
			} else {
				return sym.name();
			}
		} else {
			throw new RuntimeException("Unable to convert object to json: " + o.repr());
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
			throw new AyaRuntimeException(e.getLocalizedMessage());
		} catch (AyaException e) {
			throw new AyaRuntimeException(e.getMessage());
		}
	}

	public static Obj decodeXML(String s, JSONParams params) {
		try {
			return toObj(readXML(s), params);
		} catch (JSONException e) {
			throw new AyaRuntimeException(e.getLocalizedMessage());
		} catch (AyaException e) {
			throw new AyaRuntimeException(e.getMessage());
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
		
		JSONObject json = readJSON(example);
		try {
			Obj o  = toObj(json, JSONUtils.JSONParams.getDefaultDecode());
			System.out.println(o.repr());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
 
		
	}

}
