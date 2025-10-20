package aya.parser.tokens;

import aya.exceptions.parser.EndOfInputError;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.Instruction;
import aya.instruction.variable.GetCDictInstruction;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.parser.SourceStringRef;

public class CDictToken extends EscapedToken {

    public CDictToken(String data, SourceStringRef source) throws SyntaxError, EndOfInputError {
        super(data, Token.KEY_VAR, source, false);
    }

    public Symbol getSymbol() {
        return SymbolTable.getSymbol(unescapedData);
    }

    @Override
    public Instruction getInstruction() {
        return new GetCDictInstruction(getSourceStringRef(), getSymbol());
    }

    @Override
    public String typeString() {
        return "cdict";
    }

}
