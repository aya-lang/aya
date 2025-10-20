package aya.parser.tokens;

import aya.exceptions.parser.EndOfInputError;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.Instruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.parser.SourceStringRef;

public class VarToken extends EscapedToken {

    public VarToken(String data, SourceStringRef source, boolean hasExplicitTerminator)
            throws SyntaxError, EndOfInputError {
        super(data, Token.VAR, source, hasExplicitTerminator);
    }

    public Symbol getSymbol() {
        return SymbolTable.getSymbol(unescapedData);
    }

    @Override
    public Instruction getInstruction() {
        return new GetVariableInstruction(this.getSourceStringRef(), getSymbol());
    }

    @Override
    public String typeString() {
        return "var";
    }
}
