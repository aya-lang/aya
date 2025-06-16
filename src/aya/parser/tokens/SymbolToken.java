package aya.parser.tokens;

import aya.exceptions.parser.EndOfInputError;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.parser.SourceStringRef;

public class SymbolToken extends EscapedToken {

    public SymbolToken(String data, SourceStringRef source, boolean hasExplicitTerminator)
            throws SyntaxError, EndOfInputError {
        super(data, Token.SYMBOL, source, hasExplicitTerminator);
    }

    public Symbol getSymbol() {
        return SymbolTable.getSymbol(unescapedData);
    }


    @Override
    public Instruction getInstruction() {
        return new DataInstruction(getSymbol());
    }

    @Override
    public String typeString() {
        return "symbol";
    }
}
