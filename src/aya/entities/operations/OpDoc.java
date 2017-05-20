package aya.entities.operations;

import java.util.ArrayList;

import aya.obj.dict.Dict;
import aya.obj.list.Str;
import aya.obj.number.Num;

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
	
	public String typeString() {
		String s = "(";
		for (OpDesc d : descs) {
			s += d.getType() + '|';
		}
		// Remove the trailing '|'
		return s.substring(0, s.length()-1) + ")";
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
		
		d.set("name", new Str(_name));
		d.set("types", new Str(typeString()));
		d.set("info", new Str(infoString("")));
		d.set("vect", _vectorized ? Num.ONE : Num.ZERO);
		
		if (_overload != null) {
			d.set("oveload", new Str(_overload));
		}
		
		return d;
	}

	public String opName() {
		return _name;
	}
}
	
