package aya;

import java.io.IOException;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import aya.obj.block.StaticBlock;
import aya.obj.symbol.SymbolTable;
import aya.parser.Parser;
import aya.parser.SourceString;
import aya.util.FileUtils;

public class InteractiveAya extends Thread {
	
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
	
	private Aya _aya;
	
	public InteractiveAya(Aya aya) {
		_aya = aya;
		_scripts = new ConcurrentLinkedQueue<String>();
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
				if (blk != null) _aya.queueInput(new ExecutionRequest(blk));
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
				_aya.getOut().println(HELP_TEXT);
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
					_aya.getOut().println("No help data matching \"" + searchText + "\"");
				} else {
					for(String s : StaticData.getInstance().getHelpData().getFilteredItems()) {
						_aya.getOut().println(s.replace("\n", "\n   "));
					}
				}
				
				return SKIP_WAIT;
			}
			
			//Version
			else if(command.equals("V") || command.equals("VERSION")) {
				_aya.getOut().println(StaticData.VERSION_NAME);
				return SKIP_WAIT;
			}
			
			
			//User Command
			else if (command.equals(":") || command.equals("USERCMD")) {
				if (settings.length > 1 && SymbolTable.isBasicSymbolString(settings[1])) {
					_usercmd = settings[1];
					if (!(settings.length > 2 && settings[2].equals("."))) {
						_aya.getOut().println("Input now being parsed as:\n\t\"\"\"input\"\"\" __aya__.interpreter." + _usercmd
								+ "\nType \"\\" + command + " -\" to return to normal"
								+ "\nType \"\\" + command + " " + _usercmd + " .\" to hide this message in the future");
					}
				} else {
					_aya.getErr().println("usercmd: \"" + settings[1] + "\" is not a valid user command");
				}
				return SKIP_WAIT;
			}
			
			else if (SymbolTable.isBasicSymbolString(command)) {
				String code = splitAtFirst(' ', input).trim();
				if(code.equals("")) {
					_aya.getErr().println("No input provided");
				} else {			
					// construct [ """ (code) """ varname ]
					code = "\"\"\"" + code + "\"\"\" __aya__.interpreter." + command;
					StaticBlock blk = Parser.compileSafeOrNull(new SourceString(code, "<USERCMD>"), StaticData.IO);
					if (blk != null) _aya.queueInput(new ExecutionRequest(blk));
					return NORMAL_INPUT;
				}
			}
						
			else {
				_aya.getErr().println("Invalid command. Please make sure there is a space between command and its arguments.");
				return SKIP_WAIT;
			}
			
		}
		
		//Normal Input
		else {
			StaticBlock blk = Parser.compileSafeOrNull(new SourceString(input, "<input>"), StaticData.IO);
			if (blk != null) _aya.queueInput(new ExecutionRequest(blk));
			return NORMAL_INPUT;
		}
		
		_aya.getErr().println("invalid input");
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
	
	@Override
	public void run() {
		boolean running = true;
		StringBuilder log = new StringBuilder();
		
 		_aya.start();
 		
		if (_initcode != null) {
			StaticBlock blk = Parser.compileSafeOrNull(new SourceString(_initcode, "<args>"), StaticData.IO);
			if (blk != null) _aya.queueInput(new ExecutionRequest(blk));
		}
		
		_aya.loadAyarc();
		
		// Get Aya I/O
		PrintStream out = _aya.getOut();
		PrintStream err = _aya.getErr();		
		Scanner scanner = _aya.getScanner();
		
		
		String input = "";
		int status;
				
		//int num_scripts_at_start = _scripts.size();
		log.append("_scripts.size(): " + _scripts.size() + "\n");

		// interactive is set on startup. If true, this is an infinite REPL loop
		while (running && (interactive || !_scripts.isEmpty())) {

			log.append("-- in loop --\n");
			log.append("_scripts.size(): " + _scripts.size() + "\n");
			
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
					log.append("EOF");
					// EOF Encountered
					_aya.quit();
					running = false;
				}
			}
			log.append("input: {" + input + "}");
			
			//System.out.println("Processing input: {" + input + "}");
			if (input.equals("")) {
				continue;
			}
			
			if (_echo && interactive) {
				out.println(AyaPrefs.getPrompt() + input);
			}
			
			//Wait for aya to finish
			synchronized (_aya) {
				status = processInput(input);
				
				if (status != SKIP_WAIT) {		
					try {
						log.append("waiting for aya to finish...");
						//_aya.waitForQueue();
						_aya.wait();
					} catch (InterruptedException e) {
						out.println("Aya interrupted");
						e.printStackTrace(err);
						running = false;
						status = EXIT;
					}
				}
			}

			log.append("processing complete");
			
			
			switch (status) {
			case EXIT:
				scanner.close();
				running = false;
				_aya.quit();
				break;
			}

			_aya.getOut().flush();
			
		}

		//System.out.println("Not interactive and finished running all " + num_scripts_at_start + " scripts. Exiting..");
		//System.out.println("Log: \n" + log.toString());
	}
	
	private static String argCode(String[] args, int start) {
		//Reassemble the code on the stack
		String code = "";
		for (int i = start; i < args.length; i++) {
			code += args[i] + " ";
		}
		return code;
	}
	
	public static void main(String[] args) {
		//for (int i = 0; i < args.length; i++) {
		//	System.out.println("[" + i + "]: " + "'" + args[i] + "'");
		//}
		
		Aya aya = Aya.getInstance();
		
		//Use default system io (interactive in the terminal)
		InteractiveAya iaya = new InteractiveAya(aya);
		
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
		
		iaya.run();
		

		try {
			iaya.join();
		} catch (InterruptedException e) {
			e.printStackTrace(aya.getErr());
		}
		
		System.exit(1);
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
