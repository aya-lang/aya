package aya.ext.image;

import aya.obj.symbol.Symbol;
import aya.util.Sym;

public enum Channel {
	red(Sym.sym("r")),
	green(Sym.sym("g")),
	blue(Sym.sym("b")),
	alpha(Sym.sym("a"));

	public final Symbol symbol;

	Channel(Symbol symbol) {
		this.symbol = symbol;
	}
}
