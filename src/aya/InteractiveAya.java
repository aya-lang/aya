package aya;

import java.io.IOException;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import aya.util.FileUtils;
import aya.variable.Variable;

public class InteractiveAya extends Thread {
	
	public static final int EXIT = 0;
	public static final int SUCCESS = 1;
	public static final int NONE = 2;
	public static final int TIME = 3;
	public static final int CLS = 4;
	public static final int SKIP_WAIT = 5; // Do not wait for aya as no input was queued
	public static final int NORMAL_INPUT = 6; // Normal 8input was sent to aya, wait for it to complete
	public static final int EMPTY_INPUT = 7; // Empty string was sent as input
	private static boolean interactive = true;

	private boolean _echo = false;
	private boolean _showPromptText = true;
	private boolean _showBanner = true;
	private String _initcode = null;
	private String _usercmd = null;
	
	public void setShowPrompt(boolean b) {_showPromptText = b;};
	public void setEcho(boolean b) {_echo = b;};
	
	private Aya _aya;
	
	public InteractiveAya(Aya aya) {
		_aya = aya;
	}

	private static final String BANNER = ""
			+ "       __ _ _   _  __ _\n"
			+ "      / _` | | | |/ _` |   | A tiny stack based programming language\n"
			+ "     | (_| | |_| | (_| |   | Version: " + Aya.VERSION_NAME + "\n"
			+ "      \\__,_|\\__, |\\__,_|   | Nicholas Paul\n"
			+ "            |___/\n"
			+ "\n";
	
	public static final String HELP_TEXT = "Help:\n"
			+ "  \\QUIT or \\Q\n\tquit interactive Aya\n"
			+ "  \\HELP or \\H\n\tview this page\n"
			+ "  \\? <help text>\n\tsearch for help text in Aya\n"
			+ "  \\VERSION or \\V\n\tdisplay Ara version name\n"
			+ "  \\USERCMD <variable> [.] or \\: <variable> [.]\n"
			+ "\tInput will be parsed as \"\"\"input\"\"\" aya.interpreter.<variable>\n"
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
				String code = "\"\"\"" + input + "\"\"\" aya.interpreter." + _usercmd;
				_aya.queueInput(code);
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
				_aya.println(HELP_TEXT);
				return SKIP_WAIT;
			}
			
			//Search
			else if(command.equals("?") && settings.length > 1) {
				String searchText = "";
				for (int i = 1; i < settings.length; i++) {
					searchText += settings[i] + " ";
				}
				searchText = searchText.substring(0, searchText.length()-1);
				
				_aya.getHelpData().clearFilter();
				_aya.getHelpData().applyNewFilter(searchText);
				if(_aya.getHelpData().getFilteredItems().size() == 0) {
					_aya.println("No help data matching \"" + searchText + "\"");
				} else {
					for(String s : Aya.getInstance().getHelpData().getFilteredItems()) {
						_aya.println(s.replace("\n", "\n   "));
					}
				}
				
				return SKIP_WAIT;
			}
			
			//Version
			else if(command.equals("V") || command.equals("VERSION")) {
				_aya.println(Aya.VERSION_NAME);
				return SKIP_WAIT;
			}
			
			//Time
			else if(command.equals("TIME")) {
				String code = splitAtFirst(' ', input).trim();
				if(code.equals("")) {
					_aya.getErr().println("Nothing to time");
				} else {					
					_aya.queueInput(code);
					return TIME;
				}
			}
			
			//User Command
			else if (command.equals(":") || command.equals("USERCMD")) {
				if (settings.length > 1 && Variable.isValidStr(settings[1])) {
					_usercmd = settings[1];
					if (!(settings.length > 2 && settings[2].equals("."))) {
						_aya.getOut().println("Input now being parsed as:\n\t\"\"\"input\"\"\" aya.interpreter." + _usercmd
								+ "\nType \"\\" + command + " -\" to return to normal"
								+ "\nType \"\\" + command + " " + _usercmd + " .\" to hide this message in the future");
					}
				} else {
					_aya.getErr().println("usercmd: \"" + settings[1] + "\" is not a valid user command");
				}
				return SKIP_WAIT;
			}
			
			else if (Variable.isValidStr(command)) {
				String code = splitAtFirst(' ', input).trim();
				if(code.equals("")) {
					_aya.getErr().println("No input provided");
				} else {			
					// construct [ """ (code) """ varname ]
					code = "\"\"\"" + code + "\"\"\" aya.interpreter." + command;
					_aya.queueInput(code);
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
			_aya.queueInput(input);
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
	
	public void showBanner(boolean b) {
		_showBanner = b;
	}
	
	@Override
	public void run() {
		boolean running = true;
		
 		_aya.start();
 		
		if (_initcode != null) {
			_aya.queueInput(_initcode);
		}
		
		_aya.loadAyarc();
		
		// Get Aya I/O
		PrintStream out = _aya.getOut();
		PrintStream err = _aya.getErr();		
		Scanner scanner = _aya.getScanner();
		
		
		if (_showBanner && interactive) _aya.print(BANNER);

		String input = "";
		int status;
				
		while (running) {
			
			if (_showPromptText && interactive) {
				out.print(AyaPrefs.getPrompt());
			}
			
			try {
				input = scanner.nextLine();
			} catch (NoSuchElementException e) {
				// EOF Encountered
				_aya.quit();
				running = false;
			}
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
						_aya.wait();
					} catch (InterruptedException e) {
						out.println("Aya interrupted");
						e.printStackTrace(err);
						running = false;
						status = EXIT;
					}
				}
			}
			
			
			switch (status) {
			case EXIT:
				scanner.close();
				running = false;
				_aya.quit();
				break;
			case TIME:
				out.println("Execution time: " + ((double)_aya.getLastInputRunTime())/1000 + "s");
			}

			_aya.getOut().flush();

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
	
	public static void main(String[] args) {
		
		Aya aya = Aya.getInstance();
		
		//Use default system io (interactive in the terminal)
		InteractiveAya iaya = new InteractiveAya(aya);
		
		if (args.length > 0) {
			
			// Interactive Terminal
			if (args[0].equals("-i")) {
				iaya.initCode(argCode(args, 1));
			} 
			
			// Run a script 
			else if (args[0].contains(".aya")) {
				String filename = args[0];
					
				String code = argCode(args,	1);
				
				try {
					String script = code + "\n" + FileUtils.readAllText(filename);
					
					aya.loadAyarc();
					aya.queueInput(script);
					aya.queueInput(Aya.QUIT);
					
				} catch (IOException e) {
					System.err.println("Cannot find file: " + filename);
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
