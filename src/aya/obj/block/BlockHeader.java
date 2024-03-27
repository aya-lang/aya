package aya.obj.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import aya.Aya;
import aya.ReprStream;
import aya.instruction.Instruction;
import aya.instruction.variable.assignment.Assignment;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public class BlockHeader extends Instruction {
	
	private Dict _vars;
	private ArrayList<Assignment> _args;

	
	public BlockHeader(SourceStringRef source, Dict vars) {
		super(source);
		_vars = vars;
		_args = new ArrayList<Assignment>();
	}


	public BlockHeader(SourceStringRef source) {
		this(source, new Dict());
	}

	
	/** Add an argument to the top of the argument stack */
	public void addArg(Assignment arg) {
		_args.add(0, arg);
	}

	public void addDefault(Symbol var, Obj value) {
		_vars.set(var, value);
	}
	
	
	public void execute(Block b) {
		Dict vars = _vars.clone();
		setArgs(_args, vars, b);
		Aya.getInstance().getVars().add(vars);
	}
	
	
	
	private static void setArgs(ArrayList<Assignment> args, Dict vars, Block b) {
		for (Assignment arg : args) {
			arg.assign(vars, b.pop());
		}	
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		return repr(stream, null);
	}
	
	public ReprStream repr(ReprStream stream, HashMap<Symbol, Block> defaults) {
		return stream;
		/*
		for (int i = _args.size()-1; i >= 0; i--) {
			stream.print(_args.get(i).toString());
			stream.print(" ");
		}
		
		if (_vars.size() != 0) {
			stream.print(": ");
		}
		
		// Print any remaining locals not in defaults
		for (Symbol key : _vars.keys()) {
			stream.print(key.name());
			stream.print("(");
			_vars.get(key).repr(stream);
			stream.print(")");
		}
	
		if (defaults != null) {
			for (Symbol v : defaults.keySet()) {
				stream.print(v.name() + "(");
				BlockUtils.repr(stream, defaults.get(v), false, null);
				stream.print(") ");
			}
		}

		// Trim off the final space
		stream.delTrailingSpaces();
		stream.print(",");
		return stream;
		*/
	}

	/**
	 * Output the variable set as a space separated list of name(value) items
	 * If the value is 0, do not print the value or the parenthesis
	 */
	public ReprStream reprHeader(ReprStream stream) {
		Iterator<HashMap.Entry<Symbol, Obj>> it = _vars.getMap().entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry<Symbol, Obj> pair = (HashMap.Entry<Symbol, Obj>)it.next();
			stream.print(pair.getKey().name());

			final Obj obj = pair.getValue();
			if (obj.equiv(Num.ZERO)) {
				stream.print(" ");
			} else {
				stream.print("(");
				obj.repr(stream);
				stream.print(")");
			}
		}
		return stream;
	}

	public BlockHeader copy() {
		BlockHeader b = new BlockHeader(this.getSource());
		b._args = _args;
		b._vars = Casting.asDict(_vars.deepcopy());
		return b;
	}
	
	public ArrayList<Assignment> getArgs() {
		return _args;
	}

	public Dict getVars() {
		return _vars;
	}

	

}
