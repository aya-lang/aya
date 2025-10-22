package aya;

import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import aya.eval.ExecutionContext;
import aya.exceptions.parser.ParserException;
import aya.io.stdin.ScannerInputWrapper;
import aya.obj.Obj;
import aya.obj.block.StaticBlock;
import aya.parser.Parser;
import aya.parser.SourceString;

public class InteractiveAya {
	
	private static int lastResultCode = 0;

	private boolean _echo = false;
	private boolean _showPromptText = true;
	private boolean _interactive = true;
	
	public void setShowPrompt(boolean b) {_showPromptText = b;};
	public void setEcho(boolean b) {_echo = b;};
	
	private long _request_id_counter;
	
	private boolean running;
	
	private AyaThread _aya;
	
	private AyaStdIO _io() {
		return StaticData.IO;
	}

	protected InteractiveAya(AyaThread ayaThread) {
		_request_id_counter = 0;
		_aya = ayaThread;
	}
	
	private long makeRequestID() {
		_request_id_counter++;
		return _request_id_counter;
	}
	
	public ExecutionRequest processInput(String input) {
		//Empty Input
		if(input.equals("")) {
			return null;
		}
		
		// Settings
		else if (input.charAt(0) == '\\') {

			String[] settings = input.split(" ");
			String command = settings[0].substring(1, settings[0].length());
			
			//Exit
			if(command.equals("Q")) {
				// Notify aya to exit
				_aya.interrupt();
				running = false;
			}
			
			//Help
			else if(command.equals("H") || command.equals("HELP")) {
				_io().out().println(CLIOptions.HELP_TEXT);
			}
			
			//Search
			else if(command.equals("?") && settings.length > 1) {
				String searchText = "";
				for (int i = 1; i < settings.length; i++) {
					searchText += settings[i] + " ";
				}
				searchText = searchText.substring(0, searchText.length()-1);
				
				StaticData.getInstance().getHelpData().clearFilter();
				StaticData.getInstance().getHelpData().applyFilter(searchText);
				if(StaticData.getInstance().getHelpData().getFilteredItems().isEmpty()) {
					_io().out().println("No help data matching \"" + searchText + "\"");
				} else {
					for(String s : StaticData.getInstance().getHelpData().getFilteredItems()) {
						_io().out().println(s.replace("\n", "\n   "));
					}
				}
			}
			
			//Version
			else if(command.equals("V") || command.equals("VERSION")) {
				_io().out().println(StaticData.VERSION_NAME);
			}
			
			else {
				_io().err().println("Invalid command. Please make sure there is a space between command and its arguments.");
			}

			return null;
		}
		
		//Normal Input
		else {
			try {
				StaticBlock blk = Parser.compile(new SourceString(input, "<input>"));
				return new ExecutionRequest(makeRequestID(), blk);
			} catch (ParserException e) {
				_io().err().println("Syntax Error: " + e.getSimpleMessage());
				return null;
			}
		}
	}
	
	/** Compile and run a system generated input (i.e. in main()). Do not use for normal aya input */
	public void compileAndQueueSystemInput(String source, String input) {
		try {
			StaticBlock blk = Parser.compile(new SourceString(input, source));
			ExecutionRequest req = new ExecutionRequest(makeRequestID(), blk);
			_aya.queueInput(req);
		} catch (ParserException e) {
			e.printStackTrace();
			StaticData.IO.out().print(e);
		}
	}
	
	public void setPromptText(boolean b) {
		_showPromptText = false;
	}
	
	public static void printResult(AyaStdIO io, ExecutionResult result, boolean raw) {
		if (result != null) {
			switch (result.getType()) {
			case ExecutionResult.TYPE_SUCCESS:
				{
					lastResultCode = 0;
					ExecutionResultSuccess res = (ExecutionResultSuccess)result;
					ArrayList<Obj> data = res.getData();
					if (data.size() > 0) {
						for (int i = 0; i < data.size(); i++) {
							if (raw) {
								io.out().print(data.get(i).str());
							} else {
								io.out().print(data.get(i));
							}
							if (i < data.size() - 1) io.out().print(' ');
						}
						io.out().println();
					}
				}
				break;
			case ExecutionResult.TYPE_EXCEPTION:
				{
					lastResultCode = 1;
					ExecutionResultException res = (ExecutionResultException)result;
					res.ex().print(io.err());
					if (!res.callstack().equals("")) {
						io.err().print(res.callstack());
					}
				}
				break;
			}
		}
	}
	
	public void printPrompt() {
		if (_showPromptText) {
			_io().out().print(AyaPrefs.getPrompt());
		}
	}

	/**
	 * @return the resultCode of the last executed request
	 */
	public int loop() {
		running = true;
		
		// Get Aya I/O
		PrintStream out = _io().out();
		PrintStream err = _io().err();		
		Scanner scanner = ((ScannerInputWrapper)(_io().inputWrapper())).getScanner();
		boolean rawOutput = !_interactive;
		
 		_aya.start();
		
		// Flush output
		boolean unfinished_requests = _request_id_counter > 0;
		while (unfinished_requests) {
			try {
				ExecutionResult res = _aya.waitForResponse();
				printResult(_io(), res, rawOutput);
				
				// done?
				if (res.id() >= _request_id_counter) unfinished_requests = false;
			} catch (InterruptedException e) {
				out.println("Aya interrupted");
				e.printStackTrace(err);
				running = false;
			}
		}

		
		// interactive is set on startup. If true, this is an infinite REPL loop
		if (_interactive) {
			
			// Read input on a different thread
			new Thread(() -> {
				// Show the prompt for the first time 
				// Future prints are done in the execution loop
				printPrompt();
				while (true) {
					String input = "";
					try {
						input = scanner.nextLine();
					} catch (NoSuchElementException e) {  // Ctrl+D
						_aya.interrupt();
						_io().out().flush();
						System.exit(0);
					}

					if (input.equals("")) {
						// If no input, just re print the prompt and go back to reading the next line
						printPrompt();
					} else {
						if (_echo && _interactive) {
							out.println(AyaPrefs.getPrompt() + input);
						}
						
						ExecutionRequest request = processInput(input);
						if (request != null) {
							_aya.queueInput(request);
							// Request was sent, prompt will be printed when it is complete
						} else {
							// No request but enter was pressed so we need to re print the prompt
							printPrompt();
						}
					}
					
				}
			}).start();
				
			// Evaluation Loop
			while (running) {
				try {
					ExecutionResult result = _aya.waitForResponseBlocked();
					printResult(_io(), result, rawOutput);
					
					if (_showPromptText) {
						out.print(AyaPrefs.getPrompt());
					}
					
				} catch (InterruptedException e) {
					out.println("Aya interrupted");
					e.printStackTrace(err);
					running = false;
				}
				
				if (!running) _aya.interrupt();
				
				if (_aya.isInterrupted()) {
					scanner.close();
					running = false;
					break;
				}
	
				_io().out().flush();
			}
		}
		return lastResultCode;
	}
	
	public AyaThread getMainThread() {
		return _aya;
	}
		
	public static InteractiveAya createInteractiveSession(String workingDirectory) {
		// Init the static data
		StaticData.getInstance().init();
		//AyaPrefs.setArgs(args);
		
		AyaPrefs.initDefaultWorkingDir();
		if (workingDirectory != null) {
			AyaPrefs.setWorkingDir(workingDirectory);
		}
		
		ExecutionContext context = ExecutionContext.createRoot(StaticData.IO);
		
		// Init global vars
		context.getVars().initGlobals();
		
		AyaThread ayaThread = AyaThread.spawnRootThread(context);
		
		//Use default system io (interactive in the terminal)
		InteractiveAya iaya = new InteractiveAya(ayaThread);
		
		// Load ayarc
		String pathString = Paths.get(AyaPrefs.getAyaDir(), StaticData.ayarcPath).toString().replace("\\", "\\\\");
		iaya.compileAndQueueSystemInput("<ayarc loader>", "\"\"\"" + pathString + "\"\"\" :F");

		
		return iaya;
	}
	
	/**
	 * Helper function for main that creates requests and adds them to the interactive session
	 * @param source
	 * @param expression
	 */
//	private void _enqueueInput(String source, String expression) {
//		StaticBlock blk = Parser.compile(new SourceString(expression, "<input>"));
//		ExecutionRequest req = new ExecutionRequest(iaya.makeRequestID(), blk);
//	}
	
//	public static void main(String[] args) {
//		StaticData.IO = new AyaStdIO(System.out, System.err, System.in, new ScannerInputWrapper(System.in));
//		StaticData.HTTP_DOWNLOADER = new HTTPDownloader();
//		StaticData.FILESYSTEM = new FilesystemIO();
//
//		
//		final int RESCODE_OK  = 0;
//		final int RESCODE_ERR = 1;
//		
//		int resultCode = RESCODE_OK;
//		InteractiveAya iaya = createInteractiveSession(args);
//
//		
//		// argument[0] is always the working directory, check for args 1+
//		
//			
//		resultCode = iaya.loop();
//
//		System.exit( resultCode );
//	}
	
	public void setInteractive(boolean b) {
		_interactive = b;
	}
}
