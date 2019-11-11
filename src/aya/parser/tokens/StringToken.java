package aya.parser.tokens;

import aya.Aya;
import aya.entities.InstructionStack;
import aya.exceptions.SyntaxError;
import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.instruction.InterpolateStringInstruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.block.Block;
import aya.obj.list.Str;
import aya.parser.Parser;
import aya.parser.ParserString;
import aya.variable.Variable;

public class StringToken extends StdToken {
		
	private boolean interpolate; //If true, interpolate string at runtime
	
	public StringToken(String data) {
		super(data, Token.STRING);
		this.interpolate = true;
	}
	
	public StringToken(String data, boolean interpolate) {
		super(data, Token.STRING);
		this.interpolate = interpolate;
	}
	
	@Override
	public Instruction getInstruction() {
		if (interpolate && data.contains("$"))
			return parseInterpolateStr(data);
		return new DataInstruction(new Str(data));
	}

	private InterpolateStringInstruction parseInterpolateStr(String data) {
		ParserString in = new ParserString(data);
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
				if (Variable.isValidChar(c)) {
					String var_name = ""+c;
					while (in.hasNext() && Variable.isValidChar(in.peek())) {
						var_name += in.next();
					}
					
					//Add and reset the string builder
					instrs.insert(0, new Str(sb.toString()));
					sb.setLength(0);
					
					//Add the variable
					instrs.insert(0, new GetVariableInstruction(Variable.encodeString(var_name)));
					
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
					instrs.insert(0, new Str(sb.toString()));
					sb.setLength(0);
					
					//Add the block
					instrs.insert(0, new Block(Parser.compileIS(block.toString(), Aya.getInstance())));
					
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
		
		instrs.insert(0, new Str(sb.toString()));
		return new InterpolateStringInstruction(data, instrs);
	}

	@Override
	public String typeString() {
		return "string";
	}
}
