package aya.parser.tokens;

import java.util.ArrayList;
import java.util.HashMap;

import aya.exceptions.parser.ParserException;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.DictLiteralInstruction;
import aya.instruction.EmptyDictLiteralInstruction;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.instruction.variable.assignment.Assignment;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.parser.HeaderUtils;
import aya.parser.Parser;
import aya.parser.SourceStringRef;
import aya.parser.token.TokenQueue;
import aya.util.Triple;

public class DictToken extends CollectionToken {

	public DictToken(String data, ArrayList<Token> col, SourceStringRef source) {
		super(Token.DICT, data, col, source);
	}

	@Override
	public Instruction getInstruction() throws ParserException {
		//Split Tokens where there are commas
		ArrayList<TokenQueue> blockData = splitCommas(col);
		
		// No header, normal dict literal
		if (blockData.size() == 1) {
			InstructionStack instructions = Parser.generate(blockData.get(0));

			// Optimization for empty dict literal
			if (instructions.size() == 0) {
				return EmptyDictLiteralInstruction.INSTANCE;
			} else {
				return new DictLiteralInstruction(this.getSourceStringRef(), BlockUtils.fromIS(instructions));
			}

		} else if (blockData.size() == 2) {
			TokenQueue header = blockData.get(0);
			
			if (header.size() > 0) {
				InstructionStack main_instructions = Parser.generate(blockData.get(1));
				Triple<ArrayList<Assignment>, Dict, HashMap<Symbol, StaticBlock>> p = HeaderUtils.generateBlockHeader(blockData.get(0));
				StaticBlock blk = BlockUtils.fromIS(main_instructions, p.second(), p.first());
				return new DictLiteralInstruction(this.getSourceStringRef(), blk, p.third());
			} else {
				throw new SyntaxError("Empty header not allowed in dict literal", source);
			}
		} else {
			throw new SyntaxError("Dict literal has too many parts", source);
		}

	}

	@Override
	public String typeString() {
		return "dict";
	}

}
