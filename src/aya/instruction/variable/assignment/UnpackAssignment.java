package aya.instruction.variable.assignment;

import java.util.ArrayList;

import aya.eval.ExecutionContext;
import aya.exceptions.parser.SyntaxError;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public class UnpackAssignment extends Assignment {


	public static class Arg {
		public Assignment assignment;
		public boolean slurp;
		
		public Arg(Assignment assignment, boolean slurp) {
			this.assignment = assignment;
			this.slurp = slurp;
		}
		
		public String toString() {
			String out = this.assignment.toString();
			if (slurp) out += "~";
			return out;
		}
	}

	
	private ArrayList<Arg> _args;
	private ArrayList<Arg> _before_slurp;
	private ArrayList<Arg> _after_slurp;
	private Arg _slurp;
	private Symbol _catchall;


	public static UnpackAssignment fromArgList(ArrayList<Arg> args, Symbol catchall, SourceStringRef source) throws SyntaxError {
		ArrayList<Arg> before_slurp = null;
		ArrayList<Arg> after_slurp = null;
		Arg slurp = null;
		
		for (int i = 0; i < args.size(); i++) {
			Arg a = args.get(i);
			if (a.slurp) {
				if (slurp == null) {
					// Before slurp
					before_slurp = new ArrayList<Arg>();
					before_slurp.addAll(args.subList(0, i));

					// After slurp
					after_slurp = new ArrayList<Arg>();
					after_slurp.addAll(args.subList(i+1, args.size()));
					
					// Slurp
					slurp = args.get(i);
				} else {
					throw new SyntaxError("Cannot have multiple slurps", source);
				}
			}
		}
		
		return new UnpackAssignment(source, args, before_slurp, after_slurp, slurp, catchall);
	}

	
	private UnpackAssignment(SourceStringRef source,
							 ArrayList<Arg> args,
							 ArrayList<Arg> before_slurp,
							 ArrayList<Arg> after_slurp,
							 Arg slurp,
							 Symbol catchall) {
		super(source);
		_args = args;
		_before_slurp = before_slurp;
		_after_slurp = after_slurp;
		_slurp = slurp;
		_catchall = catchall;
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

	
	public void assign(Dict vars, Obj o, ExecutionContext ctx) {
		if (o.isa(Obj.LIST)) {
			List l = Casting.asList(o);

			if (_catchall != null) {
				vars.set(_catchall, l);
			}

			if (_slurp == null) {
				// Easy case, one-to-one mapping between args and list elements
				if (l.length() == _args.size()) {
					for (int i = 0; i < l.length(); i++) {
						Arg a = _args.get(i);
						a.assignment.assign(vars, l.getExact(i), ctx);
					}
				} else {
					if (_catchall == null) {
						ValueError e = new ValueError("Cannot unpack " + o.repr() + ". List length does not match number of args");
						e.setSource(getSource());
						throw e;
					}
				}
			} else {
				// _slurp itself may be an empty list so only before and after are required
				if (l.length() >= _before_slurp.size() + _after_slurp.size()) {
					// Before Slurp
					for (int i = 0; i < _before_slurp.size(); i++) {
						Arg a = _before_slurp.get(i);
						a.assignment.assign(vars, l.getExact(i), ctx);
					}
					// After Slurp
					for (int i = 0; i < _after_slurp.size(); i++) {
						Arg arg = _after_slurp.get((_after_slurp.size()-1) - i);
						Obj x = l.getExact((l.length()-1) - i);
						arg.assignment.assign(vars, x, ctx);
					}
					// Slurp itself
					List slurp = l.sliceExact(_before_slurp.size(), l.length() - _after_slurp.size());
					_slurp.assignment.assign(vars, slurp, ctx);
				} else {
					if (_catchall == null) {
						ValueError e = new ValueError("Cannot unpack " + o.repr() + ". List length does not match number of args (excluding slurp ~)");
						e.setSource(getSource());
						throw e;
					}
				}
			}
		} else {
			TypeError e = new TypeError("Cannot unpack " + o.repr() + ". Argument must be a list");
			e.setSource(getSource());
			throw e;

		}
	}


	public ArrayList<Arg> getArgs() {
		return _args;
	}


	@Override
	public void toDict(Dict d) {
		List args = new List();
		for (UnpackAssignment.Arg arg : _args) {
			Dict a = new Dict();
			a.set(SymbolConstants.ARG, arg.assignment.toDict());
			a.set(SymbolConstants.SLURP, Num.fromBool(arg.slurp));
			args.mutAdd(a);
		}
		d.set(SymbolConstants.ARGS, args);
	}


	@Override
	public ArrayList<Symbol> getNames() {
		var out = new ArrayList<Symbol>();
		for (var arg : _args) {
			out.addAll(arg.assignment.getNames());
		}
		return out;
	}

	@Override
	public boolean hasTypeInfo() {
		for (var a : _args) {
			if (a.assignment.hasTypeInfo()) {
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public UnpackAssignment setTypeInfo(ExecutionContext ctx) {
		if (hasTypeInfo()) {
			var args = new ArrayList<Arg>();
			for (var arg : _args) {
				args.add(new Arg(arg.assignment.setTypeInfo(ctx), arg.slurp));
			}
			try {
				return fromArgList(args, _catchall, _source);
			} catch (SyntaxError e) {
				// This shouldn't happen
				// The error should have been found when the code was originally parsed, not when assigning types
				throw new ValueError("PARSER BUG: " + e.getMessage());
			}
		} else {
			// No type info, just return this
			return this;
		}
	}
		
}
