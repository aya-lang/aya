package aya;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import aya.eval.ExecutionContext;
import aya.exceptions.parser.ParserException;
import aya.obj.Obj;
import aya.obj.block.StaticBlock;
import aya.parser.Parser;
import aya.parser.SourceString;
import aya.util.FileUtils;

public class InteractiveAya {
	
	private static boolean interactive = true;

	private boolean _echo = false;
	private boolean _showPromptText = true;
	private String _initcode = null;
	private static ConcurrentLinkedQueue<String> _scripts;
	
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
					ArrayList<Obj> data = res.getData();
					for (int i = 0; i < data.size(); i++) {
						_io().out().print(data.get(i));
						if (i < data.size() - 1) _io().out().print(' ');
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
		running = true;
		
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

		// Flush output
		while (_aya.hasUnfinishedTasks()) {
			try {
				printResult(_aya.waitForResponse());
			} catch (InterruptedException e) {
				out.println("Aya interrupted");
				e.printStackTrace(err);
				running = false;
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
					_aya.interrupt();
					running = false;
				}
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
					ExecutionResult result = _aya.waitForResponse();
					printResult(result);
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

		AyaThread ayaThread = AyaThread.spawnRootThread(context);
		
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
	
	public static void setInteractive(boolean b) {
		interactive = b;
	}
}
