package aya.obj.block;

import java.util.ArrayList;
import java.util.HashMap;

import aya.ReprStream;
import aya.instruction.Instruction;
import aya.instruction.variable.assignment.Assignment;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.symbol.Symbol;

public class BlockUtils {

	/** Split a block into a list of blocks, 1 per instruction */
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
	
	public static StaticBlock mergeLocals(StaticBlock block, Dict locals) {
		Dict new_locals = block.getLocals().clone();
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
				for (int i = args.size()-1; i >= 0; i--) {
					stream.print(args.get(i).toString());
					stream.print(" ");
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
				// These are only ever provided if this block is printed from a BlockLiteralInstruction
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

			for(Instruction i : block.getInstructions()) {
				i.repr(stream);
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
}
