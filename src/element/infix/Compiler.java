package element.infix;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import element.Element;
import element.entities.Block;
import element.entities.InstructionStack;
import element.exceptions.SyntaxError;
import element.parser.CharacterParser;
import element.parser.ParserString;
import element.variable.Variable;

public class Compiler {
	/**
	 * Stage 1: Tokenize
	 * 		Tokenize the input into literals, variables, operators, functions, and tuples
	 * Stage 1.5: Assemble (assemble all tuples, decimals, etc.
	 * Stage 2: Semantics check (can be added later)
	 * Stage 3: DeSugar
	 * Stage 4: Organize the tokens recursively
	 * Stage 6: Generate Element code
	 * 
	 */
	public static Block compile(String input, boolean debug) throws SyntaxError {
		try {
			if(debug) println("> " + input);
			//Stage 1
			InfixItemList tokenList = tokenize(input);
			if(debug) println("\tTOKENS: " + tokenList.toString());
			
			//Stage 1.5 Combine Nested Objects (Tuples, Lists, etc..)
			tokenList = assemble(tokenList);
			if(debug) println("\tASSMBL: " + tokenList.toString());
			
			//Stage 2: Semantics check
			//semanticsCheck(tokens);
			
			//Stage 3: De-sugar
			tokenList = desugar(tokenList);
			if(debug) println("\tDESGHR: " + tokenList.toString());
	
			
			//Stage 4: Organize
			ArrayList<InfixItem> tokens = organize(tokenList.getArrayList());
			Collections.reverse(tokens); //Reverse it for printing
			if(debug) println("\tORGINZ: " + show(tokens));
			Collections.reverse(tokens); //Reverse it back to normal
		
			//Stage 6: Generate element code
			Block out = generate(tokens);
			if(debug)println("\tGENELM: " + out.toString());
	
			return out;
		} catch (Exception e) {
			throw new SyntaxError("Syntax error: " + input);
		}
	}
	


	public static InfixItemList tokenize(String str) {
		InfixItemList tokens = new InfixItemList();
		
		ParserString in = new ParserString(str);
		
		while(!in.isEmpty()) {
			char current = in.next();
			
			//Number Literals
			if(Character.isDigit(current)) {
				StringBuilder num = new StringBuilder(""+current);
				while(in.hasNext() && Character.isDigit(in.peek())) {
					num.append(in.next());
				}
				tokens.add(new InfixLiteral(num.toString(), InfixLiteral.INT));
			}
			
			//String Literals
			else if (current == '"') {
				StringBuilder strBuildr = new StringBuilder();
				boolean complete = false;
				while(in.hasNext()) {
					char c = in.next();
					if (c == '\\') {
						char escape = in.next();
						switch(escape) {
						case 'n':
							strBuildr.append('\n');
							break;
						case 't':
							strBuildr.append('\t');
							break;
						case '"':
							strBuildr.append('"');
							break;
						case '\\':
							strBuildr.append('\\');
							break;
						case '{':
							StringBuilder sc = new StringBuilder(); //Special Char
							boolean specialComplete = false;
							
							while (in.hasNext()) {
								if(in.peek() == '}') {
									specialComplete = true;
									in.next(); //Skip the closing '}'
									break;
								}
								sc.append(in.next());
							}
							
							if(!specialComplete) {
								throw new SyntaxError("Early termination of special character in string literal: " + strBuildr.toString());
							}
							
							//Parse the character
							char specChar = CharacterParser.parse(sc.toString());
							if(specChar == CharacterParser.INVALID) {
								throw new SyntaxError("'\\" + sc.toString() + "' is not a valid special character");
							}
							
							strBuildr.append(specChar);
							break;
							
						default:
							throw new SyntaxError("'" + escape + "' is not a valid escape character in the string \""
									+ strBuildr.toString() + "\\" + escape + " ...\"" + in.toString());	
						}
					}
					else if(c == '"') {
						complete = true;
						break;
					} else {
						strBuildr.append(c);
					}
				}
				if(!complete) {
					throw new SyntaxError("Expected closing quote after string \"" + strBuildr.toString());
				}
				tokens.add(new InfixLiteral(strBuildr.toString(), InfixLiteral.STRING));
			}
			
			//Normal Operators
			else if (InfixTable.isOpChar(current)) {
				String opstr = ""+current;
				while(in.hasNext() && InfixTable.isOpChar(in.peek())) {
					opstr += in.next();
				}
				
				//Parse the operator(s)
				ArrayList<InfixOperator> ops = InfixTable.parseOP(opstr);

				tokens.addAll(ops);
			}
			
			//Variable Name
			else if (isLowerAlpha(current)) {
				StringBuilder sb = new StringBuilder(""+current);
				while (in.hasNext() && isLowerAlpha(in.peek())) {
					sb.append(in.next());
				}
				if(sb.length() > 12) {
					Element.instance.getOut().printWarn("Only the first 12 characters of a variable name are used. Ignoring '..."
							+ sb.toString().substring(12, sb.length()) + "' in " + sb.toString());
				}
				tokens.add(new InfixVariable(sb.toString()));
			}
			
			//Parenthesis
			else if (current == '(') {tokens.add(InfixSpecial.OPEN_PAREN);}
			else if (current == ')') {tokens.add(InfixSpecial.CLOSE_PAREN);}
			else if (current == '{') {tokens.add(InfixSpecial.OPEN_CURLY);}
			else if (current == '}') {tokens.add(InfixSpecial.CLOSE_CURLY);}
			else if (current == ',') {tokens.add(InfixSpecial.COMMA);}
			else if (current == '=') {tokens.add(InfixSpecial.EQUALS);}
			
		}
		
		
		return tokens;
	}
	
	public static InfixItemList assemble(InfixItemList in) {
		in.init();
		InfixItemList out = new InfixItemList();
		
		while (in.hasNext()) {
			InfixItem current = in.next();
			
			if(current.isLiteral()) {
				out.add(current);
//				switch(current.getPrimaryKey()) {
//				case InfixLiteral.INT:
//					out.add(current);
//					break;
//				case InfixLiteral.STRING:
//					out.add(current);
//					break;
//				default:
//					throw new RuntimeException("Compiler -> assemble -> isLiteral");
//				}
			} 
			
			else if (current.isOperator()) {
					out.add(current);
			} 
			
			
			else if (current.isSpecial()) {
				switch (current.getPrimaryKey()) {
				case InfixSpecial._OPEN_PAREN:
					//closeDelim(InfixSpecial._OPEN_PAREN,InfixSpecial._CLOSE_PAREN,-1,in,out);
					out.add(closeDelim(in, InfixSpecial._OPEN_PAREN));
					break;
				case InfixSpecial._OPEN_CURLY:
					//closeDelim(InfixSpecial._OPEN_CURLY,InfixSpecial._CLOSE_CURLY,-1,in,out);
					out.add(closeDelim(in, InfixSpecial._OPEN_CURLY));
					break;
					
				//At this point, all delims should be balanced
				//If they arent, throw an error
				case InfixSpecial._CLOSE_PAREN:
					throw new SyntaxError("Unexpected token ')'");
				case InfixSpecial._CLOSE_CURLY:
					throw new SyntaxError("Unexpected token ')'");
						
				default:
					out.add(current);
					//throw new RuntimeException("Compiler -> assemble -> isSpecial");
				}
			}
			
			else {
				out.add(current);
			}
		}
					
		return out;
	}
	
	public static InfixItemList desugar(InfixItemList in) {
		in.init();
		InfixItemList out = new InfixItemList();
		
		InfixItem current;
		while (in.hasNext()) {
			current = in.next();
			
			if(current.isVariable()) {
				
				//Function
				if(in.hasNext() && in.peekNext().isTuple()) {
					//Function Arguments
					InfixTuple args = in.next().toTuple();
					
					int numOfItems = args.getItems().size();
					
					InfixFunction fun = InfixTable.getFunction(current.toVariable().getVarName());
					
					//Valid Function
					if (fun == null) {
						InstructionStack is = new InstructionStack();
						is.push(new Variable(current.toVariable().getVarName()));
						fun = new InfixFunction(current.toVariable().getVarName(), numOfItems, is);
					}
					
					//Check number of arguments
					if(numOfItems != fun.getNumOfArgs()) {
						throw new RuntimeException("Invalid number of arguments for function '" + fun.getName() + "'");
					}
					
					fun.setArgs(args);
					fun.desugar();
					//current.setArgs(args);
					//current.varToFun(true, fun);
					//current.desugar();
					out.add(fun);
					continue;
				}
				
				//Variable
				else {
					//Add the variable name
					out.add(current);
				}
			} else if (current.isTuple()) {
				//At this point, all token
				current.desugar();
				out.add(current);
			}
			
			//Special
			else if (current.isSpecial()) {
				switch(current.getPrimaryKey()) {
				case InfixSpecial._EQUALS:
					InfixExpression var = new InfixExpression(out.pop());
					InfixExpression exp = new InfixExpression();
					while(in.hasNext()) {
						exp.add(in.next());
					}
					exp.desugar();
					InfixFunction assign = InfixTable.getFunction("assign");
					assign.setArgs(new InfixTuple(var, exp));
					out.add(assign);
					
					break;
				default:
					throw new RuntimeException("special token error");
				}
			}
			
			
			else {
				current.desugar();
				out.add(current);
			}
		}
		
		return out;
	}
	
	public static ArrayList<InfixItem> organize(ArrayList<InfixItem> tokens) {
		ArrayList<InfixItem> out = new ArrayList<InfixItem>();
		
		for(int ix = 0; ix < tokens.size(); ix++) {
			InfixItem t = tokens.get(ix);
			
			//If it is a literal, just add it to the "stack"
			if(t.isLiteral() || t.isVariable()) {
				t.organize();
				out.add(t);
			}
			
			else if(t.isTuple() || t.isFunction()) {
				t.organize();
				out.add(t);
			}

			
			else if(t.isOperator()) {
				InfixOperator op = t.toOp();
				int precedence = op.getPrecedence();
				
				//Start from the back of the "stack" and search for operators
				//If an operator of higher precedence is hit, add the operator
				//and its argument on top of the operator
				//If an operator is lower precedence is hit, add the operator
				// and its argument below the operator
				int last = out.size()-1;
					
				//Operator on stack has lower precedence
				//Left to Right Associativity
				if(op.isLeftToRight() && out.get(last).isOperator() && out.get(last).toOp().getPrecedence() < precedence) {
					
					int j = 0;
					while(out.get(last-j).isOperator() && out.get(last-j).toOp().getPrecedence() < precedence) {
						if(last-j <= 0)
							break;
						j++;
					}
					
					//Operand
					InfixItem operand = tokens.get(++ix);
					if(operand.isTuple() || operand.isFunction()) {
						operand.organize();
					}
					out.add(last-j+1, operand); //(+1 to add it just before the previous operator)
					//Add one to 'last' because out is one element larger (see previous line)
					out.add(last+2-j, op); //Operator
				} 
				//Right to Left Associativity
				else if(!op.isLeftToRight() && out.get(last).isOperator() && out.get(last).toOp().getPrecedence() <= precedence) {
					
					int j = 0;
					while(out.get(last-j).isOperator() && out.get(last-j).toOp().getPrecedence() <= precedence) {
						if(last-j <= 0)
							break;
						j++;
					}
					
					//Operand
					InfixItem operand = tokens.get(++ix);
					operand.organize();
					out.add(last-j+1, operand); //(+1 to add it just before the previous operator)
					//Add one to 'last' because out is one element larger (see previous line)
					out.add(last+2-j, op); //Operator
				} 
				
				
				// Operator on stack has greater precedence
				// Or it is not an operator (literal, function, etc.)
				else {
					//Operand
					InfixItem operand = tokens.get(++ix);
					operand.organize();
					out.add(operand); //(+1 to add it just before the previous operator)
					out.add(op); // Operator
				}
			}
		}
		Collections.reverse(out);
		return out;
	}
	
	@SuppressWarnings("unused")
	private static ArrayList<InfixItem> flatten(ArrayList<InfixItem> tokens) {
		ArrayList<InfixItem> out = new ArrayList<InfixItem>();
		
		for (InfixItem t : tokens) {
			if(t.isExpression()) {
				out.addAll(t.toExpression().getItems());
			} else if (t.isFunction()) {
				out.add(t);
				ArrayList<InfixExpression> args = t.toFunction().getArgs().getItems();
				for (InfixExpression e : args) {
					out.addAll(flatten(e.getItems()));
				}
			} else {
				out.add(t);
			}
		}
		
		return out;
	}
	
	public static Block generate(ArrayList<InfixItem> input) {
		InstructionStack is = new InstructionStack();
		
		for (InfixItem t : input) {
			is.addISorOBJ(t.generateElementCode());
		}
		
		return new Block(is);
	}
	
	
	/* ****************
	 * UTILITIES
	 * ****************/
	
	public static InfixItem closeDelim(InfixItemList in, int type) {
		InfixTuple allExps = new InfixTuple();
		InfixExpression exp = new InfixExpression();
		
		//Set up open and close delims
		int open = type, close;
		switch(open) {
		case InfixSpecial._OPEN_CURLY:
			close = InfixSpecial._CLOSE_CURLY;
			break;
		case InfixSpecial._OPEN_PAREN:
			close = InfixSpecial._CLOSE_PAREN;
			break;
		default:
			throw new RuntimeException();
		}
		
		while(in.hasNext()) {
			InfixItem current = in.next();
			
			if(!current.isSpecial()) {
				exp.add(current);
			}
			
			
			//All are assuming 'current' is an instance if InfixSpecial
			else if(current.getPrimaryKey() == close) {
				break;
			}
			
			else if (current.getPrimaryKey() == InfixSpecial._OPEN_PAREN
					|| current.getPrimaryKey() == InfixSpecial._OPEN_CURLY) {
				exp.add(closeDelim(in, current.getPrimaryKey()));
			} 
			
			else if (current.getPrimaryKey() == InfixSpecial._COMMA) {
				exp.assemble();
				allExps.add(new InfixExpression(exp)); //Copy exp
				exp = new InfixExpression(); //Then reset it
				//in.next();//Skip the comma
			}
			
			else {
				throw new RuntimeException();
			}
		}
		
		//in.next(); //Skip the closing delim
		exp.assemble();
		allExps.add(exp);
		
		//Block Literal
		if(type == InfixSpecial._OPEN_CURLY) {
			return new InfixBlock(allExps);
		}
		//Tuple
		else {
			return allExps;
		}
		
	}
	
	/** Returns true if the character is lowercase a-z */
	private static boolean isLowerAlpha(char c) {
		return (c >= 'a' && c <= 'z');
	}
	
	
	public static String show(ArrayList<InfixItem> tokens) {
		StringBuilder sb = new StringBuilder();
		for (InfixItem token : tokens) {
			sb.append(token.toString());
			sb.append(' ');
		}
		return sb.toString().substring(0, sb.length()-1);
	}
	
	public static String run(String in, boolean debug) {
		Block b = compile(in, debug);
		Element.instance.run(b);
		String res = Element.instance.getOut().dumpAsString();
		
		if(debug) println(res);
		
		return res;
	}
	
	public static void initVars() {
		run("sqrt  	= eval(\"	{Mq}	\")", false);
		run("abs   	= eval(\"	{MA}	\")", false);
		run("fact  	= eval(\"	{M!}	\")", false);
		run("log   	= eval(\"	{ML}	\")", false);
		run("ln    	= eval(\"	{Ml}	\")", false);
		run("ceil  	= eval(\"	{M{}	\")", false);
		run("floor 	= eval(\"	{M}}	\")", false);
		run("mod   	= eval(\"	{\\\\%}	\")", false);

		run("sum   	= eval(\"	{S}		\")", false);

		run("sin 	= eval(\"	{Ms}	\")", false);
		run("cos 	= eval(\"	{Mc}	\")", false);
		run("tan 	= eval(\"	{Mt}	\")", false);
		run("acos	= eval(\"	{MC}	\")", false);
		run("asin	= eval(\"	{MS}	\")", false);
		run("atan	= eval(\"	{MT}	\")", false);
	}
	
	
	public static void main(String[] args) {
		initVars();
		
		if(!(args.length == 1 && args[0].equals("-debug"))) {
			run("assign(sqrt, eval(\"{Mq}\"))", false);			
			CompilerTestCases ctc = new CompilerTestCases();
			if(ctc.testAll(false)) {
				println("All tests passed!");
			}
			
			
			System.out.println("Element Version: Oct 2015 (alpha)");
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			String input = "";
			while (true) {
				try {
					System.out.print("> ");
					input = scanner.nextLine();
					
					if(input.equals("")) {
						continue;
					} else if(input.equals(".#")) {
						return; //Exit the program
					} else {
						run(input, true);
					}
				} catch (Exception e) {
					System.out.println("EXCEPTION: StackGUI.runScriptPane()");
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					//out.setError(true);
					println(sw.toString());
					
					//scanner.nextInt(); //pause
				}
			}
		} else {
			
			//run();
		
//			CompilerTestCases ctc = new CompilerTestCases();
//			if(ctc.testAll(false)) {
//				println("All tests passed!");
//			}
			
			run("sqrt #> 1:5", true);
			
			
	//		run("(1 2)+2");
			
//			String input = "mod(1+3,2+4)";
//			println("INPUT: " + input); 
//			
//			InfixItemList testTokens = tokenize(input);
//			println("\tTOKENS: " + testTokens.toString());
//			
//			testTokens = assemble(testTokens);
//			println("\tASSMBL: " + testTokens.toString());
//			
//	
//			testTokens = desugar(testTokens);
//			println("\tDESHGR: " + testTokens.toString());
//			
//			
//			ArrayList<InfixItem> testTokensAL = organize(testTokens.getArrayList());
//			println("\tORGNIZ: " + testTokensAL.toString());
//	
//			testTokensAL = flatten(testTokensAL);
//			println("\tFLATEN: " + testTokensAL.toString());
//			
//			Block blok = generate(testTokensAL);
//			println("\tGENELM: " + blok.toString());
//			
//	
//			run("3+2:3^2K10-1/2+2/4:sqrt(12)^2+1/10");
//			
//			run("eval(\"1 1+\")");
			
			//run("ceil(1)+cos(1)/cos(1)");
		
			//run("cos(cos(9))");
			//EXPEC: 0.8843052269
			//RETUR: -0.9589838609
//			
//			run("assign(x,3)");
//			run("abs(x)");
		}
		
		
		
	}
	
	
	public static void println(Object o){System.out.println(o.toString());}
}

