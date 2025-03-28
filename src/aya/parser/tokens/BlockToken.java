package aya.parser.tokens;

import java.util.ArrayList;
import java.util.HashMap;

import aya.exceptions.parser.ParserException;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.BlockLiteralInstruction;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.instruction.variable.assignment.Assignment;
import aya.obj.Obj;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.parser.HeaderUtils;
import aya.parser.Parser;
import aya.parser.SourceStringRef;
import aya.parser.token.TokenQueue;
import aya.util.Triple;

public class BlockToken extends CollectionToken {
	
	public static final Obj DEFAULT_LOCAL_VAR = Num.ZERO;
		
	public BlockToken(String data, ArrayList<Token> col, SourceStringRef source) {
		super(Token.BLOCK, data, col, source);
	}

	
	@Override
	public Instruction getInstruction() throws ParserException {
		//Split Tokens where there are commas
		ArrayList<TokenQueue> blockData = splitCommas(col);
		if (blockData.size() == 1) {
			InstructionStack instructions = Parser.generate(blockData.get(0));
			return new BlockLiteralInstruction(this.getSourceStringRef(), BlockUtils.fromIS(instructions));
		} else {
			TokenQueue header = blockData.get(0);

			if (blockData.size() == 2) {
				//Empty header, dict literal
				if (!header.hasNext()) {
					// Eventually this may be used to specify that a block has locals
					// For now it is an error to help transition from old dict literals
					throw new SyntaxError("Block cannot have an empty header", source);
				}
				//Non-empty header, args and local variables
				else {
					InstructionStack main_instructions = Parser.generate(blockData.get(1));
					Triple<ArrayList<Assignment>, Dict, HashMap<Symbol, StaticBlock>> p = HeaderUtils.generateBlockHeader(blockData.get(0));
					StaticBlock blk = BlockUtils.fromIS(main_instructions, p.second(), p.first());
					return new BlockLiteralInstruction(this.getSourceStringRef(), blk, p.third());
				}
			} else {
				throw new SyntaxError("BlockEvaluator contains too many parts", getSourceStringRef());
			}
		}
	}
	
	

	@Override
	public String typeString() {
		return "block";
	}
}
