package aya.util;

import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.numberlist.NumberList;

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
}
