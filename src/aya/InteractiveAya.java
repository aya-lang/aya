package aya;

import java.io.PrintStream;
import java.util.Scanner;

import aya.exceptions.SyntaxError;
import aya.obj.block.Block;
import aya.parser.Parser;

public class InteractiveAya extends Thread {
	
	public static final int EXIT = 0;
	public static final int SUCCESS = 1;
	public static final int NONE = 2;
	public static final int TIME = 3;
	public static final int CLS = 4;
	
	Aya _aya;
	
	public InteractiveAya(Aya aya) {
		_aya = aya;
	}

	private static final String BANNER = ""
			+ "       __ _ _   _  __ _    | A tiny, stack based programming language \n"
			+ "      / _` | | | |/ _` |   |                                          \n"
			+ "     | (_| | |_| | (_| |   | Version: " + Aya.VERSION_NAME + "\n"
			+ "      \\__,_|\\__, |\\__,_|   | Nicholas Paul                         \n"
			+ "            |___/                                                     \n"
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

	
	String[] _args = new String[0];
	private boolean _showPromptText = true;
	private boolean _showBanner = true;
	
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
		Aya aya = Aya.getInstance();
		InteractiveAya iaya = new InteractiveAya(aya);
		iaya.setArgs(args);
		iaya.run();
		
		try {
			iaya.join();
		} catch (InterruptedException e) {
			e.printStackTrace(aya.getErr());
		}
	}
		
		
		
//		if (args.length > 0) {
//			
////			//Run the arguments as code
////			if(args[0].equals("-c")) {
////				
////				loadBase(aya);
////				
////				StringBuilder input = new StringBuilder();
////				for (int i = 1; i < args.length; i++) {
////					input.append(args[i] + ' ');
////				}
////				
////				aya.run(input.toString());
////			
////				System.out.println(aya.getOut().dumpAsString());		
////			} 
//			
//			
////			//Open and run a file
////			else if (args[0].equals("-f")) {
////				String filename = args[1];
////
////				try {
////					Aya.instance.run(FileUtils.readAllText(filename));
////				} catch (FileNotFoundException e) {
////					Aya.instance.getOut().printEx("Cannot open file: " + filename);
////				} catch (IOException e) {
////					Aya.instance.getOut().printEx("File not found: " + filename);
////
////				}
////				
//			}
//			
//			//Run interactive aya
//			else if(args[0].equals("-i")) {
//				
//				//Attempt to load base
//				//loadBase(aya);
//				
//				//aya.run("\"..\\\\test.aya\"G~");
//
//				@SuppressWarnings("resource")
//				Scanner scanner = new Scanner(System.in);
//				String input = "";
//				
////				if (System.console() == null) {
////					input = scanner.nextLine();
////					processInput(aya, input);
////					System.out.println(aya.getOut().dumpAsString());
////					return;
////				}
//
//				
//				System.out.println("       __ _ _   _  __ _    | A tiny, stack based programming language ");
//				System.out.println("      / _` | | | |/ _` |   |                                          ");
//				System.out.println("     | (_| | |_| | (_| |   | Version: " + Aya.VERSION_NAME);
//				System.out.println("      \\__,_|\\__, |\\__,_|   | Nicholas Paul                         ");
//				System.out.println("            |___/                                                     ");
//				System.out.println("");
//				
//				
//				//System.out.println(aya.getOut().dumpAsString());
//				
//			
//				while (true) {
//					System.out.print(AyaPrefs.getPrompt());
//					input = scanner.nextLine();
//					
//					
//					int status = processInput(aya, input);
//					
//					
////					switch (status) {
////					case SUCCESS:
////						//System.out.println(aya.getOut().dumpAsString());
////						break;
////					case EXIT:
////						return;
//////					case Aya.CLEAR_CONSOLE:
//////						System.out.println("Cannot clear console.");
////						break;
////					default:
////						throw new RuntimeException("Implement status in InteractiveAya.main()");
//					}
//				}
//			}
			
			//Invalid input
//			else {
//				//Aya.instance.getOut().printEx("use -c for inline code, -f to run a file, or -i to enter the repl");
//
//			}

	//	} 
	//else {
	//		System.out.println("use -c for inline code, -f to run a file, or -i to enter the repl");
	//	}
		

	
	public final static void clearConsole() {
	    try {
	        final String os = System.getProperty("os.name");

	        if (os.contains("Windows")) {
	        	//String[] cls = new String[] {"cmd.exe", "/c", "cls"};
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
