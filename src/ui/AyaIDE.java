package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import aya.AyaPrefs;
import aya.AyaStdIO;
import aya.AyaThread;
import aya.ExecutionRequest;
import aya.InteractiveAya;
import aya.StaticData;
import aya.io.fs.FilesystemIO;
import aya.io.http.HTTPDownloader;
import aya.io.stdin.ScannerInputWrapper;
import aya.obj.block.StaticBlock;
import aya.parser.Parser;
import aya.parser.SourceString;
import aya.util.FileUtils;


@SuppressWarnings("serial")
public class AyaIDE extends JFrame
{
	protected static String HELP_KEY_BINDINGS = ""
			+ "Quick Search: ctrl-q\n"
			+ "Interpreter: ctrl-i\n"
			+ "Editor: ctrl-e\n"
			+ "Run Editor: ctrl-r\n";
	protected static String HELP_ABOUT = "Aya\n"
			+ "Version: " + StaticData.VERSION_NAME + "\n"
			+ "Source: github.com/nick-paul/aya-lang\n"
			+ "Wiki: github.com/nick-paul/aya-lang/wiki";

	private final static int RESCODE_OK  = 0;
	private final static int RESCODE_ERR = 1;


	private AyaThread _aya;

	//Layout
	private MyConsole _interpreter;
	private JMenu _menu;
	private JMenuBar _menuBar;

	private AyaIDE thiside;


	public AyaIDE(AyaThread ayaThread) {
		super("Aya");

		this.thiside = this;


		this._aya = ayaThread;
		this._interpreter = new MyConsole();
		this._menu = new JMenu();
		this._menuBar = new JMenuBar();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		//Keyboard Listener
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		  .addKeyEventDispatcher(new KeyEventDispatcher() {
			    public boolean dispatchKeyEvent(KeyEvent e) {
			    //System.out.println(e.getID() + " - " + e.getKeyCode());

		    	  //On key up: 402, On key down: 401
		    	  if(e.getID() == 401) {
		    		  switch(e.getKeyCode()) {
		    		  case KeyEvent.VK_ENTER:
		    			  if(!_interpreter.getInputLine().getText().equals("") && _interpreter.getInputLine().inFocus()) {
		    				 // _aya.println(AyaPrefs.getPrompt() + " " + _interpreter.getInputLine().getText());
						  }
		    			  break;
		    		  case KeyEvent.VK_I:
		    			  if(e.isControlDown()) {
		    				  _interpreter.getInputLine().grabFocus();
		    			  }
		    			  break;
		    		  case KeyEvent.VK_Q:
		    			  if(e.isControlDown()) {
		    				  if(QuickSearch.isFrameActive()) {
			    				  QuickSearch.updateHelpTextInFrame(StaticData.getInstance().getQuickSearchData());
		    					  QuickSearch.frameFocus();
		    				  } else {
		    					  QuickSearch.newQSFrame(StaticData.getInstance().getQuickSearchData());
		    				  }
		    			  }
		    			  break;
		    		  case KeyEvent.VK_E:
		    			  if(e.isControlDown()) {
		    				  if(EditorWindow.isFrameActive()) {
		    					  EditorWindow.frameFocus();
		    				  } else {
		    					  EditorWindow.newEditorFrame(thiside);
		    				  }
		    			  }
		    			  break;
		    		  case KeyEvent.VK_R:
		    			  if(e.isControlDown()) {
		    				  if (EditorWindow.activeEditor == null) {
		    						JOptionPane.showMessageDialog(_interpreter, "No editor window open", "ERROR", JOptionPane.ERROR_MESSAGE);
		    				  }
		    				  EditorWindow.activeEditor.run();
		    			  }
		    			  break;
		    		  }
		    	  }
		    	  return false;
			}
		});

		//Confirm Close
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				exit();
		    }
		});

		//Menu Bar
		_menuBar.setPreferredSize(new Dimension(100, 20));

		//File
		_menu = new JMenu("File");
		_menu.setMnemonic(KeyEvent.VK_A);
		_menu.getAccessibleContext().setAccessibleDescription("");
		//Load
		JMenuItem mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				String path = requestFilePathUI();
				if (path != null) {
					path = path.replace("\\", "\\\\");
					StaticBlock in_block = Parser.compileSafeOrNull(new SourceString("\"" + path + "\" :F", ""), StaticData.IO);
					_aya.queueInput(new ExecutionRequest(-1, in_block)); // TODO change request id

				}
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Load");
		_menu.add(mi);



		_menuBar.add(_menu);

		//Tools
		_menu = new JMenu("Tools");
		_menu.setMnemonic(KeyEvent.VK_A);
		_menu.getAccessibleContext().setAccessibleDescription("");
		//Insert Filename
		mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				insertFilenameAtCarat();
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Insert Filename..");
		_menu.add(mi);
		//Clear Console
		mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				_interpreter.clear();
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Clear Console");
		_menu.add(mi);
		mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				if(EditorWindow.isFrameActive()) {
					  EditorWindow.frameFocus();
				  } else {
					  EditorWindow.newEditorFrame(thiside);
				  }
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Open Editor   ctrl+E");
		_menu.add(mi);

		_menuBar.add(_menu);


		//Help
		//Quick Search
		_menu = new JMenu("Help");
		_menu.setMnemonic(KeyEvent.VK_A);
		_menu.getAccessibleContext().setAccessibleDescription("");
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				if(QuickSearch.isFrameActive()) {
					QuickSearch.frameFocus();
				} else {
					QuickSearch.newQSFrame(StaticData.getInstance().getQuickSearchData());
				}
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Quick Search");
		_menu.add(mi);

		//Key Bindings
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(_interpreter, HELP_KEY_BINDINGS);
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Key Bindings");
		_menu.add(mi);
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(_interpreter, HELP_ABOUT);
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("About");
		_menu.add(mi);
		_menuBar.add(_menu);

		JPanel all = new JPanel();
		all.setLayout(new BorderLayout());



		JPanel smallConsole = new JPanel();
		smallConsole.setLayout(new BorderLayout());
		smallConsole.add(_interpreter, BorderLayout.CENTER);
		all.add(smallConsole);

		add(all);

		setJMenuBar(_menuBar);
		pack();
		setVisible(true);


		_interpreter.getInputLine().grabFocus();
	}

	public static void exit() {
		if(EditorWindow.hasText()) {
			String ObjButtons[] = {"Yes","No"};
		    int PromptResult = JOptionPane.showOptionDialog(null,
		        "Editor still has code. Are you sure you want to exit?", "Editor Has Code",
		        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
		        ObjButtons,ObjButtons[1]);
		    if(PromptResult==0) {
		      System.exit(0);
		    }
		} else {
			System.exit(0);
		}
	}

	public void insertFilenameAtCarat() {
		File file = chooseFile();
		if(file != null) {
			String path = file.getPath();
			path = path.replace("\\", "\\\\");
            _interpreter.getInputLine().insertAtCaret("\"" + path + "\"");
		}
	}

	public String requestFilePathUI() {
		File file = chooseFile();
		if(file != null) {
			return file.getPath();
		}
		return null;
	}

	public static File chooseFile() {
		JFileChooser fc = new JFileChooser();

		//Set working directory
		File here = new File(".");
		fc.setCurrentDirectory(here);
		here.delete();

		int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        } else {
        	return null;
        }
	}

	public AyaThread getAya() {
		return this._aya;
	}

	public OutputStream getOutputStream() {
		return _interpreter.getOut();
	}

	public InputStream getInputStream() {
		return _interpreter.getIn();
	}
	
	private static class CLIOptions {
		// What to do after processing CLI args
		public static final int MODE_EXIT = 0; // Exit after processing CLI args
		public static final int MODE_REPL = 1; // Enter the command line REPL
		public static final int MODE_GUI = 2; // Open the GUI
		
		public static final int SPECIAL_MODE_NONE = 0; // Print help text and exit
		public static final int SPECIAL_MODE_PRINT_HELP = 1; // Print help text and exit
		public static final int SPECIAL_MODE_PRINT_VERSION = 2; // Print help text and exit


		
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
		// File to run
		public String fileToRun = null;
		// If not null, run this package
		public String packageToRun = null;
		
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
	}
	

	public static void main(String[] args) {
		// Set to System.in/out/err. If using the GUI, these will be changed to IDE GUI later
		StaticData.IO = new AyaStdIO(System.out, System.err, System.in, new ScannerInputWrapper(System.in));
		StaticData.HTTP_DOWNLOADER = new HTTPDownloader();
		StaticData.FILESYSTEM = new FilesystemIO();
		AyaPrefs.setArgs(args);
		
		// Something was piped in, add it to the input queue
		String pipedInput = null;
		try {
			if (System.in.available() > 0) {
		        pipedInput = new String(System.in.readAllBytes(), StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CLIOptions options = new CLIOptions();
		try {
			options = CLIOptions.parse(args, pipedInput);
		} catch (RuntimeException e) {
			StaticData.IO.out().print(e.getMessage());
			System.exit(RESCODE_ERR);
		}

		// Special cases
		
		if (options.specialMode == CLIOptions.SPECIAL_MODE_PRINT_HELP) {
			StaticData.IO.out().print(InteractiveAya.HELP_TEXT);
			System.exit(RESCODE_OK);
		} else if (options.specialMode == CLIOptions.SPECIAL_MODE_PRINT_VERSION) {
			StaticData.IO.out().print(StaticData.VERSION_NAME);
			System.exit(RESCODE_OK);						
		}
		
		InteractiveAya iaya = InteractiveAya.createInteractiveSession(options.workingDir);
		

		
		if (options.autoImportGolf) {
			iaya.compileAndQueueSystemInput("<system>", "require golf *");
		}
		
		if (options.expressionToRun != null) {
			iaya.compileAndQueueSystemInput("-e", options.expressionToRun);
		}
		
		if (options.packageToRun != null) {
			iaya.compileAndQueueSystemInput("-p", "import pkg");
			iaya.compileAndQueueSystemInput("-p", "\"\"\"" + options.packageToRun + "\"\"\" pkg.run");
		}
		
		if (pipedInput != null) {
			iaya.compileAndQueueSystemInput("<stdin>", pipedInput);
		}

		if (options.fileToRun != null) {
				File f = FileUtils.resolveFile(options.fileToRun);
				String pathString = f.getPath().toString().replace("\\", "\\\\");
				iaya.compileAndQueueSystemInput("<ayarc loader>", "\"\"\"" + pathString + "\"\"\" :F");
		}

		if (options.mode == CLIOptions.MODE_EXIT) {
			iaya.setInteractive(false); // Exit once complete
		} else if (options.mode == CLIOptions.MODE_REPL) {
			iaya.setInteractive(true);
		} else if (options.mode == CLIOptions.MODE_GUI) {
			iaya.setInteractive(true);
			
			//Load and initialize the ide
			AyaIDE ide = new AyaIDE(iaya.getMainThread());

			// Redirect IO to GUI
			StaticData.IO.setOut(ide.getOutputStream());
			StaticData.IO.setErr(ide.getOutputStream());
			StaticData.IO.setIn(ide.getInputStream(), new ScannerInputWrapper(ide.getInputStream()));

			// InteractiveAya Prefs
			iaya.setPromptText(false);
			iaya.setEcho(true);

			//Grab focus
			ide._interpreter.getInputLine().grabFocus();
		}


		//if (readstdin) InteractiveAya.setInteractive(false);
		

		int resultCode = iaya.loop();
		System.exit(resultCode);
	}
}


