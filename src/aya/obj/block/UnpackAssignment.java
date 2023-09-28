package aya.obj.block;

import java.util.ArrayList;

import aya.exceptions.parser.SyntaxError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public class UnpackAssignment {


	public class Arg {
		public Symbol symbol;
		public boolean slurp;
		
		public String toString() {
			String out = symbol.name();
			if (slurp) out += "~";
			return out;
		}
	}

	
	private ArrayList<Arg> _args;
	private ArrayList<Arg> _before_slurp;
	private ArrayList<Arg> _after_slurp;
	private Arg _slurp;


	public static UnpackAssignment fromArgList(ArrayList<Arg> args, SourceStringRef source) throws SyntaxError {
		ArrayList<Arg> before_slurp = null;
		ArrayList<Arg> after_slurp = null;
		Arg slurp = null;
		
		for (int i = 0; i < args.size(); i++) {
			Arg a = args.get(i);
			if (a.slurp) {
				if (slurp == null) {
					
				} else {
					throw new SyntaxError("Cannot have multiple slurps", source);
				}
			}
		}
		
		return new UnpackAssignment(args, before_slurp, after_slurp, slurp);
	}

	
	private UnpackAssignment(ArrayList<Arg> args, ArrayList<Arg> before_slurp, ArrayList<Arg> after_slurp, Arg slurp) {
		_args = args;
		_before_slurp = before_slurp;
		_after_slurp = after_slurp;
		_slurp = slurp;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < _args.size(); i++) {
			sb.append(_args.get(i).toString());
			if (i != _args.size()-1) sb.append(" ");
		}
		sb.append("]");
		return sb.toString();
	}

	
	public void assign(Dict vars, Obj o) {
		if (o.isa(Obj.LIST)) {
			List l = Casting.asList(o);
			if (_slurp == null) {
				// Easy case, one-to-one mapping between args and list elements
				if (l.length() == _args.size()) {
					for (int i = 0; i < l.length(); i++) {
						vars.set(_args.get(i).symbol, l.getExact(i));
					}
				} else {
					throw new ValueError("Cannot unpack " + o.repr() + ". List length does not match number of args");
				}
			} else {
				// _slurp itself may be an empty list so only before and after are required
				if (l.length() >= _before_slurp.size() + _after_slurp.size()) {
					// Before Slurp
					for (int i = 0; i < _before_slurp.size(); i++) {
						vars.set(_before_slurp.get(i).symbol, l.getExact(i));
					}
					// After Slurp
					for (int i = 0; i < _after_slurp.size(); i++) {
						Arg arg = _after_slurp.get((_after_slurp.size()-1) - i);
						Obj x = l.getExact((l.length()-1) - i);
						vars.set(arg.symbol, x);
					}
					// Slurp itself
					List slurp = l.sliceExact(_before_slurp.size(), l.length() - _after_slurp.size());
					vars.set(_slurp.symbol, slurp);
				} else {
					throw new ValueError("Cannot unpack " + o.repr() + ". List length does not match number of args (excluding slurp ~)");
				}
			}
		} else {
			throw new TypeError("Cannot unpack " + o.repr() + ". Argument must be a list");
		}
	}


	public ArrayList<Arg> getArgs() {
		return _args;
	}
}
