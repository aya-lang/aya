package aya;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import aya.eval.ExecutionContext;
import aya.obj.Obj;
import aya.obj.block.StaticBlock;
import aya.obj.symbol.SymbolTable;
import aya.parser.Parser;
import aya.parser.SourceString;
import aya.util.FileUtils;

public class InteractiveAya {
	
	public static final int EXIT = 0;
	public static final int SUCCESS = 1;
	public static final int NONE = 2;
	public static final int CLS = 4;
	public static final int SKIP_WAIT = 5; // Do not wait for aya as no input was queued
	public static final int NORMAL_INPUT = 6; // Normal 8input was sent to aya, wait for it to complete
	public static final int EMPTY_INPUT = 7; // Empty string was sent as input
	private static boolean interactive = true;

	private boolean _echo = false;
	private boolean _showPromptText = true;
	private String _initcode = null;
	private String _usercmd = null;
	private static ConcurrentLinkedQueue<String> _scripts;
	
	public void setShowPrompt(boolean b) {_showPromptText = b;};
	public void setEcho(boolean b) {_echo = b;};
	
	private long _request_id_counter;
	
	private AyaThread _aya;
	
	private AyaStdIO _io() {
		return StaticData.IO;
	}
	
	protected InteractiveAya(AyaThread ayaThread) {
		_request_id_counter = 0;
		_aya = ayaThread;
		_scripts = new ConcurrentLinkedQueue<String>();
	}
	
	private long makeRequestID() {
		_request_id_counter++;
		return _request_id_counter;
	}

	public static final String HELP_TEXT = "Help:\n"
			+ "  \\QUIT or \\Q\n\tquit interactive Aya\n"
			+ "  \\HELP or \\H\n\tview this page\n"
			+ "  \\? <help text>\n\tsearch for help text in Aya\n"
			+ "  \\VERSION or \\V\n\tdisplay Ara version name\n"
			+ "  \\USERCMD <variable> [.] or \\: <variable> [.]\n"
			+ "\tInput will be parsed as \"\"\"input\"\"\" __aya__.interpreter.<variable>\n"
			+ "\tUse \"\\USERCMD -\" to return to normal\n"
			+ "\tUse \"\\USERCMD <variable> .\" to hide message\n";
	
	public int processInput(String input) {
		//Empty Input
		if(input.equals("")) {
			return EMPTY_INPUT;
		}
		
		// User Command
		if (_usercmd != null) {
			if (input.equals("\\USERCMD -") || input.equals("\\: -") ) {
				_usercmd = null;
				return SKIP_WAIT;
			} else {
				String code = "\"\"\"" + input + "\"\"\" __aya__.interpreter." + _usercmd;
				StaticBlock blk = Parser.compileSafeOrNull(new SourceString(code, "<USERCMD>"), StaticData.IO);
				if (blk != null) _aya.queueInput(new ExecutionRequest(makeRequestID(), blk));
				return NORMAL_INPUT;
			}
		}
		
		
		//Settings
		else if (input.charAt(0) == '\\') {

			
			String[] settings = input.split(" ");
			String command = settings[0].substring(1, settings[0].length());
			
			
			//Exit
			if(command.equals("Q")) {
				// Notify aya to exit
				_aya.quit();
				return EXIT; //return exit flag
			}
			
			
			//Help
			else if(command.equals("H") || command.equals("HELP")) {
				_io().out().println(HELP_TEXT);
				return SKIP_WAIT;
			}
			
			//Search
			else if(command.equals("?") && settings.length > 1) {
				String searchText = "";
				for (int i = 1; i < settings.length; i++) {
					searchText += settings[i] + " ";
				}
				searchText = searchText.substring(0, searchText.length()-1);
				
				StaticData.getInstance().getHelpData().clearFilter();
				StaticData.getInstance().getHelpData().applyNewFilter(searchText);
				if(StaticData.getInstance().getHelpData().getFilteredItems().size() == 0) {
					_io().out().println("No help data matching \"" + searchText + "\"");
				} else {
					for(String s : StaticData.getInstance().getHelpData().getFilteredItems()) {
						_io().out().println(s.replace("\n", "\n   "));
					}
				}
				
				return SKIP_WAIT;
			}
			
			//Version
			else if(command.equals("V") || command.equals("VERSION")) {
				_io().out().println(StaticData.VERSION_NAME);
				return SKIP_WAIT;
			}
			
			
			//User Command
			else if (command.equals(":") || command.equals("USERCMD")) {
				if (settings.length > 1 && SymbolTable.isBasicSymbolString(settings[1])) {
					_usercmd = settings[1];
					if (!(settings.length > 2 && settings[2].equals("."))) {
						_io().out().println("Input now being parsed as:\n\t\"\"\"input\"\"\" __aya__.interpreter." + _usercmd
								+ "\nType \"\\" + command + " -\" to return to normal"
								+ "\nType \"\\" + command + " " + _usercmd + " .\" to hide this message in the future");
					}
				} else {
					_io().err().println("usercmd: \"" + settings[1] + "\" is not a valid user command");
				}
				return SKIP_WAIT;
			}
			
			else if (SymbolTable.isBasicSymbolString(command)) {
				String code = splitAtFirst(' ', input).trim();
				if(code.equals("")) {
					_io().err().println("No input provided");
				} else {			
					// construct [ """ (code) """ varname ]
					code = "\"\"\"" + code + "\"\"\" __aya__.interpreter." + command;
					StaticBlock blk = Parser.compileSafeOrNull(new SourceString(code, "<USERCMD>"), StaticData.IO);
					if (blk != null) _aya.queueInput(new ExecutionRequest(makeRequestID(), blk));
					return NORMAL_INPUT;
				}
			}
						
			else {
				_io().err().println("Invalid command. Please make sure there is a space between command and its arguments.");
				return SKIP_WAIT;
			}
			
		}
		
		//Normal Input
		else {
			StaticBlock blk = Parser.compileSafeOrNull(new SourceString(input, "<input>"), StaticData.IO);
			if (blk != null) _aya.queueInput(new ExecutionRequest(makeRequestID(), blk));
			return NORMAL_INPUT;
		}
		
		_io().err().println("invalid input");
		return SKIP_WAIT;
	}
	
	private static String splitAtFirst(char splitter, String str) {
		int index = str.indexOf(' ');
		if (index == -1) {
			return "";
		} else {
			return str.substring(index, str.length());
		}
	}

	public void initCode(String code) {
		_initcode = code;
	}
	
	public void setPromptText(boolean b) {
		_showPromptText = false;
	}
	
	private void printResult(ExecutionResult result) {
		if (result != null) {
			switch (result.getType()) {
			case ExecutionResult.TYPE_SUCCESS:
				{
					ExecutionResultSuccess res = (ExecutionResultSuccess)result;
					for (Obj o : res.getData()) {
						_io().out().print(o);
					}
					_io().out().println();
				}
				break;
			case ExecutionResult.TYPE_EXCEPTION:
				{
					ExecutionResultException res = (ExecutionResultException)result;
					res.ex().print(_io().err());
					if (!res.callstack().equals("")) {
						_io().err().print(res.callstack());
					}
				}
				break;
			}
		}
	}

	public void loop() {
		boolean running = true;
		
 		_aya.start();
 		
		if (_initcode != null) {
			StaticBlock blk = Parser.compileSafeOrNull(new SourceString(_initcode, "<args>"), StaticData.IO);
			if (blk != null) _aya.queueInput(new ExecutionRequest(makeRequestID(), blk));
		}
		
		// Load ayarc
		String pathString = Paths.get(AyaPrefs.getAyaDir(), StaticData.ayarcPath).toString().replace("\\", "\\\\");
		StaticBlock blk = Parser.compileSafeOrNull(new SourceString("\"" + pathString + "\":F", "<ayarc loader>"), StaticData.IO);
		if (blk != null) _aya.queueInput(new ExecutionRequest(makeRequestID(), blk));
		
		// Get Aya I/O
		PrintStream out = _io().out();
		PrintStream err = _io().err();		
		Scanner scanner = _io().scanner();
		
		String input = "";
		int status;

		// TODO: Fix this
		// Note: Aya may still be in the middle of processing responses so this will not always
		// properly detect when the thread is done working. Sleep here to try to catch them
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Flush output
		while (_aya.hasOutput()) {
			try {
				printResult(_aya.waitForResponse());
			} catch (InterruptedException e) {
				out.println("Aya interrupted");
				e.printStackTrace(err);
				running = false;
				status = EXIT;
			}
		}
				
		// interactive is set on startup. If true, this is an infinite REPL loop
		while (running && (interactive || !_scripts.isEmpty())) {

			if (_showPromptText && interactive) {
				out.print(AyaPrefs.getPrompt());
			}
			
			// First run all scripts
			if (_scripts.size() > 0) {
				input = _scripts.poll();
			} else {
				try {
					input = scanner.nextLine();
				} catch (NoSuchElementException e) {
					// EOF Encountered
					_aya.quit();
					running = false;
				}
			}
			
			if (input.equals("")) {
				continue;
			}
			
			if (_echo && interactive) {
				out.println(AyaPrefs.getPrompt() + input);
			}
			
			status = processInput(input);
			
			if (status != SKIP_WAIT) {		
				try {
					ExecutionResult result = _aya.waitForResponse();
					printResult(result);
				} catch (InterruptedException e) {
					out.println("Aya interrupted");
					e.printStackTrace(err);
					running = false;
					status = EXIT;
				}
			}

			switch (status) {
			case EXIT:
				scanner.close();
				running = false;
				_aya.quit();
				break;
			}

			_io().out().flush();
			
		}
	}
	
	private static String argCode(String[] args, int start) {
		//Reassemble the code on the stack
		String code = "";
		for (int i = start; i < args.length; i++) {
			code += args[i] + " ";
		}
		return code;
	}
	
	public AyaThread getMainThread() {
		return _aya;
	}
	
	public static InteractiveAya createInteractiveSession() {
		// Init the static data
		StaticData.getInstance().init();
		
		ExecutionContext context = ExecutionContext.createRoot(StaticData.IO);
		
		// Init global vars
		context.getVars().initGlobals(context);

		AyaPrefs.init();

		AyaThread ayaThread = AyaThread.spawnThread(context);
		
		//Use default system io (interactive in the terminal)
		InteractiveAya iaya = new InteractiveAya(ayaThread);
		
		return iaya;
	}
	
	public static void main(String[] args) {
		InteractiveAya iaya = createInteractiveSession();
		
		// argument[0] is always the working directory, check for args 1+
		if (args.length > 1) {
			
			// Interactive Terminal
			if (args[1].equals("-i")) {
				iaya.initCode(argCode(args, 2));
			} 
			
			// Run a script 
			else if (args[1].contains(".aya")) {
				interactive = false;
				String filename = args[1];
					
				String code = argCode(args,	2);
				
				try {
					String script = (code + "\n" + FileUtils.readAllText(filename)).trim();
					
					// Is there a shebang? If yes, drop the first line
					if (script.charAt(0) == '#' && script.charAt(1) == '!') {
						int line_end = script.indexOf('\n');
						script = script.substring(line_end);
					}

					//System.out.println("Queueing script:");
					//System.out.println(script);

					iaya.queueScript(script);
				} catch (IOException e) {
					System.err.println("Cannot find file: " + filename);
					System.err.println(e.getMessage());
				} 

			}
			
			else {
				System.out.println("use `aya -i` to enter the repl or `aya script.aya [arg1 arg2 ...]` to run a file");
			}
		}
		
		iaya.loop();
	}
	
	private void queueScript(String script) {
		_scripts.add(script);
	}
	
	public final static void clearConsole() {
	    try {
	        final String os = System.getProperty("os.name");

	        if (os.contains("Windows")) {
	        	Runtime.getRuntime().exec("cls"); 
	        }
	        else {
	            Runtime.getRuntime().exec("clear");
	        }
	    }
	    catch (final Exception e) {
	    	throw new RuntimeException(e);
	    }
	}
	public static void setInteractive(boolean b) {
		interactive = b;
	}
}
