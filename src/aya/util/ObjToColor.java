package aya.util;

import java.awt.Color;

import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.SymbolConstants;

public class ObjToColor {

	public static Color objToColor(Obj val) {
		if ( val.isa(Obj.DICT)) {
			DictReader c = new DictReader((Dict)val);
			int r = c.getInt(SymbolConstants.R, 0);
			int g = c.getInt(SymbolConstants.G, 0);
			int b = c.getInt(SymbolConstants.B, 0);
			int a = c.getInt(SymbolConstants.A, 255);
			try {
				return new Color(r,g,b,a);
			} catch (IllegalArgumentException e) {
				throw new ValueError("Invalid color: " + r + "," + g + "," + b + "," + a);
			}
		} else if (val.isa(Obj.STR)) {
			return ColorFactory.valueOf(val.str());
		} else {
			return null;
		}
	}

	public static Color objToColorEx(Obj val, String message) {
        final Color c;
        try {
            c = objToColor(val);
        } catch (Exception e) {
            throw new ValueError("Error '" + e.getMessage() + "' while loading color " + message + "\n when reading: '" + val.repr() + "'");
        }
        if (c == null) {
            throw new ValueError("Error loading color: " + message + "\nwhen reading: " + val.repr());
        } else {
            return c;
        }
	}

	public static Color objToColorEx(Obj val) {
		Color c = objToColor(val);
		if (c == null) {
			throw new ValueError("Error loading color when reading: " + val.repr());
		} else {
			return c;
		}
	}

}
