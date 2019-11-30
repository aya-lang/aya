package aya.util.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.apfloat.Apfloat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import aya.exceptions.AyaException;
import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.GenericList;
import aya.obj.list.Str;
import aya.obj.number.Num;

public class JSONDecoder {
	
	private static JSONObject readJSON(String str) {
		JSONTokener tokener = new JSONTokener(str);
		JSONObject json = new JSONObject(tokener);
		return json;
	}
	
	private static JSONObject readXML(String str) {
		JSONObject json = XML.toJSONObject(str);
		return json;
	}
	
	private static Obj toObj(Object object) throws AyaException {
		if (object instanceof JSONObject) {
			JSONObject json = (JSONObject)object;
			Dict d = new Dict();
			for (String key : json.keySet()) {
				final Object o = json.get(key);
				d.set(key, toObj(o));
			}
			return d;
		} else if (object instanceof JSONArray) {
			JSONArray jarr = (JSONArray)object;
			ArrayList<Obj> arr = new ArrayList<Obj>(jarr.length());
			for (Object o : jarr) {
				arr.add(toObj(o));
			}
			return new GenericList(arr).promote();
		} else if (object instanceof Integer) {
			return new Num((Integer)object);
		} else if (object instanceof Double) {
			return new Num((Double)object);
		} else if (object instanceof String) {
			return new Str((String)object);
		} else if (object instanceof Boolean) {
			return (Boolean)object ? Num.ONE : Num.ZERO;
		} else {
			throw new AyaException("Unsupported type: " + object.toString());
		}
	}
	
	public static Obj decodeJSON(String s) {
		try {
			return toObj(readJSON(s));
		} catch (JSONException e) {
			throw new AyaRuntimeException(e.getLocalizedMessage());
		} catch (AyaException e) {
			throw new AyaRuntimeException(e.getMessage());
		}
	}

	public static Obj decodeXML(String s) {
		try {
			return toObj(readXML(s));
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
				+ "  \"isgood\": false\n"
				+ "}";
		
		JSONObject json = readJSON(example);
		try {
			Obj o  = toObj(json);
			System.out.println(o.repr());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
 
		
	}

}
