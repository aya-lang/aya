package aya;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import aya.eval.ExecutionContext;
import aya.obj.block.StaticBlock;
import aya.parser.Parser;
import aya.parser.SourceString;

public class Aya extends Thread {
	
	private AyaStdIO _io;
	private Scanner _scanner; 
	private final BlockingQueue<ExecutionRequest> _input = new LinkedBlockingQueue<ExecutionRequest>();
	private static Aya _instance = getInstance();
	private ExecutionContext _root = null;
	
	
	protected Aya() {
		_io = new AyaStdIO(System.out, System.err, System.in);
		_scanner = new Scanner(_io.in(), "UTF-8");
	}
	
	
	public static Aya getInstance() {
		if(_instance == null) {
			_instance = new Aya();
			_instance._root = ExecutionContext.createRoot(_instance._io);
			// Init the static data
			StaticData.getInstance().init();
			// Init global vars
			_instance._root.getVars().initGlobals(_instance._root);
			AyaPrefs.init();
		}
		return _instance;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				ExecutionRequest input = _input.take();
				
				synchronized(this) {
					if (input == null) {
						_io.out().println("Exiting...");
						break;
					}
					
					//_instance.run(new SourceString(input, "<interactive>"));
					_root.run(input.getBlock());

					if (_input.isEmpty()) {
						notify();
					}
				}
				
			} catch (InterruptedException e) {
				System.err.println("Aya interupted: " + e);
			}
		}
	}
	
	public void queueInput(ExecutionRequest request) {
		_input.offer(request);
	}
	
	public String nextLine() {
		return _scanner.nextLine();
	}
	
	public Scanner getScanner() {
		return _scanner;
	}

	
	//Returns true if load was successful
	public boolean loadAyarc() {
		//Load the standard library
		try {
			String pathString = Paths.get(AyaPrefs.getAyaDir(), StaticData.ayarcPath).toString().replace("\\", "\\\\");
			StaticBlock blk = Parser.compileSafeOrNull(new SourceString("\"" + pathString + "\":F", "<ayarc loader>"), _io);
			if (blk != null) getInstance().queueInput(new ExecutionRequest(blk));
		} catch (Exception e) {
			return false;
		}
		return true;
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
	
	//private void run(SourceString str) {
	//	try {
	//		_root.run(Parser.compile(str));
	//	} catch (ParserException e) {
	//		_io.err().println("Syntax Error: " + e.getSimpleMessage());
	//	}
	//}
	
	
	//////////////////////
	// PRINTING METHODS //
	//////////////////////
	
	public void printDebug(Object o) {if (StaticData.DEBUG) _io.out().println(o.toString());}
	
	
	////////////////////
	// HELPER METHODS //
	////////////////////


	public void quit() {
		queueInput(null);
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


