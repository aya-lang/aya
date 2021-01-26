package aya.obj.block;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;

import aya.Aya;
import aya.ReprStream;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.TypeError;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.util.Casting;

public class BlockHeader extends Instruction {
	
	public static class Arg {
		public Symbol var;
		public Symbol type;
		public boolean copy;
		
		public Arg(Symbol var) {
			this.var = var;
			this.type = SymbolConstants.ANY;
			this.copy = false;
		}
		
		public Arg(Symbol var, Symbol type, boolean copy) {
			this.var = var;
			this.type = type;
			this.copy = copy;
		}
		
		public String toString() {
			return str();
		}

		public String str() {
			String s = var.name() + (copy ? "$" : "");
			if (this.type.id() != SymbolConstants.ANY.id()) {
				s += "::" + type.name();
			}
			return s;
		}
	}
	
	private Dict _vars;
	private HashMap<Symbol, InstructionStack> _defaults;
	private ArrayList<Arg> _args;

	
	public BlockHeader(Dict vars) {
		_vars = vars;
		_defaults = new HashMap<Symbol, InstructionStack>();
		_args = new ArrayList<Arg>();
	}


	public BlockHeader() {
		this(new Dict());
	}

	
	/** Add an argument to the top of the argument stack */
	public void addArg(Arg arg) {
		_args.add(0, arg);
	}

	
	public void addDefault(Symbol var, InstructionStack instructions) {
		_defaults.put(var, instructions);
	}

	
	public void addDefault(Symbol var, Obj value) {
		_vars.set(var, value);
	}
	
	
	public void execute(Block b) {
		Dict vars = _vars.clone();
		setArgs(_args, vars, b);
		Aya.getInstance().getVars().add(vars);
		evaluateDefaults(b, vars, _defaults);
	}
	
	private static void evaluateDefaults(Block b, Dict vars, HashMap<Symbol, InstructionStack> defaults) {
		Block block = new Block();
		for (HashMap.Entry<Symbol, InstructionStack> init : defaults.entrySet()) {
			block.clear();
			block.addAll(init.getValue().duplicate().getInstrucionList());
			block.eval();
			Obj o;
			try {
				o = block.pop();
			} catch (EmptyStackException e) {
				throw new AyaRuntimeException("Empty stack in initializer (" + init.getValue().toString() + ")");
			}
			vars.set(init.getKey(), o);
		}
		
	}
	
	
	private static boolean checkType(Obj o, Symbol type) {
		// Special type "any"
		if (type.id() == SymbolConstants.ANY.id()) return true;
		
		// Check user defined type 
		if (o.isa(Obj.DICT)) {
			Symbol otype = null;

			Obj dtype = ((Dict)o).getFromMetaTableOrNull(SymbolConstants.KEYVAR_TYPE);
			if (dtype != null && dtype.isa(Obj.SYMBOL)) {
				otype = (Symbol)dtype;
			} else {
				otype = SymbolConstants.DICT;
			}
			
			if (otype != null && otype.id() == type.id()) {
				return true;
			}
		}
	
		// Normal check
		if (o.isa(Obj.symToID(type))) {
			return true;
		} else {
			return false;
		}
	}
	
	private static void setArgs(ArrayList<Arg> args, Dict vars, Block b) {
		if (args.size() == 0) return;
		
		for (Arg arg : args) {
			final Obj o = b.pop();
			if (checkType(o, arg.type)) {
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
	
	public ReprStream repr(ReprStream stream, ArrayList<Symbol> captures) {
		for (int i = _args.size()-1; i >= 0; i--) {
			stream.print(_args.get(i).str());
			stream.print(" ");
		}
		
		if (_vars.size() != 0 || _defaults.size() != 0 || captures != null) {
			stream.print(": ");
		}
		
		_vars.reprHeader(stream);
		stream.print(" ");
		
		for (HashMap.Entry<Symbol, InstructionStack> d : _defaults.entrySet()) {
			stream.print(d.getKey().name());
			stream.print("(");
			d.getValue().repr(stream);
			stream.print(") ");
		}
	
		if (captures != null) {
			for (Symbol v : captures) {
				stream.print(v.name() + "^ ");
			}
		}

		// Trim off the final space
		stream.delTrailingSpaces();
		stream.print(",");
		return stream;
	}

	public BlockHeader copy() {
		BlockHeader b = new BlockHeader();
		b._args = _args;
		b._vars = Casting.asDict(_vars.deepcopy());
		for (HashMap.Entry<Symbol, InstructionStack> d : _defaults.entrySet()) {
			b._defaults.put(d.getKey(), d.getValue());
		}
		return b;
	}
	
	public ArrayList<Arg> getArgs() {
		return _args;
	}

	public Dict getVars() {
		return _vars;
	}

	

}
