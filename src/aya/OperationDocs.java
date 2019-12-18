package aya;

import java.util.ArrayList;
import java.util.HashMap;

import aya.instruction.op.OpDoc;
import aya.obj.Obj;
import aya.obj.list.GenericList;
import aya.obj.list.List;

public class OperationDocs {
	private static HashMap<String, OpDoc> _docs = new HashMap<String, OpDoc>();
	private static HashMap<Integer, List> _allOpsCache;
	private static OpDoc NONE = new OpDoc(' ', "");
	
	public static void add(OpDoc doc) {
		_docs.put(doc.opName(), doc);
	}
	
	public static OpDoc get(String opName) {
		OpDoc doc = _docs.get(opName);
		if (doc == null) {
			return NONE;
		} else {
			return doc;
		}
	}
	
	/** Given the type of operator, return a list of dicts containing
	 * information about them. Cache the list of dicts
	 * 
	 * @param type
	 * @return
	 */
	public static List asDicts(int type) {
		if (_allOpsCache == null) _allOpsCache = new HashMap<Integer, List>();
		
		if (_allOpsCache.get(type) == null) {
			ArrayList<Obj> dicts  = new ArrayList<Obj>();
			for (HashMap.Entry<String, OpDoc> e : _docs.entrySet()) {
				if (e.getValue().type() == type) {
					dicts.add(e.getValue().toDict());
				}
			}
			List l = new GenericList(dicts);
			_allOpsCache.put(type, l);
			return l;
		} else {
			return _allOpsCache.get(type);
		}
	}

	public static ArrayList<String> getAllOpDescriptions() {
		ArrayList<String> out = new ArrayList<String>();
		for (HashMap.Entry<String, OpDoc> e : _docs.entrySet()) {
			out.add(e.getValue().toString());
		}
		return out;
	}
}
