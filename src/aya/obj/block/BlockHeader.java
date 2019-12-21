package aya.obj.block;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;

import aya.Aya;
import aya.entities.InstructionStack;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.TypeError;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.variable.Variable;
import aya.variable.VariableSet;

public class BlockHeader extends Instruction {
	
	public static final long TYPE_ANY = Obj.SYM_ANY.id();
	
	public static class Arg {
		public long var;
		public long type;
		public boolean copy;
		
		public Arg(long var) {
			this.var = var;
			this.type = TYPE_ANY;
			this.copy = false;
		}
		
		public Arg(long var, long type, boolean copy) {
			this.var = var;
			this.type = type;
			this.copy = copy;
		}
		
		public String toString() {
			return str();
		}

		public String str() {
			String s = Variable.decodeLong(var) + (copy ? "$" : "");
			if (this.type != TYPE_ANY) {
				s += "::" + Variable.decodeLong(type);
			}
			return s;
		}
	}
	
	
	/** Add an argument to the top of the argument stack */
	public void addArg(Arg arg) {
		_args.add(0, arg);
	}
	
	public void addDefault(long var, InstructionStack instructions) {
		_defaults.put(var, instructions);
	}
	
	public void addDefault(long var, Obj value) {
		_vars.setVar(var, value);
	}
	
	private VariableSet _vars;
	private HashMap<Long, InstructionStack> _defaults;
	private ArrayList<Arg> _args;
	
	public BlockHeader() {
		_vars = new VariableSet(false);
		_defaults = new HashMap<Long, InstructionStack>();
		_args = new ArrayList<Arg>();
		//_args.trimToSize();
		
	}
	
	public void execute(Block b) {
		VariableSet vars = _vars.clone();
		setArgs(_args, vars, b);
		Aya.getInstance().getVars().add(vars);
		evaluateDefaults(b, vars, _defaults);
	}
	
	private static void evaluateDefaults(Block b, VariableSet vars, HashMap<Long, InstructionStack> defaults) {
		Block block = new Block();
		for (HashMap.Entry<Long, InstructionStack> init : defaults.entrySet()) {
			block.clear();
			block.addAll(init.getValue().duplicate().getInstrucionList());
			block.eval();
			Obj o;
			try {
				o = block.pop();
			} catch (EmptyStackException e) {
				throw new AyaRuntimeException("Empty stack in initializer (" + init.getValue().toString() + ")");
			}
			vars.setVar(init.getKey(), o);
		}
		
	}
	
	
	private static boolean checkType(Obj o, long type) {
		// Special type "any"
		if (type == TYPE_ANY) return true;
		
		// Check user defined type 
		if (o.isa(Obj.DICT)) {
			long otype = -1;

			Obj dtype = ((Dict)o).getFromMetaTableOrNull(Obj.SYM_TYPE.id());
			if (dtype != null && dtype.isa(Obj.SYMBOL)) {
				otype = ((Symbol)dtype).id(); 
			} else {
				otype = Obj.SYM_DICT.id();
			}
			
			if (otype == type) {
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
	
	private static void setArgs(ArrayList<Arg> args, VariableSet vars, Block b)
	{
		if (args.size() == 0) return;
		
		for (Arg arg : args) {
			final Obj o = b.pop();
			if (checkType(o, arg.type)) {
				if (arg.copy) {
					vars.setVar(arg.var, o.deepcopy());
				} else {
					vars.setVar(arg.var, o);
				}
			} else {
				throw new TypeError("{ARGS}\n\tExpected:" + Symbol.fromID(arg.type).repr()
							+ "\n\tReceived:" + o);
			}
		}	
	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		StringBuilder sb = new StringBuilder("");
		
		for (int i = _args.size()-1; i >= 0; i--) {
			sb.append(_args.get(i).str());
			sb.append(" ");
		}
		
		if (_vars != null || _defaults != null) {
			sb.append(": ");
		}
		
		sb.append(_vars.headerStr());
		
		for (HashMap.Entry<Long, InstructionStack> d : _defaults.entrySet()) {
			sb.append(Variable.decodeLong(d.getKey()));
			sb.append("(").append(d.getValue().toString()).append(") ");
		}

		// Trim off the final space
		String out = sb.toString();

		if (sb.length() > 0) {
			out = out.substring(0, sb.length()-1);
		}
		
		return out + ",";
	}
	
	public ArrayList<Arg> getArgs() {
		return _args;
	}

	public VariableSet getVars() {
		return _vars;
	}
	

}
