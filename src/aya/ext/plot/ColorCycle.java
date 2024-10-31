package aya.ext.plot;

import aya.exceptions.runtime.ValueError;
import aya.util.CircleIterator;
import aya.util.ColorFactory;
import aya.util.DictReader;
import aya.util.ObjToColor;
import aya.util.Sym;

import java.awt.Color;
import java.util.ArrayList;

public class ColorCycle {
    public static ColorCycle getOrDefault(DictReader d, ColorCycle defaults) {
        if (d.hasKey(Sym.sym("color_cycle"))) {
            return new ColorCycle(d);
        }
        return defaults;
    }

    private final ArrayList<Color> colors = new ArrayList<>();

    public ColorCycle(DictReader d) {
        if (d.hasKey(Sym.sym("color_cycle"))) {
            aya.obj.list.List colors_obj = d.getListEx(Sym.sym("color_cycle"));
            if (colors_obj.length() <= 0) {
                throw new ValueError(d.get_err_name() + ".color_cycle: Expected non-empty list for 'color_cycle', got empty list");
            }

            for (int i = 0; i < colors_obj.length(); i++) {
                try {
                    colors.add(ObjToColor.objToColorEx(colors_obj.getExact(i), "color_cycle"));
                } catch (Exception e) {
                    throw new ValueError(e.getMessage() + " Reading from " + d.get_err_name());
                }
            }
        } else {
            // matplotlib default color cycle
            colors.add(ColorFactory.web("#1f77b4"));
            colors.add(ColorFactory.web("#ff7f0e"));
            colors.add(ColorFactory.web("#2ca02c"));
            colors.add(ColorFactory.web("#d62728"));
            colors.add(ColorFactory.web("#9467bd"));
            colors.add(ColorFactory.web("#8c564b"));
            colors.add(ColorFactory.web("#e377c2"));
            colors.add(ColorFactory.web("#7f7f7f"));
            colors.add(ColorFactory.web("#bcbd22"));
            colors.add(ColorFactory.web("#17becf"));
        }
    }

    public CircleIterator<Color> makeIterator() {
        return new CircleIterator<>(colors);
    }
}
