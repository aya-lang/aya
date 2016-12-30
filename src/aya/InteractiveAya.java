package aya;

import java.util.Scanner;

import aya.exceptions.SyntaxError;
import aya.obj.block.Block;
import aya.parser.Parser;

public class InteractiveAya {
	
	public static final int SUCCESS = 1;
	public static final int EXIT = 0;
	

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
				
				aya.getHelpData().clearFilter();
				aya.getHelpData().applyNewFilter(searchText);
				if(aya.getHelpData().getFilteredItems().size() == 0) {
					aya.println("No help data matching \"" + searchText + "\"");
				} else {
					for(String s : Aya.getInstance().getHelpData().getFilteredItems()) {
						aya.println(s.replace("\n", "\n   "));
					}
				}
			}
			
			//Clear the console
			else if (settings[0].equals("\\cls")) {
//				return Aya.CLEAR_CONSOLE;
			}
			
			//Version
			else if(settings[0].equals("\\version")) {
				//aya.getOut().print(Aya.VERSION_NAME);
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
					//aya.getOut().printEx("Nothing to time");
				} else {					
					//Compile the code
					Block b;
					try {
						b = Parser.compile(code, aya);
					} catch (SyntaxError e) {
						//aya.getOut().printEx(e.getMessage());
//						return Aya.RETURN_SUCCESS;
					}
					
					//Run the code
					long startTime = System.nanoTime();
//					aya.run(b);
					long endTime = System.nanoTime();
	
					long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
					//aya.getOut().printQuiet("\nExecution took " +((double)duration/1000000000)+ " seconds");
				}
			}
			
//			//Run Tests
//			else if(settings[0].equals("\\test")) {
//				aya.getOut().println(AyaTestCases.runTests());
//			}
						
			else {
				//aya.getOut().printWarn("Invalid command. Please make sure there is a space between command and its arguments.");
			}
			
		}
		
		//Normal Input
		else {
			aya.queueInput(input);
		}
		
		return SUCCESS;
	}
	
	//Returns true if load was successful
	public static boolean loadBase(Aya aya) {
		//Load the standard library
		try {
			aya.queueInput("\"load.aya\"G~");
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	
	
	public static void main(String[] args) {
		Aya aya = Aya.getInstance();
		aya.start();
		
		if (!loadBase(aya)) {
			System.out.println("There was an error loading base/");
		}
		
		System.out.print(BANNER);
		
		Scanner scanner = new Scanner(System.in);
		String input = "";
		
		while (true) {
			
			System.out.print(AyaPrefs.getPrompt());
			input = scanner.nextLine();

			int status = processInput(aya, input);
			
			if (status == EXIT) {
				scanner.close();
				aya.queueInput(Aya.QUIT);
				try {
					aya.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			
			//Wait for aya to finish
			synchronized (aya) {
				try {
					aya.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
