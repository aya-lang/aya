package aya.obj.list.numberlist;

import aya.obj.number.Number;

public interface NumberListOp {
	// 2 arg
	public NumberList ln(NumberList a, Number b);
	public NumberList nl(Number a, NumberList b);
	public NumberList ll(NumberList a, NumberList b);
	// 1 arg
	public NumberList l(NumberList a);
}
