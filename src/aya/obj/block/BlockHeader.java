package aya.obj.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import aya.Aya;
import aya.ReprStream;
import aya.exceptions.runtime.TypeError;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.util.Casting;

public class BlockHeader extends Instruction {
	
	private Dict _vars;
	private ArrayList<BlockHeaderArg> _args;

	
	public BlockHeader(Dict vars) {
		_vars = vars;
		_args = new ArrayList<BlockHeaderArg>();
	}


	public BlockHeader() {
		this(new Dict());
	}

	
	/** Add an argument to the top of the argument stack */
	public void addArg(BlockHeaderArg arg) {
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
	
	
	
	private static void setArgs(ArrayList<BlockHeaderArg> args, Dict vars, Block b) {
		if (args.size() == 0) return;
		
		for (BlockHeaderArg arg : args) {
			final Obj o = b.pop();
			if (Obj.isInstance(o, arg.type)) {
				if (arg.copy) {
					vars.set(arg.var, o.deepcopy());
				} else {
					vars.set(arg.var, o);
				}
			} else {
				throw new TypeError("{ARGS}\n\tExpected:" + arg.type.repr()
							+ "\n\tReceived:" + o);
			}
		}	
	}
	
	

	@Override
	public ReprStream repr(ReprStream stream) {
		return repr(stream, null);
	}
	
	public ReprStream repr(ReprStream stream, HashMap<Symbol, Block> defaults) {
		for (int i = _args.size()-1; i >= 0; i--) {
			stream.print(_args.get(i).str());
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
				defaults.get(v).repr(stream, false);
				stream.print(") ");
			}
		}

		// Trim off the final space
		stream.delTrailingSpaces();
		stream.print(",");
		return stream;
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
		BlockHeader b = new BlockHeader();
		b._args = _args;
		b._vars = Casting.asDict(_vars.deepcopy());
		return b;
	}
	
	public ArrayList<BlockHeaderArg> getArgs() {
		return _args;
	}

	public Dict getVars() {
		return _vars;
	}

	

}
