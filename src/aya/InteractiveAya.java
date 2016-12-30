package aya;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import aya.exceptions.SyntaxError;
import aya.infix.Compiler;
import aya.obj.block.Block;
import aya.parser.Parser;
import aya.util.FileUtils;
import test.AyaTestCases;

public class InteractiveAya {
		
	private static final int SUCCESS = Aya.RETURN_SUCCESS;
	private static final int EXIT = Aya.RETURN_EXIT;
	private static boolean USE_INFIX = false;

	
	public static final String HELP_TEXT = "Help:\n"
			+ "  \\q\t\t\tquit interactive Aya\n"
			+ "  \\h\t\t\tview this page\n"
			+ "  \\? <help text>\t\tsearch for help text in Aya\n"
			+ "  \\cls\t\t\tclear the console window\n"
			+ "  \\version\t\t\tdisplay Ara version name";
	
	public static int processInput(Aya aya, String input) {
		//Empty Input
		if(input.equals("")) {
			return SUCCESS;
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
				aya.println(HELP_TEXT);
			}
			
			//Search
			else if(settings[0].equals("\\?") && settings.length > 1) {
				String searchText = "";
				for (int i = 1; i < settings.length; i++) {
					searchText += settings[i] + " ";
				}
				searchText = searchText.substring(0, searchText.length()-1);
				
				aya.helpData.clearFilter();
				aya.helpData.applyNewFilter(searchText);
				if(aya.helpData.getFilteredItems().size() == 0) {
					aya.println("No help data matching \"" + searchText + "\"");
				} else {
					for(String s : Aya.instance.helpData.getFilteredItems()) {
						aya.println(s.replace("\n", "\n   "));
					}
				}
			}
			
			//Clear the console
			else if (settings[0].equals("\\cls")) {
				return Aya.CLEAR_CONSOLE;
			}
			
			//Version
			else if(settings[0].equals("\\version")) {
				aya.getOut().print(Aya.VERSION_NAME);
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
					aya.getOut().printEx("Nothing to time");
				} else {					
					//Compile the code
					Block b;
					try {
						b = Parser.compile(code, aya);
					} catch (SyntaxError e) {
						aya.getOut().printEx(e.getMessage());
						return Aya.RETURN_SUCCESS;
					}
					
					//Run the code
					long startTime = System.nanoTime();
					aya.run(b);
					long endTime = System.nanoTime();
	
					long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
					aya.getOut().printQuiet("\nExecution took " +((double)duration/1000000000)+ " seconds");
				}
			}
			
			//Invert
			else if(settings[0].equals("\\i")) {
				//Reassemble the code
				String code = "";
				for (int i = 1; i < settings.length; i++) {
					code += settings[i] + " ";
				}
				code = code.trim();
				
				if(code.equals("")) {
					aya.getOut().printEx("Nothing to eval");
				} else {					
					//Compile the code
					Block b;
					if(!USE_INFIX){
						try {
							b = Compiler.compile(code, false);
						} catch (SyntaxError se) {
							aya.getOut().printEx("Infix: Syntax error");
							return Aya.RETURN_SUCCESS;
						}
					} else {
						try {
							b = Parser.compile(code, aya);
						} catch (SyntaxError e) {
							aya.getOut().printEx(e.getMessage());
							return Aya.RETURN_SUCCESS;
						}
					}
					aya.run(b);
				}
			}
			
			//Infix (Always on)
			else if(settings[0].equals("\\i-on")) {
				USE_INFIX = true;
				Aya.PRINT_LARGE_ERRORS = false;
			}
			
			//Infix (Always off)
			else if(settings[0].equals("\\i-off")) {
				USE_INFIX = false;
				Aya.PRINT_LARGE_ERRORS = true;
			}
			
			//Run Tests
			else if(settings[0].equals("\\test")) {
				aya.getOut().println(AyaTestCases.runTests());
			}
						
			else {
				aya.getOut().printWarn("Invalid command. Please make sure there is a space bewteen command and its arguments.");
			}
			
		}
		
		//Normal Input
		else {
			if(USE_INFIX) {
				try {
					aya.run(Compiler.compile(input, false));
				} catch (SyntaxError se) {
					aya.getOut().printEx("Infix: Syntax error");
					return Aya.RETURN_SUCCESS;
				}
			} else {
				aya.run(input);
			}
		}
		
		return Aya.RETURN_SUCCESS;
	}
	
	//Returns true if load was successful
	public static boolean loadBase(Aya aya) {
		//Load the standard library
		try {
			aya.run("\"load.aya\"G~");
		} catch (Exception e) {
			aya.getOut().clear();
			return false;
		}
		if(aya.getOut().hasError()) {
			return false;
		} else {
		
			return true;
		}
	}

	
	
	public static void main(String[] args) {
		Aya aya = Aya.getInstance();
		
		
		
		if (args.length > 0) {
			
			//Run the arguments as code
			if(args[0].equals("-c")) {
				
				loadBase(aya);
				
				StringBuilder input = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					input.append(args[i] + ' ');
				}
				
				aya.run(input.toString());
			
				System.out.println(aya.getOut().dumpAsString());		
			} 
			
			
			//Open and run a file
			else if (args[0].equals("-f")) {
				String filename = args[1];

				try {
					Aya.instance.run(FileUtils.readAllText(filename));
				} catch (FileNotFoundException e) {
					Aya.instance.getOut().printEx("Cannot open file: " + filename);
				} catch (IOException e) {
					Aya.instance.getOut().printEx("File not found: " + filename);

				}
				
			}
			
			//Run interactive aya
			else if(args[0].equals("-i")) {
				
				//Attempt to load base
				loadBase(aya);
				
				//aya.run("\"..\\\\test.aya\"G~");

				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(System.in);
				String input = "";
				
//				if (System.console() == null) {
//					input = scanner.nextLine();
//					processInput(aya, input);
//					System.out.println(aya.getOut().dumpAsString());
//					return;
//				}

				
				System.out.println("       __ _ _   _  __ _    | A tiny, stack based programming language ");
				System.out.println("      / _` | | | |/ _` |   |                                          ");
				System.out.println("     | (_| | |_| | (_| |   | Version: " + Aya.VERSION_NAME);
				System.out.println("      \\__,_|\\__, |\\__,_|   | Nicholas Paul                         ");
				System.out.println("            |___/                                                     ");
				System.out.println("");
				
				
				System.out.println(aya.getOut().dumpAsString());
				
			
				while (true) {
					System.out.print(AyaPrefs.getPrompt());
					input = scanner.nextLine();
					
					
					int status = processInput(aya, input);
					
					
					switch (status) {
					case SUCCESS:
						System.out.println(aya.getOut().dumpAsString());
						break;
					case EXIT:
						return;
					case Aya.CLEAR_CONSOLE:
						System.out.println("Cannot clear console.");
						break;
					default:
						throw new RuntimeException("Implement status in InteractiveAya.main()");
					}
				}
			}
			
			//Invalid input
			else {
				Aya.instance.getOut().printEx("use -c for inline code, -f to run a file, or -i to enter the repl");

			}

		} else {
			System.out.println("use -c for inline code, -f to run a file, or -i to enter the repl");
		}
		
		
	}
	
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
