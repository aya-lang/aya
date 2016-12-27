package element.parser.tokens;

import element.Element;
import element.entities.Block;
import element.entities.InstructionStack;
import element.entities.InterpolateString;
import element.exceptions.SyntaxError;
import element.obj.list.Str;
import element.parser.Parser;
import element.parser.ParserString;
import element.variable.Variable;

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
	public Object getElementObj() {
		if (interpolate && data.contains("$"))
			return parseInterpolateStr(data);
		return new Str(data);
	}

	private InterpolateString parseInterpolateStr(String data) {
		ParserString in = new ParserString(data);
		StringBuilder sb = new StringBuilder();
		//ArrayList<Object> instrs = new ArrayList<Object>();
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
				if ('a' <= c && c <= 'z') {
					String var_name = ""+c;
					while (in.hasNext() && 'a' <= in.peek() && in.peek() <= 'z') {
						var_name += in.next();
					}
					
					//Add and reset the string builder
					instrs.insert(0, sb.toString());
					sb.setLength(0);
					
					//Add the variable
					instrs.insert(0, new Variable(var_name));
					
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
					instrs.insert(0, sb.toString());
					sb.setLength(0);
					
					//Add the block
					instrs.insert(0, new Block(Parser.compileIS(block.toString(), Element.getInstance())));
					
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
		
		instrs.insert(0, sb.toString());
		return new InterpolateString(instrs);
	}

	@Override
	public String typeString() {
		return "string";
	}
}
