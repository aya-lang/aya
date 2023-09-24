package aya.parser.tokens;

import aya.Aya;
import aya.exceptions.ex.ParserException;
import aya.exceptions.ex.SyntaxError;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.instruction.InterpolateStringInstruction;
import aya.instruction.StringLiteralInstruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.block.Block;
import aya.obj.list.List;
import aya.obj.symbol.SymbolTable;
import aya.parser.Parser;
import aya.parser.ParserString;
import aya.parser.SourceString;
import aya.parser.SourceStringRef;

public class StringToken extends StdToken {
		
	private boolean interpolate; //If true, interpolate string at runtime
	
	public StringToken(String data, SourceStringRef source) {
		super(data, Token.STRING, source);
		this.interpolate = true;
	}
	
	public StringToken(String data, boolean interpolate, SourceStringRef source) {
		super(data, Token.STRING, source);
		this.interpolate = interpolate;
	}
	
	@Override
	public Instruction getInstruction() throws ParserException {
		if (interpolate && data.contains("$"))
			return parseInterpolateStr(data);
		return new StringLiteralInstruction(data);
	}

	private InterpolateStringInstruction parseInterpolateStr(String data) throws ParserException {
		ParserString in = new ParserString(new SourceString(data, "StringToken.parseInterpolateStr"));
		StringBuilder sb = new StringBuilder();
		InstructionStack instrs = new InstructionStack();
		
		while(in.hasNext()) {
			char c = in.next();
			
			//Escaped dollar sign
			if (c == '\\' && in.hasNext() && in.peek() == '$') {
				sb.append('$');
				in.next(); //Skip the $
			} 
			
			//Requires Interpolation
			else if (c == '$' && in.hasNext()) {
				c = in.next();
				
				//Normal Var
				if (SymbolTable.isBasicSymbolChar(c)) {
					String var_name = ""+c;
					while (in.hasNext() && SymbolTable.isBasicSymbolChar(in.peek())) {
						var_name += in.next();
					}
					
					//Add and reset the string builder
					instrs.insert(0, List.fromString(sb.toString()));
					sb.setLength(0);
					
					//Add the variable
					instrs.insert(0, new GetVariableInstruction(Aya.getInstance().getSymbols().getSymbol(var_name)));
					
				}
				
				//Block Interpolation
				else if (c == '(') {
					int braces = 1;
					StringBuilder block = new StringBuilder();
					boolean complete = false;
					while (in.hasNext()) {
						
						//Close
						if(in.peek() == ')') {
							braces--;
							if(braces == 0) {
								complete = true;
								in.next(); //Skip the ')'
								break;
							} else {
								block.append(')');
								in.next();
							}
						}
						
						//Open
						else if(in.peek() == '(') {
							braces++;
							block.append('(');
							in.next();
						}
						
						//Other
						else {
							block.append(in.next());
						}
					}
					
					if(!complete) {
						throw new SyntaxError("Incomplete interpolation in \"" + data + "\"");
					}
					
					
					//Add and reset the string builder
					instrs.insert(0, List.fromString(sb.toString()));
					sb.setLength(0);
					
					//Add the block
					instrs.insert(0, new Block(Parser.compileIS(new SourceString(block.toString(), "StringToken.parseInterpolateString.B"), Aya.getInstance())));
					
				}
				
				//No Interpolation, just place the chars normally
				else {
					sb.append('$').append(c);
				}
			} 
			
			//Normal char; do nothing
			else {
				sb.append(c);
			}
		}
		
		instrs.insert(0, List.fromString(sb.toString()));
		return new InterpolateStringInstruction(data, instrs);
	}

	@Override
	public String typeString() {
		return "string";
	}
}
