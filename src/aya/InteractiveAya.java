package aya;

import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import aya.eval.ExecutionContext;
import aya.exceptions.parser.ParserException;
import aya.obj.Obj;
import aya.obj.block.StaticBlock;
import aya.parser.Parser;
import aya.parser.SourceString;

public class InteractiveAya {
	
	private static boolean interactive = true;

	private boolean _echo = false;
	private boolean _showPromptText = true;
	
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

	public static final String HELP_TEXT = "Help:\n"
			+ "  \\QUIT or \\Q\n\tquit interactive Aya\n"
			+ "  \\HELP or \\H\n\tview this page\n"
			+ "  \\? <help text>\n\tsearch for help text in Aya\n"
			+ "  \\VERSION or \\V\n\tdisplay Ara version name\n"
			+ "  \\USERCMD <variable> [.] or \\: <variable> [.]\n"
			+ "\tInput will be parsed as \"\"\"input\"\"\" __aya__.interpreter.<variable>\n"
			+ "\tUse \"\\USERCMD -\" to return to normal\n"
			+ "\tUse \"\\USERCMD <variable> .\" to hide message\n";
	
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
				_io().out().println(HELP_TEXT);
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
	
	public void setPromptText(boolean b) {
		_showPromptText = false;
	}
	
	public static void printResult(AyaStdIO io, ExecutionResult result) {
		if (result != null) {
			switch (result.getType()) {
			case ExecutionResult.TYPE_SUCCESS:
				{
					ExecutionResultSuccess res = (ExecutionResultSuccess)result;
					ArrayList<Obj> data = res.getData();
					if (data.size() > 0) {
						for (int i = 0; i < data.size(); i++) {
							io.out().print(data.get(i));
							if (i < data.size() - 1) io.out().print(' ');
						}
						io.out().println();
					}
				}
				break;
			case ExecutionResult.TYPE_EXCEPTION:
				{
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

	public void loop() {
		running = true;
		
		// Get Aya I/O
		PrintStream out = _io().out();
		PrintStream err = _io().err();		
		Scanner scanner = _io().scanner();
		
 		_aya.start();
		
		// Load ayarc
		String pathString = Paths.get(AyaPrefs.getAyaDir(), StaticData.ayarcPath).toString().replace("\\", "\\\\");
		StaticBlock blk = Parser.compileSafeOrNull(new SourceString("\"" + pathString + "\":F", "<ayarc loader>"), StaticData.IO);
		if (blk != null) _aya.queueInput(new ExecutionRequest(makeRequestID(), blk));
		
		// Load startup script
		String[] args = AyaPrefs.getArgs();
		if (args.length >= 2 && args[1].contains(".aya")) {
			String startupScript = AyaPrefs.getArgs()[1].replace("\\", "\\\\");
			StaticBlock blk2 = Parser.compileSafeOrNull(new SourceString("\"" + startupScript + "\":F", "<ayarc loader>"), StaticData.IO);
			if (blk2 != null) {
				_aya.queueInput(new ExecutionRequest(makeRequestID(), blk2));
				interactive = false;
			}
		}

		// Flush output
		boolean unfinished_requests = _request_id_counter > 0;
		while (unfinished_requests) {
			try {
				ExecutionResult res = _aya.waitForResponse();
				printResult(_io(), res);
				
				// done?
				if (res.id() >= _request_id_counter) unfinished_requests = false;
			} catch (InterruptedException e) {
				out.println("Aya interrupted");
				e.printStackTrace(err);
				running = false;
			}
		}
		
		// interactive is set on startup. If true, this is an infinite REPL loop
		if (interactive) {
			while (running) {
				if (_showPromptText) {
					out.print(AyaPrefs.getPrompt());
				}
				
				String input;
				try {
					input = scanner.nextLine();
				} catch (NoSuchElementException e) {  // Ctrl+D
					running = false;
					continue;
				}

				if (input.equals("")) {
					continue;
				}
				
				if (_echo && interactive) {
					out.println(AyaPrefs.getPrompt() + input);
				}
				
				ExecutionRequest request = processInput(input);
	
				if (request != null) {	
					_aya.queueInput(request);
					try {
						while (_aya.hasPendingTasks()) {
							ExecutionResult result = _aya.waitForResponse();
							if (result.id() == request.id()) {
								printResult(_io(), result);
							}
						}
					} catch (InterruptedException e) {
						out.println("Aya interrupted");
						e.printStackTrace(err);
						running = false;
					}
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
	}
	
	public AyaThread getMainThread() {
		return _aya;
	}
	
	public static InteractiveAya createInteractiveSession(String[] args) {
		// Init the static data
		StaticData.getInstance().init();
		AyaPrefs.setArgs(args);
		
		AyaPrefs.initDefaultWorkingDir();
		if (args.length > 0) {
			// First arg is working directory
			AyaPrefs.setWorkingDir(args[0]);
		}
		
		ExecutionContext context = ExecutionContext.createRoot(StaticData.IO);
		
		// Init global vars
		context.getVars().initGlobals();
		
		AyaThread ayaThread = AyaThread.spawnRootThread(context);
		
		//Use default system io (interactive in the terminal)
		InteractiveAya iaya = new InteractiveAya(ayaThread);
		
		return iaya;
	}
	
	public static void main(String[] args) {
		InteractiveAya iaya = createInteractiveSession(args);

		// argument[0] is always the working directory, check for args 1+
		if (args.length > 1) {
			if (args[1].equals("-i")) {
				setInteractive(true);
			}
		}
		
		iaya.loop();
		System.exit(1);
	}
	
	public static void setInteractive(boolean b) {
		interactive = b;
	}
}
