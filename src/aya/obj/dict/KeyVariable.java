package aya.obj.dict;

import aya.util.LRUCache;
import aya.variable.Variable;

/** Key variables are used for accessing/assigning dict variables */
public class KeyVariable extends Variable {

	static LRUCache<Long, KeyVariable> cache = new LRUCache<>(128);	

	private KeyVariable(long id) {super(id);}
	public KeyVariable(String s) {super(s);}
	
	/** Convert a symbol string to a keyvariable */
	public static KeyVariable fromStr(String data) {
		long id = Variable.encodeString(data);
		return fromID(id);
		
	}	
	
	public static KeyVariable fromID(long id) {
		// Check if it is in the cache
		KeyVariable s = cache.get(id);
		if (s == null) {
			// Not in cache, add it
			s = new KeyVariable(id);
			cache.put(id, s);
		}
		return s;
	}
	

	public String name() {
		return decodeLong(id);
	}
	
	@Override
	public String toString() {
		String name = decodeLong(id);
		if (referanceVariable) {
			return "." + name;
		} else {
			return ".:" + name;
		}
	}
}
