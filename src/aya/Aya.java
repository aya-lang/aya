package aya;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import aya.eval.AyaThread;
import aya.exceptions.parser.ParserException;
import aya.ext.color.ColorInstructionStore;
import aya.ext.date.DateInstructionStore;
import aya.ext.debug.DebugInstructionStore;
import aya.ext.dialog.DialogInstructionStore;
import aya.ext.fstream.FStreamInstructionStore;
import aya.ext.graphics.GraphicsInstructionStore;
import aya.ext.image.ImageInstructionStore;
import aya.ext.json.JSONInstructionStore;
import aya.ext.la.LinearAlgebraInstructionStore;
import aya.ext.plot.PlotInstructionStore;
import aya.ext.socket.SocketInstructionStore;
import aya.ext.sys.SystemInstructionStore;
import aya.instruction.named.NamedInstructionStore;
import aya.instruction.named.NamedOperator;
import aya.instruction.op.ColonOps;
import aya.instruction.op.DotOps;
import aya.instruction.op.MiscOps;
import aya.instruction.op.OpDocReader;
import aya.instruction.op.Operator;
import aya.instruction.op.Ops;
import aya.obj.symbol.SymbolTable;
import aya.parser.Parser;
import aya.parser.SourceString;
import aya.parser.SpecialNumberParser;
import aya.util.StringSearch;

public class Aya extends Thread {
	public static final boolean DEBUG = true;
	public static final String QUIT = "\\Q";
	
	public static final String VERSION_NAME = "v0.4.0";
	public static String ayarcPath = "ayarc.aya";
	
	public static boolean PRINT_LARGE_ERRORS = true;
	
	private AyaStdIO _io;
	private Scanner _scanner; 
	private final BlockingQueue<String> _input = new LinkedBlockingQueue<String>();
	private StringSearch _helpData;
	private static Aya _instance = getInstance();
	private long _lastInputRunTime = 0;
	private ArrayList<NamedInstructionStore> _namedInstructionStores = new ArrayList<NamedInstructionStore>();
	private SymbolTable _symbolTable = new SymbolTable();
	private AyaThread _root = null;
	
	
	protected Aya() {
		_io = new AyaStdIO(System.out, System.err, System.in);
		_scanner = new Scanner(_io.in(), "UTF-8");
	}
	
	
	public static Aya getInstance() {
		if(_instance == null) {
			_instance = new Aya();
			_instance._root = AyaThread.createRoot(_instance._io);
			// Init global vars
			_instance._root.getVars().initGlobals(_instance._root);
			AyaPrefs.init();
			_instance.initNamedInstructions();
		}
		return _instance;
	}
	
	public AyaThread deleteme_getRoot() {
		return _root;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String input = _input.take();
				
				synchronized(this) {
					if (input.equals(QUIT)) {
						break;
					}
					
					long startTime = System.currentTimeMillis();
					_instance.run(new SourceString(input, "<interactive>"));
					_lastInputRunTime = System.currentTimeMillis() - startTime;
					
					if (_input.isEmpty()) {
						notify();
					}
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

	
	///////////////
	// HELP DATA //
	///////////////
	
	private void initHelpData() {
		if(_instance._helpData == null) {
			
			//Make sure all classes are loaded
			try
			{
			  loadOps(Ops.OPS);
			  loadOps(Ops.EXTRA_OPS);
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
	private void loadOps(Operator[] ops) {
		for (Operator o : ops) {
			if (o != null) o.getClass();
		}
	}
	
	
	//Returns true if load was successful
	public boolean loadAyarc() {
		//Load the standard library
		try {
			String pathString = Paths.get(AyaPrefs.getAyaDir(), ayarcPath).toString().replace("\\", "\\\\");
			getInstance().queueInput("\"" + pathString + "\":F");
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
		return _io.out();
	}
	
	public PrintStream getErr() {
		return _io.err();
	}
	
	public InputStream getIn() {
		return _io.in();
	}
	
	public Scanner getInScanner() {
		return _scanner;
	}

	public void setOut(OutputStream os) {
		_io.setOut(os);
	}
	
	public void setErr(OutputStream os) {
		_io.setErr(os);
	}
	
	public void setIn(InputStream is) {
		_io.setIn(is);
		_scanner = new Scanner(_io.in(), "UTF-8");
	}
	
	
	
	
	//////////////////////
	// THREAD OVERRIDES //
	//////////////////////
	
	private void run(SourceString str) {
		try {
			_root.run(Parser.compile(str, this));
		} catch (ParserException e) {
			_io.err().println("Syntax Error: " + e.getSimpleMessage());
		}
	}
	
	
	//////////////////////
	// PRINTING METHODS //
	//////////////////////
	
	public void printDebug(Object o) {if (DEBUG) _io.out().println(o.toString());}
	
	////////////////////////
	// Named Instructions //
	////////////////////////

	private void initNamedInstructions() {
		_namedInstructionStores.add(new DebugInstructionStore());
		_namedInstructionStores.add(new JSONInstructionStore());
		_namedInstructionStores.add(new ImageInstructionStore());
		_namedInstructionStores.add(new GraphicsInstructionStore());
		_namedInstructionStores.add(new FStreamInstructionStore());
		_namedInstructionStores.add(new SystemInstructionStore());
		_namedInstructionStores.add(new DialogInstructionStore());
		_namedInstructionStores.add(new PlotInstructionStore());
		_namedInstructionStores.add(new DateInstructionStore());
		_namedInstructionStores.add(new SocketInstructionStore());
		_namedInstructionStores.add(new ColorInstructionStore());
		_namedInstructionStores.add(new LinearAlgebraInstructionStore());
		
		for (NamedInstructionStore x : _namedInstructionStores) {
			x.initHelpData(getInstance());
		}
		
	}
	public NamedOperator getNamedInstruction(String name) {
		for (NamedInstructionStore x : _namedInstructionStores) {
			NamedOperator i = x.getInstruction(name);
			if (i != null) {
				return i;
			}
		}
		return null;
	}

	/////////////////////
	// SYMBOL TABLE    //
	/////////////////////
	
	public SymbolTable getSymbols() {
		return _symbolTable;
	}
	
	
	/////////////////////
	// PRIVATE METHODS //
	/////////////////////
	

	
	////////////////////
	// HELPER METHODS //
	////////////////////


	public void quit() {
		queueInput(Aya.QUIT);
	}


	/** Return true if input is ready to be read */
	public boolean isInputAvaiable() {
		try {
			return _io.in().available() > 0;
		} catch (IOException e) {
			return false;
		}
	}

}


