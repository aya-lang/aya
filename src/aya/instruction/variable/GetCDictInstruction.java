package aya.instruction.variable;

import aya.ReprStream;
import aya.obj.block.Block;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.Parser;

public class GetCDictInstruction extends VariableInstruction {
	
	GetVariableInstruction get_cdict_;
	GetKeyVariableInstruction get_key_;
	
	public GetCDictInstruction(Symbol var) {
		super(var);
		
		get_key_ = new GetKeyVariableInstruction(var);
		get_cdict_ = new GetVariableInstruction(SymbolConstants.__CDICT__);
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
