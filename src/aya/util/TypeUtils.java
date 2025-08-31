package aya.util;

import static aya.obj.Obj.DICT;

import java.util.ArrayList;
import java.util.HashMap;

import aya.eval.BlockEvaluator;
import aya.eval.ExecutionContext;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

public class TypeUtils {
	
	public static Dict TYPE = new Dict();
	public static Dict TYPE_META = new Dict();
	public static Dict DICT_T = new Dict();
	
	private static HashMap<Symbol, Dict> TYPE_CACHE = new HashMap<Symbol, Dict>();

	private static class TypeDict {
		public static boolean isAny(Dict d) {
			return StaticDictReader.getBool(d, SymbolConstants.ANY, false);
		}
		
		public static boolean isUnion(Dict d) {
			return StaticDictReader.getBool(d, SymbolConstants.UNION, false);

		}
		
		public static boolean isClassType(Obj value, Dict type) {
			//return Obj.isInstance(value, SymbolConstants.OBJECT)
			return isClassOrStruct(value)
					&& Casting.asDict(value).containsKey(SymbolConstants.__TYPE__)
					&& type.containsKey(SymbolConstants.NAME)
					&& Casting.asSymbol(type.get(SymbolConstants.NAME)).id() == SymbolConstants.TYPE.id();
		}
	
		public static boolean hasInner(Dict d) {
			return d.hasKey(SymbolConstants.INNER);
		}
		
		public static List getInner(Dict d) {
			final Obj o = d.get(SymbolConstants.INNER);
			if (o.isa(Obj.LIST)) {
				return Casting.asList(o);
			} else {
				throw new TypeError("inner type must be a list. Got " + d.repr());
			}
		}
		
		public static Dict getInnerAt(List inner, int index) {
			final Obj inner_type = inner.getExact(index);
			if (inner_type.isa(Obj.DICT)) {
				return Casting.asDict(inner_type);
			} else {
				throw new TypeError("All inner types must be dicts/types. Got " + inner.repr());
			}
		}
		
		public static boolean namesMatch(Dict type, Dict value_type) {
			Symbol type_name       = StaticDictReader.getSymbol(type,       SymbolConstants.NAME, null);
			Symbol value_type_name = StaticDictReader.getSymbol(value_type, SymbolConstants.NAME, null);
			if (type_name != null && value_type_name != null && type_name.id() == value_type_name.id()) {
				return true;
			} else {
				return false;
			}
		}
		
		public static boolean hasTypeCheckOverride(Dict type) {
			// Type has an override if it has a outer object with __type_check__
			return (type.hasKey(SymbolConstants.OUTER) 
					&& StaticDictReader.getDictEx(type, SymbolConstants.OUTER, "outer must be a dict").hasKey(SymbolConstants.__TYPE_CHECK__));
		}
	}
	
	
	
	
	static {
		TYPE_META.set(SymbolConstants.ANY, Num.ZERO);
		TYPE_META.set(SymbolConstants.UNION, Num.ZERO);
		TYPE_META.set(SymbolConstants.__TYPE__, TYPE);
		
		TYPE.set(SymbolConstants.NAME, SymbolConstants.TYPE);
		TYPE.setMetaTable(TYPE_META);		
	}
	

	// TODO: Change after struct macro is changed
	public static boolean isClassOrStruct(Obj obj) {
		if (obj.isa(DICT)) {			
			// Check type chain
			Dict cls = Casting.asDict(obj);
			while (cls.hasMetaTable()) {
				Obj cls_type = cls.getFromMetaTableOrNull(SymbolConstants.KEYVAR_TYPE);
				if (cls_type.isa(DICT)) {
					Obj cls_name = Casting.asDict(cls_type).getSafe(SymbolConstants.NAME);
					if (cls_name != null && cls_name.equiv(SymbolConstants.OBJECT)) {
						return true;
					}
				}
				
				Obj next_cls = cls.get(SymbolConstants.KEYVAR_META);
				if (next_cls.isa(DICT)) {
					cls = Casting.asDict(next_cls);
				} else {
					break;
				}
			}
		}
		
		// ::object not found in type tree
		return false;
		
	}
	
	
	public static Dict makeType(List inner, Dict type_dict) {
		
		// Check if anything inside the inner is a list
		boolean should_recurse = false;
		if (inner.length() > 0) {
			for (int i = 0; i < inner.length(); i++) {
				if (inner.getExact(i).isa(Obj.LIST)) {
					should_recurse = true;
					break;
				}
			}
		}
		
		if (should_recurse) {
			List out = new List();
			List buf = null;
			// If inner has lists, recursively create types
			for (int i = 0; i < inner.length(); i++) {
				Obj x = inner.getExact(i);
				
				// If this is a list, save it to the buf
				if (x.isa(Obj.LIST)) {
					if (buf != null) {
						// ERROR, two lists in a row
					} else {
						buf = Casting.asList(x);
					}
				} else {
					if (buf == null) {
						// previous entry was not a list, add as normal
						out.mutAdd(x);
					} else {
						// previous entry was a list, recurse
						out.mutAdd(makeType(buf, Casting.asDict(x)));
						buf = null;
					}
				}
			}
			
			inner = out;
		}
		
		DictReader type = new DictReader(type_dict);
		
		// This is due to the way types are handled in the struct macro, this should be changed at some point
		Dict outer = null;
		//if (Obj.isInstance(type_dict, SymbolConstants.OBJECT)) {
		if (isClassOrStruct(type_dict)) {
			outer = Casting.asDict(type_dict);
			type_dict = type.getDict(SymbolConstants.__TYPE__);
			type = new DictReader(type_dict);
		}
		
		// Checks for built in types
		Symbol name = type.getSymbolEx(SymbolConstants.NAME);
		if (name.id() == SymbolConstants.LIST.id() || name.id() == SymbolConstants.DICT.id()) {
			// Assert inner has length 1
			if (inner.length() != 1) {
				throw new ValueError("Inner type for list or dict must have length 1");
			}
		} else if (name.id() == SymbolConstants.UNION.id()) {
			if (inner.length() == 1) {
				// If it is a union type and the inner list is 1, just return the inner type
				return Casting.asDict(inner.getExact(0));
			} else if (inner.length() == 0) {
				throw new ValueError("Inner type for union must have length > 0");
			}
		}
		
		// Return the final assembled type
		Dict result = new Dict();
		result.set(SymbolConstants.INNER, inner);
		result.setMetaTable(type_dict);
		if (outer != null) {
			result.set(SymbolConstants.OUTER, outer);
		}
		
		return result;
	}
	
	public static boolean isInstance(Obj value, Dict type, ExecutionContext ctx) {
		
	    // type may be a ::type or an ::object (i.e. point)
	    // the struct macro should be update so a point has type type
	    //   and an instance of a point has type point
		//if (Obj.isInstance(type, SymbolConstants.OBJECT)) {
		if (isClassOrStruct(type)) {
			type = StaticDictReader.getDictEx(type, SymbolConstants.__TYPE__, "Object must have a __type__ field of type dict");
	    }
	
	
		// Get the type of the value
		Dict value_type = null;
		if (value.isa(Obj.DICT)) {
			Dict value_dict = Casting.asDict(value);
			if (value_dict.containsKey(SymbolConstants.__TYPE__)) {
				value_type = StaticDictReader.getDictEx(value_dict, SymbolConstants.__TYPE__, "__type__ must be a dict");
			} else {
				value_type = getBuiltinType(Obj.DICT);
			}
		} else {
			value_type = getBuiltinType(value);
		}
		
		// Checks
		// 0: Any
		if (TypeDict.isAny(type)) {
			return true;
		}
		
		// 1: Special case for classes (TODO change)
		else if (TypeDict.isClassType(value, type)) {
			return true;
		}
		
		// 2: Union
		else if (TypeDict.isUnion(type)) {
			if (TypeDict.hasInner(type)) {
				List inner = TypeDict.getInner(type);
				// Return true if any type in the union matches
				for (int i = 0; i < inner.length(); i++) {
					if (isInstance(value, TypeDict.getInnerAt(inner, i), ctx)) {
						return true;
					}
				}
				// No type matched
				return false;
			} else {
				throw new ValueError("Union set but no inner type is set: " + type.repr());
			}
		}
		
		
		// 3: Container types
		//    First check that the names match, then check the contained type
		else if (TypeDict.namesMatch(type,  value_type)) {
			if (TypeDict.hasInner(type)) {
				// Type has a inner type specified, need to check that too
				List inner = TypeDict.getInner(type);
				Symbol value_type_name = StaticDictReader.getSymbolEx(value_type, SymbolConstants.NAME, "Type name must be a symbol");

				// List
				// Return true if all the elements inside the list match the type
				if (value_type_name.id() == SymbolConstants.LIST.id()) {
					Dict inner_type = TypeDict.getInnerAt(inner, 0);
					
					// TODO: Add optimization for number lists
					List value_list = Casting.asList(value);
					for (int i = 0; i < value_list.length(); i++) {
						if (!isInstance(value_list.getExact(i), inner_type, ctx)) {
							return false; // type mismatch found
						}
					}
					
					// No mismatches found
					return true;
				}
				
				// Custom type with type_check override
				// Custom types can override the __type_check__ function
				else if (TypeDict.hasTypeCheckOverride(type)) {
					// Call it
					// value type.inner type.outer.__type_check__
					BlockEvaluator be = ctx.createEvaluator();
					Dict outer = StaticDictReader.getDictEx(type, SymbolConstants.OUTER, "type must have outer key");
					// Note: this may have __pushself__ set which will leave one extra instance of the 
					//       class on the stack (see note below)
					be.callVariable(outer, SymbolConstants.__TYPE_CHECK__, value, inner, outer);
					be.eval();
					Obj res = be.pop();
					// be.pop(); // pop self (from __pushself)__ is not needed since it will get thrown
					//              away anyway
					if (be.getStack().size() > 1) {
						throw new ValueError("stuff left on stack!");
					}
					if (res.isa(Obj.NUM)) {
						return res.bool();
					} else {
						throw new TypeError("Result of __type_check__ must be a numeric. Got " + res.repr());
					}
				}						
				
				// Normal dict
				// Return true if all values inside the dict match the type
				else if (value_type_name.id() == SymbolConstants.DICT.id()) {
					Dict inner_type = TypeDict.getInnerAt(inner, 0);
					ArrayList<Pair<Symbol, Obj>> items = Casting.asDict(value).items();
					for (int i = 0; i < items.size(); i++) {
						if (items.get(i).first().id() == SymbolConstants.KEYVAR_META.id()) {
							// Skip __meta__
							continue;
						} else if (!isInstance(items.get(i).second(), inner_type, ctx)) {
							return false; // type mismatch found
						}
					}
					// No mismatches found
					return true;
				}
				
				else {
					return false;
				}
				
				
			} else {
				// No inner type specified
				// Since the names match, this is a match
				return true;
			}
		} else {
			// Nothing above matched, types do not match
			return false;
		}
	}
	

	public static Dict makeType(Symbol type_name) {
		if (!TYPE_CACHE.containsKey(type_name)) {
			Dict t = new Dict();
			t.set(SymbolConstants.NAME, type_name);
			t.setMetaTable(TYPE_META);
			TYPE_CACHE.put(type_name, t);
		}

		return TYPE_CACHE.get(type_name);
	}
	
	private static Dict getBuiltinType(Obj value) {
		return getBuiltinType(value.type());
	}
	
	private static Dict getBuiltinType(byte type) {
		return makeType(Obj.IDToSym(type));
	}


	//* Get the type of the object
	public static Dict getType(Obj a) {		
		if (a.isa(Obj.DICT)) {
			Obj type = Casting.asDict(a).getFromMetaTableOrNull(SymbolConstants.__TYPE__);
			if (type == null) {
				return getBuiltinType(Obj.DICT);
			} else if (type.isa(Obj.DICT)) {
				return Casting.asDict(type);
			} else {
				throw new ValueError("__type__ must be a dict with a ::name field");
			}
		} else {
			return getBuiltinType(a);
		}
		
	}

}
