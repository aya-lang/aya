package aya;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import aya.util.FileUtils;

public class InteractiveAya extends Thread {
	
	public static final int EXIT = 0;
	public static final int SUCCESS = 1;
	public static final int NONE = 2;
	public static final int TIME = 3;
	public static final int CLS = 4;
	
	private boolean _echo = false;
	String[] _args = new String[0];
	private boolean _showPromptText = true;
	private boolean _showBanner = true;
	
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
			+ "  \\q\t\t\tquit interactive Aya\n"
			+ "  \\h\t\t\tview this page\n"
			+ "  \\? <help text>\t\tsearch for help text in Aya\n"
			+ "  \\cls\t\t\tclear the console window\n"
			+ "  \\version\t\t\tdisplay Ara version name";
	
	public int processInput(String input) {
		//Empty Input
		if(input.equals("")) {
			return NONE;
		}
		
		//Settings
		else if (input.charAt(0) == '\\') {
			String[] settings = input.split(" ");
			
			//Exit
			if(settings[0].equals("\\q")) {
				return EXIT; //Exit the program
			}
			
			
			//Help
			else if(settings[0].equals("\\h") || settings[0].equals("\\help")) {
				_aya.println(HELP_TEXT);
			}
			
			//Search
			else if(settings[0].equals("\\?") && settings.length > 1) {
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
			}
			
			//Version
			else if(settings[0].equals("\\version")) {
				_aya.println(Aya.VERSION_NAME);
			}
			
			//Time
			else if(settings[0].equals("\\time")) {
				//Reassemble the code
				String code = "";
				for (int i = 1; i < settings.length; i++) {
					code += settings[i] + " ";
				}
				code = code.trim();
				if(code.equals("")) {
					_aya.getErr().println("Nothing to time");
				} else {					
					_aya.queueInput(code);
					return TIME;
				}
			}
						
			else {
				_aya.getErr().println("Invalid command. Please make sure there is a space between command and its arguments.");
			}
			
		}
		
		//Normal Input
		else {
			_aya.queueInput(input);
		}
		
		return SUCCESS;
	}

	
	
	public void setArgs(String[] args) {
		_args = args;
	}
	
	public void setPromptText(boolean b) {
		_showPromptText = false;
	}
	
	public void showBanner(boolean b) {
		_showBanner = b;
	}
	
	@Override
	public void run() {
 		_aya.start();
		_aya.loadAyarc();

		
		if (_showBanner) _aya.print(BANNER);
		
		PrintStream out = _aya.getOut();
		PrintStream err = _aya.getErr();
		
		Scanner scanner = new Scanner(_aya.getIn());
		String input = "";
		int status;
		

		
		while (true) {
			
			if (_showPromptText) {
				out.print(AyaPrefs.getPrompt());
			}
			input = scanner.nextLine();
			
			if (input.equals("")) {
				continue;
			}
			
			if (_echo) {
				out.println(AyaPrefs.getPrompt() + input);
			}
			
			//Wait for aya to finish
			synchronized (_aya) {
				status = processInput(input);
								
				try {
					_aya.wait();
				} catch (InterruptedException e) {
					e.printStackTrace(err);
				}
			}
			
			
			switch (status) {
			case EXIT:
				scanner.close();
				_aya.queueInput(Aya.QUIT);
				try {
					_aya.join();
				} catch (InterruptedException e) {
					e.printStackTrace(err);
				}
				return;
			case TIME:
				out.println("Execution time: " + ((double)_aya.getLastInputRunTime())/1000 + "s");
			case NONE:
				continue;
			}
		}
		
	}
	
	public static void main(String[] args) {
		
		if (args.length > 0) {
			
			// Interactive Terminal
			if (args[0].equals("-i")) {
		
				Aya aya = Aya.getInstance();
				
				//Use default system io (interactive in the terminal)
				InteractiveAya iaya = new InteractiveAya(aya);
				iaya.setArgs(args);
				iaya.run();
				
				try {
					iaya.join();
				} catch (InterruptedException e) {
					e.printStackTrace(aya.getErr());
				}
				
			} 
			
			// Run a script 
			else if (args[0].contains(".aya")) {
				String filename = args[0];
					
				//Reassemble the code on the stack
				String code = "";
				for (int i = 1; i < args.length; i++) {
					code += args[i] + " ";
				}
				
				try {
					String script = code + "\n" + FileUtils.readAllText(filename);
					
					Aya aya = Aya.getInstance();
					aya.setEchoInput(false);
					aya.loadAyarc();
					aya.setEchoInput(true);
					aya.queueInput(script);
					aya.queueInput(Aya.QUIT);
					
					aya.run();
					
					try {
						aya.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				} catch (IOException e) {
					System.err.println("Cannot find file: " + filename);
				} 

			}
			
			else {
				System.out.println("use `aya -i` to enter the repl or `aya script.aya [arg1 arg2 ...]` to run a file");
			}
		} else {
			System.out.println("use `aya -i` to enter the repl or `aya script.aya [arg1 arg2 ...]` to run a file");
		}
		
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
}
