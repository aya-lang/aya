package aya;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.PatternSyntaxException;

import aya.entities.operations.ColonOps;
import aya.entities.operations.DotOps;
import aya.entities.operations.MiscOps;
import aya.entities.operations.Ops;
import aya.exceptions.AyaBuildException;
import aya.exceptions.AyaRuntimeException;
import aya.exceptions.AyaUserObjRuntimeException;
import aya.exceptions.AyaUserRuntimeException;
import aya.exceptions.TypeError;
import aya.ext.dialog.DialogInstructionStore;
import aya.ext.fstream.FStreamInstructionStore;
import aya.ext.graphics.GraphicsInstructionStore;
import aya.ext.image.ImageInstructionStore;
import aya.ext.json.JSONInstructionStore;
import aya.ext.sys.SystemInstructionStore;
import aya.instruction.named.NamedInstruction;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.op.OpDocReader;
import aya.instruction.op.OpInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.Str;
import aya.parser.CharacterParser;
import aya.parser.Parser;
import aya.parser.SpecialNumberParser;
import aya.util.StringSearch;
import aya.variable.VariableData;

public class Aya extends Thread {
	public static final boolean DEBUG = true;
	public static final String QUIT = "\\Q";
	
	public static final String VERSION_NAME = "v0.2.0";
	public static String ayarcPath = "ayarc.aya";
	
	public static boolean PRINT_LARGE_ERRORS = true;
	
	private PrintStream _out = System.out;
	private PrintStream _err = System.err;
	private InputStream _in = System.in;
	private Scanner _scanner = new Scanner(_in, "UTF-8");
	private final BlockingQueue<String> _input = new LinkedBlockingQueue<String>();
	private StringSearch _helpData;
	private VariableData _variables;
	private static Aya _instance = getInstance();
	private long _lastInputRunTime = 0;
	private ArrayList<NamedInstructionStore> _namedInstructionStores = new ArrayList<NamedInstructionStore>();
	
	private CallStack _callstack = new CallStack();
	
	public CallStack getCallStack() {
		return _callstack;
	}
	
	protected Aya() {
		//Exists only to defeat instantiation
	}
	
	
	public static Aya getInstance() {
		if(_instance == null) {
			_instance = new Aya();
			//_instance._helpData = new StringSearch(getQuickSearchData());
			_instance._variables = new VariableData(_instance);
			//instance.out = new AyaStdout();
			CharacterParser.initMap();
			AyaPrefs.init();
			_instance.initNamedInstructions();
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
						//notify();
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
	
	public Scanner getScanner() {
		return _scanner;
	}
	
	public VariableData getVars() {
		return _instance._variables;
	}

	
	///////////////
	// HELP DATA //
	///////////////
	
	private void initHelpData() {
		if(_instance._helpData == null) {
			
			//Make sure all classes are loaded
			try
			{
			  loadOps(Ops.OPS);
			  loadOps(MiscOps.MATH_OPS);
			  loadOps(ColonOps.COLON_OPS);
			  loadOps(DotOps.DOT_OPS);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			ArrayList<String> searchList = new ArrayList<String>();
			searchList.addAll(OpDocReader.getAllOpDescriptions());
			// Add additional help data
			searchList.add(AyaPrefs.CONSTANTS_HELP);
			searchList.add(SpecialNumberParser.STR_CONSTANTS_HELP);
			searchList.toArray(new String[searchList.size()]);
			_instance._helpData = new StringSearch(searchList);
		}
	}
	
	public StringSearch getHelpData() {
		_instance.initHelpData();
		return _helpData;
	}
	
	public void addHelpText(String in) {
		_instance.getHelpData().addUnique(in);
	}

	public static String[] getQuickSearchData() {
		return _instance.getHelpData().getAllItems();
	}
	
	/* This function does nothing but force java to load
	 * the operators and call the static blocks
	 */
	private void loadOps(OpInstruction[] ops) {
		for (OpInstruction o : ops) {
			if (o != null) o.getClass();
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

	
	public void setOut(OutputStream os) {
		try {
			_out = new PrintStream(os, true, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void setErr(OutputStream os) {
		try {
			_err = new PrintStream(os, true, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void setIn(InputStream is) {
		_in = is;
		_scanner = new Scanner(_in, "UTF-8");
	}
	
	
	
	
	//////////////////////
	// THREAD OVERRIDES //
	//////////////////////
	
	private void run(String str) {
		try {
			run(Parser.compile(str, this));
		} catch (AyaBuildException e) {
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
	
	////////////////////////
	// Named Instructions //
	////////////////////////

	private void initNamedInstructions() {
		_namedInstructionStores.add(new JSONInstructionStore());
		_namedInstructionStores.add(new ImageInstructionStore());
		_namedInstructionStores.add(new GraphicsInstructionStore());
		_namedInstructionStores.add(new FStreamInstructionStore());
		_namedInstructionStores.add(new SystemInstructionStore());
		_namedInstructionStores.add(new DialogInstructionStore());
		
		for (NamedInstructionStore x : _namedInstructionStores) {
			x.initHelpData(getInstance());
		}
		
	}
	public NamedInstruction getNamedInstruction(String name) {
		for (NamedInstructionStore x : _namedInstructionStores) {
			NamedInstruction i = x.getInstruction(name);
			if (i != null) {
				return i;
			}
		}
		return null;
	}
	
	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	
	/** Run a block */
	private void run(Block b) {
		try {
			b.eval();
			String s = b.getPrintOutputState();
			if (!s.equals("")) {
				println(s);
			}
		} catch (Exception e) {
			_instance._err.println(exToString(e));
			try {
				
				if (b.hasOutputState())
					_instance._err.println("stack:\n\t" + b.getPrintOutputState());
				if (b.getInstructions().size() > 0)
					_instance._err.println("just before:\n\t" + b.getInstructions().toString());
				if (!_callstack.isEmpty())
					_instance._err.print(_callstack.toString());
			} catch (Exception e2) {
				_instance._err.println("An additional error was thrown when attempting to print the stack state:");
				_instance._err.println(exToString(e2));
				_instance._err.println("This is likely caused by an error in an overloaded __str__ or __repr__ block.");
			}
		} finally {
			_instance._variables.reset();
			_instance._callstack.reset();
		}
	}
	
	////////////////////
	// HELPER METHODS //
	////////////////////
	
	public static String exToString(Exception e) {
		if (e instanceof TypeError) {
			return "TYPE ERROR: " + ((TypeError)e).getSimpleMessage();
		} else if (e instanceof AyaBuildException) {
			return ((AyaBuildException)e).getSimpleMessage();
		} else if (e instanceof AyaRuntimeException) {
			return "ERROR: " + ((AyaRuntimeException)e).getSimpleMessage();
		} else if (e instanceof PatternSyntaxException) {
			return exToSimpleStr(e);
		} else if (e instanceof EmptyStackException) {
			return "Unexpected empty stack";
		} else if (e instanceof AyaUserRuntimeException ) {
			return ((AyaUserRuntimeException)e).getSimpleMessage();
		} else if (e instanceof IndexOutOfBoundsException) {
			return ((IndexOutOfBoundsException)e).getMessage();
		}
		else {
			if(PRINT_LARGE_ERRORS) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				pw.println(AyaPrefs.BUG_MESSAGE);
				e.printStackTrace(pw);
				return sw.toString();
			} else {
				return "Error";
			}
		}
	}
	
	private static String exToSimpleStr(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString().split("\n")[0];
	}


	public void quit() {
		queueInput(Aya.QUIT);
	}


	/** Return true if input is ready to be read */
	public boolean isInputAvaiable() {
		try {
			return _instance._in.available() > 0;
		} catch (IOException e) {
			return false;
		}
	}


	public static Obj exceptionToObj(Exception e) {
		if (e instanceof AyaUserObjRuntimeException) {
			return ((AyaUserObjRuntimeException)e).getObj();
		} else {
			return new Str(exToString(e));
		}
	}

}


