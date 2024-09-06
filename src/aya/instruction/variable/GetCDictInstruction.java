package aya.instruction.variable;

import aya.ReprStream;
import aya.obj.block.Block;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.Parser;
import aya.parser.SourceStringRef;

public class GetCDictInstruction extends VariableInstruction {
	
	GetVariableInstruction get_cdict_;
	GetKeyVariableInstruction get_key_;
	
	public GetCDictInstruction(SourceStringRef source, Symbol var) {
		super(source, var);
		
		get_key_ = new GetKeyVariableInstruction(source, var);
		get_cdict_ = new GetVariableInstruction(source, SymbolConstants.__CDICT__);
	}
	
	@Override
	public void execute(Block b) {
		b.add(get_key_);
		b.add(get_cdict_);
	}
	
	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(Parser.CDICT_CHAR + variable_.name());
		return stream;
	}
	
	
}
