package aya.util;

import aya.obj.Obj;
import aya.obj.block.StaticBlock;
import aya.obj.character.Char;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.Str;
import aya.obj.list.numberlist.NumberList;
import aya.obj.symbol.Symbol;

public class Casting {
	public static NumberList asNumberList(Obj o) {
		return ((List)o).toNumberList();
	}
	
	public static aya.obj.number.Number asNumber(Obj o) {
		return (aya.obj.number.Number)o;
	}
	
	public static Dict asDict(Obj o) {
		return (Dict)o;
	}

	public static List asList(Obj o) {
		return (List)o;
	}

	public static Char asChar(Obj o) {
		return (Char)o;
	}
	
	public static Str asStr(Obj o) {
		return List.asStr(asList(o));
	}
	
	public static StaticBlock asStaticBlock(Obj o) {
		return (StaticBlock)o;
	}

	public static Symbol asSymbol(Obj o) {
		return (Symbol)o;
	}
}
