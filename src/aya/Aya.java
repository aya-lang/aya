package aya;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.PatternSyntaxException;

import aya.entities.operations.ColonOps;
import aya.entities.operations.DotOps;
import aya.entities.operations.MathOps;
import aya.entities.operations.Ops;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.AyaUserRuntimeException;
import aya.exceptions.SyntaxError;
import aya.exceptions.TypeError;
import aya.obj.block.Block;
import aya.parser.CharacterParser;
import aya.parser.Parser;
import aya.util.StringSearch;
import aya.variable.VariableData;

public class Aya extends Thread {
	public static final boolean DEBUG = true;
	public static final String QUIT = "\\q";
	
	public static final String VERSION_NAME = "Beta 0.1.0 (Dec 2016)";
	public static String ayarcPath = "ayarc.aya";
	
	public static boolean PRINT_LARGE_ERRORS = true;
	
	private PrintStream _out = System.out;
	private PrintStream _err = System.err;
	private InputStream _in = System.in;
	private Scanner _scanner = new Scanner(_in);
	private final BlockingQueue<String> _input = new LinkedBlockingQueue<String>();
	private StringSearch _helpData;
	private VariableData _variables;
	private static Aya _instance = getInstance();
	private long _lastInputRunTime = 0;

	protected Aya() {
		//Exists only to defeat instantiation
	}
	
	
	public static Aya getInstance() {
		if(_instance == null) {
			_instance = new Aya();
			_instance._helpData = new StringSearch(getQuickSearchData());
			_instance._variables = new VariableData(_instance);
			//instance.out = new AyaStdout();
			CharacterParser.initMap();
			AyaPrefs.init();
		}
		return _instance;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String input = _input.take();
				synchronized(this) {
					if (input.equals(QUIT)) {
						notify();
						break;
					}
					
					long startTime = System.currentTimeMillis();
					_instance.run(input);
					_lastInputRunTime = System.currentTimeMillis() - startTime;
					
					notify();
				}
				
			} catch (InterruptedException e) {
				System.err.println("Aya interupted: " + e);
			}
		}
	}
	
	public void queueInput(String s) {
		_input.offer(s);
	}
	
	public String nextLine() {
		return _scanner.nextLine();
	}
	
	public VariableData getVars() {
		return _instance._variables;
	}

	
	public void addHelpText(String in) {
		_instance._helpData.addUnique(in);
	}
	
	public StringSearch getHelpData() {
		return _helpData;
	}
	
	public static String[] getQuickSearchData() {
		if(_instance._helpData == null) {
			ArrayList<String> searchList = new ArrayList<String>();
			
			searchList.addAll(Ops.getAllOpDescriptions());
			searchList.addAll(MathOps.getAllOpDescriptions());
			searchList.addAll(DotOps.getAllOpDescriptions());
			searchList.addAll(ColonOps.getAllOpDescriptions());
			//searchList.addAll(this.variables.getDefaultVariableDiscs(this));
			
			searchList.addAll(CharacterParser.getAllCharDiscs());	//always add last
			
			return searchList.toArray(new String[searchList.size()]);
		} else {
			return _instance._helpData.getAllItems();
		}
	}
	
	
	//Returns true if load was successful
	public boolean loadAyarc() {
		//Load the standard library
		try {
			getInstance().queueInput("\"" + ayarcPath + "\"G~");
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public long getLastInputRunTime() {
		return _lastInputRunTime;
	}

	

	////////////////////////
	// IO GETTERS/SETTERS //
	////////////////////////
	
	public PrintStream getOut() {
		return _out;
	}
	
	public PrintStream getErr() {
		return _err;
	}
	
	public InputStream getIn() {
		return _in;
	}
	
	public void setOut(PrintStream ps) {
		_out = ps;
	}
	
	public void setErr(PrintStream ps) {
		_err = ps;
	}
	
	public void setIn(InputStream is) {
		_in = is;
	}
	
	
	
	
	//////////////////////
	// THREAD OVERRIDES //
	//////////////////////
	
	private void run(String str) {
		try {
			run(Parser.compile(str, this));
		} catch (SyntaxError e) {
			_instance._err.println("SYNTAX ERROR: " + e.getSimpleMessage());
		}
	}
	
	
	//////////////////////
	// PRINTING METHODS //
	//////////////////////
	
	public void print(Object o) {_instance._out.print(o.toString());}
	public void println(Object o) {_instance._out.println(o.toString());}
	public void printDebug(Object o) {if (DEBUG) _instance._out.println(o.toString());}
	public void printEx(Object o) {_instance._err.print(o.toString());}
	
	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
	/** Run a block */
	private void run(Block b) {
		try {
			b.eval();
			println(b.getPrintOutputState());
		} catch (TypeError te) {
			_instance._err.println("TYPE ERROR: " + te.getSimpleMessage());
		} catch (SyntaxError se) {
			_instance._err.println("SYNTAX ERROR: " + se.getSimpleMessage());
		} catch (AyaRuntimeException ere) {
			_instance._err.println("ERROR: " + ere.getSimpleMessage());
		} catch (PatternSyntaxException pse) {
			_instance._err.println(exToSimpleStr(pse));
		} catch (EmptyStackException ese) {
			_instance._err.println("Unexpected empty stack");
		} catch (IndexOutOfBoundsException iobe) {
			_instance._err.println(exToSimpleStr(iobe));
		} catch (AyaUserRuntimeException eure) {
			_instance._err.println(eure.getSimpleMessage());
		} 
		catch (Exception e2) {
			System.out.println("EXCEPTION: Unhandled exception in Aya.run(Block)");
			if(PRINT_LARGE_ERRORS) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e2.printStackTrace(pw);
				_instance._err.println(sw.toString());
			} else {
				_instance._err.println("Error");
			}
		} finally {
			_instance._variables.reset();
		}
	}
	
	////////////////////
	// HELPER METHODS //
	////////////////////
	
	private String exToSimpleStr(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString().split("\n")[0];
	}

}


