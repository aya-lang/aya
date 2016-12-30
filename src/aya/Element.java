package aya;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.regex.PatternSyntaxException;

import aya.entities.operations.ColonOps;
import aya.entities.operations.DotOps;
import aya.entities.operations.MathOps;
import aya.entities.operations.Ops;
import aya.exceptions.ElementRuntimeException;
import aya.exceptions.ElementUserRuntimeException;
import aya.exceptions.SyntaxError;
import aya.exceptions.TypeError;
import aya.obj.block.Block;
import aya.parser.CharacterParser;
import aya.parser.Parser;
import aya.util.StringSearch;
import aya.variable.VariableData;

public class Element {
	public static final String VERSION_NAME = "Beta 0.1.0 (Dec 2016)";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";
	
	public static final int RETURN_EXIT = -2;
	public static final int RETURN_ERROR = -1;
	public static final int RETURN_SUCCESS = 0;
	public static final int CHANGE_TO_INFIX = 1;
	public static final int CHANGE_TO_STACK = 2;
	public static final int CLEAR_CONSOLE = 3;
	public static final int RUN_INFIX_TESTS = 4;
	
	public static boolean PRINT_LARGE_ERRORS = true;
	
	
	
	
	private ElementStdout out;
	StringSearch helpData;
	private VariableData variables;
	
	protected Element() {
		//Exists only to defeat instantiation
	}
	
	public static Element instance = getInstance();
	
	public static Element getInstance() {
		if(instance == null) {
			instance = new Element();
			instance.helpData = new StringSearch(getQuickSearchData());
			instance.variables = new VariableData(instance);
			instance.out = new ElementStdout();
			String charMapStatus = CharacterParser.initMap();
			if (!charMapStatus.equals("SUCCESS")) {
				instance.out.printEx("Error evaluating character map (base/charmap.txt):\n\t" + charMapStatus);
			}
			ElemPrefs.init();
		}
		return instance;
	}

	
	public VariableData getVars() {
		return instance.variables;
	}

	
	public void addHelpText(String in) {
		instance.helpData.addUnique(in);
	}
	
	public static String[] getQuickSearchData() {
		if(instance.helpData == null) {
			ArrayList<String> searchList = new ArrayList<String>();
			
			searchList.addAll(Ops.getAllOpDescriptions());
			searchList.addAll(MathOps.getAllOpDescriptions());
			searchList.addAll(DotOps.getAllOpDescriptions());
			searchList.addAll(ColonOps.getAllOpDescriptions());
			//searchList.addAll(this.variables.getDefaultVariableDiscs(this));
			
			searchList.addAll(CharacterParser.getAllCharDiscs());	//always add last
			
			return searchList.toArray(new String[searchList.size()]);
		} else {
			return instance.helpData.getAllItems();
		}
	}
	
	public void run(Block b) {
		try {
			b.eval();
			instance.out.print(b.getPrintOutputState());
		} catch (TypeError te) {
			instance.out.printEx("TYPE ERROR: " + te.getSimpleMessage());
		} catch (SyntaxError se) {
			instance.out.printEx("SYNTAX ERROR: " + se.getSimpleMessage());
		} catch (ElementRuntimeException ere) {
			instance.out.printEx("ERROR: " + ere.getSimpleMessage());
		} catch (PatternSyntaxException pse) {
			instance.out.printEx(exToSimpleStr(pse));
		} catch (EmptyStackException ese) {
			instance.out.print("Unexpected empty stack");
		} catch (IndexOutOfBoundsException iobe) {
			instance.out.printEx(exToSimpleStr(iobe));
		} catch (ElementUserRuntimeException eure) {
			instance.out.printEx(eure.getSimpleMessage());
		} 
//		catch (ClassCastException cce) {
//			instance.out.printEx(exToSimpleStr(cce));
//		} 
		catch (Exception e2) {
			System.out.println("EXCEPTION: Unhandled exception in Element.run(Block)");
			if(PRINT_LARGE_ERRORS) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e2.printStackTrace(pw);
				instance.out.printEx(sw.toString());
			} else {
				instance.out.printEx("Error");
			}
		} finally {
			instance.variables.reset();
		}
	}
	
	public void run(String str) {
		try {
			run(Parser.compile(str, this));
		} catch (SyntaxError e) {
			instance.out.printEx("SYNTAX ERROR: " + e.getSimpleMessage());
		}
	}
	
	
	public ElementStdout getOut() {
		return instance.out;
	}
	
//	public static void export(String name, String code, Element elem) {
//		//Create folder if needed
//		File dirFile = new File("exported_src\\");
//		if(!dirFile.exists()) {
//			dirFile.mkdir();
//		}
//
//		try {
//			
//			//Export the source code
//			SourceFile src = new SourceFile(new File("exported_src\\" + name + ".elem"), elem);
//			src.printOver(code);
//			
//			//Create the Runnable			
//			SourceFile runnable = new SourceFile(new File(name + ".bat"), elem);
//			runnable.printOver("@ECHO off\njava -jar element.jar -f \"exported_src\\" + name + ".elem\"\nPAUSE\nEXIT");
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
	
	public void println(Object o) {instance.out.println(o.toString());}
	public void print(Object o) {instance.out.print(o.toString());}
	public void printEx(Object o) {instance.out.printEx(o.toString());}
	
	public static String exToStr(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	private String exToSimpleStr(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString().split("\n")[0];
	}

}


