package aya.parser.tokens;

import aya.exceptions.parser.ParserException;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.instruction.InterpolateStringInstruction;
import aya.instruction.StringLiteralInstruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.block.BlockUtils;
import aya.obj.list.List;
import aya.obj.symbol.SymbolTable;
import aya.parser.Parser;
import aya.parser.ParserString;
import aya.parser.SourceStringRef;
import aya.parser.StringParseUtils;
import aya.util.UTF16;

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
		if (interpolate && data.contains("$")) {
			return parseInterpolateStr(data, this.getSourceStringRef());
		} else {
			SourceStringRef ref = this.getSourceStringRef();
			String unescaped = StringParseUtils.unescape(new ParserString(ref, this.getData()));
			return new StringLiteralInstruction(this.getSourceStringRef(), unescaped);
		}
	}

	private InterpolateStringInstruction parseInterpolateStr(String data, SourceStringRef source) throws ParserException {
		ParserString in = new ParserString(this.getSourceStringRef(), data);
		StringBuilder sb = new StringBuilder();
		InstructionStack instrs = new InstructionStack();
		
		while(in.hasNext()) {
			int c = in.next();
			
			//Escaped dollar sign
			if (c == '\\' && in.hasNext() && in.peek() == '$') {
				sb.append("\\$");
				in.next(); //Skip the $
			} 
			
			//Requires Interpolation
			else if (c == '$' && in.hasNext()) {
				c = in.next();
				
				//Normal Var
				if (SymbolTable.isBasicSymbolChar(c)) {
					StringBuilder var_name = new StringBuilder(UTF16.surrogateToStr(c));
					while (in.hasNext() && SymbolTable.isBasicSymbolChar(in.peek())) {
						var_name.append(in.nextStr());
					}
					
					//Add and reset the string builder
					SourceStringRef ref = in.currentRef();
					ref.dec(var_name.length()); // Don't include $
					String str_literal = StringParseUtils.unescape(new ParserString(ref, sb.toString()));
					instrs.insert(0, List.fromString(str_literal));
					sb.setLength(0);
					
					//Add the variable
					instrs.insert(0, new GetVariableInstruction(ref, SymbolTable.getSymbol(var_name.toString())));
					
				}
				
				//BlockEvaluator Interpolation
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
							block.append(in.nextStr());
						}
					}
					
					if(!complete) {
						throw new SyntaxError("Incomplete interpolation in \"" + data + "\"", in.currentRef());
					}
					
					
					//Add and reset the string builder
					SourceStringRef ref = in.currentRef();
					ref.dec(block.toString().length());
					ref.dec(2); // Open and close paren
					String str_literal = StringParseUtils.unescape(new ParserString(ref, sb.toString()));
					instrs.insert(0, List.fromString(str_literal));
					sb.setLength(0);
					
					//Add the blockEvaluator
					InstructionStack is = Parser.compileIS(new ParserString(in.currentRef(), block.toString()));
					instrs.insert(0, BlockUtils.fromIS(is));
					
				}
				
				//No Interpolation, just place the chars normally
				else {
					sb.append('$').append(UTF16.surrogateToStr(c));
				}
			} 
			
			//Normal char; do nothing
			else {
				sb.append(UTF16.surrogateToStr(c));
			}
		}
		
		String str_literal = StringParseUtils.unescape(new ParserString(in.currentRef().inc(), sb.toString()));
		instrs.insert(0, List.fromString(str_literal));
		return new InterpolateStringInstruction(this.getSourceStringRef(), data, instrs);
	}

	@Override
	public String typeString() {
		return "string";
	}
}
