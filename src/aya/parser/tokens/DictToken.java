package aya.parser.tokens;

import java.util.ArrayList;

import aya.exceptions.parser.ParserException;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.DictLiteralInstruction;
import aya.instruction.EmptyDictLiteralInstruction;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.obj.block.BlockUtils;
import aya.parser.Parser;
import aya.parser.SourceStringRef;
import aya.parser.token.TokenQueue;

public class DictToken extends CollectionToken {

	public DictToken(String data, ArrayList<Token> col, SourceStringRef source) {
		super(Token.DICT, data, col, source);
	}

	@Override
	public Instruction getInstruction() throws ParserException {
		//Split Tokens where there are commas
		ArrayList<TokenQueue> blockData = splitCommas(col);
		
		if (blockData.size() == 1) {
			InstructionStack instructions = Parser.generate(blockData.get(0));
			if (instructions.size() == 0) {
				return EmptyDictLiteralInstruction.INSTANCE;
			} else {
				return new DictLiteralInstruction(this.getSourceStringRef(), BlockUtils.fromIS(instructions));
			}
		} else if (blockData.size() == 2) {
			TokenQueue header = blockData.get(0);

			// Single number in header, create a dict factory with a capture
			if (header.size() == 1 && header.peek() instanceof NumberToken) {

				NumberToken nt = (NumberToken)header.peek();
				int n = 0;
				try {
					n = nt.numValue().toInt();
				} catch (NumberFormatException e) {
					throw new SyntaxError(nt + " is not a valid number in the blockEvaluator header", nt.getSourceStringRef());
				}

				if (n < 1) {
					throw new SyntaxError("Cannot capture less than 1 elements from outer stack in a dict literal", nt.getSourceStringRef());
				}
				InstructionStack instructions = Parser.generate(blockData.get(1));
				if (n == 0 && instructions.isEmpty()) {
					return EmptyDictLiteralInstruction.INSTANCE;
				} else {
					return new DictLiteralInstruction(this.getSourceStringRef(), BlockUtils.fromIS(instructions), n);
				}
			} else {
				throw new SyntaxError("dict headers not supported (yet)", source);
			}

		} else {
			throw new SyntaxError("Dict headers not supported (yet)", source);
		}
	}

	@Override
	public String typeString() {
		return "dict";
	}

}
