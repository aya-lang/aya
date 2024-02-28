package aya.obj.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import aya.Aya;
import aya.ReprStream;
import aya.exceptions.runtime.ValueError;
import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.instruction.op.Operator;
import aya.instruction.op.OperatorInstruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.instruction.variable.VariableInstruction;
import aya.instruction.variable.assignment.Assignment;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.util.Casting;
import aya.util.Pair;

public class BlockUtils {

	/** Split a blockEvaluator into a list of blocks, 1 per instruction */
	public static List split(StaticBlock block) {
		ArrayList<Obj> blocks = new ArrayList<Obj>();
		for (Instruction instr : block.getInstructions())
		{
			ArrayList<Instruction> is = new ArrayList<Instruction>();
			is.add(instr);
			blocks.add(0, new StaticBlock(is));
		}
		return new List(blocks);
	}
	
	public static StaticBlock addLocals(StaticBlock block) {
		if (block.hasLocals()) {
			return block;
		} else {
			return new StaticBlock(block.getInstructions(), new Dict(), block.getArgs());
		}
	}
	
	// Always returns a Dict
	public static Dict copyLocals(StaticBlock b) {
		if (b.getLocals() == null) {
			return new Dict();
		} else {
			return b.getLocals().clone();
		}
	}
	
	public static StaticBlock mergeLocals(StaticBlock block, Dict locals) {
		Dict new_locals = copyLocals(block);
		new_locals.update(locals);
		return new StaticBlock(block.getInstructions(), new_locals, block.getArgs());
	}

	/** If print_braces is true, return "{instructions}" else just "instructions" */
	public static ReprStream repr(ReprStream stream,
			               StaticBlock block,
			               boolean print_braces,
			               HashMap<Symbol, StaticBlock> defaults) {

		if (stream.visit(block)) {
			if (print_braces) stream.print("{");
			
			Dict locals = block.getLocals();
			ArrayList<Assignment> args = block.getArgs();
			
			// Header
			if (locals != null) {
				// Args
				if (args != null) {
					for (Assignment arg : args) {
						stream.print(arg.toString());
						stream.print(" ");
					}
				}
				
				// Non-arg locals
				if (locals.size() != 0) {
					stream.print(": ");
				
					// Print locals
					for (Symbol key : locals.keys()) {
						stream.print(key.name());
						stream.print("(");
						locals.get(key).repr(stream);
						stream.print(")");
					}
				}
				
				// Defaults
				// These are only ever provided if this blockEvaluator is printed from a BlockLiteralInstruction
				if (defaults != null) {
					for (Symbol v : defaults.keySet()) {
						stream.print(v.name() + "(");
						repr(stream, defaults.get(v), false, null);
						stream.print(") ");
					}
				}

				// Trim off the final space
				stream.delTrailingSpaces();
				stream.print(",");
			}
			
			ArrayList<Instruction> is = block.getInstructions();
			for(int i = is.size() - 1; i >= 0; i--) {
				is.get(i).repr(stream);
				stream.print(" ");
			}

			// Remove trailing space
			stream.delTrailingSpaces();

			if (print_braces) stream.print("}");
			
			
			stream.popVisited(block);
		} else {
			stream.print("{...}");
		}

		return stream;
	}

	public static ReprStream repr(ReprStream stream,
			               StaticBlock block,
			               boolean print_braces) {
		return repr(stream, block, print_braces, null);
	}
	
	public static StaticBlock makeBlockWithSingleInstruction(Instruction i) {
		ArrayList<Instruction> is = new ArrayList<Instruction>();
		is.add(i);
		return new StaticBlock(is);
	}
	
	public static StaticBlock fromIS(InstructionStack is) {
		return fromIS(is, null, null);
	}

	public static StaticBlock fromIS(InstructionStack is, Dict locals, ArrayList<Assignment> args) {
		ArrayList<Instruction> instructions = is.getInstrucionList();
		ArrayList<Instruction> block_instructions = new ArrayList<Instruction>();
		//for (int i = instructions.size()-1; i >= 0; i--) {
		//	block_instructions.add(instructions.get(i));
		//}
		block_instructions.addAll(instructions); // copy
		return new StaticBlock(block_instructions, locals, args);
	}
	
	public static StaticBlock stripHeader(StaticBlock block) {
		return new StaticBlock(block.getInstructions());
	}
	
	// Not optimized, should refactor later
	public static StaticBlock addObjToStack(StaticBlock block, Obj o) {
		ArrayList<Instruction> is = new ArrayList<Instruction>();
		is.addAll(block.getInstructions());
		is.add(new DataInstruction(o));
		return replaceInstructions(block, is);
	}
	
	private static StaticBlock replaceInstructions(StaticBlock block, ArrayList<Instruction> is) {
		return new StaticBlock(is, block.getLocals(), block.getArgs());
	}


	public static StaticBlock assignVarValues(Dict d, StaticBlock blk) {
		ArrayList<Instruction> new_is = new ArrayList<Instruction>(blk.getInstructions());

		for (Pair<Symbol, Obj> pair : d.items()) {
			Symbol var = pair.first();
			Obj item = pair.second();

			// Finds all vars with id matching varid and swaps them with item
			for (int i = 0; i < new_is.size(); i++) {
				final Instruction o = new_is.get(i);
				if (o instanceof GetVariableInstruction && ((GetVariableInstruction)o).getSymbol().id() == var.id()) {
					new_is.set(i, new DataInstruction(item));
				}
			}
		}
		
		return replaceInstructions(blk, new_is);
	}

	public static Dict getBlockMeta(StaticBlock b) {
		Dict d = new Dict();

		ArrayList<Assignment> args = b.getArgs();
		if (args != null) {
			ArrayList<Obj> args_list = new ArrayList<Obj>();
			for (Assignment a : b.getArgs()) {
				args_list.add(a.toDict());
			}
			d.set(SymbolConstants.ARGS, new List(args_list));
		} else {
			d.set(SymbolConstants.ARGS, new List());
		}
			
		final Dict vars = copyLocals(b);
		if (vars != null) {
			d.set(SymbolConstants.LOCALS, vars);
		}
				
		return d;
	}
	
	// If the blockEvaluator has a single variable, convert it to a symbol
	// otherwise return the blockEvaluator
	public static Obj convertSingleVariableToSymbol(StaticBlock b) {
		ArrayList<Instruction> is = b.getInstructions();
		if (is.size() > 0) {
			Instruction i = is.get(0);
			if (i instanceof VariableInstruction) {
				VariableInstruction v = (VariableInstruction)i;
				return v.getSymbol();
			}
		}
		// Cannot convert, return original
		return b;
	}
	
	public static StaticBlock fromList(List l) {
		ArrayList<Instruction> is = new ArrayList<Instruction>();
		for (int i = 0; i < l.length(); i++) {
			final Obj k = l.getExact(i);
			if (k.isa(Obj.BLOCK)) {
				// Add these in reverse
				ArrayList<Instruction> blk_is = Casting.asStaticBlock(k).getInstructions();
				for (int j = blk_is.size()-1; j >= 0; j--) {
					is.add(0, blk_is.get(j));
				}
			} else {
				is.add(0, new DataInstruction(k));
			}
		}
		return new StaticBlock(is);
	}

	public static StaticBlock capture(StaticBlock b, Symbol s) {
		Obj o = Aya.getInstance().getVars().getVar(s);
		Dict locals = copyLocals(b);
		locals.set(s, o);
		return new StaticBlock(b.getInstructions(), locals, b.getArgs());
	}

	/** Add an object to the instruction stack */
	public static StaticBlock add(StaticBlock b, Obj o) {
		ArrayList<Instruction> instructions = b.getInstructions();
		instructions.add(new DataInstruction(o));
		return new StaticBlock(instructions, b.getLocals(), b.getArgs());
	}

	/** Add an instruction to the instruction stack */
	public static StaticBlock add(StaticBlock b, Instruction o) {
		ArrayList<Instruction> instructions = b.getInstructions();
		instructions.add(o);
		return new StaticBlock(instructions, b.getLocals(), b.getArgs());
	}

	/** Adds a collection of objects to the instruction stack */
	public static StaticBlock addAll(StaticBlock b, StaticBlock toAdd) {
		ArrayList<Instruction> instructions = b.getInstructions();
		instructions.addAll(toAdd.getInstructions());
		return new StaticBlock(instructions, b.getLocals(), b.getArgs());
	}
	
	/** Adds a collection of objects to the instruction stack */
	public static StaticBlock addAll(StaticBlock b, Collection<? extends Instruction> list) {
		ArrayList<Instruction> instructions = b.getInstructions();
		instructions.addAll(list);
		return new StaticBlock(instructions, b.getLocals(), b.getArgs());
	}

	public static List singleToSymbolList(StaticBlock b) {
		ArrayList<Obj> out = new ArrayList<Obj>();
		ArrayList<Instruction> instructions = b.getInstructions();
		if (instructions.size() == 1) {
			Instruction i = instructions.get(0);
			if (i instanceof VariableInstruction) {
				out.add( ((VariableInstruction)i).getSymbol() );
			} else if (i instanceof OperatorInstruction) {
				Operator op = ((OperatorInstruction)i).getOperator();
				if (op.overload() != null) {
					out.addAll(op.overload().getSymbols());
				}
			}
		}
		return new List(out);
	}

	public static Dict getHelpDataForOperator(StaticBlock block) {
		if (block.getInstructions().size() == 0) {
			throw new ValueError("Empty blockEvaluator");
		} else {
			Instruction i = block.getInstructions().get(0);
			if (i instanceof OperatorInstruction) {
				Operator op = ((OperatorInstruction)i).getOperator();
				return op.getDoc().toDict();
			} else {
				throw new ValueError("No doc found for " + block.str());
			}
		}
	}

}
