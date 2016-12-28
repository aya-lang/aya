package element.infix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import element.Element;
import element.entities.InstructionStack;
import element.entities.operations.Ops;
import element.obj.block.Block;
import element.parser.Parser;
import element.variable.Variable;

public class InfixTable {	
	private static final InfixTable ifxt = new InfixTable();
	private static Element e = Element.getInstance();

	
	
	private static final Map<String, InfixOperator> infixOpTable;
	static {
		Map<String, InfixOperator> z1 = new HashMap<String, InfixOperator>();
		
//		InfixOperator equals = new InfixOperator("=", null, 0, InfixOperator.RIGHT_TO_LEFT);
//		equals.setFlag(InfixOperator.EQUALS);
//	 	z1.put("=", equals);
	 	z1.put("K", new InfixOperator("K", Parser.compileIS("K", e), 1, InfixOperator.LEFT_TO_RIGHT));
	 	z1.put("#>", new InfixOperator("#>", Parser.compileIS("{f l, [l,f]}~", e), 1, InfixOperator.LEFT_TO_RIGHT));

		
	 	z1.put(":", new InfixOperator(":", Parser.compileIS("KR", e), 2, InfixOperator.LEFT_TO_RIGHT));
	 	z1.put("<", new InfixOperator("<", Parser.compileIS("<", e), 2, InfixOperator.LEFT_TO_RIGHT));
	 	z1.put(">", new InfixOperator(">", Parser.compileIS(">", e), 2, InfixOperator.LEFT_TO_RIGHT));

	 	z1.put("+", new InfixOperator("+", Parser.compileIS("+", e), 3, InfixOperator.LEFT_TO_RIGHT));
	 	z1.put("-", new InfixOperator("-", Parser.compileIS("-", e), 3, InfixOperator.LEFT_TO_RIGHT));
	 	z1.put("*", new InfixOperator("*", Parser.compileIS("*", e), 5, InfixOperator.LEFT_TO_RIGHT));
	 	z1.put("/", new InfixOperator("/", Parser.compileIS("/", e), 5, InfixOperator.LEFT_TO_RIGHT));
	 	z1.put("%", new InfixOperator("%", Parser.compileIS("%", e), 5, InfixOperator.LEFT_TO_RIGHT));
	 	z1.put("^", new InfixOperator("^", Parser.compileIS("^", e), 7, InfixOperator.RIGHT_TO_LEFT));
	 	
	 		 	
	 	infixOpTable = Collections.unmodifiableMap(z1);
	 }
	
	
	private static final Map<String, InfixFunction> functionTable;
	static {
		Map<String, InfixFunction> z2 = new HashMap<String, InfixFunction>();
		
		//Basic Math
//	 	z2.put("abs", new InfixFunction("abs", 1, Parser.compileIS("MA")));
//	 	z2.put("fact", new InfixFunction("fact", 1, Parser.compileIS("M!")));
//	 	z2.put("log", new InfixFunction("log", 1, Parser.compileIS("ML")));
//	 	z2.put("ln", new InfixFunction("ln", 1, Parser.compileIS("Ml")));
//	 	//z2.put("sqrt", new InfixFunction("sqrt", 1, Parser.compileIS("Mq")));
//	 	z2.put("ceil", new InfixFunction("ceil", 1, Parser.compileIS("M{")));
//	 	z2.put("floor", new InfixFunction("floor", 1, Parser.compileIS("M}")));
//	 	z2.put("mod", new InfixFunction("mod", 2, Parser.compileIS("\\%")));
	 	
	 	//List
	 	//z2.put("sum", new InfixFunction("sum", 1, Parser.compileIS("S")));


	 	
	 	z2.put("eval", new InfixFunction("eval", 1, Parser.compileIS("~", e)));



	 	//Trig
//	 	z2.put("sin", new InfixFunction("sin", 1, Parser.compileIS("Ms")));
//	 	z2.put("cos", new InfixFunction("cos", 1, Parser.compileIS("Mc")));
//	 	z2.put("tan", new InfixFunction("tan", 1, Parser.compileIS("Mt")));
//	 	z2.put("acos", new InfixFunction("acos", 1, Parser.compileIS("MC")));
//	 	z2.put("asin", new InfixFunction("asin", 1, Parser.compileIS("MS")));
//	 	z2.put("atan", new InfixFunction("atan", 1, Parser.compileIS("MT")));

	 	z2.put("assign", ifxt.new InfixFunction_Assign());
	 	z2.put("map", ifxt.new InfixFunction_Map());
	 	
	 	//Compiler.run("add = eval(\"{+}\")", false);
	 	
	 	functionTable = Collections.unmodifiableMap(z2);
	 }
	
	public class InfixFunction_Assign extends InfixFunction {
		public InfixFunction_Assign() {
			super("assign", 2, null);
		}
		public InstructionStack generateElementCode() {
			InfixExpression fun = args.getItems().get(0);
			InfixExpression list = args.getItems().get(1);

			InstructionStack is = new InstructionStack();

			is.push(Ops.getOp('#'));
			
			InstructionStack mappedFun = new InstructionStack();
			//mappedFun.addISorOBJ(fun.generateElementCode());
			Object obj = fun.generateElementCode();
			if(obj instanceof InstructionStack) {
				InstructionStack mapis = (InstructionStack)obj;
				if(mapis.size() == 1 && mapis.peek(0) instanceof Block) {
					mappedFun.addISorOBJ(((Block)mapis.pop()).getInstructions());
				} else {
					mappedFun.addISorOBJ(obj);
				}
			} else {
				mappedFun.addISorOBJ(obj);
			}
			Block b = new Block(mappedFun);
			is.push(b);
			
			is.addISorOBJ(list.generateElementCode());
			
	
			
			
			
			return is;
		}
		public InfixFunction_Map duplicate() {
			return new InfixFunction_Map();
		}
	}
	
	public class InfixFunction_Map extends InfixFunction {
		public InfixFunction_Map() {
			super("map", 2, null);
		}
		public InstructionStack generateElementCode() {
			InstructionStack is = new InstructionStack();
			
			InfixExpression var = args.getItems().get(0);
			InfixExpression exp = args.getItems().get(1);
			
			Variable v = new Variable(var.getItems().get(0).toVariable().getVarName());
			v.flagBind();
			is.push(v);
			is.addISorOBJ(exp.generateElementCode());
			
			return is;
		}
		public InfixFunction_Assign duplicate() {
			return new InfixFunction_Assign();
		}
	}
	
//	private static final Map<String, InfixFunction> staticFunctionTable;
//	static {
//		Map<String, InfixFunction> z3 = new HashMap<String, InfixFunction>();
//		
//		z3.put("assign", F_ASSIGN);
//	 	
//	 	staticFunctionTable = Collections.unmodifiableMap(z3);
//	 }
//	    
	public static InfixOperator getInfixOp(String str) {
		return infixOpTable.get(str);
	}
	
	public static InfixFunction getFunction(String str) {
		InfixFunction f = functionTable.get(str);
		if (f == null) {
			return null;
		} else {
			return f.duplicate();
		}
	}
	
	public static boolean isOpChar(char c) {
		// = ;  .
		char[] op_chars = {'~','`','!','@','#','$','%','^','&','*','-','_','+','|','\\',':','<','>','?','/'};
		if (c >= 'A' && c <= 'Z') {
			return true;
		}
		for (char o : op_chars) {
			if (o == c) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<InfixOperator> parseOP(String opstr) {
		InfixOperator op = getInfixOp(opstr);
		ArrayList<InfixOperator> ops = new ArrayList<InfixOperator>();
		if(op == null) {
			if(opstr.length() == 1) {
				throw new RuntimeException("Operator '" + opstr + "' does not exist");
			} else {
				ops.addAll(parseOP(opstr.substring(0, opstr.length()-1)));
				ops.addAll(parseOP(opstr.substring(opstr.length()-1)));
			}
		} else {
			ops.add(op);
		}
		return ops;
	}
}
