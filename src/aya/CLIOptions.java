package aya;

import java.io.File;

import aya.util.FileUtils;

public class CLIOptions {
		public static final String HELP_TEXT = "aya " + StaticData.VERSION_NAME + "\n"
				+ "  aya\n"
				+ "    Running aya with no args will open the GUI IDE at the default working directory\n"
				+ "    Note: Default setting is GUI so that double clicking the jar from a file explorer opens the IDE\n"
				+ "    Use `aya <workingDir> -x` to open the GUI at a specified directory\n"
				+ "  aya <workingDir>\n"
				+ "    Open a command line REPL\n"
				+ "  aya <workingDir> <filename>\n"
				+ "    Run the file and exit\n"
				+ "  aya <workingDir> -<flags> <option1>\n"
				+ "    g: Import std/golf and bring all variables into scope\n"
				+ "    e: Run the expression given by <option1>\n"
				+ "    p: Run the package name given by <opion1>\n"
				+ "    i: Enter the REPL after finishing CLI tasks\n"
				+ "    r: Disable argument type checking\n"
				+ "    x: Launch the GUI\n"
				+ "    If <option1> is an aya source file, run it\n"
				+ "  aya --help\n"
				+ "    Print this message.\n"
				+ "  aya --version\n"
				+ "    Print version information\n"
				+ "";
	
		// What to do after processing CLI args
		public static final int MODE_EXIT = 0; // Exit after processing CLI args
		public static final int MODE_REPL = 1; // Enter the command line REPL
		public static final int MODE_GUI = 2; // Open the GUI
		
		public static final int SPECIAL_MODE_NONE = 0; // Print help text and exit
		public static final int SPECIAL_MODE_PRINT_HELP = 1; // Print help text and exit
		public static final int SPECIAL_MODE_PRINT_VERSION = 2; // Print help text and exit
		public static final int SPECIAL_MODE_CHECK= 3; // Check the file then exit
		
		// Options
		
		// Runtime working directory
		public String workingDir = null;
		// If true, import the golf standard library
		public boolean autoImportGolf = false;
		// If not null, run this expression on startup
		public String expressionToRun = null;
		// Interactive mode
		// By default, if no args are provided, we open the GUI
		public int mode = MODE_EXIT;
		// Special args that don't require the aya runtime
		public int specialMode = SPECIAL_MODE_NONE;
		// File to run (private: use getFileToRun and hasFileToRun)
		private String fileToRun = null;
		// If not null, run this package
		public String packageToRun = null;
		// If set, the type checker will be disabled
		public boolean disableTypeChecker = false;
		
		public CLIOptions() { }
		
		public static CLIOptions parse(String[] args, String pipedInput) throws RuntimeException {
			CLIOptions options = new CLIOptions();
			
			// Special case for double clicking the jar icon: Just launch the GUI
			if (args.length == 0) {
				options.mode = MODE_GUI;
				return options;
			}

			// First arg is always the working directory
			if (args.length >= 1) {
				options.workingDir = args[0];
			}
			
			// 2nd arg is flags or a special case like --help or --version
			// Only compact format for args is supported
			// The first arg must start with a "-" and may include any of the following characters
			// i: Run in CLI mode
			// e: Run an expression give by arg[2], then exit
			// g: Import golf standard library before running code
			// p: Run pkg.run on arg[2]
			if (args.length >= 2) {
				String arg = args[1];
				
				// Special case for --help and --version
				if (arg.equals("--help")) {
					options.specialMode = SPECIAL_MODE_PRINT_HELP;
				} else if (arg.equals("--version")) {
					options.specialMode = SPECIAL_MODE_PRINT_VERSION;
				} else {
					// aya <file>.aya
					if (arg.endsWith(".aya")) {
						options.fileToRun = arg;
					}
					// aya -<options>
					else if (arg.startsWith("-")) {
						
						// Auto import golf
						if (arg.contains("g")) {
							options.autoImportGolf = true;
							//iaya.compileAndQueueInput("<system>", "import golf");
						}
						
						// Enter REPL after running initial requests
						if (arg.contains("i")) {
							options.mode = MODE_REPL;							
						}
						
						// Enter REPL after running initial requests
						if (arg.contains("x")) {
							options.mode = MODE_GUI;							
						}
						
						// Check the file for errors, print any if they exist, then exit
						if (arg.contains("c") ) {
							options.specialMode = SPECIAL_MODE_CHECK;
						}
						
						// Disable type checker
						if (arg.contains("r")) {
							options.disableTypeChecker = true;							
						}
						
						// Options that require a 3rd argument
						if (arg.contains("p")) {
							if (args.length >= 3) {
								options.packageToRun = args[2];
							} else {
								throw new RuntimeException("Error: Please provide a package name");
							}
						} else if (arg.contains("e")) {
							if (args.length >= 3) {
								options.expressionToRun = args[2];
							} else {
								throw new RuntimeException("Error: No expression provided");
							}
						} else {
							// fallback, 3rd arg may be a filename
							if (args.length >= 3 && args[2].contains(".aya")) {
								options.fileToRun = args[2];
							}
						}
						
					} else {
						throw new RuntimeException("Error: Invalid argument " + options);
					}
				}
			} else {
				if (pipedInput == null) {
					options.mode = MODE_REPL;
				}
			}
				
			return options;
		}
		
		public File getFileToRun() {
			return FileUtils.resolveFile(this.fileToRun);
		}
		
		public boolean hasFileToRun() {
			return this.fileToRun != null;
		}
	}
	
