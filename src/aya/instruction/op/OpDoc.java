package aya.instruction.op;

import java.util.ArrayList;

import aya.obj.dict.Dict;
import aya.obj.list.Str;
import aya.obj.symbol.Symbol;

public class OpDoc {
	
	public static final char STD = ' ';
	public static final char DOT = '.';
	public static final char COLON = ':';
	public static final char MISC = 'M';
	
	private class OpDesc {
		private String types;
		private String desc;
		
		public OpDesc(String types, String desc) {
			this.types = types;
			this.desc = desc;
		}
		
		public String getType() {
			return types;
		}
		
		public String str() {
			return this.types + ": " + this.desc;
		}
	}

	
	ArrayList<OpDesc> descs;
	private String _name;
	private String _overload;
	private int _type;
	private boolean _vectorized = false;
	
	public OpDoc(char type, String name) {
		descs = new ArrayList<OpDesc>();
		_overload = null;
		_type = type;
		_name = name;
	}
	
	public void vect() {
		_vectorized = true;
	}
	
	
	public void ovrld(String... o) {
		_overload = "";
		for (String s : o) {
			_overload += s + "/";
		}
		_overload = _overload.substring(0, _overload.length()-1);
	}
	
	public void desc(String type, String desc) {
		descs.add(new OpDesc(type, desc));
	}
	
	public int type() {
		return _type;
	}
	
	private static final Symbol STD_SYM = Symbol.fromStr("std");
	private static final Symbol DOT_SYM = Symbol.fromStr("dot");
	private static final Symbol COLON_SYM = Symbol.fromStr("colon");
	private static final Symbol MISC_SYM = Symbol.fromStr("misc");
	
	public Symbol typeSymbol() {
		switch (_type) {
		case STD: return STD_SYM;
		case DOT: return DOT_SYM;
		case COLON: return COLON_SYM;
		case MISC: return MISC_SYM;
		default: throw new RuntimeException("OpDoc.typeSymbol: op type unknown");
		}
			
	}
	
	public String typeString() {
		String s = "(";
		for (OpDesc d : descs) {
			s += d.getType() + '|';
		}
		// Remove the trailing '|'
		return s.substring(0, s.length()-1) + ")";
	}

	public void setOverloadNames(ArrayList<String> names) {
		_overload = "";
		for (String s : names) {
			_overload += s + "/";
		}
		_overload = _overload.substring(0, _overload.length()-1);
	}
	
	public String infoString(String frontPad) {
		StringBuilder sb =  new StringBuilder();
		for (OpDesc d : descs) {
			sb.append(frontPad).append(d.str()).append('\n');
		}
		//Remove trailing \n
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(_name);
		sb.append(" " + typeString());
		
		if (_vectorized) {
			sb.append(" V");
		}
		
		sb.append('\n');
		sb.append(infoString("  "));
		
		if (_overload != null) {
			sb.append("\n  overloadable: ").append(_overload);
		}
		
		return sb.toString();
	}
	
	public Dict toDict() {
		Dict d = new Dict();
		
		for (OpDesc desc : descs) {
			String[] strs = desc.types.split("\\|");
			for (String s : strs) {
				d.set(s, new Str(desc.desc));
			}
		}
		
		return d;
	}

	public String opName() {
		return _name;
	}

}
	
